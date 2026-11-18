package com.petstation.model.chung;

/**
 * Trạng thái của chương trình khuyến mãi. Mapping tương ứng cột trang_thai trong bảng
 * chuong_trinh_khuyen_mai: 'HOAT_DONG', 'NGUNG_HOAT_DONG'
 */
public enum TrangThaiKhuyenMai implements MaDBEnum {
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
        return MaDBEnum.tuMaDB(TrangThaiKhuyenMai.class, ma);
    }
}
