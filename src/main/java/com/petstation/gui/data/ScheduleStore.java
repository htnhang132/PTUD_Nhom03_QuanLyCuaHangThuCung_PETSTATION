package com.petstation.gui.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lịch làm việc theo tuần dùng chung cho 3 màn: Lịch làm việc, Lịch làm việc cá nhân, Phân lịch nhân viên.
 * Mỗi dòng là một nhân viên trong một ca của một ngày (WORK) hoặc nghỉ phép (LEAVE). Dữ liệu mẫu trong bộ nhớ.
 */
public final class ScheduleStore {
    public static final String[] SHIFTS = {"Sáng", "Chiều", "Tối"};
    public static final String[] SHIFT_TIMES = {"07:00 – 12:00", "12:00 – 17:00", "17:00 – 21:00"};
    /** Số nhân sự tối thiểu mỗi ca; thiếu thì cảnh báo ở lưới. */
    public static final int[] MIN_STAFF = {2, 2, 1};

    public static final class Entry {
        public final LocalDate date;
        public final int shift;
        public final String staffCode;
        public String kind; // WORK hoặc LEAVE

        Entry(LocalDate date, int shift, String staffCode, String kind) {
            this.date = date; this.shift = shift; this.staffCode = staffCode; this.kind = kind;
        }
        public boolean isLeave() { return "LEAVE".equals(kind); }
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Set<LocalDate> SEEDED = new HashSet<>();
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    private ScheduleStore() {}

    public static LocalDate mondayOf(LocalDate d) { return d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); }

    /** Tạo dữ liệu mẫu cho tuần chưa có (để chuyển tuần vẫn có lịch xem). */
    private static void ensureWeek(LocalDate monday) {
        if (!SEEDED.add(monday)) return;
        for (int d = 0; d < 7; d++) {
            LocalDate day = monday.plusDays(d);
            if (d != 6) add0(day, 0, "NV002");
            if (d < 5) { add0(day, 0, "NV004"); add0(day, 0, "NV003"); }
            if (d == 6) add0(day, 0, "NV006");
            if (d != 6) add0(day, 1, "NV006");
            if (d < 5) add0(day, 1, "NV005");
            if (d == 5) add0(day, 1, "NV004");
            if (d == 1 || d == 3 || d == 5) add0(day, 2, "NV002");
            if (d == 2 || d == 4) add0(day, 2, "NV005");
            if (d == 0) add0(day, 2, "NV001");
        }
    }

    private static void add0(LocalDate day, int shift, String code) { ENTRIES.add(new Entry(day, shift, code, "WORK")); }

    public static List<Entry> weekEntries(LocalDate monday) {
        ensureWeek(monday);
        List<Entry> out = new ArrayList<>();
        for (Entry e : ENTRIES) if (!e.date.isBefore(monday) && !e.date.isAfter(monday.plusDays(6))) out.add(e);
        return out;
    }

    public static List<Entry> cell(LocalDate date, int shift) {
        ensureWeek(mondayOf(date));
        List<Entry> out = new ArrayList<>();
        for (Entry e : ENTRIES) if (e.date.equals(date) && e.shift == shift) out.add(e);
        return out;
    }

    public static long workCount(LocalDate date, int shift) {
        return cell(date, shift).stream().filter(e -> !e.isLeave()).count();
    }

    /** Thêm nhân viên vào ca. Trả về thông báo lỗi hoặc null nếu thành công. */
    public static String assign(LocalDate date, int shift, String staffCode) {
        for (Entry e : cell(date, shift)) if (e.staffCode.equals(staffCode)) return "Nhân viên đã có trong ca này.";
        for (Entry e : ENTRIES) {
            if (e.date.equals(date) && e.staffCode.equals(staffCode) && e.isLeave()) return "Nhân viên đang nghỉ phép ngày này.";
        }
        ENTRIES.add(new Entry(date, shift, staffCode, "WORK"));
        fire();
        return null;
    }

    public static void remove(Entry e) { ENTRIES.remove(e); fire(); }

    /** Nghỉ phép được duyệt: các ca của nhân viên trong ngày đó chuyển thành LEAVE (nếu chưa có ca thì thêm một dòng ở ca Sáng). */
    public static void markLeave(String staffCode, LocalDate date) {
        ensureWeek(mondayOf(date));
        boolean any = false;
        for (Entry e : ENTRIES) {
            if (e.date.equals(date) && e.staffCode.equals(staffCode)) { e.kind = "LEAVE"; any = true; }
        }
        if (!any) ENTRIES.add(new Entry(date, 0, staffCode, "LEAVE"));
        fire();
    }

    /** Tổng giờ làm của nhân viên trong tháng của ngày đã cho (không tính nghỉ phép). */
    public static int monthHours(String staffCode, LocalDate anyDayInMonth) {
        LocalDate first = anyDayInMonth.withDayOfMonth(1);
        int hours = 0;
        for (LocalDate monday = mondayOf(first); !monday.isAfter(first.plusMonths(1)); monday = monday.plusWeeks(1)) {
            for (Entry e : weekEntries(monday)) {
                if (e.staffCode.equals(staffCode) && !e.isLeave() && e.date.getMonth() == first.getMonth()) hours += 5 - (e.shift == 2 ? 1 : 0);
            }
        }
        return hours;
    }

    public static void addListener(Runnable r) { LISTENERS.add(r); }
    private static void fire() { for (Runnable r : List.copyOf(LISTENERS)) r.run(); }
}
