package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcThongKeDAO;
import com.petstation.model.KhoangNgay;
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
        return layDoanhThuTheoNgay(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDoanhThuTheoNgay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDoanhThuTheoNgay(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
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
        return layDoanhThu(KhoangNgay.ganDay(soNgay), nhomTongHop);
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDoanhThu(KhoangNgay khoangNgay, String nhomTongHop) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDoanhThu(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
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
        return laySanPhamBanChay(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> laySanPhamBanChay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.laySanPhamBanChay(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
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
        return layDuLieuSpa(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDuLieuSpa(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDuLieuSpa(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
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
        return layLichHen(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layLichHen(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layLichHen(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
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
        return layDuLieuHotel(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDuLieuHotel(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.layDuLieuHotel(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
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
        return layKhachHang(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layKhachHang(KhoangNgay khoangNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timCacKhachHang(khoangNgay.tuNgay(), khoangNgay.layCanTren());
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
        return layCongNoNhaCungCap(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layCongNoNhaCungCap(KhoangNgay khoangNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timNhaCungCapCongNo(khoangNgay.tuNgay(), khoangNgay.layCanTren());
    }

    /**
     * Lấy nhân viên trong ThongKeService.
     *
     * @param soNgay số ngày
     * @return kết quả lấy nhân viên
     */
    public static List<Object[]> layNhanVien(int soNgay) {
        return layNhanVien(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layNhanVien(KhoangNgay khoangNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return JdbcThongKeDAO.timNhanVien(khoangNgay.tuNgay(), khoangNgay.layCanTren());
    }

    public static List<Object[]> layDoanhThuHoanTat(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layDoanhThuHoanTat(k.tuNgay(), k.layCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layBienDongKho(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layBienDongKho(
                k.tuNgay(),
                k.layCanTren(),
                k.tuNgay(),
                k.layCanTren(),
                k.tuNgay(),
                k.layCanTren(),
                k.tuNgay(),
                k.layCanTren());
    }

    public static List<Object[]> layPhieuSpaTheoTrangThai(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layPhieuSpaTheoTrangThai(
                k.tuNgay(), k.layCanTren(), nhanVien, nhanVien);
    }

    public static List<Object[]> layHopDongTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layHopDongTrongKy(k.tuNgay(), k.layCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layCocTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layCocTrongKy(k.tuNgay(), k.layCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layLuotKhach(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layLuotKhach(k.tuNgay(), k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> laySuDungVoucher(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.laySuDungVoucher(k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> layTongHopCa(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layTongHopCa(k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> layDuNoHienTai(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return JdbcThongKeDAO.layDuNoHienTai();
    }
}
