package com.petstation.gui.components.common;

import com.petstation.util.TienTe;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Nhập bảng giá Spa bằng ô nhập rõ ràng; giữ định dạng lưu cũ: Loài|Khoảng|Giá. */
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
        JPanel nhap = new JPanel(new GridLayout(0, 1, 0, 8));
        nhap.setOpaque(false);
        nhap.add(hangNhap("Loài", loai));
        nhap.add(hangNhap("Từ cân nặng (kg)", tuKg));
        nhap.add(hangNhap("Đến cân nặng (kg)", denKg));
        nhap.add(hangNhap("Giá dịch vụ (VNĐ)", gia));
        gia.putClientProperty("JTextField.placeholderText", "Nhập giá rồi thêm mức giá");
        // Xếp dọc để các nút không bị cắt trong form dùng chung rộng 380 px.
        JPanel nut = new JPanel(new GridLayout(0, 1, 0, 6));
        nut.setOpaque(false);
        nut.add(them); nut.add(sua); nut.add(xoa);
        JPanel dau = new JPanel(new BorderLayout(0, 6));
        dau.setOpaque(false);
        dau.add(nhap, BorderLayout.CENTER); dau.add(nut, BorderLayout.SOUTH);
        panel.add(dau, BorderLayout.NORTH);
        JScrollPane cuon = new JScrollPane(bang);
        cuon.setPreferredSize(new Dimension(350, 150));
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
            String[] khoang = String.valueOf(model.getValueAt(r, 1)).replace("kg", "").split("-", 2);
            if (khoang.length == 2) { tuKg.setText(khoang[0]); denKg.setText(khoang[1]); }
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
            double den = Double.parseDouble(denKg.getText().trim().replace(',', '.'));
            long soTien = TienTe.doc(gia.getText());
            if (!Double.isFinite(tu) || !Double.isFinite(den) || tu < 0 || den <= tu)
                throw new IllegalArgumentException("Cân nặng phải thỏa 0 ≤ từ kg < đến kg.");
            if (soTien <= 0) throw new IllegalArgumentException("Giá phải lớn hơn 0.");
            int dong = laSua && bang.getSelectedRow() >= 0
                ? bang.convertRowIndexToModel(bang.getSelectedRow()) : -1;
            if (laSua && dong < 0) throw new IllegalArgumentException("Hãy chọn mức giá cần sửa.");
            String khoang = tuKg.getText().trim().replace(',', '.') + "-"
                + denKg.getText().trim().replace(',', '.') + "kg";
            for (int i = 0; i < model.getRowCount(); i++) {
                if (i == dong || !loai.getSelectedItem().equals(model.getValueAt(i, 0))) continue;
                String[] p = String.valueOf(model.getValueAt(i, 1)).replace("kg", "").split("-", 2);
                if (p.length == 2 && tu <= Double.parseDouble(p[1]) && den >= Double.parseDouble(p[0]))
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
