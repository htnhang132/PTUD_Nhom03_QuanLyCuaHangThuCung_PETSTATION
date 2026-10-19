package com.petstation.model;

import java.time.LocalDateTime;

/**
 * Domain model for Spa Service Package (goi_dich_vu_spa table)
 */
public class SpaPackage {
    private Long id;
    private String packageCode;
    private String packageName;
    private String description;
    private String packageType; // COMBO or SINGLE
    private String status; // ACTIVE, INACTIVE
    private LocalDateTime createdAt;
    private byte[] rowVersion;

    // Constructors
    public SpaPackage() {
        this.status = "ACTIVE";
    }

    public SpaPackage(Long id, String packageCode, String packageName, String packageType) {
        this();
        this.id = id;
        this.packageCode = packageCode;
        this.packageName = packageName;
        this.packageType = packageType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "SpaPackage{" +
                "id=" + id +
                ", packageCode='" + packageCode + '\'' +
                ", packageName='" + packageName + '\'' +
                ", packageType='" + packageType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
