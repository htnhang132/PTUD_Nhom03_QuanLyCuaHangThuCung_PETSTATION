package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Domain model for Day Lock (chot_ngay table) Lock transactions after day-end reconciliation */
public class DayLock {
    private Long id;
    private LocalDate lockDate;
    private Long lockedBy;
    private LocalDateTime lockedAt;
    private BigDecimal totalRevenue;
    private BigDecimal totalCash;
    private BigDecimal totalBankTransfer;
    private BigDecimal totalCard;
    private Integer invoiceCount;
    private String notes;
    private String status; // LOCKED, REOPENED
    private byte[] rowVersion;

    // Constructors
    public DayLock() {
        this.status = "LOCKED";
        this.totalRevenue = BigDecimal.ZERO;
        this.totalCash = BigDecimal.ZERO;
        this.totalBankTransfer = BigDecimal.ZERO;
        this.totalCard = BigDecimal.ZERO;
        this.invoiceCount = 0;
    }

    public DayLock(Long id, LocalDate lockDate, Long lockedBy) {
        this();
        this.id = id;
        this.lockDate = lockDate;
        this.lockedBy = lockedBy;
        this.lockedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLockDate() {
        return lockDate;
    }

    public void setLockDate(LocalDate lockDate) {
        this.lockDate = lockDate;
    }

    public Long getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(Long lockedBy) {
        this.lockedBy = lockedBy;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(BigDecimal totalCash) {
        this.totalCash = totalCash;
    }

    public BigDecimal getTotalBankTransfer() {
        return totalBankTransfer;
    }

    public void setTotalBankTransfer(BigDecimal totalBankTransfer) {
        this.totalBankTransfer = totalBankTransfer;
    }

    public BigDecimal getTotalCard() {
        return totalCard;
    }

    public void setTotalCard(BigDecimal totalCard) {
        this.totalCard = totalCard;
    }

    public Integer getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(Integer invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    @Override
    public String toString() {
        return "DayLock{"
                + "id="
                + id
                + ", lockDate="
                + lockDate
                + ", totalRevenue="
                + totalRevenue
                + ", invoiceCount="
                + invoiceCount
                + ", status='"
                + status
                + '\''
                + '}';
    }
}
