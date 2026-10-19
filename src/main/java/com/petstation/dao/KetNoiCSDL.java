package com.petstation.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Mở và kiểm tra kết nối JDBC theo cấu hình SQL Server. */
public final class KetNoiCSDL {
    /** Cấu hình. */
    private final CauHinhCSDL cauHinh;

    /**
     * Khởi tạo KetNoiCSDL với các giá trị được cung cấp.
     *
     * @param cauHinh cấu hình
     */
    public KetNoiCSDL(CauHinhCSDL cauHinh) {
        this.cauHinh = cauHinh;
        DriverManager.setLoginTimeout(cauHinh.thoiGianChoDangNhapGiay());
    }

    /**
     * Mở trong KetNoiCSDL.
     *
     * @return kết quả mở
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    public Connection mo() throws SQLException {
        return DriverManager.getConnection(cauHinh.url(), cauHinh.nguoiDung(), cauHinh.matKhau());
    }

    /**
     * Xác minh trong KetNoiCSDL.
     *
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    public void xacMinh() throws SQLException {
        try (Connection ketNoi = mo()) {
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (!ketNoi.isValid(cauHinh.thoiGianChoDangNhapGiay())) {
                throw new SQLException("SQL Server không phản hồi.");
            }
        }
    }
}
