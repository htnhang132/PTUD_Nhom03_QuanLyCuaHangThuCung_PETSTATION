package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho lịch làm việc dịch vụ kiểm thử. */
class LichLamViecServiceTest {
    /** Lấy thứ hai mốc tuần trong LichLamViecServiceTest. */
    @Test
    void layThuHaiMocTuan() {
        assertThat(LichLamViecService.layThuHaiCuaTuan(LocalDate.of(2026, 10, 9)))
                .isEqualTo(LocalDate.of(2026, 10, 5));
        assertThat(LichLamViecService.layThuHaiCuaTuan(LocalDate.of(2026, 10, 5)))
                .isEqualTo(LocalDate.of(2026, 10, 5));
    }
}
