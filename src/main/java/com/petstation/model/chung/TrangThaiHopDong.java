package com.petstation.model.chung;

/**
 * Trạng thái của hợp đồng lưu trú (Pet Hotel). Mapping tương ứng cột trang_thai trong bảng
 * hop_dong_luu_tru: 'DA_DAT', 'DANG_O', 'DA_TRA_PHONG', 'DA_HUY'
 */
public enum TrangThaiHopDong {
    DA_DAT("DA_DAT", "Đã đặt trước (Đã cọc)"),
    DANG_O("DANG_O", "Đang lưu trú"),
    DA_TRA_PHONG("DA_TRA_PHONG", "Đã trả phòng"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiHopDong(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiHopDong tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiHopDong t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiHopDong với mã: " + ma);
    }
}
