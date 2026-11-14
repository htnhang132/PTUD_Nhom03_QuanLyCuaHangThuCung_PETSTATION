package com.petstation.model.kho;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietKiemKe implements Serializable {

    private static final long serialVersionUID = 1L;
    private BienBanKiemKe bienBan;
    private LoHang loHang;
    private int tonHeThong;
    private int tonThucTe;
    private String nguyenNhan;

    public ChiTietKiemKe() {}

    public ChiTietKiemKe(
            BienBanKiemKe bienBan,
            LoHang loHang,
            int tonHeThong,
            int tonThucTe,
            String nguyenNhan) {
        setBienBan(bienBan);
        setLoHang(loHang);
        setTonHeThong(tonHeThong);
        setTonThucTe(tonThucTe);
        setNguyenNhan(nguyenNhan);
    }

    public BienBanKiemKe getBienBan() {
        return this.bienBan;
    }

    public void setBienBan(BienBanKiemKe bienBan) {
        this.bienBan = bienBan;
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

    public int getTonHeThong() {
        return this.tonHeThong;
    }

    public void setTonHeThong(int tonHeThong) {
        if (tonHeThong < 0) throw new IllegalArgumentException("Tồn hệ thống không được âm.");
        this.tonHeThong = tonHeThong;
    }

    public int getTonThucTe() {
        return this.tonThucTe;
    }

    public void setTonThucTe(int tonThucTe) {
        if (tonThucTe < 0) throw new IllegalArgumentException("Tồn thực tế không được âm.");
        this.tonThucTe = tonThucTe;
    }

    public String getNguyenNhan() {
        return this.nguyenNhan;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    public int getChenhLech() {
        return tinhChenhLech();
    }

    public int tinhChenhLech() {
        return tonThucTe - tonHeThong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietKiemKe that = (ChiTietKiemKe) o;
        return Objects.equals(this.bienBan, that.bienBan)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bienBan, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietKiemKe{"
                + "bienBan="
                + (bienBan != null ? bienBan.getMaBienBan() : null)
                + ", loHang="
                + (loHang != null ? loHang.getMaLo() : null)
                + ", tonHeThong="
                + tonHeThong
                + ", tonThucTe="
                + tonThucTe
                + ", chenhLech="
                + tinhChenhLech()
                + '}';
    }
}
