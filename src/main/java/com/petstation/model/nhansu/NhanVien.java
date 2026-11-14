package com.petstation.model.nhansu;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhanVien;
    private String hoTen;
    private String soDienThoai;
    private String chucVu;
    private LocalDate ngayVaoLam;
    private boolean dangLamViec;
    private String cccd;

    public NhanVien() {
        this.dangLamViec = true;
    }

    public NhanVien(String maNhanVien) {
        this();
        setMaNhanVien(maNhanVien);
    }

    public NhanVien(
            String maNhanVien,
            String hoTen,
            String soDienThoai,
            String chucVu,
            LocalDate ngayVaoLam,
            boolean dangLamViec,
            String cccd) {
        this();
        setMaNhanVien(maNhanVien);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setChucVu(chucVu);
        setNgayVaoLam(ngayVaoLam);
        setDangLamViec(dangLamViec);
        setCccd(cccd);
    }

    public String getMaNhanVien() {
        return this.maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien == null ? null : maNhanVien.trim();
    }

    public String getHoTen() {
        return this.hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen == null ? null : hoTen.trim();
    }

    public String getSoDienThoai() {
        return this.soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim();
    }

    public String getChucVu() {
        return this.chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public LocalDate getNgayVaoLam() {
        return this.ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public boolean isDangLamViec() {
        return this.dangLamViec;
    }

    public void setDangLamViec(boolean dangLamViec) {
        this.dangLamViec = dangLamViec;
    }

    public String getCccd() {
        return this.cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd == null ? null : cccd.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien that = (NhanVien) o;
        return Objects.equals(this.maNhanVien, that.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{"
                + "maNhanVien='"
                + maNhanVien
                + '\''
                + ", hoTen='"
                + hoTen
                + '\''
                + ", chucVu='"
                + chucVu
                + '\''
                + ", dangLamViec="
                + dangLamViec
                + '}';
    }
}
