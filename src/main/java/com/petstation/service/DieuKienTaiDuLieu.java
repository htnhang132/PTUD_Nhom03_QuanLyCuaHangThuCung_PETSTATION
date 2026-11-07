package com.petstation.service;

import com.petstation.database.ThaoTacSQL;

public final class DieuKienTaiDuLieu {
    private DieuKienTaiDuLieu() {}

    public static boolean coTheTachLuong() {
        return !ThaoTacSQL.dangTrongGiaoDich();
    }
}
