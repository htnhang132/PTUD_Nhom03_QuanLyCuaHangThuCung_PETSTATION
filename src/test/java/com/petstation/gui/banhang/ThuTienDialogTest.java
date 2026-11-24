package com.petstation.gui.banhang;

import static org.assertj.core.api.Assertions.*;

import com.petstation.model.chung.PhuongThuc;

import org.junit.jupiter.api.Test;

class ThuTienDialogTest {
    @Test
    void thuMotPhanGiuDungTienKhachDua() {
        var k =
                ThuTienDialog.kiemTraKhoanThu(
                        PhuongThuc.TIEN_MAT, 200000, 100000, 150000, false, false);
        assertThat(k.soTien()).isEqualTo(100000);
        assertThat(k.khachDua()).isEqualTo(150000);
    }

    @Test
    void khongThuQuaPhanConThieu() {
        assertThatThrownBy(
                        () ->
                                ThuTienDialog.kiemTraKhoanThu(
                                        PhuongThuc.TIEN_MAT, 200000, 200001, 300000, false, false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void qrPhaiHopLeVaDuocXacNhan() {
        assertThatThrownBy(
                        () ->
                                ThuTienDialog.kiemTraKhoanThu(
                                        PhuongThuc.CHUYEN_KHOAN,
                                        200000,
                                        100000,
                                        100000,
                                        true,
                                        false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(
                        () ->
                                ThuTienDialog.kiemTraKhoanThu(
                                        PhuongThuc.CHUYEN_KHOAN,
                                        200000,
                                        100000,
                                        100000,
                                        false,
                                        true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void theKhongGhiTienThua() {
        var k =
                ThuTienDialog.kiemTraKhoanThu(
                        PhuongThuc.THE_NGAN_HANG, 200000, 100000, 150000, true, false);
        assertThat(k.khachDua()).isEqualTo(k.soTien());
    }
}
