package com.petstation.model;

/** Nháp đổi phụ kiện; cho phép chưa chọn hàng gốc hoặc hàng thay thế. */
public record DonDoiTreo(
        String timHoaDon, long dongGoc, String maThayThe, String timHang,
        int soLuong, String lyDo, boolean nguyenTem, boolean chuaDung) implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public DonDoiTreo {
        timHoaDon = timHoaDon == null ? "" : timHoaDon.trim();
        maThayThe = maThayThe == null ? "" : maThayThe.trim();
        timHang = timHang == null ? "" : timHang.trim();
        lyDo = lyDo == null ? "" : lyDo.trim();
        if (timHoaDon.isBlank())
            throw new IllegalArgumentException("Nhập mã hóa đơn hoặc SĐT trước khi treo đơn đổi.");
        if (dongGoc < 0 || soLuong <= 0)
            throw new IllegalArgumentException("Thông tin đơn đổi không hợp lệ.");
    }
}
