package com.petstation;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.hethong.DangNhapFrame;
import com.petstation.util.KhoiTaoUngDung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;

public final class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] cacDoiSo) {
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) -> {
                    LOG.error("Lỗi chưa được xử lý trên luồng {}", thread.getName(), throwable);
                    Runnable baoLoi =
                            () ->
                                    HopThoai.hienThiLoi(
                                            null,
                                            "Đã xảy ra lỗi không mong muốn: "
                                                    + (throwable.getMessage() != null
                                                            ? throwable.getMessage()
                                                            : throwable.getClass().getSimpleName()),
                                            "Lỗi hệ thống");
                    if (SwingUtilities.isEventDispatchThread()) {
                        baoLoi.run();
                    } else {
                        SwingUtilities.invokeLater(baoLoi);
                    }
                });
        FlatLightLaf.setup();
        HopThoai.thietLapCacGiaTriMacDinh();
        Thread luongKhoiTao =
                new Thread(
                        () -> {
                            try {
                                KhoiTaoUngDung nguCanh = KhoiTaoUngDung.tao();
                                Runtime.getRuntime()
                                        .addShutdownHook(
                                                new Thread(
                                                        () -> nguCanh.layCSDLKetNoi().dong(),
                                                        "petstation-db-shutdown"));
                                nguCanh.layCSDLKetNoi().xacMinh();
                                SwingUtilities.invokeLater(
                                        () -> new DangNhapFrame(nguCanh).setVisible(true));
                            } catch (Exception ngoaiLe) {
                                LOG.error("Không thể khởi động Pet Station", ngoaiLe);
                                SwingUtilities.invokeLater(
                                        () ->
                                                HopThoai.hienThiLoi(
                                                        null,
                                                        "Không thể khởi động Pet Station.\n\n"
                                                                + ngoaiLe.getMessage()
                                                                + "\n\nHãy kiểm tra SQL Server và"
                                                                + " file SETUP.md.",
                                                        "Lỗi cấu hình"));
                            }
                        },
                        "petstation-startup");
        luongKhoiTao.setDaemon(false);
        luongKhoiTao.start();
    }
}
