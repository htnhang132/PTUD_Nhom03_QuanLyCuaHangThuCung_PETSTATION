package com.petstation.gui.components.common;

import com.petstation.util.TaiAnhSanPham;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

/** Hiển thị ảnh đúng tỷ lệ; tải nền và bỏ kết quả cũ khi người dùng đổi sản phẩm. */
public final class AnhSanPhamPanel extends JPanel {
    private static final ExecutorService BO_TAI =
            Executors.newFixedThreadPool(
                    2,
                    r -> {
                        Thread t = new Thread(r, "tai-anh-san-pham");
                        t.setDaemon(true);
                        return t;
                    });
    private static final Map<String, BufferedImage> BO_NHO =
            Collections.synchronizedMap(
                    new LinkedHashMap<>(96, .75f, true) {
                        @Override
                        protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> e) {
                            return size() > 96;
                        }
                    });
    private BufferedImage anh;
    private String duongDan = "";
    private long lanTai;
    private String thongBao = "Chưa có ảnh";

    public AnhSanPhamPanel(int kichThuoc) {
        setPreferredSize(new Dimension(kichThuoc, kichThuoc));
        setMinimumSize(new Dimension(80, 80));
        setOpaque(false);
    }

    /** Đặt ảnh theo đường dẫn tương đối trong SQL; không chặn luồng giao diện. */
    public void datAnh(String giaTri) {
        duongDan = giaTri == null ? "" : giaTri;
        long phien = ++lanTai;
        anh = BO_NHO.get(duongDan);
        thongBao = duongDan.isBlank() ? "Chưa có ảnh" : "Đang tải ảnh…";
        repaint();
        if (anh != null || duongDan.isBlank()) return;
        String tep = duongDan;
        CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return TaiAnhSanPham.doc(tep);
                            } catch (Exception e) {
                                return null;
                            }
                        },
                        BO_TAI)
                .thenAccept(
                        ketQua ->
                                SwingUtilities.invokeLater(
                                        () -> {
                                            if (ketQua != null) BO_NHO.put(tep, ketQua);
                                            if (phien != lanTai) return;
                                            anh = ketQua;
                                            thongBao = "Chưa có ảnh";
                                            repaint();
                                        }));
    }

    public String layDuongDan() {
        return duongDan;
    }

    @Override
    protected void paintComponent(Graphics goc) {
        super.paintComponent(goc);
        Graphics2D g = (Graphics2D) goc.create();
        try {
            if (anh == null) {
                g.setColor(new Color(125, 125, 125));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(
                        thongBao,
                        Math.max(4, (getWidth() - fm.stringWidth(thongBao)) / 2),
                        getHeight() / 2);
                return;
            }
            double tyLe =
                    Math.min(
                            (double) getWidth() / anh.getWidth(),
                            (double) getHeight() / anh.getHeight());
            int rong = Math.max(1, (int) (anh.getWidth() * tyLe)),
                    cao = Math.max(1, (int) (anh.getHeight() * tyLe));
            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(anh, (getWidth() - rong) / 2, (getHeight() - cao) / 2, rong, cao, null);
        } finally {
            g.dispose();
        }
    }
}
