package com.meditrack.patientservice.dto;

import java.util.UUID;

public class HospitalLoginConfigResponseDTO {
    private UUID hospitalId;
    private String hospitalCode;
    private String hospitalName;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String loginWelcomeText;
    private boolean isActive;

    public HospitalLoginConfigResponseDTO() {
    }

    public HospitalLoginConfigResponseDTO(UUID hospitalId, String hospitalCode, String hospitalName, String logoUrl,
                                          String primaryColor, String secondaryColor, String loginWelcomeText,
                                          boolean isActive) {
        this.hospitalId = hospitalId;
        this.hospitalCode = hospitalCode;
        this.hospitalName = hospitalName;
        this.logoUrl = logoUrl;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.loginWelcomeText = loginWelcomeText;
        this.isActive = isActive;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(UUID hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getLoginWelcomeText() {
        return loginWelcomeText;
    }

    public void setLoginWelcomeText(String loginWelcomeText) {
        this.loginWelcomeText = loginWelcomeText;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
