package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.*;

import com.petstation.gui.components.common.BoChonKhoangNgay;
import com.petstation.gui.components.common.TruongMatKhauAnHien;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import javax.swing.*;

class TruongMatKhauAnHienTest {
    @Test
    void matKhauDoiTrangThaiBangNutMatTrongO() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    var o = new TruongMatKhauAnHien();
                    o.datMatKhau("KiemTra123!");
                    var btn =
                            (JToggleButton)
                                    o.layTruongMatKhau()
                                            .getClientProperty("JTextField.trailingComponent");
                    assertThat(btn.getIcon()).isNotNull();
                    assertThat(btn.getSelectedIcon()).isNotNull();
                    assertThat((int) o.layTruongMatKhau().getEchoChar()).isNotZero();
                    btn.doClick();
                    assertThat((int) o.layTruongMatKhau().getEchoChar()).isZero();
                    btn.doClick();
                    assertThat((int) o.layTruongMatKhau().getEchoChar()).isNotZero();
                    assertThat(o.layVanBanMatKhau()).isEqualTo("KiemTra123!");
                });
    }

    @Test
    void kyChonNhanhVaNgayTuChonDongBo() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    var boChon = new BoChonKhoangNgay();
                    boChon.chonKy("30 ngày qua");
                    assertThat(boChon.layNgayBatDau()).isEqualTo(LocalDate.now().minusDays(29));
                    assertThat(boChon.layNgayKetThuc()).isEqualTo(LocalDate.now());
                    boChon.datNgayBatDau(LocalDate.of(2020, 1, 1));
                    boChon.datNgayKetThuc(LocalDate.of(2020, 1, 31));
                    assertThat(boChon.layNgayBatDau()).isEqualTo(LocalDate.of(2020, 1, 1));
                    assertThat(boChon.layNgayKetThuc()).isEqualTo(LocalDate.of(2020, 1, 31));
                });
    }
}
