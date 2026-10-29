package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Thực thể hồ sơ thú cưng khớp bảng [thu_cung].
 */
public class ThuCung implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maThuCung;
    private long khachHangId;
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

    public ThuCung() {}

    public ThuCung(long id, String maThuCung, long khachHangId, String ten, String loai, String giong, String gioiTinh, String mauLong, double canNang, String tinhTrangSucKhoe, String diUngBenhLy, String anh, boolean dangTheoDoi, LocalDate ngaySinh, String tinhCach, String tiemChung, int soMuiTiem, LocalDate ngayTiemGanNhat) {
        this.id = id;
        this.maThuCung = maThuCung;
        this.khachHangId = khachHangId;
        this.ten = ten;
        this.loai = loai;
        this.giong = giong;
        this.gioiTinh = gioiTinh;
        this.mauLong = mauLong;
        this.canNang = canNang;
        this.tinhTrangSucKhoe = tinhTrangSucKhoe;
        this.diUngBenhLy = diUngBenhLy;
        this.anh = anh;
        this.dangTheoDoi = dangTheoDoi;
        this.ngaySinh = ngaySinh;
        this.tinhCach = tinhCach;
        this.tiemChung = tiemChung;
        this.soMuiTiem = soMuiTiem;
        this.ngayTiemGanNhat = ngayTiemGanNhat;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaThuCung() { return this.maThuCung; }
    public String layMaThuCung() { return this.maThuCung; }
    public String maThuCung() { return this.maThuCung; }
    public void setMaThuCung(String maThuCung) { this.maThuCung = maThuCung; }
    public void datMaThuCung(String maThuCung) { this.maThuCung = maThuCung; }

    public long getKhachHangId() { return this.khachHangId; }
    public long layKhachHangId() { return this.khachHangId; }
    public long khachHangId() { return this.khachHangId; }
    public void setKhachHangId(long khachHangId) { this.khachHangId = khachHangId; }
    public void datKhachHangId(long khachHangId) { this.khachHangId = khachHangId; }

    public String getTen() { return this.ten; }
    public String layTen() { return this.ten; }
    public String ten() { return this.ten; }
    public void setTen(String ten) { this.ten = ten; }
    public void datTen(String ten) { this.ten = ten; }

    public String getLoai() { return this.loai; }
    public String layLoai() { return this.loai; }
    public String loai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public void datLoai(String loai) { this.loai = loai; }

    public String getGiong() { return this.giong; }
    public String layGiong() { return this.giong; }
    public String giong() { return this.giong; }
    public void setGiong(String giong) { this.giong = giong; }
    public void datGiong(String giong) { this.giong = giong; }

    public String getGioiTinh() { return this.gioiTinh; }
    public String layGioiTinh() { return this.gioiTinh; }
    public String gioiTinh() { return this.gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public void datGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getMauLong() { return this.mauLong; }
    public String layMauLong() { return this.mauLong; }
    public String mauLong() { return this.mauLong; }
    public void setMauLong(String mauLong) { this.mauLong = mauLong; }
    public void datMauLong(String mauLong) { this.mauLong = mauLong; }

    public double getCanNang() { return this.canNang; }
    public double layCanNang() { return this.canNang; }
    public double canNang() { return this.canNang; }
    public void setCanNang(double canNang) { this.canNang = canNang; }
    public void datCanNang(double canNang) { this.canNang = canNang; }

    public String getTinhTrangSucKhoe() { return this.tinhTrangSucKhoe; }
    public String layTinhTrangSucKhoe() { return this.tinhTrangSucKhoe; }
    public String tinhTrangSucKhoe() { return this.tinhTrangSucKhoe; }
    public void setTinhTrangSucKhoe(String tinhTrangSucKhoe) { this.tinhTrangSucKhoe = tinhTrangSucKhoe; }
    public void datTinhTrangSucKhoe(String tinhTrangSucKhoe) { this.tinhTrangSucKhoe = tinhTrangSucKhoe; }

    public String getDiUngBenhLy() { return this.diUngBenhLy; }
    public String layDiUngBenhLy() { return this.diUngBenhLy; }
    public String diUngBenhLy() { return this.diUngBenhLy; }
    public void setDiUngBenhLy(String diUngBenhLy) { this.diUngBenhLy = diUngBenhLy; }
    public void datDiUngBenhLy(String diUngBenhLy) { this.diUngBenhLy = diUngBenhLy; }

    public String getAnh() { return this.anh; }
    public String layAnh() { return this.anh; }
    public String anh() { return this.anh; }
    public void setAnh(String anh) { this.anh = anh; }
    public void datAnh(String anh) { this.anh = anh; }

    public boolean isDangTheoDoi() { return this.dangTheoDoi; }
    public boolean layDangTheoDoi() { return this.dangTheoDoi; }
    public boolean dangTheoDoi() { return this.dangTheoDoi; }
    public void setDangTheoDoi(boolean dangTheoDoi) { this.dangTheoDoi = dangTheoDoi; }
    public void datDangTheoDoi(boolean dangTheoDoi) { this.dangTheoDoi = dangTheoDoi; }

    public LocalDate getNgaySinh() { return this.ngaySinh; }
    public LocalDate layNgaySinh() { return this.ngaySinh; }
    public LocalDate ngaySinh() { return this.ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public void datNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getTinhCach() { return this.tinhCach; }
    public String layTinhCach() { return this.tinhCach; }
    public String tinhCach() { return this.tinhCach; }
    public void setTinhCach(String tinhCach) { this.tinhCach = tinhCach; }
    public void datTinhCach(String tinhCach) { this.tinhCach = tinhCach; }

    public String getTiemChung() { return this.tiemChung; }
    public String layTiemChung() { return this.tiemChung; }
    public String tiemChung() { return this.tiemChung; }
    public void setTiemChung(String tiemChung) { this.tiemChung = tiemChung; }
    public void datTiemChung(String tiemChung) { this.tiemChung = tiemChung; }

    public int getSoMuiTiem() { return this.soMuiTiem; }
    public int laySoMuiTiem() { return this.soMuiTiem; }
    public int soMuiTiem() { return this.soMuiTiem; }
    public void setSoMuiTiem(int soMuiTiem) { this.soMuiTiem = soMuiTiem; }
    public void datSoMuiTiem(int soMuiTiem) { this.soMuiTiem = soMuiTiem; }

    public LocalDate getNgayTiemGanNhat() { return this.ngayTiemGanNhat; }
    public LocalDate layNgayTiemGanNhat() { return this.ngayTiemGanNhat; }
    public LocalDate ngayTiemGanNhat() { return this.ngayTiemGanNhat; }
    public void setNgayTiemGanNhat(LocalDate ngayTiemGanNhat) { this.ngayTiemGanNhat = ngayTiemGanNhat; }
    public void datNgayTiemGanNhat(LocalDate ngayTiemGanNhat) { this.ngayTiemGanNhat = ngayTiemGanNhat; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThuCung that = (ThuCung) o;
        return this.id == that.id
                && Objects.equals(maThuCung, that.maThuCung)
                && this.khachHangId == that.khachHangId
                && Objects.equals(ten, that.ten)
                && Objects.equals(loai, that.loai)
                && Objects.equals(giong, that.giong)
                && Objects.equals(gioiTinh, that.gioiTinh)
                && Objects.equals(mauLong, that.mauLong)
                && this.canNang == that.canNang
                && Objects.equals(tinhTrangSucKhoe, that.tinhTrangSucKhoe)
                && Objects.equals(diUngBenhLy, that.diUngBenhLy)
                && Objects.equals(anh, that.anh)
                && this.dangTheoDoi == that.dangTheoDoi
                && Objects.equals(ngaySinh, that.ngaySinh)
                && Objects.equals(tinhCach, that.tinhCach)
                && Objects.equals(tiemChung, that.tiemChung)
                && this.soMuiTiem == that.soMuiTiem
                && Objects.equals(ngayTiemGanNhat, that.ngayTiemGanNhat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maThuCung, khachHangId, ten, loai, giong, gioiTinh, mauLong, canNang, tinhTrangSucKhoe, diUngBenhLy, anh, dangTheoDoi, ngaySinh, tinhCach, tiemChung, soMuiTiem, ngayTiemGanNhat);
    }

    @Override
    public String toString() {
        return "ThuCung{" + "id=" + id + ", " + "maThuCung=" + maThuCung + ", " + "khachHangId=" + khachHangId + ", " + "ten=" + ten + ", " + "loai=" + loai + ", " + "giong=" + giong + ", " + "gioiTinh=" + gioiTinh + ", " + "mauLong=" + mauLong + ", " + "canNang=" + canNang + ", " + "tinhTrangSucKhoe=" + tinhTrangSucKhoe + ", " + "diUngBenhLy=" + diUngBenhLy + ", " + "anh=" + anh + ", " + "dangTheoDoi=" + dangTheoDoi + ", " + "ngaySinh=" + ngaySinh + ", " + "tinhCach=" + tinhCach + ", " + "tiemChung=" + tiemChung + ", " + "soMuiTiem=" + soMuiTiem + ", " + "ngayTiemGanNhat=" + ngayTiemGanNhat + "}";
    }
}
