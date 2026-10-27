package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.PheDuyetDAO;
import com.petstation.dao.impl.PheDuyetImpl;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho phê duyệt dịch vụ. */
public final class PheDuyetService {
    private static final PheDuyetDAO pheDuyetDAO = new PheDuyetImpl();

    /** Điều phối nghiệp vụ và kiểm tra dữ liệu cho loại. */
    public enum Loai {
        INVOICE("Sửa/hủy hóa đơn"),
        RETURN("Đổi trả vượt hạn mức"),
        WRITE_OFF("Xuất hủy giá trị lớn"),
        STOCKTAKE("Điều chỉnh kiểm kê"),
        LEAVE("Nghỉ phép"),
        PRICE("Đề xuất giá bán");

        /** Nhãn. */
        public final String nhan;

        Loai(String nhan) {
            this.nhan = nhan;
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
            loai = Loai.valueOf(chuyenThanhChuoi(dongDuLieu[1]));
            nguoiGui = chuyenThanhChuoi(dongDuLieu[2]);
            thoiGian = ((java.sql.Timestamp) dongDuLieu[3]).toLocalDateTime();
            lyDo = chuyenThanhChuoi(dongDuLieu[4]);
            truoc = chuyenThanhChuoi(dongDuLieu[5]);
            sau = chuyenThanhChuoi(dongDuLieu[6]);
            trangThai =
                    switch (chuyenThanhChuoi(dongDuLieu[7])) {
                        case "APPROVED" -> "Đã duyệt";
                        case "REJECTED" -> "Từ chối";
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
                    case INVOICE, RETURN -> Quyen.POS_SELL;
                    case WRITE_OFF -> Quyen.INVENTORY_ISSUE;
                    case STOCKTAKE -> Quyen.INVENTORY_COUNT;
                    case LEAVE -> Quyen.SCHEDULE_VIEW;
                    case PRICE -> Quyen.PRICE_PROPOSE;
                };
        yeuCau(quyen);
        boolean dungThaoTac =
                switch (loai) {
                    case INVOICE -> Set.of("INVOICE_NOTE", "CANCEL_INVOICE").contains(thaoTac);
                    case RETURN -> false;
                    case WRITE_OFF -> "WRITE_OFF".equals(thaoTac);
                    case STOCKTAKE -> "STOCKTAKE".equals(thaoTac);
                    case LEAVE -> "LEAVE".equals(thaoTac);
                    case PRICE -> "PRICE_CHANGE".equals(thaoTac);
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
        yeuCau(Quyen.APPROVAL_PROCESS);
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
        yeuCau(Quyen.APPROVAL_PROCESS);
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
        yeuCau(Quyen.POS_SELL);
        // Id.
        long id = layId("hoa_don", "so_hoa_don", hoaDon);
        if (lyDo.isBlank() || !huy && thayDoi.isBlank())
            throw new IllegalArgumentException("Cần lý do và nội dung cần sửa.");
        PheDuyetService.guiThaoTac(
                PheDuyetService.Loai.INVOICE,
                lyDo,
                hoaDon,
                huy ? "Hủy hóa đơn" : thayDoi,
                huy ? "CANCEL_INVOICE" : "INVOICE_NOTE",
                id,
                thayDoi);
    }

    public static void apDungPheDuyet(String thaoTac, long dich, String noiDungXuLy) {
        yeuCau(Quyen.APPROVAL_PROCESS);
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
