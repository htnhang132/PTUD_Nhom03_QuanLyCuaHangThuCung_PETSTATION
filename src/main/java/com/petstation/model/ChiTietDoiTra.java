package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

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

    public ChiTietDoiTra(long id) {
        this();
        setId(id);
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

    public ChiTietDoiTra(
            long id,
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon,
            LoHang loHang,
            int soLuong,
            long tienHoan) {
        this(phieuDoiTra, chiTietHoaDon, loHang, soLuong, tienHoan);
        setId(id);
    }

    public ChiTietDoiTra(
            long id,
            long phieuDoiTraId,
            long chiTietHoaDonId,
            Long loHangId,
            int soLuong,
            long tienHoan) {
        this(
                id,
                new PhieuDoiTra(phieuDoiTraId),
                new ChiTietHoaDon(chiTietHoaDonId),
                loHangId == null ? null : new LoHang(loHangId),
                soLuong,
                tienHoan);
    }

    public long tinhTienHoan() {
        if (chiTietHoaDon != null && chiTietHoaDon.getDonGia() > 0 && soLuong > 0) {
            return (long) soLuong * chiTietHoaDon.getDonGia();
        }
        return Math.max(0L, this.tienHoan);
    }

    public Long getId() {
        String key = (phieuDoiTra != null ? phieuDoiTra.getMaPhieu() : "") + "_"
                + (chiTietHoaDon != null ? chiTietHoaDon.getMaChiTietHoaDon() : "") + "_"
                + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return this.phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public long getPhieuDoiTraId() {
        return phieuDoiTra == null ? 0L : phieuDoiTra.getId();
    }

    public void setPhieuDoiTraId(long phieuDoiTraId) {
        this.phieuDoiTra = new PhieuDoiTra(phieuDoiTraId);
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return this.chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public long getChiTietHoaDonId() {
        return chiTietHoaDon == null ? 0L : chiTietHoaDon.getId();
    }

    public void setChiTietHoaDonId(long chiTietHoaDonId) {
        this.chiTietHoaDon = new ChiTietHoaDon(chiTietHoaDonId);
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public Long getLoHangId() {
        return loHang == null ? null : loHang.getId();
    }

    public void setLoHangId(Long loHangId) {
        this.loHang = loHangId == null ? null : new LoHang(loHangId);
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

    void napTienHoanSnapshot(long tienHoan) {
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
        return "ChiTietDoiTra{" + "phieuDoiTraId=" + getPhieuDoiTraId() + ", chiTietHoaDonId="
                + getChiTietHoaDonId() + ", soLuong=" + soLuong + ", tienHoan=" + tinhTienHoan() + '}';
    }
}
