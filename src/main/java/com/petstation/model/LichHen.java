package com.petstation.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class LichHen implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLichHen;
    private KhachHang khachHang;
    private ThuCung thuCung;
    private NhanVien nhanVien;
    private GoiDichVuSpa dichVu;
    private String loaiDichVu;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String trangThai;
    private String ghiChu;

    public LichHen() {
        this.trangThai = "DA_XAC_NHAN";
    }

    public LichHen(String maLichHen) {
        this();
        setMaLichHen(maLichHen);
    }

    public LichHen(
            String maLichHen,
            KhachHang khachHang,
            ThuCung thuCung,
            String loaiDichVu,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String trangThai,
            String ghiChu,
            NhanVien nhanVien,
            GoiDichVuSpa dichVu) {
        this();
        setMaLichHen(maLichHen);
        setKhachHang(khachHang);
        setThuCung(thuCung);
        setLoaiDichVu(loaiDichVu);
        setBatDau(batDau);
        setKetThuc(ketThuc);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
        setNhanVien(nhanVien);
        setDichVu(dichVu);
    }

    public long tinhThoiLuongPhut() {
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau)) {
            return 0L;
        }
        return Duration.between(batDau, ketThuc).toMinutes();
    }

    public String getMaLichHen() {
        return this.maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen == null ? null : maLichHen.trim();
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public String getMaKhachHang() {
        return khachHang == null ? null : khachHang.getMaKhachHang();
    }

    public void setMaKhachHang(String maKhachHang) {
        this.khachHang = (maKhachHang == null || maKhachHang.isBlank()) ? null : new KhachHang(maKhachHang);
    }

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public String getMaThuCung() {
        return thuCung == null ? null : thuCung.getMaThuCung();
    }

    public void setMaThuCung(String maThuCung) {
        this.thuCung = (maThuCung == null || maThuCung.isBlank()) ? null : new ThuCung(maThuCung);
    }

    public String getLoaiDichVu() {
        return this.loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
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

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getMaNhanVien() {
        return nhanVien == null ? null : nhanVien.getMaNhanVien();
    }

    public void setMaNhanVien(String maNhanVien) {
        this.nhanVien = (maNhanVien == null || maNhanVien.isBlank()) ? null : new NhanVien(maNhanVien);
    }

    public GoiDichVuSpa getDichVu() {
        return this.dichVu;
    }

    public void setDichVu(GoiDichVuSpa dichVu) {
        this.dichVu = dichVu;
    }

    public String getMaDichVu() {
        return dichVu == null ? null : dichVu.getMaGoi();
    }

    public void setMaDichVu(String maDichVu) {
        this.dichVu = (maDichVu == null || maDichVu.isBlank()) ? null : new GoiDichVuSpa(maDichVu);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LichHen that = (LichHen) o;
        return Objects.equals(this.maLichHen, that.maLichHen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLichHen);
    }

    @Override
    public String toString() {
        return "LichHen{" + "maLichHen='" + maLichHen + '\'' + ", khachHang=" + (khachHang != null ? khachHang.getMaKhachHang() : null)
                + ", thuCung=" + (thuCung != null ? thuCung.getMaThuCung() : null) + ", trangThai='" + trangThai + '\'' + '}';
    }
}
