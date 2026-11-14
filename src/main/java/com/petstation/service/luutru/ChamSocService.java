package com.petstation.service.luutru;

import com.petstation.dao.luutru.NhatKyChamSocDAO;
import com.petstation.dao.luutru.impl.NhatKyChamSocDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.luutru.NhatKyChamSoc;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

public final class ChamSocService {
    private final NhatKyChamSocDAO chamSocDAO;
    private final CaiDatService caiDatService;
    private final NhatKyHeThongService nhatKyService;
    private final PhienDangNhapService phienDangNhap;

    public ChamSocService() {
        this(
                new NhatKyChamSocDAOImpl(),
                CaiDatService.macDinh(),
                NhatKyHeThongService.macDinh(),
                PhienDangNhapService.macDinh());
    }

    public ChamSocService(NhatKyChamSocDAO chamSocDAO) {
        this(
                chamSocDAO,
                CaiDatService.macDinh(),
                NhatKyHeThongService.macDinh(),
                PhienDangNhapService.macDinh());
    }

    public ChamSocService(
            NhatKyChamSocDAO chamSocDAO,
            CaiDatService caiDatService,
            NhatKyHeThongService nhatKyService,
            PhienDangNhapService phienDangNhap) {
        this.chamSocDAO = Objects.requireNonNull(chamSocDAO, "chamSocDAO không được null");
        this.caiDatService = Objects.requireNonNull(caiDatService, "caiDatService không được null");
        this.nhatKyService = Objects.requireNonNull(nhatKyService, "nhatKyService không được null");
        this.phienDangNhap = Objects.requireNonNull(phienDangNhap, "phienDangNhap không được null");
    }

    public void kiemTraThuMucAnhDungChung() {
        layThuMucAnhDungChung();
    }

    private Path layThuMucAnhDungChung() {
        String config = caiDatService.layTuyChon("THU_MUC_ANH_CHAM_SOC").trim();
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

    private void quyen(String loai) {
        if (!Set.of("SPA", "HOTEL").contains(loai))
            throw new IllegalArgumentException("Loại phiếu không hợp lệ.");
        phienDangNhap.yeuCau(loai.equals("SPA") ? Quyen.QUAN_LY_SPA : Quyen.QUAN_LY_LUU_TRU);
    }

    public List<NhatKyChamSoc> lay(String loai, String ma) {
        quyen(loai);
        return chamSocDAO.layLichSu(loai.equals("SPA"), ma);
    }

    public void luu(
            String loai,
            String ma,
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
            Path folder = (truoc != null || sau != null) ? layThuMucAnhDungChung() : null;
            Path before = truoc == null ? null : saoChepAnh(truoc, folder);
            if (before != null) copied.add(before);
            Path after = sau == null ? null : saoChepAnh(sau, folder);
            if (after != null) copied.add(after);
            QuanLyGiaoDich.macDinh()
                    .thucThi(
                            c -> {
                                List<String> r = chamSocDAO.khoaPhieu(loai.equals("SPA"), ma);
                                if (r.isEmpty()
                                        || !(loai.equals("SPA")
                                                        ? Set.of(
                                                                TrangThaiPhieuSpa.TIEP_NHAN.maDB(),
                                                                TrangThaiPhieuSpa.DANG_THUC_HIEN
                                                                        .maDB(),
                                                                TrangThaiPhieuSpa.HOAN_TAT.maDB())
                                                        : Set.of(TrangThaiHopDong.DANG_O.maDB()))
                                                .contains(r.get(0)))
                                    throw new IllegalArgumentException(
                                            "Phiếu không còn cho ghi chăm sóc.");
                                chamSocDAO.themPhieu(
                                        loai.equals("SPA") ? ma : null,
                                        loai.equals("HOTEL") ? ma : null,
                                        phienDangNhap.layMaNhanVien(),
                                        tinhTrang,
                                        anChuan,
                                        thuocChuan,
                                        viecChuan,
                                        before == null ? null : before.toString(),
                                        after == null ? null : after.toString());
                                nhatKyService.ghiNhatKy("Ghi phiếu chăm sóc", loai + " / " + ma);
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
