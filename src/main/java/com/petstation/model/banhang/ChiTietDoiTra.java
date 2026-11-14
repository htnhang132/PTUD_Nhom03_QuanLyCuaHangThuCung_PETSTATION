package com.petstation.model.banhang;

import com.petstation.model.kho.LoHang;

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
        setTienHoan(tienHoan);
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return this.phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
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

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) throw new IllegalArgumentException("Số lượng đổi trả không được âm.");
        this.soLuong = soLuong;
    }

    public long getTienHoan() {
        return this.tienHoan;
    }

    public void setTienHoan(long tienHoan) {
        if (tienHoan < 0) throw new IllegalArgumentException("Tiền hoàn không được âm.");
        this.tienHoan = tienHoan;
    }

    public long tinhTienHoan() {
        if (chiTietHoaDon != null && chiTietHoaDon.getDonGia() > 0 && soLuong > 0) {
            return (long) soLuong * chiTietHoaDon.getDonGia();
        }
        return Math.max(0L, this.tienHoan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
        return "ChiTietDoiTra{"
                + "phieuDoiTra="
                + (phieuDoiTra != null ? phieuDoiTra.getMaPhieu() : null)
                + ", chiTietHoaDon="
                + chiTietHoaDon
                + ", soLuong="
                + soLuong
                + ", tienHoan="
                + tinhTienHoan()
                + '}';
    }
}
