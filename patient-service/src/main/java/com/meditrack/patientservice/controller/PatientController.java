package com.meditrack.patientservice.controller;

import com.meditrack.patientservice.dto.PatientCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.dto.PatientUpdateRequestDTO;
import com.meditrack.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> response = patientService.getPatient();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> savePatient(@Valid @RequestBody PatientCreateRequestDTO request){
        PatientResponseDTO patientResponseDTO = patientService.savePatient(request);
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @RequestBody PatientUpdateRequestDTO request){
        PatientResponseDTO responseDTO=patientService.updatePatient(request,id);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping
    public ResponseEntity<PatientResponseDTO> deletePatient(@PathVariable UUID id){
        PatientResponseDTO responseDTO = patientService.deletePatient(id);
        return ResponseEntity.ok().body(responseDTO);
    }

}
