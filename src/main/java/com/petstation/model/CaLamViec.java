package com.petstation.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Thực thể ca làm việc khớp bảng [ca_lam_viec].
 */
public class CaLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private int soNhanSuToiThieu;
    private double heSoLuong;

    public CaLamViec() {}

    public CaLamViec(long id, String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc, int soNhanSuToiThieu, double heSoLuong) {
        this.id = id;
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.soNhanSuToiThieu = soNhanSuToiThieu;
        this.heSoLuong = heSoLuong;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaCa() { return this.maCa; }
    public String layMaCa() { return this.maCa; }
    public String maCa() { return this.maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }
    public void datMaCa(String maCa) { this.maCa = maCa; }

    public String getTenCa() { return this.tenCa; }
    public String layTenCa() { return this.tenCa; }
    public String tenCa() { return this.tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public void datTenCa(String tenCa) { this.tenCa = tenCa; }

    public LocalTime getGioBatDau() { return this.gioBatDau; }
    public LocalTime layGioBatDau() { return this.gioBatDau; }
    public LocalTime gioBatDau() { return this.gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public void datGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }

    public LocalTime getGioKetThuc() { return this.gioKetThuc; }
    public LocalTime layGioKetThuc() { return this.gioKetThuc; }
    public LocalTime gioKetThuc() { return this.gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public void datGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public int getSoNhanSuToiThieu() { return this.soNhanSuToiThieu; }
    public int laySoNhanSuToiThieu() { return this.soNhanSuToiThieu; }
    public int soNhanSuToiThieu() { return this.soNhanSuToiThieu; }
    public void setSoNhanSuToiThieu(int soNhanSuToiThieu) { this.soNhanSuToiThieu = soNhanSuToiThieu; }
    public void datSoNhanSuToiThieu(int soNhanSuToiThieu) { this.soNhanSuToiThieu = soNhanSuToiThieu; }

    public double getHeSoLuong() { return this.heSoLuong; }
    public double layHeSoLuong() { return this.heSoLuong; }
    public double heSoLuong() { return this.heSoLuong; }
    public void setHeSoLuong(double heSoLuong) { this.heSoLuong = heSoLuong; }
    public void datHeSoLuong(double heSoLuong) { this.heSoLuong = heSoLuong; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaLamViec that = (CaLamViec) o;
        return this.id == that.id
                && Objects.equals(maCa, that.maCa)
                && Objects.equals(tenCa, that.tenCa)
                && Objects.equals(gioBatDau, that.gioBatDau)
                && Objects.equals(gioKetThuc, that.gioKetThuc)
                && this.soNhanSuToiThieu == that.soNhanSuToiThieu
                && this.heSoLuong == that.heSoLuong;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maCa, tenCa, gioBatDau, gioKetThuc, soNhanSuToiThieu, heSoLuong);
    }

    @Override
    public String toString() {
        return "CaLamViec{" + "id=" + id + ", " + "maCa=" + maCa + ", " + "tenCa=" + tenCa + ", " + "gioBatDau=" + gioBatDau + ", " + "gioKetThuc=" + gioKetThuc + ", " + "soNhanSuToiThieu=" + soNhanSuToiThieu + ", " + "heSoLuong=" + heSoLuong + "}";
    }
}
