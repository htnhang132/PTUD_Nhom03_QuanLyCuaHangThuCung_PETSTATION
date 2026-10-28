package com.petstation.dao;

import com.petstation.model.KhachHang;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/** Thao tác dữ liệu cho KhachHang; service gọi qua hợp đồng này. */
public interface KhachHangDAO {
    KhachHang tao(KhachHang khachHang);

    Optional<KhachHang> timTheoId(Long id);

    Optional<KhachHang> timTheoMaKhachHang(String maKhachHang);

    Optional<KhachHang> timTheoSoDienThoai(String soDienThoai);

    List<KhachHang> timKiemTheoTen(String ten);

    List<KhachHang> timTatCa();

    List<KhachHang> timTheoHangThanhVien(String hang);

    boolean capNhat(KhachHang khachHang);

    boolean capNhatDiemTichLuy(Long idKhachHang, Integer diem);

    boolean capNhatHangThanhVien(Long idKhachHang, String hang);

    boolean xoa(Long id);

    boolean layTonTaiTheoSoDienThoai(String soDienThoai);

    long dem();
}
