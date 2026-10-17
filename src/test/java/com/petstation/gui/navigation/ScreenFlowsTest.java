package com.petstation.gui.navigation;

import com.petstation.gui.components.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScreenFlowsTest {
    @Test
    void salesFlowHasThreeOrderedSteps() {
        assertThat(ScreenFlows.find(ScreenId.PAYMENT)).isPresent();
        assertThat(ScreenFlows.find(ScreenId.PAYMENT).get().indexOf(ScreenId.PAYMENT)).isEqualTo(1);
        assertThat(ScreenFlows.find(ScreenId.REPORTS)).isEmpty();
    }

    @Test
    void moneyParsesAndFormats() {
        assertThat(Money.parse("1.250.000đ")).isEqualTo(1_250_000);
        assertThat(Money.parse("abc")).isEqualTo(-1);
        assertThat(Money.format(285_000)).endsWith("₫").contains("285");
    }

    @Test
    void spaAndHotelFlowsAreLinearAndEndAtPayment() {
        var spa = ScreenFlows.find(ScreenId.SPA_INTAKE).orElseThrow();
        assertThat(spa.linear()).isTrue();
        assertThat(spa.previous(ScreenId.SPA_INTAKE)).contains(ScreenId.SPA_SCHEDULE);
        assertThat(spa.next(ScreenId.SPA_INTAKE)).contains(ScreenId.SPA_SERVICE_SELECTION);
        assertThat(spa.isLast(ScreenId.SPA_CLOSE)).isTrue();
        assertThat(ScreenFlows.find(ScreenId.CHECKOUT).orElseThrow().isLast(ScreenId.CHECKOUT)).isTrue();
        assertThat(ScreenFlows.find(ScreenId.POS).orElseThrow().linear()).isFalse();
    }
}
