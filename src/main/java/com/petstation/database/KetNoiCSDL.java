package com.petstation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class KetNoiCSDL {
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
            try (var statement = ketNoi.createStatement();
                    var result =
                            statement.executeQuery(
                                    "SELECT gia_tri FROM dbo.cai_dat WHERE"
                                            + " ma_cai_dat='PHIEN_BAN_CSDL'")) {
                if (!result.next() || !"20261124".equals(result.getString(1)))
                    throw new SQLException(
                            "Cơ sở dữ liệu cần nâng cấp lên 20261124. Chạy"
                                + " scripts/setup-database.ps1 với database hiện tại trước khi mở"
                                + " ứng dụng.");
            }
        }
    }
}
