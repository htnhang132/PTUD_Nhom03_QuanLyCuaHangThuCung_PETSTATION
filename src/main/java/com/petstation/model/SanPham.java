package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng san_pham trong SQL Server. */
public class SanPham {
    private Long id;
    private String maSanPham;
    private String barcode;
    private String tenSanPham;
    private Long idNhomHang;
    private String donVi;
    private BigDecimal giaBan;
    private BigDecimal giaVon;
    private Integer nguongTonThap;
    private Boolean theoDoiHsd;
    private String urlAnh;
    private String thuongHieu;
    private String quyCach;
    private String doiTuongSuDung;
    private String moTa;
    private Boolean canDonThuoc;
    private Boolean dangHoatDong;
    private LocalDateTime thoiDiemTao;
    private byte[] phienBanBanGhi;

    public SanPham() {
        this.canDonThuoc = false;
        this.dangHoatDong = true;
        this.theoDoiHsd = false;
        this.nguongTonThap = 5;
    }

    public SanPham(Long id, String maSanPham, String tenSanPham, BigDecimal giaBan) {
        this();
        this.id = id;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
    }

    public Long layId() {
        return id;
    }

    public void datId(Long id) {
        this.id = id;
    }

    public String layMaSanPham() {
        return maSanPham;
    }

    public void datMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String layBarcode() {
        return barcode;
    }

    public void datBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String layTenSanPham() {
        return tenSanPham;
    }

    public void datTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public Long layIdNhomHang() {
        return idNhomHang;
    }

    public void datIdNhomHang(Long idNhomHang) {
        this.idNhomHang = idNhomHang;
    }

    public String layDonVi() {
        return donVi;
    }

    public void datDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal layGiaBan() {
        return giaBan;
    }

    public void datGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public BigDecimal layGiaVon() {
        return giaVon;
    }

    public void datGiaVon(BigDecimal giaVon) {
        this.giaVon = giaVon;
    }

    public Integer layNguongTonThap() {
        return nguongTonThap;
    }

    public void datNguongTonThap(Integer nguongTonThap) {
        this.nguongTonThap = nguongTonThap;
    }

    public Boolean layTheoDoiHsd() {
        return theoDoiHsd;
    }

    public void datTheoDoiHsd(Boolean theoDoiHsd) {
        this.theoDoiHsd = theoDoiHsd;
    }

    public String layURLAnh() {
        return urlAnh;
    }

    public void datURLAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    public String layThuongHieu() {
        return thuongHieu;
    }

    public void datThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String layQuyCach() {
        return quyCach;
    }

    public void datQuyCach(String quyCach) {
        this.quyCach = quyCach;
    }

    public String layDoiTuongSuDung() {
        return doiTuongSuDung;
    }

    public void datDoiTuongSuDung(String doiTuongSuDung) {
        this.doiTuongSuDung = doiTuongSuDung;
    }

    public String layMoTa() {
        return moTa;
    }

    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Boolean layCanDonThuoc() {
        return canDonThuoc;
    }

    public void datCanDonThuoc(Boolean canDonThuoc) {
        this.canDonThuoc = canDonThuoc;
    }

    public Boolean layDangHoatDong() {
        return dangHoatDong;
    }

    public void datDangHoatDong(Boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    @Override
    public String toString() {
        return "SanPham{"
                + "id="
                + id
                + ", maSanPham='"
                + maSanPham
                + '\''
                + ", tenSanPham='"
                + tenSanPham
                + '\''
                + ", giaBan="
                + giaBan
                + ", dangHoatDong="
                + dangHoatDong
                + '}';
    }
}
