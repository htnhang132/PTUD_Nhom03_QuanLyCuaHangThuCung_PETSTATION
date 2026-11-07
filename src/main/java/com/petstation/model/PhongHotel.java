package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PhongHotel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ma;
    private String loai;
    private long gia;
    private String trangThai;
    private int sucChua;
    private LichPhong dangO;
    private List<LichPhong> datTruoc;

    public PhongHotel() {
        this.trangThai = "TRONG";
        this.sucChua = 1;
    }

    public PhongHotel(String ma) {
        this();
        setMa(ma);
    }

    public PhongHotel(
            String ma,
            String loai,
            long gia,
            String trangThai,
            int sucChua,
            LichPhong dangO,
            List<LichPhong> datTruoc) {
        this();
        setMa(ma);
        setLoai(loai);
        setGia(gia);
        setTrangThai(trangThai);
        setSucChua(sucChua);
        setDangO(dangO);
        setDatTruoc(datTruoc);
    }

    public String getNhanTrangThai() {
        return switch (trangThai != null ? trangThai : "") {
            case "TRONG" -> "Trống";
            case "DANG_O" -> "Có thú";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI" -> "Bảo trì";
            default -> "Ngừng hoạt động";
        };
    }

    public boolean kiemTraPhongTrong() {
        return "TRONG".equalsIgnoreCase(trangThai);
    }

    public long tinhTienTheoSoDem(int soDem) {
        return Math.max(0, soDem) * gia;
    }

    public String getMa() {
        return this.ma;
    }

    public String getMaPhong() {
        return this.ma;
    }

    public void setMa(String ma) {
        this.ma = ma == null ? null : ma.trim();
    }

    public void setMaPhong(String maPhong) {
        setMa(maPhong);
    }

    public String getLoai() {
        return this.loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public long getGia() {
        return this.gia;
    }

    public void setGia(long gia) {
        if (gia < 0) throw new IllegalArgumentException("Giá phòng không được âm.");
        this.gia = gia;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getSucChua() {
        return this.sucChua;
    }

    public void setSucChua(int sucChua) {
        if (sucChua < 0) throw new IllegalArgumentException("Sức chứa phòng không được âm.");
        this.sucChua = forcePositive(sucChua);
    }

    private int forcePositive(int val) {
        return Math.max(1, val);
    }

    public LichPhong getDangO() {
        return this.dangO;
    }

    public void setDangO(LichPhong dangO) {
        this.dangO = dangO;
    }

    public List<LichPhong> getDatTruoc() {
        return this.datTruoc;
    }

    public void setDatTruoc(List<LichPhong> datTruoc) {
        this.datTruoc = datTruoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhongHotel that = (PhongHotel) o;
        return Objects.equals(this.ma, that.ma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ma);
    }

    @Override
    public String toString() {
        return "PhongHotel{" + "ma='" + ma + '\'' + ", loai='" + loai + '\'' + ", gia=" + gia + ", trangThai='" + trangThai + '\'' + '}';
    }

    public static class LichPhong implements Serializable {
        private static final long serialVersionUID = 1L;

        private String maHopDong;
        private String thuCung;
        private String khachHang;
        private LocalDateTime ngayVao;
        private LocalDateTime ngayRa;

        public LichPhong() {}

        public LichPhong(String maHopDong) {
            this.maHopDong = maHopDong;
        }

        public LichPhong(String maHopDong, String thuCung, String khachHang, LocalDateTime ngayVao, LocalDateTime ngayRa) {
            this.maHopDong = maHopDong;
            this.thuCung = thuCung;
            this.khachHang = khachHang;
            this.ngayVao = ngayVao;
            this.ngayRa = ngayRa;
        }

        public String getMaHopDong() {
            return this.maHopDong;
        }

        public void setMaHopDong(String maHopDong) {
            this.maHopDong = maHopDong;
        }

        public String getThuCung() {
            return this.thuCung;
        }

        public void setThuCung(String thuCung) {
            this.thuCung = thuCung;
        }

        public String getKhachHang() {
            return this.khachHang;
        }

        public void setKhachHang(String khachHang) {
            this.khachHang = khachHang;
        }

        public LocalDateTime getNgayVao() {
            return this.ngayVao;
        }

        public void setNgayVao(LocalDateTime ngayVao) {
            this.ngayVao = ngayVao;
        }

        public LocalDateTime getNgayRa() {
            return this.ngayRa;
        }

        public void setNgayRa(LocalDateTime ngayRa) {
            this.ngayRa = ngayRa;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LichPhong that = (LichPhong) o;
            return Objects.equals(this.maHopDong, that.maHopDong);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maHopDong);
        }

        @Override
        public String toString() {
            return "LichPhong{" + "maHopDong='" + maHopDong + '\'' + ", thuCung='" + thuCung + '\'' + ", khachHang='" + khachHang + '\'' + '}';
        }
    }
}
