package com.petstation.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {
    @Test
    void exposesVietnameseRoleNames() {
        assertThat(Role.STORE_MANAGER.displayName()).isEqualTo("Cửa hàng trưởng");
        assertThat(Role.SALES_STAFF.displayName()).isEqualTo("Nhân viên bán hàng");
    }
}
