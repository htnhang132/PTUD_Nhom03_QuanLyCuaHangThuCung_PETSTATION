package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.dao.impl.NhatKyHeThongDAOImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Ghi và tra cứu nhật ký hệ thống theo người dùng, sự kiện và kết quả. */
public final class NhatKyHeThongService {
    private static final NhatKyHeThongDAO nhatKyHeThongDAO = new NhatKyHeThongDAOImpl();

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
        public String layVanBanThoiGian() {
            return thoiGian.format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"));
        }
    }

    /** Các bộ lắng nghe. */
    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    /** Người dùng. */
    private static String nguoiDung = "Hệ thống";

    private NhatKyHeThongService() {}

    public static void datNguoiDung(String ten) {
        nguoiDung = ten == null ? "Hệ thống" : ten;
    }

    public static String layNguoiDung() {
        return layPhienHienTai() == null ? nguoiDung : layPhien().layTenHienThi();
    }
    
    public static void ghiThayDoi(
            java.sql.Connection ketNoi,
            String thaoTac,
            String loaiDoiTuong,
            String maDoiTuong,
            String giaTriCu,
            String giaTriMoi) throws Exception {

        nhatKyHeThongDAO.them(
                ketNoi,
                new com.petstation.model.NhatKyHeThong(
                        0,
                        layIdTaiKhoan(),
                        thaoTac,
                        loaiDoiTuong,
                        maDoiTuong,
                        giaTriCu,
                        giaTriMoi,
                        "Thay đổi dữ liệu danh mục",
                        java.time.LocalDateTime.now()));
    }

    public static void ghiNhatKy(String thaoTac, String doiTuong) {
        ghiNhatKy(layKieuCua(thaoTac), thaoTac, doiTuong, "Thành công");
    }

    public static void ghiNhatKy(String kieu, String thaoTac, String doiTuong, String ketQua) {
        nhatKyHeThongDAO.capNhatNhatKy(
                layPhienHienTai() == null ? null : layIdTaiKhoan(),
                thaoTac,
                doiTuong,
                layNguoiDung(),
                kieu,
                ketQua);
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    private static String layKieuCua(String thaoTac) {
        // Giá trị đầu.
        String giaTriDau = thaoTac.toLowerCase();
        if (giaTriDau.contains("đăng nhập") || giaTriDau.contains("đăng xuất")) return "LOGIN";
        if (giaTriDau.startsWith("thêm")
                || giaTriDau.startsWith("tạo")
                || giaTriDau.startsWith("lập")) return "CREATE";
        if (giaTriDau.startsWith("sửa")
                || giaTriDau.startsWith("cập nhật")
                || giaTriDau.startsWith("khóa")
                || giaTriDau.startsWith("mở khóa")
                || giaTriDau.startsWith("đặt lại")) return "UPDATE";
        if (giaTriDau.startsWith("xóa")
                || giaTriDau.startsWith("ngừng")
                || giaTriDau.startsWith("hủy")) return "DELETE";
        if (giaTriDau.startsWith("duyệt")
                || giaTriDau.startsWith("từ chối")
                || giaTriDau.startsWith("gửi")) return "APPROVAL";
        if (giaTriDau.startsWith("thanh toán") || giaTriDau.startsWith("đổi trả")) return "PAYMENT";
        if (giaTriDau.startsWith("nhập kho")
                || giaTriDau.startsWith("xuất")
                || giaTriDau.startsWith("kiểm kê")) return "INVENTORY";
        return "SYSTEM";
    }

    public static List<Muc> layCacMuc() {
        List<Muc> ketQua = new ArrayList<>();
        for (Object[] dongDuLieu : nhatKyHeThongDAO.timCacMuc())
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

    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }
}
