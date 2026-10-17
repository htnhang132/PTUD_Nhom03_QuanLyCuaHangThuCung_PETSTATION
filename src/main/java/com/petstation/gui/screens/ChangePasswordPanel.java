package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.components.ToolbarCapabilities;
import com.petstation.gui.components.common.FormValidator;
import com.petstation.gui.components.common.PasswordFieldWithToggle;

public final class ChangePasswordPanel extends BasePanel implements ToolbarAware {
    private PasswordFieldWithToggle currentPasswordField;
    private PasswordFieldWithToggle newPasswordField;
    private PasswordFieldWithToggle confirmPasswordField;
    private FormValidator validator;
    
    public ChangePasswordPanel() {
        setLayout(new BorderLayout());
        
        // Content panel
        JPanel content = card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); 
        c.insets = new Insets(8,10,8,10); 
        c.fill = GridBagConstraints.HORIZONTAL; 
        c.weightx = 1;
        
        // Title và hint
        JLabel title = sectionTitle("Đổi mật khẩu"); 
        title.setFont(AppColors.font(Font.BOLD,21));
        JLabel hint = new JLabel("Cập nhật mật khẩu đăng nhập của tài khoản hiện tại"); 
        hint.setForeground(AppColors.TEXT_GRAY);
        
        c.gridx=0; c.gridy=0; c.gridwidth=2;
        content.add(title, c);
        c.gridy++;
        content.add(hint, c);
        
        // Password fields với toggle visibility
        currentPasswordField = createPasswordField();
        newPasswordField = createPasswordField();
        confirmPasswordField = createPasswordField();
        
        addRow(content, c, 2, "Mật khẩu hiện tại", currentPasswordField);
        addRow(content, c, 3, "Mật khẩu mới", newPasswordField);
        addRow(content, c, 4, "Xác nhận mật khẩu", confirmPasswordField);
        
        // Password strength indicator
        JLabel strengthLabel = new JLabel("");
        strengthLabel.setFont(AppColors.font(Font.PLAIN, 11));
        c.gridy = 5;
        c.gridx = 1;
        c.gridwidth = 1;
        content.add(strengthLabel, c);
        
        // Thêm listener để hiển thị độ mạnh mật khẩu
        newPasswordField.getPasswordField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            
            private void updateStrength() {
                String password = newPasswordField.getPasswordText();
                if (password.isEmpty()) {
                    strengthLabel.setText("");
                    return;
                }
                
                int strength = calculatePasswordStrength(password);
                if (strength < 3) {
                    strengthLabel.setText("Mật khẩu yếu");
                    strengthLabel.setForeground(AppColors.DANGER);
                } else if (strength < 4) {
                    strengthLabel.setText("Mật khẩu trung bình");
                    strengthLabel.setForeground(AppColors.WARNING);
                } else {
                    strengthLabel.setText("Mật khẩu mạnh");
                    strengthLabel.setForeground(AppColors.SUCCESS);
                }
            }
        });
        
        // Rules
        JLabel rule = new JLabel("Mật khẩu tối thiểu 8 ký tự; nên có chữ hoa, chữ thường, số và ký tự đặc biệt.");
        rule.setForeground(AppColors.TEXT_GRAY);
        rule.setFont(AppColors.font(Font.PLAIN,11));
        c.gridy = 6;
        c.gridx = 0;
        c.gridwidth = 2;
        content.add(rule, c);
        
        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        StyledButton cancel = new StyledButton("Hủy", StyledButton.Variant.GHOST);
        StyledButton save = new StyledButton("Lưu mật khẩu", StyledButton.Variant.PRIMARY);
        actions.add(cancel);
        actions.add(save);
        c.gridy = 7;
        content.add(actions, c);
        
        // Event handlers
        cancel.addActionListener(e -> {
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            strengthLabel.setText("");
        });
        
        save.addActionListener(e -> handleSave());
        
        // Keyboard shortcuts
        registerKeyboardShortcut("F4", e -> handleSave());
        registerKeyboardShortcut("ESCAPE", e -> cancel.doClick());
        
        add(content, BorderLayout.CENTER);
    }
    
    private PasswordFieldWithToggle createPasswordField() {
        PasswordFieldWithToggle field = new PasswordFieldWithToggle(20);
        field.setPreferredSize(new Dimension(350, 36));
        return field;
    }
    
    private void handleSave() {
        String current = currentPasswordField.getPasswordText();
        String newPass = newPasswordField.getPasswordText();
        String confirm = confirmPasswordField.getPasswordText();
        
        // Validate empty
        if (current.isEmpty()) {
            message("Vui lòng nhập mật khẩu hiện tại.", "Dữ liệu chưa đầy đủ", JOptionPane.WARNING_MESSAGE);
            currentPasswordField.requestFocus();
            return;
        }
        
        if (newPass.isEmpty()) {
            message("Vui lòng nhập mật khẩu mới.", "Dữ liệu chưa đầy đủ", JOptionPane.WARNING_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }
        
        // Validate length
        if (newPass.length() < 8) {
            message("Mật khẩu mới phải có ít nhất 8 ký tự.", "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }
        
        // Validate confirmation
        if (!newPass.equals(confirm)) {
            message("Xác nhận mật khẩu không khớp.", "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }
        
        // Validate not same as current
        if (newPass.equals(current)) {
            message("Mật khẩu mới phải khác mật khẩu hiện tại.", "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }
        
        // TODO: Gọi service để đổi mật khẩu
        message("Đã kiểm tra biểu mẫu. Chưa nối ChangePasswordService nên chưa cập nhật database.", 
                "Mock UI", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear form after success
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    
    private int calculatePasswordStrength(String password) {
        int strength = 0;
        
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        if (password.matches(".*[a-z].*")) strength++; // Has lowercase
        if (password.matches(".*[A-Z].*")) strength++; // Has uppercase
        if (password.matches(".*\\d.*")) strength++;   // Has digit
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength++; // Has special char
        
        return Math.min(strength, 5);
    }
    
    private void navigateBack() {
        // TODO: Implement navigation
        message("Chức năng quay lại sẽ được triển khai khi có navigation system", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void navigateToHome() {
        // TODO: Implement navigation to home
        message("Chức năng về màn hình chính sẽ được triển khai khi có navigation system", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void registerKeyboardShortcut(String key, java.awt.event.ActionListener action) {
        javax.swing.KeyStroke keyStroke = javax.swing.KeyStroke.getKeyStroke(key);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
        getActionMap().put(key, new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                action.actionPerformed(e);
            }
        });
    }
    
    private void addRow(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = y;
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppColors.font(Font.PLAIN, 13));
        p.add(lbl, c);
        c.gridx = 1;
        p.add(field, c);
    }
    
    private void message(String text, String title, int type) {
        Dialogs.showMessageDialog(this, text, title, type);
    }
    
    @Override 
    public ToolbarCapabilities toolbarCapabilities() {
        return new ToolbarCapabilities(false, true, false, false, false, true);
    }
    
    @Override 
    public void onHelp() {
        message("Hướng dẫn đổi mật khẩu:\n\n"+
                "1. Nhập mật khẩu hiện tại\n"+
                "2. Nhập mật khẩu mới (tối thiểu 8 ký tự)\n"+
                "3. Xác nhận lại mật khẩu mới\n"+
                "4. Nhấn 'Lưu mật khẩu' hoặc phím F4\n\n"+
                "Mẹo: Nhấn nút Hiện/Ẩn để xem hoặc che mật khẩu",
                "Trợ giúp", JOptionPane.INFORMATION_MESSAGE);
    }
}
