package com.petstation.gui.catalog;

import com.petstation.gui.components.TabbedCatalogPanel;

import javax.swing.JPanel;

public final class ProductCatalogPanel extends TabbedCatalogPanel {
    public ProductCatalogPanel() {
        super(
                new String[] {"Danh sách sản phẩm", "Lịch sử giá"},
                new JPanel[] {new ProductListPanel(), new PriceHistoryPanel()});
    }
}
