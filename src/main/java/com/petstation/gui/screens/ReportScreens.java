package com.petstation.gui.screens;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.petstation.gui.components.Money;
import com.petstation.gui.data.DemoStore;
import com.petstation.gui.navigation.AppSettings;
import com.petstation.gui.screens.ReportPanel.Data;
import com.petstation.gui.screens.ReportPanel.View;

/**
 * Các màn Thống kê. Doanh thu bán hàng, Spa, Hotel là số liệu mẫu co giãn theo kỳ đã chọn;
 * tồn thấp, khách hàng, voucher, nhân viên, nhà cung cấp, phòng lấy từ dữ liệu Danh mục hiện có.
 */
public final class ReportScreens {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM");
    private static final double DAILY_REVENUE = 8_450_000;

    private ReportScreens() {}

    private static String m(double v) { return Money.format(Math.round(v / 1000.0) * 1000); }

    private static Data data(String[] labels, double[] values, boolean money, String unit, String[] cols, Object[][] rows, String[] cardT, String[] cardV) {
        return new Data(labels, values, money, unit, cols, rows, cardT, cardV);
    }

    /** Chia tổng theo tỷ lệ cho các nhóm. */
    private static Data shares(double total, String[] names, double[] pct, String col, boolean money, String[] cardT, String[] cardV) {
        double[] vals = new double[names.length];
        Object[][] rows = new Object[names.length][];
        for (int i = 0; i < names.length; i++) {
            vals[i] = total * pct[i];
            rows[i] = new Object[]{names[i], m(vals[i]), Math.round(pct[i] * 100) + "%"};
        }
        return data(names, vals, money, "", new String[]{col, "Doanh thu", "Tỷ trọng"}, rows, cardT, cardV);
    }

    // ───────────── Doanh thu
    public static JPanel revenue() {
        List<View> views = List.of(
            new View("Theo ngày", d -> {
                boolean today = d <= 1;
                int n = today ? 4 : (int) Math.max(1, Math.min(10, d));
                String[] slots = {"8h–11h", "11h–14h", "14h–18h", "18h–21h"};
                String[] labels = new String[n];
                double[] vals = new double[n];
                Object[][] rows = new Object[n][];
                double perBar = DAILY_REVENUE * d / n;
                double sum = 0;
                for (int i = 0; i < n; i++) {
                    labels[i] = today ? slots[i] : LocalDate.now().minusDays((long) ((n - 1 - i) * d / n)).format(DAY);
                    vals[i] = perBar * (0.85 + 0.3 * Math.abs(Math.sin(i * 1.7 + 1)));
                    sum += vals[i];
                    rows[i] = new Object[]{labels[i], Math.round(vals[i] / 280_000), m(vals[i])};
                }
                long orders = Math.round(sum / 280_000);
                return data(labels, vals, true, "", new String[]{today ? "Khung giờ" : "Ngày", "Số hóa đơn", "Doanh thu"}, rows,
                    new String[]{"Tổng doanh thu", "Số hóa đơn", "Trung bình / hóa đơn", "So với kỳ trước"},
                    new String[]{m(sum), String.valueOf(orders), m(sum / Math.max(1, orders)), "+8%"});
            }),
            new View("Theo nhân viên", d -> shares(DAILY_REVENUE * d, new String[]{"Phạm Thu Lan", "Đặng Minh Tú", "Trần Thị Hoa", "Lê Văn Hùng"},
                new double[]{0.42, 0.30, 0.17, 0.11}, "Nhân viên", true,
                new String[]{"Tổng doanh thu", "Nhân viên", "Cao nhất", "Thấp nhất"},
                new String[]{m(DAILY_REVENUE * d), "4", "Phạm Thu Lan", "Lê Văn Hùng"})),
            new View("Theo ca", d -> shares(DAILY_REVENUE * d, new String[]{"Ca sáng", "Ca chiều", "Ca tối"}, new double[]{0.34, 0.46, 0.20}, "Ca", true,
                new String[]{"Tổng doanh thu", "Số ca", "Ca cao nhất", "TB / ca"},
                new String[]{m(DAILY_REVENUE * d), String.valueOf((int) (3 * d)), "Ca chiều", m(DAILY_REVENUE / 3)})),
            new View("Theo nguồn", d -> shares(DAILY_REVENUE * d, new String[]{"Bán hàng", "Spa", "Hotel"}, new double[]{0.62, 0.24, 0.14}, "Nguồn", true,
                new String[]{"Tổng doanh thu", "Bán hàng", "Spa", "Hotel"},
                new String[]{m(DAILY_REVENUE * d), m(DAILY_REVENUE * d * 0.62), m(DAILY_REVENUE * d * 0.24), m(DAILY_REVENUE * d * 0.14)})),
            new View("Theo phương thức", d -> shares(DAILY_REVENUE * d, new String[]{"Tiền mặt", "Chuyển khoản", "QR Code", "Thẻ"},
                new double[]{0.52, 0.27, 0.13, 0.08}, "Phương thức", true,
                new String[]{"Tổng doanh thu", "Tiền mặt", "Không dùng tiền mặt", "Phổ biến nhất"},
                new String[]{m(DAILY_REVENUE * d), m(DAILY_REVENUE * d * 0.52), m(DAILY_REVENUE * d * 0.48), "Tiền mặt"}))
        );
        return new ReportPanel("Thống kê doanh thu", "Theo ngày, nhân viên, ca, nguồn và phương thức thanh toán", "Thong_ke_doanh_thu", views);
    }

