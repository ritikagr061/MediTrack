package com.meditrack.billingservice.dto;

import com.meditrack.billingservice.model.InvoiceItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class InvoiceItemRequestDTO {
    @NotBlank
    private String itemName;
    @NotNull
    private InvoiceItemType itemType;
    @NotNull
    @Min(1)
    private Integer quantity;
    @NotNull
    private BigDecimal unitAmount;

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public InvoiceItemType getItemType() { return itemType; }
    public void setItemType(InvoiceItemType itemType) { this.itemType = itemType; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitAmount() { return unitAmount; }
    public void setUnitAmount(BigDecimal unitAmount) { this.unitAmount = unitAmount; }
}
