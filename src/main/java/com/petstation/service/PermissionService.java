package com.petstation.service;

import com.petstation.dao.impl.JdbcPermissionDao;
import com.petstation.model.Permission;
import com.petstation.model.Role;

import java.util.EnumSet;
import java.util.Set;

/** Quyền theo vai trò (khớp database/002_sample_data.sql) để hiển thị ma trận phân quyền. */
public final class PermissionService {
    private PermissionService() {}

    public static Set<Permission> of(Role role) {
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);
        for (Object[] row : JdbcPermissionDao.findOf(role.name()))
            permissions.add(Permission.valueOf(String.valueOf(row[0])));
        return permissions;
    }

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
