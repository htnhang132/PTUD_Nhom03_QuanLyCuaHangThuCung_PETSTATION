package com.petstation.service;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import com.petstation.dao.impl.JdbcCatalogDao;
import com.petstation.model.CatalogType;
import com.petstation.model.Permission;

import java.util.*;

/** Adapts SQL rows to the existing catalog forms. SQL Server is the source of truth. */
public final class CatalogService {
    public static final class Rows extends ArrayList<Object[]> {
        public final CatalogType kind;

        Rows(CatalogType kind) {
            this.kind = kind;
            refresh();
        }

        public void refresh() {
            super.clear();
            super.addAll(JdbcCatalogDao.load(kind));
        }
    }

    private static final List<Runnable> PRICE_LISTENERS = new ArrayList<>();

    private CatalogService() {}

    public static Rows customers() {
        return new Rows(CatalogType.CUSTOMERS);
    }

    public static Rows pets() {
        return new Rows(CatalogType.PETS);
    }

    public static Rows staff() {
        return new Rows(CatalogType.STAFF);
    }

    public static Rows accounts() {
        return new Rows(CatalogType.ACCOUNTS);
    }

    public static Rows products() {
        return new Rows(CatalogType.PRODUCTS);
    }

    public static Rows suppliers() {
        return new Rows(CatalogType.SUPPLIERS);
    }

    public static Rows promotions() {
        return new Rows(CatalogType.PROMOTIONS);
    }

    public static Rows vouchers() {
        return new Rows(CatalogType.VOUCHERS);
    }

    public static Rows spaServices() {
        return new Rows(CatalogType.SPA);
    }

    public static Rows rooms() {
        return new Rows(CatalogType.ROOMS);
    }

    public static List<String> customerOptions() {
        return customers().stream()
                .filter(r -> "Đang hoạt động".equals(r[8]))
                .map(r -> r[0] + " - " + r[1])
                .toList();
    }

    public static List<String> staffOptions() {
        return staff().stream()
                .filter(r -> !"Nghỉ việc".equals(r[6]))
                .map(r -> r[0] + " - " + r[1])
                .toList();
    }

    public static String codeOf(Object option) {
        return text(option).split(" - ", 2)[0];
    }

    public static void addPriceListener(Runnable listener) {
        PRICE_LISTENERS.add(listener);
    }

    public static void save(List<Object[]> rows, Object[] before, Object[] value) {
        if (!(rows instanceof Rows catalog))
            throw new IllegalArgumentException("Danh mục chưa nối SQL Server.");
        Permission permission =
                switch (catalog.kind) {
                    case CUSTOMERS, VOUCHERS -> Permission.CUSTOMER_MANAGE;
                    case PETS -> Permission.PET_MANAGE;
                    case STAFF, ACCOUNTS -> Permission.ACCOUNT_MANAGE;
                    case PRODUCTS -> Permission.PRODUCT_MANAGE;
                    case SUPPLIERS -> Permission.SUPPLIER_MANAGE;
                    case PROMOTIONS -> Permission.PROMOTION_MANAGE;
                    case SPA -> Permission.SPA_MANAGE;
                    case ROOMS -> Permission.HOTEL_MANAGE;
                };
        require(permission);

        transaction(
                c -> {
                    JdbcCatalogDao.save(
                            catalog.kind, before, value, accountId(), session().role().name());
                    AuditService.log(
                            before == null ? "Thêm" : "Sửa", catalog.kind + " " + value[0]);
                    return null;
                });
        if (catalog.kind == CatalogType.PRODUCTS)
            for (Runnable listener : List.copyOf(PRICE_LISTENERS)) listener.run();
    }

    public static List<Object[]> priceHistory() {
        return JdbcCatalogDao.priceHistory();
    }

    public static long id(String table, String key, String code) {
        return JdbcCatalogDao.id(table, key, code);
    }

    public static int lockAccountsOf(String staffCode) {
        require(Permission.ACCOUNT_MANAGE);
        return JdbcCatalogDao.lockAccountsOf(staffCode);
    }

    public static void resetPassword(String username, String password) {
        require(Permission.ACCOUNT_MANAGE);
        JdbcCatalogDao.resetPassword(username, password);
    }
}
