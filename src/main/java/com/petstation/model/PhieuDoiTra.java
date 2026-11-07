package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieu;
    private HoaDon hoaDonGoc;
    private TaiKhoan nguoiLap;
    private HoaDon hoaDonThayThe;
    private List<ChiTietDoiTra> danhSachChiTiet = new ArrayList<>();
    private long chenhLech;
    private String trangThai;
    private LocalDateTime taoLuc;
    private String lyDo;
    private String loaiPhieu;
    private long giaTriDuocDoi;
    private String khoaThaoTac;

    public PhieuDoiTra() {
        this.trangThai = "HOAN_TAT";
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuDoiTra(String maPhieu) {
        this();
        setMaPhieu(maPhieu);
    }

    public PhieuDoiTra(
            String maPhieu,
            HoaDon hoaDonGoc,
            TaiKhoan nguoiLap,
            long chenhLech,
            String trangThai,
            LocalDateTime taoLuc,
            String lyDo,
            String loaiPhieu,
            HoaDon hoaDonThayThe,
            long giaTriDuocDoi,
            String khoaThaoTac) {
        this();
        setMaPhieu(maPhieu);
        setHoaDonGoc(hoaDonGoc);
        setNguoiLap(nguoiLap);
        setChenhLech(chenhLech);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setLyDo(lyDo);
        setLoaiPhieu(loaiPhieu);
        setHoaDonThayThe(hoaDonThayThe);
        setGiaTriDuocDoi(giaTriDuocDoi);
        setKhoaThaoTac(khoaThaoTac);
    }

    public long tinhTongGiaTriHoan() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietDoiTra ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhTienHoan();
                }
            }
            return tong;
        }
        return Math.max(0L, this.giaTriDuocDoi);
    }

    public String getMaPhieu() {
        return this.maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu == null ? null : maPhieu.trim();
    }

    public HoaDon getHoaDonGoc() {
        return this.hoaDonGoc;
    }

    public void setHoaDonGoc(HoaDon hoaDonGoc) {
        this.hoaDonGoc = hoaDonGoc;
    }

    public String getMaHoaDonGoc() {
        return hoaDonGoc == null ? null : hoaDonGoc.getMaHoaDon();
    }

    public void setMaHoaDonGoc(String maHoaDon) {
        this.hoaDonGoc = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
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

    public List<ChiTietDoiTra> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietDoiTra> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public long getChenhLech() {
        return this.chenhLech;
    }

    public void setChenhLech(long chenhLech) {
        this.chenhLech = chenhLech;
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

    public String getLyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getLoaiPhieu() {
        return this.loaiPhieu;
    }

    public void setLoaiPhieu(String loaiPhieu) {
        this.loaiPhieu = loaiPhieu;
    }

    public HoaDon getHoaDonThayThe() {
        return this.hoaDonThayThe;
    }

    public void setHoaDonThayThe(HoaDon hoaDonThayThe) {
        this.hoaDonThayThe = hoaDonThayThe;
    }

    public String getMaHoaDonThayThe() {
        return hoaDonThayThe == null ? null : hoaDonThayThe.getMaHoaDon();
    }

    public void setMaHoaDonThayThe(String maHoaDon) {
        this.hoaDonThayThe = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
    }

    public long getGiaTriDuocDoi() {
        return this.giaTriDuocDoi;
    }

    public void setGiaTriDuocDoi(long giaTriDuocDoi) {
        if (giaTriDuocDoi < 0)
            throw new IllegalArgumentException("Giá trị được đổi không được âm.");
        this.giaTriDuocDoi = giaTriDuocDoi;
    }

    public String getKhoaThaoTac() {
        return this.khoaThaoTac;
    }

    public void setKhoaThaoTac(String khoaThaoTac) {
        this.khoaThaoTac = khoaThaoTac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuDoiTra that = (PhieuDoiTra) o;
        return Objects.equals(this.maPhieu, that.maPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieu);
    }

    @Override
    public String toString() {
        return "PhieuDoiTra{" + "maPhieu='" + maPhieu + '\'' + ", hoaDonGoc=" + (hoaDonGoc != null ? hoaDonGoc.getMaHoaDon() : null)
                + ", chenhLech=" + chenhLech + ", trangThai='" + trangThai + '\'' + '}';
    }
}
