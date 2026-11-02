package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/** Khoảng báo cáo bao gồm cả ngày bắt đầu và ngày kết thúc. */
public class KhoangNgay implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate tuNgay;
    private LocalDate denNgay;

    public KhoangNgay() {}

    public KhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null) throw new IllegalArgumentException("Chưa chọn ngày bắt đầu.");
        if (denNgay == null) throw new IllegalArgumentException("Chưa chọn ngày kết thúc.");
        if (tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
    }

    public LocalDate getTuNgay() { return this.tuNgay; }
    public LocalDate layTuNgay() { return this.tuNgay; }
    public LocalDate tuNgay() { return this.tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public void datTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }

    public LocalDate getDenNgay() { return this.denNgay; }
    public LocalDate layDenNgay() { return this.denNgay; }
    public LocalDate denNgay() { return this.denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public void datDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }

    public static KhoangNgay ganDay(int soNgay) {
        LocalDate homNay = LocalDate.now();
        return new KhoangNgay(homNay.minusDays(Math.max(1, soNgay) - 1L), homNay);
    }
    
    /** Cận trên loại trừ dùng cho cột SQL có thành phần giờ. */
    public LocalDate layCanTren() {
        return denNgay.plusDays(1);
    }
    
    public long demSoNgay() {
        return java.time.temporal.ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhoangNgay that = (KhoangNgay) o;
        return Objects.equals(tuNgay, that.tuNgay)
                && Objects.equals(denNgay, that.denNgay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuNgay, denNgay);
    }

    @Override
    public String toString() {
        return "KhoangNgay{" + "tuNgay=" + tuNgay + ", " + "denNgay=" + denNgay + "}";
    }
}
