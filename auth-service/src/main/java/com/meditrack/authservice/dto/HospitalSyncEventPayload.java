package com.meditrack.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalSyncEventPayload {
    private String eventType;
    private UUID hospitalId;
    private String hospitalCode;
    private String hospitalName;
    private String logoUrl;
    private String loginWelcomeText;
    private String primaryColor;
    private String secondaryColor;
    private boolean isActive;
    private OffsetDateTime occurredAt;
}
