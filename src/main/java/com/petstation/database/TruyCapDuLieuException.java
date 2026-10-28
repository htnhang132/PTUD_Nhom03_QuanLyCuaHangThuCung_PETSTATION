package com.petstation.database;

/** Thông báo lỗi truy cập SQL Server và giữ lại ngoại lệ gốc. */
public class TruyCapDuLieuException extends RuntimeException {
    public TruyCapDuLieuException(String thongBao) {
        super(thongBao);
    }

    public TruyCapDuLieuException(String thongBao, Throwable nguyenNhan) {
        super(thongBao, nguyenNhan);
    }
}
