package com.petstation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Domain model for Customer (khach_hang table) */
public class Customer {
    private Long id;
    private String customerCode;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Integer loyaltyPoints;
    private String membershipTier; // REGULAR, SILVER, GOLD, PLATINUM
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public Customer() {
        this.loyaltyPoints = 0;
        this.membershipTier = "REGULAR";
    }

    public Customer(Long id, String customerCode, String fullName, String phoneNumber) {
        this();
        this.id = id;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
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
        return "Customer{"
                + "id="
                + id
                + ", customerCode='"
                + customerCode
                + '\''
                + ", fullName='"
                + fullName
                + '\''
                + ", phoneNumber='"
                + phoneNumber
                + '\''
                + ", membershipTier='"
                + membershipTier
                + '\''
                + ", loyaltyPoints="
                + loyaltyPoints
                + '}';
    }
}
