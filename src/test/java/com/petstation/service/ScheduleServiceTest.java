package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ScheduleServiceTest {
    @Test
    void mondayAnchorsWeek() {
        assertThat(ScheduleService.mondayOf(LocalDate.of(2026, 10, 9)))
                .isEqualTo(LocalDate.of(2026, 10, 5));
        assertThat(ScheduleService.mondayOf(LocalDate.of(2026, 10, 5)))
                .isEqualTo(LocalDate.of(2026, 10, 5));
    }
}
