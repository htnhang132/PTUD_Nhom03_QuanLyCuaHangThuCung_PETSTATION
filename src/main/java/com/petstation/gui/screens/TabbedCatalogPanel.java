package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.ToolbarAware;

/** Màn có nhiều tab (ví dụ Sản phẩm + Lịch sử giá). Chuyển onShow xuống các tab con. */
public final class TabbedCatalogPanel extends BasePanel {
    private final JTabbedPane tabs = new JTabbedPane();
    private final JPanel[] children;

    public TabbedCatalogPanel(String[] titles, JPanel[] children) {
        this.children = children;
        setLayout(new BorderLayout());
        tabs.setFont(AppColors.font(Font.BOLD, 13));
        for (int i = 0; i < titles.length; i++) {
            children[i].setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
            tabs.addTab(titles[i], children[i]);
        }
        tabs.addChangeListener(e -> onShow());
        add(tabs, BorderLayout.CENTER);
    }

    @Override public void onShow() {
        if (tabs.getSelectedComponent() instanceof ToolbarAware t) t.onShow();
    }

    @Override protected String helpTitle() { return "Danh mục"; }
    @Override protected String helpText() { return "Chuyển giữa các tab ở phía trên."; }
}
