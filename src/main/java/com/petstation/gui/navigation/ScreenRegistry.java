package com.petstation.gui.navigation;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JPanel;

import com.petstation.gui.screens.QuickFlows;
import com.petstation.gui.screens.*;
import com.petstation.model.Permission;
import com.petstation.security.UserSession;

public final class ScreenRegistry {
    /** Các màn bán hàng chỉ dùng được khi đã mở ca. */
    private static final Set<ScreenId> NEEDS_OPEN_SHIFT = EnumSet.of(ScreenId.POS, ScreenId.PAYMENT);

    private final UserSession session;
    private final Consumer<ScreenId> navigator;
    private final Map<ScreenId, ScreenDefinition> definitions = new EnumMap<>(ScreenId.class);
    private final ShiftState shift = new ShiftState();
    private final SaleContext sale = new SaleContext();

    public ScreenRegistry(UserSession session, Consumer<ScreenId> navigator) {
        this.session = session;
        this.navigator = navigator;
        registerAll();
    }

    public ShiftState shift() { return shift; }

    public SaleContext sale() { return sale; }

    /**
     * Khi Spa đóng phiếu hoặc Hotel trả phòng chuyển sang Thanh toán, nạp các khoản của phiếu vào hóa đơn.
     * (Dữ liệu mẫu cho tới khi nối service thật.)
     */
    public void prepareCheckout(ScreenId from, ScreenId to) {
        if (to != ScreenId.PAYMENT || from == null || !sale.isEmpty()) return;
        if (from == ScreenId.SPA_CLOSE) {
            sale.setLines(java.util.List.of(
                new SaleContext.Line("DV-SPA1", "Spa: Tắm & cắt lông", 250_000, 1),
                new SaleContext.Line("DV-SPA2", "Spa: Thêm lông khô mùi", 100_000, 1)));
        } else if (from == ScreenId.CHECKOUT) {
            sale.setLines(java.util.List.of(
                new SaleContext.Line("HOTEL-ROOM", "Hotel: Phòng K03 VIP x 3 đêm", 350_000, 3),
                new SaleContext.Line("HOTEL-LATE", "Hotel: Phụ phí trả trễ", 100_000, 1)));
        }
    }

    public boolean requiresOpenShift(ScreenId id) { return NEEDS_OPEN_SHIFT.contains(id); }

