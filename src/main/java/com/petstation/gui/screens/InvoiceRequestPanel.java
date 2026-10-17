package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.data.ApprovalStore;
import com.petstation.gui.navigation.AuditTrail;
import com.petstation.gui.navigation.ScreenId;

/** Yêu cầu sửa/hủy hóa đơn đã chốt: gửi sang Phê duyệt, không sửa trực tiếp. */
public final class InvoiceRequestPanel extends BasePanel implements ToolbarAware {
    private final Consumer<ScreenId> navigator;
    private final JTextField invoice = new JTextField();
    private final JComboBox<String> type = new JComboBox<>(new String[]{"Sửa hóa đơn", "Hủy hóa đơn"});
    private final JTextArea reason = new JTextArea(3, 30);
    private final JTextArea change = new JTextArea(4, 30);

    public InvoiceRequestPanel(Consumer<ScreenId> navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 12));
        add(sectionTitle("Yêu cầu sửa / hủy hóa đơn"), BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), new EmptyBorder(16, 18, 16, 18)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(4, 0, 2, 0);
        int y = 0;
        invoice.setEditable(false);
        y = add(card, c, y, "Hóa đơn", invoice);
        y = add(card, c, y, "Loại yêu cầu", type);
        y = add(card, c, y, "Lý do *", new JScrollPane(reason));
        y = add(card, c, y, "Nội dung muốn sửa (bắt buộc khi chọn Sửa)", new JScrollPane(change));
        JLabel note = new JLabel("Hóa đơn đã thanh toán bị khóa. Yêu cầu sẽ gửi Cửa hàng trưởng duyệt; khi duyệt hệ thống tự thực hiện.");
        note.setFont(AppColors.font(Font.PLAIN, 12));
        note.setForeground(AppColors.TEXT_GRAY);
        c.gridy = y++; c.insets = new Insets(10, 0, 4, 0); card.add(note, c);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        StyledButton cancel = new StyledButton("Quay lại Tìm kiếm", StyledButton.Variant.GHOST);
        StyledButton send = new StyledButton("Gửi phê duyệt", StyledButton.Variant.PRIMARY);
        cancel.addActionListener(e -> navigator.accept(ScreenId.ORDERS));
        send.addActionListener(e -> send());
        buttons.add(cancel);
        buttons.add(send);
        c.gridy = y; c.insets = new Insets(6, 0, 0, 0); card.add(buttons, c);
        JPanel holder = transparent(new BorderLayout());
        holder.add(card, BorderLayout.NORTH);
        add(holder, BorderLayout.CENTER);
    }

    private int add(JPanel p, GridBagConstraints c, int y, String label, java.awt.Component comp) {
        JLabel l = new JLabel(label);
        l.setFont(AppColors.font(Font.PLAIN, 12));
        l.setForeground(AppColors.TEXT_GRAY);
        c.gridy = y++; c.insets = new Insets(8, 0, 1, 0); p.add(l, c);
        c.gridy = y++; c.insets = new Insets(0, 0, 0, 0); p.add(comp, c);
        return y;
    }

    private void send() {
        if (invoice.getText().isBlank()) { Dialogs.warn(this, "Chưa chọn hóa đơn. Hãy vào Tìm kiếm ▸ Hóa đơn, chọn hóa đơn rồi bấm \"Yêu cầu sửa/hủy\"."); return; }
        if (reason.getText().isBlank()) { Dialogs.warn(this, "Cần nhập lý do."); reason.requestFocusInWindow(); return; }
        boolean edit = type.getSelectedIndex() == 0;
        if (edit && change.getText().isBlank()) { Dialogs.warn(this, "Hãy mô tả nội dung cần sửa."); change.requestFocusInWindow(); return; }
        String code = invoice.getText();
        ApprovalStore.submit(ApprovalStore.Kind.INVOICE, AuditTrail.user(), reason.getText().trim(),
            "Hóa đơn " + code + " (đã thanh toán, đang bị khóa)",
            edit ? change.getText().trim() : "Hủy hóa đơn " + code,
            () -> AuditTrail.log(edit ? "Sửa" : "Hủy", "Hóa đơn " + code + " theo yêu cầu đã duyệt"));
        Dialogs.success(this, "Đã gửi yêu cầu " + (edit ? "sửa" : "hủy") + " hóa đơn " + code + " sang Phê duyệt.");
        reason.setText("");
        change.setText("");
        navigator.accept(ScreenId.ORDERS);
    }

    @Override public void onShow() { invoice.setText(ApprovalStore.selectedInvoice); }

    @Override protected String helpTitle() { return "Yêu cầu sửa/hủy hóa đơn"; }
    @Override protected String helpText() { return "Chọn hóa đơn ở Tìm kiếm, nhập lý do rồi gửi phê duyệt."; }
}
