package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.InvoiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class InvoiceCreateRequestDTO {
    @NotNull
    private UUID hospitalId;
    @NotNull
    private UUID patientId;
    private UUID appointmentId;
    private UUID encounterId;
    @NotNull
    private InvoiceType invoiceType;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private OffsetDateTime dueAt;
    @NotEmpty
    @Valid
    private List<InvoiceItemRequestDTO> items;

    public UUID getHospitalId() { return hospitalId; }
    public void setHospitalId(UUID hospitalId) { this.hospitalId = hospitalId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public UUID getEncounterId() { return encounterId; }
    public void setEncounterId(UUID encounterId) { this.encounterId = encounterId; }
    public InvoiceType getInvoiceType() { return invoiceType; }
    public void setInvoiceType(InvoiceType invoiceType) { this.invoiceType = invoiceType; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public OffsetDateTime getDueAt() { return dueAt; }
    public void setDueAt(OffsetDateTime dueAt) { this.dueAt = dueAt; }
    public List<InvoiceItemRequestDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemRequestDTO> items) { this.items = items; }
}
