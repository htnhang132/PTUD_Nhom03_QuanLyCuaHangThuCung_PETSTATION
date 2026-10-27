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

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho xác thực dịch vụ. */
public final class XacThucService {
    /** Lớn nhất thất bại số lần thử. */
    private static final int LON_NHAT_THAT_BAI_SO_LAN_THU = 5;

    /** Khóa số phút. */
    private static final int KHOA_SO_PHUT = 15;

    /** Các giao dịch. */
    private final QuanLyGiaoDich cacGiaoDich;

    /** Danh sách tài khoản. */
    private final TaiKhoanDAO danhSachTaiKhoan;

    /** Các nhân viên. */
    private final NhanVienDAO cacNhanVien;

    /** Nhật ký hệ thống các nhật ký. */
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
        // Tên đăng nhập.
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
                                // Tài khoản.
                                TaiKhoan taiKhoan =
                                        danhSachTaiKhoan
                                                .timTheoTenDangNhap(ketNoi, tenDangNhap)
                                                .orElse(null);
                                if (taiKhoan == null) {
                                    return KetQuaDangNhap.taoLoi(
                                            "Tên đăng nhập hoặc mật khẩu không đúng.");
                                }
                                // Hiện tại.
                                LocalDateTime hienTai = LocalDateTime.now();

                                if (taiKhoan.trangThai() != TrangThaiTaiKhoan.ACTIVE) {
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.id(),
                                            "LOGIN_REJECTED",
                                            "Tài khoản không hoạt động");
                                    return KetQuaDangNhap.taoLoi(
                                            "Tài khoản đã bị khóa. Vui lòng liên hệ Cửa hàng"
                                                    + " trưởng.");
                                }
                                if (taiKhoan.layTamThoiDaKhoa(hienTai)) {
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.id(),
                                            "LOGIN_REJECTED",
                                            "Tài khoản đang tạm khóa");
                                    return KetQuaDangNhap.taoLoi(
                                            "Tài khoản bị tạm khóa đến "
                                                    + taiKhoan.khoaDenLuc()
                                                    + ".");
                                }

                                // Được cung cấp mật khẩu.
                                String duocCungCapMatKhau = new String(matKhau);
                                if (!BCrypt.checkpw(duocCungCapMatKhau, taiKhoan.maBamMatKhau())) {
                                    // Số lượng thất bại.
                                    int soLuongThatBai = taiKhoan.soLanDangNhapSai() + 1;
                                    // Khóa đến lúc.
                                    LocalDateTime khoaDenLuc =
                                            soLuongThatBai >= LON_NHAT_THAT_BAI_SO_LAN_THU
                                                    ? hienTai.plusMinutes(KHOA_SO_PHUT)
                                                    : null;
                                    danhSachTaiKhoan.ghiNhanDangNhapThatBai(
                                            ketNoi, taiKhoan.id(), soLuongThatBai, khoaDenLuc);
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.id(),
                                            "LOGIN_FAILED",
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
                                                    + "lần thử.");
                                }

                                // Nhân viên.
                                NhanVien nhanVien =
                                        cacNhanVien
                                                .timTheoId(ketNoi, taiKhoan.idNhanVien())
                                                .filter(NhanVien::hoatDong)
                                                .orElse(null);
                                if (nhanVien == null) {
                                    ghiNhatKyHeThong(
                                            ketNoi,
                                            taiKhoan.id(),
                                            "LOGIN_REJECTED",
                                            "Nhân viên đã ngừng làm việc");
                                    return KetQuaDangNhap.taoLoi("Nhân viên đã ngừng làm việc.");
                                }
                                danhSachTaiKhoan.ghiNhanDangNhapThanhCong(ketNoi, taiKhoan.id());
                                // Các quyền.
                                Set<Quyen> cacQuyen =
                                        danhSachTaiKhoan.timCacQuyen(ketNoi, taiKhoan.id());
                                ghiNhatKyHeThong(
                                        ketNoi,
                                        taiKhoan.id(),
                                        "LOGIN_SUCCESS",
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
            java.sql.Connection ketNoi, Long idTaiKhoan, String thaoTac, String ghiChu)
            throws Exception {
        nhatKyHeThongCacNhatKy.them(
                ketNoi,
                new NhatKyHeThong(
                        0,
                        idTaiKhoan,
                        thaoTac,
                        "TAI_KHOAN",
                        idTaiKhoan == null ? null : idTaiKhoan.toString(),
                        null,
                        null,
                        ghiChu,
                        LocalDateTime.now()));
    }

    /**
     * Điều phối nghiệp vụ và kiểm tra dữ liệu cho đăng nhập kết quả.
     *
     * @param phienDangNhap phiên đăng nhập
     * @param loiThongBao lỗi thông báo
     */
    private record KetQuaDangNhap(PhienDangNhap phienDangNhap, String loiThongBao) {
        static KetQuaDangNhap hienThiThanhCong(PhienDangNhap phienDangNhap) {
            return new KetQuaDangNhap(phienDangNhap, null);
        }

        static KetQuaDangNhap taoLoi(String thongBao) {
            return new KetQuaDangNhap(null, thongBao);
        }
    }
}
