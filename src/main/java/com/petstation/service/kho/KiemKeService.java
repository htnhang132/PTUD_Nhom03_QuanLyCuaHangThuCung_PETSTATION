package com.petstation.service.kho;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;
import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.kho.KiemKeDAO;
import com.petstation.dao.kho.impl.KiemKeDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiBienBanKiemKe;
import com.petstation.model.kho.ChiTietKiemKe;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Tầng xử lý nghiệp vụ Kiểm kê tồn kho
public final class KiemKeService {
    private final KiemKeDAO kiemKeDAO;

    public KiemKeService() {
        this(new KiemKeDAOImpl());
    }

    public KiemKeService(KiemKeDAO kiemKeDAO) {
        this.kiemKeDAO = Objects.requireNonNull(kiemKeDAO);
    }

    // Dữ liệu một dòng lô hàng trong biên bản kiểm kê theo đợt
    public static final class DongKiemKe {
        public final String maLo;
        public final String maSP;
        public final String tenSP;
        public final String barcode;
        public final String soLo;
        public final LocalDate hanDung;
        public final int tonHeThong;
        public final int tonThucTe;
        public final String lyDo;

        public DongKiemKe(
                String maLo,
                String maSP,
                String tenSP,
                String barcode,
                String soLo,
                LocalDate hanDung,
                int tonHeThong,
                int tonThucTe,
                String lyDo) {
            this.maLo = maLo;
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.barcode = barcode;
            this.soLo = soLo;
            this.hanDung = hanDung;
            this.tonHeThong = tonHeThong;
            this.tonThucTe = tonThucTe;
            this.lyDo = lyDo;
        }
    }

