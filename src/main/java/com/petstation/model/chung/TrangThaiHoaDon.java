package com.petstation.model.chung;

/**
 * Trạng thái của hóa đơn bán hàng / dịch vụ. Mapping tương ứng cột trang_thai trong bảng hoa_don:
 * 'DA_THANH_TOAN', 'DA_HUY', 'CHO_THANH_TOAN', 'GIO_TREO'
 */
public enum TrangThaiHoaDon {
    DA_THANH_TOAN("DA_THANH_TOAN", "Đã thanh toán"),
    DA_HUY("DA_HUY", "Đã hủy"),
    CHO_THANH_TOAN("CHO_THANH_TOAN", "Chờ thanh toán"),
    GIO_TREO("GIO_TREO", "Giỏ treo");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiHoaDon(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public boolean daHoanTat() {
        return this == DA_THANH_TOAN;
    }

    public static TrangThaiHoaDon tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiHoaDon t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException(
                "Không tìm thấy TrangThaiHoaDon tương ứng với mã: " + ma);
    }
}
