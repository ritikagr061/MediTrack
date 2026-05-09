package com.meditrack.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HospitalProxyNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleHospitalProxyNotFoundException(HospitalProxyNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("exception", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
