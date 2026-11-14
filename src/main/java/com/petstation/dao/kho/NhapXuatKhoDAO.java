package com.petstation.dao.kho;

import com.petstation.model.kho.ChiTietPhieuNhap;
import com.petstation.model.kho.ChiTietPhieuXuat;
import com.petstation.model.kho.LoHang;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Giao diện DAO quản lý nghiệp vụ Nhập kho & Xuất hủy kho
public interface NhapXuatKhoDAO {
    // Nghiệp vụ Nhập kho
    long demNhapKho(String maSanPham);

    long demNhaCungCapDangHopTac(String maNhaCungCap);

    String themNhapKho(
            String maPhieu, String maNhaCungCap, String maNhanVien, long tongTien, long daTra);

    int themChiTietNhapKho(
            String maPhieuNhap,
            String maSanPham,
            String maLo,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap);

    Optional<LoHang> timNhapKho(String maSanPham, String maHoacSoLo);

    int themLoHang(String maSanPham, String soLo, LocalDate hanSuDung, int soLuong);

    int capNhatNhapKho(int soLuong, String maLo);

    int capNhatGiaNhapGanNhat(long giaNhap, String maSanPham);

    List<ChiTietPhieuNhap> layNhapKhoChiTiet(String maPhieuNhap);

    long layCongNoConLai(String maPhieuNhap);

    int themThanhToanNhaCungCap(String maPhieuNhap, long daTra);

    // Nghiệp vụ Xuất hủy kho
    Optional<LoHang> timXuatHuy(String maLo);

    String themXuatHuy(String maPhieu, String maNhanVien, String lyDo, long tongGiaTri);

    int capNhatXuatHuy(String maPhieuXuat, String maLo, int soLuong, long giaTri);

    long demApDungXuatHuy(String maPhieuXuat);

    List<ChiTietPhieuXuat> timApDungXuatHuy(String maPhieuXuat);

    int truTonXuatHuy(int soLuong, String maLo);

    int capNhatApDungXuatHuy(String maPhieuXuat);
}
