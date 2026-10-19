package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.WorkflowSupport.*;

import com.petstation.dao.impl.JdbcReturnDao;
import com.petstation.model.Permission;

import java.time.*;
import java.util.*;

public final class ReturnService {
    public static long returnItem(long item, int qty, String reason) {
        require(Permission.POS_SELL);
        positive(qty);
        if (reason.isBlank()) throw new IllegalArgumentException("Cần lý do đổi trả.");
        return transaction(
                c -> {
                    List<Object[]> items = JdbcReturnDao.findReturnItem(item);
                    if (items.isEmpty() || !"PAID".equals(items.get(0)[6]))
                        throw new IllegalArgumentException("Hóa đơn không còn hợp lệ để trả hàng.");
                    Object[] i = items.get(0);
                    long already = JdbcReturnDao.countReturnItem(item);
                    if (qty + already > value(i[5]))
                        throw new IllegalArgumentException("Số trả vượt số đã mua chưa trả.");
                    long refund =
                            value(i[2]) == 0
                                    ? 0
                                    : Math.round(
                                            qty * value(i[4]) * (double) value(i[1]) / value(i[2]));
                    long result =
                            JdbcReturnDao.insertReturnItem(
                                    code("DT"), i[0], accountId(), -refund, reason);
                    int remaining = qty;
                    long refundLeft = refund;
                    for (Object[] b : JdbcReturnDao.findReturnItem2(item)) {
                        int amount = Math.min(remaining, ((Number) b[1]).intValue());
                        if (amount <= 0) continue;
                        long part =
                                amount == remaining
                                        ? refundLeft
                                        : Math.round(refund * (double) amount / qty);
                        JdbcReturnDao.updateReturnItem(result, item, b[0], amount, part);
                        remaining -= amount;
                        refundLeft -= part;
                        if (remaining == 0) break;
                    }
                    if (remaining != 0)
                        throw new IllegalArgumentException(
                                "Thiếu thông tin phân bổ lô của hóa đơn gốc.");
                    boolean late =
                            ((java.sql.Timestamp) i[3])
                                    .toLocalDateTime()
                                    .toLocalDate()
                                    .plusDays(SettingsService.returnWindowDays())
                                    .isBefore(LocalDate.now());
                    if (late || refund >= SettingsService.writeOffApprovalLimit())
                        ApprovalService.submitAction(
                                ApprovalService.Kind.RETURN,
                                reason,
                                "Hóa đơn " + i[0],
                                "Hoàn " + refund,
                                "RETURN",
                                result,
                                "");
                    else applyReturn(result);
                    return result;
                });
    }

    static void applyReturn(long returned) {
        if (JdbcReturnDao.countApplyReturn(returned) == 0)
            throw new IllegalStateException("Phiếu đổi trả đã xử lý.");
        Object[] invoice =
                JdbcReturnDao.findApplyReturn2(returned).stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Hóa đơn đã hủy."));
        for (Object[] r : JdbcReturnDao.findApplyReturn(returned))
            JdbcReturnDao.updateApplyReturn(r[1], r[0]);
        JdbcReturnDao.updateApplyReturn2(returned);
        if (invoice[4] != null && value(invoice[3]) > 0) {
            long delta =
                    Math.round(
                            (value(invoice[2]) - value(invoice[1]))
                                    * (-value(invoice[5]))
                                    / (double) value(invoice[3]));
            long old = JdbcReturnDao.countApplyReturn2(invoice[4]);
            long next = Math.max(0, old + delta);
            JdbcReturnDao.updateApplyReturn3(next, invoice[4]);
            JdbcReturnDao.updateApplyReturn4(invoice[4], invoice[0], next - old, next);
        }
        AuditService.log("Đổi trả", text(returned));
    }
}
