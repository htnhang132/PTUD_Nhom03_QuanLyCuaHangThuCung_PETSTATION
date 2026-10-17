package com.petstation.gui.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AppSettingsTest {
    @Test
    void updateStoresBusinessRules() {
        int vat = AppSettings.vatPercent();
        int low = AppSettings.lowStockThreshold();
        int days = AppSettings.expiryWarnDays();
        long fee = AppSettings.lateCheckoutFeePerDay();
        int ret = AppSettings.returnWindowDays();
        long limit = AppSettings.writeOffApprovalLimit();
        try {
            AppSettings.update(10, 5, 45, 70_000, 14, 1_000_000);
            assertThat(AppSettings.vatPercent()).isEqualTo(10);
            assertThat(AppSettings.expiryWarnDays()).isEqualTo(45);
            assertThat(AppSettings.lateCheckoutFeePerDay()).isEqualTo(70_000);
            assertThat(AppSettings.writeOffApprovalLimit()).isEqualTo(1_000_000);
        } finally {
            AppSettings.update(vat, low, days, fee, ret, limit);
        }
    }
}
