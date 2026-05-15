package com.meditrack.appointmentservice.dto;

import com.meditrack.appointmentservice.model.AppointmentType;
import com.meditrack.appointmentservice.model.BookedByRole;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AppointmentCreateRequestDTO {
    @NotNull
    private UUID hospitalId;

    @NotNull
    private UUID patientId;

    @NotNull
    private UUID doctorId;

    @NotNull
    private AppointmentType appointmentType;

    @NotBlank
    @Size(max = 1000)
    private String reasonText;

    @Size(max = 2000)
    private String notes;

    @NotNull
    @Future
    private OffsetDateTime startsAt;

    @NotNull
    private Integer durationMinutes;

    @NotNull
    private UUID bookedByUserId;

    @NotNull
    private BookedByRole bookedByRole;

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

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public UUID getBookedByUserId() {
        return bookedByUserId;
    }

    public void setBookedByUserId(UUID bookedByUserId) {
        this.bookedByUserId = bookedByUserId;
    }

    public BookedByRole getBookedByRole() {
        return bookedByRole;
    }

    public void setBookedByRole(BookedByRole bookedByRole) {
        this.bookedByRole = bookedByRole;
    }
}
