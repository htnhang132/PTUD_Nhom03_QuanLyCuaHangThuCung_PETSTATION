package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model for Voucher (voucher table)
 */
public class Voucher {
    private Long id;
    private String voucherCode;
    private String voucherType; // CAMPAIGN, LOYALTY_REWARD
    private BigDecimal discountValue;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer usageLimit;
    private Integer usedCount;
    private String status; // ACTIVE, USED, EXPIRED, CANCELLED
    private Long customerId; // For loyalty rewards
    private Boolean applySpaOnly;
    private LocalDateTime redeemedDate;
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public Voucher() {
        this.usedCount = 0;
        this.status = "ACTIVE";
        this.applySpaOnly = false;
    }

    public Voucher(Long id, String voucherCode, String voucherType, 
                   BigDecimal discountValue, String discountType) {
        this();
        this.id = id;
        this.voucherCode = voucherCode;
        this.voucherType = voucherType;
        this.discountValue = discountValue;
        this.discountType = discountType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Boolean getApplySpaOnly() {
        return applySpaOnly;
    }

    public void setApplySpaOnly(Boolean applySpaOnly) {
        this.applySpaOnly = applySpaOnly;
    }

    public LocalDateTime getRedeemedDate() {
        return redeemedDate;
    }

    public void setRedeemedDate(LocalDateTime redeemedDate) {
        this.redeemedDate = redeemedDate;
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
        return "Voucher{" +
                "id=" + id +
                ", voucherCode='" + voucherCode + '\'' +
                ", voucherType='" + voucherType + '\'' +
                ", discountValue=" + discountValue +
                ", status='" + status + '\'' +
                ", customerId=" + customerId +
                '}';
    }
}
