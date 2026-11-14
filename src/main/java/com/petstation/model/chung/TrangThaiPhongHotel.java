package com.petstation.model.chung;

/**
 * Trạng thái hiện tại của phòng Pet Hotel. Mapping tương ứng cột trang_thai trong bảng phong_hotel:
 * 'TRONG', 'DANG_O', 'DANG_DON', 'BAO_TRI'
 */
public enum TrangThaiPhongHotel {
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
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiPhongHotel t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiPhongHotel với mã: " + ma);
    }
}
