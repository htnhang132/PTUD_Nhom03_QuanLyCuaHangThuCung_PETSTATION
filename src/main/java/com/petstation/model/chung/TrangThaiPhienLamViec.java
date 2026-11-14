package com.petstation.model.chung;

/**
 * Trạng thái của phiên làm việc (ca thu ngân). Mapping tương ứng cột trang_thai trong bảng
 * phien_lam_viec: 'DANG_MO', 'DA_KHOA', 'DA_BAN_GIAO', 'CHO_DUYET', 'DA_DOI_SOAT'
 */
public enum TrangThaiPhienLamViec {
    DANG_MO("DANG_MO", "Đang mở"),
    DA_KHOA("DA_KHOA", "Đã khóa"),
    DA_BAN_GIAO("DA_BAN_GIAO", "Đã bàn giao"),
    CHO_DUYET("CHO_DUYET", "Chờ duyệt"),
    DA_DOI_SOAT("DA_DOI_SOAT", "Đã đối soát");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPhienLamViec(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiPhienLamViec tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiPhienLamViec t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiPhienLamViec với mã: " + ma);
    }
}
