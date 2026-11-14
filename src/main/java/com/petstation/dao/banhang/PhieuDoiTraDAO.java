package com.petstation.dao.banhang;

import com.petstation.model.banhang.ChiTietDoiTra;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;

import java.util.List;
import java.util.Optional;

// Giao diện DAO quản lý Phiếu Đổi Trả & Đổi hàng
public interface PhieuDoiTraDAO {
    List<HoaDon> timHoaDon(String tuKhoa);

    List<ChiTietDoiTra> layDongHoaDon(String maHoaDon);

    List<SanPham> timPhuKien(String tuKhoa);

    Optional<ChiTietHoaDon> timDongGoc(String maHoaDon, String maSanPham);

    Optional<SanPham> timThayThe(String ma);

    String taoPhieu(
            String maHoaDon,
            String maNhanVien,
            long chenhLech,
            String lyDo,
            long giaTri,
            String khoa);

    void hoanTat(String maPhieu, String maHoaDonThayThe);

    long layChenhLechTruoc(String maHoaDon);

    boolean laHoaDonThayThe(String maHoaDon);

    int capNhatApDungDoiTra(int soLuong, String maLo);

    long demDoiTraMuc(String maHoaDon, String maSanPham);

    List<LoHang> laySoLuongLoConDuocDoi(String maHoaDon, String maSanPham);

    int capNhatDoiTraMuc(
            String maPhieu,
            String maHoaDon,
            String maSanPham,
            String maLo,
            int soLuong,
            long tienHoan);
}
