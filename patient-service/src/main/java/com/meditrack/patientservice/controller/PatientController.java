package com.meditrack.patientservice.controller;

import com.meditrack.patientservice.dto.PatientCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientDiseaseCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientDiseaseResponseDTO;
import com.meditrack.patientservice.dto.PatientDiseaseUpdateRequestDTO;
import com.meditrack.patientservice.dto.PatientDuplicateCheckResponseDTO;
import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.dto.PatientStatusUpdateRequestDTO;
import com.meditrack.patientservice.dto.PatientSummaryResponseDTO;
import com.meditrack.patientservice.dto.PatientUpdateRequestDTO;
import com.meditrack.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponseDTO>> getPatients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        Page<PatientResponseDTO> response = patientService.getPatients(search, hospitalId, isActive, page, size, sortBy,
                sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PatientResponseDTO>> searchPatients(
            @RequestParam String query,
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(
                patientService.getPatients(query, hospitalId, isActive, page, size, sortBy, sortDirection)
        );
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<PatientDuplicateCheckResponseDTO> checkDuplicate(
            @RequestParam UUID hospitalId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String aadhar,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false) UUID excludePatientId) {
        return ResponseEntity.ok(
                patientService.checkDuplicate(hospitalId, email, phone, aadhar, pan, excludePatientId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<PatientSummaryResponseDTO> getPatientSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientSummary(id));
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> savePatient(@Valid @RequestBody PatientCreateRequestDTO request) {
        PatientResponseDTO patientResponseDTO = patientService.savePatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientResponseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,
                                                            @Valid @RequestBody PatientUpdateRequestDTO request) {
        PatientResponseDTO responseDTO = patientService.updatePatient(request, id);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PatientResponseDTO> updatePatientStatus(@PathVariable UUID id,
                                                                  @Valid @RequestBody PatientStatusUpdateRequestDTO request) {
        return ResponseEntity.ok(patientService.updatePatientStatus(id, request));
    }

    @PostMapping("/{id}/diseases")
    public ResponseEntity<PatientDiseaseResponseDTO> createDisease(@PathVariable UUID id,
                                                                   @Valid @RequestBody PatientDiseaseCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.addDisease(id, request));
    }

    @GetMapping("/{id}/diseases")
    public ResponseEntity<List<PatientDiseaseResponseDTO>> getDiseases(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getDiseases(id));
    }

    @PatchMapping("/{id}/diseases/{diseaseId}")
    public ResponseEntity<PatientDiseaseResponseDTO> updateDisease(@PathVariable UUID id,
                                                                   @PathVariable UUID diseaseId,
                                                                   @Valid @RequestBody PatientDiseaseUpdateRequestDTO request) {
        return ResponseEntity.ok(patientService.updateDisease(id, diseaseId, request));
    }

    @DeleteMapping("/{id}/diseases/{diseaseId}")
    public ResponseEntity<Void> deleteDisease(@PathVariable UUID id, @PathVariable UUID diseaseId) {
        patientService.deleteDisease(id, diseaseId);
        return ResponseEntity.noContent().build();
    }
}
