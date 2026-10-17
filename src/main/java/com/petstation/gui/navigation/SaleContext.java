package com.petstation.gui.navigation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.petstation.gui.data.DemoStore;

/** Giỏ hàng/hóa đơn đang lập, dùng chung giữa Lập đơn → Thanh toán → In hóa đơn. */
public final class SaleContext {
    public record Line(String code, String name, long unitPrice, int quantity) {
        public long amount() { return unitPrice * quantity; }
    }

    public record Receipt(String invoiceNo, LocalDateTime time, List<Line> lines, long subtotal, long discount,
                          long total, ShiftState.Method method, long received, long change,
                          String customer, String voucher, String cashier) {}

    private final List<Line> lines = new ArrayList<>();
    private String customerPhone = "";
    private int discountPercent;
    private String voucherCode = "";
    private String customerCode = "";
    private String customerName = "";
    private long customerPoints;
    private long pointsUsed;
    private long voucherValue;
    private Receipt lastReceipt;
    private int sequence = 23;

    public List<Line> lines() { return List.copyOf(lines); }
    public boolean isEmpty() { return lines.isEmpty(); }
    public String customerPhone() { return customerPhone; }
    public int discountPercent() { return discountPercent; }
    public String voucherCode() { return voucherCode; }
    public Receipt lastReceipt() { return lastReceipt; }

    public void setLines(List<Line> newLines) {
        lines.clear();
        lines.addAll(newLines);
    }

    public String customerCode() { return customerCode; }
    public String customerName() { return customerName; }
    public long customerPoints() { return customerPoints; }
    public long pointsUsed() { return pointsUsed; }

    /** Chọn khách thành viên cho hóa đơn (code rỗng = khách lẻ). */
    public void setCustomer(String code, String name, long points) {
        customerCode = code == null ? "" : code;
        customerName = name == null ? "" : name;
        customerPoints = Math.max(0, points);
        pointsUsed = 0;
    }

    /** Dùng điểm: không vượt số điểm của khách và không vượt số tiền còn phải trả. */
    public void setPointsUsed(long points) {
        long value = AppSettings.vndPerRedeemedPoint();
        long maxByTotal = value <= 0 ? 0 : Math.max(0, (subtotal() - promotionDiscount() - voucherAmount()) / value);
        pointsUsed = Math.max(0, Math.min(points, Math.min(customerPoints, maxByTotal)));
    }

    public long pointsValue() { return pointsUsed * AppSettings.vndPerRedeemedPoint(); }

    /** Khuyến mãi tự áp: chọn chương trình đang áp dụng cho giá giảm lớn nhất. */
    public Promotion bestPromotion() {
        Promotion best = null;
        long sub = subtotal();
        for (Object[] r : DemoStore.promotions()) {
            if ("Ngừng áp dụng".equals(r[8]) || !inDateRange(r[6], r[7])) continue;
            if (sub < com.petstation.gui.components.Money.parse(String.valueOf(r[5]))) continue;
            long base = 0;
            String scope = String.valueOf(r[3]);
            for (Line l : lines) {
                boolean isService = l.code().startsWith("DV") || l.code().startsWith("HOTEL");
                if (scope.equals("Cả đơn") || (scope.equals("Sản phẩm") && !isService) || (scope.equals("Dịch vụ") && isService)) base += l.amount();
            }
            long value = com.petstation.gui.components.Money.parse(String.valueOf(r[4]));
            long amount = switch (String.valueOf(r[2])) {
                case "Giảm %" -> Math.round(base * value / 100.0);
                case "Giảm tiền" -> base > 0 ? Math.min(value, base) : 0;
                default -> 0;
            };
            if (amount > 0 && (best == null || amount > best.amount())) best = new Promotion(String.valueOf(r[0]), String.valueOf(r[1]), amount);
        }
        return best;
    }

    /** Ngày hôm nay nằm trong [from, to] (định dạng dd/MM/yyyy); ngày sai định dạng coi như không giới hạn. */
    private static boolean inDateRange(Object from, Object to) {
        java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
        java.time.LocalDate today = java.time.LocalDate.now();
        try {
            if (java.time.LocalDate.parse(String.valueOf(from), f).isAfter(today)) return false;
            return !java.time.LocalDate.parse(String.valueOf(to), f).isBefore(today);
        } catch (java.time.format.DateTimeParseException e) {
            return true;
        }
    }

    public record Promotion(String code, String name, long amount) {}

    public long promotionDiscount() { Promotion p = bestPromotion(); return p == null ? 0 : p.amount(); }

    public void setCustomerPhone(String phone) { this.customerPhone = phone == null ? "": phone.trim(); }

    public void setDiscountPercent(int percent) { this.discountPercent = Math.max(0, Math.min(percent, 100)); }

