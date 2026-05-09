package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.Gender;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PatientResponseDTO {
    private UUID id;
    private String patientCode;
    private UUID hospitalId;
    private UUID userId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String aadhar;
    private String pan;
    private LocalDate dateOfBirth;
    private Gender gender;
    private boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public PatientResponseDTO() {
    }

    public PatientResponseDTO(UUID id, String patientCode, UUID hospitalId, UUID userId, String name, String address,
                              String phone, String email, String aadhar, String pan, LocalDate dateOfBirth,
                              Gender gender, boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.patientCode = patientCode;
        this.hospitalId = hospitalId;
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.aadhar = aadhar;
        this.pan = pan;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
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

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

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

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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
