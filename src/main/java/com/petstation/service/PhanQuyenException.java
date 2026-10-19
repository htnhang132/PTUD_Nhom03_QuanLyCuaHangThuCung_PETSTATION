package com.petstation.service;

/** Thông báo thao tác bị từ chối do phiên đăng nhập hoặc quyền truy cập không hợp lệ. */
public class PhanQuyenException extends RuntimeException {
    /**
     * Khởi tạo PhanQuyenException với các giá trị được cung cấp.
     *
     * @param thongBao thông báo
     */
    public PhanQuyenException(String thongBao) {
        super(thongBao);
    }
}
