package com.petstation.gui.processing.sales;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.BasePanel;
import com.petstation.gui.components.DenominationCounter;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.FormPanel;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarCapabilities;
import com.petstation.service.CashShiftService;
import com.petstation.util.Money;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Mở ca / Kết ca / Bàn giao. Mở ca: chọn ca (tự gợi ý theo giờ), gõ số tờ từng mệnh giá + Enter để
 * sang dòng kế, Enter ở dòng cuối là mở ca. Kết ca: kiểm đếm tương tự, hiện chênh lệch ngay, ghi
 * người nhận bàn giao.
 */
public final class CashShiftPanel extends BasePanel {
    private final CashShiftService shift;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    // Trang mở ca
    private final JComboBox<String> shiftName =
            FormPanel.combo(new String[] {"Ca sáng", "Ca chiều", "Ca tối"});
    private final DenominationCounter openCounter = new DenominationCounter();
    private final JLabel openError = new JLabel("");
    private final JLabel lastHandover = new JLabel("");
    private final JLabel pendingBanner = new JLabel("");

    // Trang đang chạy ca
    private final JPanel stats = new JPanel(new GridLayout(1, 5, 10, 0));
    private final DenominationCounter closeCounter = new DenominationCounter();
    private final JLabel difference = new JLabel();
    private final JTextField receiver = FormPanel.textField("");
    private final JTextField note = FormPanel.textField("");
    private final JLabel closeError = new JLabel("");
    private final StyledButton closeButton =
            new StyledButton("Đóng ca và bàn giao", StyledButton.Variant.PRIMARY);

    public CashShiftPanel(CashShiftService shift) {
        this.shift = shift;
        setLayout(new BorderLayout(0, 14));
        JLabel title = sectionTitle("Mở ca / Kết ca / Bàn giao");
        title.setFont(AppColors.font(Font.BOLD, 21));
        add(title, BorderLayout.NORTH);

        root.setOpaque(false);
        root.add(buildOpen(), "open");
        root.add(buildRunning(), "running");
        add(root, BorderLayout.CENTER);
        shift.addListener(this::refresh);
        refresh();
    }

