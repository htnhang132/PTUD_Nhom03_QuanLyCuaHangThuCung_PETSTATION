package com.petstation.integration;

import static com.petstation.dao.SqlDatabase.*;
import static com.petstation.service.SessionService.*;

import static org.assertj.core.api.Assertions.*;

import com.petstation.dao.DatabaseConfig;
import com.petstation.dao.DatabaseConnection;
import com.petstation.dao.SqlDatabase;
import com.petstation.dao.impl.JdbcCustomerDao;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.navigation.ScreenRegistry;
import com.petstation.model.*;
import com.petstation.model.AccountStatus;
import com.petstation.model.CatalogType;
import com.petstation.model.Employee;
import com.petstation.model.Permission;
import com.petstation.model.Role;
import com.petstation.model.SaleContext;
import com.petstation.model.UserAccount;
import com.petstation.model.UserSession;
import com.petstation.model.WorkflowType;
import com.petstation.service.ApprovalService;
import com.petstation.service.AuditService;
import com.petstation.service.CashShiftService;
import com.petstation.service.CatalogService;
import com.petstation.service.HotelService;
import com.petstation.service.InventoryService;
import com.petstation.service.ReportingService;
import com.petstation.service.ReturnService;
import com.petstation.service.SalesService;
import com.petstation.service.ScheduleService;
import com.petstation.service.SettingsService;
import com.petstation.service.SpaService;
import com.petstation.service.WorkflowService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.*;
import java.util.*;

/** Runs only against the explicitly selected integration database; each scenario rolls back. */
@EnabledIfEnvironmentVariable(named = "PETSTATION_SQL_TESTS", matches = "true")
class SqlServerIntegrationTest {
    private static class Rollback extends RuntimeException {}

    private long employee, account, customer, product, batch1, batch2;
    private String employeeCode, customerCode, productCode, voucher, petCode, roomCode, packageCode;
    private UserSession manager;

    @BeforeAll
    static void configure() {
        DatabaseConfig config = DatabaseConfig.load();
        assertThat(config.url()).contains("databaseName=PetStationIntegrationTest");
        SqlDatabase.configure(new DatabaseConnection(config));
    }

    private void scenario(Runnable test) {
        try {
            transaction(
                    c -> {
                        fixture();
                        test.run();
                        throw new Rollback();
                    });
        } catch (Rollback expected) {
        } finally {
            setSession(null);
        }
    }

