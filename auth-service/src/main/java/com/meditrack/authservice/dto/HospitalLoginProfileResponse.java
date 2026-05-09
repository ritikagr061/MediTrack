package com.meditrack.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalLoginProfileResponse {
    private UUID hospitalId;
    private String hospitalCode;
    private String hospitalName;
    private String logoUrl;
    private String hospitalMessage;
    private String primaryColor;
    private String secondaryColor;
    private boolean isActive;
}