    // ───────────── Hàng hóa
    public static JPanel goods() {
        List<View> views = List.of(
            new View("Bán chạy", d -> {
                List<Object[]> products = DemoStore.products();
                int n = Math.min(6, products.size());
                String[] labels = new String[n];
                double[] vals = new double[n];
                Object[][] rows = new Object[n][];
                double total = 0;
                for (int i = 0; i < n; i++) {
                    Object[] p = products.get(i);
                    long qty = Math.round((40 - i * 5) * Math.max(1, d / 7.0));
                    long price = Money.parse(String.valueOf(p[4]));
                    labels[i] = String.valueOf(p[1]);
                    vals[i] = qty;
                    total += qty * price;
                    rows[i] = new Object[]{p[0], p[1], qty, m(qty * price)};
                }
                return data(labels, vals, false, "", new String[]{"Mã SP", "Tên sản phẩm", "Đã bán", "Doanh thu"}, rows,
                    new String[]{"Doanh thu hàng hóa", "Sản phẩm", "Bán chạy nhất", "Nhóm hàng"},
                    new String[]{m(total), String.valueOf(products.size()), labels.length > 0 ? labels[0] : "—", "Thức ăn"});
            }),
            new View("Nhập – xuất – tồn", d -> {
                List<Object[]> products = DemoStore.products();
                Object[][] rows = new Object[products.size()][];
                String[] labels = new String[products.size()];
                double[] vals = new double[products.size()];
                long totalStock = 0;
                for (int i = 0; i < products.size(); i++) {
                    Object[] p = products.get(i);
                    long stock = Money.parse(String.valueOf(p[8]));
                    long in = Math.round(20 * Math.max(1, d / 7.0));
                    long out = Math.round(18 * Math.max(1, d / 7.0));
                    labels[i] = String.valueOf(p[0]);
                    vals[i] = stock;
                    totalStock += stock;
                    rows[i] = new Object[]{p[0], p[1], Math.max(0, stock - in + out), in, out, stock};
                }
                return data(labels, vals, false, "", new String[]{"Mã SP", "Tên sản phẩm", "Tồn đầu kỳ", "Nhập", "Xuất", "Tồn cuối kỳ"}, rows,
                    new String[]{"Tổng tồn hiện tại", "Số mặt hàng", "Nhập kỳ này", "Xuất kỳ này"},
                    new String[]{String.valueOf(totalStock), String.valueOf(products.size()), String.valueOf(Math.round(20 * Math.max(1, d / 7.0)) * products.size()),
                        String.valueOf(Math.round(18 * Math.max(1, d / 7.0)) * products.size())});
            }),
            new View("Tồn thấp", d -> {
                List<Object[]> low = new ArrayList<>();
                for (Object[] p : DemoStore.products()) {
                    long stock = Money.parse(String.valueOf(p[8]));
                    long min = Math.max(Money.parse(String.valueOf(p[6])), 0);
                    if (stock <= min || stock <= AppSettings.lowStockThreshold() / 3) low.add(p);
                }
                Object[][] rows = new Object[low.size()][];
                String[] labels = new String[low.size()];
                double[] vals = new double[low.size()];
                for (int i = 0; i < low.size(); i++) {
                    Object[] p = low.get(i);
                    labels[i] = String.valueOf(p[0]);
                    vals[i] = Money.parse(String.valueOf(p[8]));
                    rows[i] = new Object[]{p[0], p[1], p[8], p[6], "Cần nhập"};
                }
                return data(labels, vals, false, "", new String[]{"Mã SP", "Tên sản phẩm", "Tồn hiện tại", "Tồn tối thiểu", "Trạng thái"}, rows,
                    new String[]{"Sản phẩm tồn thấp", "Ngưỡng cài đặt", "", ""},
                    new String[]{String.valueOf(low.size()), String.valueOf(AppSettings.lowStockThreshold()), "—", "—"});
            }),
            new View("Sắp hết hạn", d -> {
                String[][] lots = {{"SP002", "Pate Whiskas 400g", "L2608", "12"}, {"SP004", "Sữa tắm Spa Dogs 500ml", "L2701", "28"},
                    {"SP001", "Royal Canin Adult 2kg", "L2703", "44"}, {"SP008", "Cát vệ sinh mèo 5L", "L2705", "75"}};
                List<Object[]> rows = new ArrayList<>();
                List<String> lab = new ArrayList<>();
                List<Double> val = new ArrayList<>();
                for (String[] l : lots) {
                    int days = Integer.parseInt(l[3]);
                    if (days > AppSettings.expiryWarnDays() + 15 && false) continue;
                    String tone = days <= AppSettings.expiryWarnDays() ? "Sắp hết hạn" : "Còn hạn";
                    if (!tone.equals("Sắp hết hạn")) continue;
                    rows.add(new Object[]{l[0], l[1], l[2], "Còn " + days + " ngày", tone});
                    lab.add(l[0] + " " + l[2]);
                    val.add((double) days);
                }
                return data(lab.toArray(String[]::new), val.stream().mapToDouble(Double::doubleValue).toArray(), false, "ngày",
                    new String[]{"Mã SP", "Tên sản phẩm", "Số lô", "Hạn dùng", "Trạng thái"}, rows.toArray(new Object[0][]),
                    new String[]{"Lô sắp hết hạn", "Cảnh báo trước", "", ""},
                    new String[]{String.valueOf(rows.size()), AppSettings.expiryWarnDays() + " ngày", "—", "—"});
            })
        );
        return new ReportPanel("Thống kê hàng hóa", "Bán chạy, nhập–xuất–tồn, tồn thấp, sắp hết hạn", "Thong_ke_hang_hoa", views);
    }

