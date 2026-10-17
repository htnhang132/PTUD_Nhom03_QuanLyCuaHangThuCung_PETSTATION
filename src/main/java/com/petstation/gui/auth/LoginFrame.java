package com.petstation.gui.auth;

import com.petstation.gui.components.Dialogs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.Year;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.petstation.config.ApplicationContext;
import com.petstation.config.DataAccessException;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.main.MainFrame;
import com.petstation.security.UserSession;
import com.petstation.service.AuthenticationException;

public class LoginFrame extends JFrame {
    private static final char ECHO = '\u2022';
    /** Chiều rộng vùng nội dung của thẻ đăng nhập (nhãn, ô nhập, nút cùng rộng bằng nhau). */
    private static final int FORM_WIDTH = 340;

    private final ApplicationContext context;
    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JCheckBox showPassword = new JCheckBox("Hiện mật khẩu");
    private final JLabel errorLabel = new JLabel("");
    private final JLabel capsLabel = new JLabel("");
    private final StyledButton btnLogin = new StyledButton("Đăng nhập", StyledButton.Variant.PRIMARY);

    public LoginFrame(ApplicationContext context) {
        this.context = context;
        setTitle("Pet Station – Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(480, 640);
        setMinimumSize(new Dimension(480, 640));
        setLocationRelativeTo(null);
        setResizable(false);

        // Root panel with gradient background
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, AppColors.BROWN, 0, getHeight(), new Color(0x2A, 0x0C, 0x02)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(new Color(0x8C, 0x7A, 0x6E), 1));
        setContentPane(root);

        // Top bar (draggable, close button)
        root.add(buildTopBar(), BorderLayout.NORTH);

        // Center card container - uses GridBagLayout to center perfectly
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // Login card
        JPanel card = new JPanel();
        card.setBackground(AppColors.BG_CREAM);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 48, 40, 48));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        JLabel logoIcon = createLogo();
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoIcon.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Title
        JLabel title = new JLabel("Pet Station", SwingConstants.CENTER);
        title.setFont(AppColors.font(Font.BOLD, 30));
        title.setForeground(AppColors.RED_BRAND);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 4, 0));

        // Subtitle
        JLabel subtitle = new JLabel("Hệ thống quản lý cửa hàng thú cưng", SwingConstants.CENTER);
        subtitle.setFont(AppColors.font(Font.PLAIN, 13));
        subtitle.setForeground(AppColors.TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(0, 0, 32, 0));

        // Style fields
        styleField(txtUser);
        styleField(txtPass);
        txtPass.setEchoChar(ECHO);
        txtUser.getAccessibleContext().setAccessibleName("Tên đăng nhập");
        txtPass.getAccessibleContext().setAccessibleName("Mật khẩu");

        // Show password checkbox
        showPassword.setOpaque(false);
        showPassword.setFont(AppColors.font(Font.PLAIN, 12));
        showPassword.setForeground(AppColors.TEXT_DARK);
        showPassword.addActionListener(e -> txtPass.setEchoChar(showPassword.isSelected() ? (char) 0 : ECHO));

        // Caps lock label
        capsLabel.setFont(AppColors.font(Font.PLAIN, 11));
        capsLabel.setForeground(AppColors.ORANGE);
        txtPass.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { updateCapsLock(); }
            @Override public void keyReleased(KeyEvent e) { updateCapsLock(); }
        });

        // Error label
        errorLabel.setForeground(AppColors.DANGER_TEXT);
        errorLabel.setFont(AppColors.font(Font.PLAIN, 12));
        errorLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        errorLabel.getAccessibleContext().setAccessibleName("Thông báo lỗi đăng nhập");

        // Login button
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnLogin.setFont(AppColors.font(Font.BOLD, 15));
        btnLogin.addActionListener(e -> doLogin());
        txtPass.addActionListener(e -> doLogin());
        txtUser.addActionListener(e -> txtPass.requestFocusInWindow());

        // Forgot password link
        JButton forgot = new JButton("Quên mật khẩu?");
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setFocusPainted(false);
        forgot.setForeground(AppColors.BLUE);
        forgot.setFont(AppColors.font(Font.PLAIN, 12));
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.setHorizontalAlignment(SwingConstants.RIGHT);
        forgot.setBorder(new EmptyBorder(0, 0, 0, 0));
        forgot.setMargin(new Insets(0, 0, 0, 0));
        forgot.addActionListener(e -> Dialogs.showMessageDialog(this,
                "Tài khoản do Cửa hàng trưởng quản lý.\nHãy liên hệ Cửa hàng trưởng để được cấp lại mật khẩu tạm.",
                "Quên mật khẩu", JOptionPane.INFORMATION_MESSAGE));

        // Build card content with consistent spacing
        card.add(logoIcon);
        card.add(title);
        card.add(subtitle);

        // Username field group
        card.add(fieldLabel("Tên đăng nhập"));
        card.add(Box.createVerticalStrut(8));
        card.add(txtUser);
        card.add(Box.createVerticalStrut(20));

        // Password field group
        card.add(fieldLabel("Mật khẩu"));
        card.add(Box.createVerticalStrut(8));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(8));

        // Options row: checkbox left, forgot right
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);
        optionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        optionsRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsRow.add(showPassword, BorderLayout.WEST);
        optionsRow.add(forgot, BorderLayout.EAST);
        card.add(optionsRow);
        card.add(leftRow(capsLabel));
        card.add(leftRow(errorLabel));
        card.add(Box.createVerticalStrut(24));
        card.add(btnLogin);

        // Add card to center wrapper (perfectly centered)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(card, gbc);

        root.add(centerWrapper, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© " + Year.now() + " Pet Station – v1.0.0", SwingConstants.CENTER);
        footer.setFont(AppColors.font(Font.PLAIN, 11));
        footer.setForeground(new Color(255, 255, 255, 150));
        footer.setBorder(new EmptyBorder(10, 0, 14, 0));
        root.add(footer, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);
        SwingUtilities.invokeLater(txtUser::requestFocusInWindow);
    }

    /** Top bar: draggable area + close button */
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 36));

        JButton close = new JButton("×");
        close.setFont(AppColors.font(Font.PLAIN, 20));
        close.setForeground(Color.WHITE);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setFocusPainted(false);
        close.setPreferredSize(new Dimension(44, 36));
        close.setToolTipText("Thoát");
        close.getAccessibleContext().setAccessibleName("Thoát ứng dụng");
        close.addActionListener(e -> System.exit(0));

        // Hover effect for close button
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                close.setForeground(AppColors.YELLOW);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                close.setForeground(Color.WHITE);
            }
        });

        bar.add(close);

        // Drag to move window
        MouseAdapter drag = new MouseAdapter() {
            private Point origin;
            @Override public void mousePressed(MouseEvent e) { origin = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (origin == null) return;
                Point p = e.getLocationOnScreen();
                setLocation(p.x - origin.x, p.y - origin.y);
            }
        };
        bar.addMouseListener(drag);
        bar.addMouseMotionListener(drag);
        return bar;
    }

    private void updateCapsLock() {
        boolean caps;
        try {
            caps = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        } catch (UnsupportedOperationException ex) {
            caps = false;
        }
        capsLabel.setText(caps ? "⚠ Caps Lock đang bật" : "");
    }

    private void doLogin() {
        String username = txtUser.getText().trim();
        char[] password = txtPass.getPassword();
        if (username.isEmpty()) {
            showMessage("Vui lòng nhập tên đăng nhập.", AppColors.DANGER_TEXT);
            txtUser.requestFocusInWindow();
            return;
        }
        if (password.length == 0) {
            showMessage("Vui lòng nhập mật khẩu.", AppColors.DANGER_TEXT);
            txtPass.requestFocusInWindow();
            return;
        }

        setFormEnabled(false);
        showMessage("Đang xác thực...", AppColors.TEXT_GRAY);
        new SwingWorker<UserSession, Void>() {
            @Override
            protected UserSession doInBackground() {
                try {
                    return context.authenticationService().login(username, password);
                } finally {
                    Arrays.fill(password, '\0');
                }
            }

            @Override
            protected void done() {
                try {
                    UserSession session = get();
                    context.sessionContext().start(session);
                    dispose();
                    new MainFrame(context).setVisible(true);
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    String message = cause instanceof AuthenticationException || cause instanceof DataAccessException
                            ? cause.getMessage()
                            : "Không thể đăng nhập. Vui lòng thử lại.";
                    showMessage(message, AppColors.DANGER_TEXT);
                    txtPass.setText("");
                    txtPass.requestFocusInWindow();
                    setFormEnabled(true);
                }
            }
        }.execute();
    }

    private void setFormEnabled(boolean enabled) {
        txtUser.setEnabled(enabled);
        txtPass.setEnabled(enabled);
        showPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
    }

    private JLabel createLogo() {
        URL resource = LoginFrame.class.getResource("/images/pet-station-logo.png");
        if (resource == null) {
            // Fallback: styled text logo
            JLabel fallback = new JLabel("🐾", SwingConstants.CENTER);
            fallback.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            return fallback;
        }
        Image image = new ImageIcon(resource).getImage().getScaledInstance(86, 86, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleField(JTextField f) {
        f.setFont(AppColors.font(Font.PLAIN, 14));
        f.setForeground(AppColors.TEXT_DARK);
        f.setBackground(Color.WHITE);
        f.setCaretColor(AppColors.RED_BRAND);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        f.setPreferredSize(new Dimension(FORM_WIDTH, 44));
        f.setMinimumSize(new Dimension(FORM_WIDTH, 44));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JComponent fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.font(Font.BOLD, 13));
        l.setForeground(AppColors.TEXT_DARK);
        JComponent row = leftRow(l);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    /**
     * Đặt một thành phần vào hàng căn trái, rộng bằng thẻ. Mọi phần tử trong thẻ dùng chung
     * CENTER_ALIGNMENT; nếu trộn LEFT/CENTER/RIGHT, BoxLayout sẽ dịch các phần tử lệch nhau.
     */
    private JComponent leftRow(JComponent c) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(c, BorderLayout.WEST);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        return row;
    }

    /** Hiển thị thông báo, tự xuống dòng để không làm thẻ rộng ra. */
    private void showMessage(String text, Color color) {
        errorLabel.setForeground(color);
        if (text == null || text.isEmpty()) {
            errorLabel.setText("");
            return;
        }
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        errorLabel.setText("<html><div style='width:" + FORM_WIDTH + "px'>" + safe + "</div></html>");
    }
}