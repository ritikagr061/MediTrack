package com.meditrack.appointmentservice.controller;

import com.meditrack.appointmentservice.client.PatientServiceClient;
import com.meditrack.appointmentservice.dto.AppointmentCreateRequestDTO;
import com.meditrack.appointmentservice.dto.AppointmentResponseDTO;
import com.meditrack.appointmentservice.dto.AvailableSlotResponseDTO;
import com.meditrack.appointmentservice.dto.DoctorScheduleCreateRequestDTO;
import com.meditrack.appointmentservice.dto.DoctorScheduleResponseDTO;
import com.meditrack.appointmentservice.dto.DoctorTimeOffCreateRequestDTO;
import com.meditrack.appointmentservice.model.AppointmentStatus;
import com.meditrack.appointmentservice.security.AuthContext;
import com.meditrack.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AuthContext authContext;

    public AppointmentController(AppointmentService appointmentService, AuthContext authContext) {
        this.appointmentService = appointmentService;
        this.authContext = authContext;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION','PATIENT')")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        if (request.getBookedByUserId() == null && authContext.userId() != null) {
            request.setBookedByUserId(new UUID(0L, authContext.userId()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION','PATIENT')")
    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> getAppointments(
            @RequestParam UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appointmentService.getAppointments(
                authContext.scopedHospitalId(hospitalId), patientId, doctorId, status, page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION','PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id, authContext.hospitalId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION','PATIENT')")
    @GetMapping("/doctors")
    public ResponseEntity<PatientServiceClient.PageResponse<PatientServiceClient.PatientServiceDoctor>> searchDoctors(
            @RequestParam UUID hospitalId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appointmentService.searchDoctors(authContext.scopedHospitalId(hospitalId), search, specialty, page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION','PATIENT')")
    @GetMapping("/availability")
    public ResponseEntity<List<AvailableSlotResponseDTO>> getAvailableSlots(
            @RequestParam UUID hospitalId,
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(authContext.scopedHospitalId(hospitalId), doctorId, date));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR')")
    @PostMapping("/doctor-schedules")
    public ResponseEntity<DoctorScheduleResponseDTO> createDoctorSchedule(
            @Valid @RequestBody DoctorScheduleCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createDoctorSchedule(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION')")
    @GetMapping("/doctor-schedules")
    public ResponseEntity<List<DoctorScheduleResponseDTO>> getDoctorSchedules(
            @RequestParam UUID hospitalId,
            @RequestParam UUID doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorSchedules(authContext.scopedHospitalId(hospitalId), doctorId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR')")
    @PostMapping("/doctor-time-off")
    public ResponseEntity<Void> createDoctorTimeOff(@Valid @RequestBody DoctorTimeOffCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        appointmentService.createDoctorTimeOff(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
