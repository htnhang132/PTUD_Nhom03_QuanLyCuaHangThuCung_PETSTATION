package com.petstation.gui.components.common;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Renderer và Editor cho cột "Hành động"trong JTable
 * Hiển thị các nút Edit, Delete, hoặc custom actions
 */
public class ActionColumn {

    /**
     * Thêm cột hành động vào table với các nút Edit và Delete
     */
    public static void addEditDeleteColumn(JTable table, String columnName, 
                                          Consumer<Integer> onEdit, 
                                          Consumer<Integer> onDelete) {
        addActionColumn(table, columnName, new ActionButton[]{
            new ActionButton("", "Sửa", onEdit),
            new ActionButton("", "Xóa", onDelete)
        });
    }

    /**
     * Thêm cột hành động với các nút tùy chỉnh
     */
    public static void addActionColumn(JTable table, String columnName, ActionButton[] buttons) {
        // Tìm hoặc thêm cột
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int columnIndex = -1;
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (model.getColumnName(i).equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        
        if (columnIndex == -1) {
            model.addColumn(columnName);
            columnIndex = model.getColumnCount() - 1;
        }

        // Set renderer và editor
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setCellRenderer(new ActionRenderer(buttons));
        column.setCellEditor(new ActionEditor(buttons));
        int width = widthOf(buttons);
        column.setMinWidth(width);
        column.setMaxWidth(width + 40);
        column.setPreferredWidth(width);
    }

    /**
     * Class định nghĩa một action button
     */
    public static class ActionButton {
        String icon;
        String tooltip;
        Consumer<Integer> action;

        public ActionButton(String icon, String tooltip, Consumer<Integer> action) {
            this.icon = (icon == null || icon.isBlank()) ? tooltip : icon;
            this.tooltip = tooltip;
            this.action = action;
        }
    }

    /** Nút dạng chữ phẳng, hòa vào màu dòng (không còn khối nút xám trên nền dòng đang chọn). */
    private static JButton flatButton(ActionButton spec) {
        JButton button = new JButton(spec.icon);
        button.setToolTipText(spec.tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFont(com.petstation.gui.components.AppColors.font(Font.BOLD, 12));
        boolean danger = spec.tooltip != null && spec.tooltip.contains("Xóa");
        button.setForeground(danger ? com.petstation.gui.components.AppColors.DANGER_TEXT : com.petstation.gui.components.AppColors.BLUE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        FontMetrics fm = button.getFontMetrics(button.getFont());
        button.setPreferredSize(new Dimension(fm.stringWidth(spec.icon) + 14, 28));
        return button;
    }

    private static JPanel actionPanel(ActionButton[] buttons, java.util.List<JButton> sink) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 5));
        panel.setOpaque(true);
        for (ActionButton spec : buttons) {
            JButton b = flatButton(spec);
            sink.add(b);
            panel.add(b);
        }
        return panel;
    }

    private static int widthOf(ActionButton[] buttons) {
        JButton probe = new JButton();
        int total = 16;
        for (ActionButton b : buttons) {
            total += probe.getFontMetrics(com.petstation.gui.components.AppColors.font(Font.BOLD, 12)).stringWidth(b.icon) + 16;
        }
        return total;
    }

    private static Color rowBackground(JTable table, boolean selected, int row) {
        if (selected) return table.getSelectionBackground();
        return row % 2 == 0 ? table.getBackground() : com.petstation.gui.components.AppColors.ROW_ALT;
    }

    /** Renderer hiển thị các nút trong cell. */
    static class ActionRenderer implements TableCellRenderer {
        private final JPanel panel;

        public ActionRenderer(ActionButton[] buttons) {
            panel = actionPanel(buttons, new java.util.ArrayList<>());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            panel.setBackground(rowBackground(table, isSelected, row));
            return panel;
        }
    }

    /** Editor cho phép bấm vào các nút. */
    static class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private int currentRow;

        public ActionEditor(ActionButton[] buttons) {
            java.util.List<JButton> created = new java.util.ArrayList<>();
            panel = actionPanel(buttons, created);
            for (int i = 0; i < created.size(); i++) {
                ActionButton spec = buttons[i];
                created.get(i).addActionListener(e -> {
                    fireEditingStopped();
                    if (spec.action != null) {
                        SwingUtilities.invokeLater(() -> spec.action.accept(currentRow));
                    }
                });
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                    boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel.setBackground(rowBackground(table, true, row));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    /**
     * Thêm checkbox column cho bulk actions
     */
    public static void addCheckboxColumn(JTable table, String columnName) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // Thêm cột checkbox ở đầu
        model.addColumn(columnName);
        
        // Di chuyển cột mới về vị trí đầu tiên
        table.moveColumn(model.getColumnCount() - 1, 0);
        
        // Set renderer và editor cho checkbox
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setCellRenderer(new DefaultTableCellRenderer() {
            JCheckBox checkBox = new JCheckBox();
            {
                checkBox.setHorizontalAlignment(JLabel.CENTER);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                checkBox.setSelected(value != null && (Boolean) value);
                if (isSelected) {
                    checkBox.setBackground(table.getSelectionBackground());
                } else {
                    checkBox.setBackground(table.getBackground());
                }
                return checkBox;
            }
        });
        
        column.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        column.setMaxWidth(50);
        column.setMinWidth(50);
        
        // Thêm checkbox vào header để select all
        JCheckBox headerCheckbox = new JCheckBox();
        headerCheckbox.addActionListener(e -> {
            boolean selected = headerCheckbox.isSelected();
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(selected, i, 0);
            }
        });
        
        TableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                headerCheckbox.setHorizontalAlignment(JLabel.CENTER);
                return headerCheckbox;
            }
        };
        
        column.setHeaderRenderer(headerRenderer);
    }

    /**
     * Lấy danh sách các row đã được check
     */
    public static java.util.List<Integer> getCheckedRows(JTable table, int checkboxColumnIndex) {
        java.util.List<Integer> checkedRows = new java.util.ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, checkboxColumnIndex);
            if (value != null && (Boolean) value) {
                checkedRows.add(i);
            }
        }
        
        return checkedRows;
    }
}
