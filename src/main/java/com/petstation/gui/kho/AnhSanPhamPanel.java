package com.petstation.gui.kho;

import com.petstation.gui.chung.MauUngDung;
import com.petstation.util.TaiAnhSanPham;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
        setBorder(null);
    }

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
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (anh == null) {
                g.setColor(MauUngDung.NEN_KEM);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g.setColor(MauUngDung.XAM_VIEN_TRUNG);
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g.setColor(MauUngDung.VAN_BAN_XAM);
                g.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(
                        thongBao,
                        Math.max(4, (getWidth() - fm.stringWidth(thongBao)) / 2),
                        (getHeight() + fm.getAscent()) / 2 - 2);
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
