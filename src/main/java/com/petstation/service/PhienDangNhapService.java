package com.petstation.service;

import com.petstation.model.PhienDangNhap;

/** Cung cấp phiên hiện tại và các kiểm tra quyền dùng chung cho nghiệp vụ. */
public final class PhienDangNhapService {
    /** Khởi tạo PhienDangNhapService với các giá trị được cung cấp. */
    private PhienDangNhapService() {}

    /** Phiên đăng nhập. */
    private static volatile PhienDangNhap phienDangNhap;

    /**
     * Đặt phiên đăng nhập trong PhienDangNhapService.
     *
     * @param giaTri giá trị
     */
    public static void datPhienDangNhap(PhienDangNhap giaTri) {
        phienDangNhap = giaTri;
    }

    /**
     * Lấy phiên hiện tại trong PhienDangNhapService.
     *
     * @return kết quả lấy phiên hiện tại
     */
    public static PhienDangNhap layPhienHienTai() {
        return phienDangNhap;
    }

    /**
     * Lấy phiên trong PhienDangNhapService.
     *
     * @return kết quả lấy phiên
     */
    public static PhienDangNhap layPhien() {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (phienDangNhap == null)
            throw new IllegalStateException("Bạn cần đăng nhập để thực hiện thao tác này.");
        return phienDangNhap;
    }

    /**
     * Lấy id tài khoản trong PhienDangNhapService.
     *
     * @return kết quả lấy id tài khoản
     */
    public static long layIdTaiKhoan() {
        return layPhien().taiKhoan().id();
    }

    /**
     * Lấy id nhân viên trong PhienDangNhapService.
     *
     * @return kết quả lấy id nhân viên
     */
    public static long layIdNhanVien() {
        return layPhien().nhanVien().id();
    }

    /**
     * Yêu cầu trong PhienDangNhapService.
     *
     * @param quyen quyền
     */
    public static void yeuCau(com.petstation.model.Quyen quyen) {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (!layPhien().kiemTraCo(quyen))
            throw new com.petstation.service.PhanQuyenException(
                    "Bạn không có quyền thực hiện thao tác này.");
    }
}
