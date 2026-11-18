package com.petstation.model.chung;

/**
 * Loại phiếu đổi / trả hàng. Mapping tương ứng cột loai_phieu trong bảng phieu_doi_tra: 'DOI_HANG',
 * 'TRA_HANG', 'TRA_HANG_CU'
 */
public enum LoaiPhieuDoiTra implements MaDBEnum {
    DOI_HANG("DOI_HANG", "Đổi hàng"),
    TRA_HANG("TRA_HANG", "Trả hàng hoàn tiền"),
    TRA_HANG_CU("TRA_HANG_CU", "Trả hàng cũ");

    private final String maDB;
    private final String tenHienThi;

    LoaiPhieuDoiTra(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static LoaiPhieuDoiTra tuMaDB(String ma) {
        return MaDBEnum.tuMaDB(LoaiPhieuDoiTra.class, ma);
    }
}
