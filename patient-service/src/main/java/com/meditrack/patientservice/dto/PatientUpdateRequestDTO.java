package com.meditrack.patientservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PatientUpdateRequestDTO {
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;
    private String registeredDate;
}
