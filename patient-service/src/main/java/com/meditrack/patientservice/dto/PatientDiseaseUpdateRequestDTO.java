package com.meditrack.patientservice.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PatientDiseaseUpdateRequestDTO {
    @Size(max = 255, message = "diseaseName should not exceed 255 characters")
    private String diseaseName;

    @Size(max = 50, message = "diseaseCode should not exceed 50 characters")
    private String diseaseCode;

    private Boolean isChronic;

    private LocalDate diagnosedAt;

    @Size(max = 2000, message = "notes should not exceed 2000 characters")
    private String notes;

    public PatientDiseaseUpdateRequestDTO() {
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }

    public Boolean getIsChronic() {
        return isChronic;
    }

    public void setIsChronic(Boolean chronic) {
        isChronic = chronic;
    }

    public LocalDate getDiagnosedAt() {
        return diagnosedAt;
    }

    public void setDiagnosedAt(LocalDate diagnosedAt) {
        this.diagnosedAt = diagnosedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
