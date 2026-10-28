package com.petstation.gui.components.common;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** Đồng bộ tốc độ cuộn, giữ nguyên cơ chế wheel/trackpad của Swing và Windows. */
public final class HoTroCuon {
    private HoTroCuon() {}

    public static void theoDoi(Container vung) {
        if (vung instanceof JComponent thanhPhan) {
            if (Boolean.TRUE.equals(thanhPhan.getClientProperty("petstation.cuon"))) return;
            thanhPhan.putClientProperty("petstation.cuon", true);
        }
        if (vung instanceof JScrollPane cuon) {
            Runnable capNhat =
                    () -> {
                        Component noiDung = cuon.getViewport().getView();
                        int buoc = noiDung instanceof JTable bang ? bang.getRowHeight() : 24;
                        cuon.getVerticalScrollBar().setUnitIncrement(buoc);
                        cuon.getVerticalScrollBar()
                                .setBlockIncrement(
                                        Math.max(buoc, cuon.getViewport().getHeight() - buoc));
                    };
            cuon.getViewport().addChangeListener(e -> capNhat.run());
            cuon.addHierarchyListener(e -> capNhat.run());
            capNhat.run();
        }
        vung.addContainerListener(
                new ContainerAdapter() {
                    @Override
                    public void componentAdded(ContainerEvent suKien) {
                        if (suKien.getChild() instanceof Container con) theoDoi(con);
                    }
                });
        for (Component con : vung.getComponents()) if (con instanceof Container nhom) theoDoi(nhom);
    }
}
