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

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho phê duyệt dịch vụ. */
public final class PheDuyetService {
    private static final YeuCauPheDuyetDAO pheDuyetDAO = new YeuCauPheDuyetDAOImpl();

    /** Điều phối nghiệp vụ và kiểm tra dữ liệu cho loại. */
    public enum Loai {
        SUA_HOA_DON("Sửa/hủy hóa đơn"),
        DOI_TRA("Đổi trả vượt hạn mức"),
        XUAT_HUY("Xuất hủy giá trị lớn"),
        KIEM_KE("Điều chỉnh kiểm kê"),
        NGHI_PHEP("Nghỉ phép"),
        GIA_BAN("Đề xuất giá bán");

        /** Nhãn. */
        public final String nhan;

        Loai(String nhan) {
            this.nhan = nhan;
        }

        public static Loai docTuCSDL(String ma) {
            if (ma == null || ma.isBlank()) return SUA_HOA_DON;
            return switch (ma.trim()) {
                case "INVOICE", "SUA_HOA_DON" -> SUA_HOA_DON;
                case "RETURN", "DOI_TRA" -> DOI_TRA;
                case "WRITE_OFF", "XUAT_HUY" -> XUAT_HUY;
                case "STOCKTAKE", "KIEM_KE" -> KIEM_KE;
                case "LEAVE", "NGHI_PHEP" -> NGHI_PHEP;
                case "PRICE", "GIA_BAN" -> GIA_BAN;
                default -> Loai.valueOf(ma.trim());
            };
        }
    }

    /** Điều phối nghiệp vụ và kiểm tra dữ liệu cho yêu cầu. */
    public static final class YeuCau {
        /** Id. */
        public final String id;

        /** Loại. */
        public final Loai loai;

        /** Người gửi. */
        public final String nguoiGui;

        /** Thời gian. */
        public final LocalDateTime thoiGian;

        /** Lý do. */
        public final String lyDo, truoc, sau;

        /** Trạng thái. */
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
                        case "APPROVED", "DA_DUYET" -> "Đã duyệt";
                        case "REJECTED", "TU_CHOI" -> "Từ chối";
                        default -> "Chờ duyệt";
                    };
            daQuyetDinhTheo = chuyenThanhChuoi(dongDuLieu[8]);
            tuChoiLyDo = chuyenThanhChuoi(dongDuLieu[9]);
        }

        public String layVanBanThoiGian() {
            return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        }
    }

    /** Các bộ lắng nghe. */
    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    /** Đã chọn hóa đơn. */
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
        // Quyền.
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
                    case SUA_HOA_DON -> Set.of("INVOICE_NOTE", "CANCEL_INVOICE").contains(thaoTac);
                    case DOI_TRA -> false;
                    case XUAT_HUY -> "WRITE_OFF".equals(thaoTac);
                    case KIEM_KE -> "STOCKTAKE".equals(thaoTac);
                    case NGHI_PHEP -> "LEAVE".equals(thaoTac);
                    case GIA_BAN -> "PRICE_CHANGE".equals(thaoTac);
                };
        if (!dungThaoTac || dich <= 0)
            throw new IllegalArgumentException("Thao tác không phù hợp với loại phê duyệt.");
        if (lyDo == null || lyDo.isBlank()) throw new IllegalArgumentException("Cần nhập lý do.");
        // Mã.
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
                    // Dòng dữ liệu.
                    Object[] dongDuLieu = cacDong.get(0);
                    PheDuyetService.apDungPheDuyet(
                            chuyenThanhChuoi(dongDuLieu[0]),
                            Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])),
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
        if (lyDo.isBlank()) throw new IllegalArgumentException("Cần lý do từ chối.");
        thucHienGiaoDich(
                ketNoi -> {
                    List<Object[]> cacDong = pheDuyetDAO.timTuChoi(yeuCau.id);
                    if (cacDong.isEmpty()) throw new IllegalArgumentException("Yêu cầu đã xử lý.");
                    // Dòng dữ liệu.
                    Object[] dongDuLieu = cacDong.get(0);
                    if ("RETURN".equals(dongDuLieu[0]))
                        pheDuyetDAO.tuChoiPhieuDoiTra(
                                Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    if ("WRITE_OFF".equals(dongDuLieu[0]))
                        pheDuyetDAO.capNhatTuChoi(Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    if ("STOCKTAKE".equals(dongDuLieu[0]))
                        pheDuyetDAO.tuChoiBienBanKiemKe(
                                Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    if ("PRICE_CHANGE".equals(dongDuLieu[0]))
                        DeXuatGiaService.tuChoi(Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
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
        // Id.
        long id = layId("hoa_don", "so_hoa_don", hoaDon);
        if (lyDo.isBlank() || !huy && thayDoi.isBlank())
            throw new IllegalArgumentException("Cần lý do và nội dung cần sửa.");
        PheDuyetService.guiThaoTac(
                PheDuyetService.Loai.SUA_HOA_DON,
                lyDo,
                hoaDon,
                huy ? "Hủy hóa đơn" : thayDoi,
                huy ? "CANCEL_INVOICE" : "INVOICE_NOTE",
                id,
                thayDoi);
    }

    public static void apDungPheDuyet(String thaoTac, long dich, String noiDungXuLy) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);
        switch (thaoTac) {
            case "PRICE_CHANGE" -> DeXuatGiaService.apDung(dich);
            case "WRITE_OFF" -> KhoService.apDungXuatHuy(dich);
            case "STOCKTAKE" -> KhoService.apDungKiemKe(dich);
            case "RETURN" -> DoiTraService.apDungDoiTra(dich);
            case "LEAVE" ->
                    com.petstation.service.LichLamViecService.danhDauNghiPhepDaDuyet(
                            dich, LocalDate.parse(noiDungXuLy));
            case "INVOICE_NOTE" -> {
                if (pheDuyetDAO.demApDungPheDuyet(dich) == 0)
                    throw new IllegalArgumentException("Hóa đơn không còn hợp lệ.");
                NhatKyHeThongService.ghiNhatKy("Sửa ghi chú hóa đơn", dich + ": " + noiDungXuLy);
                pheDuyetDAO.capNhatApDungPheDuyet(noiDungXuLy, dich);
            }
            case "CANCEL_INVOICE" -> BanHangService.huyHoaDon(dich);
            default -> throw new IllegalArgumentException("Loại phê duyệt không được hỗ trợ.");
        }
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }
}
