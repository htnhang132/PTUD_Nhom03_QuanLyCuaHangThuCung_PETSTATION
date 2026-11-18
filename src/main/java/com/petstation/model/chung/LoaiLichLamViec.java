package com.petstation.model.chung;

/**
 * Loại hình lịch làm việc (đi làm bình thường hoặc nghỉ phép). Mapping tương ứng cột loai trong
 * bảng lich_lam_viec: 'DI_LAM', 'NGHI_PHEP'
 */
public enum LoaiLichLamViec implements MaDBEnum {
    DI_LAM("DI_LAM", "Đi làm"),
    NGHI_PHEP("NGHI_PHEP", "Nghỉ phép");

    private final String maDB;
    private final String tenHienThi;

    LoaiLichLamViec(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static LoaiLichLamViec tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(LoaiLichLamViec.class, ma);
    }
}
