package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho ThongKe; service gọi qua hợp đồng này. */
public interface ThongKeDAO {
    List<Object[]> timKho(Object... cacThamSo);

    List<Object[]> timHanSuDung(Object... cacThamSo);

    List<Object[]> timTrangThaiPhong(Object... cacThamSo);

    List<Object[]> timCacKhachHang(Object... cacThamSo);

    List<Object[]> timCacVoucher(Object... cacThamSo);

    List<Object[]> timNhaCungCapCongNo(Object... cacThamSo);

    List<Object[]> timNhanVien(Object... cacThamSo);

    List<Object[]> layDoanhThuTheoNgay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layDoanhThu(
            java.time.LocalDate tu,
            java.time.LocalDate denNgay,
            String nhomTongHop,
            Long taiKhoan,
            Long nhanVien);

    List<Object[]> laySanPhamBanChay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layDuLieuSpa(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layLichHen(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layDuLieuHotel(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien);

    List<Object[]> layDoanhThuHoanTat(Object... thamSo);

    List<Object[]> layBienDongKho(Object... thamSo);

    List<Object[]> layPhieuSpaTheoTrangThai(Object... thamSo);

    List<Object[]> layHopDongTrongKy(Object... thamSo);

    List<Object[]> layCocTrongKy(Object... thamSo);

    List<Object[]> layLuotKhach(Object... thamSo);

    List<Object[]> laySuDungVoucher(Object... thamSo);

    List<Object[]> layTongHopCa(Object... thamSo);

    List<Object[]> layDuNoHienTai(Object... thamSo);

    List<Object[]> layDoanhThuTheoGio(java.time.LocalDate tu, java.time.LocalDate denNgay);

    List<Object[]> layDoanhThuTheoThu(java.time.LocalDate tu, java.time.LocalDate denNgay);

    List<Object[]> layHangTonDong();

    List<Object[]> layHaoHutKiemKe(java.time.LocalDate tu, java.time.LocalDate denNgay);
}
