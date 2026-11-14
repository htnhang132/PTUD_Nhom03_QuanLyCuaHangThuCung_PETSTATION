package com.petstation.dto;

import java.io.Serializable;
import java.util.Objects;

public final class DonDoiTreo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String timHoaDon;
    private final String dongGoc;
    private final String maThayThe;
    private final String timHang;
    private final int soLuong;
    private final String lyDo;
    private final boolean nguyenTem;
    private final boolean chuaDung;

    public DonDoiTreo(
            String timHoaDon,
            String dongGoc,
            String maThayThe,
            String timHang,
            int soLuong,
            String lyDo,
            boolean nguyenTem,
            boolean chuaDung) {
        this.timHoaDon = timHoaDon == null ? "" : timHoaDon.trim();
        this.dongGoc = dongGoc == null ? "" : dongGoc.trim();
        this.maThayThe = maThayThe == null ? "" : maThayThe.trim();
        this.timHang = timHang == null ? "" : timHang.trim();
        this.soLuong = soLuong;
        this.lyDo = lyDo == null ? "" : lyDo.trim();
        this.nguyenTem = nguyenTem;
        this.chuaDung = chuaDung;
        if (this.timHoaDon.isBlank())
            throw new IllegalArgumentException("Nhập mã hóa đơn hoặc SĐT trước khi treo đơn đổi.");
        if (soLuong <= 0) throw new IllegalArgumentException("Thông tin đơn đổi không hợp lệ.");
    }

    public String timHoaDon() {
        return timHoaDon;
    }

    public String dongGoc() {
        return dongGoc;
    }

    public String maThayThe() {
        return maThayThe;
    }

    public String timHang() {
        return timHang;
    }

    public int soLuong() {
        return soLuong;
    }

    public String lyDo() {
        return lyDo;
    }

    public boolean nguyenTem() {
        return nguyenTem;
    }

    public boolean chuaDung() {
        return chuaDung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DonDoiTreo that)) return false;
        return soLuong == that.soLuong
                && nguyenTem == that.nguyenTem
                && chuaDung == that.chuaDung
                && Objects.equals(timHoaDon, that.timHoaDon)
                && Objects.equals(dongGoc, that.dongGoc)
                && Objects.equals(maThayThe, that.maThayThe)
                && Objects.equals(timHang, that.timHang)
                && Objects.equals(lyDo, that.lyDo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                timHoaDon, dongGoc, maThayThe, timHang, soLuong, lyDo, nguyenTem, chuaDung);
    }
}
