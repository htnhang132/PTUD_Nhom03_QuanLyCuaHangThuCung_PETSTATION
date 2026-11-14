package com.petstation.dao.khachhang;

import com.petstation.model.khachhang.KhachHang;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KhachHangDAO {
    KhachHang tao(KhachHang khachHang);

    Optional<KhachHang> timTheoMaKhachHang(String maKhachHang);

    Optional<KhachHang> timTheoSoDienThoai(String soDienThoai);

    List<KhachHang> timKiemTheoTen(String ten);

    List<KhachHang> timTatCa();

    List<KhachHang> timTheoHangThanhVien(String hang);

    List<KhachHang> timTheoKhoangNgayTao(LocalDateTime tuThoiDiem, LocalDateTime denThoiDiem);

    boolean capNhat(KhachHang khachHang);

    boolean capNhatDiemTichLuy(String maKhachHang, Integer diem);

    boolean capNhatHangThanhVien(String maKhachHang, String hang);

    boolean xoa(String maKhachHang);

    boolean layTonTaiTheoSoDienThoai(String soDienThoai);

    boolean kiemTraTonTaiTheoSoDienThoai(String soDienThoai);

    boolean kiemTraTonTaiTheoMaKhachHang(String maKhachHang);

    long dem();

    long demSoLuong();
}
