package com.petstation.model.chung;

/**
 * Trạng thái hiện tại của phòng Pet Hotel. Mapping tương ứng cột trang_thai trong bảng phong_hotel:
 * 'TRONG', 'DANG_O', 'DANG_DON', 'BAO_TRI'
 */
public enum TrangThaiPhongHotel implements MaDBEnum {
    TRONG("TRONG", "Trống"),
    DANG_O("DANG_O", "Có thú cưng ở"),
    DANG_DON("DANG_DON", "Đang dọn dẹp"),
    BAO_TRI("BAO_TRI", "Đang bảo trì");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPhongHotel(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiPhongHotel tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiPhongHotel.class, ma);
    }
}
