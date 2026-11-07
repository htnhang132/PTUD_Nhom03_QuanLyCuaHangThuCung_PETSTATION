package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.NhanVienDAO;
import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.dao.TaiKhoanDAO;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.NhanVien;
import com.petstation.model.NhatKyHeThong;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;
import com.petstation.model.TrangThaiTaiKhoan;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;

public final class XacThucService {
    private static final int LON_NHAT_THAT_BAI_SO_LAN_THU = 5;

    private static final int KHOA_SO_PHUT = 15;

    private final QuanLyGiaoDich cacGiaoDich;

    private final TaiKhoanDAO danhSachTaiKhoan;

    private final NhanVienDAO cacNhanVien;

    private final NhatKyHeThongDAO nhatKyHeThongCacNhatKy;

    public XacThucService(
            QuanLyGiaoDich cacGiaoDich,
            TaiKhoanDAO danhSachTaiKhoan,
            NhanVienDAO cacNhanVien,
            NhatKyHeThongDAO nhatKyHeThongCacNhatKy) {
        this.cacGiaoDich = cacGiaoDich;
        this.danhSachTaiKhoan = danhSachTaiKhoan;
        this.cacNhanVien = cacNhanVien;
        this.nhatKyHeThongCacNhatKy = nhatKyHeThongCacNhatKy;
    }

    public PhienDangNhap dangNhap(String chuaXuLyTenDangNhap, char[] matKhau) {
        String tenDangNhap = chuaXuLyTenDangNhap == null ? "" : chuaXuLyTenDangNhap.trim();
        if (tenDangNhap.isEmpty()) {
            throw new XacThucException("Vui lòng nhập tên đăng nhập.");
        }
        if (matKhau == null || matKhau.length == 0) {
            throw new XacThucException("Vui lòng nhập mật khẩu.");
        }

        try {
            KetQuaDangNhap ketQua =
                    cacGiaoDich.thucThi(
                            ketNoi -> {
                                TaiKhoan taiKhoan =
                                        danhSachTaiKhoan
                                                .timTheoTenDangNhap(ketNoi, tenDangNhap)
                                                .orElse(null);
                                if (taiKhoan == null) {
                                    return KetQuaDangNhap.taoLoi(
                                            "Tên đăng nhập hoặc mật khẩu không đúng.");
                                }

                                LocalDateTime hienTai = LocalDateTime.now();

                                if (taiKhoan.trangThai() != TrangThaiTaiKhoan.HOAT_DONG) {
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.tenDangNhap(),
                                            "TU_CHOI_DANG_NHAP",
                                            "Tài khoản không hoạt động");
                                    return KetQuaDangNhap.taoLoi(
                                            "Tài khoản đã bị khóa. Vui lòng liên hệ Cửa hàng"
                                                    + " trưởng.");
                                }
                                if (taiKhoan.layTamThoiDaKhoa(hienTai)) {
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.tenDangNhap(),
                                            "TU_CHOI_DANG_NHAP",
                                            "Tài khoản đang tạm khóa");
                                    return KetQuaDangNhap.taoLoi(
                                            "Tài khoản bị tạm khóa đến "
                                                    + taiKhoan.khoaDenLuc()
                                                    + ".");
                                }

                                String duocCungCapMatKhau = new String(matKhau);
                                if (!BCrypt.checkpw(duocCungCapMatKhau, taiKhoan.maBamMatKhau())) {
                                    int soLuongThatBai = taiKhoan.soLanDangNhapSai() + 1;

                                    LocalDateTime khoaDenLuc =
                                            soLuongThatBai >= LON_NHAT_THAT_BAI_SO_LAN_THU
                                                    ? hienTai.plusMinutes(KHOA_SO_PHUT)
                                                    : null;
                                    danhSachTaiKhoan.ghiNhanDangNhapThatBai(
                                            ketNoi, taiKhoan.tenDangNhap(), soLuongThatBai, khoaDenLuc);
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.tenDangNhap(),
                                            "DANG_NHAP_THAT_BAI",
                                            "Sai mật khẩu lần " + soLuongThatBai);
                                    if (khoaDenLuc != null) {
                                        return KetQuaDangNhap.taoLoi(
                                                "Tài khoản bị tạm khóa. Vui lòng liên hệ Cửa hàng"
                                                        + " trưởng.");
                                    }
                                    return KetQuaDangNhap.taoLoi(
                                            "Tên đăng nhập hoặc mật khẩu không đúng. Bạn còn "
                                                    + (LON_NHAT_THAT_BAI_SO_LAN_THU
                                                            - soLuongThatBai)
                                                    + " lần thử.");
                                }

                                NhanVien nhanVien =
                                        cacNhanVien
                                                .timTheoId(ketNoi, taiKhoan.getMaNhanVien())
                                                .filter(NhanVien::hoatDong)
                                                .orElse(null);
                                if (nhanVien == null) {
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.tenDangNhap(),
                                            "TU_CHOI_DANG_NHAP",
                                            "Nhân viên đã ngừng làm việc");
                                    return KetQuaDangNhap.taoLoi("Nhân viên đã ngừng làm việc.");
                                }
                                danhSachTaiKhoan.ghiNhanDangNhapThanhCong(ketNoi, taiKhoan.tenDangNhap());

                                Set<Quyen> cacQuyen =
                                        danhSachTaiKhoan.timCacQuyen(ketNoi, taiKhoan.tenDangNhap());
                                ghiNhatKyHeThong(
                                        ketNoi,
                                        taiKhoan.tenDangNhap(),
                                        "DANG_NHAP_THANH_CONG",
                                        "Đăng nhập thành công");
                                return KetQuaDangNhap.hienThiThanhCong(
                                        new PhienDangNhap(taiKhoan, nhanVien, cacQuyen));
                            });
            if (ketQua.loiThongBao() != null) {
                throw new XacThucException(ketQua.loiThongBao());
            }
            return ketQua.phienDangNhap();
        } finally {
            java.util.Arrays.fill(matKhau, '\0');
        }
    }

    private void ghiNhatKyHeThong(
            java.sql.Connection ketNoi, String tenDangNhap, String thaoTac, String ghiChu)
            throws Exception {
        nhatKyHeThongCacNhatKy.them(
                ketNoi,
                new NhatKyHeThong(
                        "SYS" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 27),
                        new TaiKhoan(tenDangNhap),
                        thaoTac,
                        "TAI_KHOAN",
                        tenDangNhap,
                        null,
                        null,
                        ghiChu,
                        LocalDateTime.now()));
    }

    private record KetQuaDangNhap(PhienDangNhap phienDangNhap, String loiThongBao) {
        static KetQuaDangNhap hienThiThanhCong(PhienDangNhap phienDangNhap) {
            return new KetQuaDangNhap(phienDangNhap, null);
        }

        static KetQuaDangNhap taoLoi(String thongBao) {
            return new KetQuaDangNhap(null, thongBao);
        }
    }
}
