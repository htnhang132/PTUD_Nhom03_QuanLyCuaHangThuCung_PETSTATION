package com.petstation.service.hethong;

import com.petstation.dao.hethong.NhatKyHeThongDAO;
import com.petstation.dao.hethong.impl.NhatKyHeThongDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.NhatKyHeThong;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.util.DuLieu;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NhatKyHeThongService {
    private static final NhatKyHeThongService MAC_DINH = new NhatKyHeThongService();

    private final NhatKyHeThongDAO nhatKyHeThongDAO;
    private final PhienDangNhapService phienService;
    private final List<BoLangNgheSuKien<String>> cacBoLangNghe = new CopyOnWriteArrayList<>();

    public NhatKyHeThongService() {
        this(new NhatKyHeThongDAOImpl(), PhienDangNhapService.macDinh());
    }

    public NhatKyHeThongService(NhatKyHeThongDAO nhatKyHeThongDAO) {
        this(nhatKyHeThongDAO, PhienDangNhapService.macDinh());
    }

    public NhatKyHeThongService(
            NhatKyHeThongDAO nhatKyHeThongDAO, PhienDangNhapService phienService) {
        this.nhatKyHeThongDAO = Objects.requireNonNull(nhatKyHeThongDAO);
        this.phienService = Objects.requireNonNull(phienService);
    }

    public static NhatKyHeThongService macDinh() {
        return MAC_DINH;
    }

    public String layNguoiDung() {
        return phienService.layPhienHienTai() == null
                ? "Hệ thống"
                : phienService.layPhien().getTenHienThi();
    }

    public void ghiThayDoi(
            Connection ketNoi,
            String thaoTac,
            String loaiDoiTuong,
            String maDoiTuong,
            String ghiChu)
            throws Exception {
        NhanVien nv =
                phienService.layPhienHienTai() != null
                        ? phienService.layPhien().getNhanVien()
                        : null;
        nhatKyHeThongDAO.them(
                ketNoi,
                new NhatKyHeThong(
                        DuLieu.taoMa("LOG"),
                        nv,
                        thaoTac,
                        loaiDoiTuong,
                        maDoiTuong,
                        ghiChu == null || ghiChu.isBlank() ? "Thay đổi dữ liệu danh mục" : ghiChu,
                        layKieuCua(thaoTac, loaiDoiTuong + " " + maDoiTuong),
                        "Thành công",
                        LocalDateTime.now()));
    }

    public void ghiNhatKy(String thaoTac, String doiTuong) {
        ghiNhatKy(layKieuCua(thaoTac, doiTuong), thaoTac, doiTuong, "Thành công");
    }

    public void ghiNhatKy(String kieu, String thaoTac, String doiTuong, String ketQua) {
        nhatKyHeThongDAO.capNhatNhatKy(
                phienService.layPhienHienTai() == null ? null : phienService.layMaNhanVien(),
                thaoTac,
                doiTuong,
                kieu,
                ketQua);
        QuanLyGiaoDich.sauKhiCommit(
                () -> {
                    for (BoLangNgheSuKien<String> boLangNghe : cacBoLangNghe) {
                        boLangNghe.khiThayDoi(thaoTac);
                    }
                });
    }

    private static String layKieuCua(String thaoTac, String doiTuong) {
        String t = thaoTac == null ? "" : thaoTac.trim().toLowerCase(Locale.ROOT);
        String d = doiTuong == null ? "" : doiTuong.trim().toLowerCase(Locale.ROOT);
        String td = t + " " + d;
        if (t.contains("đăng nhập")
                || t.contains("đăng xuất")
                || t.contains("mật khẩu")
                || td.contains("tài khoản")) return "LOGIN";
        if (t.startsWith("duyệt")
                || t.startsWith("từ chối")
                || t.startsWith("gửi")
                || t.startsWith("yêu cầu bổ sung")
                || t.contains("giá đề xuất")) return "APPROVAL";
        if (t.contains("kho")
                || t.startsWith("kiểm kê")
                || t.contains("công nợ")
                || t.contains("barcode")
                || td.contains("xuất hủy")
                || td.contains("phiếu nhập")) return "INVENTORY";
        if (td.contains("spa")
                || td.contains("hotel")
                || td.contains("lưu trú")
                || t.contains("lịch hẹn")
                || t.contains("nhận phòng")
                || t.contains("trả phòng")
                || t.contains("cập nhật phòng")
                || t.contains("chăm sóc")
                || t.contains("tiêm phòng")) return "SERVICE";
        if (t.contains("thanh toán")
                || t.contains("hóa đơn")
                || t.contains("đổi")
                || t.equals("mở ca")
                || t.equals("kết ca")
                || t.contains("ca thu ngân")) return "PAYMENT";
        if (d.contains("cài đặt hệ thống")) return "SYSTEM";
        if (t.startsWith("thêm")
                || t.startsWith("tạo")
                || t.startsWith("lập")
                || t.startsWith("sửa")
                || t.startsWith("cập nhật")
                || t.startsWith("khóa")
                || t.startsWith("mở khóa")
                || t.startsWith("xóa")
                || t.startsWith("ngừng")
                || t.startsWith("hủy")
                || t.contains("phân lịch")
                || t.contains("in lịch")
                || td.contains("tích điểm")) return "CATALOG";
        return "SYSTEM";
    }

    public List<DongNhatKyHeThong> layCacMuc() {
        return layCacMuc(null, null);
    }

    public List<DongNhatKyHeThong> layCacMuc(LocalDate tuNgay, LocalDate denNgay) {
        phienService.yeuCau(Quyen.XEM_NHAT_KY);
        List<DongNhatKyHeThong> ketQua = new ArrayList<>();
        for (DongNhatKyHeThong dongDuLieu : nhatKyHeThongDAO.timCacMuc(tuNgay, denNgay)) {
            String thaoTacChuan = chuanHoaThaoTacHienThi(dongDuLieu.thaoTac());
            String doiTuongChuan =
                    "Đăng nhập".equals(thaoTacChuan)
                                    && "Đăng nhập thành công"
                                            .equalsIgnoreCase(dongDuLieu.doiTuong())
                            ? "Hệ thống"
                            : dongDuLieu.doiTuong();
            String kieuChuan = chuanHoaKieuSuKien(dongDuLieu, thaoTacChuan);
            String ketQuaChuan = chuanHoaKetQuaHienThi(dongDuLieu.ketQua());
            ketQua.add(
                    new DongNhatKyHeThong(
                            dongDuLieu.nguoiDung(),
                            dongDuLieu.thoiGian(),
                            thaoTacChuan,
                            doiTuongChuan,
                            kieuChuan,
                            ketQuaChuan,
                            dongDuLieu.loaiDoiTuong(),
                            dongDuLieu.maDoiTuong(),
                            dongDuLieu.ghiChu()));
        }
        return ketQua;
    }

    private static String chuanHoaKieuSuKien(DongNhatKyHeThong dongDuLieu, String thaoTacChuan) {
        String kieuGoc =
                dongDuLieu.kieu() == null
                        ? "SYSTEM"
                        : dongDuLieu.kieu().trim().toUpperCase(Locale.ROOT);
        String kieu =
                switch (kieuGoc) {
                    case "AUTH", "DANG_NHAP", "DANG_XUAT" -> "LOGIN";
                    case "BAN_HANG", "THANH_TOAN", "DOI_TRA", "MO_CA", "DONG_CA" -> "PAYMENT";
                    case "NHAP_KHO", "XUAT_KHO", "KIEM_KE" -> "INVENTORY";
                    case "SPA", "HOTEL" -> "SERVICE";
                    default -> kieuGoc;
                };
        if ("SYSTEM".equals(kieu)
                || "CREATE".equals(kieu)
                || "UPDATE".equals(kieu)
                || "DELETE".equals(kieu)
                || "THEM_MOI".equals(kieu)) {
            String nguCanh = (dongDuLieu.doiTuong() + " " + dongDuLieu.loaiDoiTuong()).trim();
            String suyLuan = layKieuCua(thaoTacChuan, nguCanh);
            if (!"SYSTEM".equals(suyLuan)) return suyLuan;
            if (!"SYSTEM".equals(kieu)) return "CATALOG";
        }
        return kieu;
    }

    private static String chuanHoaKetQuaHienThi(String ketQuaRaw) {
        if (ketQuaRaw == null) return "";
        return switch (ketQuaRaw.trim().toUpperCase(Locale.ROOT)) {
            case "THANH_CONG", "SUCCESS" -> "Thành công";
            case "THAT_BAI", "FAILED", "ERROR" -> "Thất bại";
            case "CANH_BAO", "WARNING" -> "Cảnh báo";
            default -> ketQuaRaw;
        };
    }

    private static String chuanHoaThaoTacHienThi(String thaoTac) {
        if (thaoTac == null) return "";
        return switch (thaoTac.trim().toUpperCase(Locale.ROOT)) {
            case "DANG_NHAP_THANH_CONG", "DANG_NHAP" -> "Đăng nhập";
            case "DANG_NHAP_THAT_BAI" -> "Đăng nhập thất bại";
            case "TU_CHOI_DANG_NHAP" -> "Từ chối đăng nhập";
            case "DANG_XUAT" -> "Đăng xuất";
            default -> thaoTac;
        };
    }

    public void themBoLangNghe(BoLangNgheSuKien<String> boLangNghe) {
        if (boLangNghe != null && !cacBoLangNghe.contains(boLangNghe)) {
            cacBoLangNghe.add(boLangNghe);
        }
    }

    public void xoaBoLangNghe(BoLangNgheSuKien<String> boLangNghe) {
        cacBoLangNghe.remove(boLangNghe);
    }

    public void xoaBoLangNghe() {
        cacBoLangNghe.clear();
    }
}
