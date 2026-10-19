package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.WorkflowSupport.*;

import com.petstation.dao.impl.JdbcApprovalDao;
import com.petstation.model.Permission;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Approval commands survive restarts; their effects and decisions commit together. */
public final class ApprovalService {
    public enum Kind {
        INVOICE("Sửa/hủy hóa đơn"),
        RETURN("Đổi trả vượt hạn mức"),
        WRITE_OFF("Xuất hủy giá trị lớn"),
        STOCKTAKE("Điều chỉnh kiểm kê"),
        LEAVE("Nghỉ phép");
        public final String label;

        Kind(String label) {
            this.label = label;
        }
    }

    public static final class Request {
        public final String id;
        public final Kind kind;
        public final String sender;
        public final LocalDateTime time;
        public final String reason, before, after;
        public String status, decidedBy, rejectReason;

        Request(Object[] r) {
            id = text(r[0]);
            kind = Kind.valueOf(text(r[1]));
            sender = text(r[2]);
            time = ((java.sql.Timestamp) r[3]).toLocalDateTime();
            reason = text(r[4]);
            before = text(r[5]);
            after = text(r[6]);
            status =
                    switch (text(r[7])) {
                        case "APPROVED" -> "Đã duyệt";
                        case "REJECTED" -> "Từ chối";
                        default -> "Chờ duyệt";
                    };
            decidedBy = text(r[8]);
            rejectReason = text(r[9]);
        }

        public String timeText() {
            return time.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        }
    }

    private static final List<Runnable> LISTENERS = new ArrayList<>();
    public static String selectedInvoice = "";

    private ApprovalService() {}

    public static Request submitAction(
            Kind kind,
            String reason,
            String before,
            String after,
            String action,
            long target,
            String payload) {
        Permission permission =
                switch (kind) {
                    case INVOICE, RETURN -> Permission.POS_SELL;
                    case WRITE_OFF -> Permission.INVENTORY_ISSUE;
                    case STOCKTAKE -> Permission.INVENTORY_COUNT;
                    case LEAVE -> Permission.SCHEDULE_VIEW;
                };
        require(permission);
        if (reason == null || reason.isBlank())
            throw new IllegalArgumentException("Cần nhập lý do.");
        String code = code("YC");
        transaction(
                c -> {
                    JdbcApprovalDao.updateSubmitAction(
                            code,
                            kind.name(),
                            accountId(),
                            action,
                            "" + target,
                            reason,
                            before,
                            after,
                            payload);
                    AuditService.log("Gửi phê duyệt", code + " / " + kind.label);
                    return null;
                });
        fire();
        return all().stream().filter(r -> r.id.equals(code)).findFirst().orElseThrow();
    }

    public static List<Request> all() {
        List<Request> result = new ArrayList<>();
        for (Object[] r : JdbcApprovalDao.findAll()) result.add(new Request(r));
        return result;
    }

    public static long pendingCount() {
        return JdbcApprovalDao.countPendingCount();
    }

    public static void approve(Request request, String approver) {
        require(Permission.APPROVAL_PROCESS);
        transaction(
                c -> {
                    List<Object[]> rows = JdbcApprovalDao.findApprove(request.id);
                    if (rows.isEmpty())
                        throw new IllegalArgumentException("Yêu cầu đã được xử lý.");
                    Object[] r = rows.get(0);
                    ApprovalService.applyApproval(
                            text(r[0]), Long.parseLong(text(r[1])), text(r[2]));
                    JdbcApprovalDao.updateApprove(accountId(), request.id);
                    AuditService.log("Duyệt", request.id);
                    return null;
                });
        request.status = "Đã duyệt";
        request.decidedBy = approver;
        fire();
    }

    public static void reject(Request request, String approver, String reason) {
        require(Permission.APPROVAL_PROCESS);
        if (reason.isBlank()) throw new IllegalArgumentException("Cần lý do từ chối.");
        transaction(
                c -> {
                    List<Object[]> rows = JdbcApprovalDao.findReject(request.id);
                    if (rows.isEmpty()) throw new IllegalArgumentException("Yêu cầu đã xử lý.");
                    Object[] r = rows.get(0);
                    if ("RETURN".equals(r[0]))
                        JdbcApprovalDao.updateReject3(Long.parseLong(text(r[1])));
                    if ("WRITE_OFF".equals(r[0]))
                        JdbcApprovalDao.updateReject(Long.parseLong(text(r[1])));
                    if ("STOCKTAKE".equals(r[0]))
                        JdbcApprovalDao.updateReject2(Long.parseLong(text(r[1])));
                    JdbcApprovalDao.updateReject4(accountId(), reason, request.id);
                    AuditService.log("Từ chối", request.id + " / " + reason);
                    return null;
                });
        request.status = "Từ chối";
        request.decidedBy = approver;
        request.rejectReason = reason;
        fire();
    }

    public static void addListener(Runnable listener) {
        LISTENERS.add(listener);
    }

    private static void fire() {
        for (Runnable listener : List.copyOf(LISTENERS)) listener.run();
    }

    public static void invoiceRequest(
            String invoice, boolean cancel, String reason, String change) {
        require(Permission.POS_SELL);
        long id = id("hoa_don", "so_hoa_don", invoice);
        if (reason.isBlank() || !cancel && change.isBlank())
            throw new IllegalArgumentException("Cần lý do và nội dung cần sửa.");
        ApprovalService.submitAction(
                ApprovalService.Kind.INVOICE,
                reason,
                invoice,
                cancel ? "Hủy hóa đơn" : change,
                cancel ? "CANCEL_INVOICE" : "INVOICE_NOTE",
                id,
                change);
    }

    public static void applyApproval(String action, long target, String payload) {
        require(Permission.APPROVAL_PROCESS);
        switch (action) {
            case "WRITE_OFF" -> InventoryService.applyWriteOff(target);
            case "STOCKTAKE" -> InventoryService.applyStocktake(target);
            case "RETURN" -> ReturnService.applyReturn(target);
            case "LEAVE" ->
                    com.petstation.service.ScheduleService.markLeaveApproved(
                            target, LocalDate.parse(payload));
            case "INVOICE_NOTE" -> {
                if (JdbcApprovalDao.countApplyApproval(target) == 0)
                    throw new IllegalArgumentException("Hóa đơn không còn hợp lệ.");
                AuditService.log("Sửa ghi chú hóa đơn", target + ": " + payload);
                JdbcApprovalDao.updateApplyApproval(payload, target);
            }
            case "CANCEL_INVOICE" -> SalesService.cancelInvoice(target);
            default -> throw new IllegalArgumentException("Loại phê duyệt không được hỗ trợ.");
        }
    }
}
