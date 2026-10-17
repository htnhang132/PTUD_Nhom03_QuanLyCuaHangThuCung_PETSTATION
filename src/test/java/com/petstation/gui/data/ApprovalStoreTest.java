package com.petstation.gui.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class ApprovalStoreTest {
    @Test
    void approvingRunsActionOnceAndRejectingDoesNot() {
        AtomicBoolean ran = new AtomicBoolean();
        var approved = ApprovalStore.submit(ApprovalStore.Kind.WRITE_OFF, "Test", "Hết hạn", "Tồn 10", "Hủy 10", () -> ran.set(true));
        assertThat(approved.status).isEqualTo("Chờ duyệt");
        ApprovalStore.approve(approved, "Cửa hàng trưởng");
        assertThat(ran).isTrue();
        assertThat(approved.status).isEqualTo("Đã duyệt");

        AtomicBoolean ran2 = new AtomicBoolean();
        var rejected = ApprovalStore.submit(ApprovalStore.Kind.STOCKTAKE, "Test", "Lệch", "10", "8", () -> ran2.set(true));
        ApprovalStore.reject(rejected, "Cửa hàng trưởng", "Không hợp lý");
        ApprovalStore.approve(rejected, "Cửa hàng trưởng");
        assertThat(ran2).isFalse();
        assertThat(rejected.status).isEqualTo("Từ chối");
    }
}
