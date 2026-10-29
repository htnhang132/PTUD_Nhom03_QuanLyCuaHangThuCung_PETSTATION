package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/** Biểu diễn dữ liệu ánh xạ với bảng tai_khoan trong SQL Server. */
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long idNhanVien;
    private String tenDangNhap;
    private String maBamMatKhau;
    private VaiTro vaiTro;
    private TrangThaiTaiKhoan trangThai;
    private int soLanDangNhapSai;
    private java.time.LocalDateTime khoaDenLuc;
    private boolean batBuocDoiMatKhau;

    public TaiKhoan() {}

    public TaiKhoan(long id, long idNhanVien, String tenDangNhap, String maBamMatKhau, VaiTro vaiTro, TrangThaiTaiKhoan trangThai, int soLanDangNhapSai, java.time.LocalDateTime khoaDenLuc, boolean batBuocDoiMatKhau) {
        this.id = id;
        this.idNhanVien = idNhanVien;
        this.tenDangNhap = tenDangNhap;
        this.maBamMatKhau = maBamMatKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.soLanDangNhapSai = soLanDangNhapSai;
        this.khoaDenLuc = khoaDenLuc;
        this.batBuocDoiMatKhau = batBuocDoiMatKhau;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getIdNhanVien() { return this.idNhanVien; }
    public long layIdNhanVien() { return this.idNhanVien; }
    public long idNhanVien() { return this.idNhanVien; }
    public void setIdNhanVien(long idNhanVien) { this.idNhanVien = idNhanVien; }
    public void datIdNhanVien(long idNhanVien) { this.idNhanVien = idNhanVien; }

    public String getTenDangNhap() { return this.tenDangNhap; }
    public String layTenDangNhap() { return this.tenDangNhap; }
    public String tenDangNhap() { return this.tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public void datTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMaBamMatKhau() { return this.maBamMatKhau; }
    public String layMaBamMatKhau() { return this.maBamMatKhau; }
    public String maBamMatKhau() { return this.maBamMatKhau; }
    public void setMaBamMatKhau(String maBamMatKhau) { this.maBamMatKhau = maBamMatKhau; }
    public void datMaBamMatKhau(String maBamMatKhau) { this.maBamMatKhau = maBamMatKhau; }

    public VaiTro getVaiTro() { return this.vaiTro; }
    public VaiTro layVaiTro() { return this.vaiTro; }
    public VaiTro vaiTro() { return this.vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
    public void datVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }

    public TrangThaiTaiKhoan getTrangThai() { return this.trangThai; }
    public TrangThaiTaiKhoan layTrangThai() { return this.trangThai; }
    public TrangThaiTaiKhoan trangThai() { return this.trangThai; }
    public void setTrangThai(TrangThaiTaiKhoan trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(TrangThaiTaiKhoan trangThai) { this.trangThai = trangThai; }

    public int getSoLanDangNhapSai() { return this.soLanDangNhapSai; }
    public int laySoLanDangNhapSai() { return this.soLanDangNhapSai; }
    public int soLanDangNhapSai() { return this.soLanDangNhapSai; }
    public void setSoLanDangNhapSai(int soLanDangNhapSai) { this.soLanDangNhapSai = soLanDangNhapSai; }
    public void datSoLanDangNhapSai(int soLanDangNhapSai) { this.soLanDangNhapSai = soLanDangNhapSai; }

    public java.time.LocalDateTime getKhoaDenLuc() { return this.khoaDenLuc; }
    public java.time.LocalDateTime layKhoaDenLuc() { return this.khoaDenLuc; }
    public java.time.LocalDateTime khoaDenLuc() { return this.khoaDenLuc; }
    public void setKhoaDenLuc(java.time.LocalDateTime khoaDenLuc) { this.khoaDenLuc = khoaDenLuc; }
    public void datKhoaDenLuc(java.time.LocalDateTime khoaDenLuc) { this.khoaDenLuc = khoaDenLuc; }

    public boolean isBatBuocDoiMatKhau() { return this.batBuocDoiMatKhau; }
    public boolean layBatBuocDoiMatKhau() { return this.batBuocDoiMatKhau; }
    public boolean batBuocDoiMatKhau() { return this.batBuocDoiMatKhau; }
    public void setBatBuocDoiMatKhau(boolean batBuocDoiMatKhau) { this.batBuocDoiMatKhau = batBuocDoiMatKhau; }
    public void datBatBuocDoiMatKhau(boolean batBuocDoiMatKhau) { this.batBuocDoiMatKhau = batBuocDoiMatKhau; }

    public boolean layTamThoiDaKhoa(java.time.LocalDateTime hienTai) {
        return khoaDenLuc != null && khoaDenLuc.isAfter(hienTai);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan that = (TaiKhoan) o;
        return this.id == that.id
                && this.idNhanVien == that.idNhanVien
                && Objects.equals(tenDangNhap, that.tenDangNhap)
                && Objects.equals(maBamMatKhau, that.maBamMatKhau)
                && Objects.equals(vaiTro, that.vaiTro)
                && Objects.equals(trangThai, that.trangThai)
                && this.soLanDangNhapSai == that.soLanDangNhapSai
                && Objects.equals(khoaDenLuc, that.khoaDenLuc)
                && this.batBuocDoiMatKhau == that.batBuocDoiMatKhau;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idNhanVien, tenDangNhap, maBamMatKhau, vaiTro, trangThai, soLanDangNhapSai, khoaDenLuc, batBuocDoiMatKhau);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" + "id=" + id + ", " + "idNhanVien=" + idNhanVien + ", " + "tenDangNhap=" + tenDangNhap + ", " + "maBamMatKhau=" + maBamMatKhau + ", " + "vaiTro=" + vaiTro + ", " + "trangThai=" + trangThai + ", " + "soLanDangNhapSai=" + soLanDangNhapSai + ", " + "khoaDenLuc=" + khoaDenLuc + ", " + "batBuocDoiMatKhau=" + batBuocDoiMatKhau + "}";
    }
}
