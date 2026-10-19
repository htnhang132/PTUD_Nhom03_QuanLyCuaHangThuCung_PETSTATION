package com.petstation.gui.components;

import com.petstation.util.Money;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Bảng kiểm đếm tiền theo mệnh giá. Gõ số tờ rồi Enter (hoặc mũi tên xuống) để sang mệnh giá kế
 * tiếp; Enter ở dòng cuối gọi {@code onComplete}. Tổng tiền cập nhật ngay khi gõ.
 */
public final class DenominationCounter extends JPanel {
    public static final long[] DENOMINATIONS = {
        500_000, 200_000, 100_000, 50_000, 20_000, 10_000, 5_000, 2_000, 1_000
    };

    private final JTextField[] fields = new JTextField[DENOMINATIONS.length];
    private final JLabel[] amounts = new JLabel[DENOMINATIONS.length];
    private final JLabel totalLabel = new JLabel("0₫", SwingConstants.RIGHT);
    private Runnable onChange = () -> {};
    private Runnable onComplete = () -> {};

    public DenominationCounter() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 6, 3, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        addHeader("Mệnh giá", 0, c);
        addHeader("Số tờ", 1, c);
        addHeader("Thành tiền", 2, c);

        for (int i = 0; i < DENOMINATIONS.length; i++) {
            final int row = i;
            JLabel name = new JLabel(Money.format(DENOMINATIONS[i]));
            name.setFont(AppColors.font(Font.BOLD, 14));
            c.gridy = i + 1;
            c.gridx = 0;
            c.weightx = 0.3;
            add(name, c);

            JTextField f = new JTextField("0", 5);
            f.setFont(AppColors.font(Font.BOLD, 15));
            f.setHorizontalAlignment(SwingConstants.RIGHT);
            f.setPreferredSize(new Dimension(80, 32));
            f.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AppColors.BORDER),
                            BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            f.addFocusListener(
                    new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            SwingUtilities.invokeLater(f::selectAll);
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            normalize(row);
                        }
                    });
            f.addKeyListener(
                    new KeyAdapter() {
                        @Override
                        public void keyReleased(KeyEvent e) {
                            refresh(row);
                        }

                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ENTER
                                    || e.getKeyCode() == KeyEvent.VK_DOWN) {
                                e.consume();
                                normalize(row);
                                if (row + 1 < fields.length) focus(row + 1);
                                else if (e.getKeyCode() == KeyEvent.VK_ENTER) onComplete.run();
                            } else if (e.getKeyCode() == KeyEvent.VK_UP && row > 0) {
                                e.consume();
                                normalize(row);
                                focus(row - 1);
                            }
                        }
                    });
            fields[i] = f;
            c.gridx = 1;
            c.weightx = 0.2;
            add(f, c);

            amounts[i] = new JLabel("0₫", SwingConstants.RIGHT);
            amounts[i].setFont(AppColors.font(Font.PLAIN, 13));
            amounts[i].setForeground(AppColors.TEXT_GRAY);
            c.gridx = 2;
            c.weightx = 0.5;
            add(amounts[i], c);
        }

        JPanel line = new JPanel();
        line.setBackground(AppColors.BORDER);
        line.setPreferredSize(new Dimension(1, 1));
        c.gridy = DENOMINATIONS.length + 1;
        c.gridx = 0;
        c.gridwidth = 3;
        add(line, c);

        JLabel totalName = new JLabel("Tổng cộng");
        totalName.setFont(AppColors.font(Font.BOLD, 15));
        c.gridy = DENOMINATIONS.length + 2;
        c.gridx = 0;
        c.gridwidth = 2;
        add(totalName, c);
        totalLabel.setFont(AppColors.font(Font.BOLD, 20));
        totalLabel.setForeground(AppColors.RED_BRAND);
        c.gridx = 2;
        c.gridwidth = 1;
        add(totalLabel, c);
    }

    private void addHeader(String text, int col, GridBagConstraints c) {
        JLabel l = new JLabel(text, col == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
        l.setFont(AppColors.font(Font.BOLD, 12));
        l.setForeground(AppColors.TEXT_GRAY);
        c.gridy = 0;
        c.gridx = col;
        c.gridwidth = 1;
        add(l, c);
    }

    public void onChange(Runnable r) {
        this.onChange = r == null ? () -> {} : r;
    }

    public void onComplete(Runnable r) {
        this.onComplete = r == null ? () -> {} : r;
    }

    public long total() {
        long sum = 0;
        for (int i = 0; i < fields.length; i++) sum += count(i) * DENOMINATIONS[i];
        return sum;
    }

    public void reset() {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setText("0");
            amounts[i].setText("0₫");
        }
        totalLabel.setText("0₫");
        onChange.run();
    }

    public void focusFirst() {
        SwingUtilities.invokeLater(
                () -> {
                    fields[0].requestFocusInWindow();
                    fields[0].selectAll();
                });
    }

    private void focus(int row) {
        fields[row].requestFocusInWindow();
        fields[row].selectAll();
    }

    private int count(int row) {
        String digits = fields[row].getText().replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 7) return 0;
        return Integer.parseInt(digits);
    }

    private void normalize(int row) {
        fields[row].setText(String.valueOf(count(row)));
        refresh(row);
    }

    private void refresh(int row) {
        amounts[row].setText(Money.format(count(row) * DENOMINATIONS[row]));
        amounts[row].setForeground(count(row) > 0 ? AppColors.TEXT_DARK : AppColors.TEXT_GRAY);
        totalLabel.setText(Money.format(total()));
        onChange.run();
    }
}
