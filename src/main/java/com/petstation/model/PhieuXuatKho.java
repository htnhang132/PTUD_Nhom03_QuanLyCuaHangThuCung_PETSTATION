package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuXuatKho implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieuXuat;
    private TaiKhoan nguoiLap;
    private List<ChiTietPhieuXuat> danhSachChiTiet = new ArrayList<>();
    private String loaiXuat;
    private String lyDo;
    private long tongGiaTri;
    private String trangThai;
    private LocalDateTime taoLuc;

    public PhieuXuatKho() {
        this.trangThai = "DA_XUAT";
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuXuatKho(String maPhieuXuat) {
        this();
        setMaPhieuXuat(maPhieuXuat);
    }

    public PhieuXuatKho(
            String maPhieuXuat,
            TaiKhoan nguoiLap,
            String loaiXuat,
            String lyDo,
            String trangThai,
            LocalDateTime taoLuc) {
        this();
        setMaPhieuXuat(maPhieuXuat);
        setNguoiLap(nguoiLap);
        setLoaiXuat(loaiXuat);
        setLyDo(lyDo);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
    }

    public long tinhTongGiaTri() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietPhieuXuat ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhGiaTriXuat();
                }
            }
            return tong;
        }
        return Math.max(0L, this.tongGiaTri);
    }

    public long getTongGiaTri() {
        return tinhTongGiaTri();
    }

    public void setTongGiaTri(long tongGiaTri) {
        this.tongGiaTri = Math.max(0L, tongGiaTri);
    }

    public String getMaPhieuXuat() {
        return this.maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat == null ? null : maPhieuXuat.trim();
    }

    public String getSoPhieu() {
        return getMaPhieuXuat();
    }

    public void setSoPhieu(String soPhieu) {
        setMaPhieuXuat(soPhieu);
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

    public List<ChiTietPhieuXuat> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuXuat> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public void themChiTietPhieuXuat(ChiTietPhieuXuat chiTiet) {
        if (chiTiet == null) return;
        if (this.danhSachChiTiet == null) this.danhSachChiTiet = new ArrayList<>();
        chiTiet.setPhieuXuat(this);
        this.danhSachChiTiet.add(chiTiet);
    }

    public String getLoaiXuat() {
        return this.loaiXuat;
    }

    public void setLoaiXuat(String loaiXuat) {
        this.loaiXuat = loaiXuat;
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
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
        PhieuXuatKho that = (PhieuXuatKho) o;
        return Objects.equals(this.getMaPhieuXuat(), that.getMaPhieuXuat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaPhieuXuat());
    }

    @Override
    public String toString() {
        return "PhieuXuatKho{" + "maPhieuXuat='" + getMaPhieuXuat() + '\'' + ", nguoiLap=" + (nguoiLap != null ? nguoiLap.getTenDangNhap() : null)
                + ", loaiXuat='" + loaiXuat + '\'' + ", tongGiaTri=" + tinhTongGiaTri() + '}';
    }
}
