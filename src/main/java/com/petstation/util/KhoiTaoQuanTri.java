package com.petstation.util;

import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;

import org.mindrot.jbcrypt.BCrypt;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Tạo tài khoản quản trị ban đầu theo cấu hình và thông tin do người vận hành cung cấp. */
public final class KhoiTaoQuanTri {
    private KhoiTaoQuanTri() {}

    public static void main(String[] cacDoiSo) throws Exception {
        // Bàn điều khiển.
        Console banDieuKhien = System.console();
        if (banDieuKhien == null) {
            System.err.println("Hãy chạy lệnh trong terminal tương tác.");
            System.exit(1);
        }

        // Tên đăng nhập.
        String tenDangNhap = banDieuKhien.readLine("Tên đăng nhập Cửa hàng trưởng: ").trim();
        // Họ tên.
        String hoTen = banDieuKhien.readLine("Họ tên: ").trim();
        // Số điện thoại.
        String soDienThoai = banDieuKhien.readLine("Số điện thoại: ").replaceAll("\\D", "");
        // Mật khẩu.
        char[] matKhau = banDieuKhien.readPassword("Mật khẩu (tối thiểu 8 ký tự): ");
        // Xác nhận.
        char[] xacNhan = banDieuKhien.readPassword("Nhập lại mật khẩu: ");

        if (tenDangNhap.isBlank() || hoTen.isBlank() || soDienThoai.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập, họ tên và SĐT không được trống.");
        }
        if (matKhau.length < 8 || !java.util.Arrays.equals(matKhau, xacNhan)) {
            throw new IllegalArgumentException("Mật khẩu không hợp lệ hoặc nhập lại không khớp.");
        }

        // Mã băm.
        String maBam = BCrypt.hashpw(new String(matKhau), BCrypt.gensalt(12));
        java.util.Arrays.fill(matKhau, '\0');
        java.util.Arrays.fill(xacNhan, '\0');

        // CSDL.
        KetNoiCSDL csdl = new KetNoiCSDL(CauHinhCSDL.nap());
        try (Connection ketNoi = csdl.mo()) {
            ketNoi.setAutoCommit(false);
            try {
                if (layQuanLyTonTai(ketNoi)) {
                    throw new IllegalStateException("Hệ thống đã có tài khoản Cửa hàng trưởng.");
                }
                // Id nhân viên.
                long idNhanVien = themNhanVien(ketNoi, hoTen, soDienThoai);
                themTaiKhoan(ketNoi, idNhanVien, tenDangNhap, maBam);
                ketNoi.commit();
                System.out.println(
                        "Đã tạo Cửa hàng trưởng. Hãy đăng nhập và đổi mật khẩu nếu cần.");
            } catch (Exception ngoaiLe) {
                ketNoi.rollback();
                throw ngoaiLe;
            }
        }
    }

    private static boolean layQuanLyTonTai(Connection ketNoi) throws Exception {
        try (PreparedStatement cauLenh =
                        ketNoi.prepareStatement(
                                "SELECT TOP 1 1 FROM tai_khoan WHERE vai_tro IN ('QUAN_LY_CUA_HANG', 'STORE_MANAGER') AND"
                                        + " trang_thai IN ('HOAT_DONG', 'ACTIVE')");
                ResultSet ketQua = cauLenh.executeQuery()) {
            return ketQua.next();
        }
    }

    private static long themNhanVien(Connection ketNoi, String hoTen, String soDienThoai)
            throws Exception {
        // Mã.
        String ma = DuLieu.taoMa("NV");
        // SQL.
        String sql =
                """
                INSERT INTO nhan_vien
                    (ma_nhan_vien, ho_ten, so_dien_thoai, chuc_vu, ngay_vao_lam)
                OUTPUT INSERTED.id
                VALUES (?, ?, ?, N'Cửa hàng trưởng', CAST(GETDATE() AS DATE))
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, ma);
            cauLenh.setString(2, hoTen);
            cauLenh.setString(3, soDienThoai);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                ketQua.next();
                return ketQua.getLong(1);
            }
        }
    }

    private static void themTaiKhoan(
            Connection ketNoi, long idNhanVien, String tenDangNhap, String maBam) throws Exception {
        // SQL.
        String sql =
                """
                INSERT INTO tai_khoan
                    (nhan_vien_id, ten_dang_nhap, mat_khau_hash, vai_tro, quyen, trang_thai, bat_buoc_doi_mat_khau)
                VALUES (?, ?, ?, 'QUAN_LY_CUA_HANG', 'XEM_TONG_QUAN,QUAN_LY_TAI_KHOAN,QUAN_LY_LICH_LAM_VIEC,XEM_LICH_LAM_VIEC,QUAN_LY_KHACH_HANG,QUAN_LY_THU_CUNG,QUAN_LY_SAN_PHAM,QUAN_LY_NHA_CUNG_CAP,NHAP_KHO,XUAT_HUY_KHO,KIEM_KE_KHO,DE_XUAT_GIA,DUYET_GIA,BAN_HANG,THANH_TOAN,QUAN_LY_CA_THU_NGAN,QUAN_LY_KHUYEN_MAI,QUAN_LY_SPA,QUAN_LY_LUU_TRU,XU_LY_PHE_DUYET,XEM_NHAT_KY,XEM_BAO_CAO_CHUNG,XEM_BAO_CAO_CA', 'HOAT_DONG', 0)
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNhanVien);
            cauLenh.setString(2, tenDangNhap);
            cauLenh.setString(3, maBam);
            cauLenh.executeUpdate();
        }
    }
}
