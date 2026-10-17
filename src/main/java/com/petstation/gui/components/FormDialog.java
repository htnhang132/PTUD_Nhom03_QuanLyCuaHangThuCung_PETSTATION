package com.petstation.gui.components;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Hộp thoại nhập nhanh vài trường, cùng kiểu nút Đồng ý/Hủy với {@link Dialogs}. */
public final class FormDialog {
    private FormDialog() {}

    /** Trả về giá trị theo thứ tự nhãn, hoặc null nếu bấm Hủy. */
    public static List<String> ask(Component parent, String title, String[] labels, String[] initial) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i; c.weightx = 0;
            p.add(new JLabel(labels[i]), c);
            fields[i] = new JTextField(initial != null && i < initial.length ? initial[i] : "", 22);
            c.gridx = 1; c.weightx = 1;
            p.add(fields[i], c);
        }
        Object[] options = {"Đồng ý", "Hủy"};
        int r = JOptionPane.showOptionDialog(parent, p, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (r != 0) return null;
        List<String> out = new ArrayList<>();
        for (JTextField f : fields) out.add(f.getText().trim());
        return out;
    }
}
