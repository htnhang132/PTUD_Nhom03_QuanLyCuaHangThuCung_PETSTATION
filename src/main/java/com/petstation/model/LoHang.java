package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class LoHang implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLo;
    private SanPham sanPham;
    private String soLo;
    private LocalDate ngaySanXuat;
    private LocalDate ngayNhap;
    private LocalDate hanSuDung;
    private long giaNhap;
    private int soLuongNhap;
    private int soLuongTon;
    private LocalDate ngayKiemKe;
    private String trangThai;

    public LoHang() {
        this.ngayNhap = LocalDate.now();
        this.trangThai = "SAN_SANG";
    }

    public LoHang(String maLo) {
        this();
        setMaLo(maLo);
    }

    public LoHang(
            String maLo,
            SanPham sanPham,
            String soLo,
            LocalDate ngaySanXuat,
            LocalDate ngayNhap,
            LocalDate hanSuDung,
            long giaNhap,
            int soLuongNhap,
            int soLuongTon,
            LocalDate ngayKiemKe,
            String trangThai) {
        this();
        setMaLo(maLo);
        setSanPham(sanPham);
        setSoLo(soLo);
        setNgaySanXuat(ngaySanXuat);
        setNgayNhap(ngayNhap);
        setHanSuDung(hanSuDung);
        setGiaNhap(giaNhap);
        setSoLuongNhap(soLuongNhap);
        setSoLuongTon(soLuongTon);
        setNgayKiemKe(ngayKiemKe);
        setTrangThai(trangThai);
    }

    public long tinhGiaTriTonKho() {
        return (long) Math.max(0, soLuongTon) * Math.max(0L, giaNhap);
    }

    public int tinhSoLuongDaXuat() {
        return Math.max(0, soLuongNhap - soLuongTon);
    }

    public long tinhSoNgayConHan() {
        if (hanSuDung == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), hanSuDung);
    }

    public boolean kiemTraHetHan() {
        return hanSuDung != null && hanSuDung.isBefore(LocalDate.now());
    }

    public boolean kiemTraCanDate(int soNgayCanhBao) {
        if (hanSuDung == null)
            return false;
        long conLai = tinhSoNgayConHan();
        return conLai >= 0 && conLai <= soNgayCanhBao;
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

    public String getMaSanPham() {
        return sanPham == null ? null : sanPham.getMaSanPham();
    }

    public void setMaSanPham(String maSanPham) {
        this.sanPham = (maSanPham == null || maSanPham.isBlank()) ? null : new SanPham(maSanPham);
    }

    public String getSoLo() {
        return this.soLo;
    }

    public void setSoLo(String soLo) {
        this.soLo = soLo == null ? null : soLo.trim();
    }

    public LocalDate getNgaySanXuat() {
        return this.ngaySanXuat;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
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
        if (giaNhap < 0)
            throw new IllegalArgumentException("Giá nhập lô hàng không được âm.");
        this.giaNhap = giaNhap;
    }

    public int getSoLuongNhap() {
        return this.soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap < 0)
            throw new IllegalArgumentException("Số lượng nhập không được âm.");
        this.soLuongNhap = soLuongNhap;
    }

    public int getSoLuongTon() {
        return this.soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0)
            throw new IllegalArgumentException("Số lượng tồn kho không được âm.");
        this.soLuongTon = soLuongTon;
    }

    public LocalDate getNgayKiemKe() {
        return this.ngayKiemKe;
    }

    public void setNgayKiemKe(LocalDate ngayKiemKe) {
        this.ngayKiemKe = ngayKiemKe;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LoHang that = (LoHang) o;
        return Objects.equals(this.maLo, that.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }

    @Override
    public String toString() {
        return "LoHang{" + "maLo='" + maLo + '\'' + ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : null) + ", soLo='" + soLo
                + '\'' + ", hanSuDung=" + hanSuDung + ", soLuongTon=" + soLuongTon + ", trangThai='" + trangThai + '\''
                + '}';
    }
}
