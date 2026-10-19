package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcThongKeDAO;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho thống kê dịch vụ. */
public final class ThongKeService {
    /** Khởi tạo ThongKeService với các giá trị được cung cấp. */
    private ThongKeService() {}

    /**
     * Lấy ngày bắt đầu trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy ngày bắt đầu
     */
    private static LocalDate layNgayBatDau(int soNgay) {
        return LocalDate.now().minusDays(Math.max(1, soNgay) - 1L);
    }

    /**
     * Lấy ngày kết thúc trong ThongKeService.
     *
     * @return kết quả lấy ngày kết thúc
     */
    private static LocalDate layNgayKetThuc() {
        return LocalDate.now().plusDays(1);
    }

    /** Kiểm tra quyền báo cáo trong ThongKeService. */
    private static void kiemTraQuyenBaoCao() {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (!layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL)) yeuCau(Quyen.REPORT_VIEW_OWN_SHIFT);
    }

    /**
     * Lấy doanh thu theo ngày trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy doanh thu theo ngày
     */
    public static List<Object[]> layDoanhThuTheoNgay(int soNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDoanhThuTheoNgay(
                layNgayBatDau(soNgay),
                layNgayKetThuc(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    /**
     * Lấy doanh thu trong ThongKeService.
     *
     * @param soNgay số ngày
     * @param nhomTongHop nhóm tổng hợp
     * @return kết quả lấy doanh thu
     */
    public static List<Object[]> layDoanhThu(int soNgay, String nhomTongHop) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDoanhThu(
                layNgayBatDau(soNgay),
                layNgayKetThuc(),
                nhomTongHop,
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    /**
     * Lấy sản phẩm bán chạy trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy sản phẩm bán chạy
     */
    public static List<Object[]> laySanPhamBanChay(int soNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.laySanPhamBanChay(
                layNgayBatDau(soNgay),
                layNgayKetThuc(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    /**
     * Lấy tồn kho trong ThongKeService.
     *
     * @return kết quả lấy tồn kho
     */
    public static List<Object[]> layTonKho() {
        kiemTraQuyenBaoCao();
        return JdbcThongKeDAO.timKho();
    }

    /**
     * Lấy lô sắp hết hạn trong ThongKeService.
     *
     * @return kết quả lấy lô sắp hết hạn
     */
    public static List<Object[]> layLoSapHetHan() {
        kiemTraQuyenBaoCao();
        return JdbcThongKeDAO.timHanSuDung(
                com.petstation.service.CaiDatService.layHanSuDungCanhBaoSoNgay());
    }

    /**
     * Lấy dữ liệu Spa trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy dữ liệu Spa
     */
    public static List<Object[]> layDuLieuSpa(int soNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDuLieuSpa(
                layNgayBatDau(soNgay),
                layNgayKetThuc(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    /**
     * Lấy lịch hẹn trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy lịch hẹn
     */
    public static List<Object[]> layLichHen(int soNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layLichHen(
                layNgayBatDau(soNgay),
                layNgayKetThuc(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    /**
     * Lấy trạng thái phòng trong ThongKeService.
     *
     * @return kết quả lấy trạng thái phòng
     */
    public static List<Object[]> layTrangThaiPhong() {
        kiemTraQuyenBaoCao();
        return JdbcThongKeDAO.timTrangThaiPhong();
    }

    /**
     * Lấy dữ liệu Hotel trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy dữ liệu Hotel
     */
    public static List<Object[]> layDuLieuHotel(int soNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDuLieuHotel(
                layNgayBatDau(soNgay),
                layNgayKetThuc(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    /**
     * Lấy khách hàng trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy khách hàng
     */
    public static List<Object[]> layKhachHang(int soNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timCacKhachHang(layNgayBatDau(soNgay), layNgayKetThuc());
    }

    /**
     * Lấy voucher trong ThongKeService.
     *
     * @return kết quả lấy voucher
     */
    public static List<Object[]> layVoucher() {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timCacVoucher();
    }

    /**
     * Lấy công nợ nhà cung cấp trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy công nợ nhà cung cấp
     */
    public static List<Object[]> layCongNoNhaCungCap(int soNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timNhaCungCapCongNo(layNgayBatDau(soNgay), layNgayKetThuc());
    }

    /**
     * Lấy nhân viên trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy nhân viên
     */
    public static List<Object[]> layNhanVien(int soNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timNhanVien(layNgayBatDau(soNgay), layNgayKetThuc());
    }
}
