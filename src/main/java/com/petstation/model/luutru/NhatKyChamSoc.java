package com.petstation.model.luutru;

import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.PhieuTiepNhanSpa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class NhatKyChamSoc implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhatKy;
    private HopDongLuuTru hopDong;
    private PhieuTiepNhanSpa phieuSpa;
    private NhanVien nhanVien;
    private LocalDateTime thoiGian;
    private String anUong;
    private String tinhTrang;
    private String huongDanThuoc;
    private String daThucHien;
    private String anhTruoc;
    private String anhSau;

    public NhatKyChamSoc() {}

    public NhatKyChamSoc(
            String maNhatKy,
            NhanVien nhanVien,
            LocalDateTime thoiGian,
            String tinhTrang,
            String anUong,
            String huongDanThuoc,
            String daThucHien,
            String anhTruoc,
            String anhSau) {
        this.maNhatKy = maNhatKy;
        this.nhanVien = nhanVien;
        this.thoiGian = thoiGian;
        this.tinhTrang = tinhTrang;
        this.anUong = anUong;
        this.huongDanThuoc = huongDanThuoc;
        this.daThucHien = daThucHien;
        this.anhTruoc = anhTruoc;
        this.anhSau = anhSau;
    }

    public String getMaNhatKy() {
        return maNhatKy;
    }

    public void setMaNhatKy(String maNhatKy) {
        this.maNhatKy = maNhatKy;
    }

    public HopDongLuuTru getHopDong() {
        return hopDong;
    }

    public void setHopDong(HopDongLuuTru hopDong) {
        this.hopDong = hopDong;
    }

    public PhieuTiepNhanSpa getPhieuSpa() {
        return phieuSpa;
    }

    public void setPhieuSpa(PhieuTiepNhanSpa phieuSpa) {
        this.phieuSpa = phieuSpa;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getAnUong() {
        return anUong;
    }

    public void setAnUong(String anUong) {
        this.anUong = anUong;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getHuongDanThuoc() {
        return huongDanThuoc;
    }

    public void setHuongDanThuoc(String huongDanThuoc) {
        this.huongDanThuoc = huongDanThuoc;
    }

    public String getDaThucHien() {
        return daThucHien;
    }

    public void setDaThucHien(String daThucHien) {
        this.daThucHien = daThucHien;
    }

    public String getAnhTruoc() {
        return anhTruoc;
    }

    public void setAnhTruoc(String anhTruoc) {
        this.anhTruoc = anhTruoc;
    }

    public String getAnhSau() {
        return anhSau;
    }

    public void setAnhSau(String anhSau) {
        this.anhSau = anhSau;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhatKyChamSoc that = (NhatKyChamSoc) o;
        return Objects.equals(maNhatKy, that.maNhatKy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhatKy);
    }

    @Override
    public String toString() {
        return maNhatKy != null ? maNhatKy : "";
    }
}
