package com.meditrack.patientservice.dto;

import java.util.UUID;

public class PatientSummaryResponseDTO {
    private UUID patientId;
    private String patientCode;
    private String name;
    private boolean isActive;
    private long diseaseCount;
    private long appointmentCount;
    private long encounterCount;

    public PatientSummaryResponseDTO() {
    }

    public PatientSummaryResponseDTO(UUID patientId, String patientCode, String name, boolean isActive, long diseaseCount,
                                     long appointmentCount, long encounterCount) {
        this.patientId = patientId;
        this.patientCode = patientCode;
        this.name = name;
        this.isActive = isActive;
        this.diseaseCount = diseaseCount;
        this.appointmentCount = appointmentCount;
        this.encounterCount = encounterCount;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getDiseaseCount() {
        return diseaseCount;
    }

    public void setDiseaseCount(long diseaseCount) {
        this.diseaseCount = diseaseCount;
    }

    public long getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(long appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public long getEncounterCount() {
        return encounterCount;
    }

    public void setEncounterCount(long encounterCount) {
        this.encounterCount = encounterCount;
    }
}
