package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.PhieuTiepNhanSpaDAO;
import com.petstation.dao.impl.PhieuTiepNhanSpaDAOImpl;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

public final class SpaService {
    private static PhieuTiepNhanSpaDAO xuLySpaDAO = new PhieuTiepNhanSpaDAOImpl();

    public static void cauHinhDAO(PhieuTiepNhanSpaDAO dao) {
        if (dao != null) xuLySpaDAO = dao;
    }

    public static long datLichHen(
            String maThuCung, String maGoiDichVu, String maNhanVien, LocalDateTime batDau) {
        yeuCau(Quyen.QUAN_LY_SPA);
        if (batDau == null || batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0)))
            throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
        return thucHienGiaoDich(
                ketNoi -> {
                    Object[] thuCung = layThuCung(maThuCung);

                    long goiDichVu = layId("goi_dich_vu_spa", "ma_goi", maGoiDichVu),
                            nhanVien = layId("nhan_vien", "ma_nhan_vien", maNhanVien);
                    if (xuLySpaDAO.demLichHen(goiDichVu) <= 0)
                        throw new IllegalArgumentException("Gói dịch vụ Spa không tồn tại hoặc đã ngừng hoạt động.");
                    if (xuLySpaDAO.khoaNhanVienDangLam(nhanVien) <= 0)
                        throw new IllegalArgumentException("Nhân viên phụ trách không hoạt động hoặc không trong ca.");
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
        yeuCau(Quyen.QUAN_LY_SPA);
        var rows = xuLySpaDAO.layChiTietLichHen(id);
        if (rows.isEmpty()) throw new IllegalArgumentException("Lịch hẹn không còn tồn tại.");
        return rows.get(0);
    }

    public static void suaLichHen(
            long id, String maThuCung, String maGoi, String maNhanVien, LocalDateTime batDau) {
        yeuCau(Quyen.QUAN_LY_SPA);
        if (batDau == null || batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0)))
            throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
        thucHienGiaoDich(
                c -> {
                    var old = xuLySpaDAO.khoaLichHen(id);
                    if (old.isEmpty() || !"DA_XAC_NHAN".equals(old.get(0)[0]))
                        throw new IllegalArgumentException("Chỉ được sửa lịch chưa tiếp nhận.");
                    Object[] thu = layThuCung(maThuCung);
                    long goi = layId("goi_dich_vu_spa", "ma_goi", maGoi),
                            nv = layId("nhan_vien", "ma_nhan_vien", maNhanVien);
                    if (xuLySpaDAO.demLichHen(goi) <= 0)
                        throw new IllegalArgumentException("Gói dịch vụ Spa không tồn tại hoặc đã ngừng hoạt động.");
                    if (xuLySpaDAO.khoaNhanVienDangLam(nv) <= 0)
                        throw new IllegalArgumentException("Nhân viên phụ trách không hoạt động hoặc không trong ca.");
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
        yeuCau(Quyen.QUAN_LY_SPA);
        if (!Double.isFinite(canNang) || canNang <= 0)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0.");
        return thucHienGiaoDich(
                ketNoi -> {
                    Object[] thuCung = layThuCung(maThuCung);

                    TiemPhongService.kiemTraNhanSpa(maThuCung);
                    Long lichHen = null;

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
                        Object goiDichVu = xuLySpaDAO.layTiepNhan(lichHen);
                        if (goiDichVu != null) themDichVuSpa(ketQua, chuyenThanhChuoi(goiDichVu));
                    }
                    NhatKyHeThongService.ghiNhatKy("Tiếp nhận Spa", maThuCung);
                    return ketQua;
                });
    }

    public static void themDichVuSpa(long phieuSpa, String maGoiDichVu) {
        yeuCau(Quyen.QUAN_LY_SPA);
        thucHienGiaoDich(
                ketNoi -> {
                    List<Object[]> cacPhieuSpa = xuLySpaDAO.timThemDichVuSpa(phieuSpa);
                    if (cacPhieuSpa.isEmpty()
                            || !Set.of("TIEP_NHAN", "DANG_THUC_HIEN")
                                    .contains(chuyenThanhChuoi(cacPhieuSpa.get(0)[2])))
                        throw new IllegalArgumentException(
                                "Phiếu không còn cho phép thêm dịch vụ.");
                    Object[] giaTriTam = cacPhieuSpa.get(0);

                    List<Object[]> cacGia =
                            xuLySpaDAO.layGiaDichVuTheoCanNang(
                                    maGoiDichVu, giaTriTam[1], giaTriTam[0], giaTriTam[0]);
                    if (cacGia.isEmpty())
                        throw new IllegalArgumentException(
                                "Chưa có bảng giá phù hợp với loài và cân nặng.");

                    Object[] gia = cacGia.get(0);
                    xuLySpaDAO.capNhatThemDichVuSpa(phieuSpa, gia[0], gia[2], gia[1]);
                    xuLySpaDAO.tinhLaiTongPhieuSpa(phieuSpa, phieuSpa);
                    NhatKyHeThongService.ghiNhatKy(
                            "Thêm dịch vụ Spa", maGoiDichVu + " / " + phieuSpa);
                    return null;
                });
    }

    public static void layTrangThaiSpa(long phieuSpa, String tiepTheo) {
        yeuCau(Quyen.QUAN_LY_SPA);
        String trangThaiMoi = switch (tiepTheo) {
            case "DANG_THUC_HIEN" -> "DANG_THUC_HIEN";
            case "HOAN_TAT" -> "HOAN_TAT";
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        };
        String duKien = "DANG_THUC_HIEN".equals(trangThaiMoi) ? "TIEP_NHAN" : "DANG_THUC_HIEN";
        thucHienGiaoDich(
                ketNoi -> {
                    if (xuLySpaDAO.demTrangThaiSpa(phieuSpa) == 0)
                        throw new IllegalArgumentException("Cần chọn dịch vụ cho phiếu.");
                    if (xuLySpaDAO.capNhatTrangThaiSpa(trangThaiMoi, phieuSpa, duKien) != 1)
                        throw new IllegalArgumentException(
                                "Phiếu đã chuyển trạng thái. Hãy tải lại.");
                    if ("HOAN_TAT".equals(trangThaiMoi)) xuLySpaDAO.hoanTatLichHen(phieuSpa);
                    NhatKyHeThongService.ghiNhatKy("Cập nhật Spa", phieuSpa + " / " + trangThaiMoi);
                    return null;
                });
    }

    public static void huyLichHen(long id, String ma) {
        yeuCau(Quyen.QUAN_LY_SPA);
        thucHienGiaoDich(
                ketNoi -> {
                    if (xuLySpaDAO.huyLichHenBanGhi(id) != 1)
                        throw new IllegalArgumentException("Chỉ có thể hủy lịch chưa tiếp nhận.");
                    NhatKyHeThongService.ghiNhatKy("Hủy lịch hẹn", ma);
                    return null;
                });
    }

    public static Object layMaLichHenThuCung(long lichHen) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.QUAN_LY_SPA);
        return xuLySpaDAO.layMaLichHenThuCung(lichHen);
    }

    public static List<Object[]> layPhieuSpaChiTiet(long phieuSpa) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.QUAN_LY_SPA);
        return xuLySpaDAO.layPhieuSpaChiTiet(phieuSpa);
    }

    public static List<Object[]> timKiem(
        LocalDate tuNgay,
        LocalDate denNgay,
        String trangThai) {
        yeuCau(Quyen.QUAN_LY_SPA);

        if ((tuNgay == null) != (denNgay == null)) {
            throw new IllegalArgumentException(
                "Vui lòng chọn đủ ngày bắt đầu và ngày kết thúc.");
        }

        if (tuNgay != null && tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException(
                "Ngày bắt đầu không được sau ngày kết thúc.");
        }

        String maTrangThai = trangThai == null ? "" : trangThai.trim();

        if (!Set.of(
            "", "TIEP_NHAN", "DANG_THUC_HIEN", "HOAN_TAT", "DA_THANH_TOAN", "DA_HUY"
        ).contains(maTrangThai)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }

        return xuLySpaDAO.timKiem(tuNgay, denNgay, maTrangThai);
    }
}
