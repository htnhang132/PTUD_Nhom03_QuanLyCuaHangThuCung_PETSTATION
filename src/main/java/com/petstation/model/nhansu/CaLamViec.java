package com.petstation.model.nhansu;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

public class CaLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public CaLamViec() {}

    public CaLamViec(String maCa) {
        this();
        setMaCa(maCa);
    }

    public CaLamViec(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc) {
        this();
        setMaCa(maCa);
        setTenCa(tenCa);
        setGioBatDau(gioBatDau);
        setGioKetThuc(gioKetThuc);
    }

    public String getMaCa() {
        return this.maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa == null ? null : maCa.trim();
    }

    public String getTenCa() {
        return this.tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa == null ? null : tenCa.trim();
    }

    public LocalTime getGioBatDau() {
        return this.gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return this.gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

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
        return "CaLamViec{"
                + "maCa='"
                + maCa
                + '\''
                + ", tenCa='"
                + tenCa
                + '\''
                + ", gioBatDau="
                + gioBatDau
                + ", gioKetThuc="
                + gioKetThuc
                + '}';
    }
}
