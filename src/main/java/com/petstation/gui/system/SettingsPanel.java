package com.petstation.gui.system;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.BasePanel;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.service.AuditService;
import com.petstation.service.SettingsService;

import java.awt.*;
import java.util.*;

import javax.print.*;
import javax.swing.*;

/**
 * Store identity, business rules and actual Windows printer choices are persisted in SQL Server.
 */
public final class SettingsPanel extends BasePanel implements ToolbarAware {
    private final Map<String, JTextField> inputs = new LinkedHashMap<>();
    private final JComboBox<String> printer = new JComboBox<>();
    private final boolean editable;

    public SettingsPanel(boolean editable) {
        this.editable = editable;
        setLayout(new BorderLayout(0, 12));
        add(sectionTitle("Cài đặt hệ thống"), BorderLayout.NORTH);
        JPanel groups = new JPanel(new GridLayout(1, 2, 20, 0));
        groups.setOpaque(false);
        JPanel store = group("Thông tin cửa hàng"), business = group("Quy tắc nghiệp vụ");
        field(store, "STORE_NAME", "Tên cửa hàng");
        field(store, "STORE_ADDRESS", "Địa chỉ");
        field(store, "STORE_PHONE", "Số điện thoại");
        field(store, "STORE_EMAIL", "Email");
        field(store, "STORE_WEBSITE", "Website");
        field(store, "STORE_TAX_CODE", "Mã số thuế");
        for (PrintService service : PrintServiceLookup.lookupPrintServices(null, null))
            printer.addItem(service.getName());
        if (printer.getItemCount() == 0) printer.addItem("Không có máy in");
        printer.setEnabled(editable);
        store.add(new JLabel("Máy in hóa đơn"));
        store.add(printer);
        StyledButton test = new StyledButton("In thử", StyledButton.Variant.GHOST);
        test.addActionListener(
                e -> {
                    JTextArea page =
                            new JTextArea(
                                    SettingsService.get("STORE_NAME")
                                            + "\n"
                                            + SettingsService.get("STORE_ADDRESS")
                                            + "\nIn thử Pet Station\n"
                                            + java.time.LocalDateTime.now());
                    try {
                        page.print();
                    } catch (java.awt.print.PrinterException exception) {
                        Dialogs.error(this, exception.getMessage());
                    }
                });
        store.add(test);
        field(business, "VAT_PERCENT", "Thuế VAT (%)");
        field(business, "LOW_STOCK_THRESHOLD", "Ngưỡng tồn thấp");
        field(business, "EXPIRY_WARN_DAYS", "Cảnh báo hạn dùng (ngày)");
        field(business, "LATE_CHECKOUT_FEE", "Phí trả phòng trễ / ngày (đồng)");
        field(business, "RETURN_WINDOW_DAYS", "Thời hạn đổi trả (ngày)");
        field(business, "WRITEOFF_APPROVAL_LIMIT", "Giá trị xuất hủy cần duyệt (đồng)");
        groups.add(store);
        groups.add(business);
        JScrollPane scroll = new JScrollPane(groups);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
        JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT));
        StyledButton reload = new StyledButton("Tải lại", StyledButton.Variant.GHOST);
        reload.addActionListener(e -> load());
        actions.add(reload);
        StyledButton save = new StyledButton("Lưu cài đặt", StyledButton.Variant.PRIMARY);
        save.setEnabled(editable);
        save.addActionListener(e -> onSave());
        actions.add(save);
        add(actions, BorderLayout.SOUTH);
        load();
    }

    private JPanel group(String title) {
        JPanel panel = card(new GridLayout(0, 1, 5, 5));
        panel.add(sectionTitle(title));
        return panel;
    }

    private void field(JPanel panel, String key, String label) {
        panel.add(new JLabel(label));
        JTextField input = new JTextField(24);
        input.setEnabled(editable);
        inputs.put(key, input);
        panel.add(input);
    }

    private void load() {
        for (var entry : inputs.entrySet())
            entry.getValue().setText(SettingsService.get(entry.getKey()));
        String selected = SettingsService.get("PRINTER");
        if (!selected.isBlank()) printer.setSelectedItem(selected);
    }

    @Override
    public void onSave() {
        if (!editable) return;
        try {
            if (inputs.get("STORE_NAME").getText().isBlank())
                throw new IllegalArgumentException("Cần tên cửa hàng.");
            int vat = Integer.parseInt(inputs.get("VAT_PERCENT").getText()),
                    low = Integer.parseInt(inputs.get("LOW_STOCK_THRESHOLD").getText()),
                    expiry = Integer.parseInt(inputs.get("EXPIRY_WARN_DAYS").getText()),
                    returns = Integer.parseInt(inputs.get("RETURN_WINDOW_DAYS").getText());
            long late = Long.parseLong(inputs.get("LATE_CHECKOUT_FEE").getText()),
                    limit = Long.parseLong(inputs.get("WRITEOFF_APPROVAL_LIMIT").getText());
            transaction(
                    c -> {
                        SettingsService.update(vat, low, expiry, late, returns, limit);
                        for (var entry : inputs.entrySet())
                            if (entry.getKey().startsWith("STORE_"))
                                SettingsService.put(
                                        entry.getKey(), entry.getValue().getText().trim());
                        SettingsService.put(
                                "PRINTER",
                                "Không có máy in".equals(printer.getSelectedItem())
                                        ? ""
                                        : String.valueOf(printer.getSelectedItem()));
                        AuditService.log("Sửa", "Cài đặt hệ thống");
                        return null;
                    });
            Dialogs.success(this, "Đã lưu cài đặt.");
        } catch (RuntimeException exception) {
            Dialogs.warn(this, exception.getMessage());
        }
    }

    @Override
    public void onShow() {
        load();
    }
}