    public long subtotal() {
        long sum = 0;
        for (Line l : lines) sum += l.amount();
        return sum;
    }

    public long discount() {
        long byPercent = Math.round(subtotal() * discountPercent / 100.0);
        return Math.min(subtotal(), byPercent + promotionDiscount() + voucherAmount() + pointsValue());
    }

    public long total() { return subtotal() - discount(); }

    /** Áp mã voucher trong Danh mục. Trả về thông báo lỗi, hoặc null nếu hợp lệ (mã rỗng = bỏ voucher). */
    public String applyVoucher(String code) {
        String c = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        if (c.isEmpty()) {
            voucherCode = "";
            voucherValue = 0;
            return null;
        }
        for (Object[] r : DemoStore.vouchers()) {
            if (!String.valueOf(r[0]).toUpperCase(Locale.ROOT).equals(c)) continue;
            String status = String.valueOf(r[6]);
            if (status.equals("Ngừng")) return "Voucher " + c + " đã ngừng sử dụng.";
            if (!inDateRange("01/01/2000", r[2])) return "Voucher " + c + " không dùng được (hết hạn).";
            if (com.petstation.gui.components.Money.parse(String.valueOf(r[4])) >= com.petstation.gui.components.Money.parse(String.valueOf(r[3])))
                return "Voucher " + c + " không dùng được (hết lượt).";
            long min = com.petstation.gui.components.Money.parse(String.valueOf(r[5]));
            if (subtotal() < min) return "Voucher chỉ áp dụng cho đơn từ " + com.petstation.gui.components.Money.format(min) + ".";
            voucherCode = c;
            voucherValue = com.petstation.gui.components.Money.parse(String.valueOf(r[1]));
            return null;
        }
        return "Mã voucher không tồn tại.";
    }

    public long voucherAmount() {
        return voucherCode.isEmpty() ? 0 : Math.min(voucherValue, Math.max(0, subtotal() - promotionDiscount()));
    }

    public Receipt complete(ShiftState.Method method, long received, String cashier) {
        long total = total();
        long change = method == ShiftState.Method.CASH ? Math.max(0, received - total) : 0;
        String no = String.format("HD-%ty%<tm-%03d", LocalDateTime.now(), ++sequence);
        lastReceipt = new Receipt(no, LocalDateTime.now(), List.copyOf(lines), subtotal(), discount(), total,
            method, method == ShiftState.Method.CASH ? received : total, change,
            customerName.isEmpty() ? "Khách lẻ" : customerName, voucherCode, cashier);
        applySideEffects(total);
        return lastReceipt;
    }

    /** Voucher dùng 1 lần thì đánh dấu đã dùng; trừ điểm đã dùng và cộng điểm mới cho khách thành viên. */
    private void applySideEffects(long total) {
        // Trừ tồn kho theo số lượng đã bán (không để âm)
        for (Line l : lines) {
            for (Object[] r : DemoStore.products()) {
                if (String.valueOf(r[0]).equals(l.code())) {
                    long stock = com.petstation.gui.components.Money.parse(String.valueOf(r[8]));
                    r[8] = String.valueOf(Math.max(0, stock - l.quantity()));
                }
            }
        }
        if (!voucherCode.isEmpty()) {
            for (Object[] r : DemoStore.vouchers()) {
                if (String.valueOf(r[0]).equalsIgnoreCase(voucherCode)) {
                    r[4] = String.valueOf(com.petstation.gui.components.Money.parse(String.valueOf(r[4])) + 1);
                    long qty = com.petstation.gui.components.Money.parse(String.valueOf(r[3]));
                    if (com.petstation.gui.components.Money.parse(String.valueOf(r[4])) >= qty) r[6] = "Hết lượt";
                }
            }
        }
        if (!customerCode.isEmpty()) {
            long earned = AppSettings.vndPerEarnedPoint() <= 0 ? 0 : total / AppSettings.vndPerEarnedPoint();
            for (Object[] r : DemoStore.customers()) {
                if (String.valueOf(r[0]).equals(customerCode)) {
                    long now = com.petstation.gui.components.Money.parse(String.valueOf(r[7])) - pointsUsed + earned;
                    r[7] = String.valueOf(Math.max(0, now));
                }
            }
        }
        AuditTrail.log("Thanh toán", "Hóa đơn " + lastReceipt.invoiceNo() + " – " + com.petstation.gui.components.Money.format(total));
    }

    public void clear() {
        lines.clear();
        customerPhone = "";
        discountPercent = 0;
        voucherCode = "";
        voucherValue = 0;
        customerCode = "";
        customerName = "";
        customerPoints = 0;
        pointsUsed = 0;
    }
}
