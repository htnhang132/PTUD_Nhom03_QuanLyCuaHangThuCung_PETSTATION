package com.petstation.database;

public class TruyCapDuLieuException extends RuntimeException {
    public TruyCapDuLieuException(String thongBao) {
        super(thongBao);
    }

    public TruyCapDuLieuException(String thongBao, Throwable nguyenNhan) {
        super(thongBao, nguyenNhan);
    }
}
