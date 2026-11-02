package com.petstation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Mở và kiểm tra kết nối JDBC theo cấu hình SQL Server. */
public final class KetNoiCSDL {
    /** Cấu hình. */
    private final CauHinhCSDL cauHinh;

    public KetNoiCSDL(CauHinhCSDL cauHinh) {
        this.cauHinh = cauHinh;
        DriverManager.setLoginTimeout(cauHinh.thoiGianChoDangNhapGiay());
    }

    public Connection mo() throws SQLException {
        return DriverManager.getConnection(cauHinh.url(), cauHinh.nguoiDung(), cauHinh.matKhau());
    }

    public void xacMinh() throws SQLException {
        try (Connection ketNoi = mo()) {
            if (!ketNoi.isValid(cauHinh.thoiGianChoDangNhapGiay())) {
                throw new SQLException("SQL Server không phản hồi.");
            }
            if (ketNoi.getMetaData().getDatabaseMajorVersion() < 15) {
                throw new SQLException(
                        "Pet Station cần SQL Server 2019 trở lên. Phiên bản SSMS không phải phiên"
                                + " bản SQL Server.");
            }
        }
    }
}
