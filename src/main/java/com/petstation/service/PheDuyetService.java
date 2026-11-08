package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.YeuCauPheDuyetDAO;
import com.petstation.dao.impl.YeuCauPheDuyetDAOImpl;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class PheDuyetService {
    private static YeuCauPheDuyetDAO pheDuyetDAO = new YeuCauPheDuyetDAOImpl();

    public static void cauHinhDAO(YeuCauPheDuyetDAO dao) {
        if (dao != null) pheDuyetDAO = dao;
    }

    public enum Loai {
        SUA_HOA_DON("Sửa/hủy hóa đơn"),
        DOI_TRA("Đổi trả vượt hạn mức"),
        XUAT_HUY("Xuất hủy giá trị lớn"),
        KIEM_KE("Điều chỉnh kiểm kê"),
        NGHI_PHEP("Nghỉ phép"),
        GIA_BAN("Đề xuất giá bán");

        public final String nhan;

        Loai(String nhan) {
            this.nhan = nhan;
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
        public final String id;

        public final Loai loai;

        public final String nguoiGui;

        public final LocalDateTime thoiGian;

        public final String lyDo, truoc, sau;

        public String trangThai, daQuyetDinhTheo, tuChoiLyDo;

        YeuCau(Object[] dongDuLieu) {
            id = chuyenThanhChuoi(dongDuLieu[0]);
            loai = Loai.docTuCSDL(chuyenThanhChuoi(dongDuLieu[1]));
            nguoiGui = chuyenThanhChuoi(dongDuLieu[2]);
            thoiGian = ((java.sql.Timestamp) dongDuLieu[3]).toLocalDateTime();
            lyDo = chuyenThanhChuoi(dongDuLieu[4]);
            truoc = chuyenThanhChuoi(dongDuLieu[5]);
            sau = chuyenThanhChuoi(dongDuLieu[6]);
            trangThai =
                    switch (chuyenThanhChuoi(dongDuLieu[7])) {
	                    case "DA_DUYET" -> "Đã duyệt";
	                    case "TU_CHOI" -> "Từ chối";
	                    case "CAN_THEM_THONG_TIN", "CHO_BO_SUNG" -> "Chờ bổ sung";
	                    case "DA_HUY" -> "Đã hủy";
	                    case "CHO_DUYET" -> "Chờ duyệt";
	                    default -> "Trạng thái không xác định";
                    };
            daQuyetDinhTheo = chuyenThanhChuoi(dongDuLieu[8]);
            tuChoiLyDo = chuyenThanhChuoi(dongDuLieu[9]);
        }

        public String layVanBanThoiGian() {
            return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        }
    }

    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    public static String daChonHoaDon = "";

    private PheDuyetService() {}

    public static YeuCau guiThaoTac(
            Loai loai,
            String lyDo,
            String truoc,
            String sau,
            String thaoTac,
            long dich,
            String noiDungXuLy) {
        Quyen quyen =
                switch (loai) {
                    case SUA_HOA_DON, DOI_TRA -> Quyen.BAN_HANG;
                    case XUAT_HUY -> Quyen.XUAT_HUY_KHO;
                    case KIEM_KE -> Quyen.KIEM_KE_KHO;
                    case NGHI_PHEP -> Quyen.XEM_LICH_LAM_VIEC;
                    case GIA_BAN -> Quyen.DE_XUAT_GIA;
                };
        yeuCau(quyen);
        boolean dungThaoTac =
                switch (loai) {
                    case SUA_HOA_DON -> Set.of("GHI_CHU_HOA_DON", "HUY_HOA_DON").contains(thaoTac);
                    case DOI_TRA -> "DOI_TRA".equals(thaoTac);
                    case XUAT_HUY -> "XUAT_HUY".equals(thaoTac);
                    case KIEM_KE -> "KIEM_KE".equals(thaoTac);
                    case NGHI_PHEP -> "NGHI_PHEP".equals(thaoTac);
                    case GIA_BAN -> "DOI_GIA".equals(thaoTac);
                };
        if (!dungThaoTac || dich <= 0)
            throw new IllegalArgumentException("Thao tác không phù hợp với loại phê duyệt.");
        if (lyDo == null || lyDo.isBlank()) throw new IllegalArgumentException("Cần nhập lý do.");

        String ma = taoMa("YC");
        thucHienGiaoDich(
                ketNoi -> {
                    pheDuyetDAO.capNhatGuiThaoTac(
                            ma,
                            loai.name(),
                            layIdTaiKhoan(),
                            thaoTac,
                            "" + dich,
                            lyDo,
                            truoc,
                            sau,
                            noiDungXuLy);
                    NhatKyHeThongService.ghiNhatKy("Gửi phê duyệt", ma + " / " + loai.nhan);
                    return null;
                });
        thongBao();
        return layTatCa().stream()
                .filter(dongDuLieu -> dongDuLieu.id.equals(ma))
                .findFirst()
                .orElseThrow();
    }

    public static List<YeuCau> layTatCa() {
        List<YeuCau> ketQua = new ArrayList<>();
        for (Object[] dongDuLieu : pheDuyetDAO.timTatCa()) ketQua.add(new YeuCau(dongDuLieu));
        return ketQua;
    }

    public static long demChoDuyet() {
        return pheDuyetDAO.demSoLuongDangCho();
    }

    public static void duyet(YeuCau yeuCau, String nguoiDuyet) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);
        thucHienGiaoDich(
                ketNoi -> {
                    List<Object[]> cacDong = pheDuyetDAO.timDuyet(yeuCau.id);
                    if (cacDong.isEmpty())
                        throw new IllegalArgumentException("Yêu cầu đã được xử lý.");

                    Object[] dongDuLieu = cacDong.get(0);
                    PheDuyetService.apDungPheDuyet(
                            chuyenThanhChuoi(dongDuLieu[0]),
                            layIdCauNoi(null, chuyenThanhChuoi(dongDuLieu[1])),
                            chuyenThanhChuoi(dongDuLieu[2]));
                    pheDuyetDAO.capNhatDuyet(layIdTaiKhoan(), yeuCau.id);
                    NhatKyHeThongService.ghiNhatKy("Duyệt", yeuCau.id);
                    return null;
                });
        yeuCau.trangThai = "Đã duyệt";
        yeuCau.daQuyetDinhTheo = nguoiDuyet;
        thongBao();
    }

    public static void tuChoi(YeuCau yeuCau, String nguoiDuyet, String lyDo) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);
        if (lyDo == null || lyDo.isBlank()) throw new IllegalArgumentException("Cần lý do từ chối.");
        thucHienGiaoDich(
                ketNoi -> {
                    List<Object[]> cacDong = pheDuyetDAO.timTuChoi(yeuCau.id);
                    if (cacDong.isEmpty()) throw new IllegalArgumentException("Yêu cầu đã xử lý.");

                    Object[] dongDuLieu = cacDong.get(0);
                    long idDoiTuong = layIdCauNoi(null, chuyenThanhChuoi(dongDuLieu[1]));
                    if ("DOI_TRA".equals(dongDuLieu[0]) || "PHIEU_DOI_TRA".equals(dongDuLieu[0]))
                        pheDuyetDAO.tuChoiPhieuDoiTra(idDoiTuong);
                    if ("XUAT_HUY".equals(dongDuLieu[0]) || "PHIEU_XUAT_KHO".equals(dongDuLieu[0]))
                        pheDuyetDAO.capNhatTuChoi(idDoiTuong);
                    if ("KIEM_KE".equals(dongDuLieu[0]) || "BIEN_BAN_KIEM_KE".equals(dongDuLieu[0]))
                        pheDuyetDAO.tuChoiBienBanKiemKe(idDoiTuong);
                    if ("DOI_GIA".equals(dongDuLieu[0]) || "SAN_PHAM".equals(dongDuLieu[0]))
                        DeXuatGiaService.tuChoi(idDoiTuong);
                    pheDuyetDAO.danhDauYeuCauTuChoi(layIdTaiKhoan(), lyDo, yeuCau.id);
                    NhatKyHeThongService.ghiNhatKy("Từ chối", yeuCau.id + " / " + lyDo);
                    return null;
                });
        yeuCau.trangThai = "Từ chối";
        yeuCau.daQuyetDinhTheo = nguoiDuyet;
        yeuCau.tuChoiLyDo = lyDo;
        thongBao();
    }

    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    private static void thongBao() {
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    public static void layHoaDonYeuCau(String hoaDon, boolean huy, String lyDo, String thayDoi) {
        yeuCau(Quyen.BAN_HANG);

        long id = layId("hoa_don", "ma_hoa_don", hoaDon);
        if (lyDo == null || lyDo.isBlank() || (!huy && (thayDoi == null || thayDoi.isBlank())))
            throw new IllegalArgumentException("Cần lý do và nội dung cần sửa.");
        PheDuyetService.guiThaoTac(
                PheDuyetService.Loai.SUA_HOA_DON,
                lyDo,
                hoaDon,
                huy ? "Hủy hóa đơn" : thayDoi,
                huy ? "HUY_HOA_DON" : "GHI_CHU_HOA_DON",
                id,
                thayDoi);
    }

    public static void apDungPheDuyet(String thaoTac, long dich, String noiDungXuLy) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);
        switch (thaoTac) {
            case "DOI_GIA", "SAN_PHAM", "DE_XUAT_GIA" -> DeXuatGiaService.apDung(dich);
            case "XUAT_HUY", "PHIEU_XUAT_KHO" -> KhoService.apDungXuatHuy(dich);
            case "KIEM_KE", "BIEN_BAN_KIEM_KE" -> KhoService.apDungKiemKe(dich);
            case "DOI_TRA", "PHIEU_DOI_TRA" -> DoiTraService.apDungDoiTra(dich);
            case "NGHI_PHEP", "LICH_LAM_VIEC" ->
                    com.petstation.service.LichLamViecService.danhDauNghiPhepDaDuyet(
                            dich, LocalDate.parse(noiDungXuLy));
            case "GHI_CHU_HOA_DON", "HOA_DON" -> {
                if (pheDuyetDAO.demApDungPheDuyet(dich) == 0)
                    throw new IllegalArgumentException("Hóa đơn không còn hợp lệ.");
                NhatKyHeThongService.ghiNhatKy("Sửa ghi chú hóa đơn", dich + ": " + noiDungXuLy);
                pheDuyetDAO.capNhatApDungPheDuyet(noiDungXuLy, dich);
            }
            case "HUY_HOA_DON" -> BanHangService.huyHoaDon(dich);
            default -> throw new IllegalArgumentException("Loại phê duyệt không được hỗ trợ.");
        }
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }

    public static void yeuCauBoSung(String ma, String noiDung) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);

        if (noiDung == null || noiDung.isBlank()) {
            throw new IllegalArgumentException(
                    "Cần nhập nội dung yêu cầu bổ sung.");
        }

        thucHienGiaoDich(ketNoi -> {
            int soDong = pheDuyetDAO.yeuCauBoSung(
                    layIdTaiKhoan(), noiDung.trim(), ma);

            if (soDong != 1) {
                throw new IllegalStateException(
                        "Yêu cầu không còn ở trạng thái chờ duyệt.");
            }

            NhatKyHeThongService.ghiNhatKy(
                    "Yêu cầu bổ sung", ma + " / " + noiDung);
            return null;
        });

        thongBao();
    }
}
