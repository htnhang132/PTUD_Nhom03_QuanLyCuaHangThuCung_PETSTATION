package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.TableStyler;

/**
 * Tìm kiếm ▸ Lịch sử khách hàng. Bước 1: tìm và chọn khách. Bước 2: xem hóa đơn, phiếu Spa,
 * lưu trú, thú cưng, điểm của khách đó (chỉ đọc).
 */
public final class CustomerHistoryPanel extends BasePanel {
    private static final String PICK = "pick";
    private static final String DETAIL = "detail";
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final JPanel detailHolder = new JPanel(new BorderLayout());
    private final JTable table;
    private DefaultTableModel model;

    private static void reloadCustomers(DefaultTableModel m) {
        m.setRowCount(0);
        for (Object[] r : com.petstation.gui.data.DemoStore.customers()) {
            m.addRow(new Object[]{r[0], r[1], r[2], r[4], r[6], r[7]});
        }
    }
    private final StyledButton view = new StyledButton("Xem lịch sử ▸", StyledButton.Variant.PRIMARY);

    public CustomerHistoryPanel() {
        setLayout(new BorderLayout());
        root.setOpaque(false);

        // ── Bước 1: chọn khách
        JPanel pick = new JPanel(new BorderLayout(0, 12));
        pick.setOpaque(false);
        JLabel title = sectionTitle("Chọn khách hàng cần xem lịch sử");
        JTextField search = searchBar("Nhập tên, SĐT hoặc mã khách...");
        view.setEnabled(false);
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.add(title, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(search);
        right.add(view);
        bar.add(right, BorderLayout.EAST);
        pick.add(bar, BorderLayout.NORTH);

        String[] cols = {"Mã KH", "Họ và tên", "Số điện thoại", "Địa chỉ", "Hạng thẻ", "Điểm"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reloadCustomers(model);
        this.model = model;
        JScrollPane scroll = styledTable(model);
        table = tableOf(scroll);
        TableStyler.apply(table);
        pick.add(scroll, BorderLayout.CENTER);
        JLabel tip = new JLabel("Bấm 2 lần vào một dòng hoặc chọn dòng rồi bấm \"Xem lịch sử\".");
        tip.setFont(AppColors.font(java.awt.Font.PLAIN, 12));
        tip.setForeground(AppColors.TEXT_GRAY);
        pick.add(tip, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> view.setEnabled(table.getSelectedRow() >= 0));
        view.addActionListener(e -> openSelected());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) openSelected();
            }
        });

        // ── Bước 2: chi tiết
        JPanel detail = new JPanel(new BorderLayout(0, 8));
        detail.setOpaque(false);
        StyledButton back = new StyledButton("◀ Chọn khách khác", StyledButton.Variant.GHOST);
        back.addActionListener(e -> cards.show(root, PICK));
        JPanel backBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backBar.setOpaque(false);
        backBar.setBorder(new EmptyBorder(0, 0, 4, 0));
        backBar.add(back);
        detail.add(backBar, BorderLayout.NORTH);
        detail.add(detailHolder, BorderLayout.CENTER);
        detailHolder.setOpaque(false);

        root.add(pick, PICK);
        root.add(detail, DETAIL);
        add(root, BorderLayout.CENTER);
    }

    private void openSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int m = table.convertRowIndexToModel(row);
        java.util.function.IntFunction<String> v = c -> String.valueOf(table.getModel().getValueAt(m, c));
        detailHolder.removeAll();
        detailHolder.add(new CustomerDetailPanel(v.apply(0), v.apply(1), v.apply(2), v.apply(3), v.apply(4), v.apply(5)), BorderLayout.CENTER);
        detailHolder.revalidate();
        detailHolder.repaint();
        cards.show(root, DETAIL);
    }

    @Override public void onShow() { reloadCustomers(model); cards.show(root, PICK); table.clearSelection(); }

    @Override protected String helpTitle() { return "Lịch sử khách hàng"; }

    @Override protected String helpText() {
        return "Bước 1: tìm và chọn khách hàng.\nBước 2: xem hóa đơn, phiếu Spa, thú cưng, voucher của khách (chỉ đọc).";
    }
}
