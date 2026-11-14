package com.petstation.model.chung;

/**
 * Trạng thái của mã giảm giá (voucher). Mapping tương ứng cột trang_thai trong bảng voucher:
 * 'CON_HAN', 'DA_DUNG', 'HET_HAN', 'DA_HUY'
 */
public enum TrangThaiVoucher {
    CON_HAN("CON_HAN", "Còn hạn sử dụng"),
    DA_DUNG("DA_DUNG", "Đã sử dụng"),
    HET_HAN("HET_HAN", "Hết hạn"),
    DA_HUY("DA_HUY", "Đã hủy");

    private final String maDB;
    private final String tenHienThi;

    TrangThaiVoucher(String maDB, String tenHienThi) {
        this.maDB = maDB;
        this.tenHienThi = tenHienThi;
    }

    public String maDB() {
        return maDB;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    public static TrangThaiVoucher tuMaDB(String ma) {
        if (ma == null || ma.isBlank()) return null;
        for (TrangThaiVoucher t : values()) {
            if (t.maDB.equalsIgnoreCase(ma.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy TrangThaiVoucher với mã: " + ma);
    }
}
