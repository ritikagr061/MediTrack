package com.meditrack.patientservice.controller;

import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/hospitals/{hospitalId}/patients")
public class HospitalPatientController {
    private final PatientService patientService;

    public HospitalPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponseDTO>> getHospitalPatients(
            @PathVariable UUID hospitalId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(
                patientService.getPatients(search, hospitalId, isActive, page, size, sortBy, sortDirection)
        );
    }
}
