package com.meditrack.patientservice.service;

import com.meditrack.patientservice.dto.EncounterCreateRequestDTO;
import com.meditrack.patientservice.dto.EncounterResponseDTO;
import com.meditrack.patientservice.dto.EncounterStatusUpdateRequestDTO;
import com.meditrack.patientservice.dto.EncounterUpdateRequestDTO;
import com.meditrack.patientservice.exception.EncounterAlreadyExistsException;
import com.meditrack.patientservice.exception.EncounterNotFoundException;
import com.meditrack.patientservice.exception.InvalidEncounterException;
import com.meditrack.patientservice.exception.MedicalProfessionalNotFoundException;
import com.meditrack.patientservice.exception.PatientNotFoundException;
import com.meditrack.patientservice.kafka.KafkaProducer;
import com.meditrack.patientservice.kafka.NotificationEvent;
import com.meditrack.patientservice.model.Encounter;
import com.meditrack.patientservice.model.EncounterStatus;
import com.meditrack.patientservice.model.EncounterType;
import com.meditrack.patientservice.model.MedicalProfessional;
import com.meditrack.patientservice.model.Patient;
import com.meditrack.patientservice.model.ProfessionalRoleType;
import com.meditrack.patientservice.repository.EncounterRepository;
import com.meditrack.patientservice.repository.MedicalProfessionalRepository;
import com.meditrack.patientservice.repository.PatientRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class EncounterService {
    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final MedicalProfessionalRepository medicalProfessionalRepository;
    private final KafkaProducer kafkaProducer;

    public EncounterService(EncounterRepository encounterRepository,
                            PatientRepository patientRepository,
                            MedicalProfessionalRepository medicalProfessionalRepository,
                            KafkaProducer kafkaProducer) {
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.medicalProfessionalRepository = medicalProfessionalRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    @CacheEvict(value = "patient-service:patient-summaries", key = "#request.patientId")
    public EncounterResponseDTO createEncounter(EncounterCreateRequestDTO request) {
        Patient patient = validatePatient(request.getHospitalId(), request.getPatientId());
        MedicalProfessional attendingDoctor = validateDoctor(request.getHospitalId(), request.getAttendingDoctorId(), "Attending doctor");
        validateDoctor(request.getHospitalId(), request.getCreatedByDoctorId(), "Creating doctor");

        if (request.getAppointmentId() != null && encounterRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new EncounterAlreadyExistsException(
                    "Encounter already exists for appointment " + request.getAppointmentId());
        }

        Encounter encounter = new Encounter();
        encounter.setHospitalId(request.getHospitalId());
        encounter.setPatientId(request.getPatientId());
        encounter.setAppointmentId(request.getAppointmentId());
        encounter.setAttendingDoctorId(request.getAttendingDoctorId());
        encounter.setCreatedByDoctorId(request.getCreatedByDoctorId());
        encounter.setEncounterType(request.getEncounterType());
        encounter.setStatus(EncounterStatus.IN_PROGRESS);
        encounter.setChiefComplaint(request.getChiefComplaint());
        encounter.setReasonText(request.getReasonText());
        encounter.setLocationType(request.getLocationType());
        encounter.setLocationText(request.getLocationText());
        encounter.setStartedAt(request.getStartedAt() == null
                ? OffsetDateTime.now(ZoneOffset.UTC)
                : request.getStartedAt());

        Encounter saved = encounterRepository.save(encounter);
        publishEncounterNotification("ENCOUNTER_CREATED", "Encounter started",
                "Your encounter with " + attendingDoctor.getName() + " has started for "
                        + saved.getChiefComplaint() + ".", saved, patient);
        return toDTO(saved);
    }

    public Page<EncounterResponseDTO> getEncounters(UUID hospitalId, UUID patientId, UUID appointmentId,
                                                    UUID attendingDoctorId, EncounterType encounterType,
                                                    EncounterStatus status, OffsetDateTime startedFrom,
                                                    OffsetDateTime startedTo, int page, int size) {
        return encounterRepository.findAllByFilters(
                        hospitalId,
                        patientId,
                        appointmentId,
                        attendingDoctorId,
                        encounterType,
                        status,
                        startedFrom,
                        startedTo,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"))
                )
                .map(this::toDTO);
    }

    @Cacheable(value = "patient-service:encounters", key = "#id")
    public EncounterResponseDTO getEncounter(UUID id) {
        return toDTO(findEncounterOrThrow(id));
    }

    public EncounterResponseDTO getEncounter(UUID id, UUID hospitalId) {
        return toDTO(findEncounterOrThrow(id, hospitalId));
    }

    @Transactional
    @CacheEvict(value = "patient-service:encounters", key = "#id")
    public EncounterResponseDTO updateEncounter(UUID id, EncounterUpdateRequestDTO request) {
        Encounter encounter = findEncounterOrThrow(id);
        if (encounter.getStatus() == EncounterStatus.FINISHED || encounter.getStatus() == EncounterStatus.CANCELLED) {
            throw new InvalidEncounterException("Finished or cancelled encounters cannot be edited");
        }

        if (request.getChiefComplaint() != null) {
            encounter.setChiefComplaint(request.getChiefComplaint());
        }
        if (request.getReasonText() != null) {
            encounter.setReasonText(request.getReasonText());
        }
        if (request.getLocationType() != null) {
            encounter.setLocationType(request.getLocationType());
        }
        if (request.getLocationText() != null) {
            encounter.setLocationText(request.getLocationText());
        }

        return toDTO(encounterRepository.save(encounter));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "patient-service:encounters", key = "#id"),
            @CacheEvict(value = "patient-service:patient-summaries", allEntries = true)
    })
    public EncounterResponseDTO updateEncounterStatus(UUID id, EncounterStatusUpdateRequestDTO request) {
        Encounter encounter = findEncounterOrThrow(id);
        validateStatusTransition(encounter.getStatus(), request.getStatus());
        encounter.setStatus(request.getStatus());
        if (request.getStatus() == EncounterStatus.FINISHED || request.getStatus() == EncounterStatus.CANCELLED) {
            encounter.setEndedAt(OffsetDateTime.now(ZoneOffset.UTC));
        }
        Encounter saved = encounterRepository.save(encounter);
        if (request.getStatus() == EncounterStatus.FINISHED) {
            Patient patient = patientRepository.findById(saved.getPatientId()).orElse(null);
            if (patient != null) {
                publishEncounterNotification("ENCOUNTER_FINISHED", "Visit summary ready",
                        "Your encounter from " + saved.getStartedAt()
                                + " has been completed. Please check your records.", saved, patient);
            }
        }
        return toDTO(saved);
    }

    private void validateStatusTransition(EncounterStatus currentStatus, EncounterStatus nextStatus) {
        if (currentStatus == nextStatus) {
            return;
        }
        if (currentStatus == EncounterStatus.FINISHED || currentStatus == EncounterStatus.CANCELLED) {
            throw new InvalidEncounterException("Cannot move encounter from terminal status " + currentStatus);
        }
        if (nextStatus == EncounterStatus.PLANNED) {
            throw new InvalidEncounterException("Cannot move an active encounter back to PLANNED");
        }
    }

    private Patient validatePatient(UUID hospitalId, UUID patientId) {
        Patient patient = patientRepository.findByIdAndHospitalId(patientId, hospitalId)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient with id " + patientId + " is not found for hospital " + hospitalId));
        if (!patient.isActive()) {
            throw new InvalidEncounterException("Patient is not active");
        }
        return patient;
    }

    private MedicalProfessional validateDoctor(UUID hospitalId, UUID doctorId, String label) {
        MedicalProfessional doctor = medicalProfessionalRepository.findByIdAndHospitalId(doctorId, hospitalId)
                .orElseThrow(() -> new MedicalProfessionalNotFoundException(
                        label + " with id " + doctorId + " is not found for hospital " + hospitalId));
        if (!doctor.isActive() || doctor.getRoleType() != ProfessionalRoleType.DOCTOR) {
            throw new InvalidEncounterException(label + " is not an active doctor");
        }
        return doctor;
    }

    private void publishEncounterNotification(String eventType, String subject, String body,
                                              Encounter encounter, Patient patient) {
        NotificationEvent event = new NotificationEvent();
        event.setEventType(eventType);
        event.setHospitalId(encounter.getHospitalId());
        event.setPatientId(encounter.getPatientId());
        event.setRecipientUserId(patient.getUserId());
        event.setRecipientName(patient.getName());
        event.setRecipientEmail(patient.getEmail());
        event.setRecipientPhone(patient.getPhone());
        event.setChannel("EMAIL");
        event.setTemplateCode(eventType);
        event.setSubject(subject);
        event.setBody(body);
        event.setSourceService("PATIENT_SERVICE");
        event.setSourceEntityId(encounter.getId());
        kafkaProducer.publishNotificationEvent(event);
    }

    private Encounter findEncounterOrThrow(UUID id) {
        return encounterRepository.findById(id)
                .orElseThrow(() -> new EncounterNotFoundException("Encounter with id " + id + " is not found"));
    }

    private Encounter findEncounterOrThrow(UUID id, UUID hospitalId) {
        return encounterRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> new EncounterNotFoundException(
                        "Encounter with id " + id + " is not found for hospital " + hospitalId));
    }

    private EncounterResponseDTO toDTO(Encounter encounter) {
        return new EncounterResponseDTO(
                encounter.getId(),
                encounter.getHospitalId(),
                encounter.getPatientId(),
                encounter.getAppointmentId(),
                encounter.getAttendingDoctorId(),
                encounter.getCreatedByDoctorId(),
                encounter.getEncounterType(),
                encounter.getStatus(),
                encounter.getChiefComplaint(),
                encounter.getReasonText(),
                encounter.getLocationType(),
                encounter.getLocationText(),
                encounter.getStartedAt(),
                encounter.getEndedAt(),
                encounter.getCreatedAt(),
                encounter.getUpdatedAt(),
                encounter.getVersion()
        );
    }
}
