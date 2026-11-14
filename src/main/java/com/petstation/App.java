package com.petstation;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.hethong.DangNhapFrame;
import com.petstation.util.KhoiTaoUngDung;

import javax.swing.SwingUtilities;

public final class App {
    public static void main(String[] cacDoiSo) {
        FlatLightLaf.setup();
        HopThoai.thietLapCacGiaTriMacDinh();
        SwingUtilities.invokeLater(
                () -> {
                    try {
                        KhoiTaoUngDung nguCanh = KhoiTaoUngDung.tao();
                        Runtime.getRuntime()
                                .addShutdownHook(
                                        new Thread(
                                                () -> nguCanh.layCSDLKetNoi().dong(),
                                                "petstation-db-shutdown"));
                        nguCanh.layCSDLKetNoi().xacMinh();
                        new DangNhapFrame(nguCanh).setVisible(true);
                    } catch (Exception ngoaiLe) {
                        HopThoai.hienThiLoi(
                                null,
                                "Không thể khởi động Pet Station.\n\n"
                                        + ngoaiLe.getMessage()
                                        + "\n\nHãy kiểm tra SQL Server và file SETUP.md.",
                                "Lỗi cấu hình");
                    }
                });
    }
}
