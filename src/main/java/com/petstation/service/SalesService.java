package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.WorkflowSupport.*;

import com.petstation.dao.impl.JdbcSalesDao;
import com.petstation.model.Permission;
import com.petstation.model.SaleContext;

import java.time.*;
import java.util.*;

/** Atomic payment, FEFO stock allocation, voucher usage and loyalty ledger. */
public final class SalesService {
    private SalesService() {}

    public static SaleContext.Receipt pay(
            SaleContext sale, CashShiftService.Method method, long received, String cashier) {
        require(Permission.PAYMENT_PROCESS);
        return transaction(
                c -> {
                    Object existing = JdbcSalesDao.getPay(sale.paymentKey());
                    if (existing != null) return receipt(text(existing));
                    if (sale.isEmpty()) throw new IllegalArgumentException("Hóa đơn chưa có hàng.");
                    long shift = JdbcSalesDao.countPay(accountId());
                    if (shift == 0)
                        throw new IllegalStateException("Cần mở ca trước khi thanh toán.");
                    // The shift lock serializes simultaneous attempts from the same account.
                    existing = JdbcSalesDao.getPay2(sale.paymentKey());
                    if (existing != null) return receipt(text(existing));
                    if (JdbcSalesDao.countPay2() > 0)
                        throw new IllegalStateException("Ngày đã chốt, không thể ghi giao dịch.");
                    Long customer =
                            sale.customerCode().isBlank()
                                    ? null
                                    : CatalogService.id(
                                            "khach_hang", "ma_khach_hang", sale.customerCode());
                    if (customer != null) {
                        List<Object[]> customers = JdbcSalesDao.findPay(customer);
                        if (customers.isEmpty() || !Boolean.TRUE.equals(customers.get(0)[1]))
                            throw new IllegalArgumentException("Khách hàng đã ngừng hoạt động.");
                        if (((Number) customers.get(0)[0]).longValue() < sale.pointsUsed())
                            throw new IllegalArgumentException(
                                    "Điểm khách hàng đã thay đổi, hãy chọn lại số điểm.");
                    } else if (sale.pointsUsed() > 0)
                        throw new IllegalArgumentException("Chưa chọn khách hàng để dùng điểm.");
                    if (!sale.voucherCode().isBlank()) {
                        String error = sale.applyVoucher(sale.voucherCode());
                        if (error != null) throw new IllegalArgumentException(error);
                        if (JdbcSalesDao.updatePay2(
                                        sale.voucherCode(),
                                        sale.subtotal(),
                                        customer,
                                        sale.sourceType())
                                != 1)
                            throw new IllegalArgumentException(
                                    "Voucher đã hết hạn, hết lượt hoặc không áp dụng cho hóa đơn"
                                            + " này.");
                    }
                    long total = sale.total();
                    if (total < 0 || method == CashShiftService.Method.CASH && received < total)
                        throw new IllegalArgumentException("Tiền khách đưa chưa đủ.");
                    String no = code("HD");
                    LocalDateTime now = LocalDateTime.now();
                    long order = JdbcSalesDao.insertPay(code("DH"), shift, customer, now);
                    for (SaleContext.Line line : sale.lines()) {
                        if (line.quantity() <= 0 || line.unitPrice() < 0)
                            throw new IllegalArgumentException(
                                    "Số lượng hoặc đơn giá không hợp lệ.");
                        Long product = null;
                        if (sale.sourceType().isBlank()) {
                            List<Object[]> products = JdbcSalesDao.findPay2(line.code());
                            if (products.isEmpty() || !"SELLING".equals(products.get(0)[2]))
                                throw new IllegalArgumentException(
                                        "Sản phẩm " + line.code() + " không còn bán.");
                            product = ((Number) products.get(0)[0]).longValue();
                            if (((Number) products.get(0)[1]).longValue() != line.unitPrice())
                                throw new IllegalArgumentException(
                                        "Giá sản phẩm "
                                                + line.code()
                                                + " đã đổi. Hãy nạp lại giỏ hàng.");
                        }
                        long item =
                                JdbcSalesDao.insertPay2(
                                        order,
                                        product,
                                        product == null ? line.code() : null,
                                        line.name(),
                                        line.quantity(),
                                        line.unitPrice());
                        if (product != null) {
                            int remaining = line.quantity();
                            for (Object[] batch : JdbcSalesDao.findPay3(product)) {
                                int qty = Math.min(remaining, ((Number) batch[1]).intValue());
                                if (qty <= 0) continue;
                                if (JdbcSalesDao.updatePay3(qty, batch[0], qty) != 1)
                                    throw new IllegalStateException("Tồn kho vừa thay đổi.");
                                JdbcSalesDao.updatePay4(item, batch[0], qty);
                                remaining -= qty;
                                if (remaining == 0) break;
                            }
                            if (remaining > 0)
                                throw new IllegalArgumentException(
                                        "Không đủ tồn kho còn hạn cho " + line.name() + ".");
                        }
                    }
                    long earned =
                            customer == null ? 0 : total / SettingsService.vndPerEarnedPoint();
                    long invoice =
                            JdbcSalesDao.insertPay3(
                                    no,
                                    order,
                                    total,
                                    method.name(),
                                    method == CashShiftService.Method.CASH ? received : total,
                                    method == CashShiftService.Method.CASH ? received - total : 0,
                                    earned,
                                    sale.paymentKey(),
                                    now,
                                    sale.subtotal(),
                                    sale.discount(),
                                    sale.pointsUsed(),
                                    sale.voucherCode());
                    if (customer != null) {
                        JdbcSalesDao.updatePay(sale.pointsUsed(), earned, customer);
                        JdbcSalesDao.updatePay5(invoice, earned - sale.pointsUsed(), customer);
                        updateTier(customer);
                    }
                    if ("SPA".equals(sale.sourceType())) {
                        long amount = JdbcSalesDao.countPay3(sale.sourceId());
                        if (amount != sale.subtotal())
                            throw new IllegalStateException(
                                    "Phiếu Spa chưa hoàn thành hoặc số tiền đã thay đổi.");
                        if (JdbcSalesDao.updatePay6(invoice, sale.sourceId()) != 1)
                            throw new IllegalStateException("Phiếu Spa đã thanh toán.");
                    } else if ("HOTEL".equals(sale.sourceType())) {
                        long expected = HotelService.hotelBalance(sale.sourceId());
                        if (expected != sale.subtotal())
                            throw new IllegalStateException(
                                    "Quyết toán Hotel đã thay đổi, hãy nạp lại.");
                        if (JdbcSalesDao.updatePay7(invoice, sale.sourceId()) != 1)
                            throw new IllegalStateException(
                                    "Hợp đồng đã trả phòng hoặc chưa check-in.");
                        JdbcSalesDao.updatePay8(sale.sourceId());
                    }
                    AuditService.log("Thanh toán", "Hóa đơn " + no + ": " + total);
                    return receipt(no);
                });
    }

