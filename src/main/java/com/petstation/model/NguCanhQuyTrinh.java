package com.petstation.model;

/** Bản ghi đang thao tác trong một cửa sổ; không dùng trạng thái toàn cục. */
public final class NguCanhQuyTrinh {
    private long lichHen;
    private long phieuSpa;
    private long hopDongHotel;
    private long phongHotel;
    private String maPhongHotel = "";

    public String layMaPhongHotel() {
        return maPhongHotel;
    }

    public void chonPhongHotel(long id, String ma) {
        phongHotel = id;
        maPhongHotel = ma;
    }

    public long layPhongHotel() {
        return phongHotel;
    }

    public void chonPhongHotel(long id) {
        phongHotel = id;
    }

    public long layLichHen() {
        return lichHen;
    }

    public long layPhieuSpa() {
        return phieuSpa;
    }

    public long layHopDongHotel() {
        return hopDongHotel;
    }

    public void chonLichHen(long id) {
        if (lichHen != id) phieuSpa = 0;
        lichHen = id;
    }

    public void chonSpa(long id) {
        if (phieuSpa != id) lichHen = 0;
        phieuSpa = id;
    }

    public void chonHotel(long id) {
        hopDongHotel = id;
    }

    public void ketThuc(String nguon) {
        if ("SPA".equals(nguon)) {
            lichHen = 0;
            phieuSpa = 0;
        }
        if ("HOTEL".equals(nguon)) {
            hopDongHotel = 0;
            phongHotel = 0;
            maPhongHotel = "";
        }
    }

    public void xoa() {
        lichHen = 0;
        phieuSpa = 0;
        hopDongHotel = 0;
        phongHotel = 0;
        maPhongHotel = "";
    }
}
