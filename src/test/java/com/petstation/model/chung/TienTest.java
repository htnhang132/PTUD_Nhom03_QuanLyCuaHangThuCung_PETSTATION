package com.petstation.model.chung;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.petstation.util.DinhDang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TienTest {

    @Test
    @DisplayName("Định dạng tiền tệ theo chuẩn VND")
    void testDinhDang() {
        assertEquals("0₫", DinhDang.dinhDang(0L));
        assertEquals("100.000₫", DinhDang.dinhDang(100_000L));
        assertEquals("1.250.000₫", DinhDang.dinhDang(1_250_000L));
        assertEquals("-50.000₫", DinhDang.dinhDang(-50_000L));
    }

    @Test
    @DisplayName("Đọc chuỗi số tiền hợp lệ và không hợp lệ")
    void testDoc() {
        assertEquals(100_000L, DinhDang.doc("100.000₫"));
        assertEquals(1_250_000L, DinhDang.doc("1,250,000 đ"));
        assertEquals(-1L, DinhDang.doc(null));
        assertEquals(-1L, DinhDang.doc(""));
        assertEquals(-1L, DinhDang.doc("abc"));
        assertEquals(-1L, DinhDang.doc("1234567890123"));
    }
}