    private JPanel buildOpen() {
        JPanel p = card(new BorderLayout(0, 12));
        JPanel top = transparent(new BorderLayout(0, 8));
        top.add(sectionTitle("Mở ca làm việc"), BorderLayout.NORTH);
        pendingBanner.setOpaque(true);
        pendingBanner.setBackground(new Color(0xFF, 0xF4, 0xCC));
        pendingBanner.setForeground(new Color(0x7A, 0x5A, 0x00));
        pendingBanner.setFont(AppColors.font(Font.BOLD, 13));
        pendingBanner.setBorder(new EmptyBorder(8, 12, 8, 12));
        pendingBanner.setVisible(false);
        top.add(pendingBanner, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);

        // Trái: tiền đầu ca theo mệnh giá. Phải: chọn ca, tổng, nút mở ca.
        JPanel left = transparent(new BorderLayout(0, 6));
        JLabel hint =
                new JLabel(
                        "Gõ số tờ rồi nhấn Enter để sang mệnh giá kế tiếp. Enter ở dòng cuối sẽ mở"
                                + " ca.");
        hint.setFont(AppColors.font(Font.PLAIN, 12));
        hint.setForeground(AppColors.TEXT_GRAY);
        left.add(hint, BorderLayout.NORTH);
        left.add(openCounter, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(320, 0));
        right.add(small("Ca làm việc"));
        shiftName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        shiftName.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(shiftName);
        right.add(Box.createVerticalStrut(14));
        right.add(small("Tiền mặt đầu ca"));
        JLabel total = new JLabel("0₫");
        total.setFont(AppColors.font(Font.BOLD, 26));
        total.setForeground(AppColors.RED_BRAND);
        total.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(total);
        openCounter.onChange(() -> total.setText(Money.format(openCounter.total())));
        right.add(Box.createVerticalStrut(10));
        lastHandover.setForeground(AppColors.TEXT_GRAY);
        lastHandover.setFont(AppColors.font(Font.PLAIN, 12));
        lastHandover.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(lastHandover);
        right.add(Box.createVerticalStrut(10));
        openError.setForeground(AppColors.DANGER_TEXT);
        openError.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(openError);
        right.add(Box.createVerticalStrut(8));
        StyledButton open = new StyledButton("Mở ca (Enter)", StyledButton.Variant.PRIMARY);
        open.setAlignmentX(Component.LEFT_ALIGNMENT);
        open.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        open.addActionListener(e -> openShift());
        right.add(open);
        openCounter.onComplete(this::openShift);

        JPanel body = transparent(new BorderLayout(24, 0));
        body.add(left, BorderLayout.CENTER);
        body.add(right, BorderLayout.EAST);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRunning() {
        JPanel p = transparent(new BorderLayout(0, 14));
        stats.setOpaque(false);
        p.add(stats, BorderLayout.NORTH);

        JPanel count = card(new BorderLayout(0, 8));
        count.add(sectionTitle("Kiểm đếm tiền mặt trong két"), BorderLayout.NORTH);
        count.add(closeCounter, BorderLayout.CENTER);
        closeCounter.onChange(this::updateCountSummary);
        closeCounter.onComplete(() -> receiver.requestFocusInWindow());

        JPanel side = new JPanel();
        side.setOpaque(false);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        difference.setFont(AppColors.font(Font.BOLD, 18));
        difference.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(small("Chênh lệch so với dự kiến"));
        side.add(difference);
        side.add(Box.createVerticalStrut(12));
        side.add(small("Người nhận bàn giao"));
        receiver.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        receiver.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(receiver);
        side.add(Box.createVerticalStrut(8));
        side.add(small("Ghi chú (bắt buộc khi có chênh lệch)"));
        note.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(note);
        receiver.addActionListener(e -> note.requestFocusInWindow());
        note.addActionListener(e -> closeShift());
        closeError.setForeground(AppColors.DANGER_TEXT);
        closeError.setFont(AppColors.font(Font.PLAIN, 12));
        closeError.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(closeError);
        side.add(Box.createVerticalStrut(8));
        closeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        closeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        closeButton.addActionListener(e -> closeShift());
        side.add(closeButton);

        JPanel sideCard = card(new BorderLayout());
        sideCard.setPreferredSize(new Dimension(330, 0));
        sideCard.add(side, BorderLayout.NORTH);

        JPanel center = transparent(new BorderLayout(14, 0));
        center.add(count, BorderLayout.CENTER);
        center.add(sideCard, BorderLayout.EAST);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JLabel small(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.font(Font.PLAIN, 12));
        l.setForeground(AppColors.TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void openShift() {
        long cash = openCounter.total();
        if (cash == 0 && !Dialogs.confirm(this, "Tiền đầu ca là 0₫. Vẫn mở ca?")) return;
        openError.setText("");
        String name = String.valueOf(shiftName.getSelectedItem());
        try {
            shift.open(name, cash);
        } catch (RuntimeException exception) {
            Dialogs.warn(this, exception.getMessage());
        }
    }

    private void closeShift() {
        long total = closeCounter.total();
        long diff = total - shift.expectedCash();
        if (receiver.getText().isBlank()) {
            closeError.setText("Nhập tên người nhận bàn giao.");
            receiver.requestFocusInWindow();
            return;
        }
        if (diff != 0 && note.getText().isBlank()) {
            closeError.setText(
                    "Có chênh lệch " + Money.format(diff) + ": hãy nhập ghi chú bàn giao.");
            note.requestFocusInWindow();
            return;
        }
        boolean ok =
                Dialogs.confirm(
                        this,
                        "Đóng "
                                + shift.shiftName()
                                + " với tiền thực đếm "
                                + Money.format(total)
                                + (diff == 0
                                        ? " (khớp)."
                                        : " (chênh lệch " + Money.format(diff) + ").")
                                + "\nBàn giao cho: "
                                + receiver.getText().trim()
                                + "\nSau khi đóng ca không thể bán hàng cho đến khi mở ca mới.");
        if (ok) {
            try {
                shift.close(
                        total,
                        "Bàn giao "
                                + receiver.getText().trim()
                                + (note.getText().isBlank() ? "" : " – " + note.getText().trim()));
            } catch (RuntimeException exception) {
                Dialogs.warn(this, exception.getMessage());
                return;
            }
            note.setText("");
            receiver.setText("");
        }
    }

    private void updateCountSummary() {
        long diff = closeCounter.total() - shift.expectedCash();
        difference.setText(
                diff == 0
                        ? "Khớp"
                        : (diff > 0 ? "Thừa " : "Thiếu ") + Money.format(Math.abs(diff)));
        difference.setForeground(diff == 0 ? AppColors.GREEN : AppColors.DANGER_TEXT);
    }

    private static String suggestedShift() {
        int h = LocalTime.now().getHour();
        return h < 12 ? "Ca sáng" : h < 18 ? "Ca chiều" : "Ca tối";
    }

    private void refresh() {
        if (!shift.isOpen()) {
            lastHandover.setText(
                    shift.lastHandover().isEmpty() ? "" : "Ca gần nhất – " + shift.lastHandover());
            shiftName.setSelectedItem(suggestedShift());
            openCounter.reset();
            boolean pending = !shift.pendingTitle().isEmpty();
            pendingBanner.setVisible(pending);
            pendingBanner.setText(
                    pending
                            ? "Mở ca xong hệ thống sẽ đưa bạn về \"" + shift.pendingTitle() + "\"."
                            : "");
            cards.show(root, "open");
            openCounter.focusFirst();
            return;
        }
        stats.removeAll();
        stats.add(
                statCard(
                        "Ca hiện tại (từ "
                                + shift.openedAt().format(DateTimeFormatter.ofPattern("HH:mm"))
                                + ")",
                        shift.shiftName()));
        stats.add(statCard("Tiền đầu ca", Money.format(shift.openingCash())));
        stats.add(statCard("Doanh thu tiền mặt", Money.format(shift.cashSales())));
        stats.add(
                statCard(
                        "Chuyển khoản / thẻ",
                        Money.format(shift.transferSales() + shift.cardSales())));
        stats.add(statCard("Dự kiến trong két", Money.format(shift.expectedCash())));
        stats.revalidate();
        stats.repaint();
        closeCounter.reset();
        closeError.setText("");
        updateCountSummary();
        cards.show(root, "running");
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
    protected String helpText() {
        return "Mở ca: chọn ca, gõ số tờ từng mệnh giá rồi Enter để sang dòng kế; Enter ở dòng cuối"
                + " là mở ca.\n"
                + "Kết ca: đếm tiền thực tế, so với tiền dự kiến (đầu ca + doanh thu tiền mặt)."
                + " Có chênh lệch thì phải ghi chú.";
    }

    @Override
    protected String helpTitle() {
        return "Mở ca / Kết ca / Bàn giao";
    }
}
