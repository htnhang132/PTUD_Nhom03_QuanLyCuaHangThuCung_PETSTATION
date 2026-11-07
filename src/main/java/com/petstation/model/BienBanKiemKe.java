package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BienBanKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maBienBan;
    private TaiKhoan nguoiLap;
    private List<ChiTietKiemKe> danhSachChiTiet = new ArrayList<>();
    private String kyKiemKe;
    private String trangThai;
    private LocalDateTime taoLuc;

    public BienBanKiemKe() {
        this.trangThai = "DA_DUYET";
        this.taoLuc = LocalDateTime.now();
    }

    public BienBanKiemKe(String maBienBan) {
        this();
        setMaBienBan(maBienBan);
    }

    public BienBanKiemKe(
            String soBienBan,
            TaiKhoan nguoiLap,
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

    public int tinhSoLoLechKho() {
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            return 0;
        }
        int dem = 0;
        for (ChiTietKiemKe ct : danhSachChiTiet) {
            if (ct != null && ct.kiemTraCoLechKho()) {
                dem++;
            }
        }
        return dem;
    }

    public int getSoLoLechKho() {
        return tinhSoLoLechKho();
    }

    public String getMaBienBan() {
        return this.maBienBan;
    }

    public void setMaBienBan(String maBienBan) {
        this.maBienBan = maBienBan == null ? null : maBienBan.trim();
    }

    public String getSoBienBan() {
        return getMaBienBan();
    }

    public void setSoBienBan(String soBienBan) {
        setMaBienBan(soBienBan);
    }

    public TaiKhoan getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(TaiKhoan nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public String getTenDangNhapNguoiLap() {
        return nguoiLap == null ? null : nguoiLap.getTenDangNhap();
    }

    public void setTenDangNhapNguoiLap(String tenDangNhap) {
        this.nguoiLap = (tenDangNhap == null || tenDangNhap.isBlank()) ? null : new TaiKhoan(tenDangNhap);
    }

    public List<ChiTietKiemKe> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietKiemKe> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BienBanKiemKe that = (BienBanKiemKe) o;
        return Objects.equals(this.getMaBienBan(), that.getMaBienBan());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaBienBan());
    }

    @Override
    public String toString() {
        return "BienBanKiemKe{" + "maBienBan='" + getMaBienBan() + '\'' + ", nguoiLap=" + (nguoiLap != null ? nguoiLap.getTenDangNhap() : null)
                + ", kyKiemKe='" + kyKiemKe + '\'' + ", trangThai='" + trangThai + '\'' + '}';
    }
}
