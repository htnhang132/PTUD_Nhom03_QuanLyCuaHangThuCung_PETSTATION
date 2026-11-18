package com.petstation.dao.khachhang;

import com.petstation.model.khachhang.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangDAO {
    KhachHang tao(KhachHang khachHang);

    Optional<KhachHang> timTheoMaKhachHang(String maKhachHang);

    List<KhachHang> timTatCa();

    boolean capNhat(KhachHang khachHang);

    boolean xoa(String maKhachHang);

    boolean kiemTraTonTaiTheoSoDienThoai(String soDienThoai);

    long demSoLuong();
}
