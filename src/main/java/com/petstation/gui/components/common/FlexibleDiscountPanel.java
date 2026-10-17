package com.petstation.gui.components.common;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Money;

import javax.swing.*;
import java.awt.*;

/**
 * Panel giảm giá linh hoạt: % hoặc số tiền cố định
 */
public class FlexibleDiscountPanel extends JPanel {
    
    public enum DiscountType {
        PERCENT("Giảm %"),
        AMOUNT("Giảm số tiền");
        
        private String label;
        DiscountType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }
    
    private JComboBox<String> typeCombo;
    private JTextField valueField;
    private JLabel errorLabel;
    private JLabel previewLabel;
    private long subtotal = 0;
    
    public FlexibleDiscountPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);
        
        JLabel label = new JLabel("Giảm giá:");
        label.setFont(AppColors.font(Font.PLAIN, 13));
        add(label);
        
        // Type selector
        typeCombo = new JComboBox<>(new String[]{
            DiscountType.PERCENT.getLabel(),
            DiscountType.AMOUNT.getLabel()
        });
        typeCombo.setFont(AppColors.font(Font.PLAIN, 12));
        typeCombo.addActionListener(e -> {
            updatePlaceholder();
            calculatePreview();
        });
        add(typeCombo);
        
        // Value input
        valueField = new JTextField(8);
        valueField.setFont(AppColors.font(Font.PLAIN, 13));
        valueField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculatePreview(); }
        });
        add(valueField);
        
        // Preview label
        previewLabel = new JLabel();
        previewLabel.setFont(AppColors.font(Font.PLAIN, 12));
        previewLabel.setForeground(new Color(34, 139, 34));
        add(previewLabel);
        
        // Error label (below)
        errorLabel = new JLabel("");
        errorLabel.setFont(AppColors.font(Font.PLAIN, 11));
        errorLabel.setForeground(AppColors.DANGER_TEXT);
        
        updatePlaceholder();
    }
    
    private void updatePlaceholder() {
        DiscountType type = getSelectedType();
        if (type == DiscountType.PERCENT) {
            valueField.setToolTipText("Nhập % (0-100)");
        } else {
            valueField.setToolTipText("Nhập số tiền giảm");
        }
    }
    
    private void calculatePreview() {
        try {
            long discount = calculateDiscount();
            if (discount > 0) {
                previewLabel.setText("(- "+ Money.format(discount) + ")");
            } else {
                previewLabel.setText("");
            }
            errorLabel.setText("");
        } catch (Exception e) {
            previewLabel.setText("");
            // Error label sẽ được set bởi calculateDiscount()
        }
    }
    
    public long calculateDiscount() {
        String value = valueField.getText().trim();
        
        if (value.isEmpty()) {
            errorLabel.setText("");
            return 0;
        }
        
        try {
            DiscountType type = getSelectedType();
            
            if (type == DiscountType.PERCENT) {
                double percent = Double.parseDouble(value);
                
                if (percent < 0) {
                    errorLabel.setText("% không thể âm");
                    return 0;
                }
                
                if (percent > 100) {
                    errorLabel.setText("% không thể > 100");
                    return 0;
                }
                
                errorLabel.setText("");
                return (long) (subtotal * percent / 100);
                
            } else { // AMOUNT
                long amount = Long.parseLong(value.replace(",", "").replace(".", ""));
                
                if (amount < 0) {
                    errorLabel.setText("Số tiền không thể âm");
                    return 0;
                }
                
                if (amount > subtotal) {
                    errorLabel.setText("Giảm giá không thể > tổng tiền");
                    return subtotal;
                }
                
                errorLabel.setText("");
                return amount;
            }
            
        } catch (NumberFormatException e) {
            errorLabel.setText("Giá trị không hợp lệ");
            return 0;
        }
    }
    
    public void setSubtotal(long subtotal) {
        this.subtotal = subtotal;
        calculatePreview();
    }
    
    public long getSubtotal() {
        return subtotal;
    }
    
    public DiscountType getSelectedType() {
        int index = typeCombo.getSelectedIndex();
        return index == 0 ? DiscountType.PERCENT : DiscountType.AMOUNT;
    }
    
    public String getValueText() {
        return valueField.getText().trim();
    }
    
    public void setValue(String value) {
        valueField.setText(value);
    }
    
    public void setType(DiscountType type) {
        typeCombo.setSelectedIndex(type == DiscountType.PERCENT ? 0 : 1);
    }
    
    public void clear() {
        valueField.setText("");
        errorLabel.setText("");
        previewLabel.setText("");
    }
    
    public JLabel getErrorLabel() {
        return errorLabel;
    }
    
    public boolean hasError() {
        return !errorLabel.getText().trim().isEmpty();
    }
    
    /**
     * Tạo panel với error label ở dưới
     */
    public JPanel createWithErrorLabel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(this, BorderLayout.NORTH);
        wrapper.add(errorLabel, BorderLayout.SOUTH);
        return wrapper;
    }
}
