package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcXuLySpaDAO;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Quản lý tiếp nhận, lựa chọn dịch vụ, tiến độ và hoàn thành phiếu Spa. */
public final class SpaService {
    /**
     * Đặt lịch hẹn trong SpaService.
     *
     * @param maThuCung mã thú cưng
     * @param maGoiDichVu mã gói dịch vụ
     * @param maNhanVien mã nhân viên
     * @param batDau bắt đầu
     * @return kết quả đặt lịch hẹn
     */
    public static long datLichHen(
            String maThuCung, String maGoiDichVu, String maNhanVien, LocalDateTime batDau) {
        yeuCau(Quyen.SPA_MANAGE);
        return thucHienGiaoDich(
                ketNoi -> {
                    // Vùng đang xét.
                    Object[] vungDangXet = layThuCung(maThuCung);
                    // Gói dịch vụ.
                    long goiDichVu = layId("goi_dich_vu_spa", "ma_goi", maGoiDichVu),
                            nhanVien = layId("nhan_vien", "ma_nhan_vien", maNhanVien);
                    kiemTraDuong(JdbcXuLySpaDAO.demLichHen(goiDichVu));
                    kiemTraDuong(JdbcXuLySpaDAO.demLichHen2(nhanVien));
                    // Kết thúc.
                    LocalDateTime ketThuc =
                            batDau.plusMinutes(JdbcXuLySpaDAO.demLichHen3(goiDichVu));
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcXuLySpaDAO.demLichHen4(nhanVien, vungDangXet[0], ketThuc, batDau) > 0)
                        throw new IllegalArgumentException(
                                "Nhân viên hoặc thú cưng đã có lịch trùng giờ.");
                    // Kết quả.
                    long ketQua =
                            JdbcXuLySpaDAO.themLichHen(
                                    taoMa("LH"),
                                    vungDangXet[1],
                                    vungDangXet[0],
                                    batDau,
                                    ketThuc,
                                    nhanVien,
                                    goiDichVu);
                    NhatKyHeThongService.ghiNhatKy("Tạo lịch hẹn", maThuCung);
                    return ketQua;
                });
    }

    /**
     * Tiếp nhận trong SpaService.
     *
     * @param maThuCung mã thú cưng
     * @param maLichHen mã lịch hẹn
     * @param canNang cân nặng
     * @param ghiChu ghi chú
     * @return kết quả tiếp nhận
     */
    public static long tiepNhan(String maThuCung, String maLichHen, double canNang, String ghiChu) {
        yeuCau(Quyen.SPA_MANAGE);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (!Double.isFinite(canNang) || canNang <= 0)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Vùng đang xét.
                    Object[] vungDangXet = layThuCung(maThuCung);
                    // Lịch hẹn.
                    Long lichHen = null;
                    // Nhân viên.
                    long nhanVien = layIdNhanVien();
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (maLichHen != null && !maLichHen.isBlank()) {
                        // Các dòng.
                        List<Object[]> cacDong =
                                JdbcXuLySpaDAO.timTiepNhan(maLichHen, vungDangXet[0]);
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (cacDong.isEmpty())
                            throw new IllegalArgumentException(
                                    "Lịch hẹn không phù hợp hoặc đã tiếp nhận.");
                        lichHen = layGiaTri(cacDong.get(0)[0]);
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (cacDong.get(0)[1] != null) nhanVien = layGiaTri(cacDong.get(0)[1]);
                        JdbcXuLySpaDAO.capNhatTiepNhan(lichHen);
                    }
                    // Kết quả.
                    long ketQua =
                            JdbcXuLySpaDAO.themTiepNhan(
                                    taoMa("SPA"),
                                    lichHen,
                                    vungDangXet[0],
                                    canNang,
                                    nhanVien,
                                    ghiChu);
                    JdbcXuLySpaDAO.capNhatTiepNhan2(canNang, ghiChu, vungDangXet[0]);
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (lichHen != null) {
                        // Gói dịch vụ.
                        Object goiDichVu = JdbcXuLySpaDAO.layTiepNhan(lichHen);
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (goiDichVu != null) themDichVuSpa(ketQua, chuyenThanhChuoi(goiDichVu));
                    }
                    NhatKyHeThongService.ghiNhatKy("Tiếp nhận Spa", maThuCung);
                    return ketQua;
                });
    }

    /**
     * Thêm dịch vụ Spa trong SpaService.
     *
     * @param phieuSpa phiếu Spa
     * @param maGoiDichVu mã gói dịch vụ
     */
    public static void themDichVuSpa(long phieuSpa, String maGoiDichVu) {
        yeuCau(Quyen.SPA_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Các phiếu Spa.
                    List<Object[]> cacPhieuSpa = JdbcXuLySpaDAO.timThemDichVuSpa(phieuSpa);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacPhieuSpa.isEmpty()
                            || !Set.of("RECEIVED", "IN_PROGRESS")
                                    .contains(chuyenThanhChuoi(cacPhieuSpa.get(0)[2])))
                        throw new IllegalArgumentException(
                                "Phiếu không còn cho phép thêm dịch vụ.");
                    // Giá trị tạm.
                    Object[] giaTriTam = cacPhieuSpa.get(0);
                    // Các giá.
                    List<Object[]> cacGia =
                            JdbcXuLySpaDAO.timThemDichVuSpa2(
                                    maGoiDichVu, giaTriTam[1], giaTriTam[0], giaTriTam[0]);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacGia.isEmpty())
                        throw new IllegalArgumentException(
                                "Chưa có bảng giá phù hợp với loài và cân nặng.");
                    // Giá.
                    Object[] gia = cacGia.get(0);
                    JdbcXuLySpaDAO.capNhatThemDichVuSpa(phieuSpa, gia[0], gia[2], gia[1]);
                    JdbcXuLySpaDAO.capNhatThemDichVuSpa2(phieuSpa, phieuSpa);
                    NhatKyHeThongService.ghiNhatKy(
                            "Thêm dịch vụ Spa", maGoiDichVu + " / " + phieuSpa);
                    return null;
                });
    }

    /**
     * Lấy trạng thái Spa trong SpaService.
     *
     * @param phieuSpa phiếu Spa
     * @param tiepTheo tiếp theo
     */
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
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcXuLySpaDAO.demTrangThaiSpa(phieuSpa) == 0)
                        throw new IllegalArgumentException("Cần chọn dịch vụ cho phiếu.");
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcXuLySpaDAO.capNhatTrangThaiSpa(tiepTheo, phieuSpa, duKien) != 1)
                        throw new IllegalArgumentException(
                                "Phiếu đã chuyển trạng thái. Hãy tải lại.");
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (tiepTheo.equals("COMPLETED")) JdbcXuLySpaDAO.capNhatSpaTrangThai2(phieuSpa);
                    NhatKyHeThongService.ghiNhatKy("Cập nhật Spa", phieuSpa + " / " + tiepTheo);
                    return null;
                });
    }

    /**
     * Hủy lịch hẹn trong SpaService.
     *
     * @param id id
     * @param ma mã
     */
    public static void huyLichHen(long id, String ma) {
        yeuCau(Quyen.SPA_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcXuLySpaDAO.huyLichHenBanGhi(id) != 1)
                        throw new IllegalArgumentException("Chỉ có thể hủy lịch chưa tiếp nhận.");
                    NhatKyHeThongService.ghiNhatKy("Hủy lịch hẹn", ma);
                    return null;
                });
    }

    /**
     * Lấy mã lịch hẹn thú cưng trong SpaService.
     *
     * @param lichHen lịch hẹn
     * @return kết quả lấy mã lịch hẹn thú cưng
     */
    public static Object layMaLichHenThuCung(long lichHen) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.SPA_MANAGE);
        return JdbcXuLySpaDAO.layMaLichHenThuCung(lichHen);
    }

    /**
     * Lấy phiếu Spa chi tiết trong SpaService.
     *
     * @param phieuSpa phiếu Spa
     * @return kết quả lấy phiếu Spa chi tiết
     */
    public static List<Object[]> layPhieuSpaChiTiet(long phieuSpa) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.SPA_MANAGE);
        return JdbcXuLySpaDAO.layPhieuSpaChiTiet(phieuSpa);
    }
}
