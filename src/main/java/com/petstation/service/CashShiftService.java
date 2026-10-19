package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import com.petstation.dao.impl.JdbcCashShiftDao;
import com.petstation.model.Permission;

import java.time.LocalTime;
import java.util.*;

/** Current account's persisted shift and invoice totals. */
public final class CashShiftService {
    public enum Method {
        CASH,
        TRANSFER,
        CARD
    }

    private final List<Runnable> listeners = new ArrayList<>();
    private String pendingTitle = "", lastHandover = "";

    private Object[] current() {
        if (currentSession() == null) return null;
        List<Object[]> rows = JdbcCashShiftDao.findCurrent(accountId());
        return rows.isEmpty() ? null : rows.get(0);
    }

    public long id() {
        Object[] r = current();
        return r == null ? 0 : ((Number) r[0]).longValue();
    }

    public boolean isOpen() {
        return id() > 0;
    }

    public String shiftName() {
        Object[] r = current();
        return r == null ? "" : text(r[1]);
    }

    public LocalTime openedAt() {
        Object[] r = current();
        return r == null
                ? null
                : ((java.sql.Timestamp) r[2]).toLocalDateTime().toLocalTime().withNano(0);
    }

    public long openingCash() {
        Object[] r = current();
        return r == null ? 0 : ((Number) r[3]).longValue();
    }

    private long sales(Method method) {
        return JdbcCashShiftDao.countSales(id(), method.name());
    }

    public long cashSales() {
        return sales(Method.CASH);
    }

    public long transferSales() {
        return sales(Method.TRANSFER);
    }

    public long cardSales() {
        return sales(Method.CARD);
    }

    public int invoiceCount() {
        return (int) JdbcCashShiftDao.countInvoiceCount(id());
    }

    public long expectedCash() {
        return openingCash() + cashSales() - JdbcCashShiftDao.countExpectedCash(id());
    }

    public String label() {
        return isOpen() ? "Đang mở – " + shiftName() : "Chưa mở ca";
    }

    public String pendingTitle() {
        return pendingTitle;
    }

    public void setPendingTitle(String title) {
        pendingTitle = title == null ? "" : title;
    }

    public String lastHandover() {
        return lastHandover;
    }

    public void open(String name, long amount) {
        require(Permission.CASH_SHIFT_MANAGE);
        if (amount < 0) throw new IllegalArgumentException("Tiền đầu ca phải không âm.");
        transaction(
                c -> {
                    if (isOpen()) throw new IllegalStateException("Tài khoản đã có ca đang mở.");
                    JdbcCashShiftDao.updateOpen(accountId(), name, amount);
                    AuditService.log("Mở ca", name);
                    return null;
                });
        fire();
    }

    public void recordSale(Method method, long amount) {
        fire();
    }

    public void close(long counted, String note) {
        require(Permission.CASH_SHIFT_MANAGE);
        if (counted < 0) throw new IllegalArgumentException("Tiền thực đếm phải không âm.");
        transaction(
                c -> {
                    long currentId = id();
                    if (currentId == 0) throw new IllegalStateException("Chưa mở ca.");
                    JdbcCashShiftDao.countClose(currentId);
                    long expected = expectedCash();
                    if (JdbcCashShiftDao.updateClose(counted, counted - expected, note, currentId)
                            != 1) throw new IllegalStateException("Ca đã đóng.");
                    lastHandover =
                            "Thực đếm "
                                    + counted
                                    + " / dự kiến "
                                    + expected
                                    + " (chênh lệch "
                                    + (counted - expected)
                                    + ")";
                    AuditService.log("Kết ca", lastHandover);
                    return null;
                });
        fire();
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void fire() {
        for (Runnable listener : List.copyOf(listeners)) listener.run();
    }
}
