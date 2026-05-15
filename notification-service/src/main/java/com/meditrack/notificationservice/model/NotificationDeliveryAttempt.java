package com.meditrack.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "notification_delivery_attempts")
public class NotificationDeliveryAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "notification_request_id", nullable = false)
    private UUID notificationRequestId;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "provider_name", length = 80)
    private String providerName;

    @Column(name = "provider_message_id", length = 120)
    private String providerMessageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryAttemptStatus status;

    @Column(name = "response_message", length = 1000)
    private String responseMessage;

    @Column(name = "attempted_at", nullable = false)
    private OffsetDateTime attemptedAt;

    @PrePersist
    public void onCreate() {
        if (attemptedAt == null) {
            attemptedAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getNotificationRequestId() {
        return notificationRequestId;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public DeliveryAttemptStatus getStatus() {
        return status;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public OffsetDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(OffsetDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public void setNotificationRequestId(UUID notificationRequestId) {
        this.notificationRequestId = notificationRequestId;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderMessageId(String providerMessageId) {
        this.providerMessageId = providerMessageId;
    }

    public void setStatus(DeliveryAttemptStatus status) {
        this.status = status;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
