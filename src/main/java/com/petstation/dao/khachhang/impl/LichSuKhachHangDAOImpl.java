package com.petstation.dao.khachhang.impl;

import com.petstation.dao.khachhang.LichSuKhachHangDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.LichSuKhachHangDTO;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;

public final class LichSuKhachHangDAOImpl implements LichSuKhachHangDAO {

    public LichSuKhachHangDAOImpl() {}

    @Override
    public List<LichSuKhachHangDTO.LichSuThuCung> layThuCung(String maKhachHang) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT"
                            + " ma_thu_cung,ten,loai,giong,ngay_sinh,can_nang,tiem_chung,di_ung_benh_ly"
                            + " FROM thu_cung WHERE ma_khach_hang = ? ORDER BY ma_thu_cung",
                        maKhachHang);
        List<LichSuKhachHangDTO.LichSuThuCung> ketQua = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            ketQua.add(
                    new LichSuKhachHangDTO.LichSuThuCung(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.chuyenThanhChuoi(r[1]),
                            DuLieu.chuyenThanhChuoi(r[2]),
                            DuLieu.chuyenThanhChuoi(r[3]),
                            DuLieu.chuyenLocalDate(r[4]),
                            r[5] instanceof Number n ? n.doubleValue() : 0.0,
                            DuLieu.chuyenThanhChuoi(r[6]),
                            DuLieu.chuyenThanhChuoi(r[7])));
        }
        return ketQua;
    }

    @Override
    public List<LichSuKhachHangDTO.LichSuHoaDon> layCacHoaDon(String maKhachHang) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT"
                            + " h.ma_hoa_don,h.thanh_toan_luc,h.tong_tien,h.hinh_thuc_thanh_toan,h.trang_thai"
                            + " FROM hoa_don h WHERE h.ma_khach_hang = ? AND"
                            + " h.trang_thai<>'GIO_TREO' ORDER BY h.thanh_toan_luc DESC",
                        maKhachHang);
        List<LichSuKhachHangDTO.LichSuHoaDon> ketQua = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            ketQua.add(
                    new LichSuKhachHangDTO.LichSuHoaDon(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.chuyenLocalDateTime(r[1]),
                            DuLieu.layGiaTri(r[2]),
                            DuLieu.chuyenThanhChuoi(r[3]),
                            DuLieu.chuyenThanhChuoi(r[4])));
        }
        return ketQua;
    }

    @Override
    public List<LichSuKhachHangDTO.LichSuSpa> layLichSuSpa(String maKhachHang) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT t.ma_phieu,p.ten,t.tao_luc,t.tong_tien,t.trang_thai FROM"
                                + " phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                                + " p.ma_thu_cung=t.ma_thu_cung WHERE p.ma_khach_hang = ? ORDER BY"
                                + " t.tao_luc DESC",
                        maKhachHang);
        List<LichSuKhachHangDTO.LichSuSpa> ketQua = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            ketQua.add(
                    new LichSuKhachHangDTO.LichSuSpa(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.chuyenThanhChuoi(r[1]),
                            DuLieu.chuyenLocalDateTime(r[2]),
                            DuLieu.layGiaTri(r[3]),
                            DuLieu.chuyenThanhChuoi(r[4])));
        }
        return ketQua;
    }

    @Override
    public List<LichSuKhachHangDTO.LichSuLuuTru> layLichSuLuuTru(String maKhachHang) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT"
                            + " h.ma_hop_dong,p.ten,r.ma_phong,h.ngay_vao,h.ngay_ra_du_kien,h.trang_thai"
                            + " FROM hop_dong_luu_tru h JOIN thu_cung p ON"
                            + " p.ma_thu_cung=h.ma_thu_cung JOIN phong_hotel r ON"
                            + " r.ma_phong=h.ma_phong WHERE h.ma_khach_hang = ? ORDER BY h.ngay_vao"
                            + " DESC",
                        maKhachHang);
        List<LichSuKhachHangDTO.LichSuLuuTru> ketQua = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            ketQua.add(
                    new LichSuKhachHangDTO.LichSuLuuTru(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.chuyenThanhChuoi(r[1]),
                            DuLieu.chuyenThanhChuoi(r[2]),
                            DuLieu.chuyenLocalDateTime(r[3]),
                            DuLieu.chuyenLocalDateTime(r[4]),
                            DuLieu.chuyenThanhChuoi(r[5])));
        }
        return ketQua;
    }

    @Override
    public List<LichSuKhachHangDTO.LichSuVoucher> layVoucher(String maKhachHang) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_voucher,menh_gia,hieu_luc_den,da_dung,so_luong,trang_thai FROM"
                                + " voucher WHERE ma_khach_hang = ? ORDER BY ma_voucher DESC",
                        maKhachHang);
        List<LichSuKhachHangDTO.LichSuVoucher> ketQua = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            ketQua.add(
                    new LichSuKhachHangDTO.LichSuVoucher(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            DuLieu.layGiaTri(r[1]),
                            DuLieu.chuyenLocalDateTime(r[2]),
                            (int) DuLieu.layGiaTri(r[3]),
                            (int) DuLieu.layGiaTri(r[4]),
                            DuLieu.chuyenThanhChuoi(r[5])));
        }
        return ketQua;
    }
}
