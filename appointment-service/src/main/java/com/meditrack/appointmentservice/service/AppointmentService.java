package com.meditrack.appointmentservice.service;

import com.meditrack.appointmentservice.client.PatientServiceClient;
import com.meditrack.appointmentservice.dto.AppointmentCreateRequestDTO;
import com.meditrack.appointmentservice.dto.AppointmentResponseDTO;
import com.meditrack.appointmentservice.dto.AvailableSlotResponseDTO;
import com.meditrack.appointmentservice.dto.DoctorScheduleCreateRequestDTO;
import com.meditrack.appointmentservice.dto.DoctorScheduleResponseDTO;
import com.meditrack.appointmentservice.dto.DoctorTimeOffCreateRequestDTO;
import com.meditrack.appointmentservice.exception.AppointmentNotFoundException;
import com.meditrack.appointmentservice.exception.BookingConflictException;
import com.meditrack.appointmentservice.exception.InvalidAppointmentRequestException;
import com.meditrack.appointmentservice.kafka.NotificationEvent;
import com.meditrack.appointmentservice.kafka.NotificationEventProducer;
import com.meditrack.appointmentservice.model.Appointment;
import com.meditrack.appointmentservice.model.AppointmentStatus;
import com.meditrack.appointmentservice.model.AppointmentStatusHistory;
import com.meditrack.appointmentservice.model.DoctorSchedule;
import com.meditrack.appointmentservice.model.DoctorTimeOff;
import com.meditrack.appointmentservice.model.PaymentStatus;
import com.meditrack.appointmentservice.repository.AppointmentRepository;
import com.meditrack.appointmentservice.repository.AppointmentStatusHistoryRepository;
import com.meditrack.appointmentservice.repository.DoctorScheduleRepository;
import com.meditrack.appointmentservice.repository.DoctorTimeOffRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private static final EnumSet<AppointmentStatus> BLOCKING_STATUSES = EnumSet.of(
            AppointmentStatus.REQUESTED,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN
    );

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorTimeOffRepository doctorTimeOffRepository;
    private final AppointmentStatusHistoryRepository statusHistoryRepository;
    private final PatientServiceClient patientServiceClient;
    private final NotificationEventProducer notificationEventProducer;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorScheduleRepository doctorScheduleRepository,
                              DoctorTimeOffRepository doctorTimeOffRepository,
                              AppointmentStatusHistoryRepository statusHistoryRepository,
                              PatientServiceClient patientServiceClient,
                              NotificationEventProducer notificationEventProducer) {
        this.appointmentRepository = appointmentRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorTimeOffRepository = doctorTimeOffRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.patientServiceClient = patientServiceClient;
        this.notificationEventProducer = notificationEventProducer;
    }

    @Transactional
    @CacheEvict(value = "appointment-service:available-slots", allEntries = true)
    public AppointmentResponseDTO createAppointment(AppointmentCreateRequestDTO request) {
        try {
            return createAppointmentWithOptimisticSlotLock(request);
        } catch (OptimisticLockingFailureException ex) {
            throw new BookingConflictException("Requested appointment slot was booked by another request. Please select another slot.");
        }
    }

    private AppointmentResponseDTO createAppointmentWithOptimisticSlotLock(AppointmentCreateRequestDTO request) {
        if (request.getDurationMinutes() == null || request.getDurationMinutes() <= 0) {
            throw new InvalidAppointmentRequestException("Appointment duration must be greater than zero");
        }

        OffsetDateTime endsAt = request.getStartsAt().plusMinutes(request.getDurationMinutes());
        PatientServiceClient.PatientServicePatient patient = validatePatient(request.getHospitalId(), request.getPatientId());
        PatientServiceClient.PatientServiceDoctor doctor = validateDoctor(request.getHospitalId(), request.getDoctorId());
        lockDoctorScheduleForBooking(request.getHospitalId(), request.getDoctorId(), request.getStartsAt(), endsAt);
        validateDoctorTimeOff(request.getHospitalId(), request.getDoctorId(), request.getStartsAt(), endsAt);
        validateNoAppointmentClash(request.getHospitalId(), request.getDoctorId(), request.getPatientId(),
                request.getStartsAt(), endsAt);

        Appointment appointment = new Appointment();
        appointment.setHospitalId(request.getHospitalId());
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setReasonText(request.getReasonText());
        appointment.setNotes(request.getNotes());
        appointment.setStartsAt(request.getStartsAt());
        appointment.setEndsAt(endsAt);
        appointment.setDurationMinutes(request.getDurationMinutes());
        appointment.setPaymentStatus(PaymentStatus.NONE);
        appointment.setBookedByUserId(request.getBookedByUserId());
        appointment.setBookedByRole(request.getBookedByRole());

        Appointment saved = appointmentRepository.save(appointment);
        saveStatusHistory(saved.getId(), null, saved.getStatus(), request.getBookedByUserId(), "Appointment booked");
        appointmentRepository.flush();
        publishAppointmentBookedNotification(saved, patient, doctor);
        return toDTO(saved);
    }

    public Page<AppointmentResponseDTO> getAppointments(UUID hospitalId, UUID patientId, UUID doctorId,
                                                        AppointmentStatus status, int page, int size) {
        return appointmentRepository.findAllByFilters(hospitalId, patientId, doctorId, status, PageRequest.of(page, size))
                .map(this::toDTO);
    }

    @Cacheable(value = "appointment-service:appointments", key = "#id")
    public AppointmentResponseDTO getAppointment(UUID id) {
        return toDTO(findAppointmentOrThrow(id));
    }

    public AppointmentResponseDTO getAppointment(UUID id, UUID hospitalId) {
        return toDTO(findAppointmentOrThrow(id, hospitalId));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "appointment-service:doctor-schedules", allEntries = true),
            @CacheEvict(value = "appointment-service:available-slots", allEntries = true)
    })
    public DoctorScheduleResponseDTO createDoctorSchedule(DoctorScheduleCreateRequestDTO request) {
        validateDoctor(request.getHospitalId(), request.getDoctorId());
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidAppointmentRequestException("Schedule endTime must be after startTime");
        }

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setHospitalId(request.getHospitalId());
        schedule.setDoctorId(request.getDoctorId());
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setSlotDurationMinutes(request.getSlotDurationMinutes());
        schedule.setBufferMinutes(request.getBufferMinutes());
        schedule.setConsultationFee(request.getConsultationFee());
        schedule.setActive(true);
        return toDTO(doctorScheduleRepository.save(schedule));
    }

    @Cacheable(value = "appointment-service:doctor-schedules", key = "#hospitalId + ':' + #doctorId")
    public List<DoctorScheduleResponseDTO> getDoctorSchedules(UUID hospitalId, UUID doctorId) {
        validateDoctor(hospitalId, doctorId);
        return doctorScheduleRepository.findActiveSchedules(hospitalId, doctorId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "appointment-service:available-slots", allEntries = true)
    public void createDoctorTimeOff(DoctorTimeOffCreateRequestDTO request) {
        validateDoctor(request.getHospitalId(), request.getDoctorId());
        if (!request.getEndsAt().isAfter(request.getStartsAt())) {
            throw new InvalidAppointmentRequestException("Time off endsAt must be after startsAt");
        }

        DoctorTimeOff timeOff = new DoctorTimeOff();
        timeOff.setHospitalId(request.getHospitalId());
        timeOff.setDoctorId(request.getDoctorId());
        timeOff.setStartsAt(request.getStartsAt());
        timeOff.setEndsAt(request.getEndsAt());
        timeOff.setReason(request.getReason());
        timeOff.setCreatedByUserId(request.getCreatedByUserId());
        doctorTimeOffRepository.save(timeOff);
    }

    @Cacheable(value = "appointment-service:available-slots", key = "#hospitalId + ':' + #doctorId + ':' + #date")
    public List<AvailableSlotResponseDTO> getAvailableSlots(UUID hospitalId, UUID doctorId, LocalDate date) {
        validateDoctor(hospitalId, doctorId);
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findActiveSchedulesForDay(hospitalId, doctorId, date.getDayOfWeek());

        return schedules.stream()
                .flatMap(schedule -> buildSlotsForSchedule(hospitalId, doctorId, date, schedule).stream())
                .toList();
    }

    public PatientServiceClient.PageResponse<PatientServiceClient.PatientServiceDoctor> searchDoctors(
            UUID hospitalId, String search, String specialty, int page, int size) {
        return patientServiceClient.searchDoctors(hospitalId, search, specialty, page, size);
    }

    private List<AvailableSlotResponseDTO> buildSlotsForSchedule(UUID hospitalId, UUID doctorId, LocalDate date,
                                                                 DoctorSchedule schedule) {
        java.time.ZoneOffset offset = java.time.ZoneOffset.systemDefault().getRules()
                .getOffset(date.atStartOfDay());
        OffsetDateTime cursor = OffsetDateTime.of(date, schedule.getStartTime(), offset);
        OffsetDateTime scheduleEnd = OffsetDateTime.of(date, schedule.getEndTime(), offset);
        java.util.ArrayList<AvailableSlotResponseDTO> slots = new java.util.ArrayList<>();

        while (cursor.plusMinutes(schedule.getSlotDurationMinutes()).compareTo(scheduleEnd) <= 0) {
            OffsetDateTime slotEnd = cursor.plusMinutes(schedule.getSlotDurationMinutes());
            if (isSlotFree(hospitalId, doctorId, cursor, slotEnd)) {
                slots.add(new AvailableSlotResponseDTO(cursor, slotEnd));
            }
            cursor = slotEnd.plusMinutes(schedule.getBufferMinutes());
        }

        return slots;
    }

    private PatientServiceClient.PatientServicePatient validatePatient(UUID hospitalId, UUID patientId) {
        PatientServiceClient.PatientServicePatient patient = patientServiceClient.getPatient(patientId);
        if (patient == null || !hospitalId.equals(patient.getHospitalId())) {
            throw new InvalidAppointmentRequestException("Patient does not belong to hospital " + hospitalId);
        }
        if (!patient.isActive()) {
            throw new InvalidAppointmentRequestException("Patient is not active");
        }
        return patient;
    }

    private PatientServiceClient.PatientServiceDoctor validateDoctor(UUID hospitalId, UUID doctorId) {
        PatientServiceClient.PatientServiceDoctor doctor = patientServiceClient.getDoctorForHospital(hospitalId, doctorId);
        if (doctor == null || !hospitalId.equals(doctor.getHospitalId())) {
            throw new InvalidAppointmentRequestException("Doctor does not belong to hospital " + hospitalId);
        }
        if (!doctor.isActive() || !"DOCTOR".equals(doctor.getRoleType())) {
            throw new InvalidAppointmentRequestException("Medical professional is not an active doctor");
        }
        return doctor;
    }

    private void publishAppointmentBookedNotification(Appointment appointment,
                                                      PatientServiceClient.PatientServicePatient patient,
                                                      PatientServiceClient.PatientServiceDoctor doctor) {
        NotificationEvent event = new NotificationEvent();
        event.setEventType("APPOINTMENT_BOOKED");
        event.setHospitalId(appointment.getHospitalId());
        event.setPatientId(appointment.getPatientId());
        event.setRecipientUserId(null);
        event.setRecipientName(patient.getName());
        event.setRecipientEmail(patient.getEmail());
        event.setRecipientPhone(patient.getPhone());
        event.setChannel("EMAIL");
        event.setTemplateCode("APPOINTMENT_BOOKED");
        event.setSubject("Appointment confirmed");
        event.setBody("Your appointment with " + doctor.getName()
                + " is confirmed for " + appointment.getStartsAt()
                + ". Reason: " + appointment.getReasonText());
        event.setSourceService("APPOINTMENT_SERVICE");
        event.setSourceEntityId(appointment.getId());
        notificationEventProducer.publish(event);
    }

    private void lockDoctorScheduleForBooking(UUID hospitalId, UUID doctorId, OffsetDateTime startsAt,
                                              OffsetDateTime endsAt) {
        LocalTime startTime = startsAt.toLocalTime();
        LocalTime endTime = endsAt.toLocalTime();
        boolean insideSchedule = doctorScheduleRepository
                .findActiveSchedulesContainingSlotForBooking(hospitalId, doctorId, startsAt.getDayOfWeek(), startTime, endTime)
                .stream()
                .findFirst()
                .isPresent();

        if (!insideSchedule) {
            throw new BookingConflictException("Doctor is not scheduled for the requested time");
        }
    }

    private void validateDoctorTimeOff(UUID hospitalId, UUID doctorId, OffsetDateTime startsAt,
                                       OffsetDateTime endsAt) {
        if (!doctorTimeOffRepository.findOverlaps(hospitalId, doctorId, startsAt, endsAt).isEmpty()) {
            throw new BookingConflictException("Doctor has time off during the requested slot");
        }
    }

    private void validateNoAppointmentClash(UUID hospitalId, UUID doctorId, UUID patientId,
                                            OffsetDateTime startsAt, OffsetDateTime endsAt) {
        if (appointmentRepository.existsDoctorOverlap(hospitalId, doctorId, startsAt, endsAt, BLOCKING_STATUSES)) {
            throw new BookingConflictException("Doctor already has an appointment during the requested slot");
        }
        if (appointmentRepository.existsPatientOverlap(hospitalId, patientId, startsAt, endsAt, BLOCKING_STATUSES)) {
            throw new BookingConflictException("Patient already has an appointment during the requested slot");
        }
    }

    private boolean isSlotFree(UUID hospitalId, UUID doctorId, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        return appointmentRepository.findDoctorOverlaps(hospitalId, doctorId, startsAt, endsAt, BLOCKING_STATUSES).isEmpty()
                && doctorTimeOffRepository.findOverlaps(hospitalId, doctorId, startsAt, endsAt).isEmpty();
    }

    private Appointment findAppointmentOrThrow(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment with id " + id + " is not found"));
    }

    private Appointment findAppointmentOrThrow(UUID id, UUID hospitalId) {
        return appointmentRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment with id " + id + " is not found for hospital " + hospitalId));
    }

    private void saveStatusHistory(UUID appointmentId, AppointmentStatus fromStatus, AppointmentStatus toStatus,
                                   UUID changedByUserId, String reason) {
        AppointmentStatusHistory history = new AppointmentStatusHistory();
        history.setAppointmentId(appointmentId);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setChangedByUserId(changedByUserId);
        history.setReason(reason);
        statusHistoryRepository.save(history);
    }

    private AppointmentResponseDTO toDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getHospitalId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getAppointmentCode(),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReasonText(),
                appointment.getNotes(),
                appointment.getStartsAt(),
                appointment.getEndsAt(),
                appointment.getDurationMinutes(),
                appointment.getPaymentStatus(),
                appointment.getPaymentId(),
                appointment.getEncounterId(),
                appointment.getBookedByUserId(),
                appointment.getBookedByRole(),
                appointment.getCheckedInAt(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getVersion()
        );
    }

    private DoctorScheduleResponseDTO toDTO(DoctorSchedule schedule) {
        return new DoctorScheduleResponseDTO(
                schedule.getId(),
                schedule.getHospitalId(),
                schedule.getDoctorId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSlotDurationMinutes(),
                schedule.getBufferMinutes(),
                schedule.getConsultationFee(),
                schedule.isActive()
        );
    }
}
