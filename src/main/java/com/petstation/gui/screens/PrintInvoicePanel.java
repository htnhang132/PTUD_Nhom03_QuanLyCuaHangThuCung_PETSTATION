package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarCapabilities;
import com.petstation.gui.navigation.SaleContext;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.navigation.ShiftState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/** Xem trước và in hóa đơn vừa thanh toán. */
public final class PrintInvoicePanel extends BasePanel {
    private static final int WIDTH = 40;
    private final SaleContext sale;
    private final Consumer<ScreenId> navigator;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final JTextArea preview = new JTextArea();
    private final JLabel banner = new JLabel();

    public PrintInvoicePanel(SaleContext sale, Consumer<ScreenId> navigator) {
        this.sale = sale;
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 14));

        JLabel title = sectionTitle("In hóa đơn");
        title.setFont(AppColors.font(Font.BOLD, 21));
        add(title, BorderLayout.NORTH);

        root.setOpaque(false);

        banner.setOpaque(true);
        banner.setBackground(new Color(0xE6, 0xF4, 0xEA));
        banner.setForeground(new Color(0x15, 0x57, 0x24));
        banner.setFont(AppColors.font(Font.BOLD, 14));
        banner.setBorder(new EmptyBorder(10, 14, 10, 14));

        preview.setEditable(false);
        preview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        preview.setBackground(Color.WHITE);
        preview.setBorder(new EmptyBorder(16, 20, 16, 20));
        JScrollPane scroll = new JScrollPane(preview);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        scroll.setPreferredSize(new Dimension(430, 0));
        JPanel paper = new JPanel(new BorderLayout());
        paper.setOpaque(false);
        paper.setBorder(new EmptyBorder(0, 0, 0, 0));
        JPanel centered = new JPanel(new GridBagLayout());
        centered.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.VERTICAL;
        gc.weighty = 1;
        centered.add(scroll, gc);
        paper.add(centered, BorderLayout.CENTER);

        StyledButton print = new StyledButton("In hóa đơn (Ctrl+P)", StyledButton.Variant.PRIMARY);
        print.addActionListener(e -> onPrint());
        StyledButton next = new StyledButton("Lập hóa đơn mới (F2)", StyledButton.Variant.SECONDARY);
        next.addActionListener(e -> navigator.accept(ScreenId.POS));
        JLabel tip = new JLabel("Xem lại hóa đơn bên trên. Nút In mở hộp thoại máy in (có thể chọn \"Lưu thành PDF\").");
        tip.setFont(AppColors.font(Font.PLAIN, 12));
        tip.setForeground(AppColors.TEXT_GRAY);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actions.setOpaque(false);
        actions.add(next);
        actions.add(print);
        JPanel south = transparent(new BorderLayout(0, 6));
        south.add(actions, BorderLayout.CENTER);
        south.add(tip, BorderLayout.SOUTH);
        tip.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel form = transparent(new BorderLayout(0, 12));
        form.add(banner, BorderLayout.NORTH);
        form.add(paper, BorderLayout.CENTER);
        form.add(south, BorderLayout.SOUTH);
        root.add(form, "receipt");

        StyledButton go = new StyledButton("Về lập đơn hàng", StyledButton.Variant.PRIMARY);
        go.addActionListener(e -> navigator.accept(ScreenId.POS));
        root.add(emptyState("Chưa có hóa đơn nào để in", "Hóa đơn xuất hiện ở đây sau khi thanh toán.", go), "empty");
        add(root, BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        SaleContext.Receipt r = sale.lastReceipt();
        if (r == null) {
            cards.show(root, "empty");
            return;
        }
        cards.show(root, "receipt");
        String change = r.method() == ShiftState.Method.CASH ? "  ·  Tiền thừa " + Money.format(r.change()) : "";
        banner.setText("✔ Đã thanh toán  " + r.invoiceNo() + "  ·  Tổng " + Money.format(r.total()) + change);
        preview.setText(render(r));
        preview.setCaretPosition(0);
    }

    static String render(SaleContext.Receipt r) {
        StringBuilder sb = new StringBuilder();
        sb.append(center("PET STATION")).append('\n');
        sb.append(center("Hóa đơn bán hàng")).append('\n');
        sb.append("-".repeat(WIDTH)).append('\n');
        sb.append("Số HĐ : ").append(r.invoiceNo()).append('\n');
        sb.append("Ngày  : ").append(r.time().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append('\n');
        sb.append("Thu ngân: ").append(r.cashier()).append('\n');
        sb.append("Khách : ").append(r.customer()).append('\n');
        sb.append("-".repeat(WIDTH)).append('\n');
        for (SaleContext.Line l : r.lines()) {
            sb.append(cut(l.name(), WIDTH)).append('\n');
            sb.append(pair(""+ l.quantity() + "x "+ Money.format(l.unitPrice()), Money.format(l.amount()))).append('\n');
        }
        sb.append("-".repeat(WIDTH)).append('\n');
        sb.append(pair("Tạm tính", Money.format(r.subtotal()))).append('\n');
        if (r.discount() > 0) {
            sb.append(pair("Giảm giá"+ (r.voucher().isEmpty() ? "": "("+ r.voucher() + ")"), "-"+ Money.format(r.discount()))).append('\n');
        }
        sb.append(pair("TỔNG CỘNG", Money.format(r.total()))).append('\n');
        String method = switch (r.method()) {
            case CASH -> "Tiền mặt";
            case TRANSFER -> "Chuyển khoản/QR";
            case CARD -> "Thẻ";
        };
        sb.append(pair("Thanh toán ("+ method + ")", Money.format(r.received()))).append('\n');
        if (r.method() == ShiftState.Method.CASH) sb.append(pair("Tiền thừa", Money.format(r.change()))).append('\n');
        sb.append("-".repeat(WIDTH)).append('\n');
        sb.append(center("Cảm ơn quý khách!")).append('\n');
        return sb.toString();
    }

    private static String center(String s) {
        int pad = Math.max(0, (WIDTH - s.length()) / 2);
        return " ".repeat(pad) + s;
    }

    private static String cut(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String pair(String left, String right) {
        int space = Math.max(1, WIDTH - left.length() - right.length());
        return left + " ".repeat(space) + right;
    }

    @Override
    public ToolbarCapabilities toolbarCapabilities() {
        return new ToolbarCapabilities(false, false, false, sale.lastReceipt() != null, false, true);
    }

    @Override
    public void onPrint() {
        if (sale.lastReceipt() == null) return;
        try {
            preview.print(null, null, true, null, null, true);
        } catch (java.awt.print.PrinterException | HeadlessException ex) {
            Dialogs.showMessageDialog(this, "Không thể in: "+ ex.getMessage(), "In hóa đơn", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override public void onShow() { refresh(); }

    @Override
    protected String helpText() {
        return "Bấm In hóa đơn để gửi tới máy in. Bấm Lập đơn mới để bắt đầu đơn tiếp theo.";
    }

    @Override protected String helpTitle() { return "In hóa đơn"; }
}
