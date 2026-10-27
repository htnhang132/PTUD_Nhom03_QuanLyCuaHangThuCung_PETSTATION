package com.petstation.service;

import com.petstation.database.ThaoTacSQL;

/** Quyết định có được tách công việc đọc sang luồng khác hay không. */
public final class DieuKienTaiDuLieu {
    private DieuKienTaiDuLieu() {}

    /** Transaction JDBC gắn với luồng hiện tại; chuyển luồng sẽ mất kết nối và khóa đang giữ. */
    public static boolean coTheTachLuong() {
        return !ThaoTacSQL.dangTrongGiaoDich();
    }
}
