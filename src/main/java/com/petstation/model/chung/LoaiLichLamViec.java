package com.petstation.model.chung;

/**
 * Loại hình lịch làm việc (đi làm bình thường hoặc nghỉ phép). Mapping tương ứng cột loai trong
 * bảng lich_lam_viec: 'DI_LAM', 'NGHI_PHEP'
 */
public enum LoaiLichLamViec {
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
        if (ma == null || ma.isBlank()) return null;
        for (LoaiLichLamViec l : values()) {
            if (l.maDB.equalsIgnoreCase(ma.trim())) {
                return l;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy LoaiLichLamViec với mã: " + ma);
    }
}
