package com.petstation.dao.banhang;

import com.petstation.dto.DonDoiTreo;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.banhang.PhieuDoiTra;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HoaDonDAO {
    Optional<PhieuDoiTra> layNguonBienLai(String soHoaDon);

    Optional<String> timHoaDonTheoKhoaThu(String khoaThanhToan);

    int capNhatThanhToan(int diemDung, long diemCong, String maKhachHang);

    int capNhatCapNhatHang(String hangThanhVien, String maKhachHang);

    long demMoLai(String maHoaDon);

    int capNhatXoaTreo(String maHoaDon);

    int xoaGioHangTreo(String maHoaDon, String maNhanVien);

    Optional<String> layQuyetToan(String maHopDong);

    Optional<KhachHang> timQuyetToan(String maKhachHang);

    long demHuyHoaDon(String maHoaDon);

    long demHopDongGanHoaDon(String maHoaDon);

    int capNhatHuyHoaDon(int soLuong, String maLo);

    int hoanDiemKhachHang(long diemMoi, String maKhachHang);

    int danhDauHuyHoaDon(String maHoaDon);

    int danhDauHuyDonHang(String maHoaDon);

    Optional<String> layMaPhienDangMo(String tenHoacMaNhanVien);

    long demNgayDaChot();

    Optional<KhachHang> timThanhToan(String maKhachHang);

    int ghiNhanSuDungVoucher(String maVoucher, long tamTinh, String maKhachHang, String kieuNguon);

    Optional<SanPham> khoaSanPhamDeBan(String maSanPham);

    long themDongDonHang(
            String maHoaDon,
            String maSanPham,
            String maDichVu,
            String tenHang,
            int soLuong,
            long donGia,
            double thueGtgt);

    List<LoHang> layLoTheoFEFO(String maSanPham);

    int truTonLo(int soLuong, String maLo);

    int ghiPhanBoLo(String maHoaDon, String maSanPham, String maLo, int soLuong);

    String themHoaDon(
            String maHoaDon,
            String maPhien,
            String maNhanVien,
            long tongTien,
            PhuongThuc phuongThuc,
            long daNhan,
            long tienThua,
            long diemCong,
            String khoaThanhToan,
            LocalDateTime thanhToanLuc,
            long tamTinh,
            long giamGia,
            int diemDung,
            String maVoucher,
            String maKhachHang);

    long layTienSpaChoThanhToan(String maPhieuSpa);

    int ganHoaDonSpa(String maHoaDon, String maPhieuSpa);

    int ganHoaDonHotel(String maHoaDon, String maHopDong);

    int chuyenPhongChoDon(String maHopDong);

    long demCapNhatHang(String maKhachHang);

    Optional<HoaDon> timBienLai(String maHoaDon);

    List<ChiTietHoaDon> layDongBienLai(String maHoaDon);

    String themTreo(String maHoaDonTreo, String maPhien, String maNhanVien, String maKhachHang);

    int capNhatTreo(String maHoaDon, String maSanPham, String tenHang, long donGia, int soLuong);

    long demGioTreoCuaTaiKhoan(String maHoaDon, String maNhanVien);

    List<ChiTietHoaDon> timMoLai(String maHoaDon);

    Optional<KhachHang> layKhachCuaGioTreo(String maHoaDon);

    long demXoaTreo(String maHoaDon, String maNhanVien);

    long demQuyetToan(String maPhieuSpa);

    List<ChiTietHoaDon> layDichVuSpaQuyetToan(String maPhieuSpa);

    Optional<String> layKhachCuaPhieuSpa(String maPhieuSpa);

    Optional<HoaDon> timHuyHoaDon(String maHoaDon);

    long demPhieuDoiTraGanHoaDon(String maHoaDon);

    List<LoHang> layPhanBoCanHoanTon(String maHoaDon);

    long khoaDiemKhachHang(String maKhachHang);

    int hoanLuotVoucher(String maVoucher);

    List<SanPham> layCoTheBanCacSanPham();

    Optional<HoaDon> layThongTinIn(String soHoaDon);

    DonDoiTreo layDonDoiTreo(String maHoaDon);

    void luuDonDoiTreo(String maHoaDon, DonDoiTreo don);

    String layMaHoaDonGoc(String maDong);
}
