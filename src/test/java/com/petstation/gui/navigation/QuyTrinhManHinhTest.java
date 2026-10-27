package com.petstation.gui.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.model.MaManHinh;
import com.petstation.util.TienTe;

import org.junit.jupiter.api.Test;

/** Cung cấp giao diện và xử lý tương tác cho màn hình các quy trình kiểm thử. */
class QuyTrinhManHinhTest {
    @Test
    void layBanHangQuyTrinhCoBaDungThuTuCacBuoc() {
        assertThat(QuyTrinhManHinh.tim(MaManHinh.PAYMENT)).isPresent();
        assertThat(QuyTrinhManHinh.tim(MaManHinh.PAYMENT).get().layChiSoCua(MaManHinh.PAYMENT))
                .isEqualTo(1);
        assertThat(QuyTrinhManHinh.tim(MaManHinh.REPORTS)).isEmpty();
    }

    @Test
    void layTienTeDocDuocVaDinhDangDuoc() {
        assertThat(TienTe.doc("1.250.000đ")).isEqualTo(1_250_000);
        assertThat(TienTe.doc("abc")).isEqualTo(-1);
        assertThat(TienTe.dinhDang(285_000)).endsWith("₫").contains("285");
    }

    /**
     * Lấy Spa và Hotel các quy trình là tuần tự và kết thúc tại thanh toán trong
     * QuyTrinhManHinhTest.
     */
    @Test
    void laySpaVaHotelCacQuyTrinhLaTuanTuVaKetThucTaiThanhToan() {
        // Spa.
        var spa = QuyTrinhManHinh.tim(MaManHinh.SPA_INTAKE).orElseThrow();
        assertThat(spa.tuanTu()).isTrue();
        assertThat(spa.layTruocDo(MaManHinh.SPA_INTAKE)).contains(MaManHinh.SPA_SCHEDULE);
        assertThat(spa.layTiepTheo(MaManHinh.SPA_INTAKE)).contains(MaManHinh.SPA_SERVICE_SELECTION);
        assertThat(spa.kiemTraCuoiCung(MaManHinh.SPA_CLOSE)).isTrue();
        assertThat(
                        QuyTrinhManHinh.tim(MaManHinh.CHECKOUT)
                                .orElseThrow()
                                .kiemTraCuoiCung(MaManHinh.CHECKOUT))
                .isTrue();
        assertThat(QuyTrinhManHinh.tim(MaManHinh.POS).orElseThrow().tuanTu()).isFalse();
    }
}
