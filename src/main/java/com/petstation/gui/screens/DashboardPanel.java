package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.components.common.SimpleBarChart;
import com.petstation.gui.components.common.TableStyler;
import com.petstation.gui.data.ApprovalStore;
import com.petstation.gui.data.DemoStore;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.navigation.ShiftState;

/** Màn hình chính: lối tắt việc hằng ngày theo quyền, số liệu thật của ca/kho/phê duyệt, biểu đồ doanh thu 7 ngày. */
public final class DashboardPanel extends BasePanel implements ToolbarAware {
    private final Consumer<ScreenId> navigator;
    private final Predicate<ScreenId> canAccess;
    private final ShiftState shift;
    private final JPanel shortcuts = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
    private final SimpleBarChart chart = new SimpleBarChart();
    private final DefaultTableModel topModel = new DefaultTableModel(new String[]{"#", "Sản phẩm", "Đã bán", "Doanh thu"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public DashboardPanel(Consumer<ScreenId> navigator, Predicate<ScreenId> canAccess, ShiftState shift) {
        this.navigator = navigator;
        this.canAccess = canAccess;
        this.shift = shift;
        setLayout(new BorderLayout(0, 14));

        JPanel head = transparent(new BorderLayout());
        JLabel title = sectionTitle("Tổng quan hôm nay");
        title.setFont(AppColors.font(Font.BOLD, 22));
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN")));
        JLabel dateLabel = new JLabel(date.substring(0, 1).toUpperCase() + date.substring(1));
        dateLabel.setFont(AppColors.font(Font.PLAIN, 13));
        dateLabel.setForeground(AppColors.TEXT_GRAY);
        head.add(title, BorderLayout.WEST);
        head.add(dateLabel, BorderLayout.EAST);

        shortcuts.setOpaque(false);
        stats.setOpaque(false);
        JPanel north = transparent(new BorderLayout(0, 12));
        north.add(head, BorderLayout.NORTH);
        north.add(shortcuts, BorderLayout.CENTER);
        north.add(stats, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        JPanel chartCard = new JPanel(new BorderLayout(0, 6));
        chartCard.setBackground(Color.WHITE);
        chartCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), new EmptyBorder(12, 14, 10, 14)));
        JLabel ct = new JLabel("Doanh thu 7 ngày qua");
        ct.setFont(AppColors.font(Font.BOLD, 14));
        chartCard.add(ct, BorderLayout.NORTH);
        chartCard.add(chart, BorderLayout.CENTER);

        JTable table = new JTable(topModel);
        TableStyler.apply(table);
        table.setAutoCreateRowSorter(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(36);
        table.getColumnModel().getColumn(1).setPreferredWidth(190);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        sp.getViewport().setBackground(Color.WHITE);
        JPanel topCard = new JPanel(new BorderLayout(0, 6));
        topCard.setOpaque(false);
        JLabel tt = new JLabel("Sản phẩm bán chạy");
        tt.setFont(AppColors.font(Font.BOLD, 14));
        topCard.add(tt, BorderLayout.NORTH);
        topCard.add(sp, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(1, 2, 14, 0));
        center.setOpaque(false);
        center.add(chartCard);
        center.add(topCard);
        add(center, BorderLayout.CENTER);
        shift.addListener(this::refresh);
        refresh();
    }

    private void addShortcut(String text, ScreenId target, StyledButton.Variant variant) {
        if (!canAccess.test(target)) return;
        StyledButton b = new StyledButton(text, variant);
        b.setPreferredSize(new Dimension(Math.max(110, text.length() * 9 + 28), 42));
        b.addActionListener(e -> navigator.accept(target));
        shortcuts.add(b);
    }

    private void refresh() {
        shortcuts.removeAll();
        addShortcut("Lập hóa đơn (F2)", ScreenId.POS, StyledButton.Variant.PRIMARY);
        addShortcut("Tạo lịch hẹn Spa", ScreenId.SPA_SCHEDULE, StyledButton.Variant.SECONDARY);
        addShortcut("Đặt phòng Hotel", ScreenId.HOTEL_BOOKING, StyledButton.Variant.SECONDARY);
        addShortcut(shift.isOpen() ? "Kết ca" : "Mở ca", ScreenId.CASH_SHIFT, StyledButton.Variant.GHOST);
        addShortcut("Nhập kho", ScreenId.PURCHASE, StyledButton.Variant.GHOST);
        long pending = ApprovalStore.pendingCount();
        addShortcut(pending > 0 ? "Phê duyệt (" + pending + ")" : "Phê duyệt", ScreenId.APPROVALS, pending > 0 ? StyledButton.Variant.DANGER : StyledButton.Variant.GHOST);
        shortcuts.revalidate();
        shortcuts.repaint();

        stats.removeAll();
        long revenue = shift.cashSales() + shift.transferSales() + shift.cardSales();
        stats.add(statCard(shift.isOpen() ? "Doanh thu ca hiện tại" : "Chưa mở ca", shift.isOpen() ? Money.format(revenue) : "—",
            shift.isOpen() ? shift.shiftName() : "Mở ca để bán hàng", AppColors.GREEN));
        stats.add(statCard("Hóa đơn trong ca", shift.isOpen() ? String.valueOf(shift.invoiceCount()) : "—", "", AppColors.BLUE));
        stats.add(statCard("Sản phẩm tồn thấp", String.valueOf(lowStock()), "Cần nhập thêm", AppColors.RED_BRAND));
        stats.add(statCard("Yêu cầu chờ duyệt", String.valueOf(pending), pending > 0 ? "Cần Cửa hàng trưởng xử lý" : "Không có", AppColors.ORANGE));
        stats.revalidate();
        stats.repaint();

        String[] days = new String[7];
        double[] vals = new double[7];
        double[] base = {7_200_000, 8_100_000, 6_800_000, 9_400_000, 8_900_000, 11_200_000, 8_450_000};
        for (int i = 0; i < 7; i++) {
            days[i] = LocalDate.now().minusDays(6 - i).format(DateTimeFormatter.ofPattern("dd/MM"));
            vals[i] = base[i];
        }
        if (shift.isOpen()) vals[6] = Math.max(vals[6], revenue);
        chart.setData(days, vals, true, "");

        topModel.setRowCount(0);
        int rank = 1;
        for (Object[] p : DemoStore.products()) {
            if (rank > 5) break;
            long qty = 48 - rank * 6L;
            topModel.addRow(new Object[]{rank, p[1], qty, Money.format(qty * Money.parse(String.valueOf(p[4])))});
            rank++;
        }
    }

    private static long lowStock() {
        long n = 0;
        for (Object[] p : DemoStore.products()) {
            if (Money.parse(String.valueOf(p[8])) <= Math.max(0, Money.parse(String.valueOf(p[6])))) n++;
        }
        return n;
    }

    @Override public void onShow() { refresh(); }

    @Override protected String helpTitle() { return "Màn hình chính"; }

    @Override protected String helpText() {
        return "Các nút phía trên là lối tắt việc hằng ngày (chỉ hiện chức năng bạn được phép dùng).\n"
            + "Doanh thu ca, tồn thấp và yêu cầu chờ duyệt lấy từ dữ liệu hiện tại; biểu đồ 7 ngày là số liệu mẫu.";
    }
}
