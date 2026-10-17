package com.petstation.gui.navigation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SaleContextTest {
    private SaleContext sampleSale() {
        SaleContext sale = new SaleContext();
        sale.setLines(List.of(
            new SaleContext.Line("SP001", "Royal Canin", 285_000, 2),
            new SaleContext.Line("SP004", "Sữa tắm", 89_000, 1)));
        return sale;
    }

    @Test
    void totalsAreConsistent() {
        SaleContext sale = sampleSale();
        // Khuyến mãi tự áp: KM001 giảm 10% sản phẩm là chương trình có mức giảm lớn nhất
        assertThat(sale.subtotal()).isEqualTo(659_000);
        assertThat(sale.bestPromotion().code()).isEqualTo("KM001");
        assertThat(sale.discount()).isEqualTo(65_900);
        assertThat(sale.total()).isEqualTo(593_100);
    }

    @Test
    void voucherAndInvalidCode() {
        SaleContext sale = sampleSale();
        assertThat(sale.applyVoucher("vc-welcome50")).isNull();
        assertThat(sale.voucherAmount()).isEqualTo(50_000);
        assertThat(sale.applyVoucher("SAI")).isNotNull();
        // voucher đã hết hạn / hết lượt không dùng được
        assertThat(sale.applyVoucher("VC-OLD")).contains("không dùng được");
    }

    @Test
    void pointsAreCappedByCustomerPointsAndTotal() {
        SaleContext sale = sampleSale();
        sale.setCustomer("KH001", "Nguyễn Văn An", 100);
        sale.setPointsUsed(500);
        assertThat(sale.pointsUsed()).isEqualTo(100);
        assertThat(sale.pointsValue()).isEqualTo(100 * AppSettings.vndPerRedeemedPoint());
        assertThat(sale.total()).isEqualTo(sale.subtotal() - sale.discount());
    }

    @Test
    void completingCashSaleComputesChange() {
        SaleContext sale = sampleSale();
        SaleContext.Receipt receipt = sale.complete(ShiftState.Method.CASH, 700_000, "Thu ngân");
        assertThat(receipt.total()).isEqualTo(593_100);
        assertThat(receipt.change()).isEqualTo(106_900);
        assertThat(receipt.invoiceNo()).startsWith("HD-");
    }

    @Test
    void shiftTracksSalesOnlyWhenOpen() {
        ShiftState shift = new ShiftState();
        shift.recordSale(ShiftState.Method.CASH, 100_000);
        assertThat(shift.invoiceCount()).isZero();
        shift.open("Ca sáng", 2_000_000);
        shift.recordSale(ShiftState.Method.CASH, 100_000);
        shift.recordSale(ShiftState.Method.TRANSFER, 50_000);
        assertThat(shift.expectedCash()).isEqualTo(2_100_000);
        assertThat(shift.invoiceCount()).isEqualTo(2);
    }
}
