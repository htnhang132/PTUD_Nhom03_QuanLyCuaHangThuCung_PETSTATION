package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.util.Objects;

public class GoiDichVuSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maGoi;
    private String tenGoi;
    private String moTa;
    private int thoiGianDuKien;
    private String chungLoaiApDung;
    private long donGiaGoc;
    private String bangGia;
    private String trangThai;

    public GoiDichVuSpa() {
        this.trangThai = "HOAT_DONG";
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
            String chungLoaiApDung,
            long donGiaGoc,
            String bangGia,
            String trangThai) {
        this();
        setMaGoi(maGoi);
        setTenGoi(tenGoi);
        setMoTa(moTa);
        setThoiGianDuKien(thoiGianDuKien);
        setChungLoaiApDung(chungLoaiApDung);
        setDonGiaGoc(donGiaGoc);
        setBangGia(bangGia);
        setTrangThai(trangThai);
    }

    public GoiDichVuSpa(long id) {
        this();
        setId(id);
    }

    public GoiDichVuSpa(
            long id,
            String maGoi,
            String tenGoi,
            String moTa,
            int thoiGianDuKien,
            String chungLoaiApDung,
            long donGiaGoc,
            String bangGia,
            String trangThai) {
        this(maGoi, tenGoi, moTa, thoiGianDuKien, chungLoaiApDung, donGiaGoc, bangGia, trangThai);
        setId(id);
    }

    public double tinhThoiGianTheoGio() {
        return Math.max(0, thoiGianDuKien) / 60.0;
    }

    public boolean kiemTraDangPhucVu() {
        return trangThai == null || "HOAT_DONG".equalsIgnoreCase(trangThai) || "Đang áp dụng".equalsIgnoreCase(trangThai) || "Đang hoạt động".equalsIgnoreCase(trangThai);
    }

    public String getMaGoi() { return this.maGoi; }
    public String layMaGoi() { return this.maGoi; }
    public String maGoi() { return this.maGoi; }
    public void setMaGoi(String maGoi) { this.maGoi = maGoi == null ? null : maGoi.trim(); }
    public void datMaGoi(String maGoi) { setMaGoi(maGoi); }

    public String getTenGoi() { return this.tenGoi; }
    public String layTenGoi() { return this.tenGoi; }
    public String tenGoi() { return this.tenGoi; }
    public void setTenGoi(String tenGoi) { this.tenGoi = tenGoi == null ? null : tenGoi.trim(); }
    public void datTenGoi(String tenGoi) { setTenGoi(tenGoi); }

    public String getMoTa() { return this.moTa; }
    public String layMoTa() { return this.moTa; }
    public String moTa() { return this.moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public void datMoTa(String moTa) { setMoTa(moTa); }

    public int getThoiGianDuKien() { return this.thoiGianDuKien; }
    public int layThoiGianDuKien() { return this.thoiGianDuKien; }
    public int thoiGianDuKien() { return this.thoiGianDuKien; }
    public void setThoiGianDuKien(int thoiGianDuKien) {
        if (thoiGianDuKien < 0) throw new IllegalArgumentException("Thời gian dự kiến không được âm.");
        this.thoiGianDuKien = thoiGianDuKien;
    }
    public void datThoiGianDuKien(int thoiGianDuKien) { setThoiGianDuKien(thoiGianDuKien); }

    public String getChungLoaiApDung() { return this.chungLoaiApDung; }
    public String layChungLoaiApDung() { return this.chungLoaiApDung; }
    public String chungLoaiApDung() { return this.chungLoaiApDung; }
    public void setChungLoaiApDung(String chungLoaiApDung) { this.chungLoaiApDung = chungLoaiApDung; }
    public void datChungLoaiApDung(String chungLoaiApDung) { setChungLoaiApDung(chungLoaiApDung); }

    public long getDonGiaGoc() { return this.donGiaGoc; }
    public long layDonGiaGoc() { return this.donGiaGoc; }
    public long donGiaGoc() { return this.donGiaGoc; }
    public void setDonGiaGoc(long donGiaGoc) {
        if (donGiaGoc < 0) throw new IllegalArgumentException("Đơn giá gốc không được âm.");
        this.donGiaGoc = donGiaGoc;
    }
    public void datDonGiaGoc(long donGiaGoc) { setDonGiaGoc(donGiaGoc); }

    public String getBangGia() { return this.bangGia; }
    public String layBangGia() { return this.bangGia; }
    public String bangGia() { return this.bangGia; }
    public void setBangGia(String bangGia) { this.bangGia = bangGia; }
    public void datBangGia(String bangGia) { setBangGia(bangGia); }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { setTrangThai(trangThai); }

    public long getId() { return DuLieu.layIdCauNoi(this, maGoi); }
    public long layId() { return getId(); }
    public long id() { return getId(); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maGoi == null || this.maGoi.isBlank()) && id > 0) {
            this.maGoi = DuLieu.taoMaTuId("SPA", 8, id);
        }
    }
    public void datId(long id) { setId(id); }

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
        return "GoiDichVuSpa{" + "maGoi='" + maGoi + '\'' + ", tenGoi='" + tenGoi + '\'' + ", donGiaGoc=" + donGiaGoc + ", trangThai='" + trangThai + '\'' + '}';
    }
}
