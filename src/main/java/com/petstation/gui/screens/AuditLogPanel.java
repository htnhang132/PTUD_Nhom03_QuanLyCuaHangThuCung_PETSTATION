package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.common.TableStyler;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.navigation.AuditTrail;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.DateRangePicker;
import com.petstation.gui.components.common.ExcelExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;

/**
 * Panel Nhật ký hệ thống (Audit Log)
 * Với Date Range Picker và Event Type Filter - BẮT BUỘC cho database lớn
 */
public final class AuditLogPanel extends BasePanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private DateRangePicker dateRangePicker;
    private JComboBox<String> eventTypeCombo;
    private JComboBox<String> resultCombo;
    private JTextField userSearchField;
    private JLabel statsLabel;

    public AuditLogPanel() {
        setLayout(new BorderLayout());

        // Main content
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header with title and stats
        JPanel header = createHeader();
        content.add(header, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = createFilterPanel();
        content.add(filterPanel, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = createTablePanel();
        content.add(tablePanel, BorderLayout.CENTER);

        // Footer with note
        JPanel footer = createFooter();
        content.add(footer, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);

        reload();
        AuditTrail.addListener(() -> SwingUtilities.invokeLater(this::reload));
    }

    private boolean initializing;

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 8));
        header.setOpaque(false);

        // Title and stats
        JPanel leftSection = new JPanel();
        leftSection.setOpaque(false);
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));

        JLabel title = sectionTitle("Nhật ký hệ thống");
        title.setFont(AppColors.font(Font.BOLD, 21));
        leftSection.add(title);
        leftSection.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Tra cứu lịch sử thao tác và sự kiện bảo mật");
        subtitle.setFont(AppColors.font(Font.PLAIN, 13));
        subtitle.setForeground(AppColors.TEXT_GRAY);
        leftSection.add(subtitle);
        leftSection.add(Box.createVerticalStrut(5));

        statsLabel = new JLabel("Đang hiển thị: 0 sự kiện");
        statsLabel.setFont(AppColors.font(Font.PLAIN, 12));
        statsLabel.setForeground(AppColors.TEXT_GRAY);
        leftSection.add(statsLabel);

        header.add(leftSection, BorderLayout.WEST);

        // Export button
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSection.setOpaque(false);

        StyledButton exportBtn = new StyledButton("Xuất CSV", StyledButton.Variant.GHOST);
        exportBtn.setToolTipText("Xuất kết quả tìm kiếm ra file CSV");
        exportBtn.addActionListener(e -> handleExport());
        rightSection.add(exportBtn);

        header.add(rightSection, BorderLayout.EAST);

        return header;
    }

    private JPanel createFilterPanel() {
        JPanel panel = card(new BorderLayout(0, 10));

        JLabel filterTitle = new JLabel("Bộ lọc tra cứu");
        filterTitle.setFont(AppColors.font(Font.BOLD, 14));
        panel.add(filterTitle, BorderLayout.NORTH);

        JPanel filterContent = new JPanel(new GridBagLayout());
        filterContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Date Range Picker
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;

        dateRangePicker = new DateRangePicker();
        filterContent.add(dateRangePicker, gbc);

        // Row 2: Event Type, Result, User Search
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;

        // Event Type Filter
        JPanel eventTypePanel = new JPanel(new BorderLayout(5, 0));
        eventTypePanel.setOpaque(false);
        eventTypePanel.add(new JLabel("Loại sự kiện:"), BorderLayout.WEST);

        String[] eventTypes = {
            "Tất cả",
            "LOGIN - Đăng nhập / Đăng xuất",
            "CREATE - Thêm mới",
            "UPDATE - Sửa",
            "DELETE - Xóa / Ngừng hoạt động",
            "APPROVAL - Phê duyệt",
            "INVENTORY - Kho",
            "PAYMENT - Thanh toán / Đổi trả",
            "SYSTEM - Hệ thống"
        };
        eventTypeCombo = new JComboBox<>(eventTypes);
        eventTypeCombo.setFont(AppColors.font(Font.PLAIN, 12));
        eventTypePanel.add(eventTypeCombo, BorderLayout.CENTER);

        gbc.gridx = 0;
        filterContent.add(eventTypePanel, gbc);

        // Result Filter
        JPanel resultPanel = new JPanel(new BorderLayout(5, 0));
        resultPanel.setOpaque(false);
        resultPanel.add(new JLabel("Kết quả:"), BorderLayout.WEST);

        String[] results = {"Tất cả", "Thành công", "Thất bại", "Cảnh báo"};
        resultCombo = new JComboBox<>(results);
        resultCombo.setFont(AppColors.font(Font.PLAIN, 12));
        resultPanel.add(resultCombo, BorderLayout.CENTER);

        gbc.gridx = 1;
        filterContent.add(resultPanel, gbc);

        // User Search
        JPanel userPanel = new JPanel(new BorderLayout(5, 0));
        userPanel.setOpaque(false);
        userPanel.add(new JLabel("Người dùng:"), BorderLayout.WEST);

        userSearchField = new JTextField();
        userSearchField.setFont(AppColors.font(Font.PLAIN, 12));
        userSearchField.setToolTipText("Tìm theo tên người dùng");
        userPanel.add(userSearchField, BorderLayout.CENTER);

        gbc.gridx = 2;
        filterContent.add(userPanel, gbc);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);

        StyledButton clearBtn = new StyledButton("Xóa bộ lọc", StyledButton.Variant.GHOST);
        clearBtn.addActionListener(e -> clearFilters());
        actionPanel.add(clearBtn);

        StyledButton applyBtn = new StyledButton("Áp dụng", StyledButton.Variant.PRIMARY);
        applyBtn.addActionListener(e -> applyFilters());
        actionPanel.add(applyBtn);

        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        filterContent.add(actionPanel, gbc);

        panel.add(filterContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = card(new BorderLayout(0, 10));

        // Table header
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(sectionTitle("Kết quả tra cứu"), BorderLayout.WEST);

        JLabel hint = new JLabel("Click vào dòng để xem chi tiết");
        hint.setFont(AppColors.font(Font.PLAIN, 11));
        hint.setForeground(AppColors.TEXT_GRAY);
        tableHeader.add(hint, BorderLayout.EAST);

        panel.add(tableHeader, BorderLayout.NORTH);

        // Create table
        String[] cols = {"Thời gian", "Người dùng", "Hành động", "Loại sự kiện", "Kết quả", "IP Address"};
        Object[][] data = new Object[0][];

        tableModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        table = new JTable(tableModel);
        TableStyler.apply(table);

        // Setup sorter
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Custom renderer cho cột Kết quả
        table.getColumnModel().getColumn(4).setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(AppColors.font(Font.PLAIN, 11));

            if (value.equals("Thành công")) {
                label.setBackground(new Color(212, 237, 218));
                label.setForeground(new Color(21, 87, 36));
            } else if (value.equals("Thất bại")) {
                label.setBackground(new Color(248, 215, 218));
                label.setForeground(new Color(114, 28, 36));
            } else if (value.equals("Cảnh báo")) {
                label.setBackground(new Color(255, 243, 205));
                label.setForeground(new Color(133, 100, 4));
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }

            return label;
        });

        // Custom renderer cho cột Loại sự kiện
        table.getColumnModel().getColumn(3).setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setFont(AppColors.font(Font.BOLD, 11));

            String str = value.toString();
            if (str.contains("WARNING") || str.contains("ERROR") || str.contains("DELETE")) {
                label.setForeground(AppColors.DANGER_TEXT);
            } else if (str.contains("LOGIN") || str.contains("LOGOUT")) {
                label.setForeground(AppColors.PRIMARY);
            } else {
                label.setForeground(AppColors.TEXT_DARK);
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(Color.WHITE);
            }

            return label;
        });

        // Add double-click listener
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        handleViewDetail(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(AppColors.WARN_BG);
        footer.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel note = new JLabel("Lưu ý: Mock UI nhật ký; dữ liệu thật sẽ lấy từ AuditLogService. Audit log thực tế có thể có hàng triệu bản ghi - bắt buộc phải dùng Date Range Filter.");
        note.setFont(AppColors.font(Font.PLAIN, 12));
        note.setForeground(AppColors.BROWN);
        footer.add(note, BorderLayout.WEST);

        return footer;
    }

    // === EVENT HANDLERS ===

    private void applyFilters() {
        reload();
    }

    /** Nạp lại bảng từ nhật ký thật của phiên theo bộ lọc hiện tại (mới nhất ở trên). */
    private void reload() {
        LocalDate from = dateRangePicker.getFromDate();
        LocalDate to = dateRangePicker.getToDate();
        String type = String.valueOf(eventTypeCombo.getSelectedItem());
        String typeKey = type.startsWith("Tất cả") ? "" : type.split(" ")[0];
        String result = String.valueOf(resultCombo.getSelectedItem());
        String user = userSearchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        java.util.List<AuditTrail.Entry> entries = new java.util.ArrayList<>(AuditTrail.entries());
        java.util.Collections.reverse(entries);
        int warnings = 0;
        for (AuditTrail.Entry e : entries) {
            LocalDate d = e.time().toLocalDate();
            if (from != null && d.isBefore(from)) continue;
            if (to != null && d.isAfter(to)) continue;
            if (!typeKey.isEmpty() && !typeKey.equals(e.type())) continue;
            if (!result.equals("Tất cả") && !result.equals(e.result())) continue;
            if (!user.isEmpty() && !e.user().toLowerCase().contains(user)) continue;
            if ("Cảnh báo".equals(e.result()) || "Thất bại".equals(e.result())) warnings++;
            tableModel.addRow(new Object[]{e.timeText(), e.user(), e.action() + (e.object().isBlank() ? "" : ": " + e.object()),
                e.type(), e.result(), "Máy hiện tại"});
        }
        statsLabel.setText("Đang hiển thị: " + tableModel.getRowCount() + " sự kiện • " + warnings + " cảnh báo/thất bại");
    }

    private void clearFilters() {
        dateRangePicker.reset();
        eventTypeCombo.setSelectedIndex(0);
        resultCombo.setSelectedIndex(0);
        userSearchField.setText("");
        reload();
    }

    private void handleViewDetail(int row) {
        int modelRow = table.convertRowIndexToModel(row);

        String time = tableModel.getValueAt(modelRow, 0).toString();
        String user = tableModel.getValueAt(modelRow, 1).toString();
        String action = tableModel.getValueAt(modelRow, 2).toString();
        String eventType = tableModel.getValueAt(modelRow, 3).toString();
        String result = tableModel.getValueAt(modelRow, 4).toString();
        String ip = tableModel.getValueAt(modelRow, 5).toString();

        String detail = String.format(
            "CHI TIẾT SỰ KIỆN\n\n"+
            "═══════════════════════════\n"+
            "Thời gian: %s\n"+
            "Người dùng: %s\n"+
            "Hành động: %s\n"+
            "Loại: %s\n"+
            "Kết quả: %s\n"+
            "IP Address: %s\n"+
            "═══════════════════════════\n\n"+
            "Nhật ký chỉ đọc, không thể sửa hoặc xóa.",
            time, user, action, eventType, result, ip
        );

        Dialogs.showMessageDialog(this,
            detail,
            "Chi tiết Audit Log",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleExport() {
        ExcelExporter.exportTable(this, table, "Audit_Log");
    }

    private void navigateBack() {
        Dialogs.showMessageDialog(this,
            "Chức năng quay lại sẽ được triển khai khi có navigation system",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void navigateToHome() {
        Dialogs.showMessageDialog(this,
            "Chức năng về màn hình chính sẽ được triển khai khi có navigation system",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected String helpTitle() {
        return "Nhật ký hệ thống";
    }

    @Override
    protected String helpText() {
        return "Hướng dẫn tra cứu Audit Log:\n\n"+
               "【 Bộ lọc 】\n"+
               "• Date Range: BẮT BUỘC phải chọn khoảng thời gian\n"+
               "→ Database có hàng triệu bản ghi, không thể load hết\n"+
               "• Loại sự kiện: Lọc theo LOGIN, PAYMENT, DELETE...\n"+
               "• Kết quả: Lọc Thành công/Thất bại/Cảnh báo\n"+
               "• Người dùng: Tìm theo tên\n\n"+
               "【 Tra soát gian lận 】\n"+
               "Các loại sự kiện quan trọng cần kiểm tra:\n"+
               "• DELETE: Xóa hóa đơn, xóa dữ liệu\n"+
               "• WARNING/ERROR: Cảnh báo bất thường\n"+
               "• LOGIN thất bại nhiều lần: Nghi vấn tấn công\n\n"+
               "【 Export 】\n"+
               "Xuất kết quả ra CSV để phân tích chi tiết\n\n"+
               "Mẹo: Double-click vào dòng để xem chi tiết đầy đủ.";
    }
}
