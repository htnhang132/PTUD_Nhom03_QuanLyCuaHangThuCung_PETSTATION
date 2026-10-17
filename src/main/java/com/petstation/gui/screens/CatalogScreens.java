package com.petstation.gui.screens;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JPanel;

import com.petstation.gui.components.Money;
import com.petstation.gui.components.common.PriceTableEditor;
import com.petstation.gui.data.DemoStore;
import com.petstation.gui.data.RolePermissions;
import com.petstation.gui.navigation.AuditTrail;
import com.petstation.gui.screens.EntityPanel.Config;
import com.petstation.gui.screens.EntityPanel.Field;
import com.petstation.gui.screens.EntityPanel.Type;
import com.petstation.model.Role;

/** Các màn Danh mục (chỉ CRUD) cấu hình trên {@link EntityPanel}. */
public final class CatalogScreens {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);

    private CatalogScreens() {}

    private static LocalDate date(Object v) {
        try { return LocalDate.parse(String.valueOf(v), DATE); } catch (DateTimeParseException e) { return null; }
    }

    public static JPanel customers() {
        Config cfg = Config.of("Danh mục khách hàng", "khách hàng").prefix("KH").search("Tìm theo tên, SĐT, mã khách...")
            .status(8, "Đang hoạt động", "Ngừng hoạt động");
        return new EntityPanel(cfg, List.of(
            Field.of("Mã khách", Type.TEXT).ro(),
            Field.of("Họ và tên", Type.TEXT).req(),
            Field.of("Số điện thoại", Type.PHONE).req().unique(),
            Field.of("Email", Type.EMAIL).hide(),
            Field.of("Địa chỉ", Type.TEXT),
            Field.of("Ghi chú", Type.AREA).hide(),
            Field.of("Hạng", Type.TEXT).ro().def("Đồng"),
            Field.of("Điểm", Type.INT).ro().def("0"),
            Field.of("Trạng thái", Type.COMBO).opts("Đang hoạt động", "Ngừng hoạt động")
        ), DemoStore.customers());
    }

    public static JPanel pets() {
        Config cfg = Config.of("Hồ sơ thú cưng", "thú cưng").prefix("TC").search("Tìm theo tên thú, giống, chủ nuôi...")
            .status(13, "Đang hoạt động", "Ngừng hoạt động")
            .validator(r -> {
                String w = String.valueOf(r[6]).trim();
                return w.isEmpty() || w.matches("\\d{1,3}([.,]\\d{1,2})?") ? null : "Cân nặng phải là số (kg), ví dụ 5.2.";
            });
        return new EntityPanel(cfg, List.of(
            Field.of("Mã thú", Type.TEXT).ro(),
            Field.of("Tên thú cưng", Type.TEXT).req(),
            Field.of("Loài", Type.COMBO).req().opts("Chó", "Mèo", "Khác"),
            Field.of("Giống", Type.TEXT).hide(),
            Field.of("Giới tính", Type.COMBO).opts("Đực", "Cái").hide(),
            Field.of("Ngày sinh", Type.DATE).hide(),
            Field.of("Cân nặng (kg)", Type.TEXT).hide(),
            Field.of("Màu lông", Type.TEXT).hide(),
            Field.of("Chủ nuôi", Type.COMBO).req().dyn(DemoStore::customerOptions),
            Field.of("Tiêm chủng", Type.AREA).hide(),
            Field.of("Dị ứng / lưu ý sức khỏe", Type.AREA).hide(),
            Field.of("Tính cách", Type.AREA).hide(),
            Field.of("Ảnh", Type.FILE).hide(),
            Field.of("Trạng thái", Type.COMBO).opts("Đang hoạt động", "Ngừng hoạt động")
        ), DemoStore.pets());
    }

    public static JPanel staff() {
        Config cfg = Config.of("Danh mục nhân viên", "nhân viên").prefix("NV").search("Tìm theo tên, SĐT, mã NV...")
            .status(6, "Đang làm", "Nghỉ việc")
            .onSaved((before, now) -> {
                boolean wasOut = before != null && "Nghỉ việc".equals(before[6]);
                if ("Nghỉ việc".equals(now[6]) && !wasOut) {
                    int locked = DemoStore.lockAccountsOf(String.valueOf(now[0]));
                    if (locked > 0) AuditTrail.log("Khóa", "Tài khoản của " + now[0] + " - " + now[1] + " (nhân viên nghỉ việc)");
                }
            });
        return new EntityPanel(cfg, List.of(
            Field.of("Mã NV", Type.TEXT).ro(),
            Field.of("Họ và tên", Type.TEXT).req(),
            Field.of("Số điện thoại", Type.PHONE).req().unique(),
            Field.of("CCCD", Type.CCCD).req().unique(),
            Field.of("Chức vụ", Type.COMBO).req().opts(roleNames()),
            Field.of("Ngày vào làm", Type.DATE).req(),
            Field.of("Trạng thái", Type.COMBO).opts("Đang làm", "Nghỉ phép", "Nghỉ việc")
        ), DemoStore.staff());
    }

    private static String[] roleNames() {
        Role[] roles = Role.values();
        String[] out = new String[roles.length];
        for (int i = 0; i < roles.length; i++) out[i] = roles[i].displayName();
        return out;
    }

    /** Sản phẩm + tab Lịch sử giá. Tồn kho chỉ đọc: chỉ đổi qua Nhập kho, Xuất kho, Kiểm kê. */
    public static JPanel products() {
        Config cfg = Config.of("Danh mục sản phẩm", "sản phẩm").prefix("SP").search("Tìm theo mã, tên sản phẩm...")
            .status(9, "Đang kinh doanh", "Ngừng kinh doanh")
            .onSaved((before, now) -> {
                String oldPrice = before == null ? "" : String.valueOf(before[4]);
                if (before == null || !oldPrice.equals(String.valueOf(now[4]))) {
                    DemoStore.recordPriceChange(String.valueOf(now[0]), String.valueOf(now[1]), oldPrice, String.valueOf(now[4]), AuditTrail.user());
                }
            });
        JPanel list = new EntityPanel(cfg, List.of(
            Field.of("Mã SP", Type.TEXT).ro(),
            Field.of("Tên sản phẩm", Type.TEXT).req().unique(),
            Field.of("Nhóm hàng", Type.COMBO).req().opts("Thức ăn", "Phụ kiện", "Grooming", "Vệ sinh", "Thuốc"),
            Field.of("Đơn vị tính", Type.TEXT).req(),
            Field.of("Giá bán", Type.MONEY).req(),
            Field.of("Giá nhập tham khảo", Type.MONEY).hide(),
            Field.of("Tồn tối thiểu", Type.INT).req().hide().def("5"),
            Field.of("Quản lý lô / HSD", Type.COMBO).opts("Có", "Không").hide(),
            Field.of("Tồn hiện tại (chỉ đổi qua Nhập, Xuất, Kiểm kê)", Type.INT).ro().def("0"),
            Field.of("Trạng thái", Type.COMBO).opts("Đang kinh doanh", "Ngừng kinh doanh")
        ), DemoStore.products());
        return new TabbedCatalogPanel(new String[]{"Danh sách sản phẩm", "Lịch sử giá"}, new JPanel[]{list, new PriceHistoryPanel()});
    }

    public static JPanel suppliers() {
        Config cfg = Config.of("Danh mục nhà cung cấp", "nhà cung cấp").prefix("NCC").search("Tìm theo tên, người liên hệ, MST...")
            .status(7, "Đang hợp tác", "Ngừng hợp tác");
        return new EntityPanel(cfg, List.of(
            Field.of("Mã NCC", Type.TEXT).ro(),
            Field.of("Tên nhà cung cấp", Type.TEXT).req(),
            Field.of("Người liên hệ", Type.TEXT),
            Field.of("Số điện thoại", Type.PHONE).req(),
            Field.of("Địa chỉ", Type.TEXT),
            Field.of("Mã số thuế", Type.TEXT).unique(),
            Field.of("Điều khoản thanh toán", Type.COMBO).opts("Trả ngay", "15 ngày", "30 ngày", "45 ngày"),
            Field.of("Trạng thái", Type.COMBO).opts("Đang hợp tác", "Ngừng hợp tác")
        ), DemoStore.suppliers());
    }

    /** Hết hạn tự chuyển "Ngừng áp dụng"; chưa tới ngày bắt đầu là "Chưa áp dụng". */
    public static JPanel promotions() {
        Config cfg = Config.of("Danh mục khuyến mãi", "khuyến mãi").prefix("KM").search("Tìm theo mã, tên chương trình...")
            .status(8, "Đang áp dụng", "Ngừng áp dụng")
            .normalizer(r -> {
                LocalDate today = LocalDate.now();
                LocalDate from = date(r[6]);
                LocalDate to = date(r[7]);
                String status = String.valueOf(r[8]);
                if (to != null && to.isBefore(today)) r[8] = "Ngừng áp dụng";
                else if (!"Ngừng áp dụng".equals(status)) r[8] = from != null && from.isAfter(today) ? "Chưa áp dụng" : "Đang áp dụng";
            })
            .validator(r -> {
                LocalDate from = date(r[6]);
                LocalDate to = date(r[7]);
                if (from != null && to != null && to.isBefore(from)) return "Đến ngày phải sau hoặc bằng Từ ngày.";
                if ("Giảm %".equals(r[2])) {
                    long v = Money.parse(String.valueOf(r[4]));
                    if (v < 1 || v > 100) return "Giá trị giảm % phải từ 1 đến 100.";
                }
                return null;
            });
        return new EntityPanel(cfg, List.of(
            Field.of("Mã KM", Type.TEXT).ro(),
            Field.of("Tên chương trình", Type.TEXT).req(),
            Field.of("Loại", Type.COMBO).opts("Giảm %", "Giảm tiền", "Tặng"),
            Field.of("Phạm vi", Type.COMBO).opts("Sản phẩm", "Dịch vụ", "Cả đơn"),
            Field.of("Giá trị (% hoặc ₫)", Type.INT).req().hide(),
            Field.of("Đơn tối thiểu", Type.MONEY).hide().def("0"),
            Field.of("Từ ngày", Type.DATE).req(),
            Field.of("Đến ngày", Type.DATE).req(),
            Field.of("Trạng thái", Type.COMBO).opts("Chưa áp dụng", "Đang áp dụng", "Ngừng áp dụng")
        ), DemoStore.promotions());
    }

    /** Tích điểm & Voucher: 2 tab. Voucher dùng hết hoặc hết hạn tự đổi trạng thái. */
    public static JPanel loyaltyVoucher() {
        Config cfg = Config.of("Danh mục voucher", "voucher").search("Tìm theo mã voucher...")
            .status(6, "Còn hiệu lực", "Ngừng")
            .normalizer(r -> {
                LocalDate exp = date(r[2]);
                String status = String.valueOf(r[6]);
                long qty = Money.parse(String.valueOf(r[3]));
                long used = Money.parse(String.valueOf(r[4]));
                if ("Ngừng".equals(status)) return;
                if (exp != null && exp.isBefore(LocalDate.now())) r[6] = "Hết hạn";
                else if (qty >= 0 && used >= qty) r[6] = "Hết lượt";
                else r[6] = "Còn hiệu lực";
            })
            .validator(r -> Money.parse(String.valueOf(r[3])) < Money.parse(String.valueOf(r[4])) ? "Số lượng không được nhỏ hơn số đã dùng." : null);
        JPanel vouchers = new EntityPanel(cfg, List.of(
            Field.of("Mã voucher", Type.TEXT).req().unique(),
            Field.of("Giá trị (₫)", Type.MONEY).req(),
            Field.of("Hạn dùng", Type.DATE).req(),
            Field.of("Số lượng", Type.INT).req(),
            Field.of("Đã dùng", Type.INT).ro().def("0"),
            Field.of("Đơn tối thiểu", Type.MONEY).def("0"),
            Field.of("Trạng thái", Type.COMBO).opts("Còn hiệu lực", "Hết hạn", "Hết lượt", "Ngừng")
        ), DemoStore.vouchers());
        return new TabbedCatalogPanel(new String[]{"Quy tắc điểm & hạng", "Voucher"}, new JPanel[]{new LoyaltyRulesPanel(), vouchers});
    }

    public static JPanel spaServices() {
        Config cfg = Config.of("Danh mục dịch vụ Spa", "dịch vụ Spa").prefix("DV").search("Tìm theo mã, tên dịch vụ...")
            .status(5, "Đang hoạt động", "Ngừng hoạt động")
            .validator(r -> PriceTableEditor.minPrice(String.valueOf(r[4])) < 0 ? "Bảng giá cần ít nhất 1 dòng có giá." : null);
        return new EntityPanel(cfg, List.of(
            Field.of("Mã DV", Type.TEXT).ro(),
            Field.of("Tên dịch vụ", Type.TEXT).req().unique(),
            Field.of("Mô tả", Type.AREA).hide(),
            Field.of("Thời gian dự kiến (phút)", Type.INT).req(),
            Field.of("Bảng giá theo loài / cân nặng", Type.CUSTOM).editor(PriceTableEditor::new)
                .show(v -> { long m = PriceTableEditor.minPrice(v); return m < 0 ? "—" : "Từ " + Money.format(m); }),
            Field.of("Trạng thái", Type.COMBO).opts("Đang hoạt động", "Ngừng hoạt động")
        ), DemoStore.spaServices());
    }

    /** Trạng thái phòng do nghiệp vụ tự cập nhật; chỉ chỉnh tay Trống ↔ Bảo trì. */
    public static JPanel hotelRooms() {
        Config cfg = Config.of("Danh mục phòng Pet Hotel", "phòng").search("Tìm theo mã phòng, loại phòng...")
            .status(5, "Trống", "Ngừng hoạt động")
            .deleteGuard(r -> {
                String st = String.valueOf(r[5]);
                return "Có thú".equals(st) || "Đang dọn".equals(st) ? "Phòng " + r[0] + " đang \"" + st + "\", chưa thể ngừng hoạt động." : null;
            });
        return new EntityPanel(cfg, List.of(
            Field.of("Mã phòng", Type.TEXT).req().unique(),
            Field.of("Loại phòng", Type.COMBO).req().opts("Tiêu chuẩn", "VIP", "Mèo"),
            Field.of("Sức chứa (thú)", Type.INT).req().def("1"),
            Field.of("Giá / ngày", Type.MONEY).req(),
            Field.of("Ghi chú", Type.TEXT),
            Field.of("Trạng thái (chỉ chỉnh tay Trống / Bảo trì)", Type.COMBO).opts("Trống", "Bảo trì")
                .editableWhen(v -> v.equals("Trống") || v.equals("Bảo trì") || v.equals("Ngừng hoạt động"))
        ), DemoStore.rooms());
    }

    /** Tài khoản & phân quyền (chỉ Cửa hàng trưởng): mỗi tài khoản gắn 1 nhân viên và 1 trong 4 vai trò. */
    public static JPanel accounts() {
        Config cfg = Config.of("Tài khoản", "tài khoản").search("Tìm theo tên đăng nhập, nhân viên...")
            .status(3, "Hoạt động", "Đã khóa")
            .extra("Khóa / Mở khóa", r -> {
                boolean locked = "Đã khóa".equals(r[3]);
                if ("admin".equals(r[0]) && !locked) return "Không thể khóa tài khoản admin.";
                r[3] = locked ? "Hoạt động" : "Đã khóa";
                AuditTrail.log(locked ? "Mở khóa" : "Khóa", "Tài khoản " + r[0]);
                return (locked ? "Đã mở khóa " : "Đã khóa ") + r[0] + ".";
            })
            .extra("Đặt lại mật khẩu", r -> {
                String temp = "PS-" + (100000 + new java.util.Random().nextInt(900000));
                AuditTrail.log("Đặt lại mật khẩu", "Tài khoản " + r[0]);
                com.petstation.gui.components.Dialogs.info(null, "Mật khẩu tạm của " + r[0] + ": " + temp
                    + "\nTài khoản phải đổi mật khẩu ở lần đăng nhập kế tiếp.");
                return "Đã đặt lại mật khẩu cho " + r[0] + ".";
            });
        JPanel list = new EntityPanel(cfg, List.of(
            Field.of("Tên đăng nhập", Type.TEXT).req().unique(),
            Field.of("Nhân viên", Type.COMBO).req().unique().dyn(DemoStore::staffOptions),
            Field.of("Vai trò", Type.COMBO).req().opts(roleNames()),
            Field.of("Trạng thái", Type.COMBO).opts("Hoạt động", "Đã khóa")
        ), DemoStore.accounts());
        return new TabbedCatalogPanel(new String[]{"Tài khoản", "Ma trận quyền"}, new JPanel[]{list, new PermissionMatrixPanel()});
    }

    /** Dùng cho kiểm thử: số quyền của vai trò. */
    static int permissionCount(Role role) { return RolePermissions.of(role).size(); }
}
