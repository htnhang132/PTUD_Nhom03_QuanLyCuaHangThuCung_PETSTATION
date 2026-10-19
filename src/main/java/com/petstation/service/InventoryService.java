package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.WorkflowSupport.*;

import com.petstation.dao.impl.JdbcInventoryDao;
import com.petstation.model.Permission;

import java.time.*;
import java.util.*;

public final class InventoryService {
    public static long purchase(
            String supplier,
            String product,
            String batch,
            LocalDate expiry,
            int qty,
            long cost,
            long paid) {
        require(Permission.INVENTORY_RECEIVE);
        positive(qty);
        positive(cost);
        if (batch.isBlank() || paid < 0 || paid > qty * cost)
            throw new IllegalArgumentException("Số lô hoặc tiền đã trả không hợp lệ.");
        return transaction(
                c -> {
                    long p = id("san_pham", "ma_san_pham", product),
                            s = id("nha_cung_cap", "ma_ncc", supplier);
                    if (JdbcInventoryDao.countPurchase(p) > 0
                            && (expiry == null || expiry.isBefore(LocalDate.now())))
                        throw new IllegalArgumentException(
                                "Sản phẩm theo dõi HSD cần ngày hết hạn còn hiệu lực.");
                    if (JdbcInventoryDao.countPurchase2(s) == 0)
                        throw new IllegalArgumentException("Nhà cung cấp đã ngừng hợp tác.");
                    long receipt =
                            JdbcInventoryDao.insertPurchase(
                                    code("PN"), s, accountId(), qty * cost, paid);
                    JdbcInventoryDao.updatePurchase3(receipt, p, batch, expiry, qty, cost);
                    List<Object[]> batches = JdbcInventoryDao.findPurchase(p, batch);
                    if (batches.isEmpty()) JdbcInventoryDao.updatePurchase4(p, batch, expiry, qty);
                    else {
                        Object[] b = batches.get(0);
                        LocalDate old = b[1] == null ? null : ((java.sql.Date) b[1]).toLocalDate();
                        if (!Objects.equals(expiry, old))
                            throw new IllegalArgumentException(
                                    "Lô đã tồn tại với hạn sử dụng khác.");
                        JdbcInventoryDao.updatePurchase(qty, b[0]);
                    }
                    JdbcInventoryDao.updatePurchase2(cost, p);
                    AuditService.log("Nhập kho", product + " / " + batch + " x " + qty);
                    return receipt;
                });
    }

    public static long writeOff(long batch, int qty, String reason) {
        require(Permission.INVENTORY_ISSUE);
        positive(qty);
        if (reason.isBlank()) throw new IllegalArgumentException("Cần lý do xuất kho.");
        return transaction(
                c -> {
                    List<Object[]> batches = JdbcInventoryDao.findWriteOff(batch);
                    if (batches.isEmpty() || value(batches.get(0)[0]) < qty)
                        throw new IllegalArgumentException("Không đủ tồn lô.");
                    long cost = value(batches.get(0)[1]) * qty;
                    long issue =
                            JdbcInventoryDao.insertWriteOff(code("PX"), accountId(), reason, cost);
                    JdbcInventoryDao.updateWriteOff(issue, batch, qty, cost);
                    if (cost >= SettingsService.writeOffApprovalLimit())
                        ApprovalService.submitAction(
                                ApprovalService.Kind.WRITE_OFF,
                                reason,
                                "Tồn " + batches.get(0)[0],
                                "Xuất " + qty,
                                "WRITE_OFF",
                                issue,
                                "");
                    else applyWriteOff(issue);
                    return issue;
                });
    }

    static void applyWriteOff(long issue) {
        if (JdbcInventoryDao.countApplyWriteOff(issue) == 0)
            throw new IllegalStateException("Phiếu xuất đã xử lý.");
        for (Object[] r : JdbcInventoryDao.findApplyWriteOff(issue))
            if (JdbcInventoryDao.updateApplyWriteOff2(r[1], r[0], r[1]) != 1)
                throw new IllegalArgumentException("Lô không còn đủ tồn, không thể duyệt xuất.");
        JdbcInventoryDao.updateApplyWriteOff(issue);
        AuditService.log("Xuất kho", text(issue));
    }

    public static long stocktake(long batch, int actual, String reason) {
        require(Permission.INVENTORY_COUNT);
        if (actual < 0) throw new IllegalArgumentException("Tồn thực tế không được âm.");
        return transaction(
                c -> {
                    List<Object[]> rows = JdbcInventoryDao.findStocktake(batch);
                    if (rows.isEmpty()) throw new IllegalArgumentException("Không tìm thấy lô.");
                    int book = ((Number) rows.get(0)[0]).intValue();
                    if (book != actual && reason.isBlank())
                        throw new IllegalArgumentException("Cần lý do chênh lệch.");
                    long result =
                            JdbcInventoryDao.insertStocktake(
                                    code("KK"),
                                    accountId(),
                                    LocalDate.now().toString(),
                                    book == actual ? "APPROVED" : "PENDING");
                    JdbcInventoryDao.updateStocktake(result, batch, book, actual, reason);
                    if (book != actual)
                        ApprovalService.submitAction(
                                ApprovalService.Kind.STOCKTAKE,
                                reason,
                                "Tồn sổ " + book,
                                "Tồn thực " + actual,
                                "STOCKTAKE",
                                result,
                                "");
                    AuditService.log("Kiểm kê", text(result));
                    return result;
                });
    }

    static void applyStocktake(long count) {
        if (JdbcInventoryDao.countApplyStocktake(count) == 0)
            throw new IllegalStateException("Biên bản đã xử lý.");
        for (Object[] r : JdbcInventoryDao.findApplyStocktake(count))
            if (JdbcInventoryDao.updateApplyStocktake(r[2], r[0], r[1]) != 1)
                throw new IllegalArgumentException(
                        "Tồn đã thay đổi sau kiểm kê. Cần lập lại biên bản.");
        JdbcInventoryDao.updateApplyStocktake2(count);
    }

    public static void paySupplier(long id, long paid) {
        require(Permission.INVENTORY_RECEIVE);
        if (paid <= 0) throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        transaction(
                c -> {
                    if (JdbcInventoryDao.addSupplierPayment(paid, id, paid) != 1)
                        throw new IllegalArgumentException("Số tiền vượt công nợ còn lại.");
                    AuditService.log("Trả công nợ", id + " / " + paid);
                    return null;
                });
    }

    public static List<Object[]> purchaseDetails(long purchase) {
        SessionService.require(com.petstation.model.Permission.INVENTORY_RECEIVE);
        return JdbcInventoryDao.purchaseDetails(purchase);
    }
}
