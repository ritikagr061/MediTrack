package com.meditrack.appointmentservice.exception;

public class InvalidAppointmentRequestException extends RuntimeException {
    public InvalidAppointmentRequestException(String message) {
        super(message);
    }
}
