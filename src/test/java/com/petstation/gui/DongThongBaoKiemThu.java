package com.petstation.gui;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/** Dismisses informational messages only; never confirms a destructive action. */
public final class DongThongBaoKiemThu implements AutoCloseable {
    private final Timer timer =
            new Timer(
                    100,
                    e -> {
                        for (Window window : Window.getWindows()) {
                            if (window instanceof JDialog dialog
                                    && dialog.isShowing()
                                    && laThongBao(dialog.getContentPane())) {
                                dialog.dispose();
                            }
                        }
                    });

    private static boolean laThongBao(java.awt.Component component) {
        if (component instanceof JOptionPane pane)
            return pane.getOptionType() == JOptionPane.DEFAULT_OPTION;
        if (component instanceof java.awt.Container container)
            for (java.awt.Component child : container.getComponents())
                if (laThongBao(child)) return true;
        return false;
    }

    public DongThongBaoKiemThu() {
        timer.start();
    }

    @Override
    public void close() {
        timer.stop();
    }
}
