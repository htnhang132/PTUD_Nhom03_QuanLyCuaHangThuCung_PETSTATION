package com.petstation.gui.components.common;

import com.petstation.gui.components.Dialogs;
import javax.swing.*;
import java.awt.*;

/**
 * Toolbar tối giản xuất hiện ở tất cả các panels
 * [ < Quay lại ] [  Màn hình chính ] [ ? Trợ giúp ]
 */
public class UniversalToolbar extends JPanel {
    private JButton backButton;
    private JButton homeButton;
    private JButton helpButton;

    public UniversalToolbar() {
        this(true, true, true);
    }

    public UniversalToolbar(boolean showBack, boolean showHome, boolean showHelp) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        if (showBack) {
            backButton = new JButton("← Quay lại");
            backButton.setFocusPainted(false);
            backButton.setToolTipText("Quay lại màn hình trước");
            add(backButton);
        }

        if (showHome) {
            homeButton = new JButton("Màn hình chính");
            homeButton.setFocusPainted(false);
            homeButton.setToolTipText("Về trang chủ");
            add(homeButton);
        }

        if (showHelp) {
            helpButton = new JButton("? Trợ giúp");
            helpButton.setFocusPainted(false);
            helpButton.setToolTipText("Xem hướng dẫn");
            add(helpButton);
        }
    }

    // Getters cho các nút để panels có thể gắn action listeners
    public JButton getBackButton() {
        return backButton;
    }

    public JButton getHomeButton() {
        return homeButton;
    }

    public JButton getHelpButton() {
        return helpButton;
    }

    /**
     * Set action cho nút Quay lại
     */
    public void setBackAction(Runnable action) {
        if (backButton != null) {
            backButton.addActionListener(e -> action.run());
        }
    }

    /**
     * Set action cho nút Màn hình chính
     */
    public void setHomeAction(Runnable action) {
        if (homeButton != null) {
            homeButton.addActionListener(e -> action.run());
        }
    }

    /**
     * Set action cho nút Trợ giúp
     */
    public void setHelpAction(Runnable action) {
        if (helpButton != null) {
            helpButton.addActionListener(e -> action.run());
        }
    }

    /**
     * Tạo toolbar với navigation context (biết nơi quay về)
     */
    public static UniversalToolbar createWithNavigation(Runnable onBack, Runnable onHome) {
        UniversalToolbar toolbar = new UniversalToolbar();
        if (onBack != null) {
            toolbar.setBackAction(onBack);
        } else {
            toolbar.getBackButton().setEnabled(false);
        }
        
        if (onHome != null) {
            toolbar.setHomeAction(onHome);
        }
        
        // Default help action - hiển thị hướng dẫn chung
        toolbar.setHelpAction(() -> {
            Dialogs.showMessageDialog(toolbar,
                "Hướng dẫn sử dụng:\n\n"+
                "• F2: Thêm mới\n"+
                "• F3: Tìm kiếm\n"+
                "• F4: Lưu\n"+
                "• F8: Thanh toán (POS)\n"+
                "• ESC: Hủy/Đóng\n"+
                "• Ctrl+K: Tìm kiếm toàn cục\n\n"+
                "Liên hệ hỗ trợ: support@petstation.com",
                "Trợ giúp",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        return toolbar;
    }

    /**
     * Disable nút Back khi không có màn hình trước
     */
    public void setBackEnabled(boolean enabled) {
        if (backButton != null) {
            backButton.setEnabled(enabled);
        }
    }
}
