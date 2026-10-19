package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Domain model for Product (san_pham table) */
public class Product {
    private Long id;
    private String productCode;
    private String productName;
    private Long categoryId;
    private String unit;
    private BigDecimal sellingPrice;
    private String imageUrl;
    private String description;
    private Boolean requiresPrescription;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public Product() {
        this.requiresPrescription = false;
        this.isActive = true;
    }

    public Product(Long id, String productCode, String productName, BigDecimal sellingPrice) {
        this();
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(Boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        return "Product{"
                + "id="
                + id
                + ", productCode='"
                + productCode
                + '\''
                + ", productName='"
                + productName
                + '\''
                + ", sellingPrice="
                + sellingPrice
                + ", isActive="
                + isActive
                + '}';
    }
}
