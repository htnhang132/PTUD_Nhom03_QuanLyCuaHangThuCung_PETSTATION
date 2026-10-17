package com.petstation.gui.components.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Money;

/** Bảng giá dịch vụ theo loài và khoảng cân nặng. Lưu dạng "Loài|Cân nặng|Giá;Loài|Cân nặng|Giá". */
public final class PriceTableEditor implements FieldEditor {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Loài", "Khoảng cân nặng", "Giá (₫)"}, 0);
    private final JTable table = new JTable(model);
    private final JPanel panel = new JPanel(new BorderLayout(0, 4));
    private final JButton add = new JButton("+ Thêm dòng");
    private final JButton remove = new JButton("Xóa dòng");

    public PriceTableEditor() {
        TableStyler.apply(table);
        table.setRowHeight(30);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(300, 150));
        sp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        add.addActionListener(e -> model.addRow(new Object[]{"Chó", "", ""}));
        remove.addActionListener(e -> {
            if (table.isEditing()) table.getCellEditor().stopCellEditing();
            int r = table.getSelectedRow();
            if (r >= 0) model.removeRow(table.convertRowIndexToModel(r));
        });
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bar.setOpaque(false);
        bar.add(add);
        bar.add(remove);
        panel.setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(bar, BorderLayout.SOUTH);
    }

    @Override public JComponent component() { return panel; }

    @Override public String get() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < model.getRowCount(); r++) {
            String species = String.valueOf(model.getValueAt(r, 0)).trim();
            String weight = String.valueOf(model.getValueAt(r, 1)).trim();
            long price = Money.parse(String.valueOf(model.getValueAt(r, 2)));
            if (species.isEmpty() && weight.isEmpty() && price < 0) continue;
            if (sb.length() > 0) sb.append(';');
            sb.append(species).append('|').append(weight).append('|').append(Math.max(price, 0));
        }
        return sb.toString();
    }

    @Override public void set(String value) {
        model.setRowCount(0);
        if (value == null || value.isBlank()) return;
        for (String part : value.split(";")) {
            String[] f = part.split("\\|", -1);
            if (f.length == 3) model.addRow(new Object[]{f[0], f[1], f[2].isEmpty() ? "" : Money.format(Money.parse(f[2]))});
        }
    }

    @Override public void setEnabled(boolean enabled) {
        table.setEnabled(enabled);
        add.setEnabled(enabled);
        remove.setEnabled(enabled);
    }

    /** Giá thấp nhất trong bảng giá, -1 nếu chưa có. */
    public static long minPrice(String value) {
        long min = -1;
        if (value == null) return min;
        for (String part : value.split(";")) {
            String[] f = part.split("\\|", -1);
            if (f.length == 3) {
                long p = Money.parse(f[2]);
                if (p >= 0 && (min < 0 || p < min)) min = p;
            }
        }
        return min;
    }

    /** Tìm giá theo loài và cân nặng (kg); trả -1 nếu không có dòng phù hợp. Dùng cho bước Chọn dịch vụ Spa. */
    public static long priceFor(String value, String species, double weightKg) {
        if (value == null) return -1;
        for (String part : value.split(";")) {
            String[] f = part.split("\\|", -1);
            if (f.length != 3 || !f[0].trim().equalsIgnoreCase(species)) continue;
            if (matchesWeight(f[1], weightKg)) return Money.parse(f[2]);
        }
        return -1;
    }

    private static boolean matchesWeight(String range, double kg) {
        String r = range.toLowerCase().replace("kg", "").replace(" ", "");
        if (r.isEmpty() || r.contains("mọi")) return true;
        try {
            if (r.startsWith("<")) return kg < Double.parseDouble(r.substring(1));
            if (r.startsWith(">")) return kg > Double.parseDouble(r.substring(1));
            if (r.contains("-")) {
                String[] p = r.split("-");
                return kg >= Double.parseDouble(p[0]) && kg <= Double.parseDouble(p[1]);
            }
        } catch (NumberFormatException ignored) { }
        return false;
    }
}
