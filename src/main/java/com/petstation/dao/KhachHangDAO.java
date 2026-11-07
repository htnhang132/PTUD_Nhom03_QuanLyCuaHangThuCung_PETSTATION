package com.petstation.dao;

import com.petstation.model.KhachHang;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KhachHangDAO {
    KhachHang tao(KhachHang khachHang);

    Optional<KhachHang> timTheoId(Long id);

    Optional<KhachHang> timTheoMaKhachHang(String maKhachHang);

    Optional<KhachHang> timTheoSoDienThoai(String soDienThoai);

    List<KhachHang> timKiemTheoTen(String ten);

    List<KhachHang> timTatCa();

    List<KhachHang> timTheoHangThanhVien(String hang);

    List<KhachHang> timTheoNgaySinh(Date ngaySinh);

    List<KhachHang> timTheoKhoangNgayTao(LocalDateTime tuThoiDiem, LocalDateTime denThoiDiem);

    boolean capNhat(KhachHang khachHang);

    boolean capNhatDiemTichLuy(Long idKhachHang, Integer diem);

    boolean capNhatHangThanhVien(Long idKhachHang, String hang);

    boolean xoa(Long id);

    boolean layTonTaiTheoSoDienThoai(String soDienThoai);

    boolean kiemTraTonTaiTheoSoDienThoai(String soDienThoai);

    boolean kiemTraTonTaiTheoMaKhachHang(String maKhachHang);

    long dem();

    long demSoLuong();
}
