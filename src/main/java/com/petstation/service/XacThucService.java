package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.NhanVienDAO;
import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.dao.QuanLyGiaoDich;
import com.petstation.dao.TaiKhoanDAO;
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

    /**
     * Khởi tạo XacThucService với các giá trị được cung cấp.
     *
     * @param cacGiaoDich các giao dịch
     * @param danhSachTaiKhoan danh sách tài khoản
     * @param cacNhanVien các nhân viên
     * @param nhatKyHeThongCacNhatKy nhật ký hệ thống các nhật ký
     */
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

    /**
     * Xác thực thông tin và đăng nhập trong XacThucService.
     *
     * @param chuaXuLyTenDangNhap chưa xử lý tên đăng nhập
     * @param matKhau mật khẩu
     * @return kết quả lấy đăng nhập
     */
    public PhienDangNhap dangNhap(String chuaXuLyTenDangNhap, char[] matKhau) {
        // Tên đăng nhập.
        String tenDangNhap = chuaXuLyTenDangNhap == null ? "" : chuaXuLyTenDangNhap.trim();
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (tenDangNhap.isEmpty()) {
            throw new XacThucException("Vui lòng nhập tên đăng nhập.");
        }
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (matKhau == null || matKhau.length == 0) {
            throw new XacThucException("Vui lòng nhập mật khẩu.");
        }

        try {
            // Kết quả.
            KetQuaDangNhap ketQua =
                    cacGiaoDich.thucThi(
                            ketNoi -> {
                                // Tài khoản.
                                TaiKhoan taiKhoan =
                                        danhSachTaiKhoan
                                                .timTheoTenDangNhap(ketNoi, tenDangNhap)
                                                .orElse(null);
                                // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                                if (taiKhoan == null) {
                                    return KetQuaDangNhap.taoLoi(
                                            "Tên đăng nhập hoặc mật khẩu không đúng.");
                                }
                                // Hiện tại.
                                LocalDateTime hienTai = LocalDateTime.now();

                                // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
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
                                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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
                                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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
                                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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
                                // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (ketQua.loiThongBao() != null) {
                throw new XacThucException(ketQua.loiThongBao());
            }
            return ketQua.phienDangNhap();
        } finally {
            java.util.Arrays.fill(matKhau, '\0');
        }
    }

    /**
     * Ghi nhật ký hệ thống trong XacThucService.
     *
     * @param ketNoi kết nối
     * @param idTaiKhoan id tài khoản
     * @param thaoTac thao tác
     * @param ghiChu ghi chú
     * @throws Exception nếu thao tác không thể hoàn tất
     */
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
        /**
         * Hiển thị kết quả thành công trong KetQuaDangNhap.
         *
         * @param phienDangNhap phiên đăng nhập
         * @return kết quả lấy thành công
         */
        static KetQuaDangNhap hienThiThanhCong(PhienDangNhap phienDangNhap) {
            return new KetQuaDangNhap(phienDangNhap, null);
        }

        /**
         * Tạo lỗi trong KetQuaDangNhap.
         *
         * @param thongBao thông báo
         * @return kết quả tạo lỗi
         */
        static KetQuaDangNhap taoLoi(String thongBao) {
            return new KetQuaDangNhap(null, thongBao);
        }
    }
}
