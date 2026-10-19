package com.petstation.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KhoangNgayTest {
    @Test
    void cungMotNgayVanLayDenTruocNgayKeTiep() {
        LocalDate ngay = LocalDate.of(2026, 9, 15);
        var khoang = new KhoangNgay(ngay, ngay);
        assertThat(khoang.demSoNgay()).isEqualTo(1);
        assertThat(khoang.layCanTren()).isEqualTo(ngay.plusDays(1));
    }

    @Test
    void tuChoiKhoangDaoNguoc() {
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () ->
                                new KhoangNgay(
                                        LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 19)));
    }

    @Test
    void baMuoiNgayBaoGomHomNay() {
        var khoang = KhoangNgay.ganDay(30);
        assertThat(khoang.demSoNgay()).isEqualTo(30);
        assertThat(khoang.denNgay()).isEqualTo(LocalDate.now());
    }
}
