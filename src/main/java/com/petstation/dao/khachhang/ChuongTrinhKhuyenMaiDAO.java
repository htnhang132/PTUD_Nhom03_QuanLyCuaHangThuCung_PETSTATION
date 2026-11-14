package com.petstation.dao.khachhang;

import com.petstation.model.khachhang.ChuongTrinhKhuyenMai;

import java.util.List;

// Giao diện DAO quản lý chương trình khuyến mãi
public interface ChuongTrinhKhuyenMaiDAO {
    List<ChuongTrinhKhuyenMai> layTatCa();

    ChuongTrinhKhuyenMai timTheoMa(String maKhuyenMai);

    List<ChuongTrinhKhuyenMai> layDanhSachKhuyenMai();
}
