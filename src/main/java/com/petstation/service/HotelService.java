package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.WorkflowSupport.*;

import com.petstation.dao.impl.JdbcHotelDao;
import com.petstation.model.Permission;

import java.time.*;
import java.util.*;

public final class HotelService {
    public static long book(
            String petCode,
            String roomCode,
            LocalDateTime start,
            LocalDateTime end,
            long deposit,
            String note) {
        require(Permission.HOTEL_MANAGE);
        if (!end.isAfter(start) || deposit < 0)
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào và tiền cọc không âm.");
        return transaction(
                c -> {
                    Object[] p = pet(petCode);
                    List<Object[]> rooms = JdbcHotelDao.findBook(roomCode);
                    if (rooms.isEmpty()
                            || Set.of("INACTIVE", "MAINTENANCE", "CLEANING")
                                    .contains(text(rooms.get(0)[2])))
                        throw new IllegalArgumentException("Phòng không nhận đặt chỗ.");
                    Object[] room = rooms.get(0);
                    if ("Mèo".equals(room[3]) && !"CAT".equals(p[2]))
                        throw new IllegalArgumentException("Phòng Mèo chỉ dành cho mèo.");
                    if (JdbcHotelDao.countBook(room[0], p[0], end, start) > 0)
                        throw new IllegalArgumentException(
                                "Phòng hoặc thú cưng đã có đặt chỗ trùng thời gian.");
                    long price = value(room[1]);
                    if (deposit > days(start, end) * price)
                        throw new IllegalArgumentException(
                                "Tiền cọc không được vượt tiền phòng dự kiến.");
                    long result =
                            JdbcHotelDao.insertBook(
                                    code("HT"),
                                    p[1],
                                    p[0],
                                    room[0],
                                    start,
                                    end,
                                    deposit,
                                    price,
                                    note);
                    AuditService.log("Đặt phòng", roomCode + " / " + petCode);
                    return result;
                });
    }

    public static void checkIn(long booking) {
        require(Permission.HOTEL_MANAGE);
        transaction(
                c -> {
                    Object room = JdbcHotelDao.getCheckIn(booking);
                    if (room == null)
                        throw new IllegalArgumentException("Hợp đồng không còn chờ nhận phòng.");
                    if (JdbcHotelDao.updateCheckIn2(room) != 1)
                        throw new IllegalArgumentException("Phòng chưa sẵn sàng.");
                    JdbcHotelDao.updateCheckIn(booking);
                    AuditService.log("Nhận phòng", text(booking));
                    return null;
                });
    }

    public static void journal(long booking, String food, String hygiene, String health) {
        require(Permission.HOTEL_MANAGE);
        if (health.isBlank()) throw new IllegalArgumentException("Cần ghi tình trạng thú cưng.");
        transaction(
                c -> {
                    if (JdbcHotelDao.countJournal(booking) == 0)
                        throw new IllegalArgumentException(
                                "Thú cưng chưa nhận phòng hoặc đã trả phòng.");
                    JdbcHotelDao.updateJournal(booking, employeeId(), food, hygiene, health);
                    AuditService.log("Ghi nhật ký Hotel", text(booking));
                    return null;
                });
    }

    private static long days(LocalDateTime start, LocalDateTime end) {
        return Math.max(1, (Duration.between(start, end).toMinutes() + 1439) / 1440);
    }

    public static long hotelBalance(long booking) {
        List<Object[]> rows = JdbcHotelDao.findHotelBalance(booking);
        if (rows.isEmpty())
            throw new IllegalArgumentException("Hợp đồng chưa nhận phòng hoặc đã quyết toán.");
        Object[] r = rows.get(0);
        LocalDateTime start = ((java.sql.Timestamp) r[0]).toLocalDateTime(),
                end = ((java.sql.Timestamp) r[1]).toLocalDateTime();
        long late =
                LocalDateTime.now().isAfter(end)
                        ? days(end, LocalDateTime.now()) * SettingsService.lateCheckoutFeePerDay()
                        : 0;
        return Math.max(0, days(start, end) * value(r[2]) + late - value(r[3]));
    }

    public static void completeCleaning(long id) {
        require(Permission.HOTEL_MANAGE);
        transaction(
                c -> {
                    if (JdbcHotelDao.markRoomAvailable(id) != 1)
                        throw new IllegalArgumentException("Chỉ áp dụng cho phòng đang dọn.");
                    AuditService.log("Cập nhật phòng", "Hoàn thành dọn phòng");
                    return null;
                });
    }

    public static List<Object[]> journalDetails(long booking) {
        SessionService.require(com.petstation.model.Permission.HOTEL_MANAGE);
        return JdbcHotelDao.journalDetails(booking);
    }
}
