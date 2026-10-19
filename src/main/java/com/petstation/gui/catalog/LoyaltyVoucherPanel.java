package com.petstation.gui.catalog;

import com.petstation.gui.components.TabbedCatalogPanel;

import javax.swing.JPanel;

public final class LoyaltyVoucherPanel extends TabbedCatalogPanel {
    public LoyaltyVoucherPanel() {
        super(
                new String[] {"Quy tắc điểm & hạng", "Voucher"},
                new JPanel[] {new LoyaltyRulesPanel(), new VoucherCatalogPanel()});
    }
}
