package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.gui.components.common.PriceTableEditor;

import org.junit.jupiter.api.Test;

class PriceTableEditorTest {
    private static final String TABLE =
            "Chó|< 5kg|150000;Chó|5 - 10kg|200000;Chó|> 10kg|280000;Mèo|Mọi cân nặng|180000";

    @Test
    void picksPriceByWeightRange() {
        assertThat(PriceTableEditor.priceFor(TABLE, "Chó", 3.2)).isEqualTo(150_000);
        assertThat(PriceTableEditor.priceFor(TABLE, "Chó", 7)).isEqualTo(200_000);
        assertThat(PriceTableEditor.priceFor(TABLE, "Chó", 29.5)).isEqualTo(280_000);
        assertThat(PriceTableEditor.priceFor(TABLE, "Mèo", 4)).isEqualTo(180_000);
        assertThat(PriceTableEditor.priceFor(TABLE, "Khác", 4)).isEqualTo(-1);
    }

    @Test
    void minPriceIsLowestRow() {
        assertThat(PriceTableEditor.minPrice(TABLE)).isEqualTo(150_000);
        assertThat(PriceTableEditor.minPrice("")).isEqualTo(-1);
    }
}
