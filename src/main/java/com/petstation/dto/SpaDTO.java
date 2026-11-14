package com.petstation.dto;

import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.PhieuSpaTreo;
import com.petstation.model.spa.PhieuTiepNhanSpa;

public final class SpaDTO {
    private SpaDTO() {}

    public static final class DongChiTietSpa {
        private final String tenDichVu;
        private final long donGia;
        private final int soLuong;
        private final long thanhTien;

        public DongChiTietSpa(String tenDichVu, long donGia, int soLuong, long thanhTien) {
            this.tenDichVu = tenDichVu;
            this.donGia = donGia;
            this.soLuong = soLuong;
            this.thanhTien = thanhTien;
        }

        public String getTenDichVu() {
            return tenDichVu;
        }

        public long getDonGia() {
            return donGia;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public long getThanhTien() {
            return thanhTien;
        }
    }

    public static final class DongTimKiemSpa {
        private final String maKhoa;
        private final PhieuTiepNhanSpa phieuSpa;
        private final KhachHang khachHang;
        private final ThuCung thuCung;
        private final NhanVien nhanVien;

        public DongTimKiemSpa(
                String maKhoa,
                PhieuTiepNhanSpa phieuSpa,
                KhachHang khachHang,
                ThuCung thuCung,
                NhanVien nhanVien) {
            this.maKhoa = maKhoa;
            this.phieuSpa = phieuSpa;
            this.khachHang = khachHang;
            this.thuCung = thuCung;
            this.nhanVien = nhanVien;
        }

        public String getMaKhoa() {
            return maKhoa;
        }

        public PhieuTiepNhanSpa getPhieuSpa() {
            return phieuSpa;
        }

        public KhachHang getKhachHang() {
            return khachHang;
        }

        public ThuCung getThuCung() {
            return thuCung;
        }

        public NhanVien getNhanVien() {
            return nhanVien;
        }
    }

    public static final class DongPhieuSpaTreo {
        private final String maKhoa;
        private final PhieuSpaTreo phieuTreo;

        public DongPhieuSpaTreo(String maKhoa, PhieuSpaTreo phieuTreo) {
            this.maKhoa = maKhoa;
            this.phieuTreo = phieuTreo;
        }

        public String getMaKhoa() {
            return maKhoa;
        }

        public PhieuSpaTreo getPhieuTreo() {
            return phieuTreo;
        }
    }

    public static final class ThongTinBuocSpa {
        private final String trangThai;
        private final boolean coDichVu;

        public ThongTinBuocSpa(String trangThai, boolean coDichVu) {
            this.trangThai = trangThai;
            this.coDichVu = coDichVu;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public boolean isCoDichVu() {
            return coDichVu;
        }
    }
}
