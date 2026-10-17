package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Money;
import com.petstation.gui.navigation.AppSettings;
import com.petstation.gui.navigation.AuditTrail;
import com.petstation.gui.components.RoundedPanel;
import com.petstation.gui.components.StyledButton;

/**
 * Panel cài đặt hệ thống
 * Sử dụng đúng component cho từng loại setting:
 * - ComboBox cho lựa chọn có sẵn (múi giờ, ngôn ngữ, tiền tệ)
 * - Checkbox cho bật/tắt
 * - TextField cho text tự do (tên, địa chỉ, SĐT)
 */
public class SettingsPanel extends BasePanel {
    
    // Store settings fields for saving
    private JTextField storeNameField;
    private JTextField storeAddressField;
    private JTextField storePhoneField;
    private JTextField storeEmailField;
    private JTextField storeWebsiteField;
    
    private JComboBox<String> timezoneCombo;
    private JComboBox<String> languageCombo;
    private JComboBox<String> currencyCombo;
    private JComboBox<String> dateFormatCombo;
    
    private JComboBox<String> printerCombo;
    private JComboBox<String> paperSizeCombo;
    private JCheckBox autoPrintCheck;
    
    private JCheckBox lowStockAlertCheck;
    private JSpinner lowStockThresholdSpinner;
    private JCheckBox newOrderAlertCheck;
    private JCheckBox spaScheduleAlertCheck;
    private JCheckBox dailyReportCheck;
    private JSpinner reportTimeSpinner;

    private final boolean editable;

    /** @param editable true khi người dùng là Cửa hàng trưởng (Admin); ngược lại màn chỉ xem. */
    public SettingsPanel(boolean editable) {
        this.editable = editable;
        setLayout(new BorderLayout());

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = sectionTitle("Cài đặt hệ thống");
        title.setFont(AppColors.font(Font.BOLD, 21));
        mainContent.add(title, BorderLayout.NORTH);

        // Settings grid
        JPanel content = new JPanel(new GridLayout(0, 2, 20, 20));
        content.setOpaque(false);

        content.add(createStoreInfoGroup());
        content.add(createSystemSettingsGroup());
        content.add(createPrintSettingsGroup());
        content.add(createNotificationSettingsGroup());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        mainContent.add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(16, 0, 0, 0));
        
        StyledButton cancelBtn = new StyledButton("Hủy thay đổi", StyledButton.Variant.GHOST);
        cancelBtn.addActionListener(e -> handleCancel());
        bottom.add(cancelBtn);
        
        StyledButton saveBtn = new StyledButton("Lưu cài đặt", StyledButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> handleSave());
        bottom.add(saveBtn);
        
        mainContent.add(bottom, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
        if (!editable) {
            setEnabledRecursive(content, false);
            saveBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
        }
    }

