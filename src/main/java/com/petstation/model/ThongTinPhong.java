package com.petstation.model;

import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu sơ đồ phòng và lịch đang sử dụng, được nạp theo ID. */
public record ThongTinPhong(
        long id,
        String ma,
        String loai,
        long gia,
        String trangThai,
        int sucChua,
        LichPhong dangO,
        List<LichPhong> datTruoc) {
    public record LichPhong(
            long id,
            String thuCung,
            String khachHang,
            LocalDateTime ngayVao,
            LocalDateTime ngayRa) {}

    public String layNhanTrangThai() {
        return switch (trangThai) {
            case "AVAILABLE" -> "Trống";
            case "OCCUPIED" -> "Có thú";
            case "CLEANING" -> "Đang dọn";
            case "MAINTENANCE" -> "Bảo trì";
            default -> "Ngừng hoạt động";
        };
    }
}
