package com.petstation.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.nio.file.*;

import javax.imageio.ImageIO;

class ChamSocServiceTest {
    @TempDir Path temp;

    @Test
    void saoChepAnhDocLapVoiTepGoc() throws Exception {
        Path source = temp.resolve("input.png");
        ImageIO.write(
                new BufferedImage(30, 20, BufferedImage.TYPE_INT_RGB), "PNG", source.toFile());
        Path copy = ChamSocService.saoChepAnh(source, temp.resolve("data"));
        Files.delete(source);
        assertThat(Files.isRegularFile(copy)).isTrue();
        assertThat(ImageIO.read(copy.toFile()).getWidth()).isEqualTo(30);
    }

    @Test
    void tuChoiTepGiaAnhVaAnhQuaLon() throws Exception {
        Path fake = temp.resolve("fake.png");
        Files.writeString(fake, "not an image");
        assertThatThrownBy(() -> ChamSocService.saoChepAnh(fake, temp.resolve("data")))
                .isInstanceOf(IllegalArgumentException.class);
        Path large = temp.resolve("large.png");
        try (var f = new java.io.RandomAccessFile(large.toFile(), "rw")) {
            f.setLength(10 * 1024 * 1024 + 1);
        }
        assertThatThrownBy(() -> ChamSocService.saoChepAnh(large, temp.resolve("data")))
                .hasMessageContaining("10 MB");
    }
}
