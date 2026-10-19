package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model for Hotel Room Pricing (bang_gia_hotel table) Price by time frame (hourly, daily,
 * overnight) and weight
 */
public class HotelPricing {
    private Long id;
    private Long roomId;
    private String timeFrame; // HOURLY, DAILY, OVERNIGHT
    private BigDecimal weightFrom;
    private BigDecimal weightTo;
    private BigDecimal price;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private byte[] rowVersion;

    // Constructors
    public HotelPricing() {}

    public HotelPricing(
            Long id,
            Long roomId,
            String timeFrame,
            BigDecimal weightFrom,
            BigDecimal weightTo,
            BigDecimal price) {
        this.id = id;
        this.roomId = roomId;
        this.timeFrame = timeFrame;
        this.weightFrom = weightFrom;
        this.weightTo = weightTo;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public BigDecimal getWeightFrom() {
        return weightFrom;
    }

    public void setWeightFrom(BigDecimal weightFrom) {
        this.weightFrom = weightFrom;
    }

    public BigDecimal getWeightTo() {
        return weightTo;
    }

    public void setWeightTo(BigDecimal weightTo) {
        this.weightTo = weightTo;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    @Override
    public String toString() {
        return "HotelPricing{"
                + "id="
                + id
                + ", roomId="
                + roomId
                + ", timeFrame='"
                + timeFrame
                + '\''
                + ", weightFrom="
                + weightFrom
                + ", weightTo="
                + weightTo
                + ", price="
                + price
                + '}';
    }
}
