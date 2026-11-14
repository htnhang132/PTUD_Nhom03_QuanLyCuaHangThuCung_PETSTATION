package com.petstation.model.chung;

/**
 * Trạng thái của phiếu nhập kho. Mapping tương ứng cột trang_thai trong bảng phieu_nhap_kho:
 * 'CHO_NHAP', 'DA_NHAP', 'DA_HUY'
 */
public enum TrangThaiPhieuNhap {
    CHO_NHAP("CHO_NHAP", "Chờ nhập kho"),
    DA_NHAP("DA_NHAP", "Đã nhập kho"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPhieuNhap(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiPhieuNhap tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiPhieuNhap t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiPhieuNhap với mã: " + ma);
    }
}
