package com.petstation.model.chung;

/**
 * Trạng thái của lịch làm việc nhân sự. Mapping tương ứng cột trang_thai trong bảng lich_lam_viec:
 * 'DANG_MO', 'DA_KHOA', 'DA_HUY'
 */
public enum TrangThaiLichLamViec implements MaDBEnum {
    DANG_MO("DANG_MO", "Đang mở (chờ xếp/chỉnh sửa)"),
    DA_KHOA("DA_KHOA", "Đã khóa chính thức"),
    DA_HUY("DA_HUY", "Đã hủy ca");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiLichLamViec(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public boolean coTheChinhSua() {
        return this == DANG_MO;
    }

    public static TrangThaiLichLamViec tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiLichLamViec.class, ma);
    }
}
