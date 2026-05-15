package com.meditrack.appointmentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "appointment_status_history")
public class AppointmentStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private AppointmentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private AppointmentStatus toStatus;

    @Column(name = "changed_by_user_id", nullable = false)
    private UUID changedByUserId;

    @Column(length = 1000)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setFromStatus(AppointmentStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public void setToStatus(AppointmentStatus toStatus) {
        this.toStatus = toStatus;
    }

    public void setChangedByUserId(UUID changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
