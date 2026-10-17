package com.petstation.gui.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ScheduleStoreTest {
    @Test
    void weekStartsOnMondayAndShortStaffIsDetected() {
        LocalDate monday = ScheduleStore.mondayOf(LocalDate.of(2026, 10, 9));
        assertThat(monday).isEqualTo(LocalDate.of(2026, 10, 5));
        // Chủ nhật ca Chiều chưa có ai: thiếu so với mức tối thiểu
        assertThat(ScheduleStore.workCount(monday.plusDays(6), 1)).isLessThan(ScheduleStore.MIN_STAFF[1]);
        assertThat(ScheduleStore.workCount(monday, 0)).isGreaterThanOrEqualTo(ScheduleStore.MIN_STAFF[0]);
    }

    @Test
    void assignRejectsDuplicatesAndLeaveTurnsShiftsIntoLeave() {
        LocalDate day = LocalDate.of(2026, 10, 12).plusWeeks(3);
        assertThat(ScheduleStore.assign(day, 0, "NV005")).isNull();
        assertThat(ScheduleStore.assign(day, 0, "NV005")).contains("đã có");
        ScheduleStore.markLeave("NV002", day);
        assertThat(ScheduleStore.cell(day, 0)).anyMatch(e -> e.staffCode.equals("NV002") && e.isLeave());
        assertThat(ScheduleStore.assign(day, 1, "NV002")).contains("nghỉ phép");
    }
}
