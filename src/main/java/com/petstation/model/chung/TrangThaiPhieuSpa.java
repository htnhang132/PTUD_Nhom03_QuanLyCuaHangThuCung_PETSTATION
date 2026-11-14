package com.petstation.model.chung;

/**
 * Trạng thái của phiếu tiếp nhận dịch vụ Spa thú cưng. Mapping tương ứng cột trang_thai trong bảng
 * phieu_tiep_nhan_spa: 'TIEP_NHAN', 'DANG_THUC_HIEN', 'HOAN_TAT', 'DA_THANH_TOAN', 'DA_HUY'
 */
public enum TrangThaiPhieuSpa {
    TIEP_NHAN("TIEP_NHAN", "Tiếp nhận"),
    DANG_THUC_HIEN("DANG_THUC_HIEN", "Đang chăm sóc"),
    HOAN_TAT("HOAN_TAT", "Hoàn tất chăm sóc"),
    DA_THANH_TOAN("DA_THANH_TOAN", "Đã thanh toán"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPhieuSpa(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiPhieuSpa tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiPhieuSpa t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiPhieuSpa với mã: " + ma);
    }
}
