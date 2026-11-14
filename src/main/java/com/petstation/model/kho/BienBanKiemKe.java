package com.petstation.model.kho;

import com.petstation.model.chung.TrangThaiBienBanKiemKe;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BienBanKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maBienBan;
    private NhanVien nguoiLap;
    private List<ChiTietKiemKe> danhSachChiTiet = new ArrayList<>();
    private String kyKiemKe;
    private String trangThai;
    private LocalDateTime taoLuc;

    public BienBanKiemKe() {
        this.trangThai = TrangThaiBienBanKiemKe.DANG_KIEM.maDB();
        this.taoLuc = LocalDateTime.now();
    }

    public BienBanKiemKe(String maBienBan) {
        this();
        setMaBienBan(maBienBan);
    }

    public BienBanKiemKe(
            String soBienBan,
            NhanVien nguoiLap,
            String kyKiemKe,
            String trangThai,
            LocalDateTime taoLuc) {
        this();
        setSoBienBan(soBienBan);
        setNguoiLap(nguoiLap);
        setKyKiemKe(kyKiemKe);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
    }

    public String getMaBienBan() {
        return this.maBienBan;
    }

    public void setMaBienBan(String maBienBan) {
        this.maBienBan = maBienBan == null ? null : maBienBan.trim();
    }

    public void setSoBienBan(String soBienBan) {
        setMaBienBan(soBienBan);
    }

    public NhanVien getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(NhanVien nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public List<ChiTietKiemKe> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietKiemKe> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>();
    }

    public String getKyKiemKe() {
        return this.kyKiemKe;
    }

    public void setKyKiemKe(String kyKiemKe) {
        this.kyKiemKe = kyKiemKe;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BienBanKiemKe that = (BienBanKiemKe) o;
        return Objects.equals(this.getMaBienBan(), that.getMaBienBan());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaBienBan());
    }

    @Override
    public String toString() {
        return "BienBanKiemKe{"
                + "maBienBan='"
                + getMaBienBan()
                + '\''
                + ", nguoiLap="
                + (nguoiLap != null ? nguoiLap.getMaNhanVien() : null)
                + ", kyKiemKe='"
                + kyKiemKe
                + '\''
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