    private void registerAll() {
        register(ScreenId.DASHBOARD, "Màn hình chính", "Chọn một công việc để bắt đầu.", null);

        // 1. Hệ thống
        register(ScreenId.CHANGE_PASSWORD, "Đổi mật khẩu", "Cập nhật mật khẩu tài khoản hiện tại.", null);
        register(ScreenId.ACCOUNT_PERMISSION, "Tài khoản & phân quyền", "Tài khoản, vai trò và ma trận quyền (chỉ Cửa hàng trưởng).", Permission.ACCOUNT_MANAGE);
        register(ScreenId.SETTINGS, "Cài đặt", "Cửa hàng, hóa đơn, VAT, ngưỡng tồn, phí trả phòng trễ.", Permission.ACCOUNT_MANAGE);
        register(ScreenId.AUDIT_LOG, "Nhật ký hệ thống", "Chỉ đọc: thời gian, người dùng, thao tác, đối tượng.", Permission.AUDIT_VIEW);

        // 2. Danh mục (chỉ CRUD)
        register(ScreenId.CUSTOMERS, "Khách hàng", "Danh mục khách hàng: thêm, sửa, xóa, lưu.", Permission.CUSTOMER_MANAGE);
        register(ScreenId.PETS, "Hồ sơ thú cưng", "Danh mục thú cưng, bắt buộc có chủ nuôi.", Permission.PET_MANAGE);
        register(ScreenId.STAFF, "Nhân viên", "Danh mục nhân viên.", Permission.ACCOUNT_MANAGE);
        register(ScreenId.INVENTORY, "Sản phẩm", "Danh mục sản phẩm, tab Lịch sử giá. Tồn kho chỉ đổi qua Nhập, Xuất, Kiểm kê.", Permission.PRODUCT_MANAGE);
        register(ScreenId.SPA_SERVICES, "Dịch vụ Spa", "Danh mục dịch vụ, giá theo loài và cân nặng.", Permission.SPA_MANAGE);
        register(ScreenId.HOTEL_ROOMS, "Phòng Pet Hotel", "Danh mục phòng. Trạng thái do nghiệp vụ cập nhật, chỉ \"bảo trì\" chỉnh tay.", Permission.HOTEL_MANAGE);
        register(ScreenId.SUPPLIERS, "Nhà cung cấp", "Danh mục nhà cung cấp.", Permission.SUPPLIER_MANAGE);
        register(ScreenId.PROMOTIONS, "Khuyến mãi", "Danh mục chương trình khuyến mãi.", Permission.PROMOTION_MANAGE);
        register(ScreenId.LOYALTY_VOUCHER, "Tích điểm & Voucher", "Quy tắc điểm, hạng thành viên và voucher.", Permission.CUSTOMER_MANAGE);

        // 3. Tìm kiếm (chỉ xem)
        register(ScreenId.ORDERS, "Tìm kiếm hóa đơn", "Lọc theo ngày, mã HĐ, khách, thu ngân, trạng thái. Chỉ xem và in lại.", Permission.POS_SELL);
        register(ScreenId.CUSTOMER_DETAIL, "Lịch sử khách hàng", "Hóa đơn, phiếu Spa, lưu trú, thú cưng, điểm.", Permission.CUSTOMER_MANAGE);
        register(ScreenId.BATCH_STOCK, "Tồn kho theo lô", "Số lô, hạn dùng, tồn. Vàng: sắp hết hạn, đỏ: hết hạn.", Permission.PRODUCT_MANAGE);
        register(ScreenId.SPA_DAILY, "Phiếu Spa trong ngày", "Lọc theo ngày và trạng thái. Mở phiếu để sang Xử lý.", Permission.SPA_MANAGE);
        register(ScreenId.HOTEL_MAP, "Sơ đồ phòng / lưu trú", "Lưới phòng theo trạng thái. Phòng quá hạn trả tô đỏ.", Permission.HOTEL_MANAGE);
        register(ScreenId.SCHEDULE, "Lịch làm việc", "Lịch toàn cửa hàng theo tuần: ca Sáng, Chiều, Tối.", Permission.SCHEDULE_MANAGE);
        register(ScreenId.PERSONAL_SCHEDULE, "Lịch làm việc cá nhân", "Ca của bạn, ngày nghỉ, tổng giờ trong tháng.", Permission.SCHEDULE_VIEW);

        // 4. Xử lý: bán hàng
        register(ScreenId.POS, "Lập hóa đơn", "Quét mã hoặc nhập mã sản phẩm, rồi Enter.", Permission.POS_SELL);
        register(ScreenId.PAYMENT, "Thanh toán", "Chọn phương thức và xác nhận thanh toán.", Permission.PAYMENT_PROCESS);
        register(ScreenId.PRINT_INVOICE, "In hóa đơn", "Xem trước, in hoặc xuất PDF.", Permission.POS_SELL);
        register(ScreenId.PENDING_INVOICES, "Đơn nháp", "Danh sách đơn treo.", Permission.POS_SELL);
        register(ScreenId.INVOICE_EDIT_REQUEST, "Yêu cầu sửa/hủy hóa đơn", "Gửi yêu cầu sang Phê duyệt, không sửa trực tiếp.", Permission.POS_SELL);
        register(ScreenId.RETURNS, "Đổi trả hàng", "Tìm hóa đơn gốc và lập phiếu hoàn trả.", Permission.POS_SELL);

        // 4. Xử lý: Spa (danh sách phiếu đang chạy, rồi 5 bước)
        register(ScreenId.SPA, "Dịch vụ Spa", "Danh sách phiếu đang chạy. Bấm phiếu để mở đúng bước hiện tại.", Permission.SPA_MANAGE);
        register(ScreenId.SPA_SCHEDULE, "Spa: Lịch hẹn", "Chọn khách, thú, dịch vụ, ngày giờ, nhân viên.", Permission.SPA_MANAGE);
        register(ScreenId.SPA_INTAKE, "Spa: Tiếp nhận", "Cân nặng, tình trạng, lưu ý, ảnh.", Permission.SPA_MANAGE);
        register(ScreenId.SPA_SERVICE_SELECTION, "Spa: Chọn dịch vụ", "Dịch vụ thực tế, giá theo cân nặng, phụ phí.", Permission.SPA_MANAGE);
        register(ScreenId.SPA_PROGRESS, "Spa: Tiến độ", "Chờ, đang làm, hoàn thành.", Permission.SPA_MANAGE);
        register(ScreenId.SPA_CLOSE, "Spa: Đóng phiếu", "Chốt tổng tiền rồi chuyển sang Thanh toán.", Permission.SPA_MANAGE);

        // 4. Xử lý: Pet Hotel (danh sách thú đang lưu trú, rồi 3 bước)
        register(ScreenId.HOTEL, "Pet Hotel", "Danh sách thú đang lưu trú. Bấm + Đặt phòng để bắt đầu.", Permission.HOTEL_MANAGE);
        register(ScreenId.HOTEL_BOOKING, "Hotel: Đặt phòng", "Chọn khách, thú, phòng trống, ngày vào ra, tiền cọc.", Permission.HOTEL_MANAGE);
        register(ScreenId.HOTEL_JOURNAL, "Hotel: Nhật ký lưu trú", "Check-in, chăm sóc hằng ngày, gia hạn, dịch vụ thêm.", Permission.HOTEL_MANAGE);
        register(ScreenId.CHECKOUT, "Hotel: Trả phòng & quyết toán", "Số ngày × giá phòng + phụ phí trễ + dịch vụ − tiền cọc.", Permission.HOTEL_MANAGE);

        // 4. Xử lý: kho, ca, lịch, phê duyệt
        register(ScreenId.PURCHASE, "Nhập kho", "Lập phiếu nhập: số lô, hạn dùng, công nợ.", Permission.INVENTORY_RECEIVE);
        register(ScreenId.GOODS_WRITE_OFF, "Xuất kho / Xuất hủy", "Chọn lý do, lô và số lượng.", Permission.INVENTORY_ISSUE);
        register(ScreenId.STOCKTAKE, "Kiểm kê tồn kho", "Nhập số thực tế, chênh lệch gửi duyệt.", Permission.INVENTORY_COUNT);
        register(ScreenId.CASH_SHIFT, "Mở ca / Kết ca / Bàn giao", "Quản lý ca và tiền mặt.", Permission.CASH_SHIFT_MANAGE);
        register(ScreenId.STAFF_SCHEDULE, "Phân lịch nhân viên", "Phân ca theo lưới tuần, cảnh báo ca thiếu người (chỉ Cửa hàng trưởng).", Permission.SCHEDULE_MANAGE);
        register(ScreenId.APPROVALS, "Phê duyệt yêu cầu", "Duyệt hoặc từ chối yêu cầu (chỉ Cửa hàng trưởng).", Permission.APPROVAL_PROCESS);

        // 5. Thống kê (chỉ xem và xuất)
        register(ScreenId.REPORTS, "Thống kê doanh thu", "Theo ngày, nhân viên, ca, nguồn, phương thức thanh toán.", Permission.REPORT_VIEW_OWN_SHIFT);
        register(ScreenId.REPORT_GOODS, "Thống kê hàng hóa", "Bán chạy, nhập–xuất–tồn, tồn thấp, sắp hết hạn.", Permission.REPORT_VIEW_OWN_SHIFT);
        register(ScreenId.REPORT_SPA, "Thống kê dịch vụ Spa", "Số phiếu, dịch vụ phổ biến, tỷ lệ hủy, hiệu suất.", Permission.REPORT_VIEW_OWN_SHIFT);
        register(ScreenId.REPORT_HOTEL, "Thống kê Pet Hotel", "Công suất phòng, số ngày lưu trú, lượt quá hạn.", Permission.REPORT_VIEW_OWN_SHIFT);
        register(ScreenId.REPORT_CUSTOMERS, "Thống kê khách hàng", "Khách mới, thân thiết, top chi tiêu, điểm và voucher.", Permission.REPORT_VIEW_ALL);
        register(ScreenId.REPORT_SUPPLIER_DEBT, "Công nợ nhà cung cấp", "Tổng nhập, đã trả, còn nợ.", Permission.REPORT_VIEW_ALL);
        register(ScreenId.REPORT_STAFF, "Thống kê nhân viên", "Số ca, giờ làm, doanh số, phiếu Spa đã thực hiện.", Permission.REPORT_VIEW_ALL);
    }

    private void register(ScreenId id, String title, String hint, Permission permission) {
        definitions.put(id, new ScreenDefinition(id, title, hint, permission, true));
    }

    public ScreenDefinition definition(ScreenId id) {
        return definitions.get(id);
    }

    public boolean canAccess(ScreenId id) {
        ScreenDefinition d = definition(id);
        return d != null && (d.permission() == null || session.has(d.permission()));
    }

    public List<ScreenDefinition> visibleDefinitions() {
        return definitions.values().stream().filter(d -> canAccess(d.id())).toList();
    }

    private static JPanel wired(WorkflowPanel panel, Consumer<java.awt.Component> primary, Consumer<java.awt.Component> secondary) {
        if (primary != null) panel.onPrimary(() -> primary.accept(panel));
        if (secondary != null) panel.onSecondary(() -> secondary.accept(panel));
        return panel;
    }

    public JPanel create(ScreenId id) {
        return switch (id) {
            case DASHBOARD -> new DashboardPanel(navigator, this::canAccess, shift);
            // Hệ thống
            case CHANGE_PASSWORD -> new ChangePasswordPanel();
            case ACCOUNT_PERMISSION -> CatalogScreens.accounts();
            case SETTINGS -> new SettingsPanel(session == null || session.role() == com.petstation.model.Role.STORE_MANAGER);
            case AUDIT_LOG -> new AuditLogPanel();
            // Danh mục
            case CUSTOMERS -> CatalogScreens.customers();
            case PETS -> CatalogScreens.pets();
            case STAFF -> CatalogScreens.staff();
            case INVENTORY -> CatalogScreens.products();
            case SPA_SERVICES -> CatalogScreens.spaServices();
            case HOTEL_ROOMS -> CatalogScreens.hotelRooms();
            case SUPPLIERS -> CatalogScreens.suppliers();
            case PROMOTIONS -> CatalogScreens.promotions();
            case LOYALTY_VOUCHER -> CatalogScreens.loyaltyVoucher();
            // Tìm kiếm
            case ORDERS -> new OrdersPanel(navigator);
            case CUSTOMER_DETAIL -> new CustomerHistoryPanel();
            case BATCH_STOCK -> new BatchStockPanel();
            case SPA_DAILY -> new SpaPanel(navigator);
            case HOTEL_MAP -> new HotelPanel();
            case SCHEDULE -> new WeekSchedulePanel(WeekSchedulePanel.Mode.VIEW_ALL, session == null ? "" : session.displayName());
            case PERSONAL_SCHEDULE -> new WeekSchedulePanel(WeekSchedulePanel.Mode.PERSONAL, session == null ? "" : session.displayName());
            // Xử lý: bán hàng
            case POS -> new POSPanel(sale, navigator);
            case PAYMENT -> new PaymentPanel(sale, shift, session.displayName(), navigator);
            case PRINT_INVOICE -> new PrintInvoicePanel(sale, navigator);
            case PENDING_INVOICES -> new PendingInvoicesPanel();
            case INVOICE_EDIT_REQUEST -> new InvoiceRequestPanel(navigator);
            case RETURNS -> new ReturnPanel();
            // Xử lý: Spa
            case SPA -> new SpaTicketsPanel(navigator);
            case SPA_SCHEDULE -> new SpaSchedulePanel();
            case SPA_INTAKE -> new SpaIntakePanel(navigator);
            case SPA_SERVICE_SELECTION -> new SpaServiceSelectionPanel().onPrimary(() -> navigator.accept(ScreenId.SPA_PROGRESS));
            case SPA_PROGRESS -> new SpaProgressPanel();
            case SPA_CLOSE -> new SpaClosePanel().onPrimary(() -> navigator.accept(ScreenId.PAYMENT));
            // Xử lý: Pet Hotel
            case HOTEL -> new HotelStayPanel(navigator);
            case HOTEL_BOOKING -> new HotelBookingPanel(navigator);
            case HOTEL_JOURNAL -> new HotelJournalPanel().onSecondary(() -> navigator.accept(ScreenId.CHECKOUT));
            case CHECKOUT -> new CheckoutPanel().onPrimary(() -> navigator.accept(ScreenId.PAYMENT)).onSecondary(() -> navigator.accept(ScreenId.HOTEL_JOURNAL));
            // Xử lý: kho, ca, lịch, phê duyệt
            case PURCHASE -> new PurchasePanel();
            case GOODS_WRITE_OFF -> wired(new GoodsWriteOffPanel(), QuickFlows::writeOff, null);
            case STOCKTAKE -> wired(new StocktakePanel(), QuickFlows::stocktake, null);
            case CASH_SHIFT -> new CashShiftPanel(shift);
            case STAFF_SCHEDULE -> new WeekSchedulePanel(WeekSchedulePanel.Mode.EDIT, session == null ? "" : session.displayName());
            case APPROVALS -> new ApprovalCenterPanel();
            // Thống kê
            case REPORTS -> ReportScreens.revenue();
            case REPORT_GOODS -> ReportScreens.goods();
            case REPORT_SPA -> ReportScreens.spa();
            case REPORT_HOTEL -> ReportScreens.hotel();
            case REPORT_CUSTOMERS -> ReportScreens.customers();
            case REPORT_SUPPLIER_DEBT -> ReportScreens.supplierDebt();
            case REPORT_STAFF -> ReportScreens.staff();
        };
    }
}
