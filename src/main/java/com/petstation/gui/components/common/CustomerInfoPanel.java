package com.petstation.gui.components.common;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel hiển thị thông tin khách hàng trong POS
 * Auto-fill từ số điện thoại
 */
public class CustomerInfoPanel extends JPanel {
    
    private JTextField phoneField;
    private JPanel infoPanel;
    private JLabel customerNameLabel;
    private JLabel tierLabel;
    private JLabel pointsLabel;
    private JButton usePointsButton;
    private JButton clearButton;
    
    private Customer currentCustomer;
    private CustomerSearchCallback searchCallback;
    
    public CustomerInfoPanel() {
        setLayout(new BorderLayout(8, 8));
        setOpaque(false);
        
        // Phone input panel
        JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        phonePanel.setOpaque(false);
        
        JLabel phoneLabel = new JLabel("SĐT Khách hàng:");
        phoneLabel.setFont(AppColors.font(Font.PLAIN, 13));
        phonePanel.add(phoneLabel);
        
        phoneField = new JTextField(15);
        phoneField.setFont(AppColors.font(Font.PLAIN, 13));
        phoneField.setToolTipText("Nhập SĐT và Enter để tìm khách hàng");
        phoneField.addActionListener(e -> searchCustomer());
        phonePanel.add(phoneField);
        
        JButton searchButton = new JButton("Tìm");
        searchButton.setFocusPainted(false);
        searchButton.setToolTipText("Tìm khách hàng");
        searchButton.addActionListener(e -> searchCustomer());
        phonePanel.add(searchButton);
        
        add(phonePanel, BorderLayout.NORTH);
        
        // Customer info panel (hidden initially)
        infoPanel = createInfoPanel();
        infoPanel.setVisible(false);
        add(infoPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInfoPanel() {
        RoundedPanel panel = new RoundedPanel(8, new Color(240, 248, 255), true);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameTitle = new JLabel("Tên:");
        nameTitle.setFont(AppColors.font(Font.PLAIN, 12));
        nameTitle.setForeground(AppColors.TEXT_GRAY);
        panel.add(nameTitle, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        customerNameLabel = new JLabel();
        customerNameLabel.setFont(AppColors.font(Font.BOLD, 14));
        panel.add(customerNameLabel, gbc);
        
        // Row 1: Tier
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel tierTitle = new JLabel("Hạng:");
        tierTitle.setFont(AppColors.font(Font.PLAIN, 12));
        tierTitle.setForeground(AppColors.TEXT_GRAY);
        panel.add(tierTitle, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tierLabel = new JLabel();
        tierLabel.setFont(AppColors.font(Font.BOLD, 13));
        panel.add(tierLabel, gbc);
        
        // Row 2: Points
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel pointsTitle = new JLabel("Điểm:");
        pointsTitle.setFont(AppColors.font(Font.PLAIN, 12));
        pointsTitle.setForeground(AppColors.TEXT_GRAY);
        panel.add(pointsTitle, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pointsLabel = new JLabel();
        pointsLabel.setFont(AppColors.font(Font.BOLD, 13));
        pointsLabel.setForeground(new Color(34, 139, 34));
        panel.add(pointsLabel, gbc);
        
        // Row 3: Actions
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 4, 4, 4);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionsPanel.setOpaque(false);
        
        usePointsButton = new JButton("Dùng điểm");
        usePointsButton.setFont(AppColors.font(Font.PLAIN, 12));
        usePointsButton.setFocusPainted(false);
        actionsPanel.add(usePointsButton);
        
        clearButton = new JButton("Xóa");
        clearButton.setFont(AppColors.font(Font.PLAIN, 12));
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearCustomer());
        actionsPanel.add(clearButton);
        
        panel.add(actionsPanel, gbc);
        
        return panel;
    }
    
    private void searchCustomer() {
        String phone = phoneField.getText().trim();
        
        if (phone.isEmpty()) {
            Dialogs.showMessageDialog(this,
                "Vui lòng nhập số điện thoại!",
                "Thiếu thông tin",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate phone format
        if (!phone.matches("^0\\d{9,10}$")) {
            Dialogs.showMessageDialog(this,
                "Số điện thoại không đúng định dạng!\nVui lòng nhập 10-11 số, bắt đầu bằng 0.",
                "Lỗi định dạng",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Call search callback
        if (searchCallback != null) {
            Customer customer = searchCallback.searchByPhone(phone);
            
            if (customer != null) {
                setCustomer(customer);
            } else {
                int result = Dialogs.showConfirmDialog(this,
                    "Không tìm thấy khách hàng với SĐT: "+ phone + "\n\n"+
                    "Bạn có muốn thêm khách hàng mới không?",
                    "Khách hàng chưa tồn tại",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION) {
                    // TODO: Open add customer dialog
                    Dialogs.showMessageDialog(this,
                        "Chức năng thêm khách hàng nhanh sẽ được triển khai.\n"+
                        "Hiện tại vui lòng vào menu Danh mục > Khách hàng để thêm.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        
        if (customer != null) {
            customerNameLabel.setText(customer.getName());
            
            // Set tier with icon
            String tierIcon = getTierIcon(customer.getTier());
            tierLabel.setText(tierIcon + ""+ customer.getTier());
            tierLabel.setForeground(getTierColor(customer.getTier()));
            
            pointsLabel.setText(String.format(java.util.Locale.forLanguageTag("vi-VN"), "%,d điểm", customer.getPoints()));
            
            // Enable/disable use points button
            usePointsButton.setEnabled(customer.getPoints() > 0);
            
            infoPanel.setVisible(true);
        } else {
            infoPanel.setVisible(false);
        }
    }
    
    public void clearCustomer() {
        phoneField.setText("");
        currentCustomer = null;
        infoPanel.setVisible(false);
    }
    
    private String getTierIcon(String tier) {
        switch (tier.toUpperCase()) {
            case "VIP": return "";
            case "VÀNG": return "";
            case "BẠC": return "";
            case "ĐỒNG": return "";
            default: return "";
        }
    }
    
    private Color getTierColor(String tier) {
        switch (tier.toUpperCase()) {
            case "VIP": return new Color(138, 43, 226); // Purple
            case "VÀNG": return new Color(255, 165, 0); // Gold
            case "BẠC": return new Color(192, 192, 192); // Silver
            case "ĐỒNG": return new Color(205, 127, 50); // Bronze
            default: return AppColors.TEXT_DARK;
        }
    }
    
    // Getters
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
    
    public String getPhone() {
        return phoneField.getText().trim();
    }
    
    public JButton getUsePointsButton() {
        return usePointsButton;
    }
    
    // Setters
    public void setSearchCallback(CustomerSearchCallback callback) {
        this.searchCallback = callback;
    }
    
    public void setPhone(String phone) {
        phoneField.setText(phone);
    }
    
    public void focusPhone() {
        phoneField.requestFocus();
        phoneField.selectAll();
    }
    
    // Inner classes
    public interface CustomerSearchCallback {
        Customer searchByPhone(String phone);
    }
    
    public static class Customer {
        private String id;
        private String name;
        private String phone;
        private String tier;
        private int points;
        
        public Customer(String id, String name, String phone, String tier, int points) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.tier = tier;
            this.points = points;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getTier() { return tier; }
        public int getPoints() { return points; }
    }
}
