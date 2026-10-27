package com.petstation.model;

import java.util.*;

/** Số tiền phân bổ là tiền thu ròng; tiền thừa chỉ thuộc dòng tiền mặt. */
public record DongThuTien(PhuongThuc phuongThuc, long soTien, long khachDua, boolean daXacNhan) {
    public DongThuTien {
        Objects.requireNonNull(phuongThuc);
        if (phuongThuc == PhuongThuc.MIXED
                || soTien < 0
                || khachDua < soTien
                || phuongThuc != PhuongThuc.CASH && khachDua != soTien)
            throw new IllegalArgumentException(
                    "Khoản thu không hợp lệ; chỉ tiền mặt có tiền thừa.");
    }

    public long tienThua() {
        return khachDua - soTien;
    }

    public static List<DongThuTien> kiemTra(List<DongThuTien> cacDong, long canThu) {
        if (cacDong == null || cacDong.isEmpty() || canThu < 0)
            throw new IllegalArgumentException("Thiếu khoản thu.");
        Set<PhuongThuc> used = EnumSet.noneOf(PhuongThuc.class);
        long sum = 0;
        for (var d : cacDong) {
            if (!used.add(d.phuongThuc()))
                throw new IllegalArgumentException("Mỗi phương thức chỉ một dòng.");
            if (d.phuongThuc() != PhuongThuc.CASH && !d.daXacNhan())
                throw new IllegalArgumentException("Cần xác nhận đã nhận tiền QR/thẻ.");
            sum = Math.addExact(sum, d.soTien());
        }
        if (sum != canThu) throw new IllegalArgumentException("Tổng phân bổ phải bằng số cần thu.");
        return List.copyOf(cacDong);
    }
}
