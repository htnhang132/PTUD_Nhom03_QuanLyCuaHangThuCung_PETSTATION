package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.XuLySpaDAO;
import com.petstation.dao.impl.XuLySpaImpl;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Quản lý tiếp nhận, lựa chọn dịch vụ, tiến độ và hoàn thành phiếu Spa. */
public final class SpaService {
    private static final XuLySpaDAO xuLySpaDAO = new XuLySpaImpl();

    public static long datLichHen(
            String maThuCung, String maGoiDichVu, String maNhanVien, LocalDateTime batDau) {
        yeuCau(Quyen.SPA_MANAGE);
        if (batDau == null || batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0)))
            throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
        return thucHienGiaoDich(
                ketNoi -> {
                    Object[] thuCung = layThuCung(maThuCung);
                    // Gói dịch vụ.
                    long goiDichVu = layId("goi_dich_vu_spa", "ma_goi", maGoiDichVu),
                            nhanVien = layId("nhan_vien", "ma_nhan_vien", maNhanVien);
                    kiemTraDuong(xuLySpaDAO.demLichHen(goiDichVu));
                    kiemTraDuong(xuLySpaDAO.khoaNhanVienDangLam(nhanVien));
                    // Kết thúc.
                    LocalDateTime ketThuc =
                            batDau.plusMinutes(xuLySpaDAO.layThoiLuongDichVu(goiDichVu));
                    if (xuLySpaDAO.demLichTrung(nhanVien, thuCung[0], ketThuc, batDau) > 0)
                        throw new IllegalArgumentException(
                                "Nhân viên hoặc thú cưng đã có lịch trùng giờ.");
                    long ketQua =
                            xuLySpaDAO.themLichHen(
                                    taoMa("LH"),
                                    thuCung[1],
                                    thuCung[0],
                                    batDau,
                                    ketThuc,
                                    nhanVien,
                                    goiDichVu);
                    NhatKyHeThongService.ghiNhatKy("Tạo lịch hẹn", maThuCung);
                    return ketQua;
                });
    }

    public static Object[] layChiTietLichHen(long id) {
        yeuCau(Quyen.SPA_MANAGE);
        var rows = xuLySpaDAO.layChiTietLichHen(id);
        if (rows.isEmpty()) throw new IllegalArgumentException("Lịch hẹn không còn tồn tại.");
        return rows.get(0);
    }

    public static void suaLichHen(
            long id, String maThuCung, String maGoi, String maNhanVien, LocalDateTime batDau) {
        yeuCau(Quyen.SPA_MANAGE);
        if (batDau == null || batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0)))
            throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
        thucHienGiaoDich(
                c -> {
                    var old = xuLySpaDAO.khoaLichHen(id);
                    if (old.isEmpty() || !"SCHEDULED".equals(old.get(0)[0]))
                        throw new IllegalArgumentException("Chỉ được sửa lịch chưa tiếp nhận.");
                    Object[] thu = layThuCung(maThuCung);
                    long goi = layId("goi_dich_vu_spa", "ma_goi", maGoi),
                            nv = layId("nhan_vien", "ma_nhan_vien", maNhanVien);
                    kiemTraDuong(xuLySpaDAO.demLichHen(goi));
                    kiemTraDuong(xuLySpaDAO.khoaNhanVienDangLam(nv));
                    LocalDateTime end = batDau.plusMinutes(xuLySpaDAO.layThoiLuongDichVu(goi));
                    if (xuLySpaDAO.demLichTrungKhiSua(id, nv, thu[0], end, batDau) > 0)
                        throw new IllegalArgumentException(
                                "Nhân viên hoặc thú cưng đã có lịch trùng giờ.");
                    xuLySpaDAO.suaLichHen(thu[1], thu[0], goi, nv, batDau, end, id);
                    NhatKyHeThongService.ghiNhatKy("Sửa lịch Spa", String.valueOf(id));
                    return null;
                });
    }

    public static long tiepNhan(String maThuCung, String maLichHen, double canNang, String ghiChu) {
        yeuCau(Quyen.SPA_MANAGE);
        if (!Double.isFinite(canNang) || canNang <= 0)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0.");
        return thucHienGiaoDich(
                ketNoi -> {
                    Object[] thuCung = layThuCung(maThuCung);
                    // Lịch hẹn.
                    TiemPhongService.kiemTraNhanSpa(maThuCung);
                    Long lichHen = null;
                    // Nhân viên.
                    long nhanVien = layIdNhanVien();
                    if (maLichHen != null && !maLichHen.isBlank()) {
                        List<Object[]> cacDong = xuLySpaDAO.timTiepNhan(maLichHen, thuCung[0]);
                        if (cacDong.isEmpty())
                            throw new IllegalArgumentException(
                                    "Lịch hẹn không phù hợp hoặc đã tiếp nhận.");
                        lichHen = layGiaTri(cacDong.get(0)[0]);
                        if (cacDong.get(0)[1] != null) nhanVien = layGiaTri(cacDong.get(0)[1]);
                        xuLySpaDAO.capNhatTiepNhan(lichHen);
                    }
                    long ketQua =
                            xuLySpaDAO.themTiepNhan(
                                    taoMa("SPA"), lichHen, thuCung[0], canNang, nhanVien, ghiChu);
                    xuLySpaDAO.capNhatSucKhoeThuCung(canNang, ghiChu, thuCung[0]);
                    if (lichHen != null) {
                        // Gói dịch vụ.
                        Object goiDichVu = xuLySpaDAO.layTiepNhan(lichHen);
                        if (goiDichVu != null) themDichVuSpa(ketQua, chuyenThanhChuoi(goiDichVu));
                    }
                    NhatKyHeThongService.ghiNhatKy("Tiếp nhận Spa", maThuCung);
                    return ketQua;
                });
    }

    public static void themDichVuSpa(long phieuSpa, String maGoiDichVu) {
        yeuCau(Quyen.SPA_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Các phiếu Spa.
                    List<Object[]> cacPhieuSpa = xuLySpaDAO.timThemDichVuSpa(phieuSpa);
                    if (cacPhieuSpa.isEmpty()
                            || !Set.of("RECEIVED", "IN_PROGRESS")
                                    .contains(chuyenThanhChuoi(cacPhieuSpa.get(0)[2])))
                        throw new IllegalArgumentException(
                                "Phiếu không còn cho phép thêm dịch vụ.");
                    Object[] giaTriTam = cacPhieuSpa.get(0);
                    // Các giá.
                    List<Object[]> cacGia =
                            xuLySpaDAO.layGiaDichVuTheoCanNang(
                                    maGoiDichVu, giaTriTam[1], giaTriTam[0], giaTriTam[0]);
                    if (cacGia.isEmpty())
                        throw new IllegalArgumentException(
                                "Chưa có bảng giá phù hợp với loài và cân nặng.");
                    // Giá.
                    Object[] gia = cacGia.get(0);
                    xuLySpaDAO.capNhatThemDichVuSpa(phieuSpa, gia[0], gia[2], gia[1]);
                    xuLySpaDAO.tinhLaiTongPhieuSpa(phieuSpa, phieuSpa);
                    NhatKyHeThongService.ghiNhatKy(
                            "Thêm dịch vụ Spa", maGoiDichVu + " / " + phieuSpa);
                    return null;
                });
    }

    public static void layTrangThaiSpa(long phieuSpa, String tiepTheo) {
        yeuCau(Quyen.SPA_MANAGE);
        // Dự kiến.
        String duKien =
                switch (tiepTheo) {
                    case "IN_PROGRESS" -> "RECEIVED";
                    case "COMPLETED" -> "IN_PROGRESS";
                    default -> throw new IllegalArgumentException("Trạng thái không hợp lệ.");
                };
        thucHienGiaoDich(
                ketNoi -> {
                    if (xuLySpaDAO.demTrangThaiSpa(phieuSpa) == 0)
                        throw new IllegalArgumentException("Cần chọn dịch vụ cho phiếu.");
                    if (xuLySpaDAO.capNhatTrangThaiSpa(tiepTheo, phieuSpa, duKien) != 1)
                        throw new IllegalArgumentException(
                                "Phiếu đã chuyển trạng thái. Hãy tải lại.");
                    if (tiepTheo.equals("COMPLETED")) xuLySpaDAO.hoanTatLichHen(phieuSpa);
                    NhatKyHeThongService.ghiNhatKy("Cập nhật Spa", phieuSpa + " / " + tiepTheo);
                    return null;
                });
    }

    public static void huyLichHen(long id, String ma) {
        yeuCau(Quyen.SPA_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    if (xuLySpaDAO.huyLichHenBanGhi(id) != 1)
                        throw new IllegalArgumentException("Chỉ có thể hủy lịch chưa tiếp nhận.");
                    NhatKyHeThongService.ghiNhatKy("Hủy lịch hẹn", ma);
                    return null;
                });
    }

    public static Object layMaLichHenThuCung(long lichHen) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.SPA_MANAGE);
        return xuLySpaDAO.layMaLichHenThuCung(lichHen);
    }

    public static List<Object[]> layPhieuSpaChiTiet(long phieuSpa) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.SPA_MANAGE);
        return xuLySpaDAO.layPhieuSpaChiTiet(phieuSpa);
    }
}
