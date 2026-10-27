package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.*;

import com.petstation.gui.components.common.BangChiTietHoaDon;

import org.junit.jupiter.api.Test;

import javax.swing.*;

class BangChiTietHoaDonTest {
    @Test
    void tenDaiDuChieuCaoTruocKhiVeVaKhiDoiBeRong() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    JTable bang =
                            new JTable(
                                    new Object[][] {
                                        {
                                            "Thức ăn dinh dưỡng dành cho thú cưng cần chăm sóc đặc"
                                                    + " biệt với tên sản phẩm rất dài",
                                            1,
                                            100000,
                                            100000
                                        }
                                    },
                                    new String[] {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"});
                    BangChiTietHoaDon.apDung(bang);
                    bang.getColumnModel().getColumn(0).setWidth(150);
                    int hep = bang.getRowHeight(0);
                    assertThat(hep).isGreaterThan(38);
                    bang.prepareRenderer(bang.getCellRenderer(0, 0), 0, 0);
                    assertThat(bang.getRowHeight(0)).isEqualTo(hep);
                    bang.getColumnModel().getColumn(0).setWidth(450);
                    assertThat(bang.getRowHeight(0)).isLessThan(hep);
                });
    }
}
