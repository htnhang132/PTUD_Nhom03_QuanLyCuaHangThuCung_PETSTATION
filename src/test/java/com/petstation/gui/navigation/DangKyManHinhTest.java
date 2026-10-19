package com.petstation.gui.navigation;

import static com.petstation.service.PhienDangNhapService.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.model.*;
import com.petstation.model.NhanVien;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;
import com.petstation.model.TrangThaiTaiKhoan;
import com.petstation.model.VaiTro;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

/** Cung cấp giao diện và xử lý tương tác cho màn hình bộ đăng ký kiểm thử. */
class DangKyManHinhTest {
    /** Lấy quản lý có thể truy cập được bảo vệ các màn hình trong DangKyManHinhTest. */
    @Test
    void layQuanLyCoTheTruyCapDuocBaoVeCacManHinh() {
        // Bộ đăng ký.
        DangKyManHinh boDangKy =
                new DangKyManHinh(
                        layPhien(VaiTro.STORE_MANAGER, EnumSet.allOf(Quyen.class)), boQua -> {});

        assertThat(boDangKy.coTheTruyCap(MaManHinh.STAFF)).isTrue();
        assertThat(boDangKy.coTheTruyCap(MaManHinh.AUDIT_LOG)).isTrue();
        assertThat(boDangKy.layDinhNghia(MaManHinh.DASHBOARD).tieuDe()).isEqualTo("Màn hình chính");
    }

    /** Lấy bán hàng nhân viên không thể truy cập tài khoản quản lý trong DangKyManHinhTest. */
    @Test
    void layBanHangNhanVienKhongTheTruyCapTaiKhoanQuanLy() {
        // Bộ đăng ký.
        DangKyManHinh boDangKy =
                new DangKyManHinh(
                        layPhien(VaiTro.SALES_STAFF, Set.of(Quyen.DASHBOARD_VIEW, Quyen.POS_SELL)),
                        boQua -> {});

        assertThat(boDangKy.coTheTruyCap(MaManHinh.POS)).isTrue();
        assertThat(boDangKy.coTheTruyCap(MaManHinh.STAFF)).isFalse();
    }

    /**
     * Lấy phiên trong DangKyManHinhTest.
     *
     * @param vaiTro vai trò
     * @param cacQuyen các quyền
     * @return kết quả lấy phiên
     */
    private PhienDangNhap layPhien(VaiTro vaiTro, Set<Quyen> cacQuyen) {
        return new PhienDangNhap(
                new TaiKhoan(
                        1, 1, "user", "hash", vaiTro, TrangThaiTaiKhoan.ACTIVE, 0, null, false),
                new NhanVien(
                        1,
                        "NV001",
                        "Nguyễn Gia Vĩ",
                        null,
                        "0900000000",
                        null,
                        vaiTro.layTenHienThi(),
                        LocalDate.now(),
                        true),
                cacQuyen);
    }
}
