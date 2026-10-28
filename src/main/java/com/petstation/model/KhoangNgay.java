package com.petstation.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Khoảng báo cáo bao gồm cả ngày bắt đầu và ngày kết thúc. */
public record KhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
    public KhoangNgay {
        if (tuNgay == null) throw new IllegalArgumentException("Chưa chọn ngày bắt đầu.");
        if (denNgay == null) throw new IllegalArgumentException("Chưa chọn ngày kết thúc.");
        if (tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }
    }

    public static KhoangNgay ganDay(int soNgay) {
        LocalDate homNay = LocalDate.now();
        return new KhoangNgay(homNay.minusDays(Math.max(1, soNgay) - 1L), homNay);
    }

    /** Cận trên loại trừ dùng cho cột SQL có thành phần giờ. */
    public LocalDate layCanTren() {
        return denNgay.plusDays(1);
    }

    public long demSoNgay() {
        return ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
    }
}
