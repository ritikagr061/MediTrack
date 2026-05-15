package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.ProfessionalRoleType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MedicalProfessionalResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID userId;
    private String name;
    private ProfessionalRoleType roleType;
    private String specialty;
    private String registrationNumber;
    private String phone;
    private String email;
    private BigDecimal consultationFee;
    private boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public MedicalProfessionalResponseDTO(UUID id, UUID hospitalId, UUID userId, String name,
                                          ProfessionalRoleType roleType, String specialty,
                                          String registrationNumber, String phone, String email,
                                          BigDecimal consultationFee, boolean isActive,
                                          OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.userId = userId;
        this.name = name;
        this.roleType = roleType;
        this.specialty = specialty;
        this.registrationNumber = registrationNumber;
        this.phone = phone;
        this.email = email;
        this.consultationFee = consultationFee;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public ProfessionalRoleType getRoleType() {
        return roleType;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public boolean isActive() {
        return isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