    static void updateTier(long customer) {
        long spend = JdbcSalesDao.countUpdateTier(customer);
        long[] tiers = SettingsService.tierSpend();
        String[] names = {"Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
        int rank = 0;
        while (rank < tiers.length && spend >= tiers[rank]) rank++;
        JdbcSalesDao.updateUpdateTier(names[rank], customer);
    }

    public static SaleContext.Receipt receipt(String no) {
        List<Object[]> rows = JdbcSalesDao.findReceipt(no);
        if (rows.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hóa đơn " + no);
        Object[] r = rows.get(0);
        List<SaleContext.Line> lines = new ArrayList<>();
        for (Object[] l : JdbcSalesDao.findReceipt2(r[1]))
            lines.add(
                    new SaleContext.Line(
                            text(l[0]),
                            text(l[1]),
                            ((Number) l[2]).longValue(),
                            ((Number) l[3]).intValue()));
        return new SaleContext.Receipt(
                no,
                ((java.sql.Timestamp) r[0]).toLocalDateTime(),
                lines,
                ((Number) r[2]).longValue(),
                ((Number) r[3]).longValue(),
                ((Number) r[4]).longValue(),
                CashShiftService.Method.valueOf(text(r[5])),
                r[6] == null ? 0 : ((Number) r[6]).longValue(),
                r[7] == null ? 0 : ((Number) r[7]).longValue(),
                text(r[8]),
                text(r[9]),
                text(r[10]));
    }

    public static long hold(SaleContext sale) {
        require(Permission.POS_SELL);
        if (sale.isEmpty()) throw new IllegalArgumentException("Giỏ hàng trống.");
        return transaction(
                c -> {
                    long id =
                            JdbcSalesDao.insertHold(
                                    code("GIO"),
                                    accountId(),
                                    sale.customerCode().isBlank()
                                            ? null
                                            : CatalogService.id(
                                                    "khach_hang",
                                                    "ma_khach_hang",
                                                    sale.customerCode()));
                    for (SaleContext.Line line : sale.lines())
                        JdbcSalesDao.updateHold(
                                id, line.code(), line.name(), line.unitPrice(), line.quantity());
                    return id;
                });
    }

    public static void resume(long id, SaleContext sale) {
        require(Permission.POS_SELL);
        transaction(
                c -> {
                    if (JdbcSalesDao.countResume2(id, accountId()) == 0)
                        throw new IllegalArgumentException("Không tìm thấy đơn treo của bạn.");
                    List<SaleContext.Line> lines = new ArrayList<>();
                    for (Object[] r : JdbcSalesDao.findResume(id))
                        lines.add(
                                new SaleContext.Line(
                                        text(r[0]),
                                        text(r[1]),
                                        ((Number) r[2]).longValue(),
                                        ((Number) r[3]).intValue()));
                    if (lines.size() != JdbcSalesDao.countResume(id))
                        throw new IllegalArgumentException("Đơn treo có sản phẩm đã ngừng bán.");
                    sale.clear();
                    sale.setLines(lines);
                    List<Object[]> customers = JdbcSalesDao.findResume2(id);
                    if (!customers.isEmpty()) {
                        Object[] k = customers.get(0);
                        sale.setCustomer(text(k[0]), text(k[1]), ((Number) k[2]).longValue());
                    }
                    deleteHold(id);
                    return null;
                });
    }

    public static void deleteHold(long id) {
        require(Permission.POS_SELL);
        transaction(
                c -> {
                    if (JdbcSalesDao.countDeleteHold(id, accountId()) == 0)
                        throw new IllegalArgumentException("Đơn treo không thuộc tài khoản này.");
                    JdbcSalesDao.updateDeleteHold(id);
                    JdbcSalesDao.updateDeleteHold2(id, accountId());
                    return null;
                });
    }

    public static void checkout(String kind, long id, SaleContext sale) {
        List<SaleContext.Line> lines = new ArrayList<>();
        Object customer;
        if (kind.equals("SPA")) {
            require(Permission.SPA_MANAGE);
            if (JdbcSalesDao.countCheckout(id) == 0)
                throw new IllegalArgumentException("Phiếu Spa chưa hoàn thành hoặc đã thanh toán.");
            for (Object[] r : JdbcSalesDao.findCheckout2(id))
                lines.add(
                        new SaleContext.Line(
                                text(r[0]), text(r[1]), value(r[2]), ((Number) r[3]).intValue()));
            customer = JdbcSalesDao.getCheckout2(id);
        } else if (kind.equals("HOTEL")) {
            require(Permission.HOTEL_MANAGE);
            long balance = HotelService.hotelBalance(id);
            lines.add(
                    new SaleContext.Line(
                            "HOTEL-ROOM", "Quyết toán Hotel (đã trừ cọc)", balance, 1));
            customer = JdbcSalesDao.getCheckout(id);
        } else throw new IllegalArgumentException("Nguồn thanh toán không hợp lệ.");
        sale.clear();
        sale.setLines(lines);
        sale.setSource(kind, id);
        List<Object[]> customers = JdbcSalesDao.findCheckout(customer);
        if (!customers.isEmpty()) {
            Object[] r = customers.get(0);
            sale.setCustomer(text(r[0]), text(r[1]), value(r[2]));
        }
    }

    static void cancelInvoice(long invoice) {
        List<Object[]> invoices = JdbcSalesDao.findCancelInvoice(invoice);
        if (invoices.isEmpty()) throw new IllegalArgumentException("Hóa đơn đã hủy.");
        if (JdbcSalesDao.countCancelInvoice3(invoice) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn có phiếu trả hàng, cần xử lý đổi trả thay vì hủy.");
        if (JdbcSalesDao.countCancelInvoice(invoice) > 0
                || JdbcSalesDao.countCancelInvoice2(invoice) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn dịch vụ đã hoàn thành không thể hủy trực tiếp.");
        Object[] h = invoices.get(0);
        for (Object[] batch : JdbcSalesDao.findCancelInvoice2(h[0]))
            JdbcSalesDao.updateCancelInvoice(batch[1], batch[0]);
        if (h[4] != null) {
            long old = JdbcSalesDao.countCancelInvoice4(h[4]),
                    next = Math.max(0, old - value(h[1]) + value(h[2]));
            JdbcSalesDao.updateCancelInvoice2(next, h[4]);
            JdbcSalesDao.updateCancelInvoice5(h[4], invoice, next - old, next);
        }
        if (!text(h[3]).isBlank()) JdbcSalesDao.updateCancelInvoice6(h[3]);
        JdbcSalesDao.updateCancelInvoice3(invoice);
        JdbcSalesDao.updateCancelInvoice4(h[0]);
        if (h[4] != null) SalesService.updateTier(value(h[4]));
        AuditService.log("Hủy hóa đơn", text(invoice));
    }

    public static List<Object[]> sellableProducts() {
        SessionService.require(com.petstation.model.Permission.POS_SELL);
        return JdbcSalesDao.sellableProducts();
    }
}
