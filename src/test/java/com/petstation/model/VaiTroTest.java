package com.petstation.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Biểu diễn dữ liệu vai trò kiểm thử dùng trong nghiệp vụ cửa hàng. */
class VaiTroTest {
    @Test
    void layCungCapTiengVietTenCacVaiTro() {
        assertThat(VaiTro.STORE_MANAGER.layTenHienThi()).isEqualTo("Cửa hàng trưởng");
        assertThat(VaiTro.SALES_STAFF.layTenHienThi()).isEqualTo("Nhân viên bán hàng");
    }
}
