package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcPheDuyetDAO;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho phê duyệt dịch vụ. */
public final class PheDuyetService {
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

        /**
         * Khởi tạo Loai với các giá trị được cung cấp.
         *
         * @param nhan nhãn
         */
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

        /**
         * Khởi tạo YeuCau với các giá trị được cung cấp.
         *
         * @param dongDuLieu dòng dữ liệu
         */
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

        /**
         * Lấy văn bản thời gian trong YeuCau.
         *
         * @return kết quả lấy văn bản thời gian
         */
        public String layVanBanThoiGian() {
            return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        }
    }

    /** Các bộ lắng nghe. */
    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    /** Đã chọn hóa đơn. */
    public static String daChonHoaDon = "";

    /** Khởi tạo PheDuyetService với các giá trị được cung cấp. */
    private PheDuyetService() {}

    /**
     * Gửi thao tác trong PheDuyetService.
     *
     * @param loai loại
     * @param lyDo lý do
     * @param truoc trước
     * @param sau sau
     * @param thaoTac thao tác
     * @param dich đích
     * @param noiDungXuLy nội dung xử lý
     * @return kết quả gửi thao tác
     */
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
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (lyDo == null || lyDo.isBlank()) throw new IllegalArgumentException("Cần nhập lý do.");
        // Mã.
        String ma = taoMa("YC");
        thucHienGiaoDich(
                ketNoi -> {
                    JdbcPheDuyetDAO.capNhatGuiThaoTac(
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

    /**
     * Lấy tất cả trong PheDuyetService.
     *
     * @return kết quả lấy tất cả
     */
    public static List<YeuCau> layTatCa() {
        // Kết quả.
        List<YeuCau> ketQua = new ArrayList<>();
        for (Object[] dongDuLieu : JdbcPheDuyetDAO.timTatCa()) ketQua.add(new YeuCau(dongDuLieu));
        return ketQua;
    }

    /**
     * Đếm chờ duyệt trong PheDuyetService.
     *
     * @return kết quả đếm chờ duyệt
     */
    public static long demChoDuyet() {
        return JdbcPheDuyetDAO.demSoLuongDangCho();
    }

    /**
     * Duyệt trong PheDuyetService.
     *
     * @param yeuCau yêu cầu
     * @param nguoiDuyet người duyệt
     */
    public static void duyet(YeuCau yeuCau, String nguoiDuyet) {
        yeuCau(Quyen.APPROVAL_PROCESS);
        thucHienGiaoDich(
                ketNoi -> {
                    // Các dòng.
                    List<Object[]> cacDong = JdbcPheDuyetDAO.timDuyet(yeuCau.id);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacDong.isEmpty())
                        throw new IllegalArgumentException("Yêu cầu đã được xử lý.");
                    // Dòng dữ liệu.
                    Object[] dongDuLieu = cacDong.get(0);
                    PheDuyetService.apDungPheDuyet(
                            chuyenThanhChuoi(dongDuLieu[0]),
                            Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])),
                            chuyenThanhChuoi(dongDuLieu[2]));
                    JdbcPheDuyetDAO.capNhatDuyet(layIdTaiKhoan(), yeuCau.id);
                    NhatKyHeThongService.ghiNhatKy("Duyệt", yeuCau.id);
                    return null;
                });
        yeuCau.trangThai = "Đã duyệt";
        yeuCau.daQuyetDinhTheo = nguoiDuyet;
        thongBao();
    }

    /**
     * Từ chối trong PheDuyetService.
     *
     * @param yeuCau yêu cầu
     * @param nguoiDuyet người duyệt
     * @param lyDo lý do
     */
    public static void tuChoi(YeuCau yeuCau, String nguoiDuyet, String lyDo) {
        yeuCau(Quyen.APPROVAL_PROCESS);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (lyDo.isBlank()) throw new IllegalArgumentException("Cần lý do từ chối.");
        thucHienGiaoDich(
                ketNoi -> {
                    // Các dòng.
                    List<Object[]> cacDong = JdbcPheDuyetDAO.timTuChoi(yeuCau.id);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacDong.isEmpty()) throw new IllegalArgumentException("Yêu cầu đã xử lý.");
                    // Dòng dữ liệu.
                    Object[] dongDuLieu = cacDong.get(0);
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if ("RETURN".equals(dongDuLieu[0]))
                        JdbcPheDuyetDAO.capNhatTuChoi3(
                                Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if ("WRITE_OFF".equals(dongDuLieu[0]))
                        JdbcPheDuyetDAO.capNhatTuChoi(
                                Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if ("STOCKTAKE".equals(dongDuLieu[0]))
                        JdbcPheDuyetDAO.capNhatTuChoi2(
                                Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    if ("PRICE_CHANGE".equals(dongDuLieu[0]))
                        DeXuatGiaService.tuChoi(Long.parseLong(chuyenThanhChuoi(dongDuLieu[1])));
                    JdbcPheDuyetDAO.capNhatTuChoi4(layIdTaiKhoan(), lyDo, yeuCau.id);
                    NhatKyHeThongService.ghiNhatKy("Từ chối", yeuCau.id + " / " + lyDo);
                    return null;
                });
        yeuCau.trangThai = "Từ chối";
        yeuCau.daQuyetDinhTheo = nguoiDuyet;
        yeuCau.tuChoiLyDo = lyDo;
        thongBao();
    }

    /**
     * Thêm bộ lắng nghe trong PheDuyetService.
     *
     * @param boLangNghe bộ lắng nghe
     */
    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    /** Thông báo trong PheDuyetService. */
    private static void thongBao() {
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    /**
     * Lấy hóa đơn yêu cầu trong PheDuyetService.
     *
     * @param hoaDon hóa đơn
     * @param huy hủy
     * @param lyDo lý do
     * @param thayDoi thay đổi
     */
    public static void layHoaDonYeuCau(String hoaDon, boolean huy, String lyDo, String thayDoi) {
        yeuCau(Quyen.POS_SELL);
        // Id.
        long id = layId("hoa_don", "so_hoa_don", hoaDon);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
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

    /**
     * Áp dụng phê duyệt trong PheDuyetService.
     *
     * @param thaoTac thao tác
     * @param dich đích
     * @param noiDungXuLy nội dung xử lý
     */
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
                // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                if (JdbcPheDuyetDAO.demApDungPheDuyet(dich) == 0)
                    throw new IllegalArgumentException("Hóa đơn không còn hợp lệ.");
                NhatKyHeThongService.ghiNhatKy("Sửa ghi chú hóa đơn", dich + ": " + noiDungXuLy);
                JdbcPheDuyetDAO.capNhatApDungPheDuyet(noiDungXuLy, dich);
            }
            case "CANCEL_INVOICE" -> BanHangService.huyHoaDon(dich);
            default -> throw new IllegalArgumentException("Loại phê duyệt không được hỗ trợ.");
        }
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }
}
