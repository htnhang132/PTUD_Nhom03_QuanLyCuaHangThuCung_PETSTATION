package com.petstation.gui.components.common;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class KiemTraBieuMau {
    private Map<JComponent, QuyTacKiemTra> cacQuyTac = new HashMap<>();

    private Map<JComponent, JLabel> loiCacNhan = new HashMap<>();

    private Map<JComponent, Border> banDauCacVien = new HashMap<>();

    private static final Color MAU_LOI = new Color(220, 53, 69);

    private static final Border LOI_VIEN = BorderFactory.createLineBorder(MAU_LOI, 2);

    public static class QuyTacKiemTra {
        Function<String, String> boKiemTra;

        boolean batBuoc;

        public QuyTacKiemTra(Function<String, String> boKiemTra, boolean batBuoc) {
            this.boKiemTra = boKiemTra;
            this.batBuoc = batBuoc;
        }
    }

    public void themQuyTac(JTextField txtTruong, QuyTacKiemTra quyTac, Container cha) {
        cacQuyTac.put(txtTruong, quyTac);
        banDauCacVien.put(txtTruong, txtTruong.getBorder());

        JLabel lblNhanLoi = new JLabel();
        lblNhanLoi.setForeground(MAU_LOI);
        lblNhanLoi.setFont(lblNhanLoi.getFont().deriveFont(Font.PLAIN, 11f));
        lblNhanLoi.setVisible(false);
        loiCacNhan.put(txtTruong, lblNhanLoi);

        try {
            GridBagConstraints rangBuocBoCuc = new GridBagConstraints();
            rangBuocBoCuc.gridx = 0;
            rangBuocBoCuc.gridy = layThanhPhanDong(cha, txtTruong) + 1;
            rangBuocBoCuc.gridwidth = 2;
            rangBuocBoCuc.anchor = GridBagConstraints.WEST;
            rangBuocBoCuc.insets = new Insets(2, 20, 5, 5);
            cha.add(lblNhanLoi, rangBuocBoCuc);
        } catch (Exception loi) {
        }

        txtTruong
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent suKien) {
                                kiemTraTruong(txtTruong);
                            }

                            @Override
                            public void removeUpdate(DocumentEvent suKien) {
                                kiemTraTruong(txtTruong);
                            }

                            @Override
                            public void changedUpdate(DocumentEvent suKien) {
                                kiemTraTruong(txtTruong);
                            }
                        });
    }

    private int layThanhPhanDong(Container cha, Component thanhPhan) {
        Component[] cacThanhPhan = cha.getComponents();
        for (int chiSo = 0; chiSo < cacThanhPhan.length; chiSo++) {
            if (cacThanhPhan[chiSo] == thanhPhan) {
                return chiSo / 2;
            }
        }
        return 0;
    }

    public boolean kiemTraTruong(JTextField txtTruong) {
        QuyTacKiemTra quyTac = cacQuyTac.get(txtTruong);
        if (quyTac == null) return true;

        String giaTri = txtTruong.getText().trim();

        if (quyTac.batBuoc && giaTri.isEmpty()) {
            hienThiLoi(txtTruong, "Trường này là bắt buộc");
            return false;
        }

        if (!giaTri.isEmpty() && quyTac.boKiemTra != null) {
            String loi = quyTac.boKiemTra.apply(giaTri);
            if (loi != null) {
                hienThiLoi(txtTruong, loi);
                return false;
            }
        }

        xoaLoi(txtTruong);
        return true;
    }

    public boolean kiemTraTatCa() {
        boolean tatCaHopLe = true;

        for (JComponent truong : cacQuyTac.keySet()) {
            if (truong instanceof JTextField) {
                boolean hopLe = kiemTraTruong((JTextField) truong);
                tatCaHopLe = tatCaHopLe && hopLe;
            }
        }

        return tatCaHopLe;
    }

    private void hienThiLoi(JTextField txtTruong, String thongBao) {
        txtTruong.setBorder(LOI_VIEN);

        JLabel lblNhanLoi = loiCacNhan.get(txtTruong);
        if (lblNhanLoi != null) {
            lblNhanLoi.setText("" + thongBao);
            lblNhanLoi.setVisible(true);
        }
    }

    private void xoaLoi(JTextField txtTruong) {
        Border banDauVien = banDauCacVien.get(txtTruong);
        if (banDauVien != null) {
            txtTruong.setBorder(banDauVien);
        }

        JLabel lblNhanLoi = loiCacNhan.get(txtTruong);
        if (lblNhanLoi != null) {
            lblNhanLoi.setVisible(false);
        }
    }

    public void xoaTatCaCacLoi() {
        for (JComponent truong : cacQuyTac.keySet()) {
            if (truong instanceof JTextField) {
                xoaLoi((JTextField) truong);
            }
        }
    }

    public static QuyTacKiemTra layBatBuoc() {
        return new QuyTacKiemTra(null, true);
    }

    public static QuyTacKiemTra layEmail() {
        Pattern mau = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        return new QuyTacKiemTra(
                giaTri -> {
                    if (!mau.matcher(giaTri).matches()) {
                        return "Email không đúng định dạng";
                    }
                    return null;
                },
                false);
    }

    public static QuyTacKiemTra laySoDienThoai() {
        Pattern mau = Pattern.compile("^0\\d{9,10}$");
        return new QuyTacKiemTra(
                giaTri -> {
                    if (!mau.matcher(giaTri).matches()) {
                        return "Số điện thoại phải có 10-11 số và bắt đầu bằng 0";
                    }
                    return null;
                },
                false);
    }

    public static QuyTacKiemTra laySoNguyen() {
        return new QuyTacKiemTra(
                giaTri -> {
                    try {
                        Integer.parseInt(giaTri);
                        return null;
                    } catch (NumberFormatException loi) {
                        return "Phải là số nguyên";
                    }
                },
                false);
    }

    public static QuyTacKiemTra docSoThapPhan() {
        return new QuyTacKiemTra(
                giaTri -> {
                    try {
                        Double.parseDouble(giaTri);
                        return null;
                    } catch (NumberFormatException loi) {
                        return "Phải là số";
                    }
                },
                false);
    }

    public static QuyTacKiemTra layNhoNhatDoDai(int nhoNhat) {
        return new QuyTacKiemTra(
                giaTri -> {
                    if (giaTri.length() < nhoNhat) {
                        return "Phải có ít nhất " + nhoNhat + "ký tự";
                    }
                    return null;
                },
                false);
    }

    public static QuyTacKiemTra layLonNhatDoDai(int lonNhat) {
        return new QuyTacKiemTra(
                giaTri -> {
                    if (giaTri.length() > lonNhat) {
                        return "Không được quá " + lonNhat + "ký tự";
                    }
                    return null;
                },
                false);
    }

    public static QuyTacKiemTra layKhoang(double nhoNhat, double lonNhat) {
        return new QuyTacKiemTra(
                giaTri -> {
                    try {
                        double so = Double.parseDouble(giaTri);
                        if (so < nhoNhat || so > lonNhat) {
                            return String.format("Phải trong khoảng %.0f - %.0f", nhoNhat, lonNhat);
                        }
                        return null;
                    } catch (NumberFormatException loi) {
                        return "Phải là số";
                    }
                },
                false);
    }

    public static QuyTacKiemTra layTuyChinh(Function<String, String> boKiemTra) {
        return new QuyTacKiemTra(boKiemTra, false);
    }

    public static QuyTacKiemTra ketHop(QuyTacKiemTra... cacQuyTac) {
        return new QuyTacKiemTra(
                giaTri -> {
                    for (QuyTacKiemTra quyTac : cacQuyTac) {
                        if (quyTac.boKiemTra != null) {
                            String loi = quyTac.boKiemTra.apply(giaTri);
                            if (loi != null) return loi;
                        }
                    }
                    return null;
                },
                false);
    }
}
