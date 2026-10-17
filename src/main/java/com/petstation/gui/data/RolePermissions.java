package com.petstation.gui.data;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.petstation.model.Permission;
import com.petstation.model.Role;

/** Quyền theo vai trò (khớp database/003_seed_reference_data.sql) để hiển thị ma trận phân quyền. */
public final class RolePermissions {
    private static final Map<Role, Set<Permission>> MAP = new EnumMap<>(Role.class);

    static {
        MAP.put(Role.STORE_MANAGER, EnumSet.allOf(Permission.class));
        MAP.put(Role.WAREHOUSE_STAFF, EnumSet.of(Permission.DASHBOARD_VIEW, Permission.SCHEDULE_VIEW, Permission.PRODUCT_MANAGE,
            Permission.SUPPLIER_MANAGE, Permission.INVENTORY_RECEIVE, Permission.INVENTORY_ISSUE, Permission.INVENTORY_COUNT));
        MAP.put(Role.SALES_STAFF, EnumSet.of(Permission.DASHBOARD_VIEW, Permission.SCHEDULE_VIEW, Permission.CUSTOMER_MANAGE,
            Permission.PET_MANAGE, Permission.POS_SELL, Permission.PAYMENT_PROCESS, Permission.CASH_SHIFT_MANAGE,
            Permission.REPORT_VIEW_OWN_SHIFT));
        MAP.put(Role.SPA_HOTEL_STAFF, EnumSet.of(Permission.DASHBOARD_VIEW, Permission.SCHEDULE_VIEW, Permission.CUSTOMER_MANAGE,
            Permission.PET_MANAGE, Permission.SPA_MANAGE, Permission.HOTEL_MANAGE,
            Permission.POS_SELL, Permission.PAYMENT_PROCESS, Permission.CASH_SHIFT_MANAGE));
    }

    private RolePermissions() {}

    public static Set<Permission> of(Role role) { return MAP.get(role); }

    public static String label(Permission p) {
        return switch (p) {
            case DASHBOARD_VIEW -> "Xem màn hình chính";
            case ACCOUNT_MANAGE -> "Tài khoản, nhân viên, cài đặt";
            case SCHEDULE_MANAGE -> "Phân lịch nhân viên";
            case SCHEDULE_VIEW -> "Xem lịch làm việc";
            case CUSTOMER_MANAGE -> "Khách hàng";
            case PET_MANAGE -> "Hồ sơ thú cưng";
            case PRODUCT_MANAGE -> "Sản phẩm";
            case SUPPLIER_MANAGE -> "Nhà cung cấp";
            case INVENTORY_RECEIVE -> "Nhập kho";
            case INVENTORY_ISSUE -> "Xuất kho / Xuất hủy";
            case INVENTORY_COUNT -> "Kiểm kê";
            case PRICE_PROPOSE -> "Đề xuất giá";
            case PRICE_APPROVE -> "Duyệt giá";
            case POS_SELL -> "Lập hóa đơn, đổi trả";
            case PAYMENT_PROCESS -> "Thanh toán";
            case CASH_SHIFT_MANAGE -> "Mở ca / Kết ca";
            case PROMOTION_MANAGE -> "Khuyến mãi, tích điểm, voucher";
            case SPA_MANAGE -> "Dịch vụ Spa";
            case HOTEL_MANAGE -> "Pet Hotel";
            case APPROVAL_PROCESS -> "Phê duyệt yêu cầu";
            case AUDIT_VIEW -> "Nhật ký hệ thống";
            case REPORT_VIEW_ALL -> "Thống kê toàn cửa hàng";
            case REPORT_VIEW_OWN_SHIFT -> "Thống kê ca của mình";
        };
    }
}
