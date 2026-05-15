package com.meditrack.appointmentservice.dto;

import com.meditrack.appointmentservice.model.AppointmentStatus;
import com.meditrack.appointmentservice.model.AppointmentType;
import com.meditrack.appointmentservice.model.BookedByRole;
import com.meditrack.appointmentservice.model.PaymentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AppointmentResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID patientId;
    private UUID doctorId;
    private String appointmentCode;
    private AppointmentType appointmentType;
    private AppointmentStatus status;
    private String reasonText;
    private String notes;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private Integer durationMinutes;
    private PaymentStatus paymentStatus;
    private UUID paymentId;
    private UUID encounterId;
    private UUID bookedByUserId;
    private BookedByRole bookedByRole;
    private OffsetDateTime checkedInAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;

    public AppointmentResponseDTO(UUID id, UUID hospitalId, UUID patientId, UUID doctorId, String appointmentCode,
                                  AppointmentType appointmentType, AppointmentStatus status, String reasonText,
                                  String notes, OffsetDateTime startsAt, OffsetDateTime endsAt,
                                  Integer durationMinutes, PaymentStatus paymentStatus, UUID paymentId,
                                  UUID encounterId, UUID bookedByUserId, BookedByRole bookedByRole,
                                  OffsetDateTime checkedInAt, OffsetDateTime createdAt,
                                  OffsetDateTime updatedAt, Long version) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentCode = appointmentCode;
        this.appointmentType = appointmentType;
        this.status = status;
        this.reasonText = reasonText;
        this.notes = notes;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.durationMinutes = durationMinutes;
        this.paymentStatus = paymentStatus;
        this.paymentId = paymentId;
        this.encounterId = encounterId;
        this.bookedByUserId = bookedByUserId;
        this.bookedByRole = bookedByRole;
        this.checkedInAt = checkedInAt;
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

    public UUID getDoctorId() {
        return doctorId;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getReasonText() {
        return reasonText;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getBookedByUserId() {
        return bookedByUserId;
    }

    public BookedByRole getBookedByRole() {
        return bookedByRole;
    }

    public OffsetDateTime getCheckedInAt() {
        return checkedInAt;
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
