package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.TaiKhoanDAO;
import com.petstation.dao.impl.TaiKhoanDAOImpl;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho tài khoản dịch vụ. */
public final class TaiKhoanService {
    private static final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAOImpl();

    private TaiKhoanService() {}

    public static void layThayDoiMatKhau(String hienTai, String tiepTheo) {
        if (hienTai == null || tiepTheo == null || tiepTheo.length() < 8)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        com.petstation.database.ThaoTacSQL.thucHienGiaoDich(
                ketNoi -> {
                    // Tài khoản.
                    long taiKhoan = PhienDangNhapService.layIdTaiKhoan();
                    // Mã băm.
                    String maBam =
                            com.petstation.util.DuLieu.chuyenThanhChuoi(
                                    taiKhoanDAO.layMaBamMatKhau(taiKhoan));
                    if (!org.mindrot.jbcrypt.BCrypt.checkpw(hienTai, maBam))
                        throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
                    taiKhoanDAO.capNhatMatKhau(
                            org.mindrot.jbcrypt.BCrypt.hashpw(
                                    tiepTheo, org.mindrot.jbcrypt.BCrypt.gensalt()),
                            taiKhoan);
                    NhatKyHeThongService.ghiNhatKy("Đổi mật khẩu", "Tài khoản hiện tại");
                    return null;
                });
    }
}
