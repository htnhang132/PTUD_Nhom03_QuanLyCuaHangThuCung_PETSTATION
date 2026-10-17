package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.ExcelExporter;
import com.petstation.gui.components.common.TableStyler;
import com.petstation.gui.navigation.AuditTrail;
import com.petstation.gui.navigation.ScreenId;

/**
 * Tìm kiếm ▸ Hóa đơn (F3): lọc theo thời gian, trạng thái, từ khóa; bấm dòng để xem chi tiết ở khung bên phải.
 * Chỉ xem, in lại và gửi yêu cầu sửa/hủy (không sửa trực tiếp hóa đơn đã chốt).
 */
public final class OrdersPanel extends BasePanel {
    private record Line(String name, int qty, long price) { long amount() { return qty * price; } }

    private record Invoice(String code, String customer, String cashier, LocalDateTime time, String method, String status, List<Line> lines) {
        long total() { return lines.stream().mapToLong(Line::amount).sum(); }
        int itemCount() { return lines.stream().mapToInt(Line::qty).sum(); }
    }

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final String[] COLS = {"Mã hóa đơn", "Khách hàng", "Thời gian", "Số SP", "Tổng tiền", "Trạng thái", "Thanh toán"};

    private final Consumer<ScreenId> navigator;
    private final List<Invoice> all = new ArrayList<>();
    private final List<Invoice> shown = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(COLS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table;
    private final JComboBox<String> period = new JComboBox<>(new String[]{"Hôm nay", "7 ngày", "30 ngày", "Tất cả"});
    private final JComboBox<String> status = new JComboBox<>(new String[]{"Mọi trạng thái", "Hoàn thành", "Tạm tính", "Đã hoàn tiền", "Đã hủy"});
    private final JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
    private final JPanel detail = new JPanel(new BorderLayout(0, 8));
    private Invoice selected;

    public OrdersPanel(Consumer<ScreenId> navigator) {
        super();
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 10));
        seed();

