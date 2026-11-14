package com.petstation.util;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.imageio.ImageIO;

public final class TaiAnhSanPham {
    private TaiAnhSanPham() {}

    public static Path layThuMucNgoai() {
        try {
            Path nguon =
                    Path.of(
                            TaiAnhSanPham.class
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .toURI());
            Path thuMuc = nguon.getParent();
            if (thuMuc != null
                    && "target".equals(thuMuc.getFileName().toString())
                    && Files.isRegularFile(thuMuc.getParent().resolve("pom.xml")))
                return thuMuc.getParent();
            return thuMuc;
        } catch (Exception e) {
            throw new IllegalArgumentException("Không xác định được thư mục ảnh ứng dụng.", e);
        }
    }

    public static boolean kiemTraDuongDan(String duongDan) {
        return duongDan != null
                && duongDan.matches("images/products/[A-Za-z0-9_-]+\\.(?i:jpg|jpeg|png)");
    }

    public static BufferedImage doc(String duongDan) throws IOException {
        if (!kiemTraDuongDan(duongDan)) return null;
        Path thuMucGoc = layThuMucNgoai();
        Path tepMoi = thuMucGoc.resolve("data").resolve(duongDan);
        Path tepCu = thuMucGoc.resolve(duongDan);
        BufferedImage anh;
        if (Files.isRegularFile(tepMoi)) anh = ImageIO.read(tepMoi.toFile());
        else if (Files.isRegularFile(tepCu)) anh = ImageIO.read(tepCu.toFile());
        else
            try (var in = TaiAnhSanPham.class.getResourceAsStream("/" + duongDan)) {
                anh = in == null ? null : ImageIO.read(in);
            }
        if (anh == null) return null;
        double tyLe = Math.min(1d, 600d / Math.max(anh.getWidth(), anh.getHeight()));
        if (tyLe == 1d) return anh;
        BufferedImage nho =
                new BufferedImage(
                        Math.max(1, (int) (anh.getWidth() * tyLe)),
                        Math.max(1, (int) (anh.getHeight() * tyLe)),
                        BufferedImage.TYPE_INT_ARGB);
        var g = nho.createGraphics();
        try {
            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(anh, 0, 0, nho.getWidth(), nho.getHeight(), null);
        } finally {
            g.dispose();
        }
        return nho;
    }

    public static String saoChep(Path nguon) throws IOException {
        if (Files.size(nguon) > 12L * 1024 * 1024)
            throw new IOException("Ảnh không được lớn hơn 12 MB.");
        try (var in = ImageIO.createImageInputStream(nguon.toFile())) {
            var boDoc = ImageIO.getImageReaders(in);
            if (!boDoc.hasNext()) throw new IOException("Tệp không phải ảnh JPG hoặc PNG hợp lệ.");
            var doc = boDoc.next();
            try {
                doc.setInput(in);
                if ((long) doc.getWidth(0) * doc.getHeight(0) > 30_000_000L)
                    throw new IOException("Ảnh có độ phân giải quá lớn.");
            } finally {
                doc.dispose();
            }
        }
        BufferedImage anh = ImageIO.read(nguon.toFile());
        if (anh == null) throw new IOException("Không đọc được ảnh.");
        String duongDan = "images/products/TUY-CHON-" + UUID.randomUUID() + ".png";
        Path dich = layThuMucNgoai().resolve("data").resolve(duongDan);
        Files.createDirectories(dich.getParent());
        if (!ImageIO.write(anh, "png", dich.toFile()))
            throw new IOException("Không ghi được ảnh PNG.");
        return duongDan;
    }
}
