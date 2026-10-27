package com.petstation.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.nio.file.*;

import javax.imageio.ImageIO;

/** Kiểm tra toàn bộ ảnh thực tế, đường dẫn an toàn và ảnh do người dùng bổ sung. */
class TaiAnhSanPhamTest {
    @TempDir Path tam;

    @Test
    void duSauMuoiAnhDongGoiVaDocDuocKhongCanMang() throws Exception {
        try (var tep = Files.list(Path.of("src/main/resources/images/products"))) {
            var ds = tep.filter(Files::isRegularFile).toList();
            assertThat(ds).hasSize(60);
            for (Path p : ds) {
                BufferedImage goc = ImageIO.read(p.toFile());
                assertThat(goc).as(p.toString()).isNotNull();
                assertThat(Math.max(goc.getWidth(), goc.getHeight())).isGreaterThanOrEqualTo(600);
                var anh = TaiAnhSanPham.doc("images/products/" + p.getFileName());
                assertThat(anh).as(p.toString()).isNotNull();
                assertThat(Math.max(anh.getWidth(), anh.getHeight())).isLessThanOrEqualTo(600);
            }
        }
    }

    @Test
    void chanDuongDanNgoaiThuMucAnhVaAnhKhongTonTai() throws Exception {
        assertThat(TaiAnhSanPham.doc("../../db.properties")).isNull();
        assertThat(TaiAnhSanPham.doc("https://example.com/a.jpg")).isNull();
        assertThat(TaiAnhSanPham.doc("C:/secret.png")).isNull();
        assertThat(TaiAnhSanPham.doc("images/products/KHONG-TON-TAI.png")).isNull();
    }

    @Test
    void saoChepAnhTuMayThanhDuongDanTuongDoiVaTuChoiTepHong() throws Exception {
        Path nguon = tam.resolve("anh.png");
        ImageIO.write(
                new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB), "png", nguon.toFile());
        String duongDan = TaiAnhSanPham.saoChep(nguon);
        try {
            assertThat(TaiAnhSanPham.kiemTraDuongDan(duongDan)).isTrue();
            var anh = TaiAnhSanPham.doc(duongDan);
            assertThat(anh.getWidth()).isEqualTo(100);
            assertThat(anh.getHeight()).isEqualTo(50);
        } finally {
            Files.deleteIfExists(TaiAnhSanPham.layThuMucNgoai().resolve(duongDan));
        }
        Path hong = tam.resolve("hong.png");
        Files.writeString(hong, "khong phai anh");
        assertThatThrownBy(() -> TaiAnhSanPham.saoChep(hong))
                .isInstanceOf(java.io.IOException.class);
    }
}
