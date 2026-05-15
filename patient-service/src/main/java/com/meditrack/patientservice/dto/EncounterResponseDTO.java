package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.EncounterLocationType;
import com.meditrack.patientservice.model.EncounterStatus;
import com.meditrack.patientservice.model.EncounterType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EncounterResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID patientId;
    private UUID appointmentId;
    private UUID attendingDoctorId;
    private UUID createdByDoctorId;
    private EncounterType encounterType;
    private EncounterStatus status;
    private String chiefComplaint;
    private String reasonText;
    private EncounterLocationType locationType;
    private String locationText;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;

    public EncounterResponseDTO(UUID id, UUID hospitalId, UUID patientId, UUID appointmentId,
                                UUID attendingDoctorId, UUID createdByDoctorId, EncounterType encounterType,
                                EncounterStatus status, String chiefComplaint, String reasonText,
                                EncounterLocationType locationType, String locationText,
                                OffsetDateTime startedAt, OffsetDateTime endedAt,
                                OffsetDateTime createdAt, OffsetDateTime updatedAt, Long version) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.attendingDoctorId = attendingDoctorId;
        this.createdByDoctorId = createdByDoctorId;
        this.encounterType = encounterType;
        this.status = status;
        this.chiefComplaint = chiefComplaint;
        this.reasonText = reasonText;
        this.locationType = locationType;
        this.locationText = locationText;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
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

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public UUID getAttendingDoctorId() {
        return attendingDoctorId;
    }

    public UUID getCreatedByDoctorId() {
        return createdByDoctorId;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public String getReasonText() {
        return reasonText;
    }

    public EncounterLocationType getLocationType() {
        return locationType;
    }

    public String getLocationText() {
        return locationText;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
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
