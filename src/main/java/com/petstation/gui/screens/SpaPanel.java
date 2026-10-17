package com.petstation.gui.screens;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.RoundedPanel;
import com.petstation.gui.components.StyledButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SpaPanel extends BasePanel {

    private final java.util.function.Consumer<com.petstation.gui.navigation.ScreenId> navigator;

    public SpaPanel(java.util.function.Consumer<com.petstation.gui.navigation.ScreenId> navigator) {
        this.navigator = navigator;
        JPanel topRow = new JPanel(new GridLayout(1, 4, 16, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        topRow.add(statCard("Lịch hôm nay", "8", "", AppColors.BLUE));
        topRow.add(statCard("Đang thực hiện", "2", "", AppColors.ORANGE));
        topRow.add(statCard("Hoàn thành", "4", "", AppColors.GREEN));
        topRow.add(statCard("Doanh thu Spa", "1.850.000₫", "", AppColors.RED_BRAND));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));
        header.add(topRow, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        StyledButton openTicket = new StyledButton("Mở phiếu ▸ Xử lý Spa", StyledButton.Variant.PRIMARY);
        openTicket.setEnabled(false);
        btnRow.add(openTicket);

        JPanel topArea = new JPanel(new BorderLayout(0, 12));
        topArea.setOpaque(false);
        topArea.add(header, BorderLayout.NORTH);
        topArea.add(btnRow, BorderLayout.SOUTH);
        add(topArea, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã lịch", "Giờ", "Thú cưng", "Chủ", "Dịch vụ", "NV thực hiện", "Giá", "Trạng thái"};
        Object[][] data = {
            {"#SP001","09:00","Mochi (Poodle)","Nguyễn Văn A","Cắt tỉa lông","Lan","250.000₫","Hoàn thành"},
            {"#SP002","10:00","Coco (Corgi)",  "Trần Thị B",  "Tắm + sấy lông","Hoa","180.000₫","Đang làm"},
            {"#SP003","10:30","Max (Labrador)","Lê Văn C",    "Cắt móng tay","Lan","80.000₫","Hoàn thành"},
            {"#SP004","11:00","Kitty (Persian)","Phạm D",     "Vệ sinh tai mắt","Mai","120.000₫","Hoàn thành"},
            {"#SP005","13:30","Luna (Shih Tzu)","Hoàng E",    "Full package","Hoa","480.000₫","Chờ"},
            {"#SP006","14:00","Rex (GSD)",     "Vũ Hải",      "Tắm khử mùi","Lan","200.000₫","Chờ"},
            {"#SP007","15:00","Bella (Pom)",   "Đặng H",      "Cắt tỉa lông","Mai","230.000₫","Chờ"},
            {"#SP008","16:00","Tiger (Siamese)","Bùi K",      "Tắm + cắt móng","Hoa","150.000₫","Chờ"},
        };
        JScrollPane scroll = styledTable(cols, data);
        JTable table = tableOf(scroll);
        table.getSelectionModel().addListSelectionListener(e -> openTicket.setEnabled(table.getSelectedRow() >= 0));
        openTicket.addActionListener(e -> navigator.accept(com.petstation.gui.navigation.ScreenId.SPA));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) navigator.accept(com.petstation.gui.navigation.ScreenId.SPA);
            }
        });
        add(scroll, BorderLayout.CENTER);
    }
}

