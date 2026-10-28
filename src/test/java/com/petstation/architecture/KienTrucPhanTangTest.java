package com.petstation.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Kiểm tra quy tắc package bằng cách đọc source, không cần framework kiến trúc riêng. */
class KienTrucPhanTangTest {
    private final Path nguon = Path.of("src/main/java/com/petstation");

    private void kiemTraPhuThuoc(String thuMuc, String... goiCam) throws Exception {
        List<String> loi = new ArrayList<>();
        try (var files = Files.walk(nguon.resolve(thuMuc))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String code = Files.readString(file);
                for (String goi : goiCam) {
                    if (code.contains(goi + ".")) loi.add(file.getFileName() + " -> " + goi);
                }
            }
        }
        assertThat(loi).as("Phụ thuộc sai tầng").isEmpty();
    }

    @Test
    void modelKhongGoiTangTren() throws Exception {
        kiemTraPhuThuoc(
                "model",
                "com.petstation.gui",
                "com.petstation.service",
                "com.petstation.dao",
                "com.petstation.database",
                "javax.swing",
                "java.awt");
    }

    @Test
    void daoKhongGoiServiceHoacGui() throws Exception {
        kiemTraPhuThuoc(
                "dao", "com.petstation.service", "com.petstation.gui", "javax.swing", "java.awt");
    }

    @Test
    void serviceKhongGoiSwing() throws Exception {
        kiemTraPhuThuoc("service", "com.petstation.gui", "javax.swing", "java.awt");
    }

    @Test
    void guiKhongGoiDaoHoacKetNoi() throws Exception {
        kiemTraPhuThuoc("gui", "com.petstation.dao", "com.petstation.database");
    }

    @Test
    void daoVaImplDatTenDungQuyUoc() throws Exception {
        try (var files = Files.list(nguon.resolve("dao/impl"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String ten = file.getFileName().toString().replace(".java", "");
                assertThat(ten).endsWith("DAOImpl").doesNotStartWith("Jdbc");
                String dao = ten.replaceFirst("DAOImpl$", "DAO");
                assertThat(Files.isRegularFile(nguon.resolve("dao/" + dao + ".java"))).isTrue();
                assertThat(Files.readString(file)).contains("implements " + dao);
            }
        }
    }
}
