package com.meditrack.patientservice.controller;

import com.meditrack.patientservice.dto.HospitalCreateRequestDTO;
import com.meditrack.patientservice.dto.HospitalLoginConfigResponseDTO;
import com.meditrack.patientservice.dto.HospitalResponseDTO;
import com.meditrack.patientservice.dto.HospitalUpdateRequestDTO;
import com.meditrack.patientservice.security.AuthContext;
import com.meditrack.patientservice.service.HospitalService;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {
    private final HospitalService hospitalService;
    private final AuthContext authContext;

    public HospitalController(HospitalService hospitalService, AuthContext authContext) {
        this.hospitalService = hospitalService;
        this.authContext = authContext;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public ResponseEntity<List<HospitalResponseDTO>> getHospitals(@RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(List.of(hospitalService.getHospitalById(authContext.hospitalId())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponseDTO> getHospitalById(@PathVariable UUID id) {
        return ResponseEntity.ok(hospitalService.getHospitalById(authContext.scopedHospitalId(id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/code/{code}")
    public ResponseEntity<HospitalResponseDTO> getHospitalByCode(@PathVariable String code) {
        HospitalResponseDTO hospital = hospitalService.getHospitalByCode(code);
        authContext.scopedHospitalId(hospital.getId());
        return ResponseEntity.ok(hospital);
    }

    @GetMapping("/code/{code}/login-config")
    public ResponseEntity<HospitalLoginConfigResponseDTO> getHospitalLoginConfig(@PathVariable String code) {
        return ResponseEntity.ok(hospitalService.getHospitalLoginConfigByCode(code));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HospitalResponseDTO> createHospital(@Valid @RequestBody HospitalCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalService.createHospital(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<HospitalResponseDTO> updateHospital(@PathVariable UUID id,
                                                              @Valid @RequestBody HospitalUpdateRequestDTO request) {
        return ResponseEntity.ok(hospitalService.updateHospital(authContext.scopedHospitalId(id), request));
    }
}
