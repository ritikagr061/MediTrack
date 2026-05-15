package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.ProfessionalRoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class MedicalProfessionalCreateRequestDTO {
    @NotNull
    private UUID hospitalId;

    private UUID userId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private ProfessionalRoleType roleType;

    @Size(max = 120)
    private String specialty;

    @Size(max = 80)
    private String registrationNumber;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Email
    @Size(max = 256)
    private String email;

    private BigDecimal consultationFee;

    public UUID getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(UUID hospitalId) {
        this.hospitalId = hospitalId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProfessionalRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(ProfessionalRoleType roleType) {
        this.roleType = roleType;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }
}
