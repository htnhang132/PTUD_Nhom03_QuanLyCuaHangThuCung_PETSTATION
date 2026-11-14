package com.petstation.model.spa;

import com.petstation.model.chung.TrangThaiLichHen;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class LichHen implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLichHen;
    private KhachHang khachHang;
    private ThuCung thuCung;
    private NhanVien nhanVien;
    private GoiDichVuSpa dichVu;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String trangThai;
    private String ghiChu;

    public LichHen() {
        this.trangThai = TrangThaiLichHen.DA_XAC_NHAN.maDB();
    }

    public LichHen(String maLichHen) {
        this();
        setMaLichHen(maLichHen);
    }

    public LichHen(
            String maLichHen,
            KhachHang khachHang,
            ThuCung thuCung,
            NhanVien nhanVien,
            GoiDichVuSpa dichVu,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String trangThai,
            String ghiChu) {
        this();
        setMaLichHen(maLichHen);
        setKhachHang(khachHang);
        setThuCung(thuCung);
        setNhanVien(nhanVien);
        setDichVu(dichVu);
        setBatDau(batDau);
        setKetThuc(ketThuc);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
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

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public GoiDichVuSpa getDichVu() {
        return this.dichVu;
    }

    public void setDichVu(GoiDichVuSpa dichVu) {
        this.dichVu = dichVu;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichHen that = (LichHen) o;
        return Objects.equals(this.maLichHen, that.maLichHen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLichHen);
    }

    @Override
    public String toString() {
        return "LichHen{"
                + "maLichHen='"
                + maLichHen
                + '\''
                + ", khachHang="
                + (khachHang != null ? khachHang.getMaKhachHang() : null)
                + ", thuCung="
                + (thuCung != null ? thuCung.getMaThuCung() : null)
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
