package com.meditrack.notificationservice.controller;

import com.meditrack.notificationservice.dto.NotificationCreateRequestDTO;
import com.meditrack.notificationservice.dto.NotificationResponseDTO;
import com.meditrack.notificationservice.model.NotificationStatus;
import com.meditrack.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @Valid @RequestBody NotificationCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(hospitalId, patientId, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotification(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.getNotification(id));
    }
}
