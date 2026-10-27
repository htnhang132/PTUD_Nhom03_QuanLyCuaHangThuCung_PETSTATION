package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho BanHang; service gọi qua hợp đồng này. */
public interface BanHangDAO {
    List<Object[]> layNguonBienLai(String soHoaDon);

    Object timHoaDonTheoKhoaThu(Object... cacThamSo);

    int capNhatThanhToan(Object... cacThamSo);

    int capNhatCapNhatHang(Object... cacThamSo);

    long demMoLai(Object... cacThamSo);

    int capNhatXoaTreo(Object... cacThamSo);

    int xoaGioHangTreo(Object... cacThamSo);

    Object layQuyetToan(Object... cacThamSo);

    List<Object[]> timQuyetToan(Object... cacThamSo);

    long demHuyHoaDon(Object... cacThamSo);

    long demHopDongGanHoaDon(Object... cacThamSo);

    int capNhatHuyHoaDon(Object... cacThamSo);

    int hoanDiemKhachHang(Object... cacThamSo);

    int danhDauHuyHoaDon(Object... cacThamSo);

    int danhDauHuyDonHang(Object... cacThamSo);

    long demThanhToan(Object... cacThamSo);

    long demNgayDaChot(Object... cacThamSo);

    List<Object[]> timThanhToan(Object... cacThamSo);

    int ghiNhanSuDungVoucher(Object... cacThamSo);

    long themThanhToan(Object... cacThamSo);

    List<Object[]> khoaSanPhamDeBan(Object... cacThamSo);

    long themDongDonHang(Object... cacThamSo);

    List<Object[]> layLoTheoFEFO(Object... cacThamSo);

    int truTonLo(Object... cacThamSo);

    int ghiPhanBoLo(Object... cacThamSo);

    long themHoaDon(Object... cacThamSo);

    int ghiDiemThanhToan(Object... cacThamSo);

    long layTienSpaChoThanhToan(Object... cacThamSo);

    int ganHoaDonSpa(Object... cacThamSo);

    int ganHoaDonHotel(Object... cacThamSo);

    int chuyenPhongChoDon(Object... cacThamSo);

    long demCapNhatHang(Object... cacThamSo);

    List<Object[]> timBienLai(Object... cacThamSo);

    List<Object[]> layDongBienLai(Object... cacThamSo);

    long themTreo(Object... cacThamSo);

    int capNhatTreo(Object... cacThamSo);

    long demGioTreoCuaTaiKhoan(Object... cacThamSo);

    List<Object[]> timMoLai(Object... cacThamSo);

    List<Object[]> layKhachCuaGioTreo(Object... cacThamSo);

    long demXoaTreo(Object... cacThamSo);

    long demQuyetToan(Object... cacThamSo);

    List<Object[]> layDichVuSpaQuyetToan(Object... cacThamSo);

    Object layKhachCuaPhieuSpa(Object... cacThamSo);

    List<Object[]> timHuyHoaDon(Object... cacThamSo);

    long demPhieuDoiTraGanHoaDon(Object... cacThamSo);

    List<Object[]> layPhanBoCanHoanTon(Object... cacThamSo);

    long khoaDiemKhachHang(Object... cacThamSo);

    int ghiDiemHuyHoaDon(Object... cacThamSo);

    int hoanLuotVoucher(Object... cacThamSo);

    List<Object[]> layCoTheBanCacSanPham(Object... cacThamSo);

    List<Object[]> layThongTinIn(String soHoaDon);
}
