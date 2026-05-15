package com.meditrack.patientservice.exception;

public class EncounterAlreadyExistsException extends RuntimeException {
    public EncounterAlreadyExistsException(String message) {
        super(message);
    }
}