    // ───────────── Spa
    public static JPanel spa() {
        List<View> views = List.of(
            new View("Dịch vụ phổ biến", d -> {
                List<Object[]> sv = DemoStore.spaServices();
                int n = sv.size();
                String[] labels = new String[n];
                double[] vals = new double[n];
                Object[][] rows = new Object[n][];
                double total = 0;
                long tickets = 0;
                for (int i = 0; i < n; i++) {
                    Object[] s = sv.get(i);
                    long cnt = Math.round((30 - i * 8) * Math.max(1, d / 7.0));
                    long price = com.petstation.gui.components.common.PriceTableEditor.minPrice(String.valueOf(s[4]));
                    labels[i] = String.valueOf(s[1]);
                    vals[i] = cnt;
                    tickets += cnt;
                    total += cnt * Math.max(price, 0);
                    rows[i] = new Object[]{s[0], s[1], cnt, m(cnt * Math.max(price, 0))};
                }
                return data(labels, vals, false, "", new String[]{"Mã DV", "Dịch vụ", "Số phiếu", "Doanh thu"}, rows,
                    new String[]{"Số phiếu Spa", "Doanh thu Spa", "Tỷ lệ hủy / không đến", "Dịch vụ phổ biến"},
                    new String[]{String.valueOf(tickets), m(total), "6%", n > 0 ? labels[0] : "—"});
            }),
            new View("Hiệu suất nhân viên", d -> staffView(d, "Nhân viên Spa/Hotel", "Phiếu Spa", 22)),
            new View("Hủy / không đến", d -> shares(Math.round(40 * Math.max(1, d / 7.0)), new String[]{"Hoàn thành", "Hủy", "Không đến"},
                new double[]{0.90, 0.06, 0.04}, "Kết quả", false,
                new String[]{"Tổng lịch hẹn", "Tỷ lệ hủy", "Tỷ lệ không đến", ""},
                new String[]{String.valueOf(Math.round(40 * Math.max(1, d / 7.0))), "6%", "4%", ""}))
        );
        return new ReportPanel("Thống kê dịch vụ Spa", "Số phiếu, dịch vụ phổ biến, tỷ lệ hủy/không đến, hiệu suất nhân viên", "Thong_ke_spa", views);
    }

