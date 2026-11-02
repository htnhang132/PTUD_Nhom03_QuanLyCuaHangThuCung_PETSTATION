package com.petstation.model;

import java.io.Serializable;
import java.util.*;
import java.util.Objects;

/** Số tiền phân bổ là tiền thu ròng; tiền thừa chỉ thuộc dòng tiền mặt. */
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
    public PhuongThuc layPhuongThuc() { return this.phuongThuc; }
    public PhuongThuc phuongThuc() { return this.phuongThuc; }
    public void setPhuongThuc(PhuongThuc phuongThuc) { this.phuongThuc = phuongThuc; }
    public void datPhuongThuc(PhuongThuc phuongThuc) { this.phuongThuc = phuongThuc; }

    public long getSoTien() { return this.soTien; }
    public long laySoTien() { return this.soTien; }
    public long soTien() { return this.soTien; }
    public void setSoTien(long soTien) { this.soTien = soTien; }
    public void datSoTien(long soTien) { this.soTien = soTien; }

    public long getKhachDua() { return this.khachDua; }
    public long layKhachDua() { return this.khachDua; }
    public long khachDua() { return this.khachDua; }
    public void setKhachDua(long khachDua) { this.khachDua = khachDua; }
    public void datKhachDua(long khachDua) { this.khachDua = khachDua; }

    public boolean isDaXacNhan() { return this.daXacNhan; }
    public boolean layDaXacNhan() { return this.daXacNhan; }
    public boolean daXacNhan() { return this.daXacNhan; }
    public void setDaXacNhan(boolean daXacNhan) { this.daXacNhan = daXacNhan; }
    public void datDaXacNhan(boolean daXacNhan) { this.daXacNhan = daXacNhan; }

    public long tienThua() {
        return khachDua - soTien;
    }
    
    public static java.util.List<DongThuTien> kiemTra(java.util.List<DongThuTien> cacDong, long canThu) {
        if (cacDong == null || cacDong.isEmpty() || canThu < 0)
            throw new IllegalArgumentException("Thiếu khoản thu.");
        java.util.Set<PhuongThuc> used = java.util.EnumSet.noneOf(PhuongThuc.class);
        long sum = 0;
        for (var d : cacDong) {
            if (!used.add(d.phuongThuc()))
                throw new IllegalArgumentException("Mỗi phương thức chỉ một dòng.");
            if (d.phuongThuc() != PhuongThuc.TIEN_MAT && !d.daXacNhan())
                throw new IllegalArgumentException("Cần xác nhận đã nhận tiền QR/thẻ.");
            sum = Math.addExact(sum, d.soTien());
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
