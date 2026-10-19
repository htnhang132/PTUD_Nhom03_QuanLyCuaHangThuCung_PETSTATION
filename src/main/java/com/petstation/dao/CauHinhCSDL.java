package com.petstation.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Đọc cấu hình SQL Server từ tài nguyên và các biến môi trường được hỗ trợ.
 *
 * @param url URL
 * @param nguoiDung người dùng
 * @param matKhau mật khẩu
 * @param thoiGianChoDangNhapGiay thời gian chờ đăng nhập giây
 */
public record CauHinhCSDL(
        String url, String nguoiDung, String matKhau, int thoiGianChoDangNhapGiay) {
    /**
     * Nạp trong CauHinhCSDL.
     *
     * @return kết quả nạp
     */
    public static CauHinhCSDL nap() {
        // Các thuộc tính.
        Properties cacThuocTinh = new Properties();
        try (InputStream dauVao = CauHinhCSDL.class.getResourceAsStream("/db.properties")) {
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (dauVao != null) {
                cacThuocTinh.load(dauVao);
            }
        } catch (IOException ngoaiLe) {
            throw new IllegalStateException("Không thể đọc cấu hình cơ sở dữ liệu.", ngoaiLe);
        }

        // URL.
        String url = layGiaTri("PETSTATION_DB_URL", cacThuocTinh, "db.url", "");
        // Người dùng.
        String nguoiDung = layGiaTri("PETSTATION_DB_USER", cacThuocTinh, "db.user", "");
        // Mật khẩu.
        String matKhau = layGiaTri("PETSTATION_DB_PASSWORD", cacThuocTinh, "db.password", "");
        // Thời gian chờ.
        int thoiGianCho =
                Integer.parseInt(
                        layGiaTri(
                                "PETSTATION_DB_LOGIN_TIMEOUT_SECONDS",
                                cacThuocTinh,
                                "db.loginTimeoutSeconds",
                                "10"));

        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (url.isBlank() || nguoiDung.isBlank()) {
            throw new IllegalStateException(
                    "Chưa cấu hình SQL Server. Hãy đặt PETSTATION_DB_URL, PETSTATION_DB_USER, "
                            + "PETSTATION_DB_PASSWORD hoặc tạo src/main/resources/db.properties.");
        }
        return new CauHinhCSDL(url, nguoiDung, matKhau, thoiGianCho);
    }

    /**
     * Lấy giá trị trong CauHinhCSDL.
     *
     * @param moiTruongKhoa môi trường khóa
     * @param cacThuocTinh các thuộc tính
     * @param thuocTinhKhoa thuộc tính khóa
     * @param thayThe thay thế
     * @return kết quả lấy giá trị
     */
    private static String layGiaTri(
            String moiTruongKhoa, Properties cacThuocTinh, String thuocTinhKhoa, String thayThe) {
        // Giá trị môi trường.
        String giaTriMoiTruong = System.getenv(moiTruongKhoa);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTriMoiTruong != null && !giaTriMoiTruong.isBlank()) {
            return giaTriMoiTruong.trim();
        }
        return cacThuocTinh.getProperty(thuocTinhKhoa, thayThe).trim();
    }
}
