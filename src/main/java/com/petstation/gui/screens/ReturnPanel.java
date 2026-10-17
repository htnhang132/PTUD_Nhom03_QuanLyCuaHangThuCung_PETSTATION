package com.petstation.gui.screens;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.FormPanel;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.StepIndicator;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarCapabilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Đổi trả hàng theo 5 bước: tra cứu hóa đơn → chọn hàng → lý do và hoàn tiền → xác nhận → hoàn tất. */
public final class ReturnPanel extends BasePanel {
    private record Invoice(String no, String customer, String date, Object[][] lines) {
        long total() {
            long t = 0;
            for (Object[] l : lines) t += (int) l[1] * (long) (int) l[2];
            return t;
        }
    }

    private static final long APPROVAL_THRESHOLD = 500_000;
    private static final List<Invoice> INVOICES = List.of(
        new Invoice("HD-2380", "Trần Thị Bích", "18/09/2026", new Object[][]{{"Royal Canin Adult 2kg", 1, 285_000}, {"Vòng cổ da size M", 2, 120_000}}),
        new Invoice("HD-2366", "Nguyễn Văn Hùng", "15/09/2026", new Object[][]{{"Áo mưa chó size S", 1, 95_000}, {"Đồ chơi bóng cao su", 3, 35_000}}),
        new Invoice("HD-2359", "Khách lẻ", "14/09/2026", new Object[][]{{"Lồng vận chuyển size L", 1, 450_000}}),
        new Invoice("HD-2351", "Lê Hoàng Cường", "12/09/2026", new Object[][]{{"Pate Whiskas 400g", 10, 45_000}, {"Cát vệ sinh mèo 5L", 2, 75_000}})
    );
    private static final String[] STEPS = {"Tra cứu hóa đơn", "Chọn hàng trả", "Lý do & hoàn tiền", "Xác nhận", "Hoàn tất"};
    private static final String[] REASONS = {"Sản phẩm lỗi", "Đổi size", "Không phù hợp", "Giao nhầm hàng", "Khác"};
    private static final String[] REFUNDS = {"Tiền mặt", "Chuyển khoản", "Voucher đổi hàng"};

    private final StepIndicator indicator = new StepIndicator(List.of(STEPS));
    private final CardLayout cards = new CardLayout();
    private final JPanel stepArea = new JPanel(cards);
    private final StyledButton back = new StyledButton("Quay lại", StyledButton.Variant.GHOST);
    private final StyledButton next = new StyledButton("Tiếp tục", StyledButton.Variant.PRIMARY);
    private final JLabel error = new JLabel("");

    private JTable invoiceTable;
    private JTextField invoiceSearch;
    private DefaultTableModel lineModel;
    private JTable lineTable;
    private final JComboBox<String> reason = FormPanel.combo(REASONS);
    private final JComboBox<String> refund = FormPanel.combo(REFUNDS);
    private final JTextField note = FormPanel.textField("");
    private final JTextArea summary = new JTextArea();
    private final JLabel resultTitle = new JLabel();
    private final JLabel resultBody = new JLabel();
    private int step;
    private int sequence = 4;
    private Invoice selected;

    public ReturnPanel() {
        setLayout(new BorderLayout(0, 12));
        JLabel title = sectionTitle("Đổi trả hàng");
        title.setFont(AppColors.font(Font.BOLD, 21));
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(6));
        head.add(indicator);
        add(head, BorderLayout.NORTH);

        stepArea.setOpaque(false);
        stepArea.add(stepSearch(), "0");
        stepArea.add(stepLines(), "1");
        stepArea.add(stepReason(), "2");
        stepArea.add(stepConfirm(), "3");
        stepArea.add(stepDone(), "4");
        add(stepArea, BorderLayout.CENTER);

        error.setForeground(AppColors.DANGER_TEXT);
        error.setFont(AppColors.font(Font.PLAIN, 12));
        JPanel footer = transparent(new BorderLayout());
        footer.add(error, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(back);
        buttons.add(next);
        footer.add(buttons, BorderLayout.EAST);
        footer.add(noteBar(), BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);

        back.addActionListener(e -> go(step - 1));
        next.addActionListener(e -> advance());
        go(0);
    }