    private void fixture() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        employeeCode = "ITNV" + suffix;
        customerCode = "ITKH" + suffix;
        productCode = "ITSP" + suffix;
        voucher = "ITVC" + suffix;
        petCode = "ITTC" + suffix;
        roomCode = "ITP" + suffix;
        packageCode = "ITDV" + suffix;
        employee =
                insert(
                        "INSERT INTO"
                            + " nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)"
                            + " VALUES(?,N'Integration Manager',?,N'Cửa hàng trưởng',CAST(GETDATE()"
                            + " AS date))",
                        employeeCode,
                        "IT" + suffix);
        account =
                insert(
                        "INSERT INTO"
                            + " tai_khoan(nhan_vien_id,ten_dang_nhap,mat_khau_hash,vai_tro,bat_buoc_doi_mat_khau)"
                            + " VALUES(?,?,?,'STORE_MANAGER',0)",
                        employee,
                        employeeCode,
                        org.mindrot.jbcrypt.BCrypt.hashpw(
                                "Integration@123", org.mindrot.jbcrypt.BCrypt.gensalt(4)));
        manager =
                new UserSession(
                        new UserAccount(
                                account,
                                employee,
                                employeeCode,
                                "hash",
                                Role.STORE_MANAGER,
                                AccountStatus.ACTIVE,
                                0,
                                null,
                                false),
                        new Employee(
                                employee,
                                employeeCode,
                                "Integration Manager",
                                null,
                                "IT" + suffix,
                                null,
                                "Cửa hàng trưởng",
                                LocalDate.now(),
                                true),
                        EnumSet.allOf(Permission.class));
        setSession(manager);
        customer =
                insert(
                        "INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,diem_tich_luy)"
                                + " VALUES(?,N'Integration Customer',?,100)",
                        customerCode,
                        "KH" + suffix);
        product =
                insert(
                        "INSERT INTO"
                            + " san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,theo_doi_hsd)"
                            + " VALUES(?,?,N'Test',N'Cái',100000,50000,1)",
                        productCode,
                        "Product " + suffix);
        batch1 =
                insert(
                        "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                                + " VALUES(?,'IT-FIRST',DATEADD(day,5,GETDATE()),2)",
                        product);
        batch2 =
                insert(
                        "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                                + " VALUES(?,'IT-SECOND',DATEADD(day,10,GETDATE()),4)",
                        product);
        update(
                "INSERT INTO"
                    + " voucher(ma_voucher,menh_gia,hieu_luc_den,so_luong,don_toi_thieu,chi_ap_dung_spa)"
                    + " VALUES(?,10000,DATEADD(day,1,GETDATE()),2,0,0)",
                voucher);
        update(
                "INSERT INTO thu_cung(ma_thu_cung,khach_hang_id,ten,loai,can_nang)"
                        + " VALUES(?,?,N'Integration Pet','DOG',4)",
                petCode,
                customer);
        update(
                "INSERT INTO phong_hotel(ma_phong,loai_phong,gia_dem,trang_thai) VALUES(?,N'Tiêu"
                        + " chuẩn',150000,'AVAILABLE')",
                roomCode);
        long pkg =
                insert(
                        "INSERT INTO goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien)"
                                + " VALUES(?,?,60)",
                        packageCode,
                        "Service " + suffix);
        update(
                "INSERT INTO"
                    + " bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)"
                    + " VALUES(?,'DOG',N'< 5kg',0,5,150000)",
                pkg);
    }

    private SaleContext sale(int quantity) {
        SaleContext sale = new SaleContext();
        sale.setLines(
                List.of(
                        new SaleContext.Line(
                                productCode, "Integration product", 100000, quantity)));
        sale.setCustomer(customerCode, "Integration Customer", 100);
        return sale;
    }

    private void open() {
        new CashShiftService().open("Integration shift", 1000000);
    }

    @Test
    void catalogsReadAndPersistActualSqlRows() {
        scenario(
                () -> {
                    var rows = CatalogService.customers();
                    Object[] before =
                            rows.stream()
                                    .filter(r -> r[0].equals(customerCode))
                                    .findFirst()
                                    .orElseThrow();
                    Object[] changed = before.clone();
                    changed[1] = "Updated customer";
                    changed[3] = "test@example.com";
                    CatalogService.save(rows, before, changed);
                    assertThat(CatalogService.customers())
                            .anySatisfy(
                                    r -> {
                                        assertThat(r[0]).isEqualTo(customerCode);
                                        assertThat(r[1]).isEqualTo("Updated customer");
                                        assertThat(r[3]).isEqualTo("test@example.com");
                                    });
                    transaction(
                            c -> {
                                assertThat(
                                                new JdbcCustomerDao(c)
                                                        .findByCustomerCode(customerCode)
                                                        .orElseThrow()
                                                        .getFullName())
                                        .isEqualTo("Updated customer");
                                return null;
                            });
                    var products = CatalogService.products();
                    Object[] p =
                            products.stream()
                                    .filter(r -> r[0].equals(productCode))
                                    .findFirst()
                                    .orElseThrow();
                    Object[] next = p.clone();
                    next[4] = "110000";
                    CatalogService.save(products, p, next);
                    assertThat(
                                    number(
                                            "SELECT COUNT(*) FROM lich_su_gia WHERE san_pham_id=?"
                                                    + " AND gia_cu=100000 AND gia_moi=110000",
                                            product))
                            .isEqualTo(1);
                    for (var kind : WorkflowType.values())
                        assertThat(WorkflowService.rows(kind)).isNotNull();
                    assertThat(CatalogService.pets()).isNotEmpty();
                    assertThat(CatalogService.staff()).isNotEmpty();
                    assertThat(CatalogService.accounts()).isNotEmpty();
                    assertThat(CatalogService.suppliers()).isNotEmpty();
                    assertThat(CatalogService.spaServices()).isNotEmpty();
                    assertThat(CatalogService.rooms()).isNotEmpty();
                });
    }

    @Test
    void paymentAllocatesFefoAndIsIdempotent() {
        scenario(
                () -> {
                    open();
                    SaleContext sale = sale(3);
                    assertThat(sale.applyVoucher(voucher)).isNull();
                    sale.setPointsUsed(2);
                    long total = sale.total();
                    SaleContext.Receipt first =
                            sale.complete(CashShiftService.Method.CASH, 500000, "Manager");
                    SaleContext.Receipt second =
                            sale.complete(CashShiftService.Method.CASH, 500000, "Manager");
                    assertThat(second.invoiceNo()).isEqualTo(first.invoiceNo());
                    assertThat(first.total()).isEqualTo(total);
                    assertThat(first.change()).isEqualTo(500000 - total);
                    assertThat(number("SELECT so_luong_ton FROM lo_hang WHERE id=?", batch1))
                            .isZero();
                    assertThat(number("SELECT so_luong_ton FROM lo_hang WHERE id=?", batch2))
                            .isEqualTo(3);
                    assertThat(number("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isEqualTo(1);
                    assertThat(
                                    number(
                                            "SELECT COUNT(*) FROM lich_su_diem WHERE"
                                                    + " khach_hang_id=?",
                                            customer))
                            .isEqualTo(1);
                    assertThat(new CashShiftService().invoiceCount()).isEqualTo(1);
                    assertThat(new CashShiftService().expectedCash()).isEqualTo(1000000 + total);
                });
    }

    @Test
    void oversellRollsBackInvoiceVoucherAndStock() {
        scenario(
                () -> {
                    open();
                    SaleContext sale = sale(7);
                    sale.applyVoucher(voucher);
                    long invoices = number("SELECT COUNT(*) FROM hoa_don"),
                            orders = number("SELECT COUNT(*) FROM don_hang");
                    assertThatThrownBy(
                                    () ->
                                            sale.complete(
                                                    CashShiftService.Method.CASH,
                                                    1000000,
                                                    "Manager"))
                            .hasMessageContaining("Không đủ tồn");
                    assertThat(number("SELECT COUNT(*) FROM hoa_don")).isEqualTo(invoices);
                    assertThat(number("SELECT COUNT(*) FROM don_hang")).isEqualTo(orders);
                    assertThat(number("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isZero();
                    assertThat(
                                    number(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            product))
                            .isEqualTo(6);
                });
    }

    @Test
    void concurrentStockSnapshotChangesCannotBeApprovedBlindly() {
        scenario(
                () -> {
                    long count = InventoryService.stocktake(batch1, 1, "Recount");
                    var request =
                            ApprovalService.all().stream()
                                    .filter(r -> r.after.equals("Tồn thực 1"))
                                    .findFirst()
                                    .orElseThrow();
                    update("UPDATE lo_hang SET so_luong_ton=3 WHERE id=?", batch1);
                    assertThatThrownBy(() -> ApprovalService.approve(request, "Manager"))
                            .hasMessageContaining("Tồn đã thay đổi");
                    assertThat(
                                    text(
                                            scalar(
                                                    "SELECT trang_thai FROM yeu_cau_phe_duyet WHERE"
                                                            + " ma_yeu_cau=?",
                                                    request.id)))
                            .isEqualTo("PENDING");
                    assertThat(number("SELECT so_luong_ton FROM lo_hang WHERE id=?", batch1))
                            .isEqualTo(3);
                });
    }

    @Test
    void persistedApprovalAndRejectionApplyExactlyOnce() {
        scenario(
                () -> {
                    SettingsService.update(8, 10, 30, 50000, 3, 1);
                    long issue = InventoryService.writeOff(batch2, 1, "Test writeoff");
                    var request =
                            ApprovalService.all().stream()
                                    .filter(r -> r.reason.equals("Test writeoff"))
                                    .findFirst()
                                    .orElseThrow();
                    assertThat(number("SELECT so_luong_ton FROM lo_hang WHERE id=?", batch2))
                            .isEqualTo(4);
                    ApprovalService.approve(request, "Manager");
                    assertThat(number("SELECT so_luong_ton FROM lo_hang WHERE id=?", batch2))
                            .isEqualTo(3);
                    assertThatThrownBy(() -> ApprovalService.approve(request, "Manager"))
                            .hasMessageContaining("đã được xử lý");
                    InventoryService.writeOff(batch2, 1, "Reject writeoff");
                    var rejected =
                            ApprovalService.all().stream()
                                    .filter(r -> r.reason.equals("Reject writeoff"))
                                    .findFirst()
                                    .orElseThrow();
                    ApprovalService.reject(rejected, "Manager", "Không hợp lệ");
                    assertThat(number("SELECT so_luong_ton FROM lo_hang WHERE id=?", batch2))
                            .isEqualTo(3);
                });
    }

    @Test
    void scheduleHasNoGeneratedRowsAndLeaveSurvivesReload() {
        scenario(
                () -> {
                    LocalDate day = LocalDate.of(2040, 1, 9);
                    assertThat(ScheduleService.weekEntries(ScheduleService.mondayOf(day)))
                            .isEmpty();
                    assertThat(ScheduleService.assign(day, 0, employeeCode)).isNull();
                    assertThat(ScheduleService.assign(day, 0, employeeCode)).contains("đã có");
                    var request =
                            ApprovalService.submitAction(
                                    ApprovalService.Kind.LEAVE,
                                    "Integration leave",
                                    "WORK",
                                    "LEAVE",
                                    "LEAVE",
                                    employee,
                                    day.toString());
                    ApprovalService.approve(request, "Manager");
                    assertThat(ScheduleService.cell(day, 0))
                            .anyMatch(e -> e.staffCode.equals(employeeCode) && e.isLeave());
                    assertThat(ScheduleService.assign(day, 1, employeeCode)).contains("nghỉ phép");
                    assertThat(ScheduleService.monthHours(employeeCode, day)).isZero();
                });
    }

    @Test
    void spaLifecycleUsesSpeciesAndWeightPrices() {
        scenario(
                () -> {
                    open();
                    long appointment =
                            SpaService.appointment(
                                    petCode,
                                    packageCode,
                                    employeeCode,
                                    LocalDateTime.now().plusHours(1));
                    String appointmentCode =
                            text(
                                    scalar(
                                            "SELECT ma_lich_hen FROM lich_hen WHERE id=?",
                                            appointment));
                    assertThatThrownBy(
                                    () ->
                                            SpaService.appointment(
                                                    petCode,
                                                    packageCode,
                                                    employeeCode,
                                                    LocalDateTime.now().plusHours(1)))
                            .hasMessageContaining("trùng");
                    long ticket = SpaService.intake(petCode, appointmentCode, 4.5, "Healthy");
                    assertThat(
                                    number(
                                            "SELECT tong_tien FROM phieu_tiep_nhan_spa WHERE id=?",
                                            ticket))
                            .isEqualTo(150000);
                    SpaService.spaStatus(ticket, "IN_PROGRESS");
                    SpaService.spaStatus(ticket, "COMPLETED");
                    SaleContext bill = new SaleContext();
                    SalesService.checkout("SPA", ticket, bill);
                    bill.complete(CashShiftService.Method.TRANSFER, bill.total(), "Manager");
                    assertThat(
                                    text(
                                            scalar(
                                                    "SELECT trang_thai FROM phieu_tiep_nhan_spa"
                                                            + " WHERE id=?",
                                                    ticket)))
                            .isEqualTo("PAID");
                    assertThatThrownBy(
                                    () -> SalesService.checkout("SPA", ticket, new SaleContext()))
                            .hasMessageContaining("đã thanh toán");
                });
    }

    @Test
    void hotelPreventsOverlapAndPersistsCareAndCheckout() {
        scenario(
                () -> {
                    open();
                    LocalDateTime start = LocalDateTime.now(), end = start.plusDays(2);
                    long booking = HotelService.book(petCode, roomCode, start, end, 50000, "Test");
                    assertThatThrownBy(
                                    () -> HotelService.book(petCode, roomCode, start, end, 0, ""))
                            .hasMessageContaining("trùng");
                    HotelService.checkIn(booking);
                    HotelService.journal(booking, "Ate", "Clean", "Healthy");
                    assertThat(
                                    number(
                                            "SELECT COUNT(*) FROM nhat_ky_cham_soc WHERE"
                                                    + " hop_dong_id=?",
                                            booking))
                            .isEqualTo(1);
                    assertThat(HotelService.hotelBalance(booking)).isEqualTo(250000);
                    SaleContext bill = new SaleContext();
                    SalesService.checkout("HOTEL", booking, bill);
                    bill.complete(CashShiftService.Method.CARD, bill.total(), "Manager");
                    assertThat(
                                    text(
                                            scalar(
                                                    "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                            + " id=?",
                                                    booking)))
                            .isEqualTo("CHECKED_OUT");
                    assertThat(
                                    text(
                                            scalar(
                                                    "SELECT trang_thai FROM phong_hotel WHERE"
                                                            + " ma_phong=?",
                                                    roomCode)))
                            .isEqualTo("CLEANING");
                });
    }

    @Test
    void purchaseAndReturnUpdateStockAndDebt() {
        scenario(
                () -> {
                    open();
                    String supplier = text(CatalogService.suppliers().get(0)[0]);
                    long receipt =
                            InventoryService.purchase(
                                    supplier,
                                    productCode,
                                    "IT-NEW",
                                    LocalDate.now().plusDays(30),
                                    3,
                                    50000,
                                    50000);
                    assertThat(
                                    number(
                                            "SELECT tong_tien-da_tra FROM phieu_nhap_kho WHERE"
                                                    + " id=?",
                                            receipt))
                            .isEqualTo(100000);
                    assertThat(
                                    number(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            product))
                            .isEqualTo(9);
                    SaleContext sale = sale(2);
                    var invoice = sale.complete(CashShiftService.Method.CASH, 500000, "Manager");
                    long item =
                            number(
                                    "SELECT i.id FROM chi_tiet_don_hang i JOIN hoa_don h ON"
                                            + " h.don_hang_id=i.don_hang_id WHERE h.so_hoa_don=?",
                                    invoice.invoiceNo());
                    ReturnService.returnItem(item, 1, "Test return");
                    assertThat(
                                    number(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            product))
                            .isEqualTo(8);
                    assertThatThrownBy(() -> ReturnService.returnItem(item, 2, "Too many"))
                            .hasMessageContaining("vượt");
                });
    }

    @Test
    void settingsAndAuditAreReloadedFromSql() {
        scenario(
                () -> {
                    SettingsService.update(10, 5, 45, 70000, 14, 1000000);
                    assertThat(SettingsService.vatPercent()).isEqualTo(10);
                    assertThat(SettingsService.expiryWarnDays()).isEqualTo(45);
                    assertThat(SettingsService.lateCheckoutFeePerDay()).isEqualTo(70000);
                    AuditService.log("Thêm", "Integration audit");
                    assertThat(AuditService.entries())
                            .anyMatch(
                                    e ->
                                            e.object().equals("Integration audit")
                                                    && e.user().equals("Integration Manager")
                                                    && e.type().equals("CREATE"));
                });
    }

    @Test
    void reportsUseActualDataAndScopeOwnAccount() {
        scenario(
                () -> {
                    UserSession own =
                            new UserSession(
                                    manager.account(),
                                    manager.employee(),
                                    Set.of(
                                            Permission.REPORT_VIEW_OWN_SHIFT,
                                            Permission.PAYMENT_PROCESS,
                                            Permission.CASH_SHIFT_MANAGE));
                    setSession(own);
                    assertThat(ReportingService.dailyRevenue(7)).isEmpty();
                    open();
                    SaleContext sale = sale(1);
                    long total = sale.total();
                    sale.complete(CashShiftService.Method.CASH, 200000, "Manager");
                    assertThat(ReportingService.dailyRevenue(7)).hasSize(1);
                    assertThat(((Number) ReportingService.dailyRevenue(7).get(0)[1]).longValue())
                            .isEqualTo(total);
                    assertThatThrownBy(() -> ReportingService.customers(7))
                            .hasMessageContaining("không có quyền");
                    assertThatThrownBy(
                                    () ->
                                            CatalogService.save(
                                                    CatalogService.customers(),
                                                    null,
                                                    new Object[] {
                                                        "X",
                                                        "X",
                                                        "0123456789",
                                                        "",
                                                        "",
                                                        "",
                                                        "Đồng",
                                                        "0",
                                                        "Đang hoạt động"
                                                    }))
                            .hasMessageContaining("không có quyền");
                });
    }

    @Test
    void everyRegisteredScreenBuildsAgainstRealSql() {
        scenario(
                () -> {
                    ScreenRegistry registry = new ScreenRegistry(manager, ignored -> {});
                    java.util.Set<Class<?>> panelTypes = new java.util.HashSet<>();
                    for (ScreenId screen : ScreenId.values()) {
                        assertThatCode(
                                        () -> {
                                            var panel = registry.create(screen);
                                            assertThat(panel).as(screen.name()).isNotNull();
                                            assertThat(panelTypes.add(panel.getClass()))
                                                    .as("Panel riêng cho " + screen)
                                                    .isTrue();
                                            if (panel instanceof ToolbarAware aware) aware.onShow();
                                            if (screen == ScreenId.INVENTORY
                                                    || screen == ScreenId.LOYALTY_VOUCHER
                                                    || screen == ScreenId.ACCOUNT_PERMISSION) {
                                                var tabs =
                                                        descendants(panel)
                                                                .filter(
                                                                        javax.swing.JTabbedPane
                                                                                        .class
                                                                                ::isInstance)
                                                                .map(
                                                                        javax.swing.JTabbedPane
                                                                                        .class
                                                                                ::cast)
                                                                .findFirst()
                                                                .orElseThrow();
                                                assertThat(tabs.getTabCount()).isEqualTo(2);
                                                for (int i = 0; i < tabs.getTabCount(); i++)
                                                    tabs.setSelectedIndex(i);
                                            }
                                            String action =
                                                    switch (screen) {
                                                        case SPA_SCHEDULE -> "+ Đặt lịch hẹn";
                                                        case SPA_CLOSE -> "Thanh toán";
                                                        case HOTEL_BOOKING -> "+ Đặt phòng";
                                                        case CHECKOUT -> "Quyết toán";
                                                        case PURCHASE -> "Trả công nợ";
                                                        case PENDING_INVOICES -> "Mở lại đơn";
                                                        case ORDERS -> "Xem / In lại";
                                                        default -> null;
                                                    };
                                            if (action != null)
                                                assertThat(
                                                                descendants(panel)
                                                                        .filter(
                                                                                javax.swing
                                                                                                .AbstractButton
                                                                                                .class
                                                                                        ::isInstance)
                                                                        .map(
                                                                                javax.swing
                                                                                                .AbstractButton
                                                                                                .class
                                                                                        ::cast)
                                                                        .filter(
                                                                                button ->
                                                                                        action
                                                                                                .equals(
                                                                                                        button
                                                                                                                .getText()))
                                                                        .anyMatch(
                                                                                button ->
                                                                                        button
                                                                                                        .getActionListeners()
                                                                                                        .length
                                                                                                > 0))
                                                        .as("Hành động " + screen)
                                                        .isTrue();
                                        })
                                .as(screen.name())
                                .doesNotThrowAnyException();
                    }
                });
    }

    private static java.util.stream.Stream<java.awt.Component> descendants(
            java.awt.Component component) {
        var self = java.util.stream.Stream.of(component);
        if (!(component instanceof java.awt.Container container)) return self;
        return java.util.stream.Stream.concat(
                self,
                java.util.Arrays.stream(container.getComponents())
                        .flatMap(SqlServerIntegrationTest::descendants));
    }

    @Test
    void heldCartsReloadCustomerAndCurrentPrices() {
        scenario(
                () -> {
                    SaleContext original = sale(2);
                    long held = SalesService.hold(original);
                    update("UPDATE san_pham SET gia_ban=120000 WHERE id=?", product);
                    SaleContext restored = new SaleContext();
                    SalesService.resume(held, restored);
                    assertThat(restored.customerCode()).isEqualTo(customerCode);
                    assertThat(restored.lines()).hasSize(1);
                    assertThat(restored.lines().get(0).quantity()).isEqualTo(2);
                    assertThat(restored.lines().get(0).unitPrice()).isEqualTo(120000);
                    assertThat(number("SELECT COUNT(*) FROM gio_hang_treo WHERE id=?", held))
                            .isZero();
                    assertThatThrownBy(() -> SalesService.resume(held, new SaleContext()))
                            .hasMessageContaining("Không tìm thấy");
                });
    }

    @Test
    void zeroBalanceHotelCanStillBeSettled() {
        scenario(
                () -> {
                    open();
                    long booking =
                            HotelService.book(
                                    petCode,
                                    roomCode,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    150000,
                                    "");
                    HotelService.checkIn(booking);
                    SaleContext bill = new SaleContext();
                    SalesService.checkout("HOTEL", booking, bill);
                    assertThat(bill.total()).isZero();
                    assertThat(bill.complete(CashShiftService.Method.CASH, 0, "Manager").total())
                            .isZero();
                    assertThat(
                                    text(
                                            scalar(
                                                    "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                            + " id=?",
                                                    booking)))
                            .isEqualTo("CHECKED_OUT");
                });
    }

    @Test
    void expiredStockAndChangedPricesCannotBeSold() {
        scenario(
                () -> {
                    open();
                    update(
                            "UPDATE lo_hang SET han_su_dung=DATEADD(day,-1,GETDATE()) WHERE"
                                    + " san_pham_id=?",
                            product);
                    assertThatThrownBy(
                                    () ->
                                            sale(1).complete(
                                                            CashShiftService.Method.CASH,
                                                            200000,
                                                            "Manager"))
                            .hasMessageContaining("Không đủ tồn");
                    update(
                            "UPDATE lo_hang SET han_su_dung=DATEADD(day,30,GETDATE()) WHERE"
                                    + " san_pham_id=?",
                            product);
                    SaleContext stale = sale(1);
                    update("UPDATE san_pham SET gia_ban=110000 WHERE id=?", product);
                    assertThatThrownBy(
                                    () ->
                                            stale.complete(
                                                    CashShiftService.Method.CASH,
                                                    200000,
                                                    "Manager"))
                            .hasMessageContaining("đã đổi");
                    assertThat(
                                    number(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            product))
                            .isEqualTo(6);
                });
    }

    @Test
    void catalogEditorsAndEveryReportQueryUseTheUnifiedSchema() {
        scenario(
                () -> {
                    List<CatalogService.Rows> catalogs =
                            List.of(
                                    CatalogService.pets(),
                                    CatalogService.staff(),
                                    CatalogService.accounts(),
                                    CatalogService.suppliers(),
                                    CatalogService.promotions(),
                                    CatalogService.vouchers(),
                                    CatalogService.spaServices(),
                                    CatalogService.rooms());
                    for (CatalogService.Rows catalog : catalogs) {
                        Object[] original = catalog.get(0).clone();
                        if (catalog.kind == CatalogType.ACCOUNTS
                                && "Hoạt động".equals(original[3])
                                && !employeeCode.equals(original[0])) continue;
                        CatalogService.save(catalog, original, original.clone());
                        assertThatCode(catalog::refresh)
                                .as(catalog.kind.name())
                                .doesNotThrowAnyException();
                    }
                    for (String grouping : List.of("method", "staff", "shift", "source"))
                        assertThat(ReportingService.revenue(7, grouping)).isNotNull();
                    assertThat(ReportingService.hotel(7)).isNotNull();
                    assertThat(ReportingService.appointments(7)).isNotNull();
                    assertThat(ReportingService.vouchers()).isNotNull();
                    assertThat(ReportingService.inventory()).isNotNull();
                    assertThat(ReportingService.expiry()).isNotNull();
                    assertThat(ReportingService.supplierDebt(7)).isNotNull();
                    assertThat(ReportingService.staff(7)).isNotNull();
                });
    }
}
