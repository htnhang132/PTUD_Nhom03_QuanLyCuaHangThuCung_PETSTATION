package com.petstation.service.pheduyet;

import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.pheduyet.YeuCauPheDuyetDAO;
import com.petstation.dao.pheduyet.impl.YeuCauPheDuyetDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.model.pheduyet.YeuCauPheDuyet;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.KiemKeService;
import com.petstation.service.kho.NhapXuatKhoService;
import com.petstation.service.nhansu.LichLamViecService;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PheDuyetService {
    private final YeuCauPheDuyetDAO pheDuyetDAO;

    public PheDuyetService() {
        this(new YeuCauPheDuyetDAOImpl());
    }

    public PheDuyetService(YeuCauPheDuyetDAO pheDuyetDAO) {
        this.pheDuyetDAO = Objects.requireNonNull(pheDuyetDAO, "pheDuyetDAO không được null");
    }

    public enum Loai {
        SUA_HOA_DON("Sửa/hủy hóa đơn"),
        DOI_TRA("Đổi trả vượt hạn mức"),
        XUAT_HUY("Xuất hủy giá trị lớn"),
        KIEM_KE("Điều chỉnh kiểm kê"),
        NGHI_PHEP("Nghỉ phép"),
        GIA_BAN("Đề xuất giá bán");
        private final String nhan;

        Loai(String nhan) {
            this.nhan = nhan;
        }

        public String layNhan() {
            return nhan;
        }

        public static Loai docTuCSDL(String ma) {
            if (ma == null || ma.isBlank()) return SUA_HOA_DON;
            return switch (ma.trim()) {
                case "SUA_HOA_DON" -> SUA_HOA_DON;
                case "DOI_TRA" -> DOI_TRA;
                case "XUAT_HUY" -> XUAT_HUY;
                case "KIEM_KE" -> KIEM_KE;
                case "NGHI_PHEP" -> NGHI_PHEP;
                case "GIA_BAN" -> GIA_BAN;
                default -> Loai.valueOf(ma.trim());
            };
        }
    }

    public static final class YeuCau {
        private final String id;
        private final Loai loai;
        private final String nguoiGui;
        private final LocalDateTime thoiGian;
        private final String lyDo;
        private final String truoc;
        private final String sau;
        private TrangThaiPheDuyet trangThai;
        private String daQuyetDinhTheo;
        private String tuChoiLyDo;

        YeuCau(YeuCauPheDuyetDAO.ThongTinYeuCau dongDuLieu) {
            id = dongDuLieu.maYeuCau();
            loai = Loai.docTuCSDL(dongDuLieu.loai());
            nguoiGui = dongDuLieu.nguoiGui();
            thoiGian = dongDuLieu.guiLuc();
            lyDo = dongDuLieu.lyDo();
            truoc = dongDuLieu.noiDungTruoc();
            sau = dongDuLieu.noiDungSau();
            trangThai = docTrangThai(dongDuLieu.trangThai());
            daQuyetDinhTheo = dongDuLieu.nguoiXuLy();
            tuChoiLyDo = dongDuLieu.phanHoi();
        }

        private static TrangThaiPheDuyet docTrangThai(String ma) {
            if (ma == null || ma.isBlank()) return null;
            if ("CHO_BO_SUNG".equals(ma.trim())) return TrangThaiPheDuyet.CAN_THEM_THONG_TIN;
            try {
                return TrangThaiPheDuyet.tuMaDB(ma);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        public String layId() {
            return id;
        }

        public Loai layLoai() {
            return loai;
        }

        public String layNguoiGui() {
            return nguoiGui;
        }

        public String layLyDo() {
            return lyDo;
        }

        public String layTruoc() {
            return truoc;
        }

        public String laySau() {
            return sau;
        }

        public TrangThaiPheDuyet layTrangThai() {
            return trangThai;
        }

        public String layTenTrangThai() {
            if (trangThai == null) return "Trạng thái không xác định";
            return switch (trangThai) {
                case CHO_DUYET -> "Chờ duyệt";
                case DA_DUYET -> "Đã duyệt";
                case TU_CHOI -> "Từ chối";
                case CAN_THEM_THONG_TIN -> "Chờ bổ sung";
                case DA_HUY -> "Đã hủy";
            };
        }

        public String layDaQuyetDinhTheo() {
            return daQuyetDinhTheo;
        }

        public String layTuChoiLyDo() {
            return tuChoiLyDo;
        }

        public String layVanBanThoiGian() {
            return thoiGian.format(DinhDang.NGAY_THANG_GIO_PHUT);
        }
    }

    private static final List<BoLangNgheSuKien<String>> CAC_BO_LANG_NGHE =
            new CopyOnWriteArrayList<>();

    public YeuCau guiThaoTac(
            Loai loai,
            String lyDo,
            String truoc,
            String sau,
            String thaoTac,
            String maDich,
            String noiDungXuLy) {
        Quyen quyen =
                switch (loai) {
                    case SUA_HOA_DON, DOI_TRA -> Quyen.BAN_HANG;
                    case XUAT_HUY -> Quyen.XUAT_HUY_KHO;
                    case KIEM_KE -> Quyen.KIEM_KE_KHO;
                    case NGHI_PHEP -> Quyen.XEM_LICH_LAM_VIEC;
                    case GIA_BAN -> Quyen.DE_XUAT_GIA;
                };
        PhienDangNhapService.macDinh().yeuCau(quyen);
        boolean dungThaoTac =
                switch (loai) {
                    case SUA_HOA_DON -> Set.of("GHI_CHU_HOA_DON", "HUY_HOA_DON").contains(thaoTac);
                    case DOI_TRA -> "DOI_TRA".equals(thaoTac);
                    case XUAT_HUY -> "XUAT_HUY".equals(thaoTac);
                    case KIEM_KE -> "KIEM_KE".equals(thaoTac);
                    case NGHI_PHEP -> "NGHI_PHEP".equals(thaoTac);
                    case GIA_BAN -> "DOI_GIA".equals(thaoTac);
                };
        if (!dungThaoTac || maDich == null || maDich.isBlank())
            throw new IllegalArgumentException("Thao tác không phù hợp với loại phê duyệt.");
        if (lyDo == null || lyDo.isBlank()) throw new IllegalArgumentException("Cần nhập lý do.");
        String ma = taoMa("YC");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            pheDuyetDAO.capNhatGuiThaoTac(
                                    ma,
                                    loai.name(),
                                    PhienDangNhapService.macDinh().layMaNhanVien(),
                                    thaoTac,
                                    maDich,
                                    lyDo,
                                    truoc,
                                    sau,
                                    noiDungXuLy);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Gửi phê duyệt", ma + " / " + loai.nhan);
                            return null;
                        });
        thongBao("GUI:" + ma);
        return layTatCa().stream()
                .filter(dongDuLieu -> dongDuLieu.id.equals(ma))
                .findFirst()
                .orElseThrow();
    }

    public List<YeuCau> layTatCa() {
        List<YeuCau> ketQua = new ArrayList<>();
        for (YeuCauPheDuyetDAO.ThongTinYeuCau dongDuLieu : pheDuyetDAO.timTatCa())
            ketQua.add(new YeuCau(dongDuLieu));
        return ketQua;
    }

    public long demChoDuyet() {
        return pheDuyetDAO.demSoLuongDangCho();
    }

    public void duyet(YeuCau yeuCau, String nguoiDuyet) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            YeuCauPheDuyetDAO.ThongTinDoiTuong dongDuLieu =
                                    pheDuyetDAO
                                            .timDuyet(yeuCau.id)
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalArgumentException(
                                                                    "Yêu cầu đã được xử lý."));
                            apDungPheDuyet(
                                    dongDuLieu.loaiDoiTuong(),
                                    dongDuLieu.maDoiTuong(),
                                    dongDuLieu.payload());
                            pheDuyetDAO.capNhatDuyet(
                                    PhienDangNhapService.macDinh().layMaNhanVien(), yeuCau.id);
                            NhatKyHeThongService.macDinh().ghiNhatKy("Duyệt", yeuCau.id);
                            return null;
                        });
        yeuCau.trangThai = TrangThaiPheDuyet.DA_DUYET;
        yeuCau.daQuyetDinhTheo = nguoiDuyet;
        thongBao("DUYET:" + yeuCau.id);
    }

    public void tuChoi(YeuCau yeuCau, String nguoiDuyet, String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        if (lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Cần lý do từ chối.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            YeuCauPheDuyetDAO.ThongTinDoiTuong dongDuLieu =
                                    pheDuyetDAO
                                            .timTuChoi(yeuCau.id)
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalArgumentException(
                                                                    "Yêu cầu đã xử lý."));
                            String loaiDoiTuong = dongDuLieu.loaiDoiTuong();
                            String maDoiTuong = dongDuLieu.maDoiTuong();
                            if ("DOI_TRA".equals(loaiDoiTuong)
                                    || "PHIEU_DOI_TRA".equals(loaiDoiTuong))
                                pheDuyetDAO.tuChoiPhieuDoiTra(maDoiTuong);
                            if ("XUAT_HUY".equals(loaiDoiTuong)
                                    || "PHIEU_XUAT_KHO".equals(loaiDoiTuong))
                                pheDuyetDAO.capNhatTuChoi(maDoiTuong);
                            if ("KIEM_KE".equals(loaiDoiTuong)
                                    || "BIEN_BAN_KIEM_KE".equals(loaiDoiTuong))
                                pheDuyetDAO.tuChoiBienBanKiemKe(maDoiTuong);
                            if ("DOI_GIA".equals(loaiDoiTuong) || "SAN_PHAM".equals(loaiDoiTuong))
                                new DeXuatGiaService().tuChoi(maDoiTuong);
                            pheDuyetDAO.danhDauYeuCauTuChoi(
                                    PhienDangNhapService.macDinh().layMaNhanVien(),
                                    lyDo,
                                    yeuCau.id);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Từ chối", yeuCau.id + " / " + lyDo);
                            return null;
                        });
        yeuCau.trangThai = TrangThaiPheDuyet.TU_CHOI;
        yeuCau.daQuyetDinhTheo = nguoiDuyet;
        yeuCau.tuChoiLyDo = lyDo;
        thongBao("TU_CHOI:" + yeuCau.id);
    }

    public static void themBoLangNghe(BoLangNgheSuKien<String> boLangNghe) {
        if (boLangNghe != null && !CAC_BO_LANG_NGHE.contains(boLangNghe)) {
            CAC_BO_LANG_NGHE.add(boLangNghe);
        }
    }

    public static void xoaBoLangNghe(BoLangNgheSuKien<String> boLangNghe) {
        CAC_BO_LANG_NGHE.remove(boLangNghe);
    }

    private static void thongBao(String suKien) {
        for (BoLangNgheSuKien<String> boLangNghe : CAC_BO_LANG_NGHE) {
            boLangNghe.khiThayDoi(suKien);
        }
    }

    public void layHoaDonYeuCau(String hoaDon, boolean huy, String lyDo, String thayDoi) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        DanhMucService.macDinh().xacNhanTonTai("hoa_don", "ma_hoa_don", hoaDon);
        if (lyDo == null || lyDo.isBlank() || (!huy && (thayDoi == null || thayDoi.isBlank())))
            throw new IllegalArgumentException("Cần lý do và nội dung cần sửa.");
        guiThaoTac(
                PheDuyetService.Loai.SUA_HOA_DON,
                lyDo,
                hoaDon,
                huy ? "Hủy hóa đơn" : thayDoi,
                huy ? "HUY_HOA_DON" : "GHI_CHU_HOA_DON",
                hoaDon,
                thayDoi);
    }

    public void apDungPheDuyet(String thaoTac, String maDich, String noiDungXuLy) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            switch (thaoTac) {
                                case "DOI_GIA", "SAN_PHAM", "DE_XUAT_GIA" ->
                                        new DeXuatGiaService().apDung(maDich);
                                case "XUAT_HUY", "PHIEU_XUAT_KHO" ->
                                        new NhapXuatKhoService().apDungXuatHuy(maDich);
                                case "KIEM_KE", "BIEN_BAN_KIEM_KE" ->
                                        new KiemKeService().apDungKiemKe(maDich);
                                case "DOI_TRA", "PHIEU_DOI_TRA" ->
                                        new DoiTraService().apDungDoiTra(maDich);
                                case "NGHI_PHEP", "LICH_LAM_VIEC" ->
                                        LichLamViecService.macDinh()
                                                .danhDauNghiPhepDaDuyet(
                                                        maDich, LocalDate.parse(noiDungXuLy));
                                case "GHI_CHU_HOA_DON", "HOA_DON" -> {
                                    if (pheDuyetDAO.demApDungPheDuyet(maDich) == 0)
                                        throw new IllegalArgumentException(
                                                "Hóa đơn không còn hợp lệ.");
                                    NhatKyHeThongService.macDinh()
                                            .ghiNhatKy(
                                                    "Sửa ghi chú hóa đơn",
                                                    maDich + ": " + noiDungXuLy);
                                    pheDuyetDAO.capNhatApDungPheDuyet(noiDungXuLy, maDich);
                                }
                                case "HUY_HOA_DON" -> new BanHangService().huyHoaDon(maDich);
                                default ->
                                        throw new IllegalArgumentException(
                                                "Loại phê duyệt không được hỗ trợ.");
                            }
                            return null;
                        });
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }

    public void yeuCauBoSung(String ma, String noiDung) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        if (noiDung == null || noiDung.isBlank()) {
            throw new IllegalArgumentException("Cần nhập nội dung yêu cầu bổ sung.");
        }
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            int soDong =
                                    pheDuyetDAO.yeuCauBoSung(
                                            PhienDangNhapService.macDinh().layMaNhanVien(),
                                            noiDung.trim(),
                                            ma);
                            if (soDong != 1) {
                                throw new IllegalStateException(
                                        "Yêu cầu không còn ở trạng thái chờ duyệt.");
                            }
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Yêu cầu bổ sung", ma + " / " + noiDung);
                            return null;
                        });
        thongBao("BO_SUNG:" + ma);
    }

    public Optional<YeuCauPheDuyet> timTheoMa(String maYeuCau) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.XU_LY_PHE_DUYET);
        return pheDuyetDAO.timTheoMa(maYeuCau);
    }
}
