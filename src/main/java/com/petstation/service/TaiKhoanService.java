package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcQuanLyTaiKhoanDAO;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho tài khoản dịch vụ. */
public final class TaiKhoanService {
    /** Khởi tạo TaiKhoanService với các giá trị được cung cấp. */
    private TaiKhoanService() {}

    /**
     * Lấy thay đổi mật khẩu trong TaiKhoanService.
     *
     * @param hienTai hiện tại
     * @param tiepTheo tiếp theo
     */
    public static void layThayDoiMatKhau(String hienTai, String tiepTheo) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (hienTai == null || tiepTheo == null || tiepTheo.length() < 8)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        com.petstation.dao.ThaoTacSQL.thucHienGiaoDich(
                ketNoi -> {
                    // Tài khoản.
                    long taiKhoan = PhienDangNhapService.layIdTaiKhoan();
                    // Mã băm.
                    String maBam =
                            com.petstation.dao.ThaoTacSQL.chuyenThanhChuoi(
                                    JdbcQuanLyTaiKhoanDAO.layMaBamMatKhau(taiKhoan));
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (!org.mindrot.jbcrypt.BCrypt.checkpw(hienTai, maBam))
                        throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
                    JdbcQuanLyTaiKhoanDAO.capNhatMatKhau(
                            org.mindrot.jbcrypt.BCrypt.hashpw(
                                    tiepTheo, org.mindrot.jbcrypt.BCrypt.gensalt()),
                            taiKhoan);
                    NhatKyHeThongService.ghiNhatKy("Đổi mật khẩu", "Tài khoản hiện tại");
                    return null;
                });
    }
}
