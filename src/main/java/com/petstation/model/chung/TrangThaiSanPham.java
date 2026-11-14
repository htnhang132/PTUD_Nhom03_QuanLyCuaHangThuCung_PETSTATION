package com.petstation.model.chung;

/**
 * Trạng thái kinh doanh của sản phẩm. Mapping tương ứng cột trang_thai trong bảng san_pham:
 * 'DANG_BAN', 'NGUNG_BAN'
 */
public enum TrangThaiSanPham {
    DANG_BAN("DANG_BAN", "Đang kinh doanh"),
    NGUNG_BAN("NGUNG_BAN", "Ngừng kinh doanh");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiSanPham(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public boolean choPhepBan() {
        return this == DANG_BAN;
    }

    public static TrangThaiSanPham tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiSanPham t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiSanPham với mã: " + ma);
    }
}
