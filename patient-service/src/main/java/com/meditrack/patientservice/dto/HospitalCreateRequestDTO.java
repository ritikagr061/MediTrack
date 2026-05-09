package com.meditrack.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HospitalCreateRequestDTO {
    @NotBlank
    @Size(max = 150, message = "name should not exceed 150 characters")
    private String name;

    @NotBlank
    @Size(max = 50, message = "code should not exceed 50 characters")
    private String code;

    @Size(max = 500, message = "address should not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "phone should not exceed 20 characters")
    private String phone;

    @Email(message = "email should be valid")
    @Size(max = 256, message = "email should not exceed 256 characters")
    private String email;

    @Size(max = 500, message = "logoUrl should not exceed 500 characters")
    private String logoUrl;

    @Size(max = 20, message = "primaryColor should not exceed 20 characters")
    private String primaryColor;

    @Size(max = 20, message = "secondaryColor should not exceed 20 characters")
    private String secondaryColor;

    @Size(max = 500, message = "loginWelcomeText should not exceed 500 characters")
    private String loginWelcomeText;

    public HospitalCreateRequestDTO() {
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
}
