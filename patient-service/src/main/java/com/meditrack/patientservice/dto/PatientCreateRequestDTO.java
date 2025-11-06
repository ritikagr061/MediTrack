package com.meditrack.patientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PatientCreateRequestDTO {
    @NotBlank
    @Size(min = 1, max = 100,message = "name should be minimum 1 and maximum 100 characters")
    private String name;

    @NotBlank
    @Size(min = 1, max = 256,message = "email character length should be minimum 1 and maximum 100")
    private String email;

    @NotBlank
    private String address;

    @NotNull
    private String dateOfBirth;

    @NotNull
    private String registeredDate;

    public PatientCreateRequestDTO(String name, String email, String address, String dateOfBirth, String registeredDate) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.registeredDate = registeredDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
