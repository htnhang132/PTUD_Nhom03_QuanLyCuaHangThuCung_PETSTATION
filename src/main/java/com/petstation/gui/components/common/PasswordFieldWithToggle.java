package com.petstation.gui.components.common;

import java.awt.*;

import javax.swing.*;

/** JPasswordField với nút toggle hiển thị/ẩn mật khẩu Icon con mắt ở cuối ô nhập */
public class PasswordFieldWithToggle extends JPanel {
    private JPasswordField passwordField;
    private JToggleButton toggleButton;
    private boolean passwordVisible = false;

    public PasswordFieldWithToggle() {
        this(20);
    }

    public PasswordFieldWithToggle(int columns) {
        setLayout(new BorderLayout(5, 0));

        passwordField = new JPasswordField(columns);

        // Toggle button với icon con mắt
        toggleButton = new JToggleButton("Hiện");
        toggleButton.setPreferredSize(new Dimension(64, 28));
        toggleButton.setFocusPainted(false);
        toggleButton.setToolTipText("Hiển thị/Ẩn mật khẩu");

        toggleButton.addActionListener(e -> togglePasswordVisibility());

        add(passwordField, BorderLayout.CENTER);
        add(toggleButton, BorderLayout.EAST);

        // Đặt border cho panel thay vì các component bên trong
        setBorder(BorderFactory.createEmptyBorder());
        passwordField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(3, 5, 3, 5)));
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            passwordField.setEchoChar((char) 0); // Hiển thị text thật
            toggleButton.setText("Ẩn");
            toggleButton.setSelected(true);
        } else {
            passwordField.setEchoChar('●'); // Ẩn với ký tự ●
            toggleButton.setText("Hiện");
            toggleButton.setSelected(false);
        }
    }

    // Delegate methods to passwordField
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public String getPasswordText() {
        return new String(passwordField.getPassword());
    }

    public void setPassword(String password) {
        passwordField.setText(password);
    }

    public void clear() {
        passwordField.setText("");
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        toggleButton.setEnabled(enabled);
    }

    public void setColumns(int columns) {
        passwordField.setColumns(columns);
    }

    public void setEchoChar(char c) {
        if (!passwordVisible) {
            passwordField.setEchoChar(c);
        }
    }

    public void addActionListener(java.awt.event.ActionListener listener) {
        passwordField.addActionListener(listener);
    }

    @Override
    public void requestFocus() {
        passwordField.requestFocus();
    }
}
