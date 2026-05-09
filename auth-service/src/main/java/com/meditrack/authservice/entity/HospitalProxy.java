package com.meditrack.authservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hospital_proxy")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HospitalProxy {
    @Id
    @Column(name = "hospital_id", nullable = false)
    private UUID hospitalId;

    @Column(name = "hospital_code", nullable = false, unique = true, length = 50)
    private String hospitalCode;

    @Column(name = "hospital_name", nullable = false, length = 150)
    private String hospitalName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "hospital_message", length = 500)
    private String hospitalMessage;

    @Column(name = "primary_color", length = 20)
    private String primaryColor;

    @Column(name = "secondary_color", length = 20)
    private String secondaryColor;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.lastSyncedAt == null) {
            this.lastSyncedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
