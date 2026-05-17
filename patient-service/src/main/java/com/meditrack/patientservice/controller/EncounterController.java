package com.meditrack.patientservice.controller;

import com.meditrack.patientservice.dto.EncounterCreateRequestDTO;
import com.meditrack.patientservice.dto.EncounterResponseDTO;
import com.meditrack.patientservice.dto.EncounterStatusUpdateRequestDTO;
import com.meditrack.patientservice.dto.EncounterUpdateRequestDTO;
import com.meditrack.patientservice.model.EncounterStatus;
import com.meditrack.patientservice.model.EncounterType;
import com.meditrack.patientservice.security.AuthContext;
import com.meditrack.patientservice.service.EncounterService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/encounters")
public class EncounterController {
    private final EncounterService encounterService;
    private final AuthContext authContext;

    public EncounterController(EncounterService encounterService, AuthContext authContext) {
        this.encounterService = encounterService;
        this.authContext = authContext;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE')")
    @PostMapping
    public ResponseEntity<EncounterResponseDTO> createEncounter(
            @Valid @RequestBody EncounterCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(encounterService.createEncounter(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION')")
    @GetMapping
    public ResponseEntity<Page<EncounterResponseDTO>> getEncounters(
            @RequestParam UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID appointmentId,
            @RequestParam(required = false) UUID attendingDoctorId,
            @RequestParam(required = false) EncounterType encounterType,
            @RequestParam(required = false) EncounterStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startedTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID scopedHospitalId = authContext.scopedHospitalId(hospitalId);
        return ResponseEntity.ok(encounterService.getEncounters(
                scopedHospitalId,
                patientId,
                appointmentId,
                attendingDoctorId,
                encounterType,
                status,
                startedFrom,
                startedTo,
                page,
                size
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE','STAFF','RECEPTION')")
    @GetMapping("/{id}")
    public ResponseEntity<EncounterResponseDTO> getEncounter(@PathVariable UUID id) {
        return ResponseEntity.ok(encounterService.getEncounter(id, authContext.hospitalId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE')")
    @PatchMapping("/{id}")
    public ResponseEntity<EncounterResponseDTO> updateEncounter(
            @PathVariable UUID id,
            @Valid @RequestBody EncounterUpdateRequestDTO request) {
        encounterService.getEncounter(id, authContext.hospitalId());
        return ResponseEntity.ok(encounterService.updateEncounter(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','DOCTOR','NURSE')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<EncounterResponseDTO> updateEncounterStatus(
            @PathVariable UUID id,
            @Valid @RequestBody EncounterStatusUpdateRequestDTO request) {
        encounterService.getEncounter(id, authContext.hospitalId());
        return ResponseEntity.ok(encounterService.updateEncounterStatus(id, request));
    }
}
