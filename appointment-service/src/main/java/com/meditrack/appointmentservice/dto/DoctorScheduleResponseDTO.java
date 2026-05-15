package com.meditrack.appointmentservice.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class DoctorScheduleResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Integer bufferMinutes;
    private BigDecimal consultationFee;
    private boolean isActive;

    public DoctorScheduleResponseDTO(UUID id, UUID hospitalId, UUID doctorId, DayOfWeek dayOfWeek,
                                     LocalTime startTime, LocalTime endTime, Integer slotDurationMinutes,
                                     Integer bufferMinutes, BigDecimal consultationFee, boolean isActive) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes;
        this.bufferMinutes = bufferMinutes;
        this.consultationFee = consultationFee;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public Integer getBufferMinutes() {
        return bufferMinutes;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public boolean isActive() {
        return isActive;
    }
}
