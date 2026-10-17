package com.petstation.gui.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Kho dữ liệu mẫu dùng chung trong phiên (chưa lưu database) để các màn liên thông với nhau:
 * Thú cưng lấy chủ nuôi từ Khách hàng, Nhân viên nghỉ việc khóa tài khoản, Sản phẩm ghi Lịch sử giá...
 * Mọi giá trị lưu dạng chuỗi.
 */
public final class DemoStore {
    private static final List<Object[]> CUSTOMERS = new ArrayList<>();
    private static final List<Object[]> PETS = new ArrayList<>();
    private static final List<Object[]> STAFF = new ArrayList<>();
    private static final List<Object[]> ACCOUNTS = new ArrayList<>();
    private static final List<Object[]> PRODUCTS = new ArrayList<>();
    private static final List<Object[]> PRICE_HISTORY = new ArrayList<>();
    private static final List<Object[]> SUPPLIERS = new ArrayList<>();
    private static final List<Object[]> PROMOTIONS = new ArrayList<>();
    private static final List<Object[]> VOUCHERS = new ArrayList<>();
    private static final List<Object[]> SPA_SERVICES = new ArrayList<>();
    private static final List<Object[]> ROOMS = new ArrayList<>();
    private static final List<Runnable> PRICE_LISTENERS = new ArrayList<>();

