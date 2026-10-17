package com.petstation.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.navigation.ShiftState;
import com.petstation.security.UserSession;

public final class DesktopStatusBar extends JPanel {
    private final JLabel clock = valueLabel("");
    private final JLabel hint = valueLabel("");

    private final JLabel shiftLabel = valueLabel("");

    public DesktopStatusBar(UserSession session, ShiftState shift) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(0, 26));
        setBackground(new Color(0xF3, 0xED, 0xE5));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));

        add(valueLabel("Người dùng: "+ session.displayName()));
        add(separator());
        add(valueLabel("Vai trò: "+ session.role().displayName()));
        add(separator());
        add(shiftLabel);
        shiftLabel.setText("Ca hiện tại: "+ shift.label());
        shift.addListener(() -> shiftLabel.setText("Ca hiện tại: "+ shift.label()));
        add(separator());
        add(clock);
        add(separator());
        hint.setForeground(AppColors.TEXT_GRAY);
        add(hint);
        add(Box.createHorizontalGlue());

        updateClock();
        new Timer(15000, event -> updateClock()).start();
    }

    public void setHint(String text) {
        hint.setText(text);
    }

    private void updateClock() {
        clock.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private static JLabel valueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setFont(AppColors.font(Font.PLAIN, 12));
        label.setForeground(AppColors.TEXT_DARK);
        label.setBorder(new EmptyBorder(0, 10, 0, 10));
        return label;
    }

    private JSeparator separator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setMaximumSize(new Dimension(1, 16));
        separator.setForeground(AppColors.BORDER);
        return separator;
    }
}
