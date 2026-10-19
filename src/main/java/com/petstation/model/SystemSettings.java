package com.petstation.model;

import java.time.LocalDateTime;

/**
 * Domain model for System Settings (cai_dat table)
 */
public class SystemSettings {
    private Long id;
    private String key;
    private String value;
    private String dataType; // STRING, INTEGER, DECIMAL, BOOLEAN, DATE
    private String group;
    private String description;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
    private byte[] rowVersion;

    // Constructors
    public SystemSettings() {
        this.dataType = "STRING";
    }

    public SystemSettings(Long id, String key, String value, String group) {
        this();
        this.id = id;
        this.key = key;
        this.value = value;
        this.group = group;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    @Override
    public String toString() {
        return "SystemSettings{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", group='" + group + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
