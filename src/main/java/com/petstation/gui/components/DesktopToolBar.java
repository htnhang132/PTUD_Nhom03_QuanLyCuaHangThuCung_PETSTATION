package com.petstation.gui.components;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class DesktopToolBar extends JPanel {
    public enum Action {
        NEW,
        SAVE,
        FIND,
        PRINT,
        UNDO,
        HELP
    }

    private final Map<Action, JButton> buttons = new EnumMap<>(Action.class);
    private final Map<Action, String> tooltips = new EnumMap<>(Action.class);
    private ToolbarAware activePanel;

    private final JButton backButton;

    public DesktopToolBar(Runnable goHome, Runnable goBack) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 4));
        setPreferredSize(new Dimension(0, 48));
        setBackground(AppColors.SURFACE_ALT);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));

        backButton = createButton("Quay lại", "Alt+←", LineIcon.Type.BACK);
        backButton.addActionListener(event -> goBack.run());
        backButton.setEnabled(false);
        add(backButton);
        JButton home = createButton("Màn hình chính", "Alt+Home", LineIcon.Type.HOME);
        home.addActionListener(event -> goHome.run());
        add(home);
        add(divider());
        addAction(Action.HELP, "Trợ giúp", "F1", LineIcon.Type.HELP);
        setActivePanel(null);
    }

    public void setActivePanel(JPanel panel) {
        activePanel = panel instanceof ToolbarAware aware ? aware : null;
        ToolbarCapabilities capabilities =
                activePanel == null
                        ? ToolbarCapabilities.none()
                        : activePanel.toolbarCapabilities();
        setEnabled(Action.NEW, capabilities.canCreate());
        setEnabled(Action.SAVE, capabilities.canSave());
        setEnabled(Action.FIND, capabilities.canFind());
        setEnabled(Action.PRINT, capabilities.canPrint());
        setEnabled(Action.UNDO, capabilities.canUndo());
        setEnabled(Action.HELP, capabilities.canHelp());
    }

    public void setCanGoBack(boolean enabled) {
        backButton.setEnabled(enabled);
    }

    public void goBackIfPossible() {
        if (backButton.isEnabled()) backButton.doClick(0);
    }

    public void trigger(Action action) {
        JButton button = buttons.get(action);
        if (button == null || !button.isEnabled() || activePanel == null) return;
        switch (action) {
            case NEW -> activePanel.onNew();
            case SAVE -> activePanel.onSave();
            case FIND -> activePanel.onFind();
            case PRINT -> activePanel.onPrint();
            case UNDO -> activePanel.onUndo();
            case HELP -> activePanel.onHelp();
        }
    }

    private void addAction(Action action, String label, String shortcut, LineIcon.Type iconType) {
        JButton button = createButton(label, shortcut, iconType);
        button.addActionListener(event -> trigger(action));
        buttons.put(action, button);
        tooltips.put(action, label + "(" + shortcut + ")");
        add(button);
    }

    private JButton createButton(String label, String shortcut, LineIcon.Type iconType) {
        JButton button =
                new JButton(label, new LineIcon(iconType, 20, AppColors.BROWN)) {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        Graphics2D g = (Graphics2D) graphics.create();
                        if (isEnabled() && getModel().isRollover()) {
                            g.setColor(AppColors.RED_TINT);
                            g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
                        }
                        if (isFocusOwner()) {
                            g.setColor(AppColors.YELLOW);
                            g.setStroke(new BasicStroke(2));
                            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                        }
                        g.dispose();
                        super.paintComponent(graphics);
                    }
                };
        button.setFont(AppColors.font(Font.PLAIN, 12));
        button.setForeground(AppColors.TEXT_DARK);
        button.setToolTipText(label + "(" + shortcut + ")");
        button.setBorder(new EmptyBorder(5, 8, 5, 8));
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.getAccessibleContext().setAccessibleName(label);
        return button;
    }

    private JSeparator divider() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(7, 30));
        separator.setForeground(AppColors.BORDER);
        return separator;
    }

    private void setEnabled(Action action, boolean enabled) {
        JButton button = buttons.get(action);
        if (button == null) return;
        button.setEnabled(enabled);
        button.setToolTipText(
                enabled
                        ? tooltips.get(action)
                        : button.getText() + "– Chức năng chưa khả dụng trên màn hình này");
    }
}
