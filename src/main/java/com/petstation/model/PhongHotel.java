package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Dữ liệu sơ đồ phòng và lịch đang sử dụng (bảng phong_hotel). */
public class PhongHotel implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String ma;
    private String loai;
    private long gia;
    private String trangThai;
    private int sucChua;
    private LichPhong dangO;
    private java.util.List<LichPhong> datTruoc;

    public PhongHotel() {}

    public PhongHotel(long id, String ma, String loai, long gia, String trangThai, int sucChua, LichPhong dangO, java.util.List<LichPhong> datTruoc) {
        this.id = id;
        this.ma = ma;
        this.loai = loai;
        this.gia = gia;
        this.trangThai = trangThai;
        this.sucChua = sucChua;
        this.dangO = dangO;
        this.datTruoc = datTruoc;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMa() { return this.ma; }
    public String layMa() { return this.ma; }
    public String ma() { return this.ma; }
    public void setMa(String ma) { this.ma = ma; }
    public void datMa(String ma) { this.ma = ma; }

    public String getLoai() { return this.loai; }
    public String layLoai() { return this.loai; }
    public String loai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public void datLoai(String loai) { this.loai = loai; }

    public long getGia() { return this.gia; }
    public long layGia() { return this.gia; }
    public long gia() { return this.gia; }
    public void setGia(long gia) { this.gia = gia; }
    public void datGia(long gia) { this.gia = gia; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getSucChua() { return this.sucChua; }
    public int laySucChua() { return this.sucChua; }
    public int sucChua() { return this.sucChua; }
    public void setSucChua(int sucChua) { this.sucChua = sucChua; }
    public void datSucChua(int sucChua) { this.sucChua = sucChua; }

    public LichPhong getDangO() { return this.dangO; }
    public LichPhong layDangO() { return this.dangO; }
    public LichPhong dangO() { return this.dangO; }
    public void setDangO(LichPhong dangO) { this.dangO = dangO; }
    public void datDangO(LichPhong dangO) { this.dangO = dangO; }

    public java.util.List<LichPhong> getDatTruoc() { return this.datTruoc; }
    public java.util.List<LichPhong> layDatTruoc() { return this.datTruoc; }
    public java.util.List<LichPhong> datTruoc() { return this.datTruoc; }
    public void setDatTruoc(java.util.List<LichPhong> datTruoc) { this.datTruoc = datTruoc; }
    public void datDatTruoc(java.util.List<LichPhong> datTruoc) { this.datTruoc = datTruoc; }

    public String layNhanTrangThai() {
        return switch (trangThai != null ? trangThai : "") {
            case "AVAILABLE" -> "Trống";
            case "OCCUPIED" -> "Có thú";
            case "CLEANING" -> "Đang dọn";
            case "MAINTENANCE" -> "Bảo trì";
            default -> "Ngừng hoạt động";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhongHotel that = (PhongHotel) o;
        return this.id == that.id
                && Objects.equals(ma, that.ma)
                && Objects.equals(loai, that.loai)
                && this.gia == that.gia
                && Objects.equals(trangThai, that.trangThai)
                && this.sucChua == that.sucChua
                && Objects.equals(dangO, that.dangO)
                && Objects.equals(datTruoc, that.datTruoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ma, loai, gia, trangThai, sucChua, dangO, datTruoc);
    }

    @Override
    public String toString() {
        return "PhongHotel{" + "id=" + id + ", " + "ma=" + ma + ", " + "loai=" + loai + ", " + "gia=" + gia + ", " + "trangThai=" + trangThai + ", " + "sucChua=" + sucChua + ", " + "dangO=" + dangO + ", " + "datTruoc=" + datTruoc + "}";
    }

    public static class LichPhong implements Serializable {
        private static final long serialVersionUID = 1L;

        private long id;
        private String thuCung;
        private String khachHang;
        private java.time.LocalDateTime ngayVao;
        private java.time.LocalDateTime ngayRa;

        public LichPhong() {}

        public LichPhong(long id, String thuCung, String khachHang, java.time.LocalDateTime ngayVao, java.time.LocalDateTime ngayRa) {
            this.id = id;
            this.thuCung = thuCung;
            this.khachHang = khachHang;
            this.ngayVao = ngayVao;
            this.ngayRa = ngayRa;
        }

        public long getId() { return this.id; }
        public long layId() { return this.id; }
        public long id() { return this.id; }
        public void setId(long id) { this.id = id; }
        public void datId(long id) { this.id = id; }

        public String getThuCung() { return this.thuCung; }
        public String layThuCung() { return this.thuCung; }
        public String thuCung() { return this.thuCung; }
        public void setThuCung(String thuCung) { this.thuCung = thuCung; }
        public void datThuCung(String thuCung) { this.thuCung = thuCung; }

        public String getKhachHang() { return this.khachHang; }
        public String layKhachHang() { return this.khachHang; }
        public String khachHang() { return this.khachHang; }
        public void setKhachHang(String khachHang) { this.khachHang = khachHang; }
        public void datKhachHang(String khachHang) { this.khachHang = khachHang; }

        public java.time.LocalDateTime getNgayVao() { return this.ngayVao; }
        public java.time.LocalDateTime layNgayVao() { return this.ngayVao; }
        public java.time.LocalDateTime ngayVao() { return this.ngayVao; }
        public void setNgayVao(java.time.LocalDateTime ngayVao) { this.ngayVao = ngayVao; }
        public void datNgayVao(java.time.LocalDateTime ngayVao) { this.ngayVao = ngayVao; }

        public java.time.LocalDateTime getNgayRa() { return this.ngayRa; }
        public java.time.LocalDateTime layNgayRa() { return this.ngayRa; }
        public java.time.LocalDateTime ngayRa() { return this.ngayRa; }
        public void setNgayRa(java.time.LocalDateTime ngayRa) { this.ngayRa = ngayRa; }
        public void datNgayRa(java.time.LocalDateTime ngayRa) { this.ngayRa = ngayRa; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LichPhong that = (LichPhong) o;
            return this.id == that.id
                    && Objects.equals(thuCung, that.thuCung)
                    && Objects.equals(khachHang, that.khachHang)
                    && Objects.equals(ngayVao, that.ngayVao)
                    && Objects.equals(ngayRa, that.ngayRa);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, thuCung, khachHang, ngayVao, ngayRa);
        }

        @Override
        public String toString() {
            return "LichPhong{" + "id=" + id + ", " + "thuCung=" + thuCung + ", " + "khachHang=" + khachHang + ", " + "ngayVao=" + ngayVao + ", " + "ngayRa=" + ngayRa + "}";
        }
    }
}
