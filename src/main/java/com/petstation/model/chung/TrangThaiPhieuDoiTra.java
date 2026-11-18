package com.petstation.model.chung;

/**
 * Trạng thái của phiếu đổi / trả hàng. Mapping tương ứng cột trang_thai trong bảng phieu_doi_tra:
 * 'CHO_XU_LY', 'HOAN_TAT', 'TU_CHOI', 'DA_HUY'
 */
public enum TrangThaiPhieuDoiTra implements MaDBEnum {
    CHO_XU_LY("CHO_XU_LY", "Chờ xử lý"),
    HOAN_TAT("HOAN_TAT", "Hoàn tất"),
    TU_CHOI("TU_CHOI", "Từ chối"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPhieuDoiTra(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiPhieuDoiTra tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(TrangThaiPhieuDoiTra.class, ma);
    }
}
