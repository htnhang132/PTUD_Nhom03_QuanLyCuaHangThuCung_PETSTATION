package com.petstation.gui.components;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class BieuMauPanel extends JPanel {
    private final GridBagConstraints giaTriDangXet = new GridBagConstraints();

    private int cot;

    private int dong;

    public BieuMauPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        giaTriDangXet.weightx = 1;
        giaTriDangXet.insets = new Insets(0, 0, 12, 14);
        giaTriDangXet.anchor = GridBagConstraints.NORTHWEST;
    }

    public void themMuc(String tieuDe) {
        layKetThucDong();

        JLabel lblNhan = new JLabel(tieuDe);
        lblNhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblNhan.setForeground(MauUngDung.NAU);
        lblNhan.setBorder(new EmptyBorder(4, 0, 0, 0));
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = dong++;
        giaTriDangXet.gridwidth = 2;
        add(lblNhan, giaTriDangXet);
        giaTriDangXet.gridwidth = 1;
    }

    public void themTruong(String nhan, JComponent truong) {
        dat(boc(nhan, truong), 1);
    }

    public void themRong(String nhan, JComponent truong) {
        dat(boc(nhan, truong), 2);
    }

    public void themDayDu(JComponent thanhPhan) {
        dat(thanhPhan, 2);
    }

    private void dat(JComponent thanhPhan, int doRong) {
        if (doRong == 2 && cot != 0) layKetThucDong();
        giaTriDangXet.gridx = cot;
        giaTriDangXet.gridy = dong;
        giaTriDangXet.gridwidth = doRong;
        add(thanhPhan, giaTriDangXet);
        giaTriDangXet.gridwidth = 1;
        cot += doRong;
        if (cot >= 2) layKetThucDong();
    }

    private void layKetThucDong() {
        if (cot != 0) {
            cot = 0;
            dong++;
        }
    }

    private JPanel boc(String nhan, JComponent truong) {
        JPanel pnlHop = new JPanel(new BorderLayout(0, 4));
        pnlHop.setOpaque(false);

        JLabel lblVanBan = new JLabel(nhan);
        lblVanBan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblVanBan.setForeground(MauUngDung.VAN_BAN_TOI);
        lblVanBan.setLabelFor(truong);
        pnlHop.add(lblVanBan, BorderLayout.NORTH);
        pnlHop.add(truong, BorderLayout.CENTER);
        return pnlHop;
    }

    public static JTextField layTruongVanBan(String vanBan) {
        JTextField txtTruongDangXet = new JTextField(vanBan);
        dinhDangDauVao(txtTruongDangXet);
        return txtTruongDangXet;
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