    private JPanel noteBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.WARN_BG);
        bar.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel l = new JLabel("Dữ liệu mẫu: chưa ghi stock movement và giao dịch hoàn tiền vào database.");
        l.setFont(AppColors.font(Font.PLAIN, 12));
        l.setForeground(AppColors.BROWN);
        bar.add(l, BorderLayout.WEST);
        return bar;
    }

    // ── Bước 1 ────────────────────────────────────────────────────
    private JPanel stepSearch() {
        JPanel p = card(new BorderLayout(0, 12));
        invoiceSearch = searchBar("Nhập mã hóa đơn hoặc tên khách hàng");
        JPanel top = transparent(new BorderLayout());
        top.add(sectionTitle("Hóa đơn gốc"), BorderLayout.WEST);
        top.add(invoiceSearch, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);
        Object[][] rows = new Object[INVOICES.size()][];
        for (int i = 0; i < rows.length; i++) {
            Invoice inv = INVOICES.get(i);
            rows[i] = new Object[]{inv.no(), inv.customer(), inv.date(), Money.format(inv.total())};
        }
        JScrollPane scroll = styledTable(new String[]{"Mã hóa đơn", "Khách hàng", "Ngày mua", "Tổng tiền"}, rows);
        invoiceTable = tableOf(scroll);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getSelectionModel().addListSelectionListener(e -> error.setText(""));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Bước 2 ────────────────────────────────────────────────────
    private JPanel stepLines() {
        JPanel p = card(new BorderLayout(0, 12));
        p.add(sectionTitle("Chọn mặt hàng và số lượng trả"), BorderLayout.NORTH);
        lineModel = new DefaultTableModel(new String[]{"Trả", "Sản phẩm", "Đã mua", "Số lượng trả", "Đơn giá"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 3; }
        };
        JScrollPane scroll = styledTable(lineModel);
        lineTable = tableOf(scroll);
        lineTable.setAutoCreateRowSorter(false);
        lineTable.setRowSorter(null);
        lineTable.getColumnModel().getColumn(0).setMaxWidth(60);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Bước 3 ────────────────────────────────────────────────────
    private JPanel stepReason() {
        JPanel p = card(new BorderLayout(0, 12));
        p.add(sectionTitle("Lý do và hình thức hoàn tiền"), BorderLayout.NORTH);
        FormPanel form = new FormPanel();
        form.addField("Lý do đổi trả", reason);
        form.addField("Hình thức hoàn", refund);
        form.addWide("Ghi chú (bắt buộc khi chọn \"Khác\")", note);
        JPanel holder = transparent(new BorderLayout());
        holder.add(form, BorderLayout.NORTH);
        p.add(holder, BorderLayout.CENTER);
        return p;
    }

    // ── Bước 4 ────────────────────────────────────────────────────
    private JPanel stepConfirm() {
        JPanel p = card(new BorderLayout(0, 12));
        p.add(sectionTitle("Kiểm tra lại trước khi tạo phiếu"), BorderLayout.NORTH);
        summary.setEditable(false);
        summary.setOpaque(false);
        summary.setFont(AppColors.font(Font.PLAIN, 14));
        summary.setBorder(new EmptyBorder(4, 2, 4, 2));
        p.add(summary, BorderLayout.CENTER);
        return p;
    }

    // ── Bước 5 ────────────────────────────────────────────────────
    private JPanel stepDone() {
        JPanel p = card(new GridBagLayout());
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        resultTitle.setFont(AppColors.font(Font.BOLD, 20));
        resultBody.setFont(AppColors.font(Font.PLAIN, 14));
        resultBody.setForeground(AppColors.TEXT_GRAY);
        resultTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultBody.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(resultTitle);
        box.add(Box.createVerticalStrut(8));
        box.add(resultBody);
        p.add(box);
        return p;
    }

    // ── Điều hướng bước ───────────────────────────────────────────
    private void go(int target) {
        step = Math.max(0, Math.min(target, STEPS.length - 1));
        indicator.setCurrent(step);
        cards.show(stepArea, String.valueOf(step));
        error.setText("");
        back.setVisible(step > 0 && step < 4);
        next.setText(switch (step) {
            case 3 -> "Tạo phiếu đổi trả";
            case 4 -> "Tạo phiếu mới";
            default -> "Tiếp tục";
        });
        if (step == 3) fillSummary();
    }

    private void advance() {
        switch (step) {
            case 0 -> {
                int row = invoiceTable.getSelectedRow();
                if (row < 0) { error.setText("Hãy chọn một hóa đơn gốc trong danh sách."); return; }
                selected = INVOICES.get(invoiceTable.convertRowIndexToModel(row));
                lineModel.setRowCount(0);
                for (Object[] l : selected.lines()) lineModel.addRow(new Object[]{Boolean.FALSE, l[0], l[1], 1, Money.format((int) l[2])});
                go(1);
            }
            case 1 -> {
                if (lineTable.isEditing()) lineTable.getCellEditor().stopCellEditing();
                String problem = validateLines();
                if (problem != null) { error.setText(problem); return; }
                go(2);
            }
            case 2 -> {
                if ("Khác".equals(reason.getSelectedItem()) && note.getText().isBlank()) {
                    error.setText("Hãy nhập ghi chú cho lý do \"Khác\".");
                    return;
                }
                go(3);
            }
            case 3 -> createTicket();
            default -> reset();
        }
    }

    private String validateLines() {
        int chosen = 0;
        for (int r = 0; r < lineModel.getRowCount(); r++) {
            if (!Boolean.TRUE.equals(lineModel.getValueAt(r, 0))) continue;
            chosen++;
            int bought = (int) lineModel.getValueAt(r, 2);
            int qty;
            try {
                qty = Integer.parseInt(String.valueOf(lineModel.getValueAt(r, 3)).trim());
            } catch (NumberFormatException ex) {
                return "Số lượng trả ở dòng "+ (r + 1) + "phải là số nguyên.";
            }
            if (qty < 1 || qty > bought) return "Số lượng trả ở dòng "+ (r + 1) + "phải từ 1 đến "+ bought + ".";
        }
        return chosen == 0 ? "Hãy tick chọn ít nhất một mặt hàng cần trả.": null;
    }

    private long refundTotal() {
        long sum = 0;
        for (int r = 0; r < lineModel.getRowCount(); r++) {
            if (!Boolean.TRUE.equals(lineModel.getValueAt(r, 0))) continue;
            int qty = Integer.parseInt(String.valueOf(lineModel.getValueAt(r, 3)).trim());
            sum += qty * (long) (int) selected.lines()[r][2];
        }
        return sum;
    }

    private void fillSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hóa đơn gốc: ").append(selected.no()).append("– ").append(selected.customer()).append("\n\n");
        for (int r = 0; r < lineModel.getRowCount(); r++) {
            if (Boolean.TRUE.equals(lineModel.getValueAt(r, 0))) {
                sb.append("• ").append(lineModel.getValueAt(r, 1)).append("x ").append(lineModel.getValueAt(r, 3)).append('\n');
            }
        }
        long total = refundTotal();
        sb.append("\nLý do: ").append(reason.getSelectedItem());
        if (!note.getText().isBlank()) sb.append("(").append(note.getText().trim()).append(")");
        sb.append("\nHoàn tiền: ").append(refund.getSelectedItem()).append("\nGiá trị hoàn: ").append(Money.format(total));
        if (total >= APPROVAL_THRESHOLD) {
            sb.append("\n\nGiá trị từ ").append(Money.format(APPROVAL_THRESHOLD)).append("trở lên: phiếu sẽ chuyển Cửa hàng trưởng phê duyệt.");
        }
        summary.setText(sb.toString());
    }

    private void createTicket() {
        long total = refundTotal();
        boolean needApproval = total >= APPROVAL_THRESHOLD;
        String no = String.format("TH-2609-%03d", ++sequence);
        resultTitle.setText("Đã tạo phiếu "+ no);
        resultTitle.setForeground(needApproval ? AppColors.ORANGE : AppColors.GREEN);
        resultBody.setText(needApproval
            ? "Giá trị "+ Money.format(total) + "– đang chờ Cửa hàng trưởng phê duyệt."
            : "Giá trị "+ Money.format(total) + "– hoàn tiền bằng "+ String.valueOf(refund.getSelectedItem()).toLowerCase() + ".");
        go(4);
    }

    private void reset() {
        selected = null;
        invoiceTable.clearSelection();
        invoiceSearch.setText("");
        note.setText("");
        reason.setSelectedIndex(0);
        refund.setSelectedIndex(0);
        go(0);
    }

    @Override
    public ToolbarCapabilities toolbarCapabilities() {
        return new ToolbarCapabilities(false, false, step == 0, false, false, true);
    }

    @Override
    protected String helpText() {
        return "Đổi trả gồm 5 bước. Bước 1: chọn hóa đơn gốc. Bước 2: tick mặt hàng và nhập số lượng trả.\n"
            + "Bước 3: chọn lý do và hình thức hoàn. Bước 4: kiểm tra và tạo phiếu.";
    }

    @Override protected String helpTitle() { return "Đổi trả hàng"; }
}
