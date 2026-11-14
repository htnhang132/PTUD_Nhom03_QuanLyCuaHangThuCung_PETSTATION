package com.petstation.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class KetNoiCSDL implements AutoCloseable {
    private final CauHinhCSDL cauHinh;
    private volatile HikariDataSource boKetNoi;

    public KetNoiCSDL(CauHinhCSDL cauHinh) {
        this.cauHinh = cauHinh;
        DriverManager.setLoginTimeout(cauHinh.thoiGianChoDangNhapGiay());
    }

    private HikariDataSource layBoKetNoi() {
        HikariDataSource hienTai = boKetNoi;
        if (hienTai != null && !hienTai.isClosed()) {
            return hienTai;
        }
        synchronized (this) {
            if (boKetNoi == null || boKetNoi.isClosed()) {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(cauHinh.url());
                config.setUsername(cauHinh.nguoiDung());
                config.setPassword(cauHinh.matKhau());
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(
                        Math.max(1000L, cauHinh.thoiGianChoDangNhapGiay() * 1000L));
                config.setIdleTimeout(300_000L);
                config.setMaxLifetime(1_800_000L);
                config.setPoolName("PetStationPool");
                boKetNoi = new HikariDataSource(config);
            }
            return boKetNoi;
        }
    }

    public Connection mo() throws SQLException {
        return layBoKetNoi().getConnection();
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

    public synchronized void dong() {
        if (boKetNoi != null && !boKetNoi.isClosed()) {
            boKetNoi.close();
        }
        boKetNoi = null;
    }

    @Override
    public void close() {
        dong();
    }
}
