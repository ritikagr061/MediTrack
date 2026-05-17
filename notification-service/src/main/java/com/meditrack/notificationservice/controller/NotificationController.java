package com.meditrack.notificationservice.controller;

import com.meditrack.notificationservice.dto.NotificationCreateRequestDTO;
import com.meditrack.notificationservice.dto.NotificationResponseDTO;
import com.meditrack.notificationservice.model.NotificationStatus;
import com.meditrack.notificationservice.security.AuthContext;
import com.meditrack.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuthContext authContext;

    public NotificationController(NotificationService notificationService, AuthContext authContext) {
        this.notificationService = notificationService;
        this.authContext = authContext;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION','DOCTOR','NURSE')")
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @Valid @RequestBody NotificationCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION','DOCTOR','NURSE')")
    @GetMapping
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(authContext.scopedHospitalId(hospitalId), patientId, status, page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION','DOCTOR','NURSE')")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotification(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.getNotification(id, authContext.hospitalId()));
    }
}
