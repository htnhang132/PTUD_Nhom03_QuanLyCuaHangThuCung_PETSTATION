package com.petstation.model;

public final class NguCanhQuyTrinh {
    private long lichHen;
    private long phieuSpa;
    private long hopDongHotel;
    private long phongHotel;
    private String maPhongHotel = "";

    public long getLichHen() { return lichHen; }
    public void setLichHen(long lichHen) { this.lichHen = lichHen; }

    public long getPhieuSpa() { return phieuSpa; }
    public void setPhieuSpa(long phieuSpa) { this.phieuSpa = phieuSpa; }

    public long getHopDongHotel() { return hopDongHotel; }
    public void setHopDongHotel(long hopDongHotel) { this.hopDongHotel = hopDongHotel; }

    public long getPhongHotel() { return phongHotel; }
    public void setPhongHotel(long phongHotel) { this.phongHotel = phongHotel; }

    public String getMaPhongHotel() { return maPhongHotel; }
    public void setMaPhongHotel(String maPhongHotel) { this.maPhongHotel = maPhongHotel == null ? "" : maPhongHotel; }

    public void chonPhongHotel(long id, String ma) {
        phongHotel = id;
        maPhongHotel = ma;
    }

    public void chonPhongHotel(long id) {
        phongHotel = id;
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
