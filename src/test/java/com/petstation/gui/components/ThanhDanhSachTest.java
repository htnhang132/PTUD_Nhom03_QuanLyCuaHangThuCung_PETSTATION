package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.awt.*;

import javax.swing.*;

class ThanhDanhSachTest {
    @Test
    void thuPhongVanMotHangKhongChongVaGiuNutChinhXuat() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    ThanhDanhSach bar = new ThanhDanhSach();
                    bar.add(new JTextField(30));
                    for (int i = 0; i < 4; i++)
                        bar.themBoLoc(
                                "Bộ lọc " + i,
                                new JComboBox<>(new String[] {"Tên giá trị rất dài"}));
                    JButton primary = new NutDinhDang("+ Tiếp nhận", NutDinhDang.KieuNut.CHINH),
                            export = new JButton("Xuất Excel");
                    bar.add(primary);
                    for (int i = 0; i < 7; i++) bar.add(new JButton("Thao tác bổ sung " + i));
                    bar.add(export);
                    for (double scale : new double[] {1, 1.25, 1.5})
                        for (int w : new int[] {1000, 1266, 1820}) {
                            for (Component c : bar.getComponents())
                                c.setFont(new Font("Dialog", Font.PLAIN, (int) (13 * scale)));
                            bar.setSize(w, 36);
                            bar.doLayout();
                            assertThat(primary.isVisible()).isTrue();
                            assertThat(export.isVisible()).isTrue();
                            java.util.List<Rectangle> used = new java.util.ArrayList<>();
                            for (Component c : bar.getComponents())
                                if (c.isVisible()) {
                                    assertThat(c.getY()).isZero();
                                    assertThat(c.getHeight()).isEqualTo(36);
                                    assertThat(c.getX()).isGreaterThanOrEqualTo(0);
                                    assertThat(c.getX() + c.getWidth()).isLessThanOrEqualTo(w);
                                    for (Rectangle r : used)
                                        assertThat(r.intersects(c.getBounds())).isFalse();
                                    used.add(c.getBounds());
                                }
                        }
                });
    }
}
