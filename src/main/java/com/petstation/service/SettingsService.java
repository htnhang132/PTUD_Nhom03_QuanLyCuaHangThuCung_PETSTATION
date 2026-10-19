package com.petstation.service;

import static com.petstation.dao.SqlDatabase.text;
import static com.petstation.dao.SqlDatabase.transaction;
import static com.petstation.service.SessionService.*;
import static com.petstation.service.SessionService.accountId;
import static com.petstation.service.SessionService.require;

import com.petstation.dao.impl.JdbcSettingsDao;
import com.petstation.model.Permission;

import java.util.Arrays;

/** Business settings persisted in SQL Server. */
public final class SettingsService {
    private SettingsService() {}

    public static String get(String key) {
        Object v = JdbcSettingsDao.findValue(key);
        if (v == null)
            throw new IllegalStateException(
                    "Thiếu cài đặt " + key + ". Chạy database/002_sample_data.sql.");
        return text(v);
    }

    public static void put(String key, String value) {
        require(Permission.ACCOUNT_MANAGE);
        transaction(
                c -> {
                    if (JdbcSettingsDao.updateValue(value, accountId(), key) == 0)
                        JdbcSettingsDao.insertValue(key, value, accountId());
                    return null;
                });
    }

    private static int integer(String k) {
        return Integer.parseInt(get(k));
    }

    private static long amount(String k) {
        return Long.parseLong(get(k));
    }

    public static long vndPerEarnedPoint() {
        return amount("VND_PER_EARNED_POINT");
    }

    public static long vndPerRedeemedPoint() {
        return amount("VND_PER_REDEEMED_POINT");
    }

    public static long[] tierSpend() {
        return Arrays.stream(get("TIER_SPEND").split(",")).mapToLong(Long::parseLong).toArray();
    }

    public static int vatPercent() {
        return integer("VAT_PERCENT");
    }

    public static int lowStockThreshold() {
        return integer("LOW_STOCK_THRESHOLD");
    }

    public static int expiryWarnDays() {
        return integer("EXPIRY_WARN_DAYS");
    }

    public static long lateCheckoutFeePerDay() {
        return amount("LATE_CHECKOUT_FEE");
    }

    public static int returnWindowDays() {
        return integer("RETURN_WINDOW_DAYS");
    }

    public static long writeOffApprovalLimit() {
        return amount("WRITEOFF_APPROVAL_LIMIT");
    }

    public static void updateLoyalty(long earn, long redeem, long[] tiers) {
        if (earn <= 0 || redeem < 0 || tiers.length != 4)
            throw new IllegalArgumentException("Quy tắc điểm không hợp lệ.");
        for (int i = 0; i < tiers.length; i++)
            if (tiers[i] < 0 || i > 0 && tiers[i] <= tiers[i - 1])
                throw new IllegalArgumentException("Ngưỡng hạng phải tăng dần.");
        transaction(
                c -> {
                    put("VND_PER_EARNED_POINT", "" + earn);
                    put("VND_PER_REDEEMED_POINT", "" + redeem);
                    put(
                            "TIER_SPEND",
                            String.join(
                                    ",", Arrays.stream(tiers).mapToObj(Long::toString).toList()));
                    return null;
                });
    }

    public static void update(int vat, int low, int days, long late, int returns, long limit) {
        if (vat < 0 || vat > 100 || low < 0 || days < 0 || late < 0 || returns < 0 || limit < 0)
            throw new IllegalArgumentException("Cài đặt không hợp lệ.");
        transaction(
                c -> {
                    put("VAT_PERCENT", "" + vat);
                    put("LOW_STOCK_THRESHOLD", "" + low);
                    put("EXPIRY_WARN_DAYS", "" + days);
                    put("LATE_CHECKOUT_FEE", "" + late);
                    put("RETURN_WINDOW_DAYS", "" + returns);
                    put("WRITEOFF_APPROVAL_LIMIT", "" + limit);
                    return null;
                });
    }
}
