package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.gui.chung.thanhphan.DinhDangBang;

import org.junit.jupiter.api.Test;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

class DinhDangBangTest {
    @Test
    void cotTienDungDoubleLonVanCanPhaiDongNhat() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    DefaultTableModel moHinh =
                            DinhDangBang.taoMoHinhChiDoc(
                                    new Object[][] {
                                        {"NCC0003", "Orijen", 14500000.0d, 6000000.0d, 8500000.0d},
                                        {"NCC0004", "PetShop", 9600000.0d, 5600000.0d, 4000000.0d}
                                    },
                                    new String[] {
                                        "Mã NCC",
                                        "Nhà cung cấp",
                                        "Tổng nhập",
                                        "Đã trả hiện tại",
                                        "Dư nợ hiện tại"
                                    });
                    JTable bang = new JTable(moHinh);
                    DinhDangBang.apDung(bang);

                    var renderer = bang.getDefaultRenderer(Object.class);
                    Component cLon =
                            renderer.getTableCellRendererComponent(
                                    bang, moHinh.getValueAt(0, 2), false, false, 0, 2);
                    assertThat(cLon).isInstanceOf(JLabel.class);
                    assertThat(((JLabel) cLon).getHorizontalAlignment())
                            .isEqualTo(SwingConstants.RIGHT);
                    assertThat(((JLabel) cLon).getText()).isEqualTo("14.500.000₫");

                    Component cNho =
                            renderer.getTableCellRendererComponent(
                                    bang, moHinh.getValueAt(1, 2), false, false, 1, 2);
                    assertThat(cNho).isInstanceOf(JLabel.class);
                    assertThat(((JLabel) cNho).getHorizontalAlignment())
                            .isEqualTo(SwingConstants.RIGHT);
                    assertThat(((JLabel) cNho).getText()).isEqualTo("9.600.000₫");
                });
    }
}
