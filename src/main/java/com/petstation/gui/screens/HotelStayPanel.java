package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.navigation.ScreenId;

/** Xử lý ▸ Pet Hotel: màn đầu tiên là danh sách thú đang lưu trú. Quá hạn trả thì cảnh báo tại đây. */
public class HotelStayPanel extends BasePanel {
    private static final String[] COLS = {"Mã lưu trú", "Thú cưng", "Chủ", "Phòng", "Ngày vào", "Dự kiến trả", "Trạng thái"};
    private static final Object[][] ROWS = {
        {"LT001", "Mochi (Poodle)", "Nguyễn Văn A", "P01", "03/12", "06/12", "Đang lưu trú"},
        {"LT002", "Max (Labrador)", "Lê Văn C", "P04", "02/12", "05/12", "Quá hạn trả"},
        {"LT003", "Luna (Shih Tzu)", "Hoàng E", "P06", "05/12", "09/12", "Đang lưu trú"}
    };

    public HotelStayPanel(Consumer<ScreenId> navigator) {
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("Đang lưu trú", "3", "", AppColors.BLUE));
        stats.add(statCard("Quá hạn trả", "1", "", AppColors.RED_BRAND));
        stats.add(statCard("Trả phòng hôm nay", "1", "", AppColors.ORANGE));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        StyledButton book = new StyledButton("+ Đặt phòng", StyledButton.Variant.PRIMARY);
        book.addActionListener(e -> navigator.accept(ScreenId.HOTEL_BOOKING));
        actions.add(book);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 12, 0));
        top.add(stats, BorderLayout.NORTH);
        top.add(actions, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JScrollPane scroll = styledTable(COLS, ROWS);
        JTable table = tableOf(scroll);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (e.getClickCount() == 2 && row >= 0) {
                    boolean overdue = "Quá hạn trả".equals(String.valueOf(table.getValueAt(row, 6)));
                    navigator.accept(overdue ? ScreenId.CHECKOUT : ScreenId.HOTEL_JOURNAL);
                }
            }
        });
        add(scroll, BorderLayout.CENTER);
    }
}
