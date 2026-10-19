package com.petstation.service;

/** Thông báo đăng nhập hoặc xác minh tài khoản thất bại. */
public class XacThucException extends RuntimeException {
    /**
     * Khởi tạo XacThucException với các giá trị được cung cấp.
     *
     * @param thongBao thông báo
     */
    public XacThucException(String thongBao) {
        super(thongBao);
    }
}
