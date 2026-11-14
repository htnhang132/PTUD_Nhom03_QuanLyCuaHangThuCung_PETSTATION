package com.petstation.model.khachhang;

import com.petstation.model.chung.TrangThaiKhuyenMai;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChuongTrinhKhuyenMai implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maChuongTrinh;
    private String ten;
    private String loaiGiam;
    private double giaTri;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String nguon;
    private String trangThai;
    private long donToiThieu;

    public ChuongTrinhKhuyenMai() {
        this.trangThai = TrangThaiKhuyenMai.HOAT_DONG.maDB();
    }

    public ChuongTrinhKhuyenMai(String maChuongTrinh) {
        this();
        setMaChuongTrinh(maChuongTrinh);
    }

    public ChuongTrinhKhuyenMai(
            String maChuongTrinh,
            String ten,
            String loaiGiam,
            double giaTri,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String nguon,
            String trangThai,
            long donToiThieu) {
        this();
        setMaChuongTrinh(maChuongTrinh);
        setTen(ten);
        setLoaiGiam(loaiGiam);
        setGiaTri(giaTri);
        setBatDau(batDau);
        setKetThuc(ketThuc);
        setNguon(nguon);
        setTrangThai(trangThai);
        setDonToiThieu(donToiThieu);
    }

    public String getMaChuongTrinh() {
        return this.maChuongTrinh;
    }

    public void setMaChuongTrinh(String maChuongTrinh) {
        this.maChuongTrinh = maChuongTrinh == null ? null : maChuongTrinh.trim();
    }

    public String getTen() {
        return this.ten;
    }

    public void setTen(String ten) {
        this.ten = ten == null ? null : ten.trim();
    }

    public String getLoaiGiam() {
        return this.loaiGiam;
    }

    public void setLoaiGiam(String loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public double getGiaTri() {
        return this.giaTri;
    }

    public void setGiaTri(double giaTri) {
        if (giaTri < 0) throw new IllegalArgumentException("Giá trị giảm không được âm.");
        this.giaTri = giaTri;
    }

    public LocalDateTime getBatDau() {
        return this.batDau;
    }

    public void setBatDau(LocalDateTime batDau) {
        this.batDau = batDau;
    }

    public LocalDateTime getKetThuc() {
        return this.ketThuc;
    }

    public void setKetThuc(LocalDateTime ketThuc) {
        this.ketThuc = ketThuc;
    }

    public String getNguon() {
        return this.nguon;
    }

    public void setNguon(String nguon) {
        this.nguon = nguon;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getDonToiThieu() {
        return this.donToiThieu;
    }

    public void setDonToiThieu(long donToiThieu) {
        if (donToiThieu < 0) throw new IllegalArgumentException("Đơn tối thiểu không được âm.");
        this.donToiThieu = donToiThieu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChuongTrinhKhuyenMai that = (ChuongTrinhKhuyenMai) o;
        return Objects.equals(this.maChuongTrinh, that.maChuongTrinh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChuongTrinh);
    }

    @Override
    public String toString() {
        return "ChuongTrinhKhuyenMai{"
                + "maChuongTrinh='"
                + maChuongTrinh
                + '\''
                + ", ten='"
                + ten
                + '\''
                + ", loaiGiam='"
                + loaiGiam
                + '\''
                + ", giaTri="
                + giaTri
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
