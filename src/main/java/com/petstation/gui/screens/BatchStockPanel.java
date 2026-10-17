package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.common.TableStyler;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.RoundedPanel;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.ActionColumn;
import com.petstation.gui.components.common.ExcelExporter;
import com.petstation.gui.components.common.ExcelImporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;

public final class BatchStockPanel extends BasePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> expiryFilterCombo;

    private static final Object[][] DATA = {
        {"SP001", "Pate Whiskas Cá ngừ", "LO-2403", "03/2025", "50", "26", "24"},
        {"SP002", "Hạt Royal Canin Puppy", "LO-2406", "06/2025", "20", "8", "12"},
        {"SP003", "Sữa tắm Beaphar", "LO-2412", "12/2025", "20", "12", "8"},
        {"SP004", "Frontline", "LO-2409", "09/2025", "10", "7", "3"},
        {"SP005", "Royal Canin Adult 2kg", "LO-2401", "01/2025", "30", "5", "25"},
        {"SP006", "Cát vệ sinh 5L", "LO-2402", "02/2025", "100", "20", "80"},
    };

    public BatchStockPanel() {
        super();
        setLayout(new BorderLayout());

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(statCard("Số lô", "6", "", AppColors.BLUE));
        statsRow.add(statCard("Sắp hết hạn (< 1 th)", "2", "", AppColors.ORANGE));
        statsRow.add(statCard("Tổng tồn", "154", "", AppColors.GREEN));
        statsRow.add(statCard("Giá trị tồn", "8.450.000₫", "", new Color(138, 43, 226)));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));
        top.add(statsRow, BorderLayout.NORTH);

        // Filter row with expiry dropdown
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        JLabel filterLabel = new JLabel("Lọc hạn dùng:");
        filterLabel.setFont(AppColors.font(Font.BOLD, 13));
        filterRow.add(filterLabel);
        
        expiryFilterCombo = new JComboBox<>(new String[]{
            "Tất cả", 
            "Đã hết hạn", 
            "Cận hạn < 1 tháng", 
            "Cận hạn < 3 tháng", 
            "Còn hạn > 3 tháng"
        });
        expiryFilterCombo.setPreferredSize(new Dimension(200, 36));
        expiryFilterCombo.setFont(AppColors.font(Font.PLAIN, 13));
        expiryFilterCombo.addActionListener(e -> filterTable());
        filterRow.add(expiryFilterCombo);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new StyledButton("Xuất báo cáo tồn", StyledButton.Variant.SECONDARY));
        filterRow.add(right);
        
        top.add(filterRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã SP", "Tên sản phẩm", "Lô", "Hạn dùng", "Nhập", "Xuất", "Tồn"};
        tableModel = new DefaultTableModel(DATA, cols) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        TableStyler.apply(table);
        table.setAutoCreateRowSorter(true);

        // Expiry renderer with color coding
        table.getColumnModel().getColumn(3).setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(AppColors.font(Font.BOLD, 12));
            
            // Parse date to determine color
            String dateStr = value.toString();
            Color bg, fg;
            if (dateStr.matches("\\d{2}/\\d{4}")) {
                int month = Integer.parseInt(dateStr.substring(0, 2));
                int year = Integer.parseInt(dateStr.substring(3));
                java.time.YearMonth expiry = java.time.YearMonth.of(year, month);
                java.time.YearMonth now = java.time.YearMonth.now();
                
                if (expiry.isBefore(now)) {
                    bg = new Color(248, 215, 218);
                    fg = new Color(114, 28, 36);
                } else if (expiry.minusMonths(1).isBefore(now)) {
                    bg = new Color(255, 235, 150);
                    fg = new Color(133, 100, 4);
                } else if (expiry.minusMonths(3).isBefore(now)) {
                    bg = new Color(255, 243, 205);
                    fg = new Color(133, 100, 4);
                } else {
                    bg = new Color(212, 237, 218);
                    fg = new Color(21, 87, 36);
                }
            } else {
                bg = Color.WHITE;
                fg = AppColors.TEXT_DARK;
            }
            label.setBackground(isSelected ? tbl.getSelectionBackground() : bg);
            label.setForeground(isSelected ? tbl.getSelectionForeground() : fg);
            return label;
        });

        // Action column
        

        JPanel tableCard = new RoundedPanel(12, Color.WHITE, true);
        tableCard.setLayout(new BorderLayout(0, 10));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(sectionTitle("Danh sách lô hàng"), BorderLayout.WEST);
        tableHeader.add(badge("Dữ liệu mẫu", AppColors.WARN_BG, AppColors.BROWN), BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        tableCard.add(scroll, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    private void filterTable() {
        String selected = (String) expiryFilterCombo.getSelectedItem();
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = 
            (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
        if (sorter != null) {
            YearMonth now = YearMonth.now();
            if ("Tất cả".equals(selected)) {
                sorter.setRowFilter(null);
            } else if ("Đã hết hạn".equals(selected)) {
                sorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String dateStr = entry.getStringValue(3);
                        if (dateStr.matches("\\d{2}/\\d{4}")) {
                            int month = Integer.parseInt(dateStr.substring(0, 2));
                            int year = Integer.parseInt(dateStr.substring(3));
                            return YearMonth.of(year, month).isBefore(now);
                        }
                        return false;
                    }
                });
            } else if ("Cận hạn < 1 tháng".equals(selected)) {
                sorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String dateStr = entry.getStringValue(3);
                        if (dateStr.matches("\\d{2}/\\d{4}")) {
                            int month = Integer.parseInt(dateStr.substring(0, 2));
                            int year = Integer.parseInt(dateStr.substring(3));
                            YearMonth expiry = YearMonth.of(year, month);
                            return !expiry.isBefore(now) && expiry.minusMonths(1).isBefore(now);
                        }
                        return false;
                    }
                });
            } else if ("Cận hạn < 3 tháng".equals(selected)) {
                sorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String dateStr = entry.getStringValue(3);
                        if (dateStr.matches("\\d{2}/\\d{4}")) {
                            int month = Integer.parseInt(dateStr.substring(0, 2));
                            int year = Integer.parseInt(dateStr.substring(3));
                            YearMonth expiry = YearMonth.of(year, month);
                            return !expiry.isBefore(now) && expiry.minusMonths(3).isBefore(now) && !expiry.minusMonths(1).isBefore(now);
                        }
                        return false;
                    }
                });
            } else if ("Còn hạn > 3 tháng".equals(selected)) {
                sorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String dateStr = entry.getStringValue(3);
                        if (dateStr.matches("\\d{2}/\\d{4}")) {
                            int month = Integer.parseInt(dateStr.substring(0, 2));
                            int year = Integer.parseInt(dateStr.substring(3));
                            YearMonth expiry = YearMonth.of(year, month);
                            return !expiry.minusMonths(3).isBefore(now);
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void handleDetail(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        String code = tableModel.getValueAt(modelRow, 0).toString();
        String name = tableModel.getValueAt(modelRow, 1).toString();
        String lot = tableModel.getValueAt(modelRow, 2).toString();
        String expiry = tableModel.getValueAt(modelRow, 3).toString();
        String stock = tableModel.getValueAt(modelRow, 6).toString();
        Dialogs.showMessageDialog(this,
            String.format("CHI TIẾT LÔ HÀNG\n\nMã SP: %s\nTên: %s\nLô: %s\nHạn dùng: %s\nTồn: %s\n\n(Mock UI)",
                code, name, lot, expiry, stock), "Chi tiết lô", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleIssue(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        String code = tableModel.getValueAt(modelRow, 0).toString();
        String name = tableModel.getValueAt(modelRow, 1).toString();
        String stock = tableModel.getValueAt(modelRow, 6).toString();
        String input = JOptionPane.showInputDialog(this,
            String.format("Xuất lô cho %s (%s)\nTồn kho: %s\n\nNhập số lượng xuất:", name, code, stock),
            "Xuất lô", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            try {
                int qty = Integer.parseInt(input.trim());
                int current = Integer.parseInt(stock);
                if (qty <= current && qty > 0) {
                    tableModel.setValueAt(String.valueOf(current - qty), modelRow, 6);
                    tableModel.setValueAt(String.valueOf(Integer.parseInt(tableModel.getValueAt(modelRow, 5).toString()) + qty), modelRow, 5);
                    table.repaint();
                    Dialogs.showMessageDialog(this, "Đã xuất " + qty + " sản phẩm! (Mock UI)", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Dialogs.showMessageDialog(this, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                Dialogs.showMessageDialog(this, "Vui lòng nhập số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        String code = tableModel.getValueAt(modelRow, 0).toString();
        String name = tableModel.getValueAt(modelRow, 1).toString();
        Dialogs.showMessageDialog(this,
            String.format("Mở form sửa lô hàng:\n\nMã: %s\nTên: %s\n\n(Mock UI)", code, name),
            "Sửa lô hàng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        String code = tableModel.getValueAt(modelRow, 0).toString();
        String name = tableModel.getValueAt(modelRow, 1).toString();
        int confirm = Dialogs.showConfirmDialog(this,
            String.format("Xóa lô hàng %s (%s)?", name, code),
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(modelRow);
            Dialogs.showMessageDialog(this, "Đã xóa lô hàng! (Mock UI)", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}