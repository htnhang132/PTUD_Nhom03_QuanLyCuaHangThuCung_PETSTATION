package com.petstation.gui.navigation;

import com.petstation.gui.catalog.CustomerCatalogPanel;
import com.petstation.gui.catalog.EmployeeCatalogPanel;
import com.petstation.gui.catalog.LoyaltyVoucherPanel;
import com.petstation.gui.catalog.PetCatalogPanel;
import com.petstation.gui.catalog.ProductCatalogPanel;
import com.petstation.gui.catalog.PromotionCatalogPanel;
import com.petstation.gui.catalog.RoomCatalogPanel;
import com.petstation.gui.catalog.SpaServiceCatalogPanel;
import com.petstation.gui.catalog.SupplierCatalogPanel;
import com.petstation.gui.dashboard.DashboardPanel;
import com.petstation.gui.processing.approval.ApprovalCenterPanel;
import com.petstation.gui.processing.approval.InvoiceRequestPanel;
import com.petstation.gui.processing.hotel.CheckoutPanel;
import com.petstation.gui.processing.hotel.HotelBookingPanel;
import com.petstation.gui.processing.hotel.HotelJournalPanel;
import com.petstation.gui.processing.hotel.HotelPanel;
import com.petstation.gui.processing.inventory.GoodsWriteOffPanel;
import com.petstation.gui.processing.inventory.PurchasePanel;
import com.petstation.gui.processing.inventory.StocktakePanel;
import com.petstation.gui.processing.sales.CashShiftPanel;
import com.petstation.gui.processing.sales.POSPanel;
import com.petstation.gui.processing.sales.PaymentPanel;
import com.petstation.gui.processing.sales.PendingInvoicesPanel;
import com.petstation.gui.processing.sales.PrintInvoicePanel;
import com.petstation.gui.processing.sales.ReturnPanel;
import com.petstation.gui.processing.spa.SpaClosePanel;
import com.petstation.gui.processing.spa.SpaIntakePanel;
import com.petstation.gui.processing.spa.SpaPanel;
import com.petstation.gui.processing.spa.SpaProgressPanel;
import com.petstation.gui.processing.spa.SpaSchedulePanel;
import com.petstation.gui.processing.spa.SpaServiceSelectionPanel;
import com.petstation.gui.processing.staff.StaffSchedulePanel;
import com.petstation.gui.reports.CustomerReportPanel;
import com.petstation.gui.reports.GoodsReportPanel;
import com.petstation.gui.reports.HotelReportPanel;
import com.petstation.gui.reports.RevenueReportPanel;
import com.petstation.gui.reports.SpaReportPanel;
import com.petstation.gui.reports.StaffReportPanel;
import com.petstation.gui.reports.SupplierDebtReportPanel;
import com.petstation.gui.search.BatchStockSearchPanel;
import com.petstation.gui.search.CustomerHistoryPanel;
import com.petstation.gui.search.HotelMapPanel;
import com.petstation.gui.search.InvoiceSearchPanel;
import com.petstation.gui.search.PersonalSchedulePanel;
import com.petstation.gui.search.ScheduleSearchPanel;
import com.petstation.gui.search.SpaDailyPanel;
import com.petstation.gui.system.AccountPermissionPanel;
import com.petstation.gui.system.AuditLogPanel;
import com.petstation.gui.system.ChangePasswordPanel;
import com.petstation.gui.system.SettingsPanel;
import com.petstation.model.Permission;
import com.petstation.model.SaleContext;
import com.petstation.model.UserSession;
import com.petstation.service.CashShiftService;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JPanel;

public final class ScreenRegistry {
    /** Các màn bán hàng chỉ dùng được khi đã mở ca. */
    private static final Set<ScreenId> NEEDS_OPEN_SHIFT =
            EnumSet.of(ScreenId.POS, ScreenId.PAYMENT);

    private final UserSession session;
    private final Consumer<ScreenId> navigator;
    private final Map<ScreenId, ScreenDefinition> definitions = new EnumMap<>(ScreenId.class);
    private final CashShiftService shift = new CashShiftService();
    private final SaleContext sale = new SaleContext();