    private static Data staffView(double d, String position, String unitName, int perWeek) {
        List<Object[]> staff = new ArrayList<>();
        for (Object[] s : DemoStore.staff()) if (position.equals(s[4]) && !"Nghỉ việc".equals(s[6])) staff.add(s);
        String[] labels = new String[staff.size()];
        double[] vals = new double[staff.size()];
        Object[][] rows = new Object[staff.size()][];
        long total = 0;
        for (int i = 0; i < staff.size(); i++) {
            Object[] s = staff.get(i);
            long cnt = Math.round((perWeek - i * 5) * Math.max(1, d / 7.0));
            labels[i] = String.valueOf(s[1]);
            vals[i] = cnt;
            total += cnt;
            rows[i] = new Object[]{s[0], s[1], cnt, Math.round(cnt * 1.1 * 10) / 10.0 + " giờ"};
        }
        return data(labels, vals, false, "", new String[]{"Mã NV", "Nhân viên", unitName, "Giờ làm"}, rows,
            new String[]{"Nhân viên", "Tổng " + unitName.toLowerCase(), "", ""}, new String[]{String.valueOf(staff.size()), String.valueOf(total), "—", "—"});
    }

    // ───────────── Hotel
    public static JPanel hotel() {
        List<View> views = List.of(
            new View("Công suất phòng", d -> {
                List<Object[]> rooms = DemoStore.rooms();
                java.util.Map<String, Integer> byStatus = new java.util.LinkedHashMap<>();
                for (String s : new String[]{"Có thú", "Trống", "Đang dọn", "Bảo trì"}) byStatus.put(s, 0);
                int active = 0;
                for (Object[] r : rooms) {
                    String st = String.valueOf(r[5]);
                    if (st.equals("Ngừng hoạt động")) continue;
                    active++;
                    byStatus.merge(st, 1, Integer::sum);
                }
                String[] labels = byStatus.keySet().toArray(String[]::new);
                double[] vals = byStatus.values().stream().mapToDouble(Integer::doubleValue).toArray();
                Object[][] rows = new Object[labels.length][];
                for (int i = 0; i < labels.length; i++) rows[i] = new Object[]{labels[i], (int) vals[i], active == 0 ? "0%" : Math.round(vals[i] * 100 / active) + "%"};
                int occupied = byStatus.get("Có thú");
                return data(labels, vals, false, "phòng", new String[]{"Trạng thái phòng", "Số phòng", "Tỷ lệ"}, rows,
                    new String[]{"Tổng số phòng", "Đang có thú", "Công suất hiện tại", "Lượt quá hạn trả"},
                    new String[]{String.valueOf(active), String.valueOf(occupied), active == 0 ? "0%" : Math.round(occupied * 100.0 / active) + "%", "1"});
            }),
            new View("Doanh thu theo loại phòng", d -> {
                String[] names = {"Tiêu chuẩn", "VIP", "Mèo"};
                double[] share = {0.55, 0.35, 0.10};
                double total = 2_400_000 * Math.max(1, d / 7.0) * 3;
                double[] vals = new double[3];
                Object[][] rows = new Object[3][];
                for (int i = 0; i < 3; i++) {
                    vals[i] = total * share[i];
                    rows[i] = new Object[]{names[i], Math.round(share[i] * 100 * Math.max(1, d / 7.0)), Math.round(share[i] * 60 * Math.max(1, d / 7.0)), m(vals[i])};
                }
                return data(names, vals, true, "", new String[]{"Loại phòng", "Lượt lưu trú", "Số ngày lưu trú", "Doanh thu"}, rows,
                    new String[]{"Doanh thu Hotel", "Lượt lưu trú", "Số ngày lưu trú", "Lượt quá hạn"},
                    new String[]{m(total), String.valueOf(Math.round(100 * Math.max(1, d / 7.0))), String.valueOf(Math.round(60 * Math.max(1, d / 7.0))), "4"});
            })
        );
        return new ReportPanel("Thống kê Pet Hotel", "Công suất phòng, số ngày lưu trú, doanh thu, số lượt quá hạn", "Thong_ke_hotel", views);
    }

