package com.petstation.dto;

public final class DongNghiepVu {
    private final String maKhoa;
    private final Object[] duLieu;

    public DongNghiepVu(Object[] duLieu) {
        this(null, duLieu);
    }

    public DongNghiepVu(String maKhoa, Object[] duLieu) {
        this.maKhoa = maKhoa;
        this.duLieu = duLieu == null ? new Object[0] : duLieu.clone();
    }

    public String layMa() {
        return maKhoa;
    }

    public Object[] thanhHang() {
        return duLieu.clone();
    }

    public Object[] thanhMang() {
        return thanhHang();
    }

    public Object[] getDuLieu() {
        return thanhHang();
    }

    public void capNhatChuoi(int cot, String giaTri) {
        if (cot >= 0 && cot < duLieu.length) {
            duLieu[cot] = giaTri;
        }
    }

    public String layChuoi(int cot) {
        if (cot < 0 || cot >= duLieu.length || duLieu[cot] == null) return "";
        return String.valueOf(duLieu[cot]);
    }

    public long laySo(int cot) {
        if (cot < 0 || cot >= duLieu.length || !(duLieu[cot] instanceof Number n)) return 0L;
        return n.longValue();
    }
}
