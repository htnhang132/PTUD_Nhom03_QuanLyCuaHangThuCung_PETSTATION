package com.petstation.model;

import com.petstation.service.CashShiftService;
import com.petstation.service.CatalogService;
import com.petstation.service.SettingsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Giỏ hàng/hóa đơn đang lập, dùng chung giữa Lập đơn → Thanh toán → In hóa đơn. */
public final class SaleContext {
    public record Line(String code, String name, long unitPrice, int quantity) {
        public Line {
            if (code == null || code.isBlank() || name == null || unitPrice < 0 || quantity <= 0)
                throw new IllegalArgumentException("Dòng hàng không hợp lệ.");
        }

        public long amount() {
            return unitPrice * quantity;
        }
    }

    public record Receipt(
            String invoiceNo,
            LocalDateTime time,
            List<Line> lines,
            long subtotal,
            long discount,
            long total,
            CashShiftService.Method method,
            long received,
            long change,
            String customer,
            String voucher,
            String cashier) {}

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
    private String paymentKey =
            "PAY-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    private String sourceType = "";
    private long sourceId;

    public List<Line> lines() {
        return List.copyOf(lines);
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public String customerPhone() {
        return customerPhone;
    }

    public int discountPercent() {
        return discountPercent;
    }

    public String voucherCode() {
        return voucherCode;
    }

    public Receipt lastReceipt() {
        return lastReceipt;
    }

    public void setLastReceipt(Receipt receipt) {
        lastReceipt = receipt;
    }

    public String paymentKey() {
        return paymentKey;
    }

    public String sourceType() {
        return sourceType;
    }

    public long sourceId() {
        return sourceId;
    }

    public void setSource(String type, long id) {
        sourceType = type;
        sourceId = id;
    }

    public void setLines(List<Line> newLines) {
        lines.clear();
        lines.addAll(newLines);
    }

    public String customerCode() {
        return customerCode;
    }

    public String customerName() {
        return customerName;
    }

    public long customerPoints() {
        return customerPoints;
    }

    public long pointsUsed() {
        return pointsUsed;
    }

    /** Chọn khách thành viên cho hóa đơn (code rỗng = khách lẻ). */
    public void setCustomer(String code, String name, long points) {
        customerCode = code == null ? "" : code;
        customerName = name == null ? "" : name;
        customerPoints = Math.max(0, points);
        pointsUsed = 0;
    }

    /** Dùng điểm: không vượt số điểm của khách và không vượt số tiền còn phải trả. */
    public void setPointsUsed(long points) {
        long value = SettingsService.vndPerRedeemedPoint();
        long maxByTotal =
                value <= 0
                        ? 0
                        : Math.max(0, (subtotal() - promotionDiscount() - voucherAmount()) / value);
        pointsUsed = Math.max(0, Math.min(points, Math.min(customerPoints, maxByTotal)));
    }

    public long pointsValue() {
        return pointsUsed * SettingsService.vndPerRedeemedPoint();
    }

    /** Khuyến mãi tự áp: chọn chương trình đang áp dụng cho giá giảm lớn nhất. */
    public Promotion bestPromotion() {
        Promotion best = null;
        long sub = subtotal();
        for (Object[] r : CatalogService.promotions()) {
            if ("Ngừng áp dụng".equals(r[8]) || !inDateRange(r[6], r[7])) continue;
            if (sub < com.petstation.util.Money.parse(String.valueOf(r[5]))) continue;
            long base = 0;
            String scope = String.valueOf(r[3]);
            for (Line l : lines) {
                boolean isService = l.code().startsWith("DV") || l.code().startsWith("HOTEL");
                if (scope.equals("Cả đơn")
                        || (scope.equals("Sản phẩm") && !isService)
                        || (scope.equals("Dịch vụ") && isService)) base += l.amount();
            }
            long value = com.petstation.util.Money.parse(String.valueOf(r[4]));
            long amount =
                    switch (String.valueOf(r[2])) {
                        case "Giảm %" -> Math.round(base * value / 100.0);
                        case "Giảm tiền" -> base > 0 ? Math.min(value, base) : 0;
                        default -> 0;
                    };
            if (amount > 0 && (best == null || amount > best.amount()))
                best = new Promotion(String.valueOf(r[0]), String.valueOf(r[1]), amount);
        }
        return best;
    }

    /**
     * Ngày hôm nay nằm trong [from, to] (định dạng dd/MM/yyyy); ngày sai định dạng không áp dụng.
     */
    private static boolean inDateRange(Object from, Object to) {
        java.time.format.DateTimeFormatter f =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        java.time.LocalDate today = java.time.LocalDate.now();
        try {
            if (java.time.LocalDate.parse(String.valueOf(from), f).isAfter(today)) return false;
            return !java.time.LocalDate.parse(String.valueOf(to), f).isBefore(today);
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    public record Promotion(String code, String name, long amount) {}

    public long promotionDiscount() {
        Promotion p = bestPromotion();
        return p == null ? 0 : p.amount();
    }

    public void setCustomerPhone(String phone) {
        this.customerPhone = phone == null ? "" : phone.trim();
    }

    public void setDiscountPercent(int percent) {
        this.discountPercent = Math.max(0, Math.min(percent, 100));
    }

    public long subtotal() {
        long sum = 0;
        for (Line l : lines) sum += l.amount();
        return sum;
    }

    public long discount() {
        long byPercent = Math.round(subtotal() * discountPercent / 100.0);
        return Math.min(
                subtotal(), byPercent + promotionDiscount() + voucherAmount() + pointsValue());
    }

    public long total() {
        return subtotal() - discount();
    }

    /**
     * Áp mã voucher trong Danh mục. Trả về thông báo lỗi, hoặc null nếu hợp lệ (mã rỗng = bỏ
     * voucher).
     */
    public String applyVoucher(String code) {
        String c = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        if (c.isEmpty()) {
            voucherCode = "";
            voucherValue = 0;
            return null;
        }
        for (Object[] r : CatalogService.vouchers()) {
            if (!String.valueOf(r[0]).toUpperCase(Locale.ROOT).equals(c)) continue;
            String status = String.valueOf(r[6]);
            if (status.equals("Ngừng")) return "Voucher " + c + " đã ngừng sử dụng.";
            if (!inDateRange("01/01/2000", r[2]))
                return "Voucher " + c + " không dùng được (hết hạn).";
            if (com.petstation.util.Money.parse(String.valueOf(r[4]))
                    >= com.petstation.util.Money.parse(String.valueOf(r[3])))
                return "Voucher " + c + " không dùng được (hết lượt).";
            long min = com.petstation.util.Money.parse(String.valueOf(r[5]));
            if (subtotal() < min)
                return "Voucher chỉ áp dụng cho đơn từ "
                        + com.petstation.util.Money.format(min)
                        + ".";
            voucherCode = c;
            voucherValue = com.petstation.util.Money.parse(String.valueOf(r[1]));
            return null;
        }
        return "Mã voucher không tồn tại.";
    }

    public long voucherAmount() {
        return voucherCode.isEmpty()
                ? 0
                : Math.min(voucherValue, Math.max(0, subtotal() - promotionDiscount()));
    }

    public Receipt complete(CashShiftService.Method method, long received, String cashier) {
        lastReceipt = com.petstation.service.SalesService.pay(this, method, received, cashier);
        return lastReceipt;
    }

    public void clear() {
        paymentKey =
                "PAY-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        sourceType = "";
        sourceId = 0;
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
