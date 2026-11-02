package com.petstation.util;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import javax.imageio.ImageIO;

/** Nạp ảnh cục bộ hoặc ảnh đóng gói trong JAR; không truy cập mạng khi bán hàng. */
public final class TaiAnhSanPham {
    private TaiAnhSanPham() {}

    /** Thư mục chứa ảnh người dùng chọn, đặt cạnh JAR để dùng được khi đổi thư mục chạy. */
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
            // Khi chạy từ target, ảnh người dùng phải sống ngoài thư mục build để không mất khi
            // clean.
            if (thuMuc != null
                    && "target".equals(thuMuc.getFileName().toString())
                    && Files.isRegularFile(thuMuc.getParent().resolve("pom.xml")))
                return thuMuc.getParent();
            return thuMuc;
        } catch (Exception e) {
            throw new IllegalStateException("Không xác định được thư mục ảnh ứng dụng.", e);
        }
    }

    /** Kiểm tra đường dẫn tương đối thuộc thư mục ảnh sản phẩm. */
    public static boolean kiemTraDuongDan(String duongDan) {
        return duongDan != null
                && duongDan.matches("images/products/[A-Za-z0-9_-]+\\.(?i:jpg|jpeg|png)");
    }

    /** Đọc ảnh theo đường dẫn SQL, ưu tiên ảnh bổ sung bên ngoài JAR. */
    public static BufferedImage doc(String duongDan) throws IOException {
        if (!kiemTraDuongDan(duongDan)) return null;
        Path tep = layThuMucNgoai().resolve(duongDan);
        BufferedImage anh;
        if (Files.isRegularFile(tep)) anh = ImageIO.read(tep.toFile());
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

    /** Sao chép ảnh hợp lệ thành PNG với tên mới; trả đường dẫn tương đối để lưu SQL. */
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
        Path dich = layThuMucNgoai().resolve(duongDan);
        Files.createDirectories(dich.getParent());
        if (!ImageIO.write(anh, "png", dich.toFile()))
            throw new IOException("Không ghi được ảnh PNG.");
        return duongDan;
    }
}
