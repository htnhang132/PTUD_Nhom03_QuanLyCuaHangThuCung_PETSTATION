package com.petstation.model.spa;

import com.petstation.model.chung.TrangThaiKhuyenMai;

import java.io.Serializable;
import java.util.Objects;

public class GoiDichVuSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maGoi;
    private String tenGoi;
    private String moTa;
    private int thoiGianDuKien;
    private String bangGia;
    private String trangThai;

    public GoiDichVuSpa() {
        this.trangThai = TrangThaiKhuyenMai.HOAT_DONG.maDB();
    }

    public GoiDichVuSpa(String maGoi) {
        this();
        setMaGoi(maGoi);
    }

    public GoiDichVuSpa(
            String maGoi,
            String tenGoi,
            String moTa,
            int thoiGianDuKien,
            String bangGia,
            String trangThai) {
        this();
        setMaGoi(maGoi);
        setTenGoi(tenGoi);
        setMoTa(moTa);
        setThoiGianDuKien(thoiGianDuKien);
        setBangGia(bangGia);
        setTrangThai(trangThai);
    }

    public String getMaGoi() {
        return this.maGoi;
    }

    public void setMaGoi(String maGoi) {
        this.maGoi = maGoi == null ? null : maGoi.trim();
    }

    public String getTenGoi() {
        return this.tenGoi;
    }

    public void setTenGoi(String tenGoi) {
        this.tenGoi = tenGoi == null ? null : tenGoi.trim();
    }

    public String getMoTa() {
        return this.moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getThoiGianDuKien() {
        return this.thoiGianDuKien;
    }

    public void setThoiGianDuKien(int thoiGianDuKien) {
        if (thoiGianDuKien < 0)
            throw new IllegalArgumentException("Thời gian dự kiến không được âm.");
        this.thoiGianDuKien = thoiGianDuKien;
    }

    public String getBangGia() {
        return this.bangGia;
    }

    public void setBangGia(String bangGia) {
        this.bangGia = bangGia;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoiDichVuSpa that = (GoiDichVuSpa) o;
        return Objects.equals(this.maGoi, that.maGoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maGoi);
    }

    @Override
    public String toString() {
        return "GoiDichVuSpa{"
                + "maGoi='"
                + maGoi
                + '\''
                + ", tenGoi='"
                + tenGoi
                + '\''
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
