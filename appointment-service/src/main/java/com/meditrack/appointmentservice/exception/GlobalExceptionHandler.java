package com.meditrack.appointmentservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST, request, ex, fieldErrors, false);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            AppointmentNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request, ex, null, false);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            BookingConflictException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request, ex, null, false);
    }

    @ExceptionHandler(InvalidAppointmentRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(
            InvalidAppointmentRequestException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request, ex, null, false);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleDownstream(
            RestClientResponseException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                "Downstream service request failed",
                HttpStatus.BAD_GATEWAY,
                request,
                ex,
                null,
                true
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "Unexpected error while processing request",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                ex,
                null,
                true
        );
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request,
            Exception ex,
            Map<String, String> fieldErrors,
            boolean includeStacktrace
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("traceId", MDC.get("traceId"));
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        body.put("method", request.getMethod());

        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            body.put("fieldErrors", fieldErrors);
        }

        if (includeStacktrace) {
            log.error(
                    "Request failed",
                    kv("http.status_code", status.value()),
                    kv("http.method", request.getMethod()),
                    kv("url.path", request.getRequestURI()),
                    kv("exception.type", ex.getClass().getName()),
                    kv("error.message", ex.getMessage()),
                    kv("downstream.status_code", ex instanceof RestClientResponseException downstream ? downstream.getStatusCode().value() : null),
                    ex
            );
        } else {
            log.warn(
                    "Request rejected",
                    kv("http.status_code", status.value()),
                    kv("http.method", request.getMethod()),
                    kv("url.path", request.getRequestURI()),
                    kv("exception.type", ex.getClass().getName()),
                    kv("error.message", message),
                    kv("validation.errors", fieldErrors)
            );
        }

        return ResponseEntity.status(status).body(body);
    }
}
