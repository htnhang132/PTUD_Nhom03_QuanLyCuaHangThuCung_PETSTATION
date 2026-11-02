package com.petstation.model;

/** Biểu diễn dữ liệu vai trò dùng trong nghiệp vụ cửa hàng. */
public enum VaiTro {
    QUAN_LY_CUA_HANG("Cửa hàng trưởng"),
    NHAN_VIEN_KHO("Nhân viên kho"),
    NHAN_VIEN_BAN_HANG("Nhân viên bán hàng"),
    NHAN_VIEN_SPA_HOTEL("Nhân viên Spa/Hotel");

    /** Tên hiển thị. */
    private final String tenHienThi;

    VaiTro(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String layTenHienThi() {
        return tenHienThi;
    }

    public static VaiTro docTuCSDL(String giaTri) {
        if (giaTri == null) return NHAN_VIEN_BAN_HANG;
        return switch (giaTri.trim().toUpperCase()) {
            case "QUAN_LY_CUA_HANG", "CUA_HANG_TRUONG", "STORE_MANAGER" -> QUAN_LY_CUA_HANG;
            case "NHAN_VIEN_KHO", "WAREHOUSE_STAFF" -> NHAN_VIEN_KHO;
            case "NHAN_VIEN_BAN_HANG", "SALES_STAFF" -> NHAN_VIEN_BAN_HANG;
            case "NHAN_VIEN_SPA_HOTEL", "SPA_HOTEL_STAFF" -> NHAN_VIEN_SPA_HOTEL;
            default -> VaiTro.valueOf(giaTri.trim());
        };
    }
}