    private static void setEnabledRecursive(java.awt.Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof java.awt.Container container) {
            for (java.awt.Component child : container.getComponents()) setEnabledRecursive(child, enabled);
        }
    }

    private JPanel createStoreInfoGroup() {
        RoundedPanel card = card(new BorderLayout(0, 12));

        JLabel t = sectionTitle("Thông tin cửa hàng");
        card.add(t, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Tên cửa hàng
        storeNameField = new JTextField("Pet Station Hà Nội");
        addFormRow(form, gbc, row++, "Tên cửa hàng", storeNameField);

        // Địa chỉ
        storeAddressField = new JTextField("123 Phố Huế, Hai Bà Trưng, Hà Nội");
        addFormRow(form, gbc, row++, "Địa chỉ", storeAddressField);

        // Điện thoại
        storePhoneField = new JTextField("024 3825 6789");
        addFormRow(form, gbc, row++, "Điện thoại", storePhoneField);

        // Email
        storeEmailField = new JTextField("info@petstation.vn");
        addFormRow(form, gbc, row++, "Email", storeEmailField);

        // Website
        storeWebsiteField = new JTextField("www.petstation.vn");
        addFormRow(form, gbc, row++, "Website", storeWebsiteField);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JSpinner vatSpinner;
    private JSpinner expiryDaysSpinner;
    private JSpinner returnDaysSpinner;
    private JTextField lateFeeField;
    private JTextField writeOffLimitField;

    private JPanel createSystemSettingsGroup() {
        RoundedPanel card = card(new BorderLayout(0, 12));
        card.add(sectionTitle("Quy tắc nghiệp vụ"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        vatSpinner = new JSpinner(new SpinnerNumberModel(AppSettings.vatPercent(), 0, 20, 1));
        addFormRow(form, gbc, row++, "Thuế VAT (%)", vatSpinner);
        lowStockThresholdSpinner = new JSpinner(new SpinnerNumberModel(AppSettings.lowStockThreshold(), 1, 1000, 1));
        addFormRow(form, gbc, row++, "Ngưỡng tồn thấp (số lượng)", lowStockThresholdSpinner);
        expiryDaysSpinner = new JSpinner(new SpinnerNumberModel(AppSettings.expiryWarnDays(), 1, 365, 1));
        addFormRow(form, gbc, row++, "Cảnh báo hết hạn trước (ngày)", expiryDaysSpinner);
        lateFeeField = new JTextField(Money.format(AppSettings.lateCheckoutFeePerDay()));
        addFormRow(form, gbc, row++, "Phí trả phòng trễ (mỗi ngày)", lateFeeField);
        returnDaysSpinner = new JSpinner(new SpinnerNumberModel(AppSettings.returnWindowDays(), 1, 90, 1));
        addFormRow(form, gbc, row++, "Thời hạn đổi trả (ngày)", returnDaysSpinner);
        writeOffLimitField = new JTextField(Money.format(AppSettings.writeOffApprovalLimit()));
        addFormRow(form, gbc, row++, "Xuất hủy cần duyệt từ (₫)", writeOffLimitField);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel who = new JLabel(editable ? "Chỉ Cửa hàng trưởng được sửa các quy tắc này." : "Bạn chỉ có quyền xem. Chỉ Cửa hàng trưởng được sửa.");
        who.setFont(AppColors.font(Font.PLAIN, 12));
        who.setForeground(AppColors.TEXT_GRAY);
        form.add(who, gbc);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPrintSettingsGroup() {
        RoundedPanel card = card(new BorderLayout(0, 12));

        JLabel t = sectionTitle("Cài đặt in ấn");
        card.add(t, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Máy in - COMBOBOX
        String[] printers = {
            "Epson TM-T82III",
            "Epson TM-T88V",
            "Star TSP143III",
            "Không có máy in"
        };
        printerCombo = new JComboBox<>(printers);
        addFormRow(form, gbc, row++, "Máy in hóa đơn", printerCombo);

        // Khổ giấy - COMBOBOX
        String[] paperSizes = {"80mm", "58mm", "A4"};
        paperSizeCombo = new JComboBox<>(paperSizes);
        addFormRow(form, gbc, row++, "Khổ giấy", paperSizeCombo);

        // Tự động in - CHECKBOX
        autoPrintCheck = new JCheckBox("Tự động in hóa đơn sau khi thanh toán");
        autoPrintCheck.setSelected(true);
        autoPrintCheck.setOpaque(false);
        autoPrintCheck.setFont(AppColors.font(Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        form.add(autoPrintCheck, gbc);
        gbc.gridwidth = 1;

        // Nút Test Print
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 4, 8);
        StyledButton testPrintBtn = new StyledButton("In thử", StyledButton.Variant.GHOST);
        testPrintBtn.setToolTipText("Kiểm tra kết nối máy in trước khi lưu");
        testPrintBtn.addActionListener(e -> handleTestPrint());
        form.add(testPrintBtn, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 0, 4, 8);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel createNotificationSettingsGroup() {
        RoundedPanel card = card(new BorderLayout(0, 12));

        JLabel t = sectionTitle("Thông báo");
        card.add(t, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Hàng sắp hết - CHECKBOX + SPINNER
        lowStockAlertCheck = new JCheckBox("Cảnh báo hàng sắp hết");
        lowStockAlertCheck.setSelected(true);
        lowStockAlertCheck.setOpaque(false);
        lowStockAlertCheck.setFont(AppColors.font(Font.PLAIN, 13));
        
        JPanel lowStockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        lowStockPanel.setOpaque(false);
        lowStockPanel.add(lowStockAlertCheck);
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        form.add(lowStockPanel, gbc);
        gbc.gridwidth = 1;

        // Đơn hàng mới - CHECKBOX
        newOrderAlertCheck = new JCheckBox("Thông báo đơn hàng mới");
        newOrderAlertCheck.setSelected(true);
        newOrderAlertCheck.setOpaque(false);
        newOrderAlertCheck.setFont(AppColors.font(Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        form.add(newOrderAlertCheck, gbc);
        gbc.gridwidth = 1;

        // Lịch Spa - CHECKBOX
        spaScheduleAlertCheck = new JCheckBox("Nhắc lịch Spa");
        spaScheduleAlertCheck.setSelected(true);
        spaScheduleAlertCheck.setOpaque(false);
        spaScheduleAlertCheck.setFont(AppColors.font(Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        form.add(spaScheduleAlertCheck, gbc);
        gbc.gridwidth = 1;

        // Báo cáo ngày - CHECKBOX + TIME SPINNER
        dailyReportCheck = new JCheckBox("Gửi báo cáo cuối ngày");
        dailyReportCheck.setSelected(true);
        dailyReportCheck.setOpaque(false);
        dailyReportCheck.setFont(AppColors.font(Font.PLAIN, 13));
        
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        reportPanel.setOpaque(false);
        reportPanel.add(dailyReportCheck);
        reportPanel.add(new JLabel("- Lúc:"));
        
        SpinnerDateModel timeModel = new SpinnerDateModel();
        reportTimeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(reportTimeSpinner, "HH:mm");
        reportTimeSpinner.setEditor(timeEditor);
        reportTimeSpinner.setPreferredSize(new Dimension(80, 25));
        reportPanel.add(reportTimeSpinner);
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        form.add(reportPanel, gbc);
        gbc.gridwidth = 1;

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(AppColors.font(Font.PLAIN, 13));
        lbl.setForeground(AppColors.TEXT_GRAY);
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Style component
        component.setFont(AppColors.font(Font.PLAIN, 13));
        if (component instanceof JTextField) {
            component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                new EmptyBorder(4, 8, 4, 8)
            ));
        }
        
        form.add(component, gbc);
    }

    // === EVENT HANDLERS ===

    private void handleTestPrint() {
        String printer = (String) printerCombo.getSelectedItem();
        String paperSize = (String) paperSizeCombo.getSelectedItem();
        
        if (printer.equals("Không có máy in")) {
            Dialogs.showMessageDialog(this,
                "Vui lòng chọn máy in trước khi in thử!",
                "Chưa chọn máy in",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulate test print
        Dialogs.showMessageDialog(this,
            String.format("KIỂM TRA IN THỬ\n\n"+
                "Máy in: %s\n"+
                "Khổ giấy: %s\n"+
                "Thời gian: %s\n\n"+
                "═══════════════════════\n"+
                "PET STATION HÀ NỘI\n"+
                "123 Phố Huế, HBT, HN\n"+
                "Tel: 024 3825 6789\n"+
                "═══════════════════════\n\n"+
                "IN THỬ THÀNH CÔNG!\n\n"+
                "Máy in đã kết nối OK\n\n"+
                "═══════════════════════\n\n"+
                "(Mock UI - trong production sẽ gửi lệnh in thật đến máy in)",
                printer, paperSize, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())),
            "In thử thành công",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCancel() {
        if (!editable) return;
        if (Dialogs.confirm(this, "Hủy các thay đổi chưa lưu và quay về giá trị đã lưu?")) loadFromSettings();
    }

    private void loadFromSettings() {
        vatSpinner.setValue(AppSettings.vatPercent());
        lowStockThresholdSpinner.setValue(AppSettings.lowStockThreshold());
        expiryDaysSpinner.setValue(AppSettings.expiryWarnDays());
        lateFeeField.setText(Money.format(AppSettings.lateCheckoutFeePerDay()));
        returnDaysSpinner.setValue(AppSettings.returnWindowDays());
        writeOffLimitField.setText(Money.format(AppSettings.writeOffApprovalLimit()));
    }

    private void handleSave() {
        if (!editable) {
            Dialogs.warn(this, "Chỉ Cửa hàng trưởng được sửa cài đặt.");
            return;
        }
        long lateFee = Money.parse(lateFeeField.getText());
        long writeOffLimit = Money.parse(writeOffLimitField.getText());
        if (lateFee < 0 || writeOffLimit < 0) {
            Dialogs.warn(this, "Phí trả phòng trễ và ngưỡng xuất hủy phải là số tiền hợp lệ (ví dụ 50.000).");
            return;
        }
        if (storeNameField.getText().isBlank()) {
            Dialogs.warn(this, "Tên cửa hàng không được để trống.");
            return;
        }
        AppSettings.update((Integer) vatSpinner.getValue(), (Integer) lowStockThresholdSpinner.getValue(),
            (Integer) expiryDaysSpinner.getValue(), lateFee, (Integer) returnDaysSpinner.getValue(), writeOffLimit);
        loadFromSettings();
        AuditTrail.log("Sửa", "Cài đặt hệ thống (VAT, tồn thấp, hạn dùng, phí trả trễ)");
        Dialogs.success(this, "Đã lưu cài đặt.");
    }

    private void navigateBack() {
        Dialogs.showMessageDialog(this,
            "Chức năng quay lại sẽ được triển khai khi có navigation system",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void navigateToHome() {
        Dialogs.showMessageDialog(this,
            "Chức năng về màn hình chính sẽ được triển khai khi có navigation system",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected String helpTitle() {
        return "Cài đặt hệ thống";
    }

    @Override
    protected String helpText() {
        return "Hướng dẫn cài đặt:\n\n"+
               "【 Thông tin cửa hàng 】\n"+
               "Cập nhật thông tin in trên hóa đơn\n\n"+
               "【 Cài đặt hệ thống 】\n"+
               "• Múi giờ: Ảnh hưởng đến báo cáo và lịch\n"+
               "• Ngôn ngữ: Ngôn ngữ hiển thị giao diện\n"+
               "• Tiền tệ: Đơn vị tiền tệ mặc định\n\n"+
               "【 Cài đặt in ấn 】\n"+
               "• Chọn máy in đã cài đặt trên Windows\n"+
               "• Khổ giấy: 80mm hoặc 58mm cho máy in nhiệt\n"+
               "• In thử: Kiểm tra kết nối trước khi lưu\n\n"+
               "【 Thông báo 】\n"+
               "Bật/tắt các loại cảnh báo tự động\n\n"+
               "Mẹo: Luôn in thử trước khi lưu cài đặt máy in!";
    }
}

