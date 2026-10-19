package com.petstation.gui.processing.sales;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.BasePanel;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.FormPanel;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarCapabilities;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.model.SaleContext;
import com.petstation.service.CashShiftService;
import com.petstation.util.Money;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Thanh toán hóa đơn đang lập: chọn phương thức, áp voucher, nhập tiền khách đưa, tính tiền thừa tự
 * động.
 */
public final class PaymentPanel extends BasePanel {
    private final SaleContext sale;
    private final CashShiftService shift;
    private final String cashier;
    private final Consumer<ScreenId> navigator;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final JPanel lineHolder = transparent(new BorderLayout());
    private final JLabel subtotal = new JLabel();
    private final JLabel discount = new JLabel();
    private final JLabel total = new JLabel();
    private final JTextField voucherField = FormPanel.textField("");
    private final JLabel voucherMessage = new JLabel("");
    private final JRadioButton cash = new JRadioButton("Tiền mặt", true);
    private final JRadioButton transfer = new JRadioButton("Chuyển khoản / QR");
    private final JRadioButton cardPay = new JRadioButton("Thẻ");
    private final JTextField received = FormPanel.textField("");
    private final JPanel quickAmounts = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final JLabel changeLabel = new JLabel("");
    private final StyledButton confirm =
            new StyledButton("Xác nhận thanh toán (F9)", StyledButton.Variant.PRIMARY);
    private boolean busy;

