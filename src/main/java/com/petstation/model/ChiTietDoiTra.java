package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;
    private LoHang loHang;
    private int soLuong;
    private long tienHoan;

    public ChiTietDoiTra() {
        this.soLuong = 1;
    }

    public ChiTietDoiTra(
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon,
            LoHang loHang,
            int soLuong,
            long tienHoan) {
        setPhieuDoiTra(phieuDoiTra);
        setChiTietHoaDon(chiTietHoaDon);
        setLoHang(loHang);
        setSoLuong(soLuong);
        this.tienHoan = Math.max(0L, tienHoan);
    }

    public long tinhTienHoan() {
        if (chiTietHoaDon != null && chiTietHoaDon.getDonGia() > 0 && soLuong > 0) {
            return (long) soLuong * chiTietHoaDon.getDonGia();
        }
        return Math.max(0L, this.tienHoan);
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return this.phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public String getMaPhieuDoiTra() {
        return phieuDoiTra == null ? null : phieuDoiTra.getMaPhieu();
    }

    public void setMaPhieuDoiTra(String maPhieu) {
        this.phieuDoiTra = (maPhieu == null || maPhieu.isBlank()) ? null : new PhieuDoiTra(maPhieu);
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return this.chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public String getMaLo() {
        return loHang == null ? null : loHang.getMaLo();
    }

    public void setMaLo(String maLo) {
        this.loHang = (maLo == null || maLo.isBlank()) ? null : new LoHang(maLo);
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng đổi trả không được âm.");
        this.soLuong = soLuong;
    }

    public long getTienHoan() {
        return tinhTienHoan();
    }

    public void setTienHoan(long tienHoan) {
        this.tienHoan = Math.max(0L, tienHoan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietDoiTra that = (ChiTietDoiTra) o;
        return Objects.equals(this.phieuDoiTra, that.phieuDoiTra)
                && Objects.equals(this.chiTietHoaDon, that.chiTietHoaDon)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuDoiTra, chiTietHoaDon, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietDoiTra{" + "phieuDoiTra=" + (phieuDoiTra != null ? phieuDoiTra.getMaPhieu() : null)
                + ", chiTietHoaDon=" + (chiTietHoaDon != null ? chiTietHoaDon.getMaChiTietHoaDon() : null)
                + ", soLuong=" + soLuong + ", tienHoan=" + tinhTienHoan() + '}';
    }
}
