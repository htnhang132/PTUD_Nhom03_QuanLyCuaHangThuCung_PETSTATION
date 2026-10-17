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

/** Xử lý ▸ Dịch vụ Spa: màn đầu tiên là danh sách phiếu đang chạy. Bấm phiếu mở đúng bước hiện tại. */
public class SpaTicketsPanel extends BasePanel {
    private static final String[] COLS = {"Mã phiếu", "Giờ hẹn", "Thú cưng", "Chủ", "Dịch vụ", "Bước hiện tại", "Trạng thái"};
    private static final Object[][] ROWS = {
        {"SP001", "09:00", "Mochi (Poodle)", "Nguyễn Văn A", "Cắt tỉa lông", "Tiến độ", "Đang làm"},
        {"SP002", "10:00", "Coco (Corgi)", "Trần Thị B", "Tắm + sấy lông", "Tiếp nhận", "Chờ"},
        {"SP003", "13:30", "Luna (Shih Tzu)", "Hoàng E", "Full package", "Lịch hẹn", "Đã hẹn"}
    };

    public SpaTicketsPanel(Consumer<ScreenId> navigator) {
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("Phiếu đang chạy", "3", "", AppColors.BLUE));
        stats.add(statCard("Đang làm", "1", "", AppColors.ORANGE));
        stats.add(statCard("Chờ tiếp nhận", "2", "", AppColors.GREEN));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        StyledButton create = new StyledButton("+ Tạo lịch hẹn", StyledButton.Variant.PRIMARY);
        create.addActionListener(e -> navigator.accept(ScreenId.SPA_SCHEDULE));
        actions.add(create);

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
                    navigator.accept(stepOf(String.valueOf(table.getValueAt(row, 5))));
                }
            }
        });
        add(scroll, BorderLayout.CENTER);
    }

    static ScreenId stepOf(String step) {
        return switch (step) {
            case "Tiếp nhận" -> ScreenId.SPA_INTAKE;
            case "Chọn dịch vụ" -> ScreenId.SPA_SERVICE_SELECTION;
            case "Tiến độ" -> ScreenId.SPA_PROGRESS;
            case "Đóng phiếu" -> ScreenId.SPA_CLOSE;
            default -> ScreenId.SPA_SCHEDULE;
        };
    }
}
