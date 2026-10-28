package com.petstation.service;

/** Thông báo đăng nhập hoặc xác minh tài khoản thất bại. */
public class XacThucException extends RuntimeException {
    public XacThucException(String thongBao) {
        super(thongBao);
    }
}
