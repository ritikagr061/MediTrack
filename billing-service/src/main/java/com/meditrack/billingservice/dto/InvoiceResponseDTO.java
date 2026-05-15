package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.InvoiceStatus;
import com.meditrack.billingservice.model.InvoiceType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class InvoiceResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID patientId;
    private UUID appointmentId;
    private UUID encounterId;
    private String invoiceNumber;
    private InvoiceType invoiceType;
    private InvoiceStatus status;
    private BigDecimal subtotalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal dueAmount;
    private OffsetDateTime issuedAt;
    private OffsetDateTime dueAt;
    private List<InvoiceItemResponseDTO> items;

    public InvoiceResponseDTO(UUID id, UUID hospitalId, UUID patientId, UUID appointmentId, UUID encounterId,
                              String invoiceNumber, InvoiceType invoiceType, InvoiceStatus status,
                              BigDecimal subtotalAmount, BigDecimal discountAmount, BigDecimal taxAmount,
                              BigDecimal totalAmount, BigDecimal dueAmount, OffsetDateTime issuedAt,
                              OffsetDateTime dueAt, List<InvoiceItemResponseDTO> items) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.encounterId = encounterId;
        this.invoiceNumber = invoiceNumber;
        this.invoiceType = invoiceType;
        this.status = status;
        this.subtotalAmount = subtotalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.dueAmount = dueAmount;
        this.issuedAt = issuedAt;
        this.dueAt = dueAt;
        this.items = items;
    }

    public UUID getId() { return id; }
    public UUID getHospitalId() { return hospitalId; }
    public UUID getPatientId() { return patientId; }
    public UUID getAppointmentId() { return appointmentId; }
    public UUID getEncounterId() { return encounterId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public InvoiceStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getDueAmount() { return dueAmount; }
    public List<InvoiceItemResponseDTO> getItems() { return items; }
}
