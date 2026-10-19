package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model for Hotel Booking (dat_phong_hotel table)
 */
public class HotelBooking {
    private Long id;
    private String bookingCode;
    private Long petId;
    private Long customerId;
    private Long roomId;
    private LocalDateTime checkInTime;
    private LocalDateTime expectedCheckOutTime;
    private LocalDateTime actualCheckOutTime;
    private String timeFrame; // HOURLY, DAILY, OVERNIGHT
    private BigDecimal basePrice;
    private BigDecimal depositAmount;
    private BigDecimal lateFee;
    private BigDecimal totalAmount;
    private String status; // RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED, OVERDUE
    private String notes;
    private Long employeeId;
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public HotelBooking() {
        this.status = "RESERVED";
        this.lateFee = BigDecimal.ZERO;
    }

    public HotelBooking(Long id, String bookingCode, Long petId, Long customerId, Long roomId) {
        this();
        this.id = id;
        this.bookingCode = bookingCode;
        this.petId = petId;
        this.customerId = customerId;
        this.roomId = roomId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getExpectedCheckOutTime() {
        return expectedCheckOutTime;
    }

    public void setExpectedCheckOutTime(LocalDateTime expectedCheckOutTime) {
        this.expectedCheckOutTime = expectedCheckOutTime;
    }

    public LocalDateTime getActualCheckOutTime() {
        return actualCheckOutTime;
    }

    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) {
        this.actualCheckOutTime = actualCheckOutTime;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
        return "HotelBooking{" +
                "id=" + id +
                ", bookingCode='" + bookingCode + '\'' +
                ", petId=" + petId +
                ", roomId=" + roomId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
