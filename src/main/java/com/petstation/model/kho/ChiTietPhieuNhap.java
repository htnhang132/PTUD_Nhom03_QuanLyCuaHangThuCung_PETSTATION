package com.petstation.model.kho;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ChiTietPhieuNhap implements Serializable {

    private static final long serialVersionUID = 1L;
    private PhieuNhapKho phieuNhap;
    private SanPham sanPham;
    private LoHang loHang;
    private int soLuong;
    private long giaNhap;
    private LocalDate hanSuDung;

    public ChiTietPhieuNhap() {
        this.soLuong = 1;
    }

    public ChiTietPhieuNhap(
            PhieuNhapKho phieuNhap, SanPham sanPham, LoHang loHang, int soLuong, long giaNhap) {
        setPhieuNhap(phieuNhap);
        setSanPham(sanPham);
        setLoHang(loHang);
        setSoLuong(soLuong);
        setGiaNhap(giaNhap);
    }

    public PhieuNhapKho getPhieuNhap() {
        return this.phieuNhap;
    }

    public void setPhieuNhap(PhieuNhapKho phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) throw new IllegalArgumentException("Số lượng nhập không được âm.");
        this.soLuong = soLuong;
    }

    public long getGiaNhap() {
        return this.giaNhap;
    }

    public void setGiaNhap(long giaNhap) {
        if (giaNhap < 0) throw new IllegalArgumentException("Giá nhập không được âm.");
        this.giaNhap = giaNhap;
    }

    public LocalDate getHanSuDung() {
        return this.hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public long tinhThanhTien() {
        return (long) Math.max(0, soLuong) * Math.max(0L, giaNhap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(this.phieuNhap, that.phieuNhap)
                && Objects.equals(this.sanPham, that.sanPham)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuNhap, sanPham, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{"
                + "phieuNhap="
                + (phieuNhap != null ? phieuNhap.getMaPhieuNhap() : null)
                + ", sanPham="
                + (sanPham != null ? sanPham.getMaSanPham() : null)
                + ", soLo='"
                + (loHang != null ? loHang.getSoLo() : null)
                + '\''
                + ", soLuong="
                + soLuong
                + ", giaNhap="
                + giaNhap
                + ", thanhTien="
                + tinhThanhTien()
                + '}';
    }
}
