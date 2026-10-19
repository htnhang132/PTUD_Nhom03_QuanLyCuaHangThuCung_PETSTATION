package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model for Spa Pricing Matrix (bang_gia_spa table) Price = Species × Weight Range × Package
 */
public class SpaPriceMatrix {
    private Long id;
    private Long packageId;
    private String species; // DOG, CAT
    private BigDecimal weightFrom;
    private BigDecimal weightTo;
    private BigDecimal price;
    private Integer estimatedDuration; // in minutes
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private byte[] rowVersion;

    // Constructors
    public SpaPriceMatrix() {}

    public SpaPriceMatrix(
            Long id,
            Long packageId,
            String species,
            BigDecimal weightFrom,
            BigDecimal weightTo,
            BigDecimal price) {
        this.id = id;
        this.packageId = packageId;
        this.species = species;
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

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
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
        return "SpaPriceMatrix{"
                + "id="
                + id
                + ", packageId="
                + packageId
                + ", species='"
                + species
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