    public ScreenRegistry(UserSession session, Consumer<ScreenId> navigator) {
        this.session = session;
        this.navigator = navigator;
        registerAll();
    }

    public CashShiftService shift() {
        return shift;
    }

    public SaleContext sale() {
        return sale;
    }

    public boolean requiresOpenShift(ScreenId id) {
        return NEEDS_OPEN_SHIFT.contains(id);
    }

    private void registerAll() {
        register(ScreenId.DASHBOARD, "Màn hình chính", "Chọn một công việc để bắt đầu.", null);

        // 1. Hệ thống
        register(
                ScreenId.CHANGE_PASSWORD,
                "Đổi mật khẩu",
                "Cập nhật mật khẩu tài khoản hiện tại.",
                null);
        register(
                ScreenId.ACCOUNT_PERMISSION,
                "Tài khoản & phân quyền",
                "Tài khoản, vai trò và ma trận quyền (chỉ Cửa hàng trưởng).",
                Permission.ACCOUNT_MANAGE);
        register(
                ScreenId.SETTINGS,
                "Cài đặt",
                "Cửa hàng, hóa đơn, VAT, ngưỡng tồn, phí trả phòng trễ.",
                Permission.ACCOUNT_MANAGE);
        register(
                ScreenId.AUDIT_LOG,
                "Nhật ký hệ thống",
                "Chỉ đọc: thời gian, người dùng, thao tác, đối tượng.",
                Permission.AUDIT_VIEW);

        // 2. Danh mục (chỉ CRUD)
        register(
                ScreenId.CUSTOMERS,
                "Khách hàng",
                "Danh mục khách hàng: thêm, sửa, xóa, lưu.",
                Permission.CUSTOMER_MANAGE);
        register(
                ScreenId.PETS,
                "Hồ sơ thú cưng",
                "Danh mục thú cưng, bắt buộc có chủ nuôi.",
                Permission.PET_MANAGE);
        register(ScreenId.STAFF, "Nhân viên", "Danh mục nhân viên.", Permission.ACCOUNT_MANAGE);
        register(
                ScreenId.INVENTORY,
                "Sản phẩm",
                "Danh mục sản phẩm, tab Lịch sử giá. Tồn kho chỉ đổi qua Nhập, Xuất, Kiểm kê.",
                Permission.PRODUCT_MANAGE);
        register(
                ScreenId.SPA_SERVICES,
                "Dịch vụ Spa",
                "Danh mục dịch vụ, giá theo loài và cân nặng.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.HOTEL_ROOMS,
                "Phòng Pet Hotel",
                "Danh mục phòng. Trạng thái do nghiệp vụ cập nhật, chỉ \"bảo trì\" chỉnh tay.",
                Permission.HOTEL_MANAGE);
        register(
                ScreenId.SUPPLIERS,
                "Nhà cung cấp",
                "Danh mục nhà cung cấp.",
                Permission.SUPPLIER_MANAGE);
        register(
                ScreenId.PROMOTIONS,
                "Khuyến mãi",
                "Danh mục chương trình khuyến mãi.",
                Permission.PROMOTION_MANAGE);
        register(
                ScreenId.LOYALTY_VOUCHER,
                "Tích điểm & Voucher",
                "Quy tắc điểm, hạng thành viên và voucher.",
                Permission.CUSTOMER_MANAGE);

        // 3. Tìm kiếm (chỉ xem)
        register(
                ScreenId.ORDERS,
                "Tìm kiếm hóa đơn",
                "Lọc theo ngày, mã HĐ, khách, thu ngân, trạng thái. Chỉ xem và in lại.",
                Permission.POS_SELL);
        register(
                ScreenId.CUSTOMER_DETAIL,
                "Lịch sử khách hàng",
                "Hóa đơn, phiếu Spa, lưu trú, thú cưng, điểm.",
                Permission.CUSTOMER_MANAGE);
        register(
                ScreenId.BATCH_STOCK,
                "Tồn kho theo lô",
                "Số lô, hạn dùng, tồn. Vàng: sắp hết hạn, đỏ: hết hạn.",
                Permission.PRODUCT_MANAGE);
        register(
                ScreenId.SPA_DAILY,
                "Phiếu Spa trong ngày",
                "Lọc theo ngày và trạng thái. Mở phiếu để sang Xử lý.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.HOTEL_MAP,
                "Sơ đồ phòng / lưu trú",
                "Lưới phòng theo trạng thái. Phòng quá hạn trả tô đỏ.",
                Permission.HOTEL_MANAGE);
        register(
                ScreenId.SCHEDULE,
                "Lịch làm việc",
                "Lịch toàn cửa hàng theo tuần: ca Sáng, Chiều, Tối.",
                Permission.SCHEDULE_MANAGE);
        register(
                ScreenId.PERSONAL_SCHEDULE,
                "Lịch làm việc cá nhân",
                "Ca của bạn, ngày nghỉ, tổng giờ trong tháng.",
                Permission.SCHEDULE_VIEW);

        // 4. Xử lý: bán hàng
        register(
                ScreenId.POS,
                "Lập hóa đơn",
                "Quét mã hoặc nhập mã sản phẩm, rồi Enter.",
                Permission.POS_SELL);
        register(
                ScreenId.PAYMENT,
                "Thanh toán",
                "Chọn phương thức và xác nhận thanh toán.",
                Permission.PAYMENT_PROCESS);
        register(
                ScreenId.PRINT_INVOICE,
                "In hóa đơn",
                "Xem trước, in hoặc xuất PDF.",
                Permission.POS_SELL);
        register(ScreenId.PENDING_INVOICES, "Đơn nháp", "Danh sách đơn treo.", Permission.POS_SELL);
        register(
                ScreenId.INVOICE_EDIT_REQUEST,
                "Yêu cầu sửa/hủy hóa đơn",
                "Gửi yêu cầu sang Phê duyệt, không sửa trực tiếp.",
                Permission.POS_SELL);
        register(
                ScreenId.RETURNS,
                "Đổi trả hàng",
                "Tìm hóa đơn gốc và lập phiếu hoàn trả.",
                Permission.POS_SELL);

        // 4. Xử lý: Spa (danh sách phiếu đang chạy, rồi 5 bước)
        register(
                ScreenId.SPA,
                "Dịch vụ Spa",
                "Danh sách phiếu đang chạy. Bấm phiếu để mở đúng bước hiện tại.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.SPA_SCHEDULE,
                "Spa: Lịch hẹn",
                "Chọn khách, thú, dịch vụ, ngày giờ, nhân viên.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.SPA_INTAKE,
                "Spa: Tiếp nhận",
                "Cân nặng, tình trạng, lưu ý, ảnh.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.SPA_SERVICE_SELECTION,
                "Spa: Chọn dịch vụ",
                "Dịch vụ thực tế, giá theo cân nặng, phụ phí.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.SPA_PROGRESS,
                "Spa: Tiến độ",
                "Chờ, đang làm, hoàn thành.",
                Permission.SPA_MANAGE);
        register(
                ScreenId.SPA_CLOSE,
                "Spa: Đóng phiếu",
                "Chốt tổng tiền rồi chuyển sang Thanh toán.",
                Permission.SPA_MANAGE);

        // 4. Xử lý: Pet Hotel (danh sách thú đang lưu trú, rồi 3 bước)
        register(
                ScreenId.HOTEL,
                "Pet Hotel",
                "Danh sách thú đang lưu trú. Bấm + Đặt phòng để bắt đầu.",
                Permission.HOTEL_MANAGE);
        register(
                ScreenId.HOTEL_BOOKING,
                "Hotel: Đặt phòng",
                "Chọn khách, thú, phòng trống, ngày vào ra, tiền cọc.",
                Permission.HOTEL_MANAGE);
        register(
                ScreenId.HOTEL_JOURNAL,
                "Hotel: Nhật ký lưu trú",
                "Check-in, chăm sóc hằng ngày, gia hạn, dịch vụ thêm.",
                Permission.HOTEL_MANAGE);
        register(
                ScreenId.CHECKOUT,
                "Hotel: Trả phòng & quyết toán",
                "Số ngày × giá phòng + phụ phí trễ + dịch vụ − tiền cọc.",
                Permission.HOTEL_MANAGE);

        // 4. Xử lý: kho, ca, lịch, phê duyệt
        register(
                ScreenId.PURCHASE,
                "Nhập kho",
                "Lập phiếu nhập: số lô, hạn dùng, công nợ.",
                Permission.INVENTORY_RECEIVE);
        register(
                ScreenId.GOODS_WRITE_OFF,
                "Xuất kho / Xuất hủy",
                "Chọn lý do, lô và số lượng.",
                Permission.INVENTORY_ISSUE);
        register(
                ScreenId.STOCKTAKE,
                "Kiểm kê tồn kho",
                "Nhập số thực tế, chênh lệch gửi duyệt.",
                Permission.INVENTORY_COUNT);
        register(
                ScreenId.CASH_SHIFT,
                "Mở ca / Kết ca / Bàn giao",
                "Quản lý ca và tiền mặt.",
                Permission.CASH_SHIFT_MANAGE);
        register(
                ScreenId.STAFF_SCHEDULE,
                "Phân lịch nhân viên",
                "Phân ca theo lưới tuần, cảnh báo ca thiếu người (chỉ Cửa hàng trưởng).",
                Permission.SCHEDULE_MANAGE);
        register(
                ScreenId.APPROVALS,
                "Phê duyệt yêu cầu",
                "Duyệt hoặc từ chối yêu cầu (chỉ Cửa hàng trưởng).",
                Permission.APPROVAL_PROCESS);

        // 5. Thống kê (chỉ xem và xuất)
        register(
                ScreenId.REPORTS,
                "Thống kê doanh thu",
                "Theo ngày, nhân viên, ca, nguồn, phương thức thanh toán.",
                Permission.REPORT_VIEW_OWN_SHIFT);
        register(
                ScreenId.REPORT_GOODS,
                "Thống kê hàng hóa",
                "Bán chạy, nhập–xuất–tồn, tồn thấp, sắp hết hạn.",
                Permission.REPORT_VIEW_OWN_SHIFT);
        register(
                ScreenId.REPORT_SPA,
                "Thống kê dịch vụ Spa",
                "Số phiếu, dịch vụ phổ biến, tỷ lệ hủy, hiệu suất.",
                Permission.REPORT_VIEW_OWN_SHIFT);
        register(
                ScreenId.REPORT_HOTEL,
                "Thống kê Pet Hotel",
                "Công suất phòng, số ngày lưu trú, lượt quá hạn.",
                Permission.REPORT_VIEW_OWN_SHIFT);
        register(
                ScreenId.REPORT_CUSTOMERS,
                "Thống kê khách hàng",
                "Khách mới, thân thiết, top chi tiêu, điểm và voucher.",
                Permission.REPORT_VIEW_ALL);
        register(
                ScreenId.REPORT_SUPPLIER_DEBT,
                "Công nợ nhà cung cấp",
                "Tổng nhập, đã trả, còn nợ.",
                Permission.REPORT_VIEW_ALL);
        register(
                ScreenId.REPORT_STAFF,
                "Thống kê nhân viên",
                "Số ca, giờ làm, doanh số, phiếu Spa đã thực hiện.",
                Permission.REPORT_VIEW_ALL);
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

    public JPanel create(ScreenId id) {
        return switch (id) {
            case DASHBOARD -> new DashboardPanel(navigator, this::canAccess, shift);
            // Hệ thống
            case CHANGE_PASSWORD -> new ChangePasswordPanel(navigator);
            case ACCOUNT_PERMISSION -> new AccountPermissionPanel();
            case SETTINGS ->
                    new SettingsPanel(
                            session == null
                                    || session.role() == com.petstation.model.Role.STORE_MANAGER);
            case AUDIT_LOG -> new AuditLogPanel();
            // Danh mục
            case CUSTOMERS -> new CustomerCatalogPanel();
            case PETS -> new PetCatalogPanel();
            case STAFF -> new EmployeeCatalogPanel();
            case INVENTORY -> new ProductCatalogPanel();
            case SPA_SERVICES -> new SpaServiceCatalogPanel();
            case HOTEL_ROOMS -> new RoomCatalogPanel();
            case SUPPLIERS -> new SupplierCatalogPanel();
            case PROMOTIONS -> new PromotionCatalogPanel();
            case LOYALTY_VOUCHER -> new LoyaltyVoucherPanel();
            // Tìm kiếm
            case ORDERS -> new InvoiceSearchPanel(sale, navigator);
            case CUSTOMER_DETAIL -> new CustomerHistoryPanel();
            case BATCH_STOCK -> new BatchStockSearchPanel(sale, navigator);
            case SPA_DAILY -> new SpaDailyPanel(sale, navigator);
            case HOTEL_MAP -> new HotelMapPanel(sale, navigator);
            case SCHEDULE ->
                    new ScheduleSearchPanel(
                            session == null ? "" : session.employee().employeeCode());
            case PERSONAL_SCHEDULE ->
                    new PersonalSchedulePanel(
                            session == null ? "" : session.employee().employeeCode());
            // Xử lý: bán hàng
            case POS -> new POSPanel(sale, navigator);
            case PAYMENT -> new PaymentPanel(sale, shift, session.displayName(), navigator);
            case PRINT_INVOICE -> new PrintInvoicePanel(sale, navigator);
            case PENDING_INVOICES -> new PendingInvoicesPanel(sale, navigator);
            case INVOICE_EDIT_REQUEST -> new InvoiceRequestPanel(navigator);
            case RETURNS -> new ReturnPanel(sale, navigator);
            // Xử lý: Spa
            case SPA -> new SpaPanel(sale, navigator);
            case SPA_SCHEDULE -> new SpaSchedulePanel(sale, navigator);
            case SPA_INTAKE -> new SpaIntakePanel(sale, navigator);
            case SPA_SERVICE_SELECTION -> new SpaServiceSelectionPanel(sale, navigator);
            case SPA_PROGRESS -> new SpaProgressPanel(sale, navigator);
            case SPA_CLOSE -> new SpaClosePanel(sale, navigator);
            // Xử lý: Pet Hotel
            case HOTEL -> new HotelPanel(sale, navigator);
            case HOTEL_BOOKING -> new HotelBookingPanel(sale, navigator);
            case HOTEL_JOURNAL -> new HotelJournalPanel(sale, navigator);
            case CHECKOUT -> new CheckoutPanel(sale, navigator);
            // Xử lý: kho, ca, lịch, phê duyệt
            case PURCHASE -> new PurchasePanel(sale, navigator);
            case GOODS_WRITE_OFF -> new GoodsWriteOffPanel(sale, navigator);
            case STOCKTAKE -> new StocktakePanel(sale, navigator);
            case CASH_SHIFT -> new CashShiftPanel(shift);
            case STAFF_SCHEDULE ->
                    new StaffSchedulePanel(
                            session == null ? "" : session.employee().employeeCode());
            case APPROVALS -> new ApprovalCenterPanel();
            // Thống kê
            case REPORTS -> new RevenueReportPanel();
            case REPORT_GOODS -> new GoodsReportPanel();
            case REPORT_SPA -> new SpaReportPanel();
            case REPORT_HOTEL -> new HotelReportPanel();
            case REPORT_CUSTOMERS -> new CustomerReportPanel();
            case REPORT_SUPPLIER_DEBT -> new SupplierDebtReportPanel();
            case REPORT_STAFF -> new StaffReportPanel();
        };
    }
}