    // ───────────── Khách hàng
    public static JPanel customers() {
        List<View> views = List.of(
            new View("Top khách (điểm tích lũy)", d -> {
                List<Object[]> list = new ArrayList<>(DemoStore.customers());
                list.sort((a, b) -> Long.compare(Money.parse(String.valueOf(b[7])), Money.parse(String.valueOf(a[7]))));
                int n = Math.min(6, list.size());
                String[] labels = new String[n];
                double[] vals = new double[n];
                Object[][] rows = new Object[n][];
                for (int i = 0; i < n; i++) {
                    Object[] c = list.get(i);
                    labels[i] = String.valueOf(c[1]);
                    vals[i] = Money.parse(String.valueOf(c[7]));
                    rows[i] = new Object[]{c[0], c[1], c[6], c[7]};
                }
                long vip = list.stream().filter(c -> !"Đồng".equals(c[6]) && !"Bạc".equals(c[6])).count();
                return data(labels, vals, false, "điểm", new String[]{"Mã KH", "Khách hàng", "Hạng", "Điểm"}, rows,
                    new String[]{"Tổng khách hàng", "Khách thân thiết (Vàng trở lên)", "Khách mới kỳ này", "Voucher đã dùng"},
                    new String[]{String.valueOf(list.size()), String.valueOf(vip), String.valueOf(Math.max(1, Math.round(d / 5))), String.valueOf(voucherUsed())});
            }),
            new View("Voucher đã dùng", d -> {
                List<Object[]> v = DemoStore.vouchers();
                String[] labels = new String[v.size()];
                double[] vals = new double[v.size()];
                Object[][] rows = new Object[v.size()][];
                for (int i = 0; i < v.size(); i++) {
                    Object[] r = v.get(i);
                    labels[i] = String.valueOf(r[0]);
                    vals[i] = Money.parse(String.valueOf(r[4]));
                    rows[i] = new Object[]{r[0], m(Money.parse(String.valueOf(r[1]))), r[4] + " / " + r[3], r[6]};
                }
                return data(labels, vals, false, "lượt", new String[]{"Voucher", "Giá trị", "Đã dùng / số lượng", "Trạng thái"}, rows,
                    new String[]{"Voucher đã dùng", "Số loại voucher", "", ""}, new String[]{String.valueOf(voucherUsed()), String.valueOf(v.size()), "—", "—"});
            })
        );
        return new ReportPanel("Thống kê khách hàng", "Khách mới, khách thân thiết, top chi tiêu, điểm và voucher đã dùng", "Thong_ke_khach_hang", views);
    }

