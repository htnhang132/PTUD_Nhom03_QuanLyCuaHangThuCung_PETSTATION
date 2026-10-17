package com.petstation.gui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Lưới biểu mẫu 2 cột: nhãn ở trên, ô nhập ở dưới. */
public final class FormPanel extends JPanel {
    private final GridBagConstraints c = new GridBagConstraints();
    private int column;
    private int row;

    public FormPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 12, 14);
        c.anchor = GridBagConstraints.NORTHWEST;
    }

    public void addSection(String title) {
        endRow();
        JLabel label = new JLabel(title);
        label.setFont(AppColors.font(Font.BOLD, 14));
        label.setForeground(AppColors.BROWN);
        label.setBorder(new EmptyBorder(4, 0, 0, 0));
        c.gridx = 0; c.gridy = row++; c.gridwidth = 2;
        add(label, c);
        c.gridwidth = 1;
    }

    public void addField(String label, JComponent field) {
        place(wrap(label, field), 1);
    }

    public void addWide(String label, JComponent field) {
        place(wrap(label, field), 2);
    }

    public void addFull(JComponent component) {
        place(component, 2);
    }

    private void place(JComponent component, int span) {
        if (span == 2 && column != 0) endRow();
        c.gridx = column; c.gridy = row; c.gridwidth = span;
        add(component, c);
        c.gridwidth = 1;
        column += span;
        if (column >= 2) endRow();
    }

    private void endRow() {
        if (column != 0) { column = 0; row++; }
    }

    private JPanel wrap(String label, JComponent field) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setOpaque(false);
        JLabel text = new JLabel(label);
        text.setFont(AppColors.font(Font.BOLD, 12));
        text.setForeground(AppColors.TEXT_DARK);
        text.setLabelFor(field);
        box.add(text, BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        return box;
    }

    /** Ô nhập chuẩn của ứng dụng. */
    public static JTextField textField(String text) {
        JTextField f = new JTextField(text);
        styleInput(f);
        return f;
    }

    public static void styleInput(JComponent f) {
        f.setFont(AppColors.font(Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(160, 36));
    }

    public static <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setFont(AppColors.font(Font.PLAIN, 13));
        box.setPreferredSize(new Dimension(160, 36));
        box.setBackground(Color.WHITE);
        return box;
    }
}
