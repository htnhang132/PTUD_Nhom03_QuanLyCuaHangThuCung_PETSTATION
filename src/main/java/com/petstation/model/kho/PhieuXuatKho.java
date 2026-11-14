package com.petstation.model.kho;

import com.petstation.model.chung.TrangThaiPhieuXuat;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuXuatKho implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maPhieuXuat;
    private NhanVien nguoiLap;
    private List<ChiTietPhieuXuat> danhSachChiTiet = new ArrayList<>();
    private String loaiXuat;
    private String lyDo;
    private String trangThai;
    private LocalDateTime taoLuc;
    private long tongGiaTri;

    public PhieuXuatKho() {
        this.trangThai = TrangThaiPhieuXuat.CHO_XUAT.maDB();
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuXuatKho(String maPhieuXuat) {
        this();
        setMaPhieuXuat(maPhieuXuat);
    }

    public PhieuXuatKho(
            String maPhieuXuat,
            NhanVien nguoiLap,
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

    public String getMaPhieuXuat() {
        return this.maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat == null ? null : maPhieuXuat.trim();
    }

    public NhanVien getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(NhanVien nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public List<ChiTietPhieuXuat> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuXuat> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>();
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

    public long getTongGiaTri() {
        return this.tongGiaTri;
    }

    public void setTongGiaTri(long tongGiaTri) {
        if (tongGiaTri < 0) throw new IllegalArgumentException("Tổng giá trị xuất không được âm.");
        this.tongGiaTri = tongGiaTri;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuXuatKho that = (PhieuXuatKho) o;
        return Objects.equals(this.getMaPhieuXuat(), that.getMaPhieuXuat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaPhieuXuat());
    }

    @Override
    public String toString() {
        return "PhieuXuatKho{"
                + "maPhieuXuat='"
                + getMaPhieuXuat()
                + '\''
                + ", nguoiLap="
                + (nguoiLap != null ? nguoiLap.getMaNhanVien() : null)
                + ", loaiXuat='"
                + loaiXuat
                + '\''
                + ", tongGiaTri="
                + tinhTongGiaTri()
                + '}';
    }
}
