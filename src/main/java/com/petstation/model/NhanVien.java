package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String viTri;
    private LocalDate ngayBatDau;
    private boolean hoatDong;

    public NhanVien() {
        this.hoatDong = true;
    }

    public NhanVien(String maNhanVien) {
        this();
        setMaNhanVien(maNhanVien);
    }

    public NhanVien(
            String maNhanVien,
            String hoTen,
            LocalDate ngaySinh,
            String soDienThoai,
            String diaChi,
            String viTri,
            LocalDate ngayBatDau,
            boolean hoatDong) {
        this();
        setMaNhanVien(maNhanVien);
        setHoTen(hoTen);
        setNgaySinh(ngaySinh);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setViTri(viTri);
        setNgayBatDau(ngayBatDau);
        setHoatDong(hoatDong);
    }

    public NhanVien(long id) {
        this();
        setId(id);
    }

    public NhanVien(
            long id,
            String maNhanVien,
            String hoTen,
            LocalDate ngaySinh,
            String soDienThoai,
            String diaChi,
            String viTri,
            LocalDate ngayBatDau,
            boolean hoatDong) {
        this(maNhanVien, hoTen, ngaySinh, soDienThoai, diaChi, viTri, ngayBatDau, hoatDong);
        setId(id);
    }

    public int tinhTuoi() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    public int getTuoi() {
        return tinhTuoi();
    }

    public int layTuoi() {
        return tinhTuoi();
    }

    public int tinhThamNienNam() {
        if (ngayBatDau == null || ngayBatDau.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngayBatDau, LocalDate.now()).getYears();
    }

    public int getThamNienNam() {
        return tinhThamNienNam();
    }

    public int layThamNienNam() {
        return tinhThamNienNam();
    }

    public String getMaNhanVien() {
        return this.maNhanVien;
    }

    public String layMaNhanVien() {
        return this.maNhanVien;
    }

    public String maNhanVien() {
        return this.maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien == null ? null : maNhanVien.trim();
    }

    public void datMaNhanVien(String maNhanVien) {
        setMaNhanVien(maNhanVien);
    }

    public String getHoTen() {
        return this.hoTen;
    }

    public String layHoTen() {
        return this.hoTen;
    }

    public String hoTen() {
        return this.hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen == null ? null : hoTen.trim();
    }

    public void datHoTen(String hoTen) {
        setHoTen(hoTen);
    }

    public LocalDate getNgaySinh() {
        return this.ngaySinh;
    }

    public LocalDate layNgaySinh() {
        return this.ngaySinh;
    }

    public LocalDate ngaySinh() {
        return this.ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void datNgaySinh(LocalDate ngaySinh) {
        setNgaySinh(ngaySinh);
    }

    public String getSoDienThoai() {
        return this.soDienThoai;
    }

    public String laySoDienThoai() {
        return this.soDienThoai;
    }

    public String soDienThoai() {
        return this.soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim();
    }

    public void datSoDienThoai(String soDienThoai) {
        setSoDienThoai(soDienThoai);
    }

    public String getDiaChi() {
        return this.diaChi;
    }

    public String layDiaChi() {
        return this.diaChi;
    }

    public String diaChi() {
        return this.diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void datDiaChi(String diaChi) {
        setDiaChi(diaChi);
    }

    public String getViTri() {
        return this.viTri;
    }

    public String layViTri() {
        return this.viTri;
    }

    public String viTri() {
        return this.viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public void datViTri(String viTri) {
        setViTri(viTri);
    }

    public LocalDate getNgayBatDau() {
        return this.ngayBatDau;
    }

    public LocalDate layNgayBatDau() {
        return this.ngayBatDau;
    }

    public LocalDate ngayBatDau() {
        return this.ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void datNgayBatDau(LocalDate ngayBatDau) {
        setNgayBatDau(ngayBatDau);
    }

    public boolean isHoatDong() {
        return this.hoatDong;
    }

    public boolean layHoatDong() {
        return this.hoatDong;
    }

    public boolean hoatDong() {
        return this.hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    public void datHoatDong(boolean hoatDong) {
        setHoatDong(hoatDong);
    }

    public long getId() {
        return DuLieu.layIdCauNoi(this, maNhanVien);
    }

    public long layId() {
        return getId();
    }

    public long id() {
        return getId();
    }

    public void setId(long id) {
        if (id < 0) throw new IllegalArgumentException("Mã định danh nhân viên không hợp lệ.");
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maNhanVien == null || this.maNhanVien.isBlank()) && id > 0) {
            this.maNhanVien = DuLieu.taoMaTuId("NV", 4, id);
        }
    }

    public void datId(long id) {
        setId(id);
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
        return "NhanVien{" + "maNhanVien='" + maNhanVien + '\'' + ", hoTen='" + hoTen + '\''
                + ", viTri='" + viTri + '\'' + ", hoatDong=" + hoatDong + '}';
    }
}
