package com.petstation.model;

import java.time.LocalDateTime;

/**
 * Biểu diễn dữ liệu ánh xạ với bảng tai_khoan trong SQL Server.
 *
 * @param id id
 * @param idNhanVien id nhân viên
 * @param tenDangNhap tên đăng nhập
 * @param maBamMatKhau mã băm mật khẩu
 * @param vaiTro vai trò
 * @param trangThai trạng thái
 * @param soLanDangNhapSai số lần đăng nhập sai
 * @param khoaDenLuc khóa đến lúc
 * @param batBuocDoiMatKhau bắt buộc đổi mật khẩu
 */
public record TaiKhoan(
        long id,
        long idNhanVien,
        String tenDangNhap,
        String maBamMatKhau,
        VaiTro vaiTro,
        TrangThaiTaiKhoan trangThai,
        int soLanDangNhapSai,
        LocalDateTime khoaDenLuc,
        boolean batBuocDoiMatKhau) {
    public boolean layTamThoiDaKhoa(LocalDateTime hienTai) {
        return khoaDenLuc != null && khoaDenLuc.isAfter(hienTai);
    }
}
