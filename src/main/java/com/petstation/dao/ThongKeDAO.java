package com.petstation.dao;

import java.util.List;

// Giao diện truy xuất dữ liệu (DAO) cho 7 phân hệ Thống kê & Báo cáo.
public interface ThongKeDAO {

    // 1. THỐNG KÊ DOANH THU

    List<Object[]> layDoanhThuTheoNgay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layDoanhThu(
            java.time.LocalDate tu,
            java.time.LocalDate denNgay,
            String nhomTongHop,
            Long taiKhoan,
            Long nhanVien);

    List<Object[]> layDoanhThuHoanTat(Object... thamSo);

    List<Object[]> layDoanhThuTheoGio(java.time.LocalDate tu, java.time.LocalDate denNgay);

    List<Object[]> layDoanhThuTheoThu(java.time.LocalDate tu, java.time.LocalDate denNgay);

    // 2. THỐNG KÊ HÀNG HÓA & TỒN KHO

    List<Object[]> laySanPhamBanChay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> timKho(Object... cacThamSo);

    List<Object[]> timHanSuDung(Object... cacThamSo);

    List<Object[]> layBienDongKho(Object... thamSo);

    List<Object[]> layHangTonDong();

    List<Object[]> layHaoHutKiemKe(java.time.LocalDate tu, java.time.LocalDate denNgay);

    // 3. THỐNG KÊ DỊCH VỤ SPA

    List<Object[]> layDuLieuSpa(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layLichHen(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layPhieuSpaTheoTrangThai(Object... thamSo);

    // 4. THỐNG KÊ LƯU TRÚ HOTEL

    List<Object[]> timTrangThaiPhong(Object... cacThamSo);

    List<Object[]> layDuLieuHotel(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layHopDongTrongKy(Object... thamSo);

    List<Object[]> layCocTrongKy(Object... thamSo);

    // 5. THỐNG KÊ KHÁCH HÀNG & VOUCHER

    List<Object[]> timCacKhachHang(Object... cacThamSo);

    List<Object[]> layLuotKhach(Object... thamSo);

    List<Object[]> timCacVoucher(Object... cacThamSo);

    List<Object[]> laySuDungVoucher(Object... thamSo);

    // 6. THỐNG KÊ CÔNG NỢ NHÀ CUNG CẤP

    List<Object[]> timNhaCungCapCongNo(Object... cacThamSo);

    List<Object[]> layDuNoHienTai(Object... thamSo);

    // 7. THỐNG KÊ NHÂN VIÊN & CA THU NGÂN

    // Tổng hợp thu tiền đầu ca, hóa đơn, tiền cọc, thực đếm và chênh lệch của các ca thu ngân
    List<Object[]> layTongHopCa(Object... thamSo);

    // Thống kê số ca đi làm và tổng số giờ công của nhân viên trong kỳ
    List<Object[]> timNhanVien(Object... cacThamSo);
}
