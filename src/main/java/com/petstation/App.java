package com.petstation;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.gui.system.DangNhapFrame;
import com.petstation.util.KhoiTaoUngDung;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class App {
    public static void main(String[] cacDoiSo) {
        FlatLightLaf.setup();
        com.petstation.gui.components.HopThoai.thietLapCacGiaTriMacDinh();

        SwingUtilities.invokeLater(
                () -> {
                    try {
                        KhoiTaoUngDung nguCanh = KhoiTaoUngDung.tao();
                        nguCanh.layCSDLKetNoi().xacMinh();
                        new DangNhapFrame(nguCanh).setVisible(true);
                    } catch (Exception ngoaiLe) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Không thể khởi động Pet Station.\n\n"
                                        + ngoaiLe.getMessage()
                                        + "\n\nHãy kiểm tra SQL Server và file SETUP.md.",
                                "Lỗi cấu hình",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
    }
}
