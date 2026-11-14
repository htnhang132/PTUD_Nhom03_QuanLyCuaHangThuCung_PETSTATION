package com.petstation.model.kho;

import com.petstation.model.chung.TrangThaiLoHang;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class LoHang implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLo;
    private SanPham sanPham;
    private String soLo;
    private LocalDate ngayNhap;
    private LocalDate hanSuDung;
    private long giaNhap;
    private int soLuongNhap;
    private int soLuongTon;
    private String trangThai;

    public LoHang() {
        this.ngayNhap = LocalDate.now();
        this.trangThai = TrangThaiLoHang.SAN_SANG.maDB();
    }

    public LoHang(String maLo) {
        this();
        setMaLo(maLo);
    }

    public LoHang(
            String maLo,
            SanPham sanPham,
            String soLo,
            LocalDate ngayNhap,
            LocalDate hanSuDung,
            long giaNhap,
            int soLuongNhap,
            int soLuongTon,
            String trangThai) {
        this();
        setMaLo(maLo);
        setSanPham(sanPham);
        setSoLo(soLo);
        setNgayNhap(ngayNhap);
        setHanSuDung(hanSuDung);
        setGiaNhap(giaNhap);
        setSoLuongNhap(soLuongNhap);
        setSoLuongTon(soLuongTon);
        setTrangThai(trangThai);
    }

    public String getMaLo() {
        return this.maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo == null ? null : maLo.trim();
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public String getSoLo() {
        return this.soLo;
    }

    public void setSoLo(String soLo) {
        this.soLo = soLo == null ? null : soLo.trim();
    }

    public LocalDate getNgayNhap() {
        return this.ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public LocalDate getHanSuDung() {
        return this.hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public long getGiaNhap() {
        return this.giaNhap;
    }

    public void setGiaNhap(long giaNhap) {
        if (giaNhap < 0) throw new IllegalArgumentException("Giá nhập lô hàng không được âm.");
        this.giaNhap = giaNhap;
    }

    public int getSoLuongNhap() {
        return this.soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap < 0) throw new IllegalArgumentException("Số lượng nhập không được âm.");
        this.soLuongNhap = soLuongNhap;
    }

    public int getSoLuongTon() {
        return this.soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) throw new IllegalArgumentException("Số lượng tồn kho không được âm.");
        this.soLuongTon = soLuongTon;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setTrangThaiEnum(TrangThaiLoHang trangThai) {
        this.trangThai = trangThai == null ? null : trangThai.maDB();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoHang that = (LoHang) o;
        return Objects.equals(this.maLo, that.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }

    @Override
    public String toString() {
        return "LoHang{"
                + "maLo='"
                + maLo
                + '\''
                + ", sanPham="
                + (sanPham != null ? sanPham.getMaSanPham() : null)
                + ", soLo='"
                + soLo
                + '\''
                + ", hanSuDung="
                + hanSuDung
                + ", soLuongTon="
                + soLuongTon
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
