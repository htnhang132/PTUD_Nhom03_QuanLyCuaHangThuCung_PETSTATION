package com.petstation.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.navigation.MenuModel;
import com.petstation.gui.navigation.ScreenDefinition;
import com.petstation.gui.navigation.ScreenId;
import com.petstation.gui.navigation.ScreenRegistry;

public final class DesktopMenuBar extends JPanel {
    private final ScreenRegistry registry;
    private final Consumer<ScreenId> navigator;
    private final Runnable logout;
    private final Runnable exit;
    private final Map<Character, JButton> menuButtons = new LinkedHashMap<>();
    private final Map<Character, JPopupMenu> popups = new LinkedHashMap<>();

    public DesktopMenuBar(
        ScreenRegistry registry,
        Consumer<ScreenId> navigator,
        Runnable logout,
        Runnable exit
    ) {
        this.registry = registry;
        this.navigator = navigator;
        this.logout = logout;
        this.exit = exit;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(0, 30));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));

        for (MenuModel.Group group : MenuModel.groups()) {
            addMenu(group.mnemonic(), group.label(), group.items());
        }
    }

    public void openMenu(char mnemonic) {
        JButton button = menuButtons.get(Character.toUpperCase(mnemonic));
        JPopupMenu popup = popups.get(Character.toUpperCase(mnemonic));
        if (button != null && popup != null && button.isEnabled()) {
            popup.show(button, 0, button.getHeight());
        }
    }

    private void addMenu(char mnemonic, String label, List<MenuModel.Item> entries) {
        int underline = Math.max(0, label.toLowerCase().indexOf(Character.toLowerCase(mnemonic)));
        JButton button = new JButton("<html>"+ label.substring(0, underline) + "<u>"+ label.charAt(underline) + "</u>"+ label.substring(underline + 1) + "</html>") {
            @Override protected void paintComponent(Graphics graphics) {
                if (getModel().isRollover()) {
                    graphics.setColor(AppColors.RED_TINT);
                    graphics.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(graphics);
            }
        };
        button.setFont(AppColors.font(Font.PLAIN, 13));
        button.setForeground(AppColors.TEXT_DARK);
        button.setBorder(new EmptyBorder(5, 12, 5, 12));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);

        JPopupMenu popup = new JPopupMenu();
        for (MenuModel.Item entry : entries) {
            if (entry.isSeparator()) {
                if (popup.getComponentCount() > 0 && !(popup.getComponent(popup.getComponentCount() - 1) instanceof JPopupMenu.Separator)) {
                    popup.addSeparator();
                }
                continue;
            }
            ScreenDefinition definition = registry.definition(entry.screen());
            if (!registry.canAccess(entry.screen())) continue;
            JMenuItem item = new JMenuItem(entry.label());
            item.setFont(AppColors.font(Font.PLAIN, 13));
            item.setEnabled(definition.implemented());
            item.setToolTipText(definition.implemented()
                ? definition.hint()
                : "Chức năng sẽ được triển khai ở giai đoạn tiếp theo");
            if (entry.shortcut() != null) {
                item.setAccelerator(parseShortcut(entry.shortcut()));
            }
            item.addActionListener(event -> navigator.accept(entry.screen()));
            popup.add(item);
        }
        trimTrailingSeparator(popup);
        if (mnemonic == 'H') {
            if (popup.getComponentCount() > 0) popup.addSeparator();
            JMenuItem logoutItem = new JMenuItem("Đăng xuất");
            logoutItem.addActionListener(event -> logout.run());
            popup.add(logoutItem);
            JMenuItem exitItem = new JMenuItem("Thoát");
            exitItem.addActionListener(event -> exit.run());
            popup.add(exitItem);
        }

        button.setEnabled(popup.getComponentCount() > 0);
        button.getAccessibleContext().setAccessibleName("Menu "+ label);
        button.addActionListener(event -> popup.show(button, 0, button.getHeight()));
        menuButtons.put(mnemonic, button);
        popups.put(mnemonic, popup);
        add(button);
    }

    private void trimTrailingSeparator(JPopupMenu popup) {
        int n = popup.getComponentCount();
        if (n > 0 && popup.getComponent(n - 1) instanceof JPopupMenu.Separator) popup.remove(n - 1);
    }

    private KeyStroke parseShortcut(String shortcut) {
        return KeyStroke.getKeyStroke(shortcut);
    }
}
