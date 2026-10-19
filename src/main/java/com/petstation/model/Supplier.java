package com.petstation.model;

import java.time.LocalDateTime;

/** Domain model for Supplier (nha_cung_cap table) */
public class Supplier {
    private Long id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private String taxCode;
    private Boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public Supplier() {
        this.isActive = true;
    }

    public Supplier(Long id, String supplierCode, String supplierName, String phoneNumber) {
        this();
        this.id = id;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        return "Supplier{"
                + "id="
                + id
                + ", supplierCode='"
                + supplierCode
                + '\''
                + ", supplierName='"
                + supplierName
                + '\''
                + ", phoneNumber='"
                + phoneNumber
                + '\''
                + ", isActive="
                + isActive
                + '}';
    }
}
