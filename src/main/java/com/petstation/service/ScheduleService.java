package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import com.petstation.dao.impl.JdbcScheduleDao;
import com.petstation.model.Permission;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/** Staff assignments are loaded from SQL; empty weeks remain empty until assigned. */
public final class ScheduleService {
    public static final String[] SHIFTS = {"Sáng", "Chiều", "Tối"};
    public static final String[] SHIFT_TIMES = {"07:00 – 12:00", "12:00 – 17:00", "17:00 – 21:00"};
    public static final int[] MIN_STAFF = {2, 2, 1};
    private static final String[] CODES = {"CA_SANG", "CA_CHIEU", "CA_TOI"};
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    public static final class Entry {
        public final LocalDate date;
        public final int shift;
        public final String staffCode;
        public String kind;

        Entry(LocalDate date, int shift, String code, String kind) {
            this.date = date;
            this.shift = shift;
            this.staffCode = code;
            this.kind = kind;
        }

        public boolean isLeave() {
            return "LEAVE".equals(kind);
        }
    }

    private ScheduleService() {}

    public static LocalDate mondayOf(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private static List<Entry> range(LocalDate from, LocalDate to) {
        List<Entry> result = new ArrayList<>();
        for (Object[] r : JdbcScheduleDao.findRange(from, to)) {
            int shift = Arrays.asList(CODES).indexOf(text(r[1]));
            if (shift >= 0)
                result.add(
                        new Entry(
                                ((java.sql.Date) r[0]).toLocalDate(),
                                shift,
                                text(r[2]),
                                text(r[3])));
        }
        return result;
    }

    public static List<Entry> weekEntries(LocalDate monday) {
        return range(monday, monday.plusDays(7));
    }

    public static List<Entry> cell(LocalDate date, int shift) {
        return range(date, date.plusDays(1)).stream().filter(e -> e.shift == shift).toList();
    }

    public static long workCount(LocalDate date, int shift) {
        return cell(date, shift).stream().filter(e -> !e.isLeave()).count();
    }

    public static String assign(LocalDate date, int shift, String code) {
        require(Permission.SCHEDULE_MANAGE);
        if (shift < 0 || shift >= CODES.length) return "Ca không hợp lệ.";
        return transaction(
                c -> {
                    long staff = CatalogService.id("nhan_vien", "ma_nhan_vien", code);
                    JdbcScheduleDao.countAssign(staff);
                    if (JdbcScheduleDao.countAssign2(staff) == 0) return "Nhân viên đã nghỉ việc.";
                    if (JdbcScheduleDao.countAssign3(staff, date, CODES[shift]) > 0)
                        return "Nhân viên đã có ca hoặc nghỉ phép ngày này.";
                    JdbcScheduleDao.updateAssign(staff, date, CODES[shift]);
                    JdbcScheduleDao.updateAssign2(staff, date, CODES[shift]);
                    fire();
                    return null;
                });
    }

    public static void remove(Entry entry) {
        require(Permission.SCHEDULE_MANAGE);
        JdbcScheduleDao.updateRemove(entry.staffCode, entry.date, CODES[entry.shift]);
        fire();
    }

    public static void markLeaveApproved(long staff, LocalDate date) {
        require(Permission.APPROVAL_PROCESS);
        transaction(
                c -> {
                    JdbcScheduleDao.countMarkLeaveApproved(staff);
                    if (JdbcScheduleDao.updateMarkLeaveApproved(staff, date) == 0)
                        JdbcScheduleDao.updateMarkLeaveApproved2(staff, date);
                    return null;
                });
        fire();
    }

    public static void markLeave(String code, LocalDate date) {
        markLeaveApproved(CatalogService.id("nhan_vien", "ma_nhan_vien", code), date);
    }

    public static int monthHours(String code, LocalDate date) {
        LocalDate first = date.withDayOfMonth(1);
        return (int) JdbcScheduleDao.countMonthHours(code, first, first.plusMonths(1));
    }

    public static void addListener(Runnable listener) {
        LISTENERS.add(listener);
    }

    private static void fire() {
        for (Runnable listener : List.copyOf(LISTENERS)) listener.run();
    }
}
