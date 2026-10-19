package com.petstation.gui.components.common;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Form validation real-time Hiển thị lỗi ngay khi nhập không đợi đến lúc Submit */
public class FormValidator {

    private Map<JComponent, ValidationRule> rules = new HashMap<>();
    private Map<JComponent, JLabel> errorLabels = new HashMap<>();
    private Map<JComponent, Border> originalBorders = new HashMap<>();

    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Border ERROR_BORDER = BorderFactory.createLineBorder(ERROR_COLOR, 2);

    /** Validation rule */
    public static class ValidationRule {
        Function<String, String> validator; // Returns error message or null if valid
        boolean required;

        public ValidationRule(Function<String, String> validator, boolean required) {
            this.validator = validator;
            this.required = required;
        }
    }

    /** Thêm rule cho text field */
    public void addRule(JTextField field, ValidationRule rule, Container parent) {
        rules.put(field, rule);
        originalBorders.put(field, field.getBorder());

        // Create error label
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 11f));
        errorLabel.setVisible(false);
        errorLabels.put(field, errorLabel);

        // Add error label below field (assuming parent uses GridBagLayout or similar)
        // If parent layout doesn't support, error will just not show
        try {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = getComponentRow(parent, field) + 1;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 20, 5, 5);
            parent.add(errorLabel, gbc);
        } catch (Exception e) {
            // If can't add to parent, skip error label
        }

        // Add listener để validate real-time
        field.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                validateField(field);
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                validateField(field);
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                validateField(field);
                            }
                        });
    }

    private int getComponentRow(Container parent, Component comp) {
        Component[] components = parent.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == comp) {
                return i / 2; // Rough estimate
            }
        }
        return 0;
    }

    /** Validate một field */
    public boolean validateField(JTextField field) {
        ValidationRule rule = rules.get(field);
        if (rule == null) return true;

        String value = field.getText().trim();

        // Check required
        if (rule.required && value.isEmpty()) {
            showError(field, "Trường này là bắt buộc");
            return false;
        }

        // Check custom validator
        if (!value.isEmpty() && rule.validator != null) {
            String error = rule.validator.apply(value);
            if (error != null) {
                showError(field, error);
                return false;
            }
        }

        clearError(field);
        return true;
    }

    /** Validate tất cả fields */
    public boolean validateAll() {
        boolean allValid = true;

        for (JComponent field : rules.keySet()) {
            if (field instanceof JTextField) {
                boolean valid = validateField((JTextField) field);
                allValid = allValid && valid;
            }
        }

        return allValid;
    }

    private void showError(JTextField field, String message) {
        field.setBorder(ERROR_BORDER);

        JLabel errorLabel = errorLabels.get(field);
        if (errorLabel != null) {
            errorLabel.setText("" + message);
            errorLabel.setVisible(true);
        }
    }

    private void clearError(JTextField field) {
        Border originalBorder = originalBorders.get(field);
        if (originalBorder != null) {
            field.setBorder(originalBorder);
        }

        JLabel errorLabel = errorLabels.get(field);
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    /** Clear tất cả errors */
    public void clearAllErrors() {
        for (JComponent field : rules.keySet()) {
            if (field instanceof JTextField) {
                clearError((JTextField) field);
            }
        }
    }

    // === COMMON VALIDATORS ===

    /** Required field */
    public static ValidationRule required() {
        return new ValidationRule(null, true);
    }

    /** Email validation */
    public static ValidationRule email() {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        return new ValidationRule(
                value -> {
                    if (!pattern.matcher(value).matches()) {
                        return "Email không đúng định dạng";
                    }
                    return null;
                },
                false);
    }

    /** Số điện thoại (10 hoặc 11 số) */
    public static ValidationRule phone() {
        Pattern pattern = Pattern.compile("^0\\d{9,10}$");
        return new ValidationRule(
                value -> {
                    if (!pattern.matcher(value).matches()) {
                        return "Số điện thoại phải có 10-11 số và bắt đầu bằng 0";
                    }
                    return null;
                },
                false);
    }

    /** Số (integer) */
    public static ValidationRule integer() {
        return new ValidationRule(
                value -> {
                    try {
                        Integer.parseInt(value);
                        return null;
                    } catch (NumberFormatException e) {
                        return "Phải là số nguyên";
                    }
                },
                false);
    }

    /** Số (double) */
    public static ValidationRule decimal() {
        return new ValidationRule(
                value -> {
                    try {
                        Double.parseDouble(value);
                        return null;
                    } catch (NumberFormatException e) {
                        return "Phải là số";
                    }
                },
                false);
    }

    /** Min length */
    public static ValidationRule minLength(int min) {
        return new ValidationRule(
                value -> {
                    if (value.length() < min) {
                        return "Phải có ít nhất " + min + "ký tự";
                    }
                    return null;
                },
                false);
    }

    /** Max length */
    public static ValidationRule maxLength(int max) {
        return new ValidationRule(
                value -> {
                    if (value.length() > max) {
                        return "Không được quá " + max + "ký tự";
                    }
                    return null;
                },
                false);
    }

    /** Range (for numbers) */
    public static ValidationRule range(double min, double max) {
        return new ValidationRule(
                value -> {
                    try {
                        double num = Double.parseDouble(value);
                        if (num < min || num > max) {
                            return String.format("Phải trong khoảng %.0f - %.0f", min, max);
                        }
                        return null;
                    } catch (NumberFormatException e) {
                        return "Phải là số";
                    }
                },
                false);
    }

    /** Custom validator */
    public static ValidationRule custom(Function<String, String> validator) {
        return new ValidationRule(validator, false);
    }

    /** Combine multiple rules */
    public static ValidationRule combine(ValidationRule... rules) {
        return new ValidationRule(
                value -> {
                    for (ValidationRule rule : rules) {
                        if (rule.validator != null) {
                            String error = rule.validator.apply(value);
                            if (error != null) return error;
                        }
                    }
                    return null;
                },
                false);
    }
}
