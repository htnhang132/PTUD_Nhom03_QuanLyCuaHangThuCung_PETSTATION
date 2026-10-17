package com.petstation.gui.components.common;

import com.petstation.gui.components.Dialogs;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manager cho tính năng "Treo đơn"(Hold Cart) trong POS
 * Cho phép lưu giỏ hàng tạm thời để phục vụ khách khác
 */
public class HoldCartManager {
    
    private List<HeldCart> heldCarts = new ArrayList<>();
    private int nextId = 1;
    
    /**
     * Treo giỏ hàng hiện tại
     */
    public HeldCart holdCart(List<?> items, String customerInfo) {
        HeldCart cart = new HeldCart(
            nextId++,
            new ArrayList<>(items),
            customerInfo,
            System.currentTimeMillis()
        );
        heldCarts.add(cart);
        return cart;
    }
    
    /**
     * Lấy giỏ hàng đang treo
     */
    public HeldCart resumeCart(int index) {
        if (index >= 0 && index < heldCarts.size()) {
            return heldCarts.remove(index);
        }
        return null;
    }
    
    /**
     * Xóa giỏ hàng đang treo
     */
    public void removeCart(int index) {
        if (index >= 0 && index < heldCarts.size()) {
            heldCarts.remove(index);
        }
    }
    
    /**
     * Hiển thị dialog chọn đơn treo
     */
    public HeldCart showSelectDialog(Component parent) {
        if (heldCarts.isEmpty()) {
            Dialogs.showMessageDialog(parent,
                "Không có đơn nào đang treo.",
                "Danh sách trống",
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent),
            "Chọn đơn cần mở lại", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // List of held carts
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < heldCarts.size(); i++) {
            HeldCart cart = heldCarts.get(i);
            listModel.addElement(String.format(
                "#%d - %d SP - %s - %s",
                cart.getId(),
                cart.getItems().size(),
                cart.getCustomerInfo().isEmpty() ? "Khách lẻ": cart.getCustomerInfo(),
                formatTime(cart.getTimestamp())
            ));
        }
        
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setFixedCellHeight(40);
        
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        
        HeldCart[] result = {null};
        
        JButton resumeButton = new JButton("Mở lại");
        resumeButton.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                result[0] = resumeCart(index);
                dialog.dispose();
            } else {
                Dialogs.showMessageDialog(dialog,
                    "Vui lòng chọn đơn cần mở!",
                    "Chưa chọn",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(resumeButton);
        
        JButton deleteButton = new JButton("Xóa");
        deleteButton.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                int confirm = Dialogs.showConfirmDialog(dialog,
                    "Xác nhận xóa đơn này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    removeCart(index);
                    listModel.remove(index);
                    if (listModel.isEmpty()) {
                        dialog.dispose();
                    }
                }
            } else {
                Dialogs.showMessageDialog(dialog,
                    "Vui lòng chọn đơn cần xóa!",
                    "Chưa chọn",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(deleteButton);
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Double-click to resume
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    resumeButton.doClick();
                }
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        
        if (minutes < 1) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + "phút trước";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(new Date(timestamp));
        }
    }
    
    public int getHeldCount() {
        return heldCarts.size();
    }
    
    public boolean hasHeldCarts() {
        return !heldCarts.isEmpty();
    }
    
    /**
     * Held Cart data class
     */
    public static class HeldCart {
        private int id;
        private List<?> items;
        private String customerInfo;
        private long timestamp;
        
        public HeldCart(int id, List<?> items, String customerInfo, long timestamp) {
            this.id = id;
            this.items = items;
            this.customerInfo = customerInfo;
            this.timestamp = timestamp;
        }
        
        public int getId() { return id; }
        public List<?> getItems() { return items; }
        public String getCustomerInfo() { return customerInfo; }
        public long getTimestamp() { return timestamp; }
    }
}
