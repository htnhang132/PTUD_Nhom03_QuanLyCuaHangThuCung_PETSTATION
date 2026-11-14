package com.petstation.gui.chung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public final class BieuMauPanel extends JPanel {
    private BieuMauPanel() {}

    public static JTextField layTruongVanBan(String vanBan) {
        JTextField oTruongDangXet = new JTextField(vanBan);
        dinhDangDauVao(oTruongDangXet);
        return oTruongDangXet;
    }

    public static void dinhDangDauVao(JComponent truongDangXet) {
        truongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        truongDangXet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true),
                        new EmptyBorder(6, 10, 6, 10)));
        truongDangXet.setPreferredSize(new Dimension(160, 36));
    }

    public static <KieuDuLieu> JComboBox<KieuDuLieu> layHopChon(KieuDuLieu[] cacMuc) {
        JComboBox<KieuDuLieu> cboHop = new JComboBox<>(cacMuc);
        cboHop.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        cboHop.setPreferredSize(new Dimension(160, 36));
        cboHop.setBackground(Color.WHITE);
        return cboHop;
    }
}
