package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ChiTietPhieuNhap implements Serializable {
    private static final long serialVersionUID = 1L;

    private PhieuNhapKho phieuNhap;
    private SanPham sanPham;
    private String maLo;
    private String soLo;
    private LocalDate hanSuDung;
    private int soLuong;
    private long giaNhap;

    public ChiTietPhieuNhap() {
        this.soLuong = 1;
    }

    public ChiTietPhieuNhap(
            PhieuNhapKho phieuNhap,
            SanPham sanPham,
            String soLo,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap) {
        setPhieuNhap(phieuNhap);
        setSanPham(sanPham);
        setSoLo(soLo);
        setHanSuDung(hanSuDung);
        setSoLuong(soLuong);
        setGiaNhap(giaNhap);
    }

    public long tinhThanhTien() {
        return (long) Math.max(0, soLuong) * Math.max(0L, giaNhap);
    }

    public long getThanhTien() {
        return tinhThanhTien();
    }

    public PhieuNhapKho getPhieuNhap() {
        return this.phieuNhap;
    }

    public void setPhieuNhap(PhieuNhapKho phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    public String getMaPhieuNhap() {
        return phieuNhap == null ? null : phieuNhap.getMaPhieuNhap();
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.phieuNhap = (maPhieuNhap == null || maPhieuNhap.isBlank()) ? null : new PhieuNhapKho(maPhieuNhap);
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public String getMaSanPham() {
        return sanPham == null ? null : sanPham.getMaSanPham();
    }

    public void setMaSanPham(String maSanPham) {
        this.sanPham = (maSanPham == null || maSanPham.isBlank()) ? null : new SanPham(maSanPham);
    }

    public String getMaLo() {
        return this.maLo != null ? this.maLo : this.soLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo == null ? null : maLo.trim();
        if (this.soLo == null) {
            this.soLo = this.maLo;
        }
    }

    public String getSoLo() {
        return this.soLo != null ? this.soLo : this.maLo;
    }

    public void setSoLo(String soLo) {
        this.soLo = soLo == null ? null : soLo.trim();
    }

    public LocalDate getHanSuDung() {
        return this.hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng nhập không được âm.");
        this.soLuong = soLuong;
    }

    public long getGiaNhap() {
        return this.giaNhap;
    }

    public void setGiaNhap(long giaNhap) {
        if (giaNhap < 0)
            throw new IllegalArgumentException("Giá nhập không được âm.");
        this.giaNhap = giaNhap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(this.phieuNhap, that.phieuNhap)
                && Objects.equals(this.sanPham, that.sanPham)
                && Objects.equals(this.getMaLo(), that.getMaLo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuNhap, sanPham, getMaLo());
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" + "phieuNhap=" + (phieuNhap != null ? phieuNhap.getMaPhieuNhap() : null)
                + ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : null)
                + ", soLo='" + soLo + '\'' + ", soLuong=" + soLuong + ", giaNhap=" + giaNhap + ", thanhTien="
                + tinhThanhTien() + '}';
    }
}
