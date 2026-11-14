package com.petstation.service.hethong;

import com.petstation.dao.hethong.NhatKyHeThongDAO;
import com.petstation.dao.hethong.TaiKhoanDAO;
import com.petstation.dao.nhansu.NhanVienDAO;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.PhienDangNhap;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.hethong.NhatKyHeThong;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

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
            throw new SecurityException("Vui lòng nhập tên đăng nhập.");
        }
        if (matKhau == null || matKhau.length == 0) {
            throw new SecurityException("Vui lòng nhập mật khẩu.");
        }
        try {
            KetQuaDangNhap ketQua =
                    cacGiaoDich.thucThi(
                            ketNoi -> xacThucTrongGiaoDich(ketNoi, tenDangNhap, matKhau));
            if (ketQua.loiThongBao != null) {
                throw new SecurityException(ketQua.loiThongBao);
            }
            return ketQua.phienDangNhap;
        } finally {
            Arrays.fill(matKhau, '\0');
        }
    }

    private KetQuaDangNhap xacThucTrongGiaoDich(
            Connection ketNoi, String tenDangNhap, char[] matKhau) throws Exception {
        TaiKhoan taiKhoan = danhSachTaiKhoan.timTheoTenDangNhap(ketNoi, tenDangNhap).orElse(null);
        if (taiKhoan == null) {
            return KetQuaDangNhap.taoLoi("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        LocalDateTime hienTai = LocalDateTime.now();
        KetQuaDangNhap loiTrangThai = kiemTraTrangThaiTaiKhoan(ketNoi, taiKhoan, hienTai);
        if (loiTrangThai != null) {
            return loiTrangThai;
        }
        // jbcrypt 0.4 chỉ cung cấp BCrypt.checkpw(String, String) nên cần chuyển char[] tạm sang
        // String.
        if (!BCrypt.checkpw(new String(matKhau), taiKhoan.getMaBamMatKhau())) {
            return xuLySaiMatKhau(ketNoi, taiKhoan, hienTai);
        }
        NhanVien nhanVien =
                cacNhanVien
                        .timTheoId(ketNoi, taiKhoan.getMaNhanVien())
                        .filter(NhanVien::isDangLamViec)
                        .orElse(null);
        if (nhanVien == null) {
            ghiNhatKyHeThong(
                    ketNoi, taiKhoan, null, "TU_CHOI_DANG_NHAP", "Nhân viên đã ngừng làm việc");
            return KetQuaDangNhap.taoLoi("Nhân viên đã ngừng làm việc.");
        }
        danhSachTaiKhoan.ghiNhanDangNhapThanhCong(ketNoi, taiKhoan.getTenDangNhap());
        Set<Quyen> cacQuyen = danhSachTaiKhoan.timCacQuyen(ketNoi, taiKhoan.getTenDangNhap());
        ghiNhatKyHeThong(
                ketNoi, taiKhoan, nhanVien, "DANG_NHAP_THANH_CONG", "Đăng nhập thành công");
        return KetQuaDangNhap.hienThiThanhCong(new PhienDangNhap(taiKhoan, nhanVien, cacQuyen));
    }

    private KetQuaDangNhap kiemTraTrangThaiTaiKhoan(
            Connection ketNoi, TaiKhoan taiKhoan, LocalDateTime hienTai) throws Exception {
        if (taiKhoan.getTrangThai() != TrangThaiTaiKhoan.HOAT_DONG) {
            ghiNhatKyHeThong(
                    ketNoi, taiKhoan, null, "TU_CHOI_DANG_NHAP", "Tài khoản không hoạt động");
            return KetQuaDangNhap.taoLoi("Tài khoản đã bị khóa. Vui lòng liên hệ Cửa hàng trưởng.");
        }
        if (taiKhoan.isTamThoiDaKhoa(hienTai)) {
            ghiNhatKyHeThong(
                    ketNoi, taiKhoan, null, "TU_CHOI_DANG_NHAP", "Tài khoản đang tạm khóa");
            return KetQuaDangNhap.taoLoi(
                    "Tài khoản bị tạm khóa đến " + taiKhoan.getKhoaDenLuc() + ".");
        }
        return null;
    }

    private KetQuaDangNhap xuLySaiMatKhau(
            Connection ketNoi, TaiKhoan taiKhoan, LocalDateTime hienTai) throws Exception {
        int soLuongThatBai = taiKhoan.getSoLanDangNhapSai() + 1;
        LocalDateTime khoaDenLuc =
                soLuongThatBai >= LON_NHAT_THAT_BAI_SO_LAN_THU
                        ? hienTai.plusMinutes(KHOA_SO_PHUT)
                        : null;
        danhSachTaiKhoan.ghiNhanDangNhapThatBai(
                ketNoi, taiKhoan.getTenDangNhap(), soLuongThatBai, khoaDenLuc);
        ghiNhatKyHeThong(
                ketNoi, taiKhoan, null, "DANG_NHAP_THAT_BAI", "Sai mật khẩu lần " + soLuongThatBai);
        if (khoaDenLuc != null) {
            return KetQuaDangNhap.taoLoi(
                    "Tài khoản bị tạm khóa. Vui lòng liên hệ Cửa hàng trưởng.");
        }
        return KetQuaDangNhap.taoLoi("Tên đăng nhập hoặc mật khẩu không đúng.");
    }

    private void ghiNhatKyHeThong(
            Connection ketNoi, TaiKhoan taiKhoan, NhanVien nhanVien, String thaoTac, String ghiChu)
            throws Exception {
        NhanVien nv = nhanVien;
        if (nv == null && taiKhoan != null && taiKhoan.getMaNhanVien() != null) {
            nv = new NhanVien();
            nv.setMaNhanVien(taiKhoan.getMaNhanVien());
        }
        nhatKyHeThongCacNhatKy.them(
                ketNoi,
                new NhatKyHeThong(
                        "SYS" + UUID.randomUUID().toString().replace("-", "").substring(0, 27),
                        nv,
                        thaoTac,
                        "TAI_KHOAN",
                        taiKhoan != null ? taiKhoan.getTenDangNhap() : null,
                        ghiChu,
                        "AUTH",
                        "DANG_NHAP_THANH_CONG".equals(thaoTac) ? "Thành công" : "Thất bại",
                        LocalDateTime.now()));
    }

    private static final class KetQuaDangNhap {
        final PhienDangNhap phienDangNhap;
        final String loiThongBao;

        private KetQuaDangNhap(PhienDangNhap phienDangNhap, String loiThongBao) {
            this.phienDangNhap = phienDangNhap;
            this.loiThongBao = loiThongBao;
        }

        static KetQuaDangNhap hienThiThanhCong(PhienDangNhap phienDangNhap) {
            return new KetQuaDangNhap(phienDangNhap, null);
        }

        static KetQuaDangNhap taoLoi(String thongBao) {
            return new KetQuaDangNhap(null, thongBao);
        }
    }
}
