package com.petstation.service.hethong;

import com.petstation.dao.hethong.NhatKyHeThongDAO;
import com.petstation.dao.hethong.impl.NhatKyHeThongDAOImpl;
import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.NhatKyHeThong;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.util.DuLieu;

import java.sql.Connection;
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
                        layKieuCua(thaoTac),
                        "Thành công",
                        LocalDateTime.now()));
    }

    public void ghiNhatKy(String thaoTac, String doiTuong) {
        ghiNhatKy(layKieuCua(thaoTac), thaoTac, doiTuong, "Thành công");
    }

    public void ghiNhatKy(String kieu, String thaoTac, String doiTuong, String ketQua) {
        nhatKyHeThongDAO.capNhatNhatKy(
                phienService.layPhienHienTai() == null ? null : phienService.layMaNhanVien(),
                thaoTac,
                doiTuong,
                kieu,
                ketQua);
        for (BoLangNgheSuKien<String> boLangNghe : cacBoLangNghe) {
            boLangNghe.khiThayDoi(thaoTac);
        }
    }

    private String layKieuCua(String thaoTac) {
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

    public List<DongNhatKyHeThong> layCacMuc() {
        phienService.yeuCau(Quyen.XEM_NHAT_KY);
        List<DongNhatKyHeThong> ketQua = new ArrayList<>();
        for (DongNhatKyHeThong dongDuLieu : nhatKyHeThongDAO.timCacMuc()) {
            String kieuRaw = dongDuLieu.kieu();
            String kieuChuan =
                    switch (kieuRaw.toUpperCase(Locale.ROOT)) {
                        case "DANG_NHAP", "DANG_XUAT" -> "LOGIN";
                        case "BAN_HANG", "THANH_TOAN", "DOI_TRA" -> "PAYMENT";
                        case "NHAP_KHO", "XUAT_KHO", "KIEM_KE" -> "INVENTORY";
                        case "SPA", "HOTEL", "THEM_MOI" -> "CREATE";
                        case "MO_CA", "DONG_CA" -> "SYSTEM";
                        default -> kieuRaw;
                    };
            String ketQuaRaw = dongDuLieu.ketQua();
            String ketQuaChuan =
                    switch (ketQuaRaw.toUpperCase(Locale.ROOT)) {
                        case "THANH_CONG", "SUCCESS" -> "Thành công";
                        case "THAT_BAI", "FAILED", "ERROR" -> "Thất bại";
                        case "CANH_BAO", "WARNING" -> "Cảnh báo";
                        default -> ketQuaRaw;
                    };
            ketQua.add(
                    new DongNhatKyHeThong(
                            dongDuLieu.nguoiDung(),
                            dongDuLieu.thoiGian(),
                            dongDuLieu.thaoTac(),
                            dongDuLieu.doiTuong(),
                            kieuChuan,
                            ketQuaChuan));
        }
        return ketQua;
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
