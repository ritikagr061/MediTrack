package com.meditrack.notificationservice.dto;

import com.meditrack.notificationservice.model.NotificationChannel;
import com.meditrack.notificationservice.model.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID recipientUserId;
    private UUID patientId;
    private NotificationChannel channel;
    private String recipientAddress;
    private String recipientName;
    private String templateCode;
    private String subject;
    private String body;
    private NotificationStatus status;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime sentAt;
    private String failureReason;
    private String sourceService;
    private String sourceEventType;
    private UUID sourceEntityId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public NotificationResponseDTO(UUID id, UUID hospitalId, UUID recipientUserId, UUID patientId,
                                   NotificationChannel channel, String recipientAddress, String recipientName,
                                   String templateCode, String subject, String body, NotificationStatus status,
                                   OffsetDateTime scheduledAt, OffsetDateTime sentAt, String failureReason,
                                   String sourceService, String sourceEventType, UUID sourceEntityId,
                                   OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.recipientUserId = recipientUserId;
        this.patientId = patientId;
        this.channel = channel;
        this.recipientAddress = recipientAddress;
        this.recipientName = recipientName;
        this.templateCode = templateCode;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.failureReason = failureReason;
        this.sourceService = sourceService;
        this.sourceEventType = sourceEventType;
        this.sourceEntityId = sourceEntityId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public String getSourceService() {
        return sourceService;
    }

    public String getSourceEventType() {
        return sourceEventType;
    }

    public UUID getSourceEntityId() {
        return sourceEntityId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
