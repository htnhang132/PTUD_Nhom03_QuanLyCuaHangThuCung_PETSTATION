package com.petstation.gui.chung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

public final class TaoThanhPhanBieuMau {
    private TaoThanhPhanBieuMau() {}

    public static JTextField layTruongVanBan(String vanBan) {
        JTextField oTruongDangXet = new JTextField(vanBan);
        dinhDangDauVao(oTruongDangXet);
        return oTruongDangXet;
    }

    public static void dinhDangDauVao(JComponent truongDangXet) {
        truongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        truongDangXet.setPreferredSize(new Dimension(160, MauUngDung.CAO_DAU_VAO));
    }

    public static <KieuDuLieu> JComboBox<KieuDuLieu> layHopChon(KieuDuLieu[] cacMuc) {
        JComboBox<KieuDuLieu> cboHop = new JComboBox<>(cacMuc);
        cboHop.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        cboHop.setPreferredSize(new Dimension(160, MauUngDung.CAO_DAU_VAO));
        cboHop.setBackground(Color.WHITE);
        return cboHop;
    }
}
