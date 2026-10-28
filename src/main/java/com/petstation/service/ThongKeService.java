package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.ThongKeDAO;
import com.petstation.dao.impl.ThongKeDAOImpl;
import com.petstation.model.KhoangNgay;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho thống kê dịch vụ. */
public final class ThongKeService {
    private static final ThongKeDAO thongKeDAO = new ThongKeDAOImpl();

    private ThongKeService() {}

    private static LocalDate layNgayBatDau(int soNgay) {
        return LocalDate.now().minusDays(Math.max(1, soNgay) - 1L);
    }

    private static LocalDate layNgayKetThuc() {
        return LocalDate.now().plusDays(1);
    }

    private static void kiemTraQuyenBaoCao() {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (!layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL)) yeuCau(Quyen.REPORT_VIEW_OWN_SHIFT);
    }

    public static List<Object[]> layDoanhThuTheoNgay(int soNgay) {
        return layDoanhThuTheoNgay(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDoanhThuTheoNgay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.layDoanhThuTheoNgay(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layDoanhThu(int soNgay, String nhomTongHop) {
        return layDoanhThu(KhoangNgay.ganDay(soNgay), nhomTongHop);
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDoanhThu(KhoangNgay khoangNgay, String nhomTongHop) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.layDoanhThu(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
                nhomTongHop,
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> laySanPhamBanChay(int soNgay) {
        return laySanPhamBanChay(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> laySanPhamBanChay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.laySanPhamBanChay(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layTonKho() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timKho();
    }

    public static List<Object[]> layLoSapHetHan() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timHanSuDung(
                com.petstation.service.CaiDatService.layHanSuDungCanhBaoSoNgay());
    }

    public static List<Object[]> layDuLieuSpa(int soNgay) {
        return layDuLieuSpa(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDuLieuSpa(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.layDuLieuSpa(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layLichHen(int soNgay) {
        return layLichHen(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layLichHen(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.layLichHen(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layTrangThaiPhong() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timTrangThaiPhong();
    }

    public static List<Object[]> layDuLieuHotel(int soNgay) {
        return layDuLieuHotel(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layDuLieuHotel(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        // Tất cả.
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.layDuLieuHotel(
                khoangNgay.tuNgay(),
                khoangNgay.layCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layKhachHang(int soNgay) {
        return layKhachHang(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layKhachHang(KhoangNgay khoangNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.timCacKhachHang(khoangNgay.tuNgay(), khoangNgay.layCanTren());
    }

    public static List<Object[]> layVoucher() {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.timCacVoucher();
    }

    public static List<Object[]> layCongNoNhaCungCap(int soNgay) {
        return layCongNoNhaCungCap(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layCongNoNhaCungCap(KhoangNgay khoangNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.timNhaCungCapCongNo(khoangNgay.tuNgay(), khoangNgay.layCanTren());
    }

    public static List<Object[]> layNhanVien(int soNgay) {
        return layNhanVien(KhoangNgay.ganDay(soNgay));
    }

    /** Truy vấn theo khoảng ngày đã kiểm tra, bao gồm cả hai ngày. */
    public static List<Object[]> layNhanVien(KhoangNgay khoangNgay) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        return thongKeDAO.timNhanVien(khoangNgay.tuNgay(), khoangNgay.layCanTren());
    }

    public static List<Object[]> layDoanhThuHoanTat(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layDoanhThuHoanTat(k.tuNgay(), k.layCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layBienDongKho(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layBienDongKho(
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
        return thongKeDAO.layPhieuSpaTheoTrangThai(k.tuNgay(), k.layCanTren(), nhanVien, nhanVien);
    }

    public static List<Object[]> layHopDongTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layHopDongTrongKy(k.tuNgay(), k.layCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layCocTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layCocTrongKy(k.tuNgay(), k.layCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layLuotKhach(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layLuotKhach(k.tuNgay(), k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> laySuDungVoucher(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.laySuDungVoucher(k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> layTongHopCa(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layTongHopCa(k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> layDuNoHienTai(KhoangNgay k) {
        yeuCau(Quyen.REPORT_VIEW_ALL);
        Long taiKhoan = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdTaiKhoan();
        Long nhanVien = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL) ? null : layIdNhanVien();
        return thongKeDAO.layDuNoHienTai();
    }

    public static List<Object[]> layDoanhThuTheoGio(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layDoanhThuTheoGio(k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> layDoanhThuTheoThu(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layDoanhThuTheoThu(k.tuNgay(), k.layCanTren());
    }

    public static List<Object[]> layHangTonDong() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layHangTonDong();
    }

    public static List<Object[]> layHaoHutKiemKe(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layHaoHutKiemKe(k.tuNgay(), k.layCanTren());
    }
}
