package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.EncounterStatus;
import jakarta.validation.constraints.NotNull;

public class EncounterStatusUpdateRequestDTO {
    @NotNull
    private EncounterStatus status;

    public EncounterStatus getStatus() {
        return status;
    }

    public void setStatus(EncounterStatus status) {
        this.status = status;
    }
}
