package com.petstation.gui.banhang;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.dto.GioHang;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

class LapHoaDonBoCucTest {
    private LapHoaDonPanel panel;
    private JTable table;

    @Test
    void congTruVaNhapSoLuongKhongDoiKichThuocCotHayO() throws Exception {
        SwingUtilities.invokeAndWait(this::taoPanel);
        dongBoLayout();
        int[] cotBanDau = new int[7];
        Dimension[] oBanDau = new Dimension[1];
        SwingUtilities.invokeAndWait(
                () -> {
                    for (int i = 0; i < 7; i++)
                        cotBanDau[i] = table.getColumnModel().getColumn(i).getWidth();
                    oBanDau[0] = kichThuocO();
                });
        for (int i = 0; i < 3; i++) {
            SwingUtilities.invokeAndWait(
                    () -> {
                        assertThat(table.editCellAt(0, 4)).isTrue();
                        Component editor = table.getEditorComponent();
                        layout(editor);
                        assertThat(timText(editor).getSize()).isEqualTo(oBanDau[0]);
                        timNut(editor, "+").doClick();
                    });
            dongBoLayout();
            SwingUtilities.invokeAndWait(
                    () -> {
                        assertThat(kichThuocCot()).containsExactly(cotBanDau);
                        assertThat(kichThuocO()).isEqualTo(oBanDau[0]);
                        assertThat(table.editCellAt(0, 4)).isTrue();
                        timNut(table.getEditorComponent(), "−").doClick();
                    });
            dongBoLayout();
        }
        SwingUtilities.invokeAndWait(
                () -> {
                    assertThat(table.getValueAt(0, 4)).isEqualTo(1);
                    assertThat(table.editCellAt(0, 4)).isTrue();
                    timText(table.getEditorComponent()).setText("12");
                    table.getCellEditor().stopCellEditing();
                });
        dongBoLayout();
        SwingUtilities.invokeAndWait(
                () -> {
                    assertThat(table.getValueAt(0, 4)).isEqualTo(12);
                    assertThat(kichThuocCot()).containsExactly(cotBanDau);
                    assertThat(kichThuocO()).isEqualTo(oBanDau[0]);
                });
    }

    @Test
    void khuyenMaiDaiNamDuoiNhanKhongChongChu() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    taoPanel();
                    JTextArea promo = (JTextArea) field("nhanNhanKhuyenMai");
                    promo.setText("Giảm 10% thức ăn hạt dinh dưỡng: −11.500₫");
                    layout(panel);
                    Container row = promo.getParent();
                    JLabel label =
                            (JLabel)
                                    ((BorderLayout) row.getLayout())
                                            .getLayoutComponent(BorderLayout.NORTH);
                    assertThat(promo.getY())
                            .isGreaterThanOrEqualTo(label.getY() + label.getHeight() + 4);
                    assertThat(promo.getWidth()).isPositive();
                    assertThat(promo.getLineWrap()).isTrue();
                    assertThat(promo.getRows()).isEqualTo(1);
                    assertThat(row.getHeight()).isEqualTo(row.getPreferredSize().height);
                    luuAnh(row, "khuyen-mai-khong-chong-chu.png");
                });
    }

    private void taoPanel() {
        try {
            GioHang.ChinhSach policy =
                    (GioHang.ChinhSach)
                            Proxy.newProxyInstance(
                                    GioHang.ChinhSach.class.getClassLoader(),
                                    new Class<?>[] {GioHang.ChinhSach.class},
                                    (p, m, a) ->
                                            m.getReturnType() == List.class
                                                    ? List.of()
                                                    : m.getReturnType() == long.class ? 0L : null);
            panel = new LapHoaDonPanel(new GioHang(policy), id -> {});
            table = (JTable) field("bangChiTiet");
            ((JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table))
                    .setColumnHeaderView(table.getTableHeader());
            Class<?> sp = Class.forName("com.petstation.gui.banhang.LapHoaDonPanel$SanPham");
            Constructor<?> ctor =
                    sp.getDeclaredConstructor(
                            String.class, String.class, long.class, int.class, String.class);
            ctor.setAccessible(true);
            datField(
                    "danhSachSanPhamCache",
                    List.of(
                            ctor.newInstance(
                                    "SP000001",
                                    "Thức ăn hạt cho mèo ROYAL CANIN Fit 32 — Gói 400g",
                                    115000L,
                                    999,
                                    "")));
            @SuppressWarnings("unchecked")
            List<GioHang.DongHang> gio = (List<GioHang.DongHang>) field("gioHang");
            gio.add(
                    new GioHang.DongHang(
                            "SP000001",
                            "Thức ăn hạt cho mèo ROYAL CANIN Fit 32 — Gói 400g",
                            115000L,
                            1));
            Method refresh = LapHoaDonPanel.class.getDeclaredMethod("lamMoiGioHang");
            refresh.setAccessible(true);
            refresh.invoke(panel);
            panel.setSize(1480, 800);
            layout(panel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dongBoLayout() throws Exception {
        SwingUtilities.invokeAndWait(() -> layout(panel));
        SwingUtilities.invokeAndWait(() -> layout(panel));
    }

    private Object field(String name) {
        try {
            Field f = LapHoaDonPanel.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(panel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void datField(String name, Object value) throws Exception {
        Field f = LapHoaDonPanel.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(panel, value);
    }

    private int[] kichThuocCot() {
        return java.util.stream.IntStream.range(0, 7)
                .map(i -> table.getColumnModel().getColumn(i).getWidth())
                .toArray();
    }

    private Dimension kichThuocO() {
        Component r = table.prepareRenderer(table.getCellRenderer(0, 4), 0, 4);
        r.setSize(table.getCellRect(0, 4, false).getSize());
        layout(r);
        return timText(r).getSize();
    }

    private static JTextField timText(Component c) {
        if (c instanceof JTextField text) return text;
        if (c instanceof Container p)
            for (Component x : p.getComponents()) {
                JTextField text = timText(x);
                if (text != null) return text;
            }
        return null;
    }

    private static JButton timNut(Component c, String text) {
        if (c instanceof JButton button && text.equals(button.getText())) return button;
        if (c instanceof Container p)
            for (Component x : p.getComponents()) {
                JButton button = timNut(x, text);
                if (button != null) return button;
            }
        return null;
    }

    private static void layout(Component c) {
        c.doLayout();
        if (c instanceof Container p) for (Component x : p.getComponents()) layout(x);
    }

    private static void luuAnh(Component c, String name) {
        try {
            BufferedImage img =
                    new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            c.paint(g);
            g.dispose();
            File folder = new File("target/ui-pos-fix");
            folder.mkdirs();
            ImageIO.write(img, "png", new File(folder, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