    static {
        // Khách hàng: Mã, Họ tên, SĐT, Email, Địa chỉ, Ghi chú, Hạng, Điểm, Trạng thái
        c("KH001", "Nguyễn Văn An", "0901234567", "an.nguyen@email.com", "12 Nguyễn Trãi, Hà Nội", "Thích giao hàng buổi tối", "Vàng", "1240");
        c("KH002", "Trần Thị Bình", "0912345678", "binh.tran@email.com", "45 Lê Lợi, TP.HCM", "", "Kim Cương", "4860");
        c("KH003", "Lê Hoàng Cường", "0923456789", "", "8 Hùng Vương, Đà Nẵng", "", "Bạch Kim", "3210");
        c("KH004", "Phạm Minh Đức", "0934567890", "duc.pham@email.com", "21 Lạch Tray, Hải Phòng", "", "Bạc", "840");
        c("KH005", "Hoàng Thị Em", "0945678901", "", "5 Ninh Kiều, Cần Thơ", "Dị ứng mùi nước hoa mạnh", "Đồng", "230");
        c("KH006", "Vũ Quang Hải", "0956789012", "hai.vu@email.com", "77 Cầu Giấy, Hà Nội", "", "Vàng", "1980");
        // Thú cưng: Mã, Tên, Loài, Giống, Giới tính, Ngày sinh, Cân nặng, Màu lông, Chủ nuôi, Tiêm chủng, Dị ứng/lưu ý, Tính cách, Ảnh, Trạng thái
        p("TC001", "Mochi", "Chó", "Poodle", "Đực", "12/03/2022", "5.2", "Trắng", "KH001 - Nguyễn Văn An", "Dại (01/2026), 5 bệnh (02/2026)", "", "Hiền, sợ tiếng máy sấy");
        p("TC002", "Bống", "Mèo", "Anh lông ngắn", "Cái", "05/07/2021", "4.1", "Xám", "KH002 - Trần Thị Bình", "Dại (03/2026)", "Dị ứng cá ngừ", "Nhút nhát");
        p("TC003", "Lucky", "Chó", "Golden Retriever", "Đực", "20/11/2020", "29.5", "Vàng", "KH003 - Lê Hoàng Cường", "Dại, 7 bệnh (12/2025)", "", "Năng động, thân thiện");
        p("TC004", "Miu", "Mèo", "Ba Tư", "Cái", "02/02/2023", "3.8", "Kem", "KH004 - Phạm Minh Đức", "", "Hen suyễn nhẹ", "Hay cào khi bị bế");
        p("TC005", "Coco", "Chó", "Corgi", "Cái", "14/09/2021", "11.0", "Nâu trắng", "KH006 - Vũ Quang Hải", "Dại (06/2026)", "", "Ham ăn");
        // Nhân viên: Mã NV, Họ tên, SĐT, CCCD, Chức vụ, Ngày vào làm, Trạng thái
        s("NV001", "Nguyễn Văn An", "0901111222", "001090012345", "Cửa hàng trưởng", "01/03/2020");
        s("NV002", "Phạm Thu Lan", "0902222333", "001092045678", "Nhân viên bán hàng", "15/06/2021");
        s("NV003", "Hoàng Văn Nam", "0903333444", "001088078901", "Nhân viên kho", "10/01/2022");
        s("NV004", "Trần Thị Hoa", "0904444555", "001095011223", "Nhân viên Spa/Hotel", "05/09/2022");
        s("NV005", "Lê Văn Hùng", "0905555666", "001093099887", "Nhân viên Spa/Hotel", "20/02/2023");
        s("NV006", "Đặng Minh Tú", "0906666777", "001096055443", "Nhân viên bán hàng", "01/08/2024");
        // Tài khoản: Tên đăng nhập, Nhân viên, Vai trò, Trạng thái
        a("admin", "NV001 - Nguyễn Văn An", "Cửa hàng trưởng");
        a("lan.pt", "NV002 - Phạm Thu Lan", "Nhân viên bán hàng");
        a("nam.hv", "NV003 - Hoàng Văn Nam", "Nhân viên kho");
        a("hoa.tt", "NV004 - Trần Thị Hoa", "Nhân viên Spa/Hotel");
        // Sản phẩm: Mã, Tên, Nhóm hàng, ĐVT, Giá bán, Giá nhập, Tồn tối thiểu, Quản lý lô/HSD, Tồn hiện tại, Trạng thái
        pr("SP001", "Royal Canin Adult 2kg", "Thức ăn", "Túi", "285000", "240000", "10", "Có", "47");
        pr("SP002", "Pate Whiskas 400g", "Thức ăn", "Lon", "45000", "36000", "30", "Có", "82");
        pr("SP003", "Vòng cổ da size M", "Phụ kiện", "Cái", "120000", "80000", "5", "Không", "3");
        pr("SP004", "Sữa tắm Spa Dogs 500ml", "Grooming", "Chai", "89000", "62000", "10", "Có", "28");
        pr("SP005", "Lồng vận chuyển size L", "Phụ kiện", "Cái", "450000", "330000", "3", "Không", "8");
        pr("SP006", "Đồ chơi bóng cao su", "Phụ kiện", "Cái", "35000", "20000", "10", "Không", "15");
        pr("SP007", "Thức ăn hạt Orijen 1.8kg", "Thức ăn", "Túi", "520000", "430000", "5", "Có", "2");
        pr("SP008", "Cát vệ sinh mèo 5L", "Vệ sinh", "Túi", "75000", "55000", "15", "Có", "31");
        pr("SP009", "Bàn chải lông chó", "Grooming", "Cái", "55000", "40000", "5", "Không", "12");
        pr("SP010", "Thuốc nhỏ gáy Frontline", "Thuốc", "Hộp", "180000", "130000", "5", "Có", "0");
        pr("SP011", "Snack xương gặm răng", "Thức ăn", "Gói", "42000", "30000", "10", "Có", "40");
        pr("SP012", "Áo mưa chó size S", "Phụ kiện", "Cái", "95000", "65000", "5", "Không", "9");
        // Nhà cung cấp: Mã, Tên, Người liên hệ, SĐT, Địa chỉ, MST, Điều khoản, Trạng thái
        sup("NCC001", "Royal Canin VN", "Nguyễn Minh Tâm", "0243825678", "Hà Nội", "0101234567", "30 ngày");
        sup("NCC002", "Mars Petcare VN", "Trần Thành Long", "0283826789", "TP.HCM", "0309876543", "15 ngày");
        sup("NCC003", "Orijen Import", "Lê Đức Anh", "0243827890", "Hà Nội", "0105551234", "Trả ngay");
        sup("NCC004", "PetShop Thú Y", "Phạm Quang Vinh", "0911222333", "TP.HCM", "0312223344", "30 ngày");
        // Khuyến mãi: Mã, Tên, Loại, Phạm vi, Giá trị, Đơn tối thiểu, Từ ngày, Đến ngày, Trạng thái (ngày tính theo hôm nay để dữ liệu mẫu luôn hợp lệ)
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        km("KM001", "Giảm 10% sản phẩm trong tháng", "Giảm %", "Sản phẩm", "10", "0", d(monthStart), d(monthStart.plusMonths(1).minusDays(1)), "Đang áp dụng");
        km("KM002", "Giảm 50.000₫ đơn từ 500.000₫", "Giảm tiền", "Cả đơn", "50000", "500000", d(monthStart.minusMonths(1)), d(monthStart.minusDays(1)), "Đang áp dụng");
        km("KM003", "Spa giảm 15% tháng sau", "Giảm %", "Dịch vụ", "15", "0", d(monthStart.plusMonths(1)), d(monthStart.plusMonths(2).minusDays(1)), "Chưa áp dụng");
        // Voucher: Mã, Giá trị, Hạn dùng, Số lượng, Đã dùng, Đơn tối thiểu, Trạng thái
        vo("VC-WELCOME50", "50000", d(today.plusDays(90)), "100", "37", "300000");
        vo("VC-SPA100", "100000", d(today.plusDays(30)), "20", "20", "500000");
        vo("VC-OLD", "30000", d(today.minusDays(100)), "50", "12", "200000");
        // Dịch vụ Spa: Mã, Tên, Mô tả, Thời gian (phút), Bảng giá, Trạng thái
        sv("DV001", "Tắm và sấy khô", "Tắm, sấy, chải lông", "60",
            "Chó|< 5kg|150000;Chó|5 - 10kg|200000;Chó|> 10kg|280000;Mèo|Mọi cân nặng|180000");
        sv("DV002", "Cắt tỉa tạo kiểu", "Cắt tỉa theo giống", "90",
            "Chó|< 5kg|230000;Chó|5 - 10kg|300000;Chó|> 10kg|400000;Mèo|Mọi cân nặng|250000");
        sv("DV003", "Cắt móng, vệ sinh tai", "Cắt và dũa móng, lau tai", "20", "Chó|Mọi cân nặng|80000;Mèo|Mọi cân nặng|80000");
        // Phòng: Mã phòng, Loại phòng, Sức chứa, Giá/ngày, Ghi chú, Trạng thái
        rm("P01", "Tiêu chuẩn", "1", "150000", "", "Có thú");
        rm("P02", "Tiêu chuẩn", "1", "150000", "", "Trống");
        rm("P03", "Mèo", "1", "120000", "Có cây leo", "Đang dọn");
        rm("P04", "VIP", "2", "300000", "Điều hòa riêng", "Có thú");
        rm("P05", "VIP", "2", "300000", "Điều hòa riêng", "Trống");
        rm("P06", "Tiêu chuẩn", "1", "150000", "Sửa quạt thông gió", "Bảo trì");
        // Lịch sử giá ban đầu
        PRICE_HISTORY.add(new Object[]{"01/10/2026 08:00", "SP001", "Royal Canin Adult 2kg", "270000", "285000", "Nguyễn Văn An"});
    }

