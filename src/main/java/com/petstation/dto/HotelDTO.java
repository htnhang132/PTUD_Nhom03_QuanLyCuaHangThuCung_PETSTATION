package com.petstation.dto;

import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class HotelDTO {
    private HotelDTO() {}

    public static class LichPhong implements Serializable {
        private static final long serialVersionUID = 1L;
        private HopDongLuuTru hopDong;
        private ThuCung thuCung;
        private KhachHang khachHang;
        private LocalDateTime ngayVao;
        private LocalDateTime ngayRa;

        public LichPhong() {}

        public LichPhong(HopDongLuuTru hopDong) {
            this.hopDong = hopDong;
        }

        public LichPhong(
                HopDongLuuTru hopDong,
                ThuCung thuCung,
                KhachHang khachHang,
                LocalDateTime ngayVao,
                LocalDateTime ngayRa) {
            this.hopDong = hopDong;
            this.thuCung = thuCung;
            this.khachHang = khachHang;
            this.ngayVao = ngayVao;
            this.ngayRa = ngayRa;
        }

        public HopDongLuuTru getHopDong() {
            return this.hopDong;
        }

        public ThuCung getThuCung() {
            return this.thuCung;
        }

        public KhachHang getKhachHang() {
            return this.khachHang;
        }

        public LocalDateTime getNgayVao() {
            return this.ngayVao;
        }

        public LocalDateTime getNgayRa() {
            return this.ngayRa;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LichPhong that = (LichPhong) o;
            return Objects.equals(this.hopDong, that.hopDong);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hopDong);
        }
    }

    public static final class ChiTietLichPhong {
        private final PhongHotel phong;
        private final LichPhong lichPhong;
        private final String trangThaiHopDong;

        public ChiTietLichPhong(PhongHotel phong, LichPhong lichPhong, String trangThaiHopDong) {
            this.phong = phong;
            this.lichPhong = lichPhong;
            this.trangThaiHopDong = trangThaiHopDong;
        }

        public PhongHotel getPhong() {
            return phong;
        }

        public LichPhong getLichPhong() {
            return lichPhong;
        }

        public String getTrangThaiHopDong() {
            return trangThaiHopDong;
        }
    }

    public static final class TomTatPhong {
        private final String maKhoa;
        private final PhongHotel phong;
        private final HopDongLuuTru hopDongUuTien;
        private final int soDatTruoc;

        public TomTatPhong(
                String maKhoa, PhongHotel phong, HopDongLuuTru hopDongUuTien, int soDatTruoc) {
            this.maKhoa = maKhoa;
            this.phong = phong;
            this.hopDongUuTien = hopDongUuTien;
            this.soDatTruoc = soDatTruoc;
        }

        public String getMaKhoa() {
            return maKhoa;
        }

        public PhongHotel getPhong() {
            return phong;
        }

        public HopDongLuuTru getHopDongUuTien() {
            return hopDongUuTien;
        }

        public int getSoDatTruoc() {
            return soDatTruoc;
        }
    }

    public static final class ThongTinDatPhong {
        private final ThuCung thuCung;
        private final PhongHotel phong;
        private final String trangThai;
        private final LocalDateTime ngayVao;
        private final LocalDateTime ngayRaDuKien;
        private final String ghiChu;
        private final long tienCoc;
        private final boolean daNhanCoc;
        private final long cocYeuCau;
        private final long giaDem;

        public ThongTinDatPhong(
                ThuCung thuCung,
                PhongHotel phong,
                String trangThai,
                LocalDateTime ngayVao,
                LocalDateTime ngayRaDuKien,
                String ghiChu,
                long tienCoc,
                boolean daNhanCoc,
                long cocYeuCau,
                long giaDem) {
            this.thuCung = thuCung;
            this.phong = phong;
            this.trangThai = trangThai;
            this.ngayVao = ngayVao;
            this.ngayRaDuKien = ngayRaDuKien;
            this.ghiChu = ghiChu;
            this.tienCoc = tienCoc;
            this.daNhanCoc = daNhanCoc;
            this.cocYeuCau = cocYeuCau;
            this.giaDem = giaDem;
        }

        public ThuCung getThuCung() {
            return thuCung;
        }

        public PhongHotel getPhong() {
            return phong;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public LocalDateTime getNgayVao() {
            return ngayVao;
        }

        public LocalDateTime getNgayRaDuKien() {
            return ngayRaDuKien;
        }

        public String getGhiChu() {
            return ghiChu;
        }

        public long getTienCoc() {
            return tienCoc;
        }

        public boolean isDaNhanCoc() {
            return daNhanCoc;
        }

        public long getCocYeuCau() {
            return cocYeuCau;
        }

        public long getGiaDem() {
            return giaDem;
        }
    }

    public static final class DongNhatKyChiTiet {
        private final NhanVien nhanVien;
        private final LocalDateTime thoiGian;
        private final String anUong;
        private final String daThucHien;
        private final String tinhTrang;

        public DongNhatKyChiTiet(
                NhanVien nhanVien,
                LocalDateTime thoiGian,
                String anUong,
                String daThucHien,
                String tinhTrang) {
            this.nhanVien = nhanVien;
            this.thoiGian = thoiGian;
            this.anUong = anUong;
            this.daThucHien = daThucHien;
            this.tinhTrang = tinhTrang;
        }

        public NhanVien getNhanVien() {
            return nhanVien;
        }

        public LocalDateTime getThoiGian() {
            return thoiGian;
        }

        public String getAnUong() {
            return anUong;
        }

        public String getDaThucHien() {
            return daThucHien;
        }

        public String getTinhTrang() {
            return tinhTrang;
        }
    }

    public static final class DongLichSuCoc {
        private final String phuongThuc;
        private final long soTien;
        private final long khachDua;
        private final long tienThua;
        private final LocalDateTime taoLuc;
        private final String ca;

        public DongLichSuCoc(
                String phuongThuc,
                long soTien,
                long khachDua,
                long tienThua,
                LocalDateTime taoLuc,
                String ca) {
            this.phuongThuc = phuongThuc;
            this.soTien = soTien;
            this.khachDua = khachDua;
            this.tienThua = tienThua;
            this.taoLuc = taoLuc;
            this.ca = ca;
        }

        public String getPhuongThuc() {
            return phuongThuc;
        }

        public long getSoTien() {
            return soTien;
        }

        public long getKhachDua() {
            return khachDua;
        }

        public long getTienThua() {
            return tienThua;
        }

        public LocalDateTime getTaoLuc() {
            return taoLuc;
        }

        public String getCa() {
            return ca;
        }
    }
}