    public List<Object[]> layDanhSachLoHangKiemKe(String loaiSanPham, boolean chiSapHetHan) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.KIEM_KE_KHO);
        List<LoHang> tatCa = kiemKeDAO.layDanhSachLoHangKiemKe();
        int soNgayCanhBao = CaiDatService.macDinh().layHanSuDungCanhBaoSoNgay();
        LocalDate mocHetHan = LocalDate.now().plusDays(soNgayCanhBao);
        return tatCa.stream()
                .filter(
                        lo -> {
                            SanPham sp = lo.getSanPham();
                            if (loaiSanPham != null
                                    && !loaiSanPham.isBlank()
                                    && !loaiSanPham.equalsIgnoreCase("TẤT CẢ")) {
                                String loai =
                                        sp == null || sp.getNhomHang() == null
                                                ? ""
                                                : sp.getNhomHang();
                                if (!loai.equalsIgnoreCase(loaiSanPham)) return false;
                            }
                            if (chiSapHetHan) {
                                LocalDate hsd = lo.getHanSuDung();
                                if (hsd == null || hsd.isAfter(mocHetHan)) return false;
                            }
                            return true;
                        })
                .map(
                        lo -> {
                            SanPham sp = lo.getSanPham();
                            return new Object[] {
                                lo.getMaLo(),
                                sp != null ? sp.getMaSanPham() : null,
                                sp != null ? sp.getTenSanPham() : null,
                                sp != null ? sp.getBarcode() : "",
                                lo.getSoLo(),
                                lo.getHanSuDung(),
                                lo.getSoLuongTon(),
                                sp != null ? sp.getNhomHang() : null
                            };
                        })
                .toList();
    }

    public List<Object[]> layChiTietKiemKe(String maBienBan) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.KIEM_KE_KHO);
        return kiemKeDAO.layChiTietKiemKe(maBienBan).stream()
                .map(
                        ct -> {
                            LoHang lo = ct.getLoHang();
                            SanPham sp = lo == null ? null : lo.getSanPham();
                            return new Object[] {
                                ct.getMaLo(),
                                sp != null ? sp.getMaSanPham() : null,
                                sp != null ? sp.getTenSanPham() : null,
                                lo != null ? lo.getSoLo() : null,
                                lo != null ? lo.getHanSuDung() : null,
                                ct.getTonHeThong(),
                                ct.getTonThucTe(),
                                ct.getChenhLech(),
                                ct.getNguyenNhan()
                            };
                        })
                .toList();
    }

    public String lapBienBanKiemKe(String kyKiemKe, List<DongKiemKe> danhSachDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.KIEM_KE_KHO);
        if (danhSachDong == null || danhSachDong.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng kiểm kê không được rỗng.");
        }
        String kyChuan =
                (kyKiemKe == null || kyKiemKe.isBlank())
                        ? "Kỳ kiểm kê " + LocalDate.now()
                        : kyKiemKe;
        int demChenhLech = kiemTraVaDemChenhLech(danhSachDong);
        return QuanLyGiaoDich.macDinh()
                .thucThi(ketNoi -> luuBienBanKiemKe(kyChuan, danhSachDong, demChenhLech));
    }

    private static int kiemTraVaDemChenhLech(List<DongKiemKe> danhSachDong) {
        int demChenhLech = 0;
        for (DongKiemKe d : danhSachDong) {
            if (d.tonThucTe < 0) {
                throw new IllegalArgumentException(
                        "Tồn thực tế của SP " + d.maSP + " không được âm.");
            }
            if (d.tonThucTe != d.tonHeThong) {
                demChenhLech++;
                if (d.lyDo == null || d.lyDo.isBlank()) {
                    throw new IllegalArgumentException(
                            "Vui lòng ghi lý do chênh lệch cho sản phẩm: "
                                    + d.maSP
                                    + " ("
                                    + d.tenSP
                                    + ")");
                }
            }
        }
        return demChenhLech;
    }

    private String luuBienBanKiemKe(
            String kyKiemKe, List<DongKiemKe> danhSachDong, int demChenhLech) {
        boolean coChenhLech = demChenhLech > 0;
        String soBienBan = taoMa("KK");
        String trangThai =
                coChenhLech
                        ? TrangThaiBienBanKiemKe.CHO_DUYET.maDB()
                        : TrangThaiBienBanKiemKe.DA_DUYET.maDB();
        String maBienBan =
                kiemKeDAO.themKiemKe(
                        soBienBan,
                        PhienDangNhapService.macDinh().layMaNhanVien(),
                        kyKiemKe,
                        trangThai);
        for (DongKiemKe d : danhSachDong) {
            kiemKeDAO.capNhatKiemKe(
                    maBienBan, d.maLo, d.tonHeThong, d.tonThucTe, d.lyDo == null ? "" : d.lyDo);
        }
        if (coChenhLech) {
            new PheDuyetService()
                    .guiThaoTac(
                            PheDuyetService.Loai.KIEM_KE,
                            "Kiểm kê định kỳ: có " + demChenhLech + " mặt hàng chênh lệch",
                            "Kỳ: " + kyKiemKe,
                            "Số BB: " + soBienBan,
                            "KIEM_KE",
                            maBienBan,
                            "");
        }
        NhatKyHeThongService.macDinh()
                .ghiNhatKy(
                        "Kiểm kê đợt",
                        soBienBan
                                + " ("
                                + danhSachDong.size()
                                + " dòng, "
                                + demChenhLech
                                + " lệch)");
        return maBienBan;
    }

    public String kiemKe(String maLo, int thucTe, String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.KIEM_KE_KHO);
        if (thucTe < 0) throw new IllegalArgumentException("Tồn thực tế không được âm.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            Optional<Integer> tonHienTai = kiemKeDAO.timKiemKe(maLo);
                            if (tonHienTai.isEmpty())
                                throw new IllegalArgumentException("Không tìm thấy lô.");
                            int datPhong = tonHienTai.get();
                            if (datPhong != thucTe && (lyDo == null || lyDo.isBlank()))
                                throw new IllegalArgumentException("Cần lý do chênh lệch.");
                            String maBienBan =
                                    kiemKeDAO.themKiemKe(
                                            taoMa("KK"),
                                            PhienDangNhapService.macDinh().layMaNhanVien(),
                                            LocalDate.now().toString(),
                                            datPhong == thucTe
                                                    ? TrangThaiBienBanKiemKe.DA_DUYET.maDB()
                                                    : TrangThaiBienBanKiemKe.CHO_DUYET.maDB());
                            kiemKeDAO.capNhatKiemKe(maBienBan, maLo, datPhong, thucTe, lyDo);
                            if (datPhong != thucTe)
                                new PheDuyetService()
                                        .guiThaoTac(
                                                PheDuyetService.Loai.KIEM_KE,
                                                lyDo,
                                                "Tồn sổ " + datPhong,
                                                "Tồn thực " + thucTe,
                                                "KIEM_KE",
                                                maBienBan,
                                                "");
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Kiểm kê", chuyenThanhChuoi(maBienBan));
                            return maBienBan;
                        });
    }

    public void duyetVaCanBangKho(String maBienBan) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            apDungKiemKe(maBienBan);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Cân bằng kho kiểm kê", "Biên bản " + maBienBan);
                            return null;
                        });
    }

    public void apDungKiemKe(String maBienBan) {
        if (kiemKeDAO.demApDungKiemKe(maBienBan) == 0)
            throw new IllegalStateException("Biên bản đã xử lý.");
        for (ChiTietKiemKe dongDuLieu : kiemKeDAO.timApDungKiemKe(maBienBan))
            if (kiemKeDAO.capNhatApDungKiemKe(
                            dongDuLieu.getTonThucTe(),
                            dongDuLieu.getMaLo(),
                            dongDuLieu.getTonHeThong())
                    != 1)
                throw new IllegalStateException(
                        "Tồn đã thay đổi sau kiểm kê. Cần lập lại biên bản.");
        kiemKeDAO.danhDauKiemKeDaDuyet(maBienBan);
    }
}