    private DemoStore() {}

    private static String d(LocalDate date) { return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }

    private static void c(String... v) { CUSTOMERS.add(row(v, "Đang hoạt động")); }
    private static void p(String... v) { PETS.add(row(v, "", "Đang hoạt động")); }
    private static void s(String... v) { STAFF.add(row(v, "Đang làm")); }
    private static void a(String... v) { ACCOUNTS.add(row(v, "Hoạt động")); }
    private static void pr(String... v) { PRODUCTS.add(row(v, "Đang kinh doanh")); }
    private static void sup(String... v) { SUPPLIERS.add(row(v, "Đang hợp tác")); }
    private static void km(String... v) { PROMOTIONS.add(v.clone()); }
    private static void vo(String... v) { VOUCHERS.add(row(v, "Còn hiệu lực")); }
    private static void sv(String... v) { SPA_SERVICES.add(row(v, "Đang hoạt động")); }
    private static void rm(String... v) { ROOMS.add(v.clone()); }

    private static Object[] row(String[] v, String... tail) {
        Object[] r = new Object[v.length + tail.length];
        System.arraycopy(v, 0, r, 0, v.length);
        System.arraycopy(tail, 0, r, v.length, tail.length);
        return r;
    }

    public static List<Object[]> customers() { return CUSTOMERS; }
    public static List<Object[]> pets() { return PETS; }
    public static List<Object[]> staff() { return STAFF; }
    public static List<Object[]> accounts() { return ACCOUNTS; }
    public static List<Object[]> products() { return PRODUCTS; }
    public static List<Object[]> priceHistory() { return PRICE_HISTORY; }
    public static List<Object[]> suppliers() { return SUPPLIERS; }
    public static List<Object[]> promotions() { return PROMOTIONS; }
    public static List<Object[]> vouchers() { return VOUCHERS; }
    public static List<Object[]> spaServices() { return SPA_SERVICES; }
    public static List<Object[]> rooms() { return ROOMS; }

    /** "KH001 - Tên" cho khách đang hoạt động (dùng cho ô chọn chủ nuôi). */
    public static List<String> customerOptions() {
        List<String> out = new ArrayList<>();
        for (Object[] r : CUSTOMERS) if (!"Ngừng hoạt động".equals(r[8])) out.add(r[0] + " - " + r[1]);
        return out;
    }

    /** "NV001 - Tên" cho nhân viên chưa nghỉ việc. */
    public static List<String> staffOptions() {
        List<String> out = new ArrayList<>();
        for (Object[] r : STAFF) if (!"Nghỉ việc".equals(r[6])) out.add(r[0] + " - " + r[1]);
        return out;
    }

    /** Khóa mọi tài khoản của nhân viên (khi nghỉ việc). Trả về số tài khoản bị khóa. */
    public static int lockAccountsOf(String staffCode) {
        int n = 0;
        for (Object[] r : ACCOUNTS) {
            if (String.valueOf(r[1]).startsWith(staffCode + " ") && !"Đã khóa".equals(r[3])) { r[3] = "Đã khóa"; n++; }
        }
        return n;
    }

    public static void recordPriceChange(String code, String name, String oldPrice, String newPrice, String user) {
        PRICE_HISTORY.add(new Object[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), code, name, oldPrice, newPrice, user});
        for (Runnable r : List.copyOf(PRICE_LISTENERS)) r.run();
    }

    public static void addPriceListener(Runnable r) { PRICE_LISTENERS.add(r); }
}
