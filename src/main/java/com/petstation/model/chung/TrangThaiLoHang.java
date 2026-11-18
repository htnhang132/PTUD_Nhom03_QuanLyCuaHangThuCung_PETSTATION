package com.petstation.model.chung;

/**
 * Trạng thái của lô hàng theo dõi theo hạn sử dụng (FEFO). Mapping tương ứng cột trang_thai trong
 * bảng lo_hang: 'SAN_SANG', 'HET_HAN', 'TAM_KHOA', 'HET_HANG'
 */
public enum TrangThaiLoHang implements MaDBEnum {
    SAN_SANG("SAN_SANG", "Sẵn sàng xuất"),
    HET_HAN("HET_HAN", "Đã hết hạn"),
    TAM_KHOA("TAM_KHOA", "Tạm khóa"),
    HET_HANG("HET_HANG", "Đã hết hàng");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiLoHang(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiLoHang tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiLoHang.class, ma);
    }
}
