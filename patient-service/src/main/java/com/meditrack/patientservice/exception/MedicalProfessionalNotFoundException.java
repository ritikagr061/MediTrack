package com.meditrack.patientservice.exception;

public class MedicalProfessionalNotFoundException extends RuntimeException {
    public MedicalProfessionalNotFoundException(String message) {
        super(message);
    }
}
