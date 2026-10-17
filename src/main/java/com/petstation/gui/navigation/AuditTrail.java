package com.petstation.gui.navigation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nhật ký hệ thống trong phiên: các màn gọi {@link #log} khi đăng nhập, thêm, sửa, xóa, duyệt...
 * (chưa lưu database). Màn Nhật ký chỉ đọc danh sách này, không có thao tác xóa.
 */
public final class AuditTrail {
    public record Entry(LocalDateTime time, String user, String action, String object, String type, String result) {
        public String timeText() { return time.format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss")); }
    }

    private static final List<Entry> ENTRIES = Collections.synchronizedList(new ArrayList<>());
    private static final List<Runnable> LISTENERS = new ArrayList<>();
    private static String user = "Hệ thống";

    private AuditTrail() {}

    public static void setUser(String name) { user = name == null || name.isBlank() ? "Hệ thống" : name; }
    public static String user() { return user; }

    /** Ghi một sự kiện thành công; loại sự kiện suy ra từ tên thao tác. */
    public static void log(String action, String object) {
        log(typeOf(action), action, object, "Thành công");
    }

    public static void log(String type, String action, String object, String result) {
        ENTRIES.add(new Entry(LocalDateTime.now(), user, action, object, type, result));
        for (Runnable r : List.copyOf(LISTENERS)) r.run();
    }

    private static String typeOf(String action) {
        String a = action.toLowerCase();
        if (a.contains("đăng nhập") || a.contains("đăng xuất")) return "LOGIN";
        if (a.startsWith("thêm") || a.startsWith("tạo") || a.startsWith("lập")) return "CREATE";
        if (a.startsWith("sửa") || a.startsWith("cập nhật") || a.startsWith("khóa") || a.startsWith("mở khóa") || a.startsWith("đặt lại")) return "UPDATE";
        if (a.startsWith("xóa") || a.startsWith("ngừng") || a.startsWith("hủy")) return "DELETE";
        if (a.startsWith("duyệt") || a.startsWith("từ chối") || a.startsWith("gửi")) return "APPROVAL";
        if (a.startsWith("thanh toán") || a.startsWith("đổi trả")) return "PAYMENT";
        if (a.startsWith("nhập kho") || a.startsWith("xuất") || a.startsWith("kiểm kê")) return "INVENTORY";
        return "SYSTEM";
    }

    public static List<Entry> entries() {
        synchronized (ENTRIES) { return List.copyOf(ENTRIES); }
    }

    public static void addListener(Runnable r) { LISTENERS.add(r); }
}
