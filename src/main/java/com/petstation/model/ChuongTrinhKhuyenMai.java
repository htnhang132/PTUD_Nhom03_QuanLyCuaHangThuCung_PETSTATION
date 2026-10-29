package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể chương trình khuyến mãi khớp bảng [chuong_trinh_khuyen_mai].
 */
public class ChuongTrinhKhuyenMai implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maChuongTrinh;
    private String ten;
    private String loaiGiam;
    private double giaTri;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String nguon;
    private String trangThai;
    private long donToiThieu;

    public ChuongTrinhKhuyenMai() {}

    public ChuongTrinhKhuyenMai(long id, String maChuongTrinh, String ten, String loaiGiam, double giaTri, LocalDateTime batDau, LocalDateTime ketThuc, String nguon, String trangThai, long donToiThieu) {
        this.id = id;
        this.maChuongTrinh = maChuongTrinh;
        this.ten = ten;
        this.loaiGiam = loaiGiam;
        this.giaTri = giaTri;
        this.batDau = batDau;
        this.ketThuc = ketThuc;
        this.nguon = nguon;
        this.trangThai = trangThai;
        this.donToiThieu = donToiThieu;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaChuongTrinh() { return this.maChuongTrinh; }
    public String layMaChuongTrinh() { return this.maChuongTrinh; }
    public String maChuongTrinh() { return this.maChuongTrinh; }
    public void setMaChuongTrinh(String maChuongTrinh) { this.maChuongTrinh = maChuongTrinh; }
    public void datMaChuongTrinh(String maChuongTrinh) { this.maChuongTrinh = maChuongTrinh; }

    public String getTen() { return this.ten; }
    public String layTen() { return this.ten; }
    public String ten() { return this.ten; }
    public void setTen(String ten) { this.ten = ten; }
    public void datTen(String ten) { this.ten = ten; }

    public String getLoaiGiam() { return this.loaiGiam; }
    public String layLoaiGiam() { return this.loaiGiam; }
    public String loaiGiam() { return this.loaiGiam; }
    public void setLoaiGiam(String loaiGiam) { this.loaiGiam = loaiGiam; }
    public void datLoaiGiam(String loaiGiam) { this.loaiGiam = loaiGiam; }

    public double getGiaTri() { return this.giaTri; }
    public double layGiaTri() { return this.giaTri; }
    public double giaTri() { return this.giaTri; }
    public void setGiaTri(double giaTri) { this.giaTri = giaTri; }
    public void datGiaTri(double giaTri) { this.giaTri = giaTri; }

    public LocalDateTime getBatDau() { return this.batDau; }
    public LocalDateTime layBatDau() { return this.batDau; }
    public LocalDateTime batDau() { return this.batDau; }
    public void setBatDau(LocalDateTime batDau) { this.batDau = batDau; }
    public void datBatDau(LocalDateTime batDau) { this.batDau = batDau; }

    public LocalDateTime getKetThuc() { return this.ketThuc; }
    public LocalDateTime layKetThuc() { return this.ketThuc; }
    public LocalDateTime ketThuc() { return this.ketThuc; }
    public void setKetThuc(LocalDateTime ketThuc) { this.ketThuc = ketThuc; }
    public void datKetThuc(LocalDateTime ketThuc) { this.ketThuc = ketThuc; }

    public String getNguon() { return this.nguon; }
    public String layNguon() { return this.nguon; }
    public String nguon() { return this.nguon; }
    public void setNguon(String nguon) { this.nguon = nguon; }
    public void datNguon(String nguon) { this.nguon = nguon; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public long getDonToiThieu() { return this.donToiThieu; }
    public long layDonToiThieu() { return this.donToiThieu; }
    public long donToiThieu() { return this.donToiThieu; }
    public void setDonToiThieu(long donToiThieu) { this.donToiThieu = donToiThieu; }
    public void datDonToiThieu(long donToiThieu) { this.donToiThieu = donToiThieu; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChuongTrinhKhuyenMai that = (ChuongTrinhKhuyenMai) o;
        return this.id == that.id
                && Objects.equals(maChuongTrinh, that.maChuongTrinh)
                && Objects.equals(ten, that.ten)
                && Objects.equals(loaiGiam, that.loaiGiam)
                && this.giaTri == that.giaTri
                && Objects.equals(batDau, that.batDau)
                && Objects.equals(ketThuc, that.ketThuc)
                && Objects.equals(nguon, that.nguon)
                && Objects.equals(trangThai, that.trangThai)
                && this.donToiThieu == that.donToiThieu;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maChuongTrinh, ten, loaiGiam, giaTri, batDau, ketThuc, nguon, trangThai, donToiThieu);
    }

    @Override
    public String toString() {
        return "ChuongTrinhKhuyenMai{" + "id=" + id + ", " + "maChuongTrinh=" + maChuongTrinh + ", " + "ten=" + ten + ", " + "loaiGiam=" + loaiGiam + ", " + "giaTri=" + giaTri + ", " + "batDau=" + batDau + ", " + "ketThuc=" + ketThuc + ", " + "nguon=" + nguon + ", " + "trangThai=" + trangThai + ", " + "donToiThieu=" + donToiThieu + "}";
    }
}
