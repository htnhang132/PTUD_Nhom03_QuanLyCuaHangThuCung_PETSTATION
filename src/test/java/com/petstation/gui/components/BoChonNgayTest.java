package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.*;

import com.petstation.gui.components.common.BoChonNgay;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

class BoChonNgayTest {
    static java.util.List<Component> con(Container cha) {
        var ds = new ArrayList<Component>();
        for (var c : cha.getComponents()) {
            ds.add(c);
            if (c instanceof Container p) ds.addAll(con(p));
        }
        return ds;
    }

    static void boTri(Container p) {
        p.doLayout();
        for (var c : p.getComponents()) if (c instanceof Container n) boTri(n);
    }

    @Test
    void lichDuNgayHaiChuSoVaKhongCatTaiNhieuCoChu() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    Font cu = UIManager.getFont("Button.font");
                    try {
                        for (float tiLe : new float[] {1, 1.25f, 1.5f})
                            for (LocalDate ngay :
                                    java.util.List.of(
                                            LocalDate.of(2026, 2, 1),
                                            LocalDate.of(2024, 2, 1),
                                            LocalDate.of(2026, 4, 1),
                                            LocalDate.of(2026, 10, 1),
                                            LocalDate.of(2026, 12, 1))) {
                                UIManager.put("Button.font", cu.deriveFont(cu.getSize2D() * tiLe));
                                var chon = new BoChonNgay(ngay);
                                var tao = BoChonNgay.class.getDeclaredMethod("taoLichHopThoai");
                                tao.setAccessible(true);
                                tao.invoke(chon);
                                var nap = BoChonNgay.class.getDeclaredMethod("capNhatLich");
                                nap.setAccessible(true);
                                nap.invoke(chon);
                                var f = BoChonNgay.class.getDeclaredField("lichHopThoai");
                                f.setAccessible(true);
                                JDialog hop = (JDialog) f.get(chon);
                                hop.pack();
                                for (int i = 0; i < 4; i++) boTri(hop);
                                var cacNgay =
                                        con(hop).stream()
                                                .filter(
                                                        c ->
                                                                c instanceof JButton b
                                                                        && b.getName() != null
                                                                        && b.getName()
                                                                                .startsWith(
                                                                                        "ngay:"))
                                                .map(c -> (JButton) c)
                                                .toList();
                                assertThat(cacNgay).hasSize(ngay.lengthOfMonth());
                                for (var nut : cacNgay) {
                                    Insets le = nut.getInsets();
                                    assertThat(nut.getWidth() - le.left - le.right)
                                            .isGreaterThanOrEqualTo(
                                                    nut.getFontMetrics(nut.getFont())
                                                            .stringWidth(nut.getText()));
                                }
                                hop.dispose();
                            }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        UIManager.put("Button.font", cu);
                    }
                });
    }

    @Test
    void xoaNgayPhatSuKienVaChapNhanKhoiTaoTrong() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    var chon = new BoChonNgay(null);
                    AtomicInteger so = new AtomicInteger();
                    chon.addPropertyChangeListener("date", e -> so.incrementAndGet());
                    chon.datNgayDuocChon(LocalDate.of(2026, 12, 31));
                    chon.xoa();
                    assertThat(chon.layNgayDuocChon()).isNull();
                    assertThat(so.get()).isEqualTo(2);
                });
    }
}
