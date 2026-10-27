package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.gui.components.common.BoSoanThaoBangGia;

import org.junit.jupiter.api.Test;

/** Cung cấp giao diện và xử lý tương tác cho giá bảng bộ soạn thảo kiểm thử. */
class BoSoanThaoBangGiaTest {
    /** Bảng. */
    private static final String BANG =
            "Chó|< 5kg|150000;Chó|5 - 10kg|200000;Chó|> 10kg|280000;Mèo|Mọi cân nặng|180000";

    @Test
    void layKhoangChonDuocGiaTheoCanNang() {
        assertThat(BoSoanThaoBangGia.layGiaCho(BANG, "Chó", 3.2)).isEqualTo(150_000);
        assertThat(BoSoanThaoBangGia.layGiaCho(BANG, "Chó", 7)).isEqualTo(200_000);
        assertThat(BoSoanThaoBangGia.layGiaCho(BANG, "Chó", 29.5)).isEqualTo(280_000);
        assertThat(BoSoanThaoBangGia.layGiaCho(BANG, "Mèo", 4)).isEqualTo(180_000);
        assertThat(BoSoanThaoBangGia.layGiaCho(BANG, "Khác", 4)).isEqualTo(-1);
    }

    @Test
    void layNhoNhatGiaLaThapNhatDong() {
        assertThat(BoSoanThaoBangGia.layGiaNhoNhat(BANG)).isEqualTo(150_000);
        assertThat(BoSoanThaoBangGia.layGiaNhoNhat("")).isEqualTo(-1);
    }
}