    public PaymentPanel(
            SaleContext sale,
            CashShiftService shift,
            String cashier,
            Consumer<ScreenId> navigator) {
        this.sale = sale;
        this.shift = shift;
        this.cashier = cashier;
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 14));

        JLabel title = sectionTitle("Thanh toán");
        title.setFont(AppColors.font(Font.BOLD, 21));
        JLabel hint = new JLabel("Kiểm tra hóa đơn, chọn phương thức và xác nhận.");
        hint.setForeground(AppColors.TEXT_GRAY);
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(3));
        head.add(hint);
        add(head, BorderLayout.NORTH);

        root.setOpaque(false);
        root.add(buildForm(), "form");
        StyledButton back = new StyledButton("Về lập đơn hàng", StyledButton.Variant.PRIMARY);
        back.addActionListener(e -> navigator.accept(ScreenId.POS));
        root.add(
                emptyState(
                        "Chưa có hóa đơn để thanh toán",
                        "Hãy thêm sản phẩm ở màn Lập đơn hàng trước.",
                        back),
                "empty");
        add(root, BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(1, 2, 16, 0));
        form.setOpaque(false);

        JPanel left = card(new BorderLayout(0, 12));
        left.add(sectionTitle("Hóa đơn"), BorderLayout.NORTH);
        left.add(lineHolder, BorderLayout.CENTER);
        JPanel sums = new JPanel(new GridLayout(3, 2, 0, 4));
        sums.setOpaque(false);
        sums.add(new JLabel("Tạm tính"));
        subtotal.setHorizontalAlignment(SwingConstants.RIGHT);
        sums.add(subtotal);
        sums.add(new JLabel("Giảm giá và voucher"));
        discount.setHorizontalAlignment(SwingConstants.RIGHT);
        sums.add(discount);
        JLabel totalText = new JLabel("Cần thanh toán");
        totalText.setFont(AppColors.font(Font.BOLD, 15));
        sums.add(totalText);
        total.setHorizontalAlignment(SwingConstants.RIGHT);
        total.setFont(AppColors.font(Font.BOLD, 24));
        total.setForeground(AppColors.RED_BRAND);
        sums.add(total);
        left.add(sums, BorderLayout.SOUTH);

        JPanel right = card(new BorderLayout(0, 12));
        right.add(sectionTitle("Phương thức thanh toán"), BorderLayout.NORTH);
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        ButtonGroup group = new ButtonGroup();
        JPanel methods = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        methods.setOpaque(false);
        for (JRadioButton r : new JRadioButton[] {cash, transfer, cardPay}) {
            r.setOpaque(false);
            r.setFont(AppColors.font(Font.PLAIN, 13));
            r.addActionListener(e -> updateMethod());
            group.add(r);
            methods.add(r);
        }
        methods.setAlignmentX(Component.LEFT_ALIGNMENT);
        methods.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        body.add(methods);
        body.add(Box.createVerticalStrut(14));

        body.add(label("Mã voucher"));
        JPanel voucherRow = new JPanel(new BorderLayout(8, 0));
        voucherRow.setOpaque(false);
        voucherField.setPreferredSize(new Dimension(180, 36));
        StyledButton apply = new StyledButton("Áp dụng", StyledButton.Variant.GHOST);
        apply.addActionListener(e -> applyVoucher());
        voucherField.addActionListener(e -> applyVoucher());
        voucherRow.add(voucherField, BorderLayout.CENTER);
        voucherRow.add(apply, BorderLayout.EAST);
        voucherRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        voucherRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        body.add(voucherRow);
        voucherMessage.setFont(AppColors.font(Font.PLAIN, 11));
        voucherMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        voucherMessage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        body.add(voucherMessage);
        body.add(Box.createVerticalStrut(10));

        body.add(label("Tiền khách đưa"));
        received.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        received.setAlignmentX(Component.LEFT_ALIGNMENT);
        received.getAccessibleContext().setAccessibleName("Tiền khách đưa");
        received.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            @Override
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                validateInput();
                            }

                            @Override
                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                validateInput();
                            }

                            @Override
                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                validateInput();
                            }
                        });
        body.add(received);
        body.add(Box.createVerticalStrut(6));
        quickAmounts.setOpaque(false);
        quickAmounts.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickAmounts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        body.add(quickAmounts);
        body.add(Box.createVerticalStrut(12));
        changeLabel.setFont(AppColors.font(Font.BOLD, 16));
        changeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(changeLabel);
        body.add(Box.createVerticalGlue());
        JPanel bodyHolder = transparent(new BorderLayout());
        bodyHolder.add(body, BorderLayout.NORTH);
        right.add(bodyHolder, BorderLayout.CENTER);

        confirm.setPreferredSize(new Dimension(0, 46));
        confirm.addActionListener(e -> confirmPayment());
        StyledButton cancel = new StyledButton("Quay lại sửa đơn", StyledButton.Variant.GHOST);
        cancel.addActionListener(e -> navigator.accept(ScreenId.POS));
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(confirm);
        right.add(actions, BorderLayout.SOUTH);

        form.add(left);
        form.add(right);
        return form;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.font(Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    private CashShiftService.Method method() {
        return transfer.isSelected()
                ? CashShiftService.Method.TRANSFER
                : cardPay.isSelected()
                        ? CashShiftService.Method.CARD
                        : CashShiftService.Method.CASH;
    }

    private void applyVoucher() {
        String error = sale.applyVoucher(voucherField.getText());
        if (error != null) {
            voucherMessage.setForeground(AppColors.DANGER_TEXT);
            voucherMessage.setText(error);
        } else if (sale.voucherCode().isEmpty()) {
            voucherMessage.setText("");
        } else {
            voucherMessage.setForeground(AppColors.GREEN);
            voucherMessage.setText(
                    "Đã áp dụng "
                            + sale.voucherCode()
                            + ": −"
                            + Money.format(sale.voucherAmount()));
        }
        refreshTotals();
    }

    private void refresh() {
        if (sale.isEmpty()) {
            cards.show(root, "empty");
            return;
        }
        cards.show(root, "form");
        DefaultTableModel model =
                new DefaultTableModel(new String[] {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"}, 0) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
        for (SaleContext.Line l : sale.lines()) {
            model.addRow(
                    new Object[] {
                        l.name(),
                        l.quantity(),
                        Money.format(l.unitPrice()),
                        Money.format(l.amount())
                    });
        }
        lineHolder.removeAll();
        lineHolder.add(styledTable(model), BorderLayout.CENTER);
        lineHolder.revalidate();
        voucherField.setText(sale.voucherCode());
        voucherMessage.setText("");
        received.setText("");
        busy = false;
        cash.setSelected(true);
        updateMethod();
        refreshTotals();
    }

    private void refreshTotals() {
        subtotal.setText(Money.format(sale.subtotal()));
        long d = sale.discount();
        discount.setText(d == 0 ? "0₫" : "−" + Money.format(d));
        total.setText(Money.format(sale.total()));
        rebuildQuickAmounts();
        validateInput();
    }

    private void rebuildQuickAmounts() {
        quickAmounts.removeAll();
        long t = sale.total();
        Set<Long> options = new LinkedHashSet<>();
        options.add(t);
        for (long step : new long[] {50_000, 100_000, 500_000}) {
            long up = ((t + step - 1) / step) * step;
            if (up >= t) options.add(up);
        }
        for (long amount : options) {
            JButton b = new JButton(amount == t ? "Đủ tiền" : Money.format(amount));
            b.setFont(AppColors.font(Font.PLAIN, 12));
            b.setFocusPainted(false);
            b.addActionListener(e -> received.setText(String.valueOf(amount)));
            quickAmounts.add(b);
        }
        quickAmounts.revalidate();
        quickAmounts.repaint();
    }

    private void updateMethod() {
        boolean isCash = method() == CashShiftService.Method.CASH;
        received.setEnabled(isCash);
        for (Component c : quickAmounts.getComponents()) c.setEnabled(isCash);
        validateInput();
    }

    private void validateInput() {
        if (sale.isEmpty()) return;
        long t = sale.total();
        if (method() != CashShiftService.Method.CASH) {
            changeLabel.setForeground(AppColors.TEXT_GRAY);
            changeLabel.setText(
                    "Thu đúng "
                            + Money.format(t)
                            + "qua "
                            + (method() == CashShiftService.Method.TRANSFER
                                    ? "chuyển khoản/QR"
                                    : "thẻ"));
            confirm.setEnabled(!busy);
            return;
        }
        long r = Money.parse(received.getText());
        if (received.getText().isBlank()) {
            changeLabel.setForeground(AppColors.TEXT_GRAY);
            changeLabel.setText("Nhập số tiền khách đưa");
            confirm.setEnabled(false);
        } else if (r < 0) {
            changeLabel.setForeground(AppColors.DANGER_TEXT);
            changeLabel.setText("Số tiền không hợp lệ");
            confirm.setEnabled(false);
        } else if (r < t) {
            changeLabel.setForeground(AppColors.DANGER_TEXT);
            changeLabel.setText("Còn thiếu " + Money.format(t - r));
            confirm.setEnabled(false);
        } else {
            changeLabel.setForeground(AppColors.GREEN);
            changeLabel.setText("Tiền thừa trả khách: " + Money.format(r - t));
            confirm.setEnabled(!busy);
        }
    }

    private void confirmPayment() {
        if (busy || sale.isEmpty()) return;
        long t = sale.total();
        long r = method() == CashShiftService.Method.CASH ? Money.parse(received.getText()) : t;
        if (r < t) return;
        int ok =
                Dialogs.showConfirmDialog(
                        this,
                        "Xác nhận thu " + Money.format(t) + "?",
                        "Xác nhận thanh toán",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        busy = true;
        confirm.setEnabled(false);
        try {
            SaleContext.Receipt receipt = sale.complete(method(), r, cashier);
            shift.recordSale(method(), receipt.total());
            sale.clear();
            navigator.accept(ScreenId.PRINT_INVOICE);
        } catch (RuntimeException exception) {
            busy = false;
            validateInput();
            Dialogs.warn(this, "Chưa thanh toán: " + exception.getMessage());
        }
    }

    @Override
    public ToolbarCapabilities toolbarCapabilities() {
        return new ToolbarCapabilities(false, false, false, false, false, true);
    }

    @Override
    public void onShow() {
        refresh();
    }

    @Override
    public void onSave() {}

    @Override
    protected String helpText() {
        return "Chọn phương thức, nhập tiền khách đưa (nút gợi ý bên dưới ô nhập).\n"
                + "Nút xác nhận chỉ bật khi số tiền đủ. Voucher được kiểm tra theo hạn dùng, số"
                + " lượt và điều kiện trong SQL Server.";
    }

    @Override
    protected String helpTitle() {
        return "Thanh toán";
    }
}
