package com.petstation.model.banhang;

import com.petstation.model.chung.TrangThaiPhieuDoiTra;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieu;
    private HoaDon hoaDonGoc;
    private HoaDon hoaDonThayThe;
    private NhanVien nguoiLap;
    private String loaiPhieu;
    private String lyDo;
    private long giaTriDuocDoi;
    private String trangThai;
    private LocalDateTime taoLuc;
    private String khoaThaoTac;
    private long chenhLech;

    public PhieuDoiTra() {
        this.trangThai = TrangThaiPhieuDoiTra.HOAN_TAT.maDB();
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuDoiTra(String maPhieu) {
        this();
        setMaPhieu(maPhieu);
    }

    public PhieuDoiTra(
            String maPhieu,
            HoaDon hoaDonGoc,
            HoaDon hoaDonThayThe,
            NhanVien nguoiLap,
            String loaiPhieu,
            String lyDo,
            long giaTriDuocDoi,
            String trangThai,
            LocalDateTime taoLuc,
            String khoaThaoTac,
            long chenhLech) {
        this();
        setMaPhieu(maPhieu);
        setHoaDonGoc(hoaDonGoc);
        setHoaDonThayThe(hoaDonThayThe);
        setNguoiLap(nguoiLap);
        setLoaiPhieu(loaiPhieu);
        setLyDo(lyDo);
        setGiaTriDuocDoi(giaTriDuocDoi);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setKhoaThaoTac(khoaThaoTac);
        setChenhLech(chenhLech);
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

    public HoaDon getHoaDonThayThe() {
        return this.hoaDonThayThe;
    }

    public void setHoaDonThayThe(HoaDon hoaDonThayThe) {
        this.hoaDonThayThe = hoaDonThayThe;
    }

    public NhanVien getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(NhanVien nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public String getLoaiPhieu() {
        return this.loaiPhieu;
    }

    public void setLoaiPhieu(String loaiPhieu) {
        this.loaiPhieu = loaiPhieu;
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public long getGiaTriDuocDoi() {
        return this.giaTriDuocDoi;
    }

    public void setGiaTriDuocDoi(long giaTriDuocDoi) {
        if (giaTriDuocDoi < 0)
            throw new IllegalArgumentException("Giá trị được đổi không được âm.");
        this.giaTriDuocDoi = giaTriDuocDoi;
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

    public String getKhoaThaoTac() {
        return this.khoaThaoTac;
    }

    public void setKhoaThaoTac(String khoaThaoTac) {
        this.khoaThaoTac = khoaThaoTac;
    }

    public long getChenhLech() {
        return this.chenhLech;
    }

    public void setChenhLech(long chenhLech) {
        this.chenhLech = chenhLech;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuDoiTra that = (PhieuDoiTra) o;
        return Objects.equals(this.maPhieu, that.maPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieu);
    }

    @Override
    public String toString() {
        return "PhieuDoiTra{"
                + "maPhieu='"
                + maPhieu
                + '\''
                + ", hoaDonGoc="
                + (hoaDonGoc != null ? hoaDonGoc.getMaHoaDon() : null)
                + ", chenhLech="
                + chenhLech
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
