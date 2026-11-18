package com.petstation.model.chung;

/**
 * Trạng thái hợp tác của nhà cung cấp. Mapping tương ứng cột trang_thai trong bảng nha_cung_cap:
 * 'DANG_HOP_TAC', 'NGUNG_HOP_TAC'
 */
public enum TrangThaiNhaCungCap implements MaDBEnum {
    DANG_HOP_TAC("DANG_HOP_TAC", "Đang hợp tác"),
    NGUNG_HOP_TAC("NGUNG_HOP_TAC", "Ngừng hợp tác");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiNhaCungCap(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiNhaCungCap tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiNhaCungCap.class, ma);
    }
}
