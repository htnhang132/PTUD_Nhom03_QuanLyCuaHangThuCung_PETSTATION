package com.petstation.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

class CauHinhCSDLTest {
    @Test
    void docCauHinhUtf8CoBomVaMatKhauTiengViet() throws Exception {
        ghi(
                "config/db.properties",
                "\uFEFFdb.url=test-url\ndb.user=test-user\ndb.password=mậtKhẩu123\n");
        var cauHinh = CauHinhCSDL.nap(Map.of(), thuMuc);
        assertThat(cauHinh.url()).isEqualTo("test-url");
        assertThat(cauHinh.matKhau()).isEqualTo("mậtKhẩu123");
    }

    @TempDir Path thuMuc;

    private void ghi(String tep, String noiDung) throws Exception {
        Path dich = thuMuc.resolve(tep);
        Files.createDirectories(dich.getParent());
        Files.writeString(dich, noiDung);
    }

    @Test
    void bienMoiTruongUuTienHonTepNgoai() throws Exception {
        ghi("config/db.properties", "db.url=file-url\ndb.user=file-user\ndb.password=file-pass\n");
        var config =
                CauHinhCSDL.nap(
                        Map.of(
                                "PETSTATION_DB_URL",
                                "env-url",
                                "PETSTATION_DB_USER",
                                "env-user",
                                "PETSTATION_DB_PASSWORD",
                                "env-pass"),
                        thuMuc);
        assertThat(config.url()).isEqualTo("env-url");
        assertThat(config.nguoiDung()).isEqualTo("env-user");
        assertThat(config.matKhau()).isEqualTo("env-pass");
    }

    @Test
    void tepNgoaiDuocUuTienHonCauHinhPhatTrienCu() throws Exception {
        ghi("src/main/resources/db.properties", "db.url=legacy\ndb.user=legacy\n");
        ghi("config/db.properties", "db.url=external\ndb.user=external\n");
        assertThat(CauHinhCSDL.nap(Map.of(), thuMuc).url()).isEqualTo("external");
    }

    @Test
    void giuTuongThichCauHinhPhatTrienCu() throws Exception {
        ghi("src/main/resources/db.properties", "db.url=legacy\ndb.user=legacy\n");
        assertThat(CauHinhCSDL.nap(Map.of(), thuMuc).url()).isEqualTo("legacy");
    }

    @Test
    void tepChiDinhBiThieuKhongAmThamChuyenDatabaseKhac() {
        assertThatThrownBy(
                        () ->
                                CauHinhCSDL.nap(
                                        Map.of("PETSTATION_DB_CONFIG", "missing.properties"),
                                        thuMuc))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Không thể đọc");
    }

    @Test
    void thieuCauHinhBaoLoiRoRang() {
        assertThatThrownBy(() -> CauHinhCSDL.nap(Map.of(), thuMuc))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Chưa cấu hình");
    }

    @Test
    void toStringKhongLamLoThongTinKetNoi() {
        var cauHinh =
                new CauHinhCSDL(
                        "jdbc:sqlserver://private-host;password=hidden",
                        "private-user",
                        "secret-password",
                        10);
        assertThat(cauHinh.toString())
                .doesNotContain("private-host", "private-user", "secret-password", "hidden");
        assertThat(cauHinh.toString()).contains("***");
    }
}
