package com.petstation.gui.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.petstation.gui.navigation.AuditTrail;

/**
 * Hàng chờ Phê duyệt dùng chung: sửa/hủy hóa đơn, đổi trả vượt hạn mức, xuất hủy lớn,
 * điều chỉnh kiểm kê, nghỉ phép. Khi duyệt sẽ chạy hành động đã đăng ký và ghi Nhật ký.
 */
public final class ApprovalStore {
    public enum Kind {
        INVOICE("Sửa/hủy hóa đơn"), RETURN("Đổi trả vượt hạn mức"), WRITE_OFF("Xuất hủy giá trị lớn"),
        STOCKTAKE("Điều chỉnh kiểm kê"), LEAVE("Nghỉ phép");
        public final String label;
        Kind(String label) { this.label = label; }
    }

    public static final class Request {
        public final String id;
        public final Kind kind;
        public final String sender;
        public final LocalDateTime time = LocalDateTime.now();
        public final String reason;
        public final String before;
        public final String after;
        public String status = "Chờ duyệt";
        public String decidedBy = "";
        public String rejectReason = "";
        private final Runnable onApprove;

        Request(String id, Kind kind, String sender, String reason, String before, String after, Runnable onApprove) {
            this.id = id; this.kind = kind; this.sender = sender; this.reason = reason;
            this.before = before; this.after = after; this.onApprove = onApprove;
        }

        public String timeText() { return time.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")); }
    }

    private static final List<Request> REQUESTS = new ArrayList<>();
    private static final List<Runnable> LISTENERS = new ArrayList<>();
    private static int seq = 1;
    /** Hóa đơn đang chọn ở Tìm kiếm khi bấm "Yêu cầu sửa/hủy". */
    public static String selectedInvoice = "";

    private ApprovalStore() {}

    public static Request submit(Kind kind, String sender, String reason, String before, String after, Runnable onApprove) {
        Request r = new Request(String.format("YC%03d", seq++), kind, sender, reason, before, after, onApprove);
        REQUESTS.add(r);
        AuditTrail.log("Gửi", "yêu cầu " + kind.label + " " + r.id + ": " + reason);
        fire();
        return r;
    }

    public static List<Request> all() { return List.copyOf(REQUESTS); }

    public static long pendingCount() { return REQUESTS.stream().filter(r -> r.status.equals("Chờ duyệt")).count(); }

    public static void approve(Request r, String approver) {
        if (!r.status.equals("Chờ duyệt")) return;
        r.status = "Đã duyệt";
        r.decidedBy = approver;
        if (r.onApprove != null) r.onApprove.run();
        AuditTrail.log("Duyệt", "yêu cầu " + r.kind.label + " " + r.id + " của " + r.sender);
        fire();
    }

    public static void reject(Request r, String approver, String reason) {
        if (!r.status.equals("Chờ duyệt")) return;
        r.status = "Từ chối";
        r.decidedBy = approver;
        r.rejectReason = reason;
        AuditTrail.log("Từ chối", "yêu cầu " + r.kind.label + " " + r.id + " của " + r.sender + " (" + reason + ")");
        fire();
    }

    public static void addListener(Runnable l) { LISTENERS.add(l); }

    private static void fire() { for (Runnable l : List.copyOf(LISTENERS)) l.run(); }
}
