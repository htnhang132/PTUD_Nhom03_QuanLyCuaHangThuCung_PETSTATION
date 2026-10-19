package com.petstation.model;

import java.math.BigDecimal;

/**
 * Domain model for Spa Ticket Detail (chi_tiet_phieu_spa table)
 * For individual services and surcharges
 */
public class SpaTicketDetail {
    private Long id;
    private Long spaTicketId;
    private Long packageId; // NULL for surcharges
    private String detailType; // SERVICE, SURCHARGE
    private String description;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
    private String notes;
    private byte[] rowVersion;

    // Constructors
    public SpaTicketDetail() {
        this.quantity = 1;
    }

    public SpaTicketDetail(Long id, Long spaTicketId, String detailType, 
                           String description, BigDecimal unitPrice) {
        this();
        this.id = id;
        this.spaTicketId = spaTicketId;
        this.detailType = detailType;
        this.description = description;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    // Business logic
    public void calculateLineTotal() {
        if (unitPrice != null && quantity != null) {
            this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpaTicketId() {
        return spaTicketId;
    }

    public void setSpaTicketId(Long spaTicketId) {
        this.spaTicketId = spaTicketId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    @Override
    public String toString() {
        return "SpaTicketDetail{" +
                "id=" + id +
                ", spaTicketId=" + spaTicketId +
                ", detailType='" + detailType + '\'' +
                ", description='" + description + '\'' +
                ", lineTotal=" + lineTotal +
                '}';
    }
}
