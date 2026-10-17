package com.petstation.model;

public enum Role {
    STORE_MANAGER("Cửa hàng trưởng"),
    WAREHOUSE_STAFF("Nhân viên kho"),
    SALES_STAFF("Nhân viên bán hàng"),
    SPA_HOTEL_STAFF("Nhân viên Spa/Hotel");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static Role fromDatabase(String value) {
        return Role.valueOf(value);
    }
}
