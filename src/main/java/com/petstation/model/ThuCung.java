package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class ThuCung implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maThuCung;
    private KhachHang khachHang;
    private String ten;
    private String loai;
    private String giong;
    private String gioiTinh;
    private String mauLong;
    private double canNang;
    private String tinhTrangSucKhoe;
    private String diUngBenhLy;
    private String anh;
    private boolean dangTheoDoi;
    private LocalDate ngaySinh;
    private String tinhCach;
    private String tiemChung;
    private int soMuiTiem;
    private LocalDate ngayTiemGanNhat;

    public ThuCung() {
        this.dangTheoDoi = true;
    }

    public ThuCung(String maThuCung) {
        this();
        setMaThuCung(maThuCung);
    }

    public ThuCung(
            String maThuCung,
            KhachHang khachHang,
            String ten,
            String loai,
            String giong,
            String gioiTinh,
            String mauLong,
            double canNang,
            String tinhTrangSucKhoe,
            String diUngBenhLy,
            String anh,
            boolean dangTheoDoi,
            LocalDate ngaySinh,
            String tinhCach,
            String tiemChung,
            int soMuiTiem,
            LocalDate ngayTiemGanNhat) {
        this();
        setMaThuCung(maThuCung);
        setKhachHang(khachHang);
        setTen(ten);
        setLoai(loai);
        setGiong(giong);
        setGioiTinh(gioiTinh);
        setMauLong(mauLong);
        setCanNang(canNang);
        setTinhTrangSucKhoe(tinhTrangSucKhoe);
        setDiUngBenhLy(diUngBenhLy);
        setAnh(anh);
        setDangTheoDoi(dangTheoDoi);
        setNgaySinh(ngaySinh);
        setTinhCach(tinhCach);
        setTiemChung(tiemChung);
        setSoMuiTiem(soMuiTiem);
        setNgayTiemGanNhat(ngayTiemGanNhat);
    }

    public int tinhTuoiTheoNam() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    public int tinhTuoiTheoThang() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        Period p = Period.between(ngaySinh, LocalDate.now());
        return p.getYears() * 12 + p.getMonths();
    }

    public boolean kiemTraDaTiemPhong() {
        return soMuiTiem > 0 || (tiemChung != null && !tiemChung.isBlank());
    }

    public String getMaThuCung() {
        return this.maThuCung;
    }

    public void setMaThuCung(String maThuCung) {
        this.maThuCung = maThuCung == null ? null : maThuCung.trim();
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

    public String getTen() {
        return this.ten;
    }

    public void setTen(String ten) {
        this.ten = ten == null ? null : ten.trim();
    }

    public String getLoai() {
        return this.loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getGiong() {
        return this.giong;
    }

    public void setGiong(String giong) {
        this.giong = giong;
    }

    public String getGioiTinh() {
        return this.gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getMauLong() {
        return this.mauLong;
    }

    public void setMauLong(String mauLong) {
        this.mauLong = mauLong;
    }

    public double getCanNang() {
        return this.canNang;
    }

    public void setCanNang(double canNang) {
        if (canNang < 0)
            throw new IllegalArgumentException("Cân nặng thú cưng không được âm.");
        this.canNang = canNang;
    }

    public String getTinhTrangSucKhoe() {
        return this.tinhTrangSucKhoe;
    }

    public void setTinhTrangSucKhoe(String tinhTrangSucKhoe) {
        this.tinhTrangSucKhoe = tinhTrangSucKhoe;
    }

    public String getDiUngBenhLy() {
        return this.diUngBenhLy;
    }

    public void setDiUngBenhLy(String diUngBenhLy) {
        this.diUngBenhLy = diUngBenhLy;
    }

    public String getAnh() {
        return this.anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public boolean isDangTheoDoi() {
        return this.dangTheoDoi;
    }

    public void setDangTheoDoi(boolean dangTheoDoi) {
        this.dangTheoDoi = dangTheoDoi;
    }

    public LocalDate getNgaySinh() {
        return this.ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getTinhCach() {
        return this.tinhCach;
    }

    public void setTinhCach(String tinhCach) {
        this.tinhCach = tinhCach;
    }

    public String getTiemChung() {
        return this.tiemChung;
    }

    public void setTiemChung(String tiemChung) {
        this.tiemChung = tiemChung;
    }

    public int getSoMuiTiem() {
        return this.soMuiTiem;
    }

    public void setSoMuiTiem(int soMuiTiem) {
        if (soMuiTiem < 0)
            throw new IllegalArgumentException("Số mũi tiêm không được âm.");
        this.soMuiTiem = soMuiTiem;
    }

    public LocalDate getNgayTiemGanNhat() {
        return this.ngayTiemGanNhat;
    }

    public void setNgayTiemGanNhat(LocalDate ngayTiemGanNhat) {
        this.ngayTiemGanNhat = ngayTiemGanNhat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ThuCung that = (ThuCung) o;
        return Objects.equals(this.maThuCung, that.maThuCung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThuCung);
    }

    @Override
    public String toString() {
        return "ThuCung{" + "maThuCung='" + maThuCung + '\'' + ", khachHang=" + (khachHang != null ? khachHang.getMaKhachHang() : null)
                + ", ten='" + ten + '\'' + ", loai='" + loai + '\'' + ", canNang=" + canNang + '}';
    }
}
