package com.petstation.model.chung;

/**
 * Trạng thái của yêu cầu phê duyệt gửi Cửa hàng trưởng. Mapping tương ứng cột trang_thai trong bảng
 * yeu_cau_phe_duyet: 'CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'CAN_THEM_THONG_TIN', 'DA_HUY'
 */
public enum TrangThaiPheDuyet {
    CHO_DUYET("CHO_DUYET", "Chờ phê duyệt"),
    DA_DUYET("DA_DUYET", "Đã duyệt chấp thuận"),
    TU_CHOI("TU_CHOI", "Từ chối yêu cầu"),
    CAN_THEM_THONG_TIN("CAN_THEM_THONG_TIN", "Cần thêm thông tin"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiPheDuyet(String maDB, String tenHienThi) {
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
        return this == CHO_DUYET || this == CAN_THEM_THONG_TIN;
    }

    public static TrangThaiPheDuyet tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiPheDuyet t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiPheDuyet với mã: " + ma);
    }
}
