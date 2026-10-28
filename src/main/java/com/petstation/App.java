package com.petstation;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.gui.system.DangNhapFrame;
import com.petstation.util.KhoiTaoUngDung;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Điểm khởi chạy Pet Station: thiết lập giao diện, kiểm tra SQL Server và mở màn đăng nhập. */
public final class App {
    /**
     * Khởi tạo ứng dụng trên luồng sự kiện Swing.
     *
     * @param cacDoiSo các đối số
     */
    public static void main(String[] cacDoiSo) {
        FlatLightLaf.setup();
        com.petstation.gui.components.HopThoai.thietLapCacGiaTriMacDinh();

        SwingUtilities.invokeLater(
                () -> {
                    try {
                        // Ngữ cảnh.
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