    private static long voucherUsed() {
        long n = 0;
        for (Object[] r : DemoStore.vouchers()) n += Money.parse(String.valueOf(r[4]));
        return n;
    }

    // ───────────── Công nợ nhà cung cấp
    public static JPanel supplierDebt() {
        List<View> views = List.of(new View("Công nợ", d -> {
            List<Object[]> sup = DemoStore.suppliers();
            String[] labels = new String[sup.size()];
            double[] vals = new double[sup.size()];
            Object[][] rows = new Object[sup.size()][];
            double totalIn = 0, totalPaid = 0;
            for (int i = 0; i < sup.size(); i++) {
                Object[] s = sup.get(i);
                double in = (45 - i * 9) * 1_000_000.0;
                double paid = i % 2 == 0 ? in * 0.9 : in;
                totalIn += in;
                totalPaid += paid;
                labels[i] = String.valueOf(s[1]);
                vals[i] = in - paid;
                rows[i] = new Object[]{s[0], s[1], m(in), m(paid), m(in - paid), s[6]};
            }
            return data(labels, vals, true, "", new String[]{"Mã NCC", "Nhà cung cấp", "Tổng nhập", "Đã trả", "Còn nợ", "Điều khoản"}, rows,
                new String[]{"Tổng nhập", "Đã trả", "Còn nợ", "Nhà cung cấp còn nợ"},
                new String[]{m(totalIn), m(totalPaid), m(totalIn - totalPaid), String.valueOf(java.util.Arrays.stream(vals).filter(v -> v > 0).count())});
        }));
        return new ReportPanel("Công nợ nhà cung cấp", "Tổng nhập, đã trả, còn nợ theo từng nhà cung cấp", "Cong_no_nha_cung_cap", views);
    }

    // ───────────── Nhân viên
    public static JPanel staff() {
        List<View> views = List.of(new View("Tất cả nhân viên", d -> {
            List<Object[]> staff = new ArrayList<>();
            for (Object[] s : DemoStore.staff()) if (!"Nghỉ việc".equals(s[6])) staff.add(s);
            String[] labels = new String[staff.size()];
            double[] vals = new double[staff.size()];
            Object[][] rows = new Object[staff.size()][];
            long shifts = 0;
            for (int i = 0; i < staff.size(); i++) {
                Object[] s = staff.get(i);
                long sh = Math.max(1, Math.round(5 * Math.max(1, d / 7.0)) - i % 2);
                boolean sales = String.valueOf(s[4]).contains("bán hàng");
                boolean spa = String.valueOf(s[4]).contains("Spa");
                labels[i] = String.valueOf(s[1]);
                vals[i] = sh;
                shifts += sh;
                rows[i] = new Object[]{s[0], s[1], s[4], sh, sh * 7 + " giờ", sales ? m(DAILY_REVENUE * 0.2 * sh / 5) : "—", spa ? Math.round(sh * 3.5) : "—"};
            }
            return data(labels, vals, false, "ca", new String[]{"Mã NV", "Nhân viên", "Chức vụ", "Số ca", "Giờ làm", "Doanh số", "Phiếu Spa"}, rows,
                new String[]{"Nhân viên đang làm", "Tổng số ca", "Tổng giờ làm", ""}, new String[]{String.valueOf(staff.size()), String.valueOf(shifts), shifts * 7 + " giờ", ""});
        }));
        return new ReportPanel("Thống kê nhân viên", "Số ca, giờ làm, doanh số, số phiếu Spa đã thực hiện", "Thong_ke_nhan_vien", views);
    }
}
