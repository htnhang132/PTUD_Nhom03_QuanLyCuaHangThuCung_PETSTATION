package com.petstation.model;

/** Biểu diễn dữ liệu vai trò dùng trong nghiệp vụ cửa hàng. */
public enum VaiTro {
    STORE_MANAGER("Cửa hàng trưởng"),
    WAREHOUSE_STAFF("Nhân viên kho"),
    SALES_STAFF("Nhân viên bán hàng"),
    SPA_HOTEL_STAFF("Nhân viên Spa/Hotel");

    /** Tên hiển thị. */
    private final String tenHienThi;

    VaiTro(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String layTenHienThi() {
        return tenHienThi;
    }

    public static VaiTro docTuCSDL(String giaTri) {
        return VaiTro.valueOf(giaTri);
    }
}
