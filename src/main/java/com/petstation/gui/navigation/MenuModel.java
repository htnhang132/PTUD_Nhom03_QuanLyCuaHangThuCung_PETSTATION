package com.petstation.gui.navigation;

import java.util.List;

/**
 * Cấu trúc menu theo hướng chức năng: Hệ thống, Danh mục, Tìm kiếm, Xử lý, Thống kê.
 * Tách khỏi Swing để kiểm thử được.
 */
public final class MenuModel {
    public record Item(String label, ScreenId screen, String shortcut) {
        public boolean isSeparator() { return screen == null; }
    }

    public record Group(char mnemonic, String label, List<Item> items) {}

    private static final Item SEP = new Item(null, null, null);

    private static Item item(String label, ScreenId screen) { return new Item(label, screen, null); }
    private static Item item(String label, ScreenId screen, String key) { return new Item(label, screen, key); }

    private static final List<Group> GROUPS = List.of(
        // 1. Hệ thống (Đăng xuất / Thoát do DesktopMenuBar thêm vào cuối)
        new Group('H', "Hệ thống", List.of(
            item("Đổi mật khẩu", ScreenId.CHANGE_PASSWORD),
            item("Tài khoản & phân quyền", ScreenId.ACCOUNT_PERMISSION),
            item("Cài đặt", ScreenId.SETTINGS),
            item("Nhật ký hệ thống", ScreenId.AUDIT_LOG)
        )),
        // 2. Danh mục: chỉ CRUD
        new Group('D', "Danh mục", List.of(
            item("Khách hàng", ScreenId.CUSTOMERS),
            item("Hồ sơ thú cưng", ScreenId.PETS),
            item("Nhân viên", ScreenId.STAFF),
            item("Sản phẩm (kèm Lịch sử giá)", ScreenId.INVENTORY),
            item("Dịch vụ Spa", ScreenId.SPA_SERVICES),
            item("Phòng Pet Hotel", ScreenId.HOTEL_ROOMS),
            item("Nhà cung cấp", ScreenId.SUPPLIERS),
            item("Khuyến mãi", ScreenId.PROMOTIONS),
            item("Tích điểm & Voucher", ScreenId.LOYALTY_VOUCHER)
        )),
        // 3. Tìm kiếm: chỉ xem
        new Group('T', "Tìm kiếm", List.of(
            item("Hóa đơn", ScreenId.ORDERS, "F3"),
            item("Lịch sử khách hàng", ScreenId.CUSTOMER_DETAIL),
            item("Tồn kho theo lô", ScreenId.BATCH_STOCK),
            item("Phiếu Spa trong ngày", ScreenId.SPA_DAILY),
            item("Sơ đồ phòng / lưu trú", ScreenId.HOTEL_MAP),
            SEP,
            item("Lịch làm việc", ScreenId.SCHEDULE),
            item("Lịch làm việc cá nhân", ScreenId.PERSONAL_SCHEDULE)
        )),
        // 4. Xử lý: nghiệp vụ
        new Group('X', "Xử lý", List.of(
            item("Lập hóa đơn", ScreenId.POS, "F2"),
            item("Đổi trả hàng", ScreenId.RETURNS),
            item("Dịch vụ Spa", ScreenId.SPA),
            item("Pet Hotel", ScreenId.HOTEL),
            SEP,
            item("Nhập kho", ScreenId.PURCHASE),
            item("Xuất kho / Xuất hủy", ScreenId.GOODS_WRITE_OFF),
            item("Kiểm kê tồn kho", ScreenId.STOCKTAKE),
            SEP,
            item("Mở ca / Kết ca / Bàn giao", ScreenId.CASH_SHIFT),
            item("Phân lịch nhân viên", ScreenId.STAFF_SCHEDULE),
            item("Phê duyệt yêu cầu", ScreenId.APPROVALS)
        )),
        // 5. Thống kê: chỉ xem và xuất
        new Group('K', "Thống kê", List.of(
            item("Doanh thu", ScreenId.REPORTS),
            item("Hàng hóa", ScreenId.REPORT_GOODS),
            item("Dịch vụ Spa", ScreenId.REPORT_SPA),
            item("Pet Hotel", ScreenId.REPORT_HOTEL),
            item("Khách hàng", ScreenId.REPORT_CUSTOMERS),
            item("Công nợ nhà cung cấp", ScreenId.REPORT_SUPPLIER_DEBT),
            item("Nhân viên", ScreenId.REPORT_STAFF)
        ))
    );

    private MenuModel() {}

    public static List<Group> groups() { return GROUPS; }
}
