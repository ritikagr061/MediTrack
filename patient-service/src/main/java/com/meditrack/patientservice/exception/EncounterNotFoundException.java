package com.meditrack.patientservice.exception;

public class EncounterNotFoundException extends RuntimeException {
    public EncounterNotFoundException(String message) {
        super(message);
    }
}
