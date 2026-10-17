package com.petstation.gui.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DemoStoreTest {
    @Test
    void lockingAccountsOfDepartedStaffOnlyAffectsThatStaff() {
        int locked = DemoStore.lockAccountsOf("NV005");
        assertThat(locked).isZero(); // NV005 chưa có tài khoản

        locked = DemoStore.lockAccountsOf("NV004");
        assertThat(locked).isEqualTo(1);
        assertThat(DemoStore.accounts().stream().filter(a -> a[0].equals("hoa.tt")).findFirst().orElseThrow()[3]).isEqualTo("Đã khóa");
        assertThat(DemoStore.accounts().stream().filter(a -> a[0].equals("admin")).findFirst().orElseThrow()[3]).isEqualTo("Hoạt động");
    }

    @Test
    void customerOptionsExcludeInactiveCustomers() {
        Object[] kh = DemoStore.customers().get(0);
        String before = String.valueOf(kh[8]);
        kh[8] = "Ngừng hoạt động";
        try {
            assertThat(DemoStore.customerOptions()).noneMatch(o -> o.startsWith(kh[0] + " - "));
        } finally {
            kh[8] = before;
        }
    }

    @Test
    void priceChangesAreRecordedInHistory() {
        int before = DemoStore.priceHistory().size();
        DemoStore.recordPriceChange("SP001", "Royal Canin Adult 2kg", "285000", "290000", "Test");
        assertThat(DemoStore.priceHistory()).hasSize(before + 1);
    }
}
