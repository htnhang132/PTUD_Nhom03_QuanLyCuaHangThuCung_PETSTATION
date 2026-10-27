package com.petstation.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class DongThuTienTest {
    @Test
    void ketHopBaPhuongThucVaChiTienMatCoTienThua() {
        var rows =
                List.of(
                        new DongThuTien(PhuongThuc.CASH, 30000, 50000, true),
                        new DongThuTien(PhuongThuc.TRANSFER, 40000, 40000, true),
                        new DongThuTien(PhuongThuc.CARD, 30000, 30000, true));
        assertThat(DongThuTien.kiemTra(rows, 100000)).hasSize(3);
        assertThat(rows.stream().mapToLong(DongThuTien::tienThua).sum()).isEqualTo(20000);
        assertThatThrownBy(() -> new DongThuTien(PhuongThuc.CARD, 100, 200, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void chanSaiTongLapPhuongThucVaChuaXacNhan() {
        var cash = new DongThuTien(PhuongThuc.CASH, 100, 100, true);
        assertThatThrownBy(() -> DongThuTien.kiemTra(List.of(cash), 101))
                .hasMessageContaining("Tổng phân bổ");
        assertThatThrownBy(() -> DongThuTien.kiemTra(List.of(cash, cash), 200))
                .hasMessageContaining("một dòng");
        assertThatThrownBy(
                        () ->
                                DongThuTien.kiemTra(
                                        List.of(
                                                new DongThuTien(
                                                        PhuongThuc.TRANSFER, 100, 100, false)),
                                        100))
                .hasMessageContaining("xác nhận");
    }
}
