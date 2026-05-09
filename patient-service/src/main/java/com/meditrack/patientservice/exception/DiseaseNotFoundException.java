package com.meditrack.patientservice.exception;

public class DiseaseNotFoundException extends RuntimeException {
    public DiseaseNotFoundException(String message) {
        super(message);
    }
}
