package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PatientUpdateRequestDTO {
    @Size(max = 100, message = "name should not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "address should not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "phone should not exceed 20 characters")
    private String phone;

    @Email(message = "email should be valid")
    @Size(max = 256, message = "email should not exceed 256 characters")
    private String email;

    @Size(max = 20, message = "aadhar should not exceed 20 characters")
    private String aadhar;

    @Size(max = 20, message = "pan should not exceed 20 characters")
    private String pan;

    private LocalDate dateOfBirth;

    private Gender gender;

    private Boolean isActive;

    public PatientUpdateRequestDTO() {
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
