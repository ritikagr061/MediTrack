package com.meditrack.patientservice.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PatientDiseaseResponseDTO {
    private UUID id;
    private UUID patientId;
    private String diseaseName;
    private String diseaseCode;
    private boolean isChronic;
    private LocalDate diagnosedAt;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public PatientDiseaseResponseDTO() {
    }

    public PatientDiseaseResponseDTO(UUID id, UUID patientId, String diseaseName, String diseaseCode, boolean isChronic,
                                     LocalDate diagnosedAt, String notes, OffsetDateTime createdAt,
                                     OffsetDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.diseaseName = diseaseName;
        this.diseaseCode = diseaseCode;
        this.isChronic = isChronic;
        this.diagnosedAt = diagnosedAt;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
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

    public boolean isChronic() {
        return isChronic;
    }

    public void setChronic(boolean chronic) {
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
