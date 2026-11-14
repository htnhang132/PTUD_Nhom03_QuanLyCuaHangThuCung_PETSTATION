package com.petstation.database;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public final class CauHinhCSDL {
    private final String url;
    private final String nguoiDung;
    private final String matKhau;
    private final int thoiGianChoDangNhapGiay;

    public CauHinhCSDL(String url, String nguoiDung, String matKhau, int thoiGianChoDangNhapGiay) {
        this.url = url;
        this.nguoiDung = nguoiDung;
        this.matKhau = matKhau;
        this.thoiGianChoDangNhapGiay = thoiGianChoDangNhapGiay;
    }

    public String url() {
        return url;
    }

    public String nguoiDung() {
        return nguoiDung;
    }

    public String matKhau() {
        return matKhau;
    }

    public int thoiGianChoDangNhapGiay() {
        return thoiGianChoDangNhapGiay;
    }

    public static CauHinhCSDL nap() {
        return nap(System.getenv(), Path.of("."));
    }

    static CauHinhCSDL nap(Map<String, String> moiTruong, Path thuMuc) {
        Properties cacThuocTinh = new Properties();
        String chiDinh = moiTruong.get("PETSTATION_DB_CONFIG");
        Path tepCauHinh =
                chiDinh == null || chiDinh.isBlank()
                        ? thuMuc.resolve(Path.of("config", "db.properties"))
                        : thuMuc.resolve(chiDinh);
        if ((chiDinh == null || chiDinh.isBlank()) && !Files.isRegularFile(tepCauHinh))
            tepCauHinh = thuMuc.resolve(Path.of("src", "main", "resources", "db.properties"));
        try {
            if (chiDinh != null && !chiDinh.isBlank() || Files.isRegularFile(tepCauHinh))
                try (var dauVao =
                        new StringReader(
                                Files.readString(tepCauHinh, StandardCharsets.UTF_8)
                                        .replaceFirst("^\\uFEFF", ""))) {
                    cacThuocTinh.load(dauVao);
                }
        } catch (IOException ngoaiLe) {
            throw new IllegalArgumentException("Không thể đọc cấu hình cơ sở dữ liệu.", ngoaiLe);
        }
        String url = layGiaTri(moiTruong, "PETSTATION_DB_URL", cacThuocTinh, "db.url", "");
        String nguoiDung = layGiaTri(moiTruong, "PETSTATION_DB_USER", cacThuocTinh, "db.user", "");
        String matKhau =
                layGiaTri(moiTruong, "PETSTATION_DB_PASSWORD", cacThuocTinh, "db.password", "");
        int thoiGianCho =
                Integer.parseInt(
                        layGiaTri(
                                moiTruong,
                                "PETSTATION_DB_LOGIN_TIMEOUT_SECONDS",
                                cacThuocTinh,
                                "db.loginTimeoutSeconds",
                                "10"));
        if (url.isBlank() || nguoiDung.isBlank()) {
            throw new IllegalArgumentException(
                    "Chưa cấu hình SQL Server. Hãy đặt PETSTATION_DB_URL, PETSTATION_DB_USER, "
                            + "PETSTATION_DB_PASSWORD hoặc tạo config/db.properties cục bộ.");
        }
        return new CauHinhCSDL(url, nguoiDung, matKhau, thoiGianCho);
    }

    @Override
    public String toString() {
        return "CauHinhCSDL[cauHinh=***, thoiGianChoDangNhapGiay=" + thoiGianChoDangNhapGiay + "]";
    }

    private static String layGiaTri(
            Map<String, String> moiTruong,
            String moiTruongKhoa,
            Properties cacThuocTinh,
            String thuocTinhKhoa,
            String thayThe) {
        String giaTriMoiTruong = moiTruong.get(moiTruongKhoa);
        if (giaTriMoiTruong != null && !giaTriMoiTruong.isBlank()) {
            return giaTriMoiTruong.trim();
        }
        return cacThuocTinh.getProperty(thuocTinhKhoa, thayThe).trim();
    }
}
