package com.petstation.gui.components.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.petstation.gui.components.AppColors;

/**
 * Kiểu bảng dùng chung cho mọi màn danh sách: dòng chọn màu hồng nhạt (không dùng xanh dương mặc định),
 * dòng xen kẽ, chữ số căn phải, cột trạng thái tô màu, tiêu đề đồng nhất.
 */
public final class TableStyler {
    private TableStyler() {}

    public static void apply(JTable table) {
        table.setFont(AppColors.font(Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setGridColor(AppColors.BORDER);
        table.setBackground(AppColors.CARD_BG);
        table.setSelectionBackground(AppColors.RED_TINT);
        table.setSelectionForeground(AppColors.TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFocusable(true);
        table.getAccessibleContext().setAccessibleName("Bảng dữ liệu");

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, false, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String text = value == null ? "" : value.toString();
                setBackground(selected ? t.getSelectionBackground() : (row % 2 == 0 ? AppColors.CARD_BG : AppColors.ROW_ALT));
                setForeground(AppColors.TEXT_DARK);
                setFont(AppColors.font(Font.PLAIN, 13));
                setHorizontalAlignment(SwingConstants.LEFT);
                if (isStatusColumn(t, column)) {
                    setForeground(AppColors.toneColor(text));
                    setFont(AppColors.font(Font.BOLD, 12));
                } else if (looksNumeric(text)) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return this;
            }
        });

        JTableHeader th = table.getTableHeader();
        th.setFont(AppColors.font(Font.BOLD, 12));
        th.setBackground(new Color(0xF3, 0xED, 0xE5));
        th.setForeground(AppColors.TEXT_DARK);
        th.setPreferredSize(new Dimension(th.getWidth(), 38));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));
        th.setReorderingAllowed(false);

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            String name = String.valueOf(column.getHeaderValue()).toLowerCase();
            if (name.startsWith("mã") || name.equals("stt") || name.equals("hạng")) column.setPreferredWidth(110);
            else if (name.contains("nội dung") || name.contains("tên") || name.contains("sản phẩm")) column.setPreferredWidth(200);
            else if (name.contains("chủ nuôi") || name.contains("địa chỉ") || name.contains("nhân viên")) column.setPreferredWidth(180);
            else if (name.contains("trạng thái")) column.setPreferredWidth(120);
        }
    }

    private static boolean isStatusColumn(JTable table, int column) {
        String name = String.valueOf(table.getColumnName(column)).toLowerCase();
        return name.contains("trạng thái") || name.equals("tiến độ") || name.contains("hạng thẻ");
    }

    private static boolean looksNumeric(String text) {
        if (text.isEmpty() || text.length() > 20) return false;
        String t = text.replace("₫", "").replace(".", "").replace(",", "").replace("%", "").replace("-", "").replace("kg", "").trim();
        return !t.isEmpty() && t.chars().allMatch(Character::isDigit);
    }
}
