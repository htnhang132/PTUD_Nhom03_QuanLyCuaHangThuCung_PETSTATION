package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.ThongKeDAO;
import com.petstation.dao.impl.ThongKeDAOImpl;
import com.petstation.model.KhoangNgay;
import com.petstation.model.Quyen;

import java.util.*;

// Tầng dịch vụ Thống kê & Báo cáo (Service)
public final class ThongKeService {
    private static final ThongKeDAO thongKeDAO = new ThongKeDAOImpl();

    private ThongKeService() {}

    // Kiểm tra quyền xem báo cáo chung hoặc báo cáo ca của người dùng hiện tại
    private static void kiemTraQuyenBaoCao() {
        if (!layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)) yeuCau(Quyen.XEM_BAO_CAO_CA);
    }

    // 1. THỐNG KÊ DOANH THU (ThongKeDoanhThuPanel & BangDieuKhienPanel)

    public static List<Object[]> layDoanhThuTheoNgay(int soNgay) {
        return layDoanhThuTheoNgay(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> layDoanhThuTheoNgay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();

        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDoanhThuTheoNgay(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layDoanhThu(int soNgay, String nhomTongHop) {
        return layDoanhThu(KhoangNgay.ganDay(soNgay), nhomTongHop);
    }

    public static List<Object[]> layDoanhThu(KhoangNgay khoangNgay, String nhomTongHop) {
        kiemTraQuyenBaoCao();

        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDoanhThu(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                nhomTongHop,
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layDoanhThuHoanTat(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG) ? null : layIdTaiKhoan();
        return thongKeDAO.layDoanhThuHoanTat(k.getTuNgay(), k.getCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layDoanhThuTheoGio(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layDoanhThuTheoGio(k.getTuNgay(), k.getCanTren());
    }

    public static List<Object[]> layDoanhThuTheoThu(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layDoanhThuTheoThu(k.getTuNgay(), k.getCanTren());
    }

    // 2. THỐNG KÊ HÀNG HÓA & TỒN KHO (ThongKeHangHoaPanel)

    public static List<Object[]> laySanPhamBanChay(int soNgay) {
        return laySanPhamBanChay(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> laySanPhamBanChay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();

        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.laySanPhamBanChay(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
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

    public static List<Object[]> layBienDongKho(KhoangNgay k) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layBienDongKho(
                k.getTuNgay(),
                k.getCanTren(),
                k.getTuNgay(),
                k.getCanTren(),
                k.getTuNgay(),
                k.getCanTren(),
                k.getTuNgay(),
                k.getCanTren());
    }

    public static List<Object[]> layHangTonDong() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layHangTonDong();
    }

    public static List<Object[]> layHaoHutKiemKe(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layHaoHutKiemKe(k.getTuNgay(), k.getCanTren());
    }

    // 3. THỐNG KÊ DỊCH VỤ SPA (ThongKeSpaPanel)

    public static List<Object[]> layDuLieuSpa(int soNgay) {
        return layDuLieuSpa(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> layDuLieuSpa(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();

        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDuLieuSpa(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layLichHen(int soNgay) {
        return layLichHen(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> layLichHen(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();

        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layLichHen(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layPhieuSpaTheoTrangThai(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long nhanVien = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG) ? null : layIdNhanVien();
        return thongKeDAO.layPhieuSpaTheoTrangThai(k.getTuNgay(), k.getCanTren(), nhanVien, nhanVien);
    }

    // 4. THỐNG KÊ LƯU TRÚ HOTEL (ThongKeLuuTruPanel)

    public static List<Object[]> layTrangThaiPhong() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timTrangThaiPhong();
    }

    public static List<Object[]> layDuLieuHotel(int soNgay) {
        return layDuLieuHotel(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> layDuLieuHotel(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();

        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDuLieuHotel(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : layIdTaiKhoan(),
                tatCa ? null : layIdNhanVien());
    }

    public static List<Object[]> layHopDongTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG) ? null : layIdTaiKhoan();
        return thongKeDAO.layHopDongTrongKy(k.getTuNgay(), k.getCanTren(), taiKhoan, taiKhoan);
    }

    public static List<Object[]> layCocTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        Long taiKhoan = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG) ? null : layIdTaiKhoan();
        return thongKeDAO.layCocTrongKy(k.getTuNgay(), k.getCanTren(), taiKhoan, taiKhoan);
    }

    // 5. THỐNG KÊ KHÁCH HÀNG & VOUCHER (ThongKeKhachHangPanel)

    public static List<Object[]> layKhachHang(int soNgay) {
        return layKhachHang(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> layKhachHang(KhoangNgay khoangNgay) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timCacKhachHang(khoangNgay.getTuNgay(), khoangNgay.getCanTren());
    }

    public static List<Object[]> layLuotKhach(KhoangNgay k) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layLuotKhach(k.getTuNgay(), k.getTuNgay(), k.getCanTren());
    }

    public static List<Object[]> layVoucher() {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timCacVoucher();
    }

    public static List<Object[]> laySuDungVoucher(KhoangNgay k) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.laySuDungVoucher(k.getTuNgay(), k.getCanTren());
    }

    // 6. THỐNG KÊ CÔNG NỢ NHÀ CUNG CẤP (ThongKeCongNoPanel)

    public static List<Object[]> layCongNoNhaCungCap(int soNgay) {
        return layCongNoNhaCungCap(KhoangNgay.ganDay(soNgay));
    }

    public static List<Object[]> layCongNoNhaCungCap(KhoangNgay khoangNgay) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timNhaCungCapCongNo(khoangNgay.getTuNgay(), khoangNgay.getCanTren());
    }

    public static List<Object[]> layDuNoHienTai(KhoangNgay k) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDuNoHienTai();
    }

    // 7. THỐNG KÊ NHÂN VIÊN & CA THU NGÂN (ThongKeNhanVienPanel)

    // Lấy bảng tổng hợp thu tiền đầu ca, hóa đơn, cọc lưu trú, thực đếm và chênh lệch ca
    public static List<Object[]> layTongHopCa(KhoangNgay k) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layTongHopCa(k.getTuNgay(), k.getCanTren());
    }

    public static List<Object[]> layNhanVien(int soNgay) {
        return layNhanVien(KhoangNgay.ganDay(soNgay));
    }

    // Lấy thống kê tổng số ca đi làm và tổng số giờ làm việc của từng nhân viên trong kỳ
    public static List<Object[]> layNhanVien(KhoangNgay khoangNgay) {
        yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timNhanVien(khoangNgay.getTuNgay(), khoangNgay.getCanTren());
    }
}
