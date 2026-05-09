package com.meditrack.patientservice.exception;

public class HospitalCodeAlreadyExistsException extends RuntimeException {
    public HospitalCodeAlreadyExistsException(String message) {
        super(message);
    }
}
