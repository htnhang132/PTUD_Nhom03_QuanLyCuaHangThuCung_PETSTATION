package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.gui.components.common.BoChonNgay;
import java.awt.*;
import javax.swing.*;
import org.junit.jupiter.api.Test;

class ThanhDanhSachTest {
    @Test
    void boLocLichHenKhongBiCatKhiThanhCongCuXuongHang() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ThanhDanhSach bar = new ThanhDanhSach();
            bar.add(new JTextField(20));
            bar.add(new JButton("Làm mới"));
            bar.add(new JButton("Xuất Excel"));
            bar.add(new JComboBox<>(new String[] {"Danh sách", "Ngày", "Tuần"}));
            bar.themBoLoc("Ngày:", new BoChonNgay());
            bar.themBoLoc("Nhân viên:", new JComboBox<>(new String[] {"Tất cả", "Phạm Thảo Nhi"}));
            bar.themBoLoc("Trạng thái:", new JComboBox<>(new String[] {"Tất cả", "Đang thực hiện"}));
            for (String ten : new String[] {"+ Đặt lịch hẹn", "Sửa lịch", "Khách đã đến", "Hủy lịch"})
                bar.add(new JButton(ten));

            for (int rong : new int[] {1300, 1100, 700, 420}) {
                bar.setSize(rong, 32);
                bar.setSize(rong, bar.getPreferredSize().height);
                bar.doLayout();
                Rectangle vung = new Rectangle(0, 0, bar.getWidth(), bar.getHeight());
                Component[] cac = bar.getComponents();
                for (int i = 0; i < cac.length; i++) {
                    Component c = cac[i];
                    assertThat(vung.contains(c.getBounds())).as("Thành phần nằm trong thanh ở %s px", rong).isTrue();
                    for (int j = 0; j < i; j++)
                        assertThat(c.getBounds().intersects(cac[j].getBounds())).isFalse();
                    if (c instanceof JPanel nhom) {
                        nhom.doLayout();
                        for (Component con : nhom.getComponents()) {
                            assertThat(new Rectangle(0, 0, nhom.getWidth(), nhom.getHeight()).contains(con.getBounds()))
                                .as("Nhãn và ô lọc không bị cắt ở %s px", rong).isTrue();
                        }
                    } else if (c instanceof JButton) {
                        assertThat(c.getWidth()).isGreaterThanOrEqualTo(c.getPreferredSize().width);
                    }
                }
            }
        });
    }

    @Test
    void tonTrongChieuCaoNhomLocVaLeThanhCongCu() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ThanhDanhSach bar = new ThanhDanhSach();
            bar.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
            JPanel nhom = new JPanel();
            nhom.setPreferredSize(new Dimension(200, 54));
            bar.add(nhom);
            bar.add(new JButton("Tìm kiếm"));
            bar.setSize(250, 1);
            bar.setSize(250, bar.getPreferredSize().height);
            bar.doLayout();
            assertThat(nhom.getHeight()).isEqualTo(54);
            assertThat(nhom.getX()).isEqualTo(8);
            assertThat(nhom.getY()).isEqualTo(4);
            Component nut = bar.getComponent(1);
            assertThat(nut.getY()).isGreaterThanOrEqualTo(nhom.getY() + nhom.getHeight() + 6);
            assertThat(nut.getY() + nut.getHeight()).isLessThanOrEqualTo(bar.getHeight() - 6);
        });
    }
}
