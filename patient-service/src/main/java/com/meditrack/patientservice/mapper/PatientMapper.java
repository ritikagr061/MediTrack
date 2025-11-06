package com.meditrack.patientservice.mapper;

import com.meditrack.patientservice.dto.PatientCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.model.Patient;

import java.time.LocalDate;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient){
        return new PatientResponseDTO(
                patient.getId().toString()
                ,patient.getName()
                ,patient.getEmail()
                ,patient.getAddress()
                ,patient.getDateOfBirth().toString()
                ,patient.getRegisteredDate().toString()
        );
    }

    public static Patient toModel(PatientCreateRequestDTO patientRequestDTO){
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth())); //todo give specific format, this should come in config bean which in turn should come from db
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate())); //todo same as above, this above thing is nice problem, like you want to pull from db but bean should be singleton? is it possible

        return patient;
    }
}
