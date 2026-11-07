package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tenDangNhap;

    private NhanVien nhanVien;
    private String maBamMatKhau;
    private VaiTro vaiTro;
    private TrangThaiTaiKhoan trangThai;
    private int soLanDangNhapSai;
    private LocalDateTime khoaDenLuc;
    private boolean batBuocDoiMatKhau;

    public TaiKhoan() {
        this.trangThai = TrangThaiTaiKhoan.HOAT_DONG;
    }

    public TaiKhoan(String tenDangNhap) {
        this();
        setTenDangNhap(tenDangNhap);
    }

    public TaiKhoan(
            String tenDangNhap,
            NhanVien nhanVien,
            String maBamMatKhau,
            VaiTro vaiTro,
            TrangThaiTaiKhoan trangThai,
            int soLanDangNhapSai,
            LocalDateTime khoaDenLuc,
            boolean batBuocDoiMatKhau) {
        this();
        setTenDangNhap(tenDangNhap);
        setNhanVien(nhanVien);
        setMaBamMatKhau(maBamMatKhau);
        setVaiTro(vaiTro);
        setTrangThai(trangThai);
        setSoLanDangNhapSai(soLanDangNhapSai);
        setKhoaDenLuc(khoaDenLuc);
        setBatBuocDoiMatKhau(batBuocDoiMatKhau);
    }

    public boolean isTamThoiDaKhoa(LocalDateTime hienTai) {
        return khoaDenLuc != null && khoaDenLuc.isAfter(hienTai);
    }

    public String getTenDangNhap() {
        return this.tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap == null ? null : tenDangNhap.trim();
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

    public String getMaBamMatKhau() {
        return this.maBamMatKhau;
    }

    public void setMaBamMatKhau(String maBamMatKhau) {
        this.maBamMatKhau = maBamMatKhau;
    }

    public VaiTro getVaiTro() {
        return this.vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public TrangThaiTaiKhoan getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(TrangThaiTaiKhoan trangThai) {
        this.trangThai = trangThai;
    }

    public int getSoLanDangNhapSai() {
        return this.soLanDangNhapSai;
    }

    public void setSoLanDangNhapSai(int soLanDangNhapSai) {
        if (soLanDangNhapSai < 0)
            throw new IllegalArgumentException("Số lần đăng nhập sai không được âm.");
        this.soLanDangNhapSai = soLanDangNhapSai;
    }

    public LocalDateTime getKhoaDenLuc() {
        return this.khoaDenLuc;
    }

    public void setKhoaDenLuc(LocalDateTime khoaDenLuc) {
        this.khoaDenLuc = khoaDenLuc;
    }

    public boolean isBatBuocDoiMatKhau() {
        return this.batBuocDoiMatKhau;
    }

    public void setBatBuocDoiMatKhau(boolean batBuocDoiMatKhau) {
        this.batBuocDoiMatKhau = batBuocDoiMatKhau;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan that = (TaiKhoan) o;
        if (this.nhanVien != null && that.nhanVien != null
                && this.nhanVien.getMaNhanVien() != null && that.nhanVien.getMaNhanVien() != null) {
            return Objects.equals(this.nhanVien.getMaNhanVien(), that.nhanVien.getMaNhanVien());
        }
        return Objects.equals(this.tenDangNhap, that.tenDangNhap);
    }

    @Override
    public int hashCode() {
        if (nhanVien != null && nhanVien.getMaNhanVien() != null) {
            return Objects.hash(nhanVien.getMaNhanVien());
        }
        return Objects.hash(tenDangNhap);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" + "tenDangNhap='" + tenDangNhap + '\''
                + ", nhanVien=" + (nhanVien == null ? null : nhanVien.getMaNhanVien())
                + ", vaiTro=" + vaiTro + ", trangThai=" + trangThai + '}';
    }
}
