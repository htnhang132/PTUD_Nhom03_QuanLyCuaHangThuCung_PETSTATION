package com.petstation.service;

/** Thông báo thao tác bị từ chối do phiên đăng nhập hoặc quyền truy cập không hợp lệ. */
public class PhanQuyenException extends RuntimeException {
    public PhanQuyenException(String thongBao) {
        super(thongBao);
    }
}
