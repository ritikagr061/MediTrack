package com.meditrack.patientservice.exception;

public class InvalidEncounterException extends RuntimeException {
    public InvalidEncounterException(String message) {
        super(message);
    }
}
