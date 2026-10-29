package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể gói dịch vụ spa khớp bảng [goi_dich_vu_spa].
 */
public class GoiDichVuSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maGoi;
    private String tenGoi;
    private String moTa;
    private int thoiGianDuKien;
    private String chungLoaiApDung;
    private long donGiaGoc;
    private String bangGia;
    private String trangThai;

    public GoiDichVuSpa() {}

    public GoiDichVuSpa(long id, String maGoi, String tenGoi, String moTa, int thoiGianDuKien, String chungLoaiApDung, long donGiaGoc, String bangGia, String trangThai) {
        this.id = id;
        this.maGoi = maGoi;
        this.tenGoi = tenGoi;
        this.moTa = moTa;
        this.thoiGianDuKien = thoiGianDuKien;
        this.chungLoaiApDung = chungLoaiApDung;
        this.donGiaGoc = donGiaGoc;
        this.bangGia = bangGia;
        this.trangThai = trangThai;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaGoi() { return this.maGoi; }
    public String layMaGoi() { return this.maGoi; }
    public String maGoi() { return this.maGoi; }
    public void setMaGoi(String maGoi) { this.maGoi = maGoi; }
    public void datMaGoi(String maGoi) { this.maGoi = maGoi; }

    public String getTenGoi() { return this.tenGoi; }
    public String layTenGoi() { return this.tenGoi; }
    public String tenGoi() { return this.tenGoi; }
    public void setTenGoi(String tenGoi) { this.tenGoi = tenGoi; }
    public void datTenGoi(String tenGoi) { this.tenGoi = tenGoi; }

    public String getMoTa() { return this.moTa; }
    public String layMoTa() { return this.moTa; }
    public String moTa() { return this.moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public void datMoTa(String moTa) { this.moTa = moTa; }

    public int getThoiGianDuKien() { return this.thoiGianDuKien; }
    public int layThoiGianDuKien() { return this.thoiGianDuKien; }
    public int thoiGianDuKien() { return this.thoiGianDuKien; }
    public void setThoiGianDuKien(int thoiGianDuKien) { this.thoiGianDuKien = thoiGianDuKien; }
    public void datThoiGianDuKien(int thoiGianDuKien) { this.thoiGianDuKien = thoiGianDuKien; }

    public String getChungLoaiApDung() { return this.chungLoaiApDung; }
    public String layChungLoaiApDung() { return this.chungLoaiApDung; }
    public String chungLoaiApDung() { return this.chungLoaiApDung; }
    public void setChungLoaiApDung(String chungLoaiApDung) { this.chungLoaiApDung = chungLoaiApDung; }
    public void datChungLoaiApDung(String chungLoaiApDung) { this.chungLoaiApDung = chungLoaiApDung; }

    public long getDonGiaGoc() { return this.donGiaGoc; }
    public long layDonGiaGoc() { return this.donGiaGoc; }
    public long donGiaGoc() { return this.donGiaGoc; }
    public void setDonGiaGoc(long donGiaGoc) { this.donGiaGoc = donGiaGoc; }
    public void datDonGiaGoc(long donGiaGoc) { this.donGiaGoc = donGiaGoc; }

    public String getBangGia() { return this.bangGia; }
    public String layBangGia() { return this.bangGia; }
    public String bangGia() { return this.bangGia; }
    public void setBangGia(String bangGia) { this.bangGia = bangGia; }
    public void datBangGia(String bangGia) { this.bangGia = bangGia; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoiDichVuSpa that = (GoiDichVuSpa) o;
        return this.id == that.id
                && Objects.equals(maGoi, that.maGoi)
                && Objects.equals(tenGoi, that.tenGoi)
                && Objects.equals(moTa, that.moTa)
                && this.thoiGianDuKien == that.thoiGianDuKien
                && Objects.equals(chungLoaiApDung, that.chungLoaiApDung)
                && this.donGiaGoc == that.donGiaGoc
                && Objects.equals(bangGia, that.bangGia)
                && Objects.equals(trangThai, that.trangThai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maGoi, tenGoi, moTa, thoiGianDuKien, chungLoaiApDung, donGiaGoc, bangGia, trangThai);
    }

    @Override
    public String toString() {
        return "GoiDichVuSpa{" + "id=" + id + ", " + "maGoi=" + maGoi + ", " + "tenGoi=" + tenGoi + ", " + "moTa=" + moTa + ", " + "thoiGianDuKien=" + thoiGianDuKien + ", " + "chungLoaiApDung=" + chungLoaiApDung + ", " + "donGiaGoc=" + donGiaGoc + ", " + "bangGia=" + bangGia + ", " + "trangThai=" + trangThai + "}";
    }
}
