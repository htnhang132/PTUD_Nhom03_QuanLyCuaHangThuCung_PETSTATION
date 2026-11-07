package com.petstation.gui.components.common;

import com.petstation.util.TienTe;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class BoSoanThaoBangGia implements BoSoanThaoTruong {
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Loài", "Khoảng cân nặng", "Giá (₫)"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable bang = new JTable(model);
    private final JPanel panel = new JPanel(new BorderLayout(5, 8));
    private final JComboBox<String> loai = new JComboBox<>(new String[]{"Chó", "Mèo"});
    private final JTextField tuKg = new JTextField("0", 5);
    private final JTextField denKg = new JTextField("5", 5);
    private final JTextField gia = new JTextField(10);
    private final JButton them = new NutDinhDang("+ Thêm mức giá", NutDinhDang.KieuNut.PHU);
    private final JButton sua = new NutDinhDang("Sửa mức giá", NutDinhDang.KieuNut.PHU);
    private final JButton xoa = new NutDinhDang("Xóa mức giá", NutDinhDang.KieuNut.NGUY_HIEM);

    public BoSoanThaoBangGia() {
        DinhDangBang.apDung(bang);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.setOpaque(false);
        JPanel nhap = new JPanel(new GridLayout(2, 2, 8, 6));
        nhap.setOpaque(false);
        nhap.add(hangNhap("Loài", loai));
        nhap.add(hangNhap("Giá dịch vụ (VNĐ)", gia));
        nhap.add(hangNhap("Từ cân (kg)", tuKg));
        nhap.add(hangNhap("Đến cân (kg, trống = ∞)", denKg));
        gia.putClientProperty("JTextField.placeholderText", "Nhập giá...");

        JPanel nut = new JPanel(new GridLayout(1, 3, 6, 0));
        nut.setOpaque(false);
        nut.add(them); nut.add(sua); nut.add(xoa);
        JPanel dau = new JPanel(new BorderLayout(0, 6));
        dau.setOpaque(false);
        dau.add(nhap, BorderLayout.CENTER); dau.add(nut, BorderLayout.SOUTH);
        panel.add(dau, BorderLayout.NORTH);
        JScrollPane cuon = new JScrollPane(bang);
        cuon.setPreferredSize(new Dimension(350, 140));
        cuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        panel.add(cuon, BorderLayout.CENTER);
        them.addActionListener(e -> capNhat(false));
        sua.addActionListener(e -> capNhat(true));
        xoa.addActionListener(e -> {
            int r = bang.getSelectedRow();
            if (r >= 0) model.removeRow(bang.convertRowIndexToModel(r));
        });
        bang.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || bang.getSelectedRow() < 0) return;
            int r = bang.convertRowIndexToModel(bang.getSelectedRow());
            loai.setSelectedItem(model.getValueAt(r, 0));
            double[] khoang = docKhoang(String.valueOf(model.getValueAt(r, 1)));
            tuKg.setText(soCan(khoang[0]));
            denKg.setText(Double.isInfinite(khoang[1]) ? "" : soCan(khoang[1]));
            gia.setText(String.valueOf(TienTe.doc(String.valueOf(model.getValueAt(r, 2)))));
        });
    }

    private static JPanel hangNhap(String nhan, JComponent oNhap) {
        JPanel hang = new JPanel(new BorderLayout(0, 4));
        hang.setOpaque(false);
        JLabel tieuDe = new JLabel(nhan);
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        tieuDe.setForeground(MauUngDung.VAN_BAN_XAM);
        oNhap.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        hang.add(tieuDe, BorderLayout.NORTH);
        hang.add(oNhap, BorderLayout.CENTER);
        return hang;
    }

    private void capNhat(boolean laSua) {
        try {
            double tu = Double.parseDouble(tuKg.getText().trim().replace(',', '.'));
            double den = denKg.getText().isBlank() ? Double.POSITIVE_INFINITY
                    : Double.parseDouble(denKg.getText().trim().replace(',', '.'));
            long soTien = TienTe.doc(gia.getText());
            if (!Double.isFinite(tu) || Double.isNaN(den)
                    || (!denKg.getText().isBlank() && !Double.isFinite(den)) || tu < 0 || den <= tu)
                throw new IllegalArgumentException("Cân nặng phải thỏa 0 ≤ từ kg < đến kg.");
            if (soTien <= 0) throw new IllegalArgumentException("Giá phải lớn hơn 0.");
            int dong = laSua && bang.getSelectedRow() >= 0
                ? bang.convertRowIndexToModel(bang.getSelectedRow()) : -1;
            if (laSua && dong < 0) throw new IllegalArgumentException("Hãy chọn mức giá cần sửa.");
            String khoang = Double.isInfinite(den) ? ">= " + soCan(tu) + "kg"
                    : tu == 0 ? "< " + soCan(den) + "kg"
                    : soCan(tu) + " - " + soCan(den) + "kg";
            if (dong >= 0) {
                String cu = String.valueOf(model.getValueAt(dong, 1));
                double[] canCu = docKhoang(cu);
                if (tu == canCu[0] && den == canCu[1]) khoang = cu;
            }
            for (int i = 0; i < model.getRowCount(); i++) {
                if (i == dong || !loai.getSelectedItem().equals(model.getValueAt(i, 0))) continue;
                double[] p = docKhoang(String.valueOf(model.getValueAt(i, 1)));
                if (tu < p[1] && den > p[0])
                    throw new IllegalArgumentException("Khoảng cân trùng với mức giá khác của cùng loài.");
            }
            Object[] duLieu = {loai.getSelectedItem(), khoang, TienTe.dinhDang(soTien)};
            if (laSua) for (int c = 0; c < 3; c++) model.setValueAt(duLieu[c], dong, c);
            else model.addRow(duLieu);
            bang.clearSelection(); gia.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, ex.getMessage(), "Bảng giá không hợp lệ",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override public JComponent layThanhPhan() { return panel; }

    private static String soCan(double kg) {
        return java.math.BigDecimal.valueOf(kg).stripTrailingZeros().toPlainString();
    }

    private static double[] docKhoang(String nhan) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+(?:[.,]\\d+)?").matcher(nhan);
        java.util.List<Double> so = new java.util.ArrayList<>();
        while (m.find()) so.add(Double.parseDouble(m.group().replace(',', '.')));
        if (so.size() == 2) return new double[]{so.get(0), so.get(1)};
        if (so.size() == 1 && nhan.contains("<")) return new double[]{0, so.get(0)};
        if (so.size() == 1 && nhan.contains(">")) return new double[]{so.get(0), Double.POSITIVE_INFINITY};
        if (nhan.toLowerCase(java.util.Locale.ROOT).contains("mọi"))
            return new double[]{0, Double.POSITIVE_INFINITY};
        throw new IllegalArgumentException("Khoảng cân nặng không hợp lệ: " + nhan);
    }

    @Override public String lay() {
        StringBuilder kq = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i > 0) kq.append(';');
            kq.append(model.getValueAt(i, 0)).append('|')
                .append(model.getValueAt(i, 1)).append('|')
                .append(TienTe.doc(String.valueOf(model.getValueAt(i, 2))));
        }
        return kq.toString();
    }

    @Override public void dat(String s) {
        model.setRowCount(0);
        if (s == null || s.isBlank()) return;
        for (String dong : s.split(";")) {
            String[] p = dong.split("\\|", -1);
            if (p.length == 3) model.addRow(new Object[]{p[0], p[1], TienTe.dinhDang(TienTe.doc(p[2]))});
        }
    }

    @Override public void datDuocBat(boolean bat) {
        bang.setEnabled(bat);
        loai.setEnabled(bat); tuKg.setEnabled(bat); denKg.setEnabled(bat); gia.setEnabled(bat);
        them.setEnabled(bat); sua.setEnabled(bat); xoa.setEnabled(bat);
    }

    public static long layGiaNhoNhat(String s) {
        long min = -1;
        if (s == null) return min;
        for (String dong : s.split(";")) {
            String[] p = dong.split("\\|", -1);
            if (p.length != 3) continue;
            long v = TienTe.doc(p[2]);
            if (v >= 0 && (min < 0 || v < min)) min = v;
        }
        return min;
    }

    public static long layGiaCho(String s, String loai, double kg) {
        if (s == null) return -1;
        for (String dong : s.split(";")) {
            String[] p = dong.split("\\|", -1);
            if (p.length != 3 || !p[0].trim().equalsIgnoreCase(loai)) continue;
            String khoang = p[1].toLowerCase().replace("kg", "").replace(" ", "");
            try {
                if (khoang.isEmpty() || khoang.contains("mọi")) return TienTe.doc(p[2]);
                if (khoang.startsWith("<") && kg < Double.parseDouble(khoang.substring(1))) return TienTe.doc(p[2]);
                if (khoang.startsWith(">") && kg > Double.parseDouble(khoang.substring(1))) return TienTe.doc(p[2]);
                if (khoang.contains("-")) {
                    String[] can = khoang.split("-", 2);
                    if (kg >= Double.parseDouble(can[0]) && kg <= Double.parseDouble(can[1]))
                        return TienTe.doc(p[2]);
                }
            } catch (NumberFormatException ignored) { }
        }
        return -1;
    }
}
