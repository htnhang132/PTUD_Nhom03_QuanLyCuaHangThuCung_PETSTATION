package com.petstation.dao.khachhang;

import com.petstation.dto.LichSuKhachHangDTO;

import java.util.List;

public interface LichSuKhachHangDAO {
    List<LichSuKhachHangDTO.LichSuThuCung> layThuCung(String maKhachHang);

    List<LichSuKhachHangDTO.LichSuHoaDon> layCacHoaDon(String maKhachHang);

    List<LichSuKhachHangDTO.LichSuSpa> layLichSuSpa(String maKhachHang);

    List<LichSuKhachHangDTO.LichSuLuuTru> layLichSuLuuTru(String maKhachHang);

    List<LichSuKhachHangDTO.LichSuVoucher> layVoucher(String maKhachHang);
}
