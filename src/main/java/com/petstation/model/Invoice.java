package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model for Invoice (hoa_don table)
 */
public class Invoice {
    private Long id;
    private String invoiceCode;
    private Long customerId;
    private Long employeeId;
    private Long shiftSessionId;
    private LocalDateTime invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal loyaltyPointsAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal customerPaid;
    private BigDecimal changeAmount;
    private String paymentMethod; // CASH, BANK_TRANSFER, CARD, MIXED
    private String invoiceType; // RETAIL, SPA, HOTEL
    private String status; // DRAFT, COMPLETED, CANCELLED, REFUNDED
    private String notes;
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public Invoice() {
        this.status = "DRAFT";
        this.paymentMethod = "CASH";
        this.loyaltyPointsAmount = BigDecimal.ZERO;
    }

    public Invoice(Long id, String invoiceCode, Long customerId, Long employeeId) {
        this();
        this.id = id;
        this.invoiceCode = invoiceCode;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.invoiceDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getShiftSessionId() {
        return shiftSessionId;
    }

    public void setShiftSessionId(Long shiftSessionId) {
        this.shiftSessionId = shiftSessionId;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getLoyaltyPointsAmount() {
        return loyaltyPointsAmount;
    }

    public void setLoyaltyPointsAmount(BigDecimal loyaltyPointsAmount) {
        this.loyaltyPointsAmount = loyaltyPointsAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getCustomerPaid() {
        return customerPaid;
    }

    public void setCustomerPaid(BigDecimal customerPaid) {
        this.customerPaid = customerPaid;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
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
        return "Invoice{" +
                "id=" + id +
                ", invoiceCode='" + invoiceCode + '\'' +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
