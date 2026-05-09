package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class PatientCreateRequestDTO {
    @NotNull
    private UUID hospitalId;

    private UUID userId;

    @NotBlank
    @Size(max = 100, message = "name should not exceed 100 characters")
    private String name;

    @NotBlank
    @Size(max = 500, message = "address should not exceed 500 characters")
    private String address;

    @NotBlank
    @Size(max = 20, message = "phone should not exceed 20 characters")
    private String phone;

    @NotBlank
    @Email(message = "email should be valid")
    @Size(max = 256, message = "email should not exceed 256 characters")
    private String email;

    @Size(max = 20, message = "aadhar should not exceed 20 characters")
    private String aadhar;

    @Size(max = 20, message = "pan should not exceed 20 characters")
    private String pan;

    private LocalDate dateOfBirth;

    private Gender gender;

    public PatientCreateRequestDTO() {
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
}
