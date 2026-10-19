package com.petstation.model;

/** Biểu diễn dữ liệu vai trò dùng trong nghiệp vụ cửa hàng. */
public enum VaiTro {
    STORE_MANAGER("Cửa hàng trưởng"),
    WAREHOUSE_STAFF("Nhân viên kho"),
    SALES_STAFF("Nhân viên bán hàng"),
    SPA_HOTEL_STAFF("Nhân viên Spa/Hotel");

    /** Tên hiển thị. */
    private final String tenHienThi;

    /**
     * Khởi tạo VaiTro với các giá trị được cung cấp.
     *
     * @param tenHienThi tên hiển thị
     */
    VaiTro(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    /**
     * Lấy tên hiển thị trong VaiTro.
     *
     * @return kết quả lấy tên hiển thị
     */
    public String layTenHienThi() {
        return tenHienThi;
    }

    /**
     * Đọc từ CSDL trong VaiTro.
     *
     * @param giaTri giá trị
     * @return kết quả đọc từ CSDL
     */
    public static VaiTro docTuCSDL(String giaTri) {
        return VaiTro.valueOf(giaTri);
    }
}
