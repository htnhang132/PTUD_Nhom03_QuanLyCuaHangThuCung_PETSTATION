package com.petstation.model.chung;

/**
 * Loại mục đích xuất kho. Mapping tương ứng cột loai_xuat trong bảng phieu_xuat_kho: 'XUAT_HUY',
 * 'THANH_LY', 'TRA_NHA_CUNG_CAP', 'SU_DUNG_NOI_BO'
 */
public enum LoaiPhieuXuat {
    XUAT_HUY("XUAT_HUY", "Xuất hủy hàng hỏng/hết hạn"),
    THANH_LY("THANH_LY", "Xuất thanh lý"),
    TRA_NHA_CUNG_CAP("TRA_NHA_CUNG_CAP", "Trả hàng nhà cung cấp"),
    SU_DUNG_NOI_BO("SU_DUNG_NOI_BO", "Sử dụng nội bộ (Spa/Hotel)");

    private final String maDB;
    private final String tenHienThi;

    LoaiPhieuXuat(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static LoaiPhieuXuat tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (LoaiPhieuXuat l : values()) {
            if (l.maDB.equalsIgnoreCase(ma.trim())) {
                return l;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy LoaiPhieuXuat với mã: " + ma);
    }
}
