package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class CaLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private int soNhanSuToiThieu;
    private double heSoLuong;

    public CaLamViec() {
        this.soNhanSuToiThieu = 1;
        this.heSoLuong = 1.0;
    }

    public CaLamViec(String maCa) {
        this();
        setMaCa(maCa);
    }

    public CaLamViec(
            String maCa,
            String tenCa,
            LocalTime gioBatDau,
            LocalTime gioKetThuc,
            int soNhanSuToiThieu,
            double heSoLuong) {
        this();
        setMaCa(maCa);
        setTenCa(tenCa);
        setGioBatDau(gioBatDau);
        setGioKetThuc(gioKetThuc);
        setSoNhanSuToiThieu(soNhanSuToiThieu);
        setHeSoLuong(heSoLuong);
    }

    public CaLamViec(long id) {
        this();
        setId(id);
    }

    public CaLamViec(
            long id,
            String maCa,
            String tenCa,
            LocalTime gioBatDau,
            LocalTime gioKetThuc,
            int soNhanSuToiThieu,
            double heSoLuong) {
        this(maCa, tenCa, gioBatDau, gioKetThuc, soNhanSuToiThieu, heSoLuong);
        setId(id);
    }

    public double tinhSoGioLam() {
        if (gioBatDau == null || gioKetThuc == null) {
            return 0.0;
        }
        long phut = Duration.between(gioBatDau, gioKetThuc).toMinutes();
        if (phut < 0) {
            phut += 24 * 60;
        }
        return phut / 60.0;
    }

    public String getMaCa() { return this.maCa; }
    public String layMaCa() { return this.maCa; }
    public String maCa() { return this.maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa == null ? null : maCa.trim(); }
    public void datMaCa(String maCa) { setMaCa(maCa); }

    public String getTenCa() { return this.tenCa; }
    public String layTenCa() { return this.tenCa; }
    public String tenCa() { return this.tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa == null ? null : tenCa.trim(); }
    public void datTenCa(String tenCa) { setTenCa(tenCa); }

    public LocalTime getGioBatDau() { return this.gioBatDau; }
    public LocalTime layGioBatDau() { return this.gioBatDau; }
    public LocalTime gioBatDau() { return this.gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public void datGioBatDau(LocalTime gioBatDau) { setGioBatDau(gioBatDau); }

    public LocalTime getGioKetThuc() { return this.gioKetThuc; }
    public LocalTime layGioKetThuc() { return this.gioKetThuc; }
    public LocalTime gioKetThuc() { return this.gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public void datGioKetThuc(LocalTime gioKetThuc) { setGioKetThuc(gioKetThuc); }

    public int getSoNhanSuToiThieu() { return this.soNhanSuToiThieu; }
    public int laySoNhanSuToiThieu() { return this.soNhanSuToiThieu; }
    public int soNhanSuToiThieu() { return this.soNhanSuToiThieu; }
    public void setSoNhanSuToiThieu(int soNhanSuToiThieu) {
        if (soNhanSuToiThieu < 0) throw new IllegalArgumentException("Số nhân sự tối thiểu không được âm.");
        this.soNhanSuToiThieu = soNhanSuToiThieu;
    }
    public void datSoNhanSuToiThieu(int soNhanSuToiThieu) { setSoNhanSuToiThieu(soNhanSuToiThieu); }

    public double getHeSoLuong() { return this.heSoLuong; }
    public double layHeSoLuong() { return this.heSoLuong; }
    public double heSoLuong() { return this.heSoLuong; }
    public void setHeSoLuong(double heSoLuong) {
        if (heSoLuong < 0) throw new IllegalArgumentException("Hệ số lương không được âm.");
        this.heSoLuong = heSoLuong;
    }
    public void datHeSoLuong(double heSoLuong) { setHeSoLuong(heSoLuong); }

    public long getId() { return DuLieu.layIdCauNoi(this, maCa); }
    public long layId() { return getId(); }
    public long id() { return getId(); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maCa == null || this.maCa.isBlank()) && id > 0) {
            this.maCa = DuLieu.taoMaTuId("CA", 2, id);
        }
    }
    public void datId(long id) { setId(id); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaLamViec that = (CaLamViec) o;
        return Objects.equals(this.maCa, that.maCa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCa);
    }

    @Override
    public String toString() {
        return "CaLamViec{" + "maCa='" + maCa + '\'' + ", tenCa='" + tenCa + '\'' + ", gioBatDau=" + gioBatDau + ", gioKetThuc=" + gioKetThuc + '}';
    }
}
