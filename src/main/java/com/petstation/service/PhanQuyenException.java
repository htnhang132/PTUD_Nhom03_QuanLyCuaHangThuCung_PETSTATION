package com.petstation.service;

public class PhanQuyenException extends RuntimeException {
    public PhanQuyenException(String thongBao) {
        super(thongBao);
    }
}
