package com.petstation.gui.chung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.JComponent;

public final class ChiBaoBuoc extends JComponent {
    private final List<String> cacBuoc;

    private int hienTai;

    private IntConsumer khiBam;

    public ChiBaoBuoc(List<String> cacBuoc) {
        this.cacBuoc = List.copyOf(cacBuoc);
        setPreferredSize(new Dimension(0, 44));
        setOpaque(false);
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (khiBam == null) return;
                        int chiSo = layChiSoTai(suKien.getX());
                        if (chiSo >= 0) khiBam.accept(chiSo);
                    }
                });
    }

    public void datHienTai(int chiSo) {
        this.hienTai = Math.max(0, Math.min(chiSo, cacBuoc.size() - 1));
        repaint();
    }

    public void datKhiBam(IntConsumer khiBam) {
        this.khiBam = khiBam;
        setCursor(
                khiBam == null
                        ? Cursor.getDefaultCursor()
                        : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private int layChiSoTai(int toaDoNgang) {
        if (cacBuoc.isEmpty()) return -1;
        int o = getWidth() / cacBuoc.size();
        int chiSo = toaDoNgang / Math.max(o, 1);
        return chiSo >= 0 && chiSo < cacBuoc.size() ? chiSo : -1;
    }

    @Override
    protected void paintComponent(Graphics doHoa) {
        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
        doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        doHoa2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int soPhanTu = cacBuoc.size();
        if (soPhanTu == 0) {
            doHoa2.dispose();
            return;
        }
        int o = getWidth() / soPhanTu;
        int tamDoc = getHeight() / 2;
        int dongDuLieu = 11;
        doHoa2.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        FontMetrics thongSoPhongChu = doHoa2.getFontMetrics();
        for (int chiSo = 0; chiSo < soPhanTu; chiSo++) {
            int trai = chiSo * o;
            int tamNgang = trai + 16 + dongDuLieu;
            boolean daHoanTat = chiSo < hienTai;
            boolean hoatDong = chiSo == hienTai;
            if (chiSo < soPhanTu - 1) {
                doHoa2.setColor(daHoanTat ? MauUngDung.XANH_LA : MauUngDung.VIEN);
                doHoa2.setStroke(new BasicStroke(2f));
                int nhanKetThuc =
                        tamNgang + dongDuLieu + 8 + thongSoPhongChu.stringWidth(cacBuoc.get(chiSo));
                doHoa2.drawLine(
                        Math.min(nhanKetThuc + 8, trai + o - 12), tamDoc, trai + o - 6, tamDoc);
            }
            doHoa2.setColor(
                    hoatDong
                            ? MauUngDung.DO_THUONG_HIEU
                            : daHoanTat ? MauUngDung.XANH_LA : Color.WHITE);
            doHoa2.fillOval(
                    tamNgang - dongDuLieu, tamDoc - dongDuLieu, 2 * dongDuLieu, 2 * dongDuLieu);
            doHoa2.setColor(hoatDong || daHoanTat ? Color.WHITE : MauUngDung.VAN_BAN_XAM);
            if (!hoatDong && !daHoanTat) {
                doHoa2.setColor(MauUngDung.VIEN);
                doHoa2.setStroke(new BasicStroke(1.5f));
                doHoa2.drawOval(
                        tamNgang - dongDuLieu, tamDoc - dongDuLieu, 2 * dongDuLieu, 2 * dongDuLieu);
                doHoa2.setColor(MauUngDung.VAN_BAN_XAM);
            }
            if (daHoanTat) {
                doHoa2.setStroke(
                        new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                doHoa2.drawLine(tamNgang - 4, tamDoc, tamNgang - 1, tamDoc + 3);
                doHoa2.drawLine(tamNgang - 1, tamDoc + 3, tamNgang + 5, tamDoc - 3);
            } else {
                String so = String.valueOf(chiSo + 1);
                doHoa2.drawString(
                        so,
                        tamNgang - thongSoPhongChu.stringWidth(so) / 2,
                        tamDoc + thongSoPhongChu.getAscent() / 2 - 1);
            }
            doHoa2.setColor(hoatDong ? MauUngDung.VAN_BAN_TOI : MauUngDung.VAN_BAN_XAM);
            String nhan = cacBuoc.get(chiSo);
            int rong = Math.max(0, o - 54);
            while (nhan.length() > 1 && thongSoPhongChu.stringWidth(nhan) > rong)
                nhan = nhan.substring(0, nhan.length() - 2) + "…";
            doHoa2.drawString(
                    nhan, tamNgang + dongDuLieu + 8, tamDoc + thongSoPhongChu.getAscent() / 2 - 1);
        }
        doHoa2.dispose();
    }
}
