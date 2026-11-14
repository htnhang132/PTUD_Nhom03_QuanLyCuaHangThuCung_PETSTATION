package com.petstation.dao.spa;

import com.petstation.dto.SpaDTO;
import com.petstation.model.spa.LichHen;
import com.petstation.model.spa.PhieuTiepNhanSpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PhieuTiepNhanSpaDAO {
    Optional<PhieuTiepNhanSpa> timTheoMa(String maPhieu);

    int capNhatTiepNhan(String maLichHen);

    int capNhatSucKhoeThuCung(double canNang, String tinhTrang, String maThuCung);

    long demTrangThaiSpa(String maPhieu);

    long demLichHen(String maGoi);

    long khoaNhanVienDangLam(String maNhanVien);

    long layThoiLuongDichVu(String maGoi);

    long demLichTrung(
            String maNhanVien, String maThuCung, LocalDateTime ketThuc, LocalDateTime batDau);

    String themLichHen(
            String maLichHen,
            String maKhachHang,
            String maThuCung,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String maNhanVien,
            String maGoi);

    List<ThongTinTiepNhan> timTiepNhan(String maLichHen, String maThuCung);

    String themTiepNhan(
            String maPhieu,
            String maLichHen,
            String maThuCung,
            double canNang,
            String maNhanVien,
            String ghiChu);

    String layTiepNhan(String maLichHen);

    List<ThongTinThemDichVu> timThemDichVuSpa(String maPhieu);

    List<GiaDichVuCanNang> layGiaDichVuTheoCanNang(
            String maGoi, String loaiThuCung, double canNang);

    int capNhatThemDichVuSpa(String maPhieu, String maGoi, String moTa, long donGia);

    int tinhLaiTongPhieuSpa(String maPhieu);

    int capNhatTrangThaiSpa(String trangThaiMoi, String maPhieu, String trangThaiHienTai);

    int hoanTatLichHen(String maPhieu);

    int huyLichHenBanGhi(String maLichHen);

    String layMaLichHenThuCung(String maLichHen);

    List<SpaDTO.DongChiTietSpa> layPhieuSpaChiTiet(String maPhieu);

    List<LichHen> layChiTietLichHen(String maLichHen);

    List<String> khoaLichHen(String maLichHen);

    long demLichTrungKhiSua(
            String maLichHen,
            String maNhanVien,
            String maThuCung,
            LocalDateTime ketThuc,
            LocalDateTime batDau);

    int suaLichHen(
            String maKhachHang,
            String maThuCung,
            String maGoi,
            String maNhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String maLichHen);

    List<SpaDTO.DongTimKiemSpa> timKiem(LocalDate tuNgay, LocalDate denNgay, String trangThai);

    final class ThongTinTiepNhan {
        public final String maLichHen;
        public final String maNhanVien;

        public ThongTinTiepNhan(String maLichHen, String maNhanVien) {
            this.maLichHen = maLichHen;
            this.maNhanVien = maNhanVien;
        }
    }

    final class ThongTinThemDichVu {
        public final double canNangThucTe;
        public final String loaiThuCung;
        public final String trangThai;

        public ThongTinThemDichVu(double canNangThucTe, String loaiThuCung, String trangThai) {
            this.canNangThucTe = canNangThucTe;
            this.loaiThuCung = loaiThuCung;
            this.trangThai = trangThai;
        }
    }

    final class GiaDichVuCanNang {
        public final String maGoi;
        public final long donGia;
        public final String tenGoi;

        public GiaDichVuCanNang(String maGoi, long donGia, String tenGoi) {
            this.maGoi = maGoi;
            this.donGia = donGia;
            this.tenGoi = tenGoi;
        }
    }
}
