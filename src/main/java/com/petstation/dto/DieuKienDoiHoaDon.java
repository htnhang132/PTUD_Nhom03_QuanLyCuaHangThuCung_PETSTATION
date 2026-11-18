package com.petstation.dto;

public final class DieuKienDoiHoaDon {
    private final boolean hopLe;
    private final String lyDoKhongHopLe;
    private final int soNgayHanDoi;
    private final long tongConDuocDoi;

    public DieuKienDoiHoaDon(
            boolean hopLe, String lyDoKhongHopLe, int soNgayHanDoi, long tongConDuocDoi) {
        this.hopLe = hopLe;
        this.lyDoKhongHopLe = lyDoKhongHopLe == null ? "" : lyDoKhongHopLe;
        this.soNgayHanDoi = soNgayHanDoi;
        this.tongConDuocDoi = tongConDuocDoi;
    }

    public boolean isHopLe() {
        return hopLe;
    }

    public String getLyDoKhongHopLe() {
        return lyDoKhongHopLe;
    }

    public int getSoNgayHanDoi() {
        return soNgayHanDoi;
    }

    public long getTongConDuocDoi() {
        return tongConDuocDoi;
    }
}
