package com.petstation.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class SaleContextTest {
    @Test
    void subtotalUsesActualLinesAndQuantity() {
        SaleContext sale = new SaleContext();
        sale.setLines(
                List.of(
                        new SaleContext.Line("P1", "Product", 285000, 2),
                        new SaleContext.Line("P2", "Product 2", 89000, 1)));
        assertThat(sale.subtotal()).isEqualTo(659000);
        assertThatThrownBy(() -> sale.lines().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void clearResetsCustomerAndServiceButRefreshesPaymentIdentity() {
        SaleContext sale = new SaleContext();
        String key = sale.paymentKey();
        sale.setCustomer("KH1", "Customer", 12);
        sale.setSource("SPA", 20);
        sale.setLines(List.of(new SaleContext.Line("DV1", "Service", 150000, 1)));
        sale.clear();
        assertThat(sale.isEmpty()).isTrue();
        assertThat(sale.customerCode()).isEmpty();
        assertThat(sale.sourceType()).isEmpty();
        assertThat(sale.paymentKey()).isNotEqualTo(key);
    }

    @Test
    void discountPercentIsBounded() {
        SaleContext sale = new SaleContext();
        sale.setDiscountPercent(200);
        assertThat(sale.discountPercent()).isEqualTo(100);
        sale.setDiscountPercent(-20);
        assertThat(sale.discountPercent()).isZero();
    }

    @Test
    void linesRejectNegativeAmountsAndQuantities() {
        assertThatThrownBy(() -> new SaleContext.Line("P", "P", -1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SaleContext.Line("P", "P", 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
