package com.meditrack.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HospitalProxyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleHospitalProxyNotFoundException(
            HospitalProxyNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request, ex, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(
                "Unexpected error while processing request",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                ex,
                true
        );
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request,
            Exception ex,
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

        if (includeStacktrace) {
            log.error(
                    "Request failed",
                    kv("http.status_code", status.value()),
                    kv("http.method", request.getMethod()),
                    kv("url.path", request.getRequestURI()),
                    kv("exception.type", ex.getClass().getName()),
                    kv("error.message", ex.getMessage()),
                    ex
            );
        } else {
            log.warn(
                    "Request rejected",
                    kv("http.status_code", status.value()),
                    kv("http.method", request.getMethod()),
                    kv("url.path", request.getRequestURI()),
                    kv("exception.type", ex.getClass().getName()),
                    kv("error.message", message)
            );
        }

        return ResponseEntity.status(status).body(body);
    }
}
