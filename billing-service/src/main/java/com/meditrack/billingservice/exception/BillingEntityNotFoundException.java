package com.meditrack.billingservice.exception;

public class BillingEntityNotFoundException extends RuntimeException {
    public BillingEntityNotFoundException(String message) {
        super(message);
    }
}
