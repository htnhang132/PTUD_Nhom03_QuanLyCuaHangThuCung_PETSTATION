package com.petstation.gui.components;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class BieuMauDialog {
    private BieuMauDialog() {}

    public static List<String> layYeuCauNhap(
            Component cha, String tieuDe, String[] cacNhan, String[] banDau) {
        JPanel pnlVungDangXet = new JPanel(new GridBagLayout());

        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(4, 4, 4, 4);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] cacTruong = new JTextField[cacNhan.length];
        for (int chiSo = 0; chiSo < cacNhan.length; chiSo++) {
            giaTriDangXet.gridx = 0;
            giaTriDangXet.gridy = chiSo;
            giaTriDangXet.weightx = 0;
            pnlVungDangXet.add(new JLabel(cacNhan[chiSo]), giaTriDangXet);
            cacTruong[chiSo] =
                    new JTextField(
                            banDau != null && chiSo < banDau.length ? banDau[chiSo] : "", 22);
            giaTriDangXet.gridx = 1;
            giaTriDangXet.weightx = 1;
            pnlVungDangXet.add(cacTruong[chiSo], giaTriDangXet);
        }

        Object[] cacLuaChon = {"Đồng ý", "Hủy"};

        int dongDuLieu =
                JOptionPane.showOptionDialog(
                        cha,
                        pnlVungDangXet,
                        tieuDe,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        cacLuaChon,
                        cacLuaChon[0]);
        if (dongDuLieu != 0) return null;

        List<String> ra = new ArrayList<>();
        for (JTextField txtTruongDangXet : cacTruong) ra.add(txtTruongDangXet.getText().trim());
        return ra;
    }
}
