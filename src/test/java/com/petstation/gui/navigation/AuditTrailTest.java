package com.petstation.gui.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AuditTrailTest {
    @Test
    void logsWithCurrentUserAndDerivesType() {
        AuditTrail.setUser("Phạm Thu Lan");
        int before = AuditTrail.entries().size();
        AuditTrail.log("Thêm", "khách hàng KH100 - Test");
        AuditTrail.log("Duyệt", "yêu cầu YC001");
        var entries = AuditTrail.entries();
        assertThat(entries).hasSize(before + 2);
        var last = entries.get(entries.size() - 1);
        assertThat(last.user()).isEqualTo("Phạm Thu Lan");
        assertThat(last.type()).isEqualTo("APPROVAL");
        assertThat(entries.get(entries.size() - 2).type()).isEqualTo("CREATE");
    }
}
