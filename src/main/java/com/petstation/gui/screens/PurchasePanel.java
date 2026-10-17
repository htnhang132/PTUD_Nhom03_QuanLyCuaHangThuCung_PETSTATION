package com.petstation.gui.screens;

import com.petstation.gui.components.StyledButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PurchasePanel extends BasePanel {
    public PurchasePanel() {
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));
        top.add(hbox(searchBar("Tìm phiếu nhập...")), BorderLayout.CENTER);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new StyledButton("+ Tạo phiếu nhập", StyledButton.Variant.PRIMARY));
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Mã phiếu","Ngày nhập","Nhà cung cấp","Số mặt hàng","Tổng tiền","Trạng thái","Ghi chú"};
        Object[][] data = {
            {"#PN0021","20/12/2024","Royal Canin VN","5","8.550.000₫","Đã nhập","—"},
            {"#PN0020","18/12/2024","Mars Petcare VN","3","2.700.000₫","Đã nhập","—"},
            {"#PN0019","15/12/2024","Orijen Import","2","5.200.000₫","Đã nhập","—"},
            {"#PN0018","12/12/2024","Phụ kiện Thú Cưng","8","3.420.000₫","Đã nhập","Thiếu 2 SP"},
            {"#PN0017","10/12/2024","PetShop Thú Y","4","1.860.000₫","Đã nhập","—"},
        };
        add(styledTable(cols, data), BorderLayout.CENTER);
    }
}

