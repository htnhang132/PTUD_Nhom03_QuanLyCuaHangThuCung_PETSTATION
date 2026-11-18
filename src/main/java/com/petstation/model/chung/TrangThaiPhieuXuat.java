package com.petstation.model.chung;

/**
 * Trạng thái của phiếu xuất kho (xuất hủy, thanh lý, trả NCC, nội bộ). Mapping tương ứng cột
 * trang_thai trong bảng phieu_xuat_kho: 'CHO_XUAT', 'DA_XUAT', 'TU_CHOI', 'DA_HUY'
 */
public enum TrangThaiPhieuXuat implements MaDBEnum {
    CHO_XUAT("CHO_XUAT", "Chờ duyệt xuất"),
    DA_XUAT("DA_XUAT", "Đã xuất kho"),
    TU_CHOI("TU_CHOI", "Bị từ chối"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPhieuXuat(String maDB, String tenHienThi) {
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
        return this == CHO_XUAT;
    }

    public static TrangThaiPhieuXuat tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiPhieuXuat.class, ma);
    }
}
