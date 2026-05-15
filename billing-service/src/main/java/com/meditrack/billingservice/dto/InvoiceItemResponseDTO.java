package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.InvoiceItemType;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceItemResponseDTO {
    private UUID id;
    private String itemName;
    private InvoiceItemType itemType;
    private Integer quantity;
    private BigDecimal unitAmount;
    private BigDecimal totalAmount;

    public InvoiceItemResponseDTO(UUID id, String itemName, InvoiceItemType itemType, Integer quantity,
                                  BigDecimal unitAmount, BigDecimal totalAmount) {
        this.id = id;
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitAmount = unitAmount;
        this.totalAmount = totalAmount;
    }

    public UUID getId() { return id; }
    public String getItemName() { return itemName; }
    public InvoiceItemType getItemType() { return itemType; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitAmount() { return unitAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
