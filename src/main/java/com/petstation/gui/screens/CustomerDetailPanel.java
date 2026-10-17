package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.RoundedPanel;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.ActionColumn;
import com.petstation.gui.components.common.ExcelExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class CustomerDetailPanel extends BasePanel {

    private JTabbedPane tabbedPane;

    public CustomerDetailPanel(String code, String customerName, String phone, String address, String tier, String points) {
        super();
        setBorder(null);
        setLayout(new BorderLayout());

        // LEFT PANEL: Customer Info Card
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.setBackground(Color.WHITE);

        // Avatar placeholder
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(avatar);
        leftPanel.add(Box.createVerticalStrut(12));

        // Name
        JLabel nameLabel = new JLabel(customerName);
        nameLabel.setFont(AppColors.font(Font.BOLD, 20));
        nameLabel.setForeground(AppColors.TEXT_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(nameLabel);

        // Tier badge
        JLabel tierBadge = new JLabel("  HẠNG " + tier.toUpperCase() + "  ");
        tierBadge.setFont(AppColors.font(Font.BOLD, 12));
        tierBadge.setOpaque(true);
        tierBadge.setBackground(new Color(255, 248, 220));
        tierBadge.setForeground(new Color(180, 140, 0));
        tierBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        tierBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(tierBadge);
        leftPanel.add(Box.createVerticalStrut(16));

        // Info rows
        addInfoRow(leftPanel, "Mã KH", code);
        addInfoRow(leftPanel, "Điện thoại", phone);
        addInfoRow(leftPanel, "Địa chỉ", address);
        addInfoRow(leftPanel, "Điểm tích lũy", points);
        leftPanel.add(Box.createVerticalStrut(16));

        // Points
        JPanel pointsPanel = new RoundedPanel(8, new Color(237, 224, 255), true);
        pointsPanel.setLayout(new BoxLayout(pointsPanel, BoxLayout.Y_AXIS));
        pointsPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        pointsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pointsPanel.setMaximumSize(new Dimension(340, 80));

        JLabel pointsLabel = new JLabel(points + " ĐIỂM (chỉ xem)");
        pointsLabel.setFont(AppColors.font(Font.BOLD, 14));
        pointsLabel.setForeground(new Color(138, 43, 226));
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pointsPanel.add(pointsLabel);

        JLabel pointsDesc = new JLabel("Điểm tự động cộng/trừ theo hóa đơn, không sửa tay");
        pointsDesc.setFont(AppColors.font(Font.PLAIN, 11));
        pointsDesc.setForeground(new Color(138, 43, 226));
        pointsDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        pointsPanel.add(Box.createVerticalStrut(4));
        pointsPanel.add(pointsDesc);

        leftPanel.add(pointsPanel);
        leftPanel.add(Box.createVerticalStrut(16));

        add(leftPanel, BorderLayout.WEST);

        // RIGHT PANEL: Tabbed content
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(AppColors.font(Font.PLAIN, 13));

        // Tab 1: Thú cưng
        tabbedPane.addTab("🐾 Thú cưng", createPetsTab());
        // Tab 2: Lịch sử mua hàng
        tabbedPane.addTab("🛒 Lịch sử mua hàng", createOrdersTab());
        // Tab 3: Lịch sử Spa
        tabbedPane.addTab("💆 Lịch sử Spa", createSpaTab());
        // Tab 4: Voucher
        tabbedPane.addTab("🎫 Voucher", createVoucherTab());

        // Make tabs scrollable
        JScrollPane rightScroll = new JScrollPane(tabbedPane);
        rightScroll.setBorder(null);
        rightScroll.getViewport().setBackground(Color.WHITE);
        add(rightScroll, BorderLayout.CENTER);
    }

    private void addInfoRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(340, 30));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(AppColors.font(Font.PLAIN, 12));
        lbl.setForeground(AppColors.TEXT_GRAY);
        lbl.setPreferredSize(new Dimension(90, 24));

        JLabel val = new JLabel(value);
        val.setFont(AppColors.font(Font.PLAIN, 12));
        val.setForeground(AppColors.TEXT_DARK);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
    }

    private JPanel createPetsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Danh sách thú cưng");
        title.setFont(AppColors.font(Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Mã TC", "Tên", "Loài", "Giống", "Tuổi", "Ghi chú"};
        Object[][] data = {
            {"#TC001", "Mochi", "Chó", "Poodle", "2 tuổi", "Dị ứng gà"},
            {"#TC002", "Bông", "Mèo", "Persian", "3 tuổi", "—"},
        };

        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        com.petstation.gui.components.common.TableStyler.apply(table);

        ActionColumn.addActionColumn(table, "Hành động", new ActionColumn.ActionButton[]{
            new ActionColumn.ActionButton("", "Xem chi tiết", r -> handlePetView(r)),
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) handlePetView(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Lịch sử mua hàng");
        title.setFont(AppColors.font(Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Mã HD", "Ngày", "Tổng tiền", "Trạng thái", "Nhân viên", "Thanh toán"};
        Object[][] data = {
            {"HD-2409-021", "22/09/2024", "450.000₫", "Hoàn thành", "Nguyễn Văn An", "Tiền mặt"},
            {"HD-2409-018", "15/09/2024", "1.250.000₫", "Hoàn thành", "Trần Thị Bình", "Chuyển khoản"},
            {"HD-2409-012", "01/09/2024", "680.000₫", "Hoàn thành", "Lê Hoàng Cường", "Tiền mặt"},
        };

        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        com.petstation.gui.components.common.TableStyler.apply(table);

        ActionColumn.addActionColumn(table, "Hành động", new ActionColumn.ActionButton[]{
            new ActionColumn.ActionButton("", "Xem chi tiết", r -> handleOrderView(r)),
            new ActionColumn.ActionButton("", "In lại", r -> handleReprint(r)),
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) handleOrderView(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSpaTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Lịch sử dịch vụ Spa");
        title.setFont(AppColors.font(Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Mã phiếu", "Thú cưng", "Dịch vụ", "Ngày", "Thợ", "Trạng thái", "Tổng tiền"};
        Object[][] data = {
            {"SP-010", "Mochi", "Tắm & cắt lông", "20/09/2024", "Hoa", "Hoàn thành", "150.000₫"},
            {"SP-005", "Mochi", "Cắt móng + vệ sinh tai", "10/08/2024", "Lan", "Hoàn thành", "80.000₫"},
            {"SP-001", "Bông", "Tắm & sấy", "05/07/2024", "Nam", "Hoàn thành", "120.000₫"},
        };

        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        com.petstation.gui.components.common.TableStyler.apply(table);

        ActionColumn.addActionColumn(table, "Hành động", new ActionColumn.ActionButton[]{
            new ActionColumn.ActionButton("", "Xem phiếu", r -> handleSpaView(r)),
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createVoucherTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Voucher & Điểm thưởng");
        title.setFont(AppColors.font(Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Mã Voucher", "Giá trị", "Loại", "Hạn dùng", "Trạng thái"};
        Object[][] data = {
            {"BICH50K", "50.000₫", "Giảm tiền", "30/09/2024", "Chưa dùng"},
            {"KHACH10", "10%", "Giảm %", "31/10/2024", "Chưa dùng"},
        };

        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        com.petstation.gui.components.common.TableStyler.apply(table);



        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void handleEdit() {
        Dialogs.showMessageDialog(this, "Mở form cập nhật hồ sơ khách hàng (Mock UI)", "Cập nhật", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handlePetView(int row) {
        Dialogs.showMessageDialog(this, "Xem chi tiết thú cưng (Mock UI)", "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handlePetService(int row) {
        tabbedPane.setSelectedIndex(2);
    }

    private void handlePetMedical(int row) {
        Dialogs.showMessageDialog(this, "Xem bệnh án thú cưng (Mock UI)", "Bệnh án", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOrderView(int row) {
        Dialogs.showMessageDialog(this, "Xem chi tiết hóa đơn - Mở màn hình chi tiết hóa đơn (Mock UI)", "Chi tiết HD", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleReprint(int row) {
        Dialogs.showMessageDialog(this, "Đã gửi lệnh in lại hóa đơn! (Mock UI)", "In lại", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSpaView(int row) {
        Dialogs.showMessageDialog(this, "Xem chi tiết phiếu Spa (Mock UI)", "Chi tiết Spa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleResendVoucher(int row) {
        Dialogs.showMessageDialog(this, "Đã gửi voucher qua SMS/Email! (Mock UI)", "Gửi lại", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCancelVoucher(int row) {
        Dialogs.showMessageDialog(this, "Đã hủy voucher! (Mock UI)", "Hủy voucher", JOptionPane.INFORMATION_MESSAGE);
    }
}