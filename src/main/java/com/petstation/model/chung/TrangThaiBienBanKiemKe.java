package com.petstation.model.chung;

/**
 * Trạng thái của biên bản kiểm kê kho. Mapping tương ứng cột trang_thai trong bảng
 * bien_ban_kiem_ke: 'DANG_KIEM', 'CHO_DUYET', 'DA_DUYET', 'TU_CHOI'
 */
public enum TrangThaiBienBanKiemKe implements MaDBEnum {
    DANG_KIEM("DANG_KIEM", "Đang kiểm đếm"),
    CHO_DUYET("CHO_DUYET", "Chờ quản lý duyệt"),
    DA_DUYET("DA_DUYET", "Đã duyệt điều chỉnh"),
    TU_CHOI("TU_CHOI", "Bị từ chối");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiBienBanKiemKe(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public boolean coTheDuyet() {
        return this == CHO_DUYET;
    }

    public static TrangThaiBienBanKiemKe tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiBienBanKiemKe.class, ma);
    }
}
