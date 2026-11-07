package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class ChiTietKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    private BienBanKiemKe bienBan;

    private LoHang loHang;
    private int tonHeThong;
    private int tonThucTe;
    private String nguyenNhan;

    public ChiTietKiemKe() {
    }

    public ChiTietKiemKe(long id) {
        setId(id);
    }

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

    public ChiTietKiemKe(
            long id,
            BienBanKiemKe bienBan,
            LoHang loHang,
            int tonHeThong,
            int tonThucTe,
            String nguyenNhan) {
        this(bienBan, loHang, tonHeThong, tonThucTe, nguyenNhan);
        setId(id);
    }

    public ChiTietKiemKe(
            long id,
            long bienBanId,
            long loHangId,
            int tonHeThong,
            int tonThucTe,
            String nguyenNhan) {
        this(id, new BienBanKiemKe(bienBanId), new LoHang(loHangId), tonHeThong, tonThucTe, nguyenNhan);
    }

    public int tinhChenhLech() {
        return tonThucTe - tonHeThong;
    }

    public int getChenhLech() {
        return tinhChenhLech();
    }

    public boolean kiemTraCoLechKho() {
        return tonThucTe != tonHeThong;
    }

    public Long getId() {
        String key = (bienBan != null ? bienBan.getSoBienBan() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
    }

    public BienBanKiemKe getBienBan() {
        return this.bienBan;
    }

    public void setBienBan(BienBanKiemKe bienBan) {
        this.bienBan = bienBan;
    }

    public long getBienBanId() {
        return bienBan == null ? 0L : bienBan.getId();
    }

    public void setBienBanId(long bienBanId) {
        this.bienBan = new BienBanKiemKe(bienBanId);
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public long getLoHangId() {
        return loHang == null ? 0L : loHang.getId();
    }

    public void setLoHangId(long loHangId) {
        this.loHang = new LoHang(loHangId);
    }

    public int getTonHeThong() {
        return this.tonHeThong;
    }

    public void setTonHeThong(int tonHeThong) {
        if (tonHeThong < 0)
            throw new IllegalArgumentException("Tồn hệ thống không được âm.");
        this.tonHeThong = tonHeThong;
    }

    public int getTonThucTe() {
        return this.tonThucTe;
    }

    public void setTonThucTe(int tonThucTe) {
        if (tonThucTe < 0)
            throw new IllegalArgumentException("Tồn thực tế không được âm.");
        this.tonThucTe = tonThucTe;
    }

    public String getNguyenNhan() {
        return this.nguyenNhan;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
        return "ChiTietKiemKe{" + "bienBanId=" + getBienBanId() + ", loHangId=" + getLoHangId()
                + ", tonHeThong=" + tonHeThong + ", tonThucTe=" + tonThucTe + ", chenhLech=" + tinhChenhLech() + '}';
    }
}
