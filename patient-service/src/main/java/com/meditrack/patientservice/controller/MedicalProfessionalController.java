package com.meditrack.patientservice.controller;

import com.meditrack.patientservice.dto.MedicalProfessionalCreateRequestDTO;
import com.meditrack.patientservice.dto.MedicalProfessionalResponseDTO;
import com.meditrack.patientservice.model.ProfessionalRoleType;
import com.meditrack.patientservice.service.MedicalProfessionalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/medical-professionals")
public class MedicalProfessionalController {
    private final MedicalProfessionalService medicalProfessionalService;

    public MedicalProfessionalController(MedicalProfessionalService medicalProfessionalService) {
        this.medicalProfessionalService = medicalProfessionalService;
    }

    @GetMapping
    public ResponseEntity<Page<MedicalProfessionalResponseDTO>> getMedicalProfessionals(
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) ProfessionalRoleType roleType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(medicalProfessionalService.getMedicalProfessionals(
                hospitalId, roleType, isActive, specialty, search, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalProfessionalResponseDTO> getMedicalProfessional(@PathVariable UUID id) {
        return ResponseEntity.ok(medicalProfessionalService.getMedicalProfessional(id));
    }

    @GetMapping("/{id}/hospitals/{hospitalId}")
    public ResponseEntity<MedicalProfessionalResponseDTO> getMedicalProfessionalForHospital(
            @PathVariable UUID id,
            @PathVariable UUID hospitalId) {
        return ResponseEntity.ok(medicalProfessionalService.getMedicalProfessionalForHospital(hospitalId, id));
    }

    @PostMapping
    public ResponseEntity<MedicalProfessionalResponseDTO> createMedicalProfessional(
            @Valid @RequestBody MedicalProfessionalCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalProfessionalService.createMedicalProfessional(request));
    }
}
