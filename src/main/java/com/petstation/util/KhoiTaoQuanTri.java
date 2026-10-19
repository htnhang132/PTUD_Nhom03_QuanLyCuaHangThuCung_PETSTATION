package com.petstation.util;

import com.petstation.dao.CauHinhCSDL;
import com.petstation.dao.KetNoiCSDL;

import org.mindrot.jbcrypt.BCrypt;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Tạo tài khoản quản trị ban đầu theo cấu hình và thông tin do người vận hành cung cấp. */
public final class KhoiTaoQuanTri {
    /** Khởi tạo KhoiTaoQuanTri với các giá trị được cung cấp. */
    private KhoiTaoQuanTri() {}

    /**
     * Lấy chính trong KhoiTaoQuanTri.
     *
     * @param cacDoiSo các đối số
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    public static void main(String[] cacDoiSo) throws Exception {
        // Bàn điều khiển.
        Console banDieuKhien = System.console();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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

        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (tenDangNhap.isBlank() || hoTen.isBlank() || soDienThoai.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập, họ tên và SĐT không được trống.");
        }
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
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
                // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
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

    /**
     * Lấy quản lý tồn tại trong KhoiTaoQuanTri.
     *
     * @param ketNoi kết nối
     * @return kết quả kiểm tra điều kiện xử lý
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    private static boolean layQuanLyTonTai(Connection ketNoi) throws Exception {
        try (PreparedStatement cauLenh =
                        ketNoi.prepareStatement(
                                "SELECT TOP 1 1 FROM tai_khoan WHERE vai_tro = 'STORE_MANAGER' AND"
                                        + " trang_thai='ACTIVE'");
                ResultSet ketQua = cauLenh.executeQuery()) {
            return ketQua.next();
        }
    }

    /**
     * Thêm nhân viên trong KhoiTaoQuanTri.
     *
     * @param ketNoi kết nối
     * @param hoTen họ tên
     * @param soDienThoai số điện thoại
     * @return kết quả thêm nhân viên
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    private static long themNhanVien(Connection ketNoi, String hoTen, String soDienThoai)
            throws Exception {
        // Mã.
        String ma = "NV" + System.currentTimeMillis();
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

    /**
     * Thêm tài khoản trong KhoiTaoQuanTri.
     *
     * @param ketNoi kết nối
     * @param idNhanVien id nhân viên
     * @param tenDangNhap tên đăng nhập
     * @param maBam mã băm
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    private static void themTaiKhoan(
            Connection ketNoi, long idNhanVien, String tenDangNhap, String maBam) throws Exception {
        // SQL.
        String sql =
                """
                INSERT INTO tai_khoan
                    (nhan_vien_id, ten_dang_nhap, mat_khau_hash, vai_tro, bat_buoc_doi_mat_khau)
                VALUES (?, ?, ?, 'STORE_MANAGER', 0)
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNhanVien);
            cauLenh.setString(2, tenDangNhap);
            cauLenh.setString(3, maBam);
            cauLenh.executeUpdate();
        }
    }
}
