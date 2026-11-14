package com.petstation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class LichSuKhachHangDTO {
    private LichSuKhachHangDTO() {}

    public static final class LichSuThuCung {
        public final String maThuCung;
        public final String ten;
        public final String loai;
        public final String giong;
        public final LocalDate ngaySinh;
        public final double canNang;
        public final String tiemChung;
        public final String diUngBenhLy;

        public LichSuThuCung(
                String maThuCung,
                String ten,
                String loai,
                String giong,
                LocalDate ngaySinh,
                double canNang,
                String tiemChung,
                String diUngBenhLy) {
            this.maThuCung = maThuCung;
            this.ten = ten;
            this.loai = loai;
            this.giong = giong;
            this.ngaySinh = ngaySinh;
            this.canNang = canNang;
            this.tiemChung = tiemChung;
            this.diUngBenhLy = diUngBenhLy;
        }
    }

    public static final class LichSuHoaDon {
        public final String maHoaDon;
        public final LocalDateTime thanhToanLuc;
        public final long tongTien;
        public final String hinhThucThanhToan;
        public final String trangThai;

        public LichSuHoaDon(
                String maHoaDon,
                LocalDateTime thanhToanLuc,
                long tongTien,
                String hinhThucThanhToan,
                String trangThai) {
            this.maHoaDon = maHoaDon;
            this.thanhToanLuc = thanhToanLuc;
            this.tongTien = tongTien;
            this.hinhThucThanhToan = hinhThucThanhToan;
            this.trangThai = trangThai;
        }
    }

    public static final class LichSuSpa {
        public final String maPhieu;
        public final String tenThuCung;
        public final LocalDateTime taoLuc;
        public final long tongTien;
        public final String trangThai;

        public LichSuSpa(
                String maPhieu,
                String tenThuCung,
                LocalDateTime taoLuc,
                long tongTien,
                String trangThai) {
            this.maPhieu = maPhieu;
            this.tenThuCung = tenThuCung;
            this.taoLuc = taoLuc;
            this.tongTien = tongTien;
            this.trangThai = trangThai;
        }
    }

    public static final class LichSuLuuTru {
        public final String maHopDong;
        public final String tenThuCung;
        public final String maPhong;
        public final LocalDateTime ngayVao;
        public final LocalDateTime ngayRaDuKien;
        public final String trangThai;

        public LichSuLuuTru(
                String maHopDong,
                String tenThuCung,
                String maPhong,
                LocalDateTime ngayVao,
                LocalDateTime ngayRaDuKien,
                String trangThai) {
            this.maHopDong = maHopDong;
            this.tenThuCung = tenThuCung;
            this.maPhong = maPhong;
            this.ngayVao = ngayVao;
            this.ngayRaDuKien = ngayRaDuKien;
            this.trangThai = trangThai;
        }
    }

    public static final class LichSuVoucher {
        public final String maVoucher;
        public final long menhGia;
        public final LocalDateTime hieuLucDen;
        public final int daDung;
        public final int soLuong;
        public final String trangThai;

        public LichSuVoucher(
                String maVoucher,
                long menhGia,
                LocalDateTime hieuLucDen,
                int daDung,
                int soLuong,
                String trangThai) {
            this.maVoucher = maVoucher;
            this.menhGia = menhGia;
            this.hieuLucDen = hieuLucDen;
            this.daDung = daDung;
            this.soLuong = soLuong;
            this.trangThai = trangThai;
        }
    }
}
