package com.petstation.model;

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
    private String veSinh;
    private String tinhTrang;
    private String huongDanThuoc;
    private String daThucHien;
    private String anhTruoc;
    private String anhSau;
    private String tepDinhKem;

    public NhatKyChamSoc() {
        this.thoiGian = LocalDateTime.now();
    }

    public NhatKyChamSoc(String maNhatKy) {
        this();
        setMaNhatKy(maNhatKy);
    }

    public NhatKyChamSoc(
            String maNhatKy,
            HopDongLuuTru hopDong,
            PhieuTiepNhanSpa phieuSpa,
            NhanVien nhanVien,
            LocalDateTime thoiGian,
            String anUong,
            String veSinh,
            String tinhTrang,
            String huongDanThuoc,
            String daThucHien,
            String anhTruoc,
            String anhSau,
            String tepDinhKem) {
        this();
        setMaNhatKy(maNhatKy);
        setHopDong(hopDong);
        setPhieuSpa(phieuSpa);
        setNhanVien(nhanVien);
        setThoiGian(thoiGian);
        setAnUong(anUong);
        setVeSinh(veSinh);
        setTinhTrang(tinhTrang);
        setHuongDanThuoc(huongDanThuoc);
        setDaThucHien(daThucHien);
        setAnhTruoc(anhTruoc);
        setAnhSau(anhSau);
        setTepDinhKem(tepDinhKem);
    }

    public boolean kiemTraCoHinhAnhDoiChung() {
        return (anhTruoc != null && !anhTruoc.isBlank()) || (anhSau != null && !anhSau.isBlank());
    }

    public String getMaNhatKy() {
        return this.maNhatKy;
    }

    public void setMaNhatKy(String maNhatKy) {
        this.maNhatKy = maNhatKy == null ? null : maNhatKy.trim();
    }

    public HopDongLuuTru getHopDong() {
        return this.hopDong;
    }

    public void setHopDong(HopDongLuuTru hopDong) {
        this.hopDong = hopDong;
    }

    public String getMaHopDong() {
        return hopDong == null ? null : hopDong.getMaHopDong();
    }

    public void setMaHopDong(String maHopDong) {
        this.hopDong = (maHopDong == null || maHopDong.isBlank()) ? null : new HopDongLuuTru(maHopDong);
    }

    public PhieuTiepNhanSpa getPhieuSpa() {
        return this.phieuSpa;
    }

    public void setPhieuSpa(PhieuTiepNhanSpa phieuSpa) {
        this.phieuSpa = phieuSpa;
    }

    public String getMaPhieuSpa() {
        return phieuSpa == null ? null : phieuSpa.getMaPhieu();
    }

    public void setMaPhieuSpa(String maPhieuSpa) {
        this.phieuSpa = (maPhieuSpa == null || maPhieuSpa.isBlank()) ? null : new PhieuTiepNhanSpa(maPhieuSpa);
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

    public LocalDateTime getThoiGian() {
        return this.thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getAnUong() {
        return this.anUong;
    }

    public void setAnUong(String anUong) {
        this.anUong = anUong;
    }

    public String getVeSinh() {
        return this.veSinh;
    }

    public void setVeSinh(String veSinh) {
        this.veSinh = veSinh;
    }

    public String getTinhTrang() {
        return this.tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getHuongDanThuoc() {
        return this.huongDanThuoc;
    }

    public void setHuongDanThuoc(String huongDanThuoc) {
        this.huongDanThuoc = huongDanThuoc;
    }

    public String getDaThucHien() {
        return this.daThucHien;
    }

    public void setDaThucHien(String daThucHien) {
        this.daThucHien = daThucHien;
    }

    public String getAnhTruoc() {
        return this.anhTruoc;
    }

    public void setAnhTruoc(String anhTruoc) {
        this.anhTruoc = anhTruoc;
    }

    public String getAnhSau() {
        return this.anhSau;
    }

    public void setAnhSau(String anhSau) {
        this.anhSau = anhSau;
    }

    public String getTepDinhKem() {
        return this.tepDinhKem;
    }

    public void setTepDinhKem(String tepDinhKem) {
        this.tepDinhKem = tepDinhKem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NhatKyChamSoc that = (NhatKyChamSoc) o;
        return Objects.equals(this.maNhatKy, that.maNhatKy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhatKy);
    }

    @Override
    public String toString() {
        return "NhatKyChamSoc{" + "maNhatKy='" + maNhatKy + '\''
                + ", hopDong=" + (hopDong != null ? hopDong.getMaHopDong() : null)
                + ", phieuSpa=" + (phieuSpa != null ? phieuSpa.getMaPhieu() : null)
                + ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNhanVien() : null) + '}';
    }
}
