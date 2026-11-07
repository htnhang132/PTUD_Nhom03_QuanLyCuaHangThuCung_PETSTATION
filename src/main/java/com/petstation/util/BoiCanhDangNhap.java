package com.petstation.util;

import com.petstation.model.PhienDangNhap;

public final class BoiCanhDangNhap {
    private BoiCanhDangNhap() {}

    private static volatile PhienDangNhap phienHienTai;
    private static final ThreadLocal<PhienDangNhap> phienCongViec = new ThreadLocal<>();

    public static void datPhien(PhienDangNhap phien) {
        phienHienTai = phien;
    }

    public static PhienDangNhap layPhienHienTai() {
        return phienHienTai;
    }

    public static ThreadLocal<PhienDangNhap> phienCongViec() {
        return phienCongViec;
    }

    public static String layTenDangNhap() {
        PhienDangNhap p = phienCongViec.get();
        if (p == null) {
            p = phienHienTai;
        }
        if (p != null && p.taiKhoan() != null) {
            return p.taiKhoan().tenDangNhap();
        }
        return "";
    }
}

