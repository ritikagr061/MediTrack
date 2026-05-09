package com.meditrack.patientservice.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class HospitalResponseDTO {
    private UUID id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String email;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String loginWelcomeText;
    private boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public HospitalResponseDTO() {
    }

    public HospitalResponseDTO(UUID id, String name, String code, String address, String phone, String email,
                               String logoUrl, String primaryColor, String secondaryColor, String loginWelcomeText,
                               boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.logoUrl = logoUrl;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.loginWelcomeText = loginWelcomeText;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
