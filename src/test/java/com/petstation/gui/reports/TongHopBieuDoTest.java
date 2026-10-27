package com.petstation.gui.reports;

import static org.assertj.core.api.Assertions.*;

import com.petstation.model.KhoangNgay;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TongHopBieuDoTest {
    @Test
    void dienNgayTrongVaGiuChiSoChungTu() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        var r =
                TongHopBieuDo.tao(
                        new String[] {"2026-01-01", "2026-01-03"},
                        new double[] {10, -4},
                        new KhoangNgay(start, start.plusDays(6)),
                        "Ngày");
        assertThat(r.giaTri()).containsExactly(10, 0, -4, 0, 0, 0, 0);
        assertThat(r.dongGoc().get(2)).containsExactly(1);
    }

    @Test
    void kyDaiTongHopThangKhongMatSoLieu() {
        var r =
                TongHopBieuDo.tao(
                        new String[] {"2026-01-01", "2026-01-05", "2026-12-31"},
                        new double[] {10, 20, 7},
                        new KhoangNgay(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)),
                        "Tự động");
        assertThat(r.nhan()).hasSize(12);
        assertThat(java.util.Arrays.stream(r.giaTri()).sum()).isEqualTo(37);
        assertThat(r.dongGoc().get(0)).containsExactly(0, 1);
    }

    @Test
    void nhanDaiTrungNhauDuocGopVaSoAmDuocGiu() {
        var r =
                TongHopBieuDo.tao(
                        new String[] {"Tên rất dài", "Tên rất dài", "Khác"},
                        new double[] {10, 2, -4},
                        KhoangNgay.ganDay(30),
                        "Tự động");
        assertThat(r.giaTri()).containsExactly(12, -4);
        assertThat(r.dongGoc().get(0)).containsExactly(0, 1);
    }
}
