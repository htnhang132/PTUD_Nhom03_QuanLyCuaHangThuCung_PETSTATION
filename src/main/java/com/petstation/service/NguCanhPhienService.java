package com.petstation.service;

import com.petstation.model.PhienDangNhap;

import java.util.Optional;

public final class NguCanhPhienService {
    private PhienDangNhap phienDangNhap;

    public synchronized void batDau(PhienDangNhap phienDangNhap) {
        this.phienDangNhap = phienDangNhap;
    }

    public synchronized Optional<PhienDangNhap> layHienTai() {
        return Optional.ofNullable(phienDangNhap);
    }

    public synchronized PhienDangNhap yeuCauHienTai() {
        if (phienDangNhap == null) {
            throw new PhanQuyenException("Phiên đăng nhập đã hết hạn.");
        }
        return phienDangNhap;
    }

    public synchronized void xoa() {
        phienDangNhap = null;
    }
}
