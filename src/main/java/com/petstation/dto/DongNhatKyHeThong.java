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

    public DongNhatKyHeThong(
            NhanVien nguoiDung,
            LocalDateTime thoiGian,
            String thaoTac,
            String doiTuong,
            String kieu,
            String ketQua) {
        this.nguoiDung = nguoiDung;
        this.thoiGian = thoiGian;
        this.thaoTac = thaoTac;
        this.doiTuong = doiTuong;
        this.kieu = kieu;
        this.ketQua = ketQua;
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

    public String layVanBanThoiGian() {
        return thoiGian.format(DinhDang.NGAY_THANG_GIO_GIAY);
    }
}
