package com.petstation.service;

import com.petstation.dao.QuyenVaiTroDAO;
import com.petstation.dao.impl.QuyenVaiTroDAOImpl;
import com.petstation.model.Quyen;
import com.petstation.model.VaiTro;

import java.util.EnumSet;
import java.util.Set;

/**
 * Điều phối nghiệp vụ và kiểm tra dữ liệu cho quyền dịch vụ.
 *
 * <p>Quyền theo vai trò (khớp database/002_sample_data.sql) để hiển thị ma trận phân quyền.
 */
public final class QuyenVaiTroService {
    private static final QuyenVaiTroDAO quyenVaiTroDAO = new QuyenVaiTroDAOImpl();

    private QuyenVaiTroService() {}

    public static Set<Quyen> tao(VaiTro vaiTro) {
        // Các quyền.
        Set<Quyen> cacQuyen = EnumSet.noneOf(Quyen.class);
        for (Object[] dong : quyenVaiTroDAO.timCua(vaiTro.name()))
            cacQuyen.add(Quyen.valueOf(String.valueOf(dong[0])));
        return cacQuyen;
    }

    public static String layNhan(Quyen vungDangXet) {
        return switch (vungDangXet) {
            case DASHBOARD_VIEW -> "Xem màn hình chính";
            case ACCOUNT_MANAGE -> "Tài khoản, nhân viên, cài đặt";
            case SCHEDULE_MANAGE -> "Phân lịch nhân viên";
            case SCHEDULE_VIEW -> "Xem lịch làm việc";
            case CUSTOMER_MANAGE -> "Khách hàng";
            case PET_MANAGE -> "Hồ sơ thú cưng";
            case PRODUCT_MANAGE -> "Sản phẩm";
            case SUPPLIER_MANAGE -> "Nhà cung cấp";
            case INVENTORY_RECEIVE -> "Lập phiếu nhập kho";
            case INVENTORY_ISSUE -> "Xuất hủy hàng hóa";
            case INVENTORY_COUNT -> "Kiểm kê";
            case PRICE_PROPOSE -> "Đề xuất giá";
            case PRICE_APPROVE -> "Duyệt giá";
            case POS_SELL -> "Lập hóa đơn, đổi hàng";
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
