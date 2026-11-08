package com.petstation.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PhieuTiepNhanSpaDAO {
    int capNhatTiepNhan(Object maLichHen);

    int capNhatSucKhoeThuCung(double canNang, String tinhTrang, Object maThuCung);

    long demTrangThaiSpa(Object maPhieu);

    long demLichHen(Object maGoi);

    long khoaNhanVienDangLam(Object maNhanVien);

    long layThoiLuongDichVu(Object maGoi);

    long demLichTrung(Object maNhanVien, Object maThuCung, LocalDateTime ketThuc, LocalDateTime batDau);

    long themLichHen(
            String maLichHen,
            Object maKhachHang,
            Object maThuCung,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            Object maNhanVien,
            Object maGoi);

    List<Object[]> timTiepNhan(String maLichHen, Object maThuCung);

    long themTiepNhan(
            String maPhieu,
            Object maLichHen,
            Object maThuCung,
            double canNang,
            Object maNhanVien,
            String ghiChu);

    Object layTiepNhan(Object maLichHen);

    List<Object[]> timThemDichVuSpa(Object maPhieu);

    List<Object[]> layGiaDichVuTheoCanNang(String maGoi, Object loaiThuCung, double canNang);

    int capNhatThemDichVuSpa(Object maPhieu, Object maGoi, String moTa, double donGia);

    int tinhLaiTongPhieuSpa(Object maPhieu);

    int capNhatTrangThaiSpa(String trangThaiMoi, Object maPhieu, String trangThaiHienTai);

    int hoanTatLichHen(Object maPhieu);

    int huyLichHenBanGhi(Object maLichHen);

    Object layMaLichHenThuCung(Object maLichHen);

    List<Object[]> layPhieuSpaChiTiet(Object maPhieu);

    List<Object[]> layChiTietLichHen(long id);

    List<Object[]> khoaLichHen(long id);

    long demLichTrungKhiSua(
            long id, long nhanVien, Object thuCung, LocalDateTime ketThuc, LocalDateTime batDau);

    int suaLichHen(
            Object khachHang,
            Object thuCung,
            long goi,
            long nhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long id);

    List<Object[]> timKiem(
        LocalDate tuNgay,
        LocalDate denNgay,
        String trangThai);
}
