package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.NhatKyChamSocDAO;
import com.petstation.dao.impl.NhatKyChamSocDAOImpl;
import com.petstation.model.Quyen;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.imageio.ImageIO;

public final class ChamSocService {
    private static final NhatKyChamSocDAO chamSocDAO = new NhatKyChamSocDAOImpl();

    private ChamSocService() {}

    public static void kiemTraThuMucAnhDungChung() {
        layThuMucAnhDungChung();
    }

    private static Path layThuMucAnhDungChung() {
        String config = CaiDatService.layTuyChon("THU_MUC_ANH_CHAM_SOC").trim();
        String dauGachCheo = Character.toString((char) 92);
        if (config.isBlank())
            throw new IllegalArgumentException(
                    "Chưa cấu hình thư mục ảnh chăm sóc dùng chung. "
                            + "Quản lý cần nhập đường dẫn UNC trong Cài đặt hệ thống.");
        if (!config.startsWith(dauGachCheo + dauGachCheo))
            throw new IllegalArgumentException(
                    "Ảnh chăm sóc phải lưu trên thư mục mạng UNC, ví dụ "
                            + "\\\\PETSTATION-NAS\\DuLieuPetStation\\cham-soc. "
                            + "Không dùng thư mục C:, D: hoặc ổ mạng Z: trên từng máy.");

        Path folder;
        try {
            folder = Path.of(config).toAbsolutePath().normalize();
            Files.createDirectories(folder);
            Path probe = folder.resolve(".petstation-write-" + UUID.randomUUID() + ".tmp");
            Files.writeString(
                    probe,
                    "PetStation shared storage check",
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);
            Files.deleteIfExists(probe);
        } catch (IOException | InvalidPathException ex) {
            throw new IllegalArgumentException(
                    "Không truy cập được thư mục ảnh dùng chung. Kiểm tra kết nối mạng "
                            + "và quyền đọc/ghi của tài khoản Windows.",
                    ex);
        }
        return folder;
    }

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
        yeuCau(loai.equals("SPA") ? Quyen.QUAN_LY_SPA : Quyen.QUAN_LY_LUU_TRU);
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
        String anChuan = an == null ? "" : an;
        String thuocChuan = thuoc == null ? "" : thuoc;
        String viecChuan = viec == null ? "" : viec;
        if (tinhTrang == null
                || tinhTrang.isBlank()
                || tinhTrang.length() > 1000
                || anChuan.length() > 1000
                || thuocChuan.length() > 1000
                || viecChuan.length() > 2000)
            throw new IllegalArgumentException("Cần tình trạng; nội dung vượt độ dài cho phép.");
        List<Path> copied = new ArrayList<>();
        try {
            Path folder =
                    (truoc != null || sau != null) ? layThuMucAnhDungChung() : null;
            Path before = truoc == null ? null : saoChepAnh(truoc, folder);
            if (before != null) copied.add(before);
            Path after = sau == null ? null : saoChepAnh(sau, folder);
            if (after != null) copied.add(after);
            thucHienGiaoDich(
                    c -> {
                        var r = chamSocDAO.khoaPhieu(loai.equals("SPA"), id);
                        if (r.isEmpty()
                                || !(loai.equals("SPA")
                                                ? Set.of("TIEP_NHAN", "DANG_THUC_HIEN", "HOAN_TAT")
                                                : Set.of("DANG_O"))
                                        .contains(r.get(0)[0]))
                            throw new IllegalArgumentException("Phiếu không còn cho ghi chăm sóc.");
                        chamSocDAO.themPhieu(
                                loai.equals("SPA") ? id : null,
                                loai.equals("HOTEL") ? id : null,
                                layIdNhanVien(),
                                tinhTrang,
                                anChuan,
                                thuocChuan,
                                viecChuan,
                                before == null ? null : before.toString(),
                                after == null ? null : after.toString());
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
