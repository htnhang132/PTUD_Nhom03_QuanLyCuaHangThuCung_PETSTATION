package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.model.Quyen;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho phân quyền dịch vụ. */
public final class PhanQuyenService {
    /** Ngữ cảnh phiên. */
    private final NguCanhPhienService nguCanhPhien;

    /**
     * Khởi tạo PhanQuyenService với các giá trị được cung cấp.
     *
     * @param nguCanhPhien ngữ cảnh phiên
     */
    public PhanQuyenService(NguCanhPhienService nguCanhPhien) {
        this.nguCanhPhien = nguCanhPhien;
    }

    /**
     * Yêu cầu trong PhanQuyenService.
     *
     * @param quyen quyền
     */
    public void yeuCau(Quyen quyen) {
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (!nguCanhPhien.yeuCauHienTai().kiemTraCo(quyen)) {
            throw new PhanQuyenException(
                    "Bạn không có quyền sử dụng chức năng này. Liên hệ Cửa hàng trưởng để được cấp"
                            + " quyền.");
        }
    }
}
