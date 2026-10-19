package com.petstation.gui.catalog;

import com.petstation.gui.components.BasePanel;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.service.CatalogService;
import com.petstation.util.Money;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Sản phẩm ▸ Lịch sử giá: chỉ đọc, tự thêm dòng mỗi khi giá bán đổi ở tab Danh sách. */
public final class PriceHistoryPanel extends BasePanel implements ToolbarAware {
    private final DefaultTableModel model =
            new DefaultTableModel(
                    new String[] {
                        "Thời gian", "Mã SP", "Tên sản phẩm", "Giá cũ", "Giá mới", "Người thay đổi"
                    },
                    0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

    public PriceHistoryPanel() {
        setLayout(new BorderLayout(0, 10));
        JTextField search = searchBar("Tìm theo mã hoặc tên sản phẩm...");
        JScrollPane scroll = styledTable(model);
        JTable table = tableOf(scroll);
        bindFilter(search, table);
        javax.swing.JPanel top = transparent(new BorderLayout(10, 0));
        top.add(sectionTitle("Lịch sử thay đổi giá bán"), BorderLayout.WEST);
        top.add(search, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        reload();
        CatalogService.addPriceListener(() -> SwingUtilities.invokeLater(this::reload));
    }

    private void reload() {
        model.setRowCount(0);
        java.util.List<Object[]> rows = new java.util.ArrayList<>(CatalogService.priceHistory());

        for (Object[] r : rows) {
            model.addRow(
                    new Object[] {
                        r[0],
                        r[1],
                        r[2],
                        String.valueOf(r[3]).isBlank()
                                ? "—"
                                : Money.format(Money.parse(String.valueOf(r[3]))),
                        Money.format(Money.parse(String.valueOf(r[4]))),
                        r[5]
                    });
        }
    }

    @Override
    public void onShow() {
        reload();
    }

    @Override
    protected String helpTitle() {
        return "Lịch sử giá";
    }

    @Override
    protected String helpText() {
        return "Chỉ xem. Giá bán đổi ở tab Danh sách sản phẩm sẽ tự ghi vào đây.";
    }
}
