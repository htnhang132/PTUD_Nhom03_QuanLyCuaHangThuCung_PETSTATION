package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.model.NhanVien;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;
import com.petstation.model.TrangThaiTaiKhoan;
import com.petstation.model.VaiTro;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho phân quyền dịch vụ kiểm thử. */
class PhanQuyenServiceTest {
    /** Lấy từ chối thiếu quyền trong PhanQuyenServiceTest. */
    @Test
    void layTuChoiThieuQuyen() {
        // Các phiên.
        NguCanhPhienService cacPhien = new NguCanhPhienService();
        cacPhien.batDau(
                new PhienDangNhap(
                        new TaiKhoan(
                                1,
                                1,
                                "sales",
                                "hash",
                                VaiTro.SALES_STAFF,
                                TrangThaiTaiKhoan.ACTIVE,
                                0,
                                null,
                                false),
                        new NhanVien(
                                1,
                                "NV001",
                                "Nguyễn Văn An",
                                null,
                                "0900000000",
                                null,
                                "Nhân viên bán hàng",
                                LocalDate.now(),
                                true),
                        Set.of(Quyen.POS_SELL)));

        // Dịch vụ.
        PhanQuyenService dichVu = new PhanQuyenService(cacPhien);

        assertThatThrownBy(() -> dichVu.yeuCau(Quyen.ACCOUNT_MANAGE))
                .isInstanceOf(PhanQuyenException.class);
    }
}
