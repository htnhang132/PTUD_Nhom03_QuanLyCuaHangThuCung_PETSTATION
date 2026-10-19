package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import com.petstation.dao.impl.JdbcAuditDao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Persistent read-only audit trail. */
public final class AuditService {
    public record Entry(
            LocalDateTime time,
            String user,
            String action,
            String object,
            String type,
            String result) {
        public String timeText() {
            return time.format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"));
        }
    }

    private static final List<Runnable> LISTENERS = new ArrayList<>();
    private static String user = "Hệ thống";

    private AuditService() {}

    public static void setUser(String name) {
        user = name == null ? "Hệ thống" : name;
    }

    public static String user() {
        return currentSession() == null ? user : session().displayName();
    }

    public static void log(String action, String object) {
        log(typeOf(action), action, object, "Thành công");
    }

    public static void log(String type, String action, String object, String result) {
        JdbcAuditDao.updateLog(
                currentSession() == null ? null : accountId(),
                action,
                object,
                user(),
                type,
                result);
        for (Runnable listener : List.copyOf(LISTENERS)) listener.run();
    }

    private static String typeOf(String action) {
        String a = action.toLowerCase();
        if (a.contains("đăng nhập") || a.contains("đăng xuất")) return "LOGIN";
        if (a.startsWith("thêm") || a.startsWith("tạo") || a.startsWith("lập")) return "CREATE";
        if (a.startsWith("sửa")
                || a.startsWith("cập nhật")
                || a.startsWith("khóa")
                || a.startsWith("mở khóa")
                || a.startsWith("đặt lại")) return "UPDATE";
        if (a.startsWith("xóa") || a.startsWith("ngừng") || a.startsWith("hủy")) return "DELETE";
        if (a.startsWith("duyệt") || a.startsWith("từ chối") || a.startsWith("gửi"))
            return "APPROVAL";
        if (a.startsWith("thanh toán") || a.startsWith("đổi trả")) return "PAYMENT";
        if (a.startsWith("nhập kho") || a.startsWith("xuất") || a.startsWith("kiểm kê"))
            return "INVENTORY";
        return "SYSTEM";
    }

    public static List<Entry> entries() {
        List<Entry> result = new ArrayList<>();
        for (Object[] r : JdbcAuditDao.findEntries())
            result.add(
                    new Entry(
                            ((java.sql.Timestamp) r[0]).toLocalDateTime(),
                            text(r[1]),
                            text(r[2]),
                            text(r[3]),
                            text(r[4]),
                            text(r[5])));
        return result;
    }

    public static void addListener(Runnable listener) {
        LISTENERS.add(listener);
    }
}
