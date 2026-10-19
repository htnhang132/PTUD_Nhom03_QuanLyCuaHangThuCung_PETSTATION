package com.petstation.dao;

/** Thông báo lỗi truy cập SQL Server và giữ lại ngoại lệ gốc. */
public class TruyCapDuLieuException extends RuntimeException {
    /**
     * Khởi tạo TruyCapDuLieuException với các giá trị được cung cấp.
     *
     * @param thongBao thông báo
     */
    public TruyCapDuLieuException(String thongBao) {
        super(thongBao);
    }

    /**
     * Khởi tạo TruyCapDuLieuException với các giá trị được cung cấp.
     *
     * @param thongBao thông báo
     * @param nguyenNhan nguyên nhân
     */
    public TruyCapDuLieuException(String thongBao, Throwable nguyenNhan) {
        super(thongBao, nguyenNhan);
    }
}
