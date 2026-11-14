package com.petstation.model.chung;

/**
 * Trạng thái của lịch hẹn làm dịch vụ Spa. Mapping tương ứng cột trang_thai trong bảng lich_hen:
 * 'DA_XAC_NHAN', 'DANG_THUC_HIEN', 'HOAN_TAT', 'DA_HUY'
 */
public enum TrangThaiLichHen {
    DA_XAC_NHAN("DA_XAC_NHAN", "Đã xác nhận hẹn"),
    DANG_THUC_HIEN("DANG_THUC_HIEN", "Đang thực hiện"),
    HOAN_TAT("HOAN_TAT", "Hoàn tất"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiLichHen(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiLichHen tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiLichHen t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiLichHen với mã: " + ma);
    }
}
