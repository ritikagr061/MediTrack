package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.RefundStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class RefundResponseDTO {
    private UUID id;
    private UUID paymentId;
    private UUID invoiceId;
    private UUID appointmentId;
    private BigDecimal amount;
    private String reason;
    private RefundStatus status;
    private OffsetDateTime processedAt;

    public RefundResponseDTO(UUID id, UUID paymentId, UUID invoiceId, UUID appointmentId,
                             BigDecimal amount, String reason, RefundStatus status, OffsetDateTime processedAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.processedAt = processedAt;
    }

    public UUID getId() { return id; }
    public RefundStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
}
