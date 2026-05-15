package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.BillingAccountStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class BillingAccountResponseDTO {
    private UUID id;
    private UUID hospitalId;
    private UUID patientId;
    private String patientName;
    private String patientEmail;
    private String accountCode;
    private BigDecimal balanceAmount;
    private BillingAccountStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BillingAccountResponseDTO(UUID id, UUID hospitalId, UUID patientId, String patientName,
                                     String patientEmail, String accountCode, BigDecimal balanceAmount,
                                     BillingAccountStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.accountCode = accountCode;
        this.balanceAmount = balanceAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getHospitalId() { return hospitalId; }
    public UUID getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getPatientEmail() { return patientEmail; }
    public String getAccountCode() { return accountCode; }
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public BillingAccountStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
