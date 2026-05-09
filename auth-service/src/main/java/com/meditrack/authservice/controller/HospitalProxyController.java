package com.meditrack.authservice.controller;

import com.meditrack.authservice.dto.HospitalLoginProfileResponse;
import com.meditrack.authservice.service.HospitalProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth/hospitals")
public class HospitalProxyController {
    private final HospitalProxyService hospitalProxyService;

    public HospitalProxyController(HospitalProxyService hospitalProxyService) {
        this.hospitalProxyService = hospitalProxyService;
    }

    @GetMapping("/id/{hospitalId}/login-profile")
    public ResponseEntity<HospitalLoginProfileResponse> getByHospitalId(@PathVariable UUID hospitalId) {
        return ResponseEntity.ok(hospitalProxyService.getByHospitalId(hospitalId));
    }

    @GetMapping("/code/{hospitalCode}/login-profile")
    public ResponseEntity<HospitalLoginProfileResponse> getByHospitalCode(@PathVariable String hospitalCode) {
        return ResponseEntity.ok(hospitalProxyService.getByHospitalCode(hospitalCode));
    }
}
