package com.petstation.model.spa;

import com.petstation.model.banhang.HoaDon;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PhieuTiepNhanSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieu;
    private LichHen lichHen;
    private ThuCung thuCung;
    private GoiDichVuSpa goiDichVu;
    private NhanVien nhanVien;
    private HoaDon hoaDon;
    private String moTaDichVu;
    private long donGia;
    private int soLuong;
    private double canNangThucTe;
    private TrangThaiPhieuSpa trangThai;
    private LocalDateTime taoLuc;
    private String ghiChu;
    private String anh;
    private long tongTien;

    public PhieuTiepNhanSpa() {
        this.soLuong = 1;
        this.trangThai = TrangThaiPhieuSpa.TIEP_NHAN;
        this.ghiChu = "";
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public ThuCung getThuCung() {
        return thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public GoiDichVuSpa getGoiDichVu() {
        return goiDichVu;
    }

    public void setGoiDichVu(GoiDichVuSpa goiDichVu) {
        this.goiDichVu = goiDichVu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getMoTaDichVu() {
        return moTaDichVu;
    }

    public void setMoTaDichVu(String moTaDichVu) {
        this.moTaDichVu = moTaDichVu;
    }

    public long getDonGia() {
        return donGia;
    }

    public void setDonGia(long donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getCanNangThucTe() {
        return canNangThucTe;
    }

    public void setCanNangThucTe(double canNangThucTe) {
        this.canNangThucTe = canNangThucTe;
    }

    public TrangThaiPhieuSpa getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPhieuSpa trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getTaoLuc() {
        return taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }
}
