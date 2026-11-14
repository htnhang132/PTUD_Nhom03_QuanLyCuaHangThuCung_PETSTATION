package com.petstation.model.chung;

/**
 * Trạng thái của chương trình khuyến mãi. Mapping tương ứng cột trang_thai trong bảng
 * chuong_trinh_khuyen_mai: 'HOAT_DONG', 'NGUNG_HOAT_DONG'
 */
public enum TrangThaiKhuyenMai {
    HOAT_DONG("HOAT_DONG", "Đang áp dụng"),
    NGUNG_HOAT_DONG("NGUNG_HOAT_DONG", "Ngừng áp dụng");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiKhuyenMai(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiKhuyenMai tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiKhuyenMai t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiKhuyenMai với mã: " + ma);
    }
}
