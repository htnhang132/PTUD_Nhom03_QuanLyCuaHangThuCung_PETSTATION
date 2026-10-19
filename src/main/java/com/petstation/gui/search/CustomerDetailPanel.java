package com.petstation.gui.search;

import static com.petstation.dao.SqlDatabase.*;

import com.petstation.gui.components.BasePanel;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.processing.sales.PrintInvoicePanel;
import com.petstation.service.CustomerHistoryService;
import com.petstation.service.SalesService;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Customer history is scoped by the selected customer's identity in SQL. */
public final class CustomerDetailPanel extends BasePanel {
    public CustomerDetailPanel(
            String code, String name, String phone, String address, String tier, String points) {
        setLayout(new BorderLayout(0, 10));
        add(
                sectionTitle(
                        name + " · " + code + " · " + phone + " · " + tier + " · " + points
                                + " điểm"),
                BorderLayout.NORTH);
        long customer =
                com.petstation.service.CatalogService.id("khach_hang", "ma_khach_hang", code);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(
                "Thú cưng",
                table(
                        new String[] {
                            "Mã thú",
                            "Tên",
                            "Loài",
                            "Giống",
                            "Ngày sinh",
                            "Cân nặng",
                            "Tiêm chủng",
                            "Lưu ý sức khỏe"
                        },
                        CustomerHistoryService.pets(customer)));
        List<Object[]> invoices = CustomerHistoryService.invoices(customer);
        JScrollPane invoiceTable =
                table(
                        new String[] {
                            "Số hóa đơn", "Ngày", "Tổng tiền", "Thanh toán", "Trạng thái"
                        },
                        invoices);
        JPanel orders = transparent(new BorderLayout(0, 8));
        orders.add(invoiceTable, BorderLayout.CENTER);
        StyledButton preview = new StyledButton("Xem / In hóa đơn", StyledButton.Variant.SECONDARY);
        preview.addActionListener(
                e -> {
                    JTable t = tableOf(invoiceTable);
                    if (t.getSelectedRow() < 0) {
                        Dialogs.warn(this, "Chọn hóa đơn cần xem.");
                        return;
                    }
                    String no =
                            text(
                                    t.getModel()
                                            .getValueAt(
                                                    t.convertRowIndexToModel(t.getSelectedRow()),
                                                    0));
                    JTextArea text =
                            new JTextArea(
                                    PrintInvoicePanel.render(SalesService.receipt(no)), 28, 48);
                    text.setEditable(false);
                    StyledButton print = new StyledButton("In", StyledButton.Variant.PRIMARY);
                    print.addActionListener(
                            event -> {
                                try {
                                    text.print();
                                } catch (java.awt.print.PrinterException exception) {
                                    Dialogs.error(this, exception.getMessage());
                                }
                            });
                    JPanel p = new JPanel(new BorderLayout());
                    p.add(new JScrollPane(text), BorderLayout.CENTER);
                    p.add(print, BorderLayout.SOUTH);
                    Dialogs.showMessageDialog(this, p, "Hóa đơn " + no, JOptionPane.PLAIN_MESSAGE);
                });
        orders.add(preview, BorderLayout.SOUTH);
        tabs.addTab("Hóa đơn", orders);
        tabs.addTab(
                "Spa",
                table(
                        new String[] {"Mã phiếu", "Thú cưng", "Ngày", "Tổng tiền", "Trạng thái"},
                        CustomerHistoryService.spaHistory(customer)));
        tabs.addTab(
                "Lưu trú",
                table(
                        new String[] {
                            "Hợp đồng",
                            "Thú cưng",
                            "Phòng",
                            "Ngày vào",
                            "Ngày ra dự kiến",
                            "Trạng thái"
                        },
                        CustomerHistoryService.hotelHistory(customer)));
        tabs.addTab(
                "Điểm",
                table(
                        new String[] {"Thời gian", "Thay đổi", "Số dư sau", "Lý do"},
                        CustomerHistoryService.pointsHistory(customer)));
        tabs.addTab(
                "Voucher cá nhân",
                table(
                        new String[] {
                            "Mã voucher",
                            "Mệnh giá",
                            "Hạn dùng",
                            "Đã dùng",
                            "Số lượng",
                            "Trạng thái"
                        },
                        CustomerHistoryService.vouchers(customer)));
        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane table(String[] columns, List<Object[]> rows) {
        DefaultTableModel model =
                new DefaultTableModel(rows.toArray(Object[][]::new), columns) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
        return styledTable(model);
    }
}
