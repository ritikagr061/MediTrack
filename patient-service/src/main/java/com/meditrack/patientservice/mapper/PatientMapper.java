package com.meditrack.patientservice.mapper;

import com.meditrack.patientservice.dto.HospitalCreateRequestDTO;
import com.meditrack.patientservice.dto.HospitalLoginConfigResponseDTO;
import com.meditrack.patientservice.dto.HospitalResponseDTO;
import com.meditrack.patientservice.dto.PatientCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientDiseaseCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientDiseaseResponseDTO;
import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.dto.PatientSummaryResponseDTO;
import com.meditrack.patientservice.model.Hospital;
import com.meditrack.patientservice.model.Patient;
import com.meditrack.patientservice.model.PatientDisease;

public final class PatientMapper {
    private PatientMapper() {
    }

    public static PatientResponseDTO toDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getPatientCode(),
                patient.getHospitalId(),
                patient.getUserId(),
                patient.getName(),
                patient.getAddress(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAadhar(),
                patient.getPan(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.isActive(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    public static HospitalResponseDTO toHospitalDTO(Hospital hospital) {
        return new HospitalResponseDTO(
                hospital.getId(),
                hospital.getName(),
                hospital.getCode(),
                hospital.getAddress(),
                hospital.getPhone(),
                hospital.getEmail(),
                hospital.getLogoUrl(),
                hospital.getPrimaryColor(),
                hospital.getSecondaryColor(),
                hospital.getLoginWelcomeText(),
                hospital.isActive(),
                hospital.getCreatedAt(),
                hospital.getUpdatedAt()
        );
    }

    public static HospitalLoginConfigResponseDTO toHospitalLoginConfigDTO(Hospital hospital) {
        return new HospitalLoginConfigResponseDTO(
                hospital.getId(),
                hospital.getCode(),
                hospital.getName(),
                hospital.getLogoUrl(),
                hospital.getPrimaryColor(),
                hospital.getSecondaryColor(),
                hospital.getLoginWelcomeText(),
                hospital.isActive()
        );
    }

    public static Hospital toHospitalModel(HospitalCreateRequestDTO requestDTO) {
        Hospital hospital = new Hospital();
        hospital.setName(requestDTO.getName());
        hospital.setCode(requestDTO.getCode().trim());
        hospital.setAddress(requestDTO.getAddress());
        hospital.setPhone(requestDTO.getPhone());
        hospital.setEmail(requestDTO.getEmail());
        hospital.setLogoUrl(requestDTO.getLogoUrl());
        hospital.setPrimaryColor(requestDTO.getPrimaryColor());
        hospital.setSecondaryColor(requestDTO.getSecondaryColor());
        hospital.setLoginWelcomeText(requestDTO.getLoginWelcomeText());
        hospital.setActive(true);
        return hospital;
    }

    public static Patient toModel(PatientCreateRequestDTO requestDTO) {
        Patient patient = new Patient();
        patient.setHospitalId(requestDTO.getHospitalId());
        patient.setUserId(requestDTO.getUserId());
        patient.setName(requestDTO.getName());
        patient.setAddress(requestDTO.getAddress());
        patient.setPhone(requestDTO.getPhone());
        patient.setEmail(requestDTO.getEmail());
        patient.setAadhar(requestDTO.getAadhar());
        patient.setPan(requestDTO.getPan());
        patient.setDateOfBirth(requestDTO.getDateOfBirth());
        patient.setGender(requestDTO.getGender());
        patient.setActive(true);
        return patient;
    }

    public static PatientDiseaseResponseDTO toDiseaseDTO(PatientDisease patientDisease) {
        return new PatientDiseaseResponseDTO(
                patientDisease.getId(),
                patientDisease.getPatient().getId(),
                patientDisease.getDiseaseName(),
                patientDisease.getDiseaseCode(),
                patientDisease.isChronic(),
                patientDisease.getDiagnosedAt(),
                patientDisease.getNotes(),
                patientDisease.getCreatedAt(),
                patientDisease.getUpdatedAt()
        );
    }

    public static PatientDisease toDiseaseModel(Patient patient, PatientDiseaseCreateRequestDTO requestDTO) {
        PatientDisease patientDisease = new PatientDisease();
        patientDisease.setPatient(patient);
        patientDisease.setDiseaseName(requestDTO.getDiseaseName());
        patientDisease.setDiseaseCode(requestDTO.getDiseaseCode());
        patientDisease.setChronic(Boolean.TRUE.equals(requestDTO.getIsChronic()));
        patientDisease.setDiagnosedAt(requestDTO.getDiagnosedAt());
        patientDisease.setNotes(requestDTO.getNotes());
        return patientDisease;
    }

    public static PatientSummaryResponseDTO toSummaryDTO(Patient patient, long diseaseCount) {
        return new PatientSummaryResponseDTO(
                patient.getId(),
                patient.getPatientCode(),
                patient.getName(),
                patient.isActive(),
                diseaseCount,
                0L,
                0L
        );
    }
}
