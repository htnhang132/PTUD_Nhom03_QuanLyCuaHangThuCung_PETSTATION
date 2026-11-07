package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.dao.impl.NhatKyHeThongDAOImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class NhatKyHeThongService {
    private static final NhatKyHeThongDAO nhatKyHeThongDAO = new NhatKyHeThongDAOImpl();

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

    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    private static String nguoiDung = "Hệ thống";

    private NhatKyHeThongService() {}

    public static void datNguoiDung(String ten) {
        nguoiDung = ten == null ? "Hệ thống" : ten;
    }

    public static String layNguoiDung() {
        return layPhienHienTai() == null ? nguoiDung : layPhien().getTenHienThi();
    }

    public static void ghiThayDoi(
            java.sql.Connection ketNoi,
            String thaoTac,
            String loaiDoiTuong,
            String maDoiTuong,
            String giaTriCu,
            String giaTriMoi) throws Exception {
        com.petstation.model.TaiKhoan tk = layPhienHienTai() != null ? layPhien().getTaiKhoan() : (layTenDangNhap() != null ? new com.petstation.model.TaiKhoan(layTenDangNhap()) : null);
        nhatKyHeThongDAO.them(
                ketNoi,
                new com.petstation.model.NhatKyHeThong(
                        com.petstation.util.DuLieu.taoMa("SYS"),
                        tk,
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
                layPhienHienTai() == null ? null : layTenDangNhap(),
                thaoTac,
                doiTuong,
                layNguoiDung(),
                kieu,
                ketQua);
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    private static String layKieuCua(String thaoTac) {
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
        for (Object[] dongDuLieu : nhatKyHeThongDAO.timCacMuc()) {
            String kieuRaw = chuyenThanhChuoi(dongDuLieu[4]);
            String kieuChuan =
                    switch (kieuRaw.toUpperCase(Locale.ROOT)) {
                        case "DANG_NHAP", "DANG_XUAT" -> "LOGIN";
                        case "BAN_HANG", "THANH_TOAN", "DOI_TRA" -> "PAYMENT";
                        case "NHAP_KHO", "XUAT_KHO", "KIEM_KE" -> "INVENTORY";
                        case "SPA", "HOTEL", "THEM_MOI" -> "CREATE";
                        case "MO_CA", "DONG_CA" -> "SYSTEM";
                        default -> kieuRaw;
                    };
            String ketQuaRaw = chuyenThanhChuoi(dongDuLieu[5]);
            String ketQuaChuan =
                    switch (ketQuaRaw.toUpperCase(Locale.ROOT)) {
                        case "THANH_CONG", "SUCCESS" -> "Thành công";
                        case "THAT_BAI", "FAILED", "ERROR" -> "Thất bại";
                        case "CANH_BAO", "WARNING" -> "Cảnh báo";
                        default -> ketQuaRaw;
                    };
            ketQua.add(
                    new Muc(
                            ((java.sql.Timestamp) dongDuLieu[0]).toLocalDateTime(),
                            chuyenThanhChuoi(dongDuLieu[1]),
                            chuyenThanhChuoi(dongDuLieu[2]),
                            chuyenThanhChuoi(dongDuLieu[3]),
                            kieuChuan,
                            ketQuaChuan));
        }
        return ketQua;
    }

    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }
}
