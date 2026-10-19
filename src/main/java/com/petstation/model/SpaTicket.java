package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Domain model for Spa Ticket (phieu_tiep_nhan_spa table) */
public class SpaTicket {
    private Long id;
    private String ticketCode;
    private Long petId;
    private Long customerId;
    private Long packageId;
    private Long employeeId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private BigDecimal basePrice;
    private BigDecimal surchargeAmount;
    private BigDecimal totalAmount;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String notes;
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public SpaTicket() {
        this.status = "PENDING";
        this.surchargeAmount = BigDecimal.ZERO;
    }

    public SpaTicket(Long id, String ticketCode, Long petId, Long customerId, Long packageId) {
        this();
        this.id = id;
        this.ticketCode = ticketCode;
        this.petId = petId;
        this.customerId = customerId;
        this.packageId = packageId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
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

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
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
        return "SpaTicket{"
                + "id="
                + id
                + ", ticketCode='"
                + ticketCode
                + '\''
                + ", petId="
                + petId
                + ", totalAmount="
                + totalAmount
                + ", status='"
                + status
                + '\''
                + '}';
    }
}
