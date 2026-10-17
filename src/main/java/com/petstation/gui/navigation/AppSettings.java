package com.petstation.gui.navigation;

/**
 * Cài đặt nghiệp vụ dùng chung cho các màn (dữ liệu tạm trong phiên, chưa lưu database).
 * Chỉ Cửa hàng trưởng được sửa ở màn Cài đặt; các màn khác chỉ đọc.
 */
public final class AppSettings {
    private static int vatPercent = 8;
    private static int lowStockThreshold = 10;
    private static int expiryWarnDays = 30;
    private static long lateCheckoutFeePerDay = 50_000;
    private static int returnWindowDays = 3;
    private static long writeOffApprovalLimit = 500_000;

    // Tích điểm và hạng thành viên
    private static long vndPerEarnedPoint = 10_000;   // chi bao nhiêu đồng được 1 điểm
    private static long vndPerRedeemedPoint = 1_000;  // 1 điểm đổi được bao nhiêu đồng
    private static long[] tierSpend = {2_000_000, 10_000_000, 25_000_000, 50_000_000}; // Bạc, Vàng, Bạch kim, Kim cương

    private AppSettings() {}

    public static long vndPerEarnedPoint() { return vndPerEarnedPoint; }
    public static long vndPerRedeemedPoint() { return vndPerRedeemedPoint; }
    public static long[] tierSpend() { return tierSpend.clone(); }

    public static void updateLoyalty(long earn, long redeem, long[] tiers) {
        vndPerEarnedPoint = earn;
        vndPerRedeemedPoint = redeem;
        tierSpend = tiers.clone();
    }

    public static int vatPercent() { return vatPercent; }
    public static int lowStockThreshold() { return lowStockThreshold; }
    public static int expiryWarnDays() { return expiryWarnDays; }
    public static long lateCheckoutFeePerDay() { return lateCheckoutFeePerDay; }
    public static int returnWindowDays() { return returnWindowDays; }
    public static long writeOffApprovalLimit() { return writeOffApprovalLimit; }

    public static void update(int vat, int lowStock, int expiryDays, long lateFee, int returnDays, long writeOffLimit) {
        vatPercent = vat;
        lowStockThreshold = lowStock;
        expiryWarnDays = expiryDays;
        lateCheckoutFeePerDay = lateFee;
        returnWindowDays = returnDays;
        writeOffApprovalLimit = writeOffLimit;
    }
}
