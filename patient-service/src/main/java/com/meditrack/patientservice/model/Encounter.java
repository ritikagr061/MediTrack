package com.meditrack.patientservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "encounters", uniqueConstraints = {
        @UniqueConstraint(name = "uk_encounters_appointment", columnNames = {"appointment_id"})
})
public class Encounter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hospital_id", nullable = false)
    private UUID hospitalId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "attending_doctor_id", nullable = false)
    private UUID attendingDoctorId;

    @Column(name = "created_by_doctor_id", nullable = false)
    private UUID createdByDoctorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "encounter_type", nullable = false, length = 30)
    private EncounterType encounterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EncounterStatus status;

    @Column(name = "chief_complaint", nullable = false, length = 1000)
    private String chiefComplaint;

    @Column(name = "reason_text", length = 1000)
    private String reasonText;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", length = 30)
    private EncounterLocationType locationType;

    @Column(name = "location_text", length = 255)
    private String locationText;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
        if (startedAt == null) {
            startedAt = now;
        }
        if (status == null) {
            status = EncounterStatus.IN_PROGRESS;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public UUID getId() {
        return id;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(UUID hospitalId) {
        this.hospitalId = hospitalId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public UUID getAttendingDoctorId() {
        return attendingDoctorId;
    }

    public void setAttendingDoctorId(UUID attendingDoctorId) {
        this.attendingDoctorId = attendingDoctorId;
    }

    public UUID getCreatedByDoctorId() {
        return createdByDoctorId;
    }

    public void setCreatedByDoctorId(UUID createdByDoctorId) {
        this.createdByDoctorId = createdByDoctorId;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public void setStatus(EncounterStatus status) {
        this.status = status;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public EncounterLocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(EncounterLocationType locationType) {
        this.locationType = locationType;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(OffsetDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}
