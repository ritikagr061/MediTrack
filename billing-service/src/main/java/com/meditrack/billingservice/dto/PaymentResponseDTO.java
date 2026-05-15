package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.PaymentMethod;
import com.meditrack.billingservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PaymentResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID patientId;
    private UUID invoiceId;
    private UUID appointmentId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private OffsetDateTime paidAt;
    private String failureReason;

    public PaymentResponseDTO(UUID id, UUID hospitalId, UUID patientId, UUID invoiceId, UUID appointmentId,
                              BigDecimal amount, PaymentMethod paymentMethod, PaymentStatus status,
                              String transactionReference, OffsetDateTime paidAt, String failureReason) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.invoiceId = invoiceId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionReference = transactionReference;
        this.paidAt = paidAt;
        this.failureReason = failureReason;
    }

    public UUID getId() { return id; }
    public PaymentStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public UUID getInvoiceId() { return invoiceId; }
}
