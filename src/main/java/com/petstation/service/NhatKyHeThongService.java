package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcQuanLyNhatKyHeThongDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Ghi và tra cứu nhật ký hệ thống theo người dùng, sự kiện và kết quả. */
public final class NhatKyHeThongService {
    /**
     * Điều phối nghiệp vụ và kiểm tra dữ liệu cho mục.
     *
     * @param thoiGian thời gian
     * @param nguoiDung người dùng
     * @param thaoTac thao tác
     * @param doiTuong đối tượng
     * @param kieu kiểu
     * @param ketQua kết quả
     */
    public record Muc(
            LocalDateTime thoiGian,
            String nguoiDung,
            String thaoTac,
            String doiTuong,
            String kieu,
            String ketQua) {
        /**
         * Lấy văn bản thời gian trong Muc.
         *
         * @return kết quả lấy văn bản thời gian
         */
        public String layVanBanThoiGian() {
            return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"));
        }
    }

    /** Các bộ lắng nghe. */
    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    /** Người dùng. */
    private static String nguoiDung = "Hệ thống";

    /** Khởi tạo NhatKyHeThongService với các giá trị được cung cấp. */
    private NhatKyHeThongService() {}

    /**
     * Đặt người dùng trong NhatKyHeThongService.
     *
     * @param ten tên
     */
    public static void datNguoiDung(String ten) {
        nguoiDung = ten == null ? "Hệ thống" : ten;
    }

    /**
     * Lấy người dùng trong NhatKyHeThongService.
     *
     * @return kết quả lấy người dùng
     */
    public static String layNguoiDung() {
        return layPhienHienTai() == null ? nguoiDung : layPhien().layTenHienThi();
    }

    /**
     * Ghi sự kiện vào nhật ký hệ thống trong NhatKyHeThongService.
     *
     * @param thaoTac thao tác
     * @param doiTuong đối tượng
     */
    public static void ghiNhatKy(String thaoTac, String doiTuong) {
        ghiNhatKy(layKieuCua(thaoTac), thaoTac, doiTuong, "Thành công");
    }

    /**
     * Ghi sự kiện vào nhật ký hệ thống trong NhatKyHeThongService.
     *
     * @param kieu kiểu
     * @param thaoTac thao tác
     * @param doiTuong đối tượng
     * @param ketQua kết quả
     */
    public static void ghiNhatKy(String kieu, String thaoTac, String doiTuong, String ketQua) {
        JdbcQuanLyNhatKyHeThongDAO.capNhatNhatKy(
                layPhienHienTai() == null ? null : layIdTaiKhoan(),
                thaoTac,
                doiTuong,
                layNguoiDung(),
                kieu,
                ketQua);
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    /**
     * Lấy kiểu của trong NhatKyHeThongService.
     *
     * @param thaoTac thao tác
     * @return kết quả lấy kiểu của
     */
    private static String layKieuCua(String thaoTac) {
        // Giá trị đầu.
        String giaTriDau = thaoTac.toLowerCase();
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.contains("đăng nhập") || giaTriDau.contains("đăng xuất")) return "LOGIN";
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.startsWith("thêm")
                || giaTriDau.startsWith("tạo")
                || giaTriDau.startsWith("lập")) return "CREATE";
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.startsWith("sửa")
                || giaTriDau.startsWith("cập nhật")
                || giaTriDau.startsWith("khóa")
                || giaTriDau.startsWith("mở khóa")
                || giaTriDau.startsWith("đặt lại")) return "UPDATE";
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.startsWith("xóa")
                || giaTriDau.startsWith("ngừng")
                || giaTriDau.startsWith("hủy")) return "DELETE";
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.startsWith("duyệt")
                || giaTriDau.startsWith("từ chối")
                || giaTriDau.startsWith("gửi")) return "APPROVAL";
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.startsWith("thanh toán") || giaTriDau.startsWith("đổi trả")) return "PAYMENT";
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (giaTriDau.startsWith("nhập kho")
                || giaTriDau.startsWith("xuất")
                || giaTriDau.startsWith("kiểm kê")) return "INVENTORY";
        return "SYSTEM";
    }

    /**
     * Lấy các mục trong NhatKyHeThongService.
     *
     * @return kết quả lấy các mục
     */
    public static List<Muc> layCacMuc() {
        // Kết quả.
        List<Muc> ketQua = new ArrayList<>();
        for (Object[] dongDuLieu : JdbcQuanLyNhatKyHeThongDAO.timCacMuc())
            ketQua.add(
                    new Muc(
                            ((java.sql.Timestamp) dongDuLieu[0]).toLocalDateTime(),
                            chuyenThanhChuoi(dongDuLieu[1]),
                            chuyenThanhChuoi(dongDuLieu[2]),
                            chuyenThanhChuoi(dongDuLieu[3]),
                            chuyenThanhChuoi(dongDuLieu[4]),
                            chuyenThanhChuoi(dongDuLieu[5])));
        return ketQua;
    }

    /**
     * Thêm bộ lắng nghe trong NhatKyHeThongService.
     *
     * @param boLangNghe bộ lắng nghe
     */
    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }
}
