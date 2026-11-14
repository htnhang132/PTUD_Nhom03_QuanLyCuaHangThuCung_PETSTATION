package com.petstation.dao.khachhang;

import com.petstation.model.khachhang.ThuCung;

import java.util.List;

// Giao diện DAO quản lý hồ sơ thú cưng
public interface ThuCungDAO {
    List<ThuCung> layTheoKhachHang(String maKhachHang);

    ThuCung timTheoMa(String maThuCung);

    List<ThuCung> layDanhSachThuCung();

    ThuCung timThuCungHoatDong(String maThuCung);
}
