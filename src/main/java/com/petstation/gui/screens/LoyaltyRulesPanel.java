package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.Money;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.navigation.AppSettings;
import com.petstation.gui.navigation.AuditTrail;

/** Tích điểm & Voucher ▸ Quy tắc điểm: bao nhiêu đồng được 1 điểm, 1 điểm đổi bao nhiêu đồng, ngưỡng hạng thành viên. */
public final class LoyaltyRulesPanel extends BasePanel {
    private final JTextField earn = field();
    private final JTextField redeem = field();
    private final JTextField[] tiers = {field(), field(), field(), field()};
    private static final String[] TIER_NAMES = {"Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
    private final JLabel message = new JLabel(" ");

    public LoyaltyRulesPanel() {
        setLayout(new BorderLayout(0, 10));
        add(sectionTitle("Quy tắc tích điểm và hạng thành viên"), BorderLayout.NORTH);
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), new EmptyBorder(16, 18, 16, 18)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 0, 6, 12);
        c.anchor = GridBagConstraints.WEST;
        int y = 0;
        row(card, c, y++, "Chi tiêu bao nhiêu đồng được 1 điểm", earn, "₫");
        row(card, c, y++, "1 điểm đổi được bao nhiêu đồng", redeem, "₫");
        JLabel head = new JLabel("Hạng thành viên (theo tổng chi tiêu từ):");
        head.setFont(AppColors.font(Font.BOLD, 13));
        c.gridx = 0; c.gridy = y++; c.gridwidth = 3; c.insets = new Insets(16, 0, 4, 0);
        card.add(head, c);
        c.gridwidth = 1; c.insets = new Insets(6, 0, 6, 12);
        for (int i = 0; i < 4; i++) row(card, c, y++, "Hạng " + TIER_NAMES[i], tiers[i], "₫");
        message.setFont(AppColors.font(Font.BOLD, 12));
        c.gridx = 0; c.gridy = y++; c.gridwidth = 3;
        card.add(message, c);
        StyledButton save = new StyledButton("Lưu quy tắc", StyledButton.Variant.SUCCESS);
        save.addActionListener(e -> save());
        c.gridy = y; c.fill = GridBagConstraints.NONE;
        card.add(save, c);
        JPanel holder = transparent(new BorderLayout());
        holder.add(card, BorderLayout.NORTH);
        add(holder, BorderLayout.CENTER);
        load();
    }

    private static JTextField field() {
        JTextField t = new JTextField(14);
        t.setFont(AppColors.font(Font.PLAIN, 13));
        t.setHorizontalAlignment(JTextField.RIGHT);
        return t;
    }

    private void row(JPanel card, GridBagConstraints c, int y, String label, JTextField f, String unit) {
        c.gridy = y; c.gridx = 0; card.add(new JLabel(label), c);
        c.gridx = 1; card.add(f, c);
        c.gridx = 2; card.add(new JLabel(unit), c);
    }

    private void load() {
        earn.setText(Money.format(AppSettings.vndPerEarnedPoint()));
        redeem.setText(Money.format(AppSettings.vndPerRedeemedPoint()));
        long[] t = AppSettings.tierSpend();
        for (int i = 0; i < 4; i++) tiers[i].setText(Money.format(t[i]));
    }

    private void save() {
        long e = Money.parse(earn.getText());
        long r = Money.parse(redeem.getText());
        long[] t = new long[4];
        for (int i = 0; i < 4; i++) t[i] = Money.parse(tiers[i].getText());
        if (e <= 0 || r <= 0) { Dialogs.warn(this, "Số đồng cho 1 điểm và giá trị 1 điểm phải lớn hơn 0."); return; }
        for (int i = 0; i < 4; i++) {
            if (t[i] < 0) { Dialogs.warn(this, "Ngưỡng hạng " + TIER_NAMES[i] + " không hợp lệ."); return; }
            if (i > 0 && t[i] <= t[i - 1]) { Dialogs.warn(this, "Ngưỡng hạng phải tăng dần: " + TIER_NAMES[i - 1] + " < " + TIER_NAMES[i] + "."); return; }
        }
        AppSettings.updateLoyalty(e, r, t);
        load();
        AuditTrail.log("Sửa", "Quy tắc tích điểm và hạng thành viên");
        message.setForeground(AppColors.GREEN);
        message.setText("Đã lưu quy tắc.");
    }

    @Override protected String helpTitle() { return "Quy tắc điểm"; }
    @Override protected String helpText() { return "Điểm của khách tự cộng/trừ theo hóa đơn, không sửa tay."; }
}
