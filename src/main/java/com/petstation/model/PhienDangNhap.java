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
    public VaiTro docVaiTro() {
        return taiKhoan.vaiTro();
    }

    public String layTenHienThi() {
        return nhanVien.hoTen();
    }

    public boolean kiemTraCo(Quyen quyen) {
        return cacQuyen.contains(quyen);
    }
}
