package com.petstation.service;

import com.petstation.model.PhienDangNhap;

import java.util.Optional;

/** Lưu và kiểm tra phiên đăng nhập đang hoạt động trong ứng dụng. */
public final class NguCanhPhienService {
    /** Phiên đăng nhập. */
    private PhienDangNhap phienDangNhap;

    public synchronized void batDau(PhienDangNhap phienDangNhap) {
        this.phienDangNhap = phienDangNhap;
    }

    public synchronized Optional<PhienDangNhap> layHienTai() {
        return Optional.ofNullable(phienDangNhap);
    }

    public synchronized PhienDangNhap yeuCauHienTai() {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (phienDangNhap == null) {
            throw new PhanQuyenException("Phiên đăng nhập đã hết hạn.");
        }
        return phienDangNhap;
    }

    public synchronized void xoa() {
        phienDangNhap = null;
    }
}
