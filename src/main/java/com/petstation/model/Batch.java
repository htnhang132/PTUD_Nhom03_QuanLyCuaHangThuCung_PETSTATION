package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Domain model for Product Batch (lo_hang table) */
public class Batch {
    private Long id;
    private String batchCode;
    private Long productId;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private Integer quantity;
    private BigDecimal costPrice;
    private Long supplierId;
    private String status; // IN_STOCK, LOW_STOCK, EXPIRED, SOLD_OUT
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public Batch() {
        this.status = "IN_STOCK";
    }

    public Batch(
            Long id, String batchCode, Long productId, Integer quantity, BigDecimal costPrice) {
        this();
        this.id = id;
        this.batchCode = batchCode;
        this.productId = productId;
        this.quantity = quantity;
        this.costPrice = costPrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    @Override
    public String toString() {
        return "Batch{"
                + "id="
                + id
                + ", batchCode='"
                + batchCode
                + '\''
                + ", productId="
                + productId
                + ", quantity="
                + quantity
                + ", expiryDate="
                + expiryDate
                + ", status='"
                + status
                + '\''
                + '}';
    }
}
