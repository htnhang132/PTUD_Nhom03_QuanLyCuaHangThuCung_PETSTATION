package com.petstation.model;

import java.util.Set;

/**
 * Biểu diễn dữ liệu phiên đăng nhập dùng trong nghiệp vụ cửa hàng.
 *
 * @param taiKhoan tài khoản
 * @param nhanVien nhân viên
 * @param cacQuyen các quyền
 */
public record PhienDangNhap(TaiKhoan taiKhoan, NhanVien nhanVien, Set<Quyen> cacQuyen) {
    /**
     * Đọc vai trò trong PhienDangNhap.
     *
     * @return kết quả đọc vai trò
     */
    public VaiTro docVaiTro() {
        return taiKhoan.vaiTro();
    }

    /**
     * Lấy tên hiển thị trong PhienDangNhap.
     *
     * @return kết quả lấy tên hiển thị
     */
    public String layTenHienThi() {
        return nhanVien.hoTen();
    }

    /**
     * Kiểm tra có trong PhienDangNhap.
     *
     * @param quyen quyền
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public boolean kiemTraCo(Quyen quyen) {
        return cacQuyen.contains(quyen);
    }
}
