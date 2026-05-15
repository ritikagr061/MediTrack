package com.meditrack.appointmentservice.dto;

import java.time.OffsetDateTime;

public class AvailableSlotResponseDTO {
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;

    public AvailableSlotResponseDTO(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }
}