        stats.setOpaque(false);
        JTextField search = searchBar("Tìm theo mã HĐ, tên khách...");
        search.setPreferredSize(new Dimension(260, 34));
        period.setPreferredSize(new Dimension(110, 34));
        status.setPreferredSize(new Dimension(150, 34));
        period.addActionListener(e -> refilter());
        status.addActionListener(e -> refilter());
        StyledButton export = new StyledButton("Xuất Excel", StyledButton.Variant.GHOST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        filters.add(period);
        filters.add(status);
        filters.add(search);
        JPanel filterRow = new JPanel(new BorderLayout());
        filterRow.setOpaque(false);
        filterRow.add(filters, BorderLayout.WEST);
        filterRow.add(export, BorderLayout.EAST);

        JPanel top = transparent(new BorderLayout(0, 10));
        top.add(stats, BorderLayout.NORTH);
        top.add(filterRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JScrollPane scroll = styledTable(model);
        table = tableOf(scroll);
        TableStyler.apply(table);
        int[] widths = {105, 140, 95, 55, 105, 105, 105};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        bindFilter(search, table);
        export.addActionListener(e -> ExcelExporter.exportTable(this, table, "Hoa_don"));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow();
            selected = r < 0 ? null : shown.get(table.convertRowIndexToModel(r));
            renderDetail();
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && selected != null) reprint();
            }
        });

        detail.setBackground(Color.WHITE);
        detail.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), new EmptyBorder(14, 16, 14, 16)));
        JPanel holder = new JPanel(new BorderLayout());
        holder.setPreferredSize(new Dimension(320, 0));
        holder.add(detail);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, holder);
        split.setResizeWeight(0.75);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);

        refilter();
        renderDetail();
    }

    private void seed() {
        LocalDateTime now = LocalDateTime.now();
        all.add(inv("HD-2610-024", "Trần Thị Bình", "Phạm Thu Lan", now.minusHours(1), "Tiền mặt", "Hoàn thành",
            new Line("Royal Canin Adult 2kg", 1, 285_000), new Line("Pate Whiskas 400g", 2, 45_000), new Line("Sữa tắm Spa Dogs 500ml", 1, 89_000)));
        all.add(inv("HD-2610-023", "Khách lẻ", "Phạm Thu Lan", now.minusHours(3), "Chuyển khoản", "Tạm tính",
            new Line("Lồng vận chuyển size L", 2, 450_000), new Line("Đồ chơi bóng cao su", 3, 35_000)));
        all.add(inv("HD-2610-019", "Nguyễn Văn An", "Đặng Minh Tú", now.minusDays(1).withHour(16), "Tiền mặt", "Đã hoàn tiền",
            new Line("Thức ăn hạt Orijen 1.8kg", 1, 520_000), new Line("Cát vệ sinh mèo 5L", 2, 75_000)));
        all.add(inv("HD-2610-015", "Lê Hoàng Cường", "Phạm Thu Lan", now.minusDays(3).withHour(9), "QR Code", "Hoàn thành",
            new Line("Royal Canin Adult 2kg", 2, 285_000), new Line("Vòng cổ da size M", 1, 120_000), new Line("Pate Whiskas 400g", 4, 45_000)));
        all.add(inv("HD-2610-012", "Phạm Minh Đức", "Đặng Minh Tú", now.minusDays(6).withHour(14), "Tiền mặt", "Hoàn thành",
            new Line("Royal Canin Adult 2kg", 1, 285_000)));
        all.add(inv("HD-2609-088", "Hoàng Thị Em", "Phạm Thu Lan", now.minusDays(12).withHour(11), "Tiền mặt", "Đã hủy",
            new Line("Sữa tắm Spa Dogs 500ml", 2, 89_000)));
    }

    private static Invoice inv(String code, String customer, String cashier, LocalDateTime time, String method, String status, Line... lines) {
        return new Invoice(code, customer, cashier, time.withSecond(0).withNano(0), method, status, List.of(lines));
    }

    private void refilter() {
        LocalDate today = LocalDate.now();
        int days = switch (period.getSelectedIndex()) { case 0 -> 0; case 1 -> 7; case 2 -> 30; default -> Integer.MAX_VALUE; };
        String st = String.valueOf(status.getSelectedItem());
        shown.clear();
        model.setRowCount(0);
        for (Invoice i : all) {
            if (days != Integer.MAX_VALUE && i.time().toLocalDate().isBefore(today.minusDays(days))) continue;
            if (!st.startsWith("Mọi") && !st.equals(i.status())) continue;
            shown.add(i);
            model.addRow(new Object[]{i.code(), i.customer(), i.time().format(TIME), i.itemCount(), Money.format(i.total()), i.status(), i.method()});
        }
        stats.removeAll();
        long revenue = shown.stream().filter(i -> i.status().equals("Hoàn thành")).mapToLong(Invoice::total).sum();
        stats.add(statCard("Số hóa đơn", String.valueOf(shown.size()), "", AppColors.BLUE));
        stats.add(statCard("Doanh thu (hoàn thành)", Money.format(revenue), "", AppColors.GREEN));
        stats.add(statCard("Tạm tính", String.valueOf(shown.stream().filter(i -> i.status().equals("Tạm tính")).count()), "", AppColors.ORANGE));
        stats.add(statCard("Đã hủy / hoàn", String.valueOf(shown.stream().filter(i -> i.status().equals("Đã hủy") || i.status().equals("Đã hoàn tiền")).count()), "", AppColors.RED_BRAND));
        stats.revalidate();
        stats.repaint();
        selected = null;
        renderDetail();
    }

    private void renderDetail() {
        detail.removeAll();
        if (selected == null) {
            JLabel hint = new JLabel("<html><div style='text-align:center'>Chọn một hóa đơn ở danh sách<br>để xem chi tiết tại đây.</div></html>", JLabel.CENTER);
            hint.setForeground(AppColors.TEXT_GRAY);
            detail.add(hint, BorderLayout.CENTER);
        } else {
            JPanel head = new JPanel(new GridLayout(0, 1, 0, 2));
            head.setOpaque(false);
            JLabel title = new JLabel(selected.code());
            title.setFont(AppColors.font(Font.BOLD, 17));
            head.add(title);
            head.add(info("Khách hàng", selected.customer()));
            head.add(info("Thu ngân", selected.cashier()));
            head.add(info("Thời gian", selected.time().format(TIME)));
            head.add(info("Thanh toán", selected.method()));
            head.add(info("Trạng thái", selected.status()));
            detail.add(head, BorderLayout.NORTH);

            DefaultTableModel lm = new DefaultTableModel(new String[]{"Sản phẩm", "SL", "Thành tiền"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Line l : selected.lines()) lm.addRow(new Object[]{l.name(), l.qty(), Money.format(l.amount())});
            JTable lt = new JTable(lm);
            TableStyler.apply(lt);
            lt.setAutoCreateRowSorter(false);
            lt.getColumnModel().getColumn(1).setPreferredWidth(40);
            JScrollPane lsp = new JScrollPane(lt);
            lsp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
            detail.add(lsp, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new BorderLayout(0, 8));
            bottom.setOpaque(false);
            JLabel total = new JLabel("Tổng cộng: " + Money.format(selected.total()), JLabel.RIGHT);
            total.setFont(AppColors.font(Font.BOLD, 16));
            total.setForeground(AppColors.RED_BRAND);
            bottom.add(total, BorderLayout.NORTH);
            JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
            buttons.setOpaque(false);
            StyledButton print = new StyledButton("In lại", StyledButton.Variant.PRIMARY);
            StyledButton request = new StyledButton("Yêu cầu sửa/hủy", StyledButton.Variant.SECONDARY);
            print.addActionListener(e -> reprint());
            request.addActionListener(e -> requestChange());
            request.setEnabled(!"Đã hủy".equals(selected.status()));
            buttons.add(print);
            buttons.add(request);
            bottom.add(buttons, BorderLayout.SOUTH);
            detail.add(bottom, BorderLayout.SOUTH);
        }
        detail.revalidate();
        detail.repaint();
    }

    private static JLabel info(String k, String v) {
        JLabel l = new JLabel("<html><span style='color:#7a7a7a'>" + k + ":</span> " + v + "</html>");
        l.setFont(AppColors.font(Font.PLAIN, 13));
        return l;
    }

    private void reprint() {
        if (selected == null) return;
        if (Dialogs.confirm(this, "In lại hóa đơn " + selected.code() + " (bản sao)?")) {
            AuditTrail.log("In lại", "Hóa đơn " + selected.code());
            Dialogs.success(this, "Đã gửi bản sao hóa đơn " + selected.code() + " tới máy in.");
        }
    }

    private void requestChange() {
        if (selected == null) return;
        // Hóa đơn đã chốt thì khóa: muốn sửa hoặc hủy phải gửi yêu cầu sang Phê duyệt.
        com.petstation.gui.data.ApprovalStore.selectedInvoice = selected.code();
        navigator.accept(ScreenId.INVOICE_EDIT_REQUEST);
    }

    @Override protected String helpTitle() { return "Tìm kiếm hóa đơn"; }

    @Override protected String helpText() {
        return "Lọc theo thời gian, trạng thái hoặc gõ từ khóa. Bấm dòng để xem chi tiết bên phải.\n"
            + "Hóa đơn đã chốt không sửa trực tiếp: dùng \"Yêu cầu sửa/hủy\" để gửi Cửa hàng trưởng duyệt.";
    }
}
