package com.petstation.model;

import java.io.Serializable;
import java.util.*;
import java.util.Objects;

public class DongThuTien implements Serializable {
    private static final long serialVersionUID = 1L;

    private PhuongThuc phuongThuc;
    private long soTien;
    private long khachDua;
    private boolean daXacNhan;

    public DongThuTien() {}

    public DongThuTien(PhuongThuc phuongThuc, long soTien, long khachDua, boolean daXacNhan) {
        Objects.requireNonNull(phuongThuc);
        if (phuongThuc == PhuongThuc.KET_HOP
                || soTien < 0
                || khachDua < soTien
                || phuongThuc != PhuongThuc.TIEN_MAT && khachDua != soTien)
            throw new IllegalArgumentException(
                    "Khoản thu không hợp lệ; chỉ tiền mặt có tiền thừa.");
        this.phuongThuc = phuongThuc;
        this.soTien = soTien;
        this.khachDua = khachDua;
        this.daXacNhan = daXacNhan;
    }

    public PhuongThuc getPhuongThuc() { return this.phuongThuc; }
    public void setPhuongThuc(PhuongThuc phuongThuc) {
        Objects.requireNonNull(phuongThuc, "Phương thức thanh toán không được rỗng.");
        if (phuongThuc == PhuongThuc.KET_HOP) {
            throw new IllegalArgumentException("Không dùng trực tiếp phương thức kết hợp cho dòng thu đơn lẻ.");
        }
        this.phuongThuc = phuongThuc;
    }

    public long getSoTien() { return this.soTien; }
    public void setSoTien(long soTien) {
        if (soTien < 0) {
            throw new IllegalArgumentException("Số tiền thu không được âm.");
        }
        this.soTien = soTien;
    }

    public long getKhachDua() { return this.khachDua; }
    public void setKhachDua(long khachDua) {
        if (khachDua < 0) {
            throw new IllegalArgumentException("Tiền khách đưa không được âm.");
        }
        this.khachDua = khachDua;
    }

    public boolean isDaXacNhan() { return this.daXacNhan; }
    public void setDaXacNhan(boolean daXacNhan) { this.daXacNhan = daXacNhan; }

    public long tienThua() {
        return khachDua - soTien;
    }

    public static java.util.List<DongThuTien> kiemTra(java.util.List<DongThuTien> cacDong, long canThu) {
        if (cacDong == null || cacDong.isEmpty() || canThu < 0)
            throw new IllegalArgumentException("Thiếu khoản thu.");
        java.util.Set<PhuongThuc> used = java.util.EnumSet.noneOf(PhuongThuc.class);
        long sum = 0;
        for (var d : cacDong) {
            if (d.getPhuongThuc() == null || d.getPhuongThuc() == PhuongThuc.KET_HOP)
                throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
            if (d.getSoTien() < 0 || d.getKhachDua() < d.getSoTien()
                    || (d.getPhuongThuc() != PhuongThuc.TIEN_MAT && d.getKhachDua() != d.getSoTien()))
                throw new IllegalArgumentException("Khoản thu không hợp lệ; chỉ tiền mặt có tiền thừa.");
            if (!used.add(d.getPhuongThuc()))
                throw new IllegalArgumentException("Mỗi phương thức chỉ một dòng.");
            if (d.getPhuongThuc() != PhuongThuc.TIEN_MAT && !d.isDaXacNhan())
                throw new IllegalArgumentException("Cần xác nhận đã nhận tiền QR/thẻ.");
            sum = Math.addExact(sum, d.getSoTien());
        }
        if (sum != canThu) throw new IllegalArgumentException("Tổng phân bổ phải bằng số cần thu.");
        return java.util.List.copyOf(cacDong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DongThuTien that = (DongThuTien) o;
        return Objects.equals(phuongThuc, that.phuongThuc)
                && this.soTien == that.soTien
                && this.khachDua == that.khachDua
                && this.daXacNhan == that.daXacNhan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phuongThuc, soTien, khachDua, daXacNhan);
    }

    @Override
    public String toString() {
        return "DongThuTien{" + "phuongThuc=" + phuongThuc + ", " + "soTien=" + soTien + ", " + "khachDua=" + khachDua + ", " + "daXacNhan=" + daXacNhan + "}";
    }
}
