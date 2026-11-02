package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Thực thể lô hàng hóa khớp bảng [lo_hang].
 */
public class LoHang implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maLo;
    private long sanPhamId;
    private String soLo;
    private LocalDate ngaySanXuat;
    private LocalDate ngayNhap;
    private LocalDate hanSuDung;
    private long giaNhap;
    private int soLuongNhap;
    private int soLuongTon;
    private LocalDate ngayKiemKe;
    private String trangThai;

    public LoHang() {}

    public LoHang(long id, String maLo, long sanPhamId, String soLo, LocalDate ngaySanXuat, LocalDate ngayNhap, LocalDate hanSuDung, long giaNhap, int soLuongNhap, int soLuongTon, LocalDate ngayKiemKe, String trangThai) {
        this.id = id;
        this.maLo = maLo;
        this.sanPhamId = sanPhamId;
        this.soLo = soLo;
        this.ngaySanXuat = ngaySanXuat;
        this.ngayNhap = ngayNhap;
        this.hanSuDung = hanSuDung;
        this.giaNhap = giaNhap;
        this.soLuongNhap = soLuongNhap;
        this.soLuongTon = soLuongTon;
        this.ngayKiemKe = ngayKiemKe;
        this.trangThai = trangThai;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaLo() { return this.maLo; }
    public String layMaLo() { return this.maLo; }
    public String maLo() { return this.maLo; }
    public void setMaLo(String maLo) { this.maLo = maLo; }
    public void datMaLo(String maLo) { this.maLo = maLo; }

    public long getSanPhamId() { return this.sanPhamId; }
    public long laySanPhamId() { return this.sanPhamId; }
    public long sanPhamId() { return this.sanPhamId; }
    public void setSanPhamId(long sanPhamId) { this.sanPhamId = sanPhamId; }
    public void datSanPhamId(long sanPhamId) { this.sanPhamId = sanPhamId; }

    public String getSoLo() { return this.soLo; }
    public String laySoLo() { return this.soLo; }
    public String soLo() { return this.soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }
    public void datSoLo(String soLo) { this.soLo = soLo; }

    public LocalDate getNgaySanXuat() { return this.ngaySanXuat; }
    public LocalDate layNgaySanXuat() { return this.ngaySanXuat; }
    public LocalDate ngaySanXuat() { return this.ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }
    public void datNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public LocalDate getNgayNhap() { return this.ngayNhap; }
    public LocalDate layNgayNhap() { return this.ngayNhap; }
    public LocalDate ngayNhap() { return this.ngayNhap; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }
    public void datNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }

    public LocalDate getHanSuDung() { return this.hanSuDung; }
    public LocalDate layHanSuDung() { return this.hanSuDung; }
    public LocalDate hanSuDung() { return this.hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }
    public void datHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }

    public long getGiaNhap() { return this.giaNhap; }
    public long layGiaNhap() { return this.giaNhap; }
    public long giaNhap() { return this.giaNhap; }
    public void setGiaNhap(long giaNhap) { this.giaNhap = giaNhap; }
    public void datGiaNhap(long giaNhap) { this.giaNhap = giaNhap; }

    public int getSoLuongNhap() { return this.soLuongNhap; }
    public int laySoLuongNhap() { return this.soLuongNhap; }
    public int soLuongNhap() { return this.soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }
    public void datSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }

    public int getSoLuongTon() { return this.soLuongTon; }
    public int laySoLuongTon() { return this.soLuongTon; }
    public int soLuongTon() { return this.soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
    public void datSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public LocalDate getNgayKiemKe() { return this.ngayKiemKe; }
    public LocalDate layNgayKiemKe() { return this.ngayKiemKe; }
    public LocalDate ngayKiemKe() { return this.ngayKiemKe; }
    public void setNgayKiemKe(LocalDate ngayKiemKe) { this.ngayKiemKe = ngayKiemKe; }
    public void datNgayKiemKe(LocalDate ngayKiemKe) { this.ngayKiemKe = ngayKiemKe; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoHang that = (LoHang) o;
        return this.id == that.id
                && Objects.equals(maLo, that.maLo)
                && this.sanPhamId == that.sanPhamId
                && Objects.equals(soLo, that.soLo)
                && Objects.equals(ngaySanXuat, that.ngaySanXuat)
                && Objects.equals(ngayNhap, that.ngayNhap)
                && Objects.equals(hanSuDung, that.hanSuDung)
                && this.giaNhap == that.giaNhap
                && this.soLuongNhap == that.soLuongNhap
                && this.soLuongTon == that.soLuongTon
                && Objects.equals(ngayKiemKe, that.ngayKiemKe)
                && Objects.equals(trangThai, that.trangThai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maLo, sanPhamId, soLo, ngaySanXuat, ngayNhap, hanSuDung, giaNhap, soLuongNhap, soLuongTon, ngayKiemKe, trangThai);
    }

    @Override
    public String toString() {
        return "LoHang{" + "id=" + id + ", " + "maLo=" + maLo + ", " + "sanPhamId=" + sanPhamId + ", " + "soLo=" + soLo + ", " + "ngaySanXuat=" + ngaySanXuat + ", " + "ngayNhap=" + ngayNhap + ", " + "hanSuDung=" + hanSuDung + ", " + "giaNhap=" + giaNhap + ", " + "soLuongNhap=" + soLuongNhap + ", " + "soLuongTon=" + soLuongTon + ", " + "ngayKiemKe=" + ngayKiemKe + ", " + "trangThai=" + trangThai + "}";
    }
}
