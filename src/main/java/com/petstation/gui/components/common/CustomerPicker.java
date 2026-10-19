package com.petstation.gui.components.common;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Dialogs;
import com.petstation.model.SaleContext;
import com.petstation.service.CatalogService;
import com.petstation.util.Money;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 * Chọn khách cho hóa đơn bằng MỘT ô tìm (SĐT, mã hoặc tên). Chọn xong hiện hạng, điểm và ô "Dùng
 * điểm". Không chọn thì là khách lẻ.
 */
public final class CustomerPicker extends JPanel {
    private final SaleContext sale;
    private final Runnable onChange;
    private final JTextField input = new JTextField();
    private final JPanel chip = new JPanel();
    private final JLabel chipText = new JLabel();
    private final JSpinner points = new JSpinner(new SpinnerNumberModel(0, 0, 0, 1));
    private final JLabel pointsHint = new JLabel();
    private boolean updating;

    public CustomerPicker(SaleContext sale, Runnable onChange) {
        this.sale = sale;
        this.onChange = onChange;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label =
                new JLabel("Khách hàng (SĐT, mã hoặc tên — Enter để tìm; bỏ trống = khách lẻ)");
        label.setFont(AppColors.font(Font.PLAIN, 11));
        label.setForeground(AppColors.TEXT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        input.setFont(AppColors.font(Font.PLAIN, 13));
        input.getAccessibleContext().setAccessibleName("Tìm khách hàng");
        input.addActionListener(e -> search());
        JButton find = new JButton("Tìm");
        find.setFocusPainted(false);
        find.addActionListener(e -> search());
        row.add(input, BorderLayout.CENTER);
        row.add(find, BorderLayout.EAST);
        add(row);

        chip.setLayout(new BorderLayout(8, 0));
        chip.setBackground(new Color(0xEA, 0xF3, 0xFF));
        chip.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xBF, 0xD6, 0xF5)),
                        new EmptyBorder(6, 10, 6, 10)));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        chipText.setFont(AppColors.font(Font.BOLD, 12));
        JButton clear = new JButton("Bỏ chọn");
        clear.setFocusPainted(false);
        clear.addActionListener(e -> clear());
        JPanel usePoints = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        usePoints.setOpaque(false);
        JLabel useLabel = new JLabel("Dùng điểm:");
        useLabel.setFont(AppColors.font(Font.PLAIN, 12));
        points.setPreferredSize(new Dimension(80, 26));
        points.addChangeListener(
                e -> {
                    if (updating) return;
                    sale.setPointsUsed(((Number) points.getValue()).longValue());
                    refreshPointsHint();
                    onChange.run();
                });
        pointsHint.setFont(AppColors.font(Font.PLAIN, 11));
        pointsHint.setForeground(AppColors.TEXT_GRAY);
        usePoints.add(useLabel);
        usePoints.add(points);
        usePoints.add(pointsHint);
        JPanel text = new JPanel(new BorderLayout(0, 2));
        text.setOpaque(false);
        text.add(chipText, BorderLayout.NORTH);
        text.add(usePoints, BorderLayout.CENTER);
        chip.add(text, BorderLayout.CENTER);
        chip.add(clear, BorderLayout.EAST);
        chip.setVisible(false);
        add(javax.swing.Box.createVerticalStrut(4));
        add(chip);
        sync();
    }

    private void search() {
        String q = input.getText().trim();
        if (q.isEmpty()) {
            clear();
            return;
        }
        String digits = q.replaceAll("[\\s.]", "");
        List<Object[]> hits = new ArrayList<>();
        for (Object[] r : CatalogService.customers()) {
            if ("Ngừng hoạt động".equals(r[8])) continue;
            if (String.valueOf(r[2]).equals(digits) || String.valueOf(r[0]).equalsIgnoreCase(q)) {
                hits.clear();
                hits.add(r);
                break;
            }
            if (String.valueOf(r[1]).toLowerCase().contains(q.toLowerCase())) hits.add(r);
        }
        if (hits.isEmpty()) {
            Dialogs.info(
                    this,
                    "Không tìm thấy khách \""
                            + q
                            + "\".\n"
                            + "Thêm khách mới tại Danh mục ▸ Khách hàng, hoặc để trống để bán cho"
                            + " khách lẻ.");
            return;
        }
        Object[] chosen = hits.get(0);
        if (hits.size() > 1) {
            String[] opts =
                    hits.stream()
                            .map(r -> r[0] + " - " + r[1] + " (" + r[2] + ")")
                            .toArray(String[]::new);
            Object pick =
                    JOptionPane.showInputDialog(
                            this,
                            "Có nhiều khách phù hợp, chọn một:",
                            "Chọn khách hàng",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opts,
                            opts[0]);
            if (pick == null) return;
            for (int i = 0; i < opts.length; i++) if (opts[i].equals(pick)) chosen = hits.get(i);
        }
        sale.setCustomer(
                String.valueOf(chosen[0]),
                String.valueOf(chosen[1]),
                Money.parse(String.valueOf(chosen[7])));
        input.setText("");
        sync();
        onChange.run();
    }

    public void clear() {
        sale.setCustomer("", "", 0);
        input.setText("");
        sync();
        onChange.run();
    }

    /** Đồng bộ giao diện theo hóa đơn hiện tại (ví dụ sau khi thanh toán xong hóa đơn được xóa). */
    public void sync() {
        boolean has = !sale.customerCode().isEmpty();
        chip.setVisible(has);
        if (has) {
            String tier = "";
            for (Object[] r : CatalogService.customers())
                if (r[0].equals(sale.customerCode())) tier = String.valueOf(r[6]);
            chipText.setText(
                    sale.customerName()
                            + "  ·  Hạng "
                            + tier
                            + "  ·  "
                            + sale.customerPoints()
                            + " điểm");
            updating = true;
            long max =
                    Math.max(
                            0,
                            Math.min(
                                    sale.customerPoints(),
                                    (sale.subtotal()
                                                    - sale.promotionDiscount()
                                                    - sale.voucherAmount())
                                            / Math.max(
                                                    1,
                                                    com.petstation.service.SettingsService
                                                            .vndPerRedeemedPoint())));
            points.setModel(
                    new SpinnerNumberModel(
                            (int) Math.min(sale.pointsUsed(), max), 0, (int) max, 1));
            updating = false;
            refreshPointsHint();
        }
        revalidate();
        repaint();
    }

    private void refreshPointsHint() {
        pointsHint.setText(
                "= "
                        + Money.format(sale.pointsValue())
                        + " (1 điểm = "
                        + Money.format(com.petstation.service.SettingsService.vndPerRedeemedPoint())
                        + ")");
    }
}
