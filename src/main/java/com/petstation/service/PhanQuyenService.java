package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.model.Quyen;

public final class PhanQuyenService {
    private final NguCanhPhienService nguCanhPhien;

    public PhanQuyenService(NguCanhPhienService nguCanhPhien) {
        this.nguCanhPhien = nguCanhPhien;
    }

    public void yeuCau(Quyen quyen) {
        if (!nguCanhPhien.yeuCauHienTai().kiemTraCo(quyen)) {
            throw new PhanQuyenException(
                    "Bạn không có quyền sử dụng chức năng này. Liên hệ Cửa hàng trưởng để được cấp"
                            + " quyền.");
        }
    }
}
