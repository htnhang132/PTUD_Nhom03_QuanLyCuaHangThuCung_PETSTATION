package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Domain model for Price History (lich_su_gia table) Tracks all price changes for products */
public class PriceHistory {
    private Long id;
    private Long productId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String reason;
    private Long changedBy;
    private LocalDateTime changedAt;
    private String changeType; // MANUAL, PROMOTION, EXPIRY_DISCOUNT, COST_ADJUSTMENT
    private byte[] rowVersion;

    // Constructors
    public PriceHistory() {
        this.changeType = "MANUAL";
    }

    public PriceHistory(
            Long id, Long productId, BigDecimal oldPrice, BigDecimal newPrice, Long changedBy) {
        this();
        this.id = id;
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedBy = changedBy;
        this.changedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    @Override
    public String toString() {
        return "PriceHistory{"
                + "id="
                + id
                + ", productId="
                + productId
                + ", oldPrice="
                + oldPrice
                + ", newPrice="
                + newPrice
                + ", changeType='"
                + changeType
                + '\''
                + ", changedAt="
                + changedAt
                + '}';
    }
}
