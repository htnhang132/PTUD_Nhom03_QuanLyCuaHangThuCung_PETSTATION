package com.petstation.service;

import com.petstation.model.PhienDangNhap;

import java.util.Optional;

/** Lưu và kiểm tra phiên đăng nhập đang hoạt động trong ứng dụng. */
public final class NguCanhPhienService {
    /** Phiên đăng nhập. */
    private PhienDangNhap phienDangNhap;

    /**
     * Bắt đầu phiên hoặc bước xử lý trong NguCanhPhienService.
     *
     * @param phienDangNhap phiên đăng nhập
     */
    public synchronized void batDau(PhienDangNhap phienDangNhap) {
        this.phienDangNhap = phienDangNhap;
    }

    /**
     * Lấy hiện tại trong NguCanhPhienService.
     *
     * @return kết quả lấy hiện tại
     */
    public synchronized Optional<PhienDangNhap> layHienTai() {
        return Optional.ofNullable(phienDangNhap);
    }

    /**
     * Yêu cầu hiện tại trong NguCanhPhienService.
     *
     * @return kết quả yêu cầu hiện tại
     */
    public synchronized PhienDangNhap yeuCauHienTai() {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (phienDangNhap == null) {
            throw new PhanQuyenException("Phiên đăng nhập đã hết hạn.");
        }
        return phienDangNhap;
    }

    /** Xóa trong NguCanhPhienService. */
    public synchronized void xoa() {
        phienDangNhap = null;
    }
}
