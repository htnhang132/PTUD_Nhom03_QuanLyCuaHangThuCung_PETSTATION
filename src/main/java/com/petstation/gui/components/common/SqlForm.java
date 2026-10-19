package com.petstation.gui.components.common;

import com.petstation.gui.components.Dialogs;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

/** Small data-entry forms with SQL-backed choices. Identity values never need manual entry. */
public final class SqlForm {
    public record Option(String value, String label) {
        @Override
        public String toString() {
            return label;
        }
    }

    public record Field(String key, String label, String initial, List<Option> options) {
        public static Field text(String key, String label, String initial) {
            return new Field(key, label, initial, null);
        }

        public static Field choice(String key, String label, List<Object[]> rows) {
            return new Field(
                    key,
                    label,
                    "",
                    rows.stream()
                            .map(r -> new Option(String.valueOf(r[0]), String.valueOf(r[1])))
                            .toList());
        }
    }

    private SqlForm() {}

    public static Map<String, String> ask(Component parent, String title, Field... fields) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        Map<String, JComponent> inputs = new LinkedHashMap<>();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            c.gridy = i;
            c.gridx = 0;
            c.weightx = 0;
            form.add(new JLabel(f.label()), c);
            JComponent input;
            if (f.options() == null) input = new JTextField(f.initial(), 28);
            else {
                if (f.options().isEmpty()) {
                    Dialogs.warn(
                            parent,
                            "Chưa có "
                                    + f.label().toLowerCase()
                                    + " phù hợp. Hãy bổ sung dữ liệu trước.");
                    return null;
                }
                input = new JComboBox<>(f.options().toArray(Option[]::new));
            }
            inputs.put(f.key(), input);
            c.gridx = 1;
            c.weightx = 1;
            form.add(input, c);
        }
        if (Dialogs.showConfirmDialog(
                        parent,
                        form,
                        title,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE)
                != JOptionPane.OK_OPTION) return null;
        Map<String, String> values = new LinkedHashMap<>();
        for (Field f : fields) {
            JComponent input = inputs.get(f.key());
            values.put(
                    f.key(),
                    input instanceof JTextField t
                            ? t.getText().trim()
                            : ((Option) ((JComboBox<?>) input).getSelectedItem()).value());
        }
        return values;
    }
}
