package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.TaiKhoanDAO;
import com.petstation.dao.impl.TaiKhoanDAOImpl;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho tài khoản dịch vụ. */
public final class TaiKhoanService {
    private static final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAOImpl();

    private TaiKhoanService() {}

    public static void layThayDoiMatKhau(String hienTai, String tiepTheo) {
        if (hienTai == null || hienTai.isEmpty())
            throw new IllegalArgumentException("Cần nhập mật khẩu hiện tại.");
        if (tiepTheo == null || tiepTheo.length() < 8)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        if (hienTai.equals(tiepTheo))
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu hiện tại.");
        com.petstation.database.ThaoTacSQL.thucHienGiaoDich(ketNoi -> {
            String tenDangNhap = PhienDangNhapService.layTenDangNhap();
            Object giaTri = taiKhoanDAO.layMaBamMatKhau(tenDangNhap);
            if (giaTri == null)
                throw new IllegalStateException("Tài khoản không còn tồn tại.");
            String maBam = String.valueOf(giaTri);
            if (!org.mindrot.jbcrypt.BCrypt.checkpw(hienTai, maBam))
                throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
            int soDong = taiKhoanDAO.capNhatMatKhau(
                    org.mindrot.jbcrypt.BCrypt.hashpw(
                            tiepTheo, org.mindrot.jbcrypt.BCrypt.gensalt()), tenDangNhap);
            if (soDong != 1)
                throw new IllegalStateException("Không cập nhật được mật khẩu.");
            NhatKyHeThongService.ghiNhatKy("Đổi mật khẩu", "Tài khoản hiện tại");
            return null;
        });
    }
}
