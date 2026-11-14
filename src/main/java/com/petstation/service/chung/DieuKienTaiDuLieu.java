package com.petstation.service.chung;

import com.petstation.database.QuanLyGiaoDich;

public final class DieuKienTaiDuLieu {
    private DieuKienTaiDuLieu() {}

    public static boolean coTheTachLuong() {
        return !QuanLyGiaoDich.dangTrongGiaoDich();
    }
}
