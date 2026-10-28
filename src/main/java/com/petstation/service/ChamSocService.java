package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.ChamSocDAO;
import com.petstation.dao.impl.ChamSocDAOImpl;
import com.petstation.model.Quyen;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.imageio.ImageIO;

public final class ChamSocService {
    private static final ChamSocDAO chamSocDAO = new ChamSocDAOImpl();

    private ChamSocService() {}

    public static Path saoChepAnh(Path source, Path directory) throws IOException {
        if (!Files.isRegularFile(source) || Files.size(source) > 10 * 1024 * 1024)
            throw new IllegalArgumentException("Ảnh phải là tệp JPEG/PNG tối đa 10 MB.");
        String name = source.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")))
            throw new IllegalArgumentException("Chỉ nhận JPEG/PNG.");
        try (var input = ImageIO.createImageInputStream(source.toFile())) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("Ảnh bị lỗi.");
            var reader = readers.next();
            try {
                reader.setInput(input);
                String format = reader.getFormatName();
                if (!format.equalsIgnoreCase("JPEG") && !format.equalsIgnoreCase("PNG"))
                    throw new IllegalArgumentException("Nội dung ảnh không phải JPEG/PNG.");
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 40_000_000)
                    throw new IllegalArgumentException("Ảnh vượt 40 megapixel.");
                if (reader.read(0) == null)
                    throw new IllegalArgumentException("Không đọc được ảnh.");
            } finally {
                reader.dispose();
            }
        }
        Files.createDirectories(directory);
        Path target =
                directory
                        .toAbsolutePath()
                        .normalize()
                        .resolve(UUID.randomUUID() + (name.endsWith(".png") ? ".png" : ".jpg"));
        Files.copy(source, target);
        return target;
    }

    private static void quyen(String loai) {
        if (!Set.of("SPA", "HOTEL").contains(loai))
            throw new IllegalArgumentException("Loại phiếu không hợp lệ.");
        yeuCau(loai.equals("SPA") ? Quyen.SPA_MANAGE : Quyen.HOTEL_MANAGE);
    }

    public static List<Object[]> lay(String loai, long id) {
        quyen(loai);
        return chamSocDAO.layLichSu(loai.equals("SPA"), id);
    }

    public static void luu(
            String loai,
            long id,
            String tinhTrang,
            String an,
            String thuoc,
            String viec,
            Path truoc,
            Path sau) {
        quyen(loai);
        if (tinhTrang == null
                || tinhTrang.isBlank()
                || tinhTrang.length() > 1000
                || an.length() > 1000
                || thuoc.length() > 1000
                || viec.length() > 2000)
            throw new IllegalArgumentException("Cần tình trạng; nội dung vượt độ dài cho phép.");
        List<Path> copied = new ArrayList<>();
        try {
            String config = CaiDatService.layTuyChon("CARE_DATA_DIR");
            Path folder =
                    Path.of(
                            config.isBlank()
                                    ? Path.of(
                                                    System.getProperty("user.home"),
                                                    "PetStation",
                                                    "cham-soc")
                                            .toString()
                                    : config);
            Path before = truoc == null ? null : saoChepAnh(truoc, folder);
            if (before != null) copied.add(before);
            Path after = sau == null ? null : saoChepAnh(sau, folder);
            if (after != null) copied.add(after);
            thucHienGiaoDich(
                    c -> {
                        var r = chamSocDAO.khoaPhieu(loai.equals("SPA"), id);
                        if (r.isEmpty()
                                || !(loai.equals("SPA")
                                                ? Set.of("RECEIVED", "IN_PROGRESS", "COMPLETED")
                                                : Set.of("CHECKED_IN"))
                                        .contains(r.get(0)[0]))
                            throw new IllegalArgumentException("Phiếu không còn cho ghi chăm sóc.");
                        chamSocDAO.themPhieu(
                                loai.equals("SPA") ? id : null,
                                loai.equals("HOTEL") ? id : null,
                                layIdNhanVien(),
                                tinhTrang,
                                an,
                                thuoc,
                                viec,
                                before == null ? null : before.toString(),
                                after == null ? null : after.toString());
                        if (loai.equals("HOTEL")) LuuTruService.ghiNhatKy(id, an, viec, tinhTrang);
                        NhatKyHeThongService.ghiNhatKy("Ghi phiếu chăm sóc", loai + " / " + id);
                        return null;
                    });
        } catch (IOException | RuntimeException ex) {
            for (Path p : copied)
                try {
                    Files.deleteIfExists(p);
                } catch (IOException cleanup) {
                    ex.addSuppressed(cleanup);
                }
            throw ex instanceof RuntimeException r
                    ? r
                    : new IllegalArgumentException("Không lưu được ảnh", ex);
        }
    }
}
