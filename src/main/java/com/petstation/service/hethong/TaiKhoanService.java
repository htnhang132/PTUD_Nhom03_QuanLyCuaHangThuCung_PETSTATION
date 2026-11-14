package com.petstation.service.hethong;

import com.petstation.dao.hethong.TaiKhoanDAO;
import com.petstation.dao.hethong.impl.TaiKhoanDAOImpl;
import com.petstation.database.QuanLyGiaoDich;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public final class TaiKhoanService {
    private final TaiKhoanDAO taiKhoanDAO;
    private final NhatKyHeThongService nhatKyService;
    private final PhienDangNhapService phienService;

    public TaiKhoanService() {
        this(new TaiKhoanDAOImpl(), NhatKyHeThongService.macDinh(), PhienDangNhapService.macDinh());
    }

    public TaiKhoanService(TaiKhoanDAO taiKhoanDAO) {
        this(taiKhoanDAO, NhatKyHeThongService.macDinh(), PhienDangNhapService.macDinh());
    }

    public TaiKhoanService(
            TaiKhoanDAO taiKhoanDAO,
            NhatKyHeThongService nhatKyService,
            PhienDangNhapService phienService) {
        this.taiKhoanDAO = Objects.requireNonNull(taiKhoanDAO);
        this.nhatKyService = Objects.requireNonNull(nhatKyService);
        this.phienService = Objects.requireNonNull(phienService);
    }

    public void layThayDoiMatKhau(String hienTai, String tiepTheo) {
        if (hienTai == null || hienTai.isEmpty())
            throw new IllegalArgumentException("Cần nhập mật khẩu hiện tại.");
        if (tiepTheo == null || tiepTheo.length() < 8)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        if (hienTai.equals(tiepTheo))
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu hiện tại.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            String tenDangNhap = phienService.layTenDangNhap();
                            String maBam = taiKhoanDAO.layMaBamMatKhau(tenDangNhap);
                            if (maBam == null)
                                throw new IllegalArgumentException("Tài khoản không còn tồn tại.");
                            if (!BCrypt.checkpw(hienTai, maBam))
                                throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
                            int soDong =
                                    taiKhoanDAO.capNhatMatKhau(
                                            BCrypt.hashpw(tiepTheo, BCrypt.gensalt()), tenDangNhap);
                            if (soDong != 1)
                                throw new IllegalArgumentException("Không cập nhật được mật khẩu.");
                            nhatKyService.ghiNhatKy("Đổi mật khẩu", "Tài khoản hiện tại");
                            return null;
                        });
    }
}
