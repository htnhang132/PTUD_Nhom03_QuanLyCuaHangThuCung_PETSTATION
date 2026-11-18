package com.petstation.gui.chung;

/** Mục chọn bộ lọc trạng thái kèm bộ đếm số lượng bản ghi hiện tại. */
public final class MucLocTrangThai {
    private final String ma;
    private final String ten;
    private int soLuong = -1;

    public MucLocTrangThai(String ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }

    public String layMa() {
        return ma;
    }

    public void datSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return soLuong >= 0 ? ten + " (" + soLuong + ")" : ten;
    }
}
