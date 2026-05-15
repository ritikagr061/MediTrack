package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.EncounterLocationType;
import com.meditrack.patientservice.model.EncounterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EncounterCreateRequestDTO {
    @NotNull
    private UUID hospitalId;

    @NotNull
    private UUID patientId;

    private UUID appointmentId;

    @NotNull
    private UUID attendingDoctorId;

    @NotNull
    private UUID createdByDoctorId;

    @NotNull
    private EncounterType encounterType;

    @NotBlank
    @Size(max = 1000)
    private String chiefComplaint;

    @Size(max = 1000)
    private String reasonText;

    private EncounterLocationType locationType;

    @Size(max = 255)
    private String locationText;

    private OffsetDateTime startedAt;

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
}
