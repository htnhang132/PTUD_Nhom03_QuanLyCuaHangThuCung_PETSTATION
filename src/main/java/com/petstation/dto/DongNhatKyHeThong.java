package com.petstation.dto;

import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DinhDang;

import java.time.LocalDateTime;

public final class DongNhatKyHeThong {
    private final NhanVien nguoiDung;
    private final LocalDateTime thoiGian;
    private final String thaoTac;
    private final String doiTuong;
    private final String kieu;
    private final String ketQua;
    private final String loaiDoiTuong;
    private final String maDoiTuong;
    private final String ghiChu;

    public DongNhatKyHeThong(
            NhanVien nguoiDung,
            LocalDateTime thoiGian,
            String thaoTac,
            String doiTuong,
            String kieu,
            String ketQua) {
        this(nguoiDung, thoiGian, thaoTac, doiTuong, kieu, ketQua, "", "", doiTuong);
    }

    public DongNhatKyHeThong(
            NhanVien nguoiDung,
            LocalDateTime thoiGian,
            String thaoTac,
            String doiTuong,
            String kieu,
            String ketQua,
            String loaiDoiTuong,
            String maDoiTuong,
            String ghiChu) {
        this.nguoiDung = nguoiDung;
        this.thoiGian = thoiGian;
        this.thaoTac = thaoTac;
        this.doiTuong = doiTuong;
        this.kieu = kieu;
        this.ketQua = ketQua;
        this.loaiDoiTuong = loaiDoiTuong == null ? "" : loaiDoiTuong;
        this.maDoiTuong = maDoiTuong == null ? "" : maDoiTuong;
        this.ghiChu = ghiChu == null ? "" : ghiChu;
    }

    public NhanVien nguoiDung() {
        return nguoiDung;
    }

    public LocalDateTime thoiGian() {
        return thoiGian;
    }

    public String thaoTac() {
        return thaoTac;
    }

    public String doiTuong() {
        return doiTuong;
    }

    public String kieu() {
        return kieu;
    }

    public String ketQua() {
        return ketQua;
    }

    public String loaiDoiTuong() {
        return loaiDoiTuong;
    }

    public String maDoiTuong() {
        return maDoiTuong;
    }

    public String ghiChu() {
        return ghiChu;
    }

    public String layVanBanThoiGian() {
        return thoiGian == null ? "" : thoiGian.format(DinhDang.NGAY_GIO_GIAY);
    }
}
