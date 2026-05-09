package com.meditrack.patientservice.dto;

import jakarta.validation.constraints.NotNull;

public class PatientStatusUpdateRequestDTO {
    @NotNull
    private Boolean isActive;

    public PatientStatusUpdateRequestDTO() {
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
