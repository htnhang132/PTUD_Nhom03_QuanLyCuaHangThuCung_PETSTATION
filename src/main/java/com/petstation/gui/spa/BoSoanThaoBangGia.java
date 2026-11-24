package com.petstation.gui.spa;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.thanhphan.BoSoanThaoTruong;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

public final class BoSoanThaoBangGia implements BoSoanThaoTruong {
    private final DefaultTableModel model =
            DinhDangBang.taoMoHinhChiDoc(new String[] {"Loài", "Khoảng cân nặng", "Giá (₫)"});
    private final JTable bang = new JTable(model);
    private final JPanel panel = new JPanel(new BorderLayout(5, 8));
    private final JComboBox<String> loai = new JComboBox<>(new String[] {"Chó", "Mèo"});
    private final JTextField tuKg = new JTextField("0", 4);
    private final JTextField denKg = new JTextField("5", 4);
    private final JTextField gia = new JTextField(6);
    private final JButton them = new NutDinhDang("+ Thêm mức giá", NutDinhDang.KieuNut.PHU);
    private final JButton taoNhanh = new NutDinhDang("4 mốc cân", NutDinhDang.KieuNut.TRONG_SUOT);
    private final JButton sua = new NutDinhDang("Sửa mức giá", NutDinhDang.KieuNut.PHU);
    private final JButton xoa = new NutDinhDang("Xóa mức giá", NutDinhDang.KieuNut.NGUY_HIEM);

    public BoSoanThaoBangGia() {
        DinhDangBang.apDung(bang);
        bang.setRowHeight(32);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.setOpaque(false);
        this.dau = taoVungNhapBangGia();
        panel.add(dau, BorderLayout.NORTH);
        if (bang.getColumnModel().getColumnCount() >= 3) {
            bang.getColumnModel().getColumn(0).setPreferredWidth(55);
            bang.getColumnModel().getColumn(1).setPreferredWidth(155);
            bang.getColumnModel().getColumn(2).setPreferredWidth(130);
        }
        JScrollPane cuon = DinhDangBang.taoBangKhongCuon(bang, new Dimension(340, 96));
        panel.add(cuon, BorderLayout.CENTER);
        them.addActionListener(e -> capNhat(false));
        taoNhanh.setToolTipText(
                "Tạo nhanh 4 mốc cân (<5kg, 5-10kg, 10-20kg, >=20kg) theo giá cơ bản");
        taoNhanh.addActionListener(e -> taoNhanhBonMocCan());
        sua.addActionListener(e -> capNhat(true));
        xoa.addActionListener(
                e -> DinhDangBang.xoaDongDangChon(bang, model, this::capNhatKichThuocBang));
        bang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            capNhatTrangThaiNut();
                            if (e.getValueIsAdjusting() || bang.getSelectedRow() < 0) return;
                            int r = bang.convertRowIndexToModel(bang.getSelectedRow());
                            loai.setSelectedItem(model.getValueAt(r, 0));
                            double[] khoang = docKhoang(String.valueOf(model.getValueAt(r, 1)));
                            tuKg.setText(soCan(khoang[0]));
                            denKg.setText(Double.isInfinite(khoang[1]) ? "" : soCan(khoang[1]));
                            gia.setText(
                                    String.valueOf(
                                            DinhDang.doc(String.valueOf(model.getValueAt(r, 2)))));
                        });
        this.cuon = cuon;
        capNhatTrangThaiNut();
        capNhatKichThuocBang();
    }

    private void capNhatTrangThaiNut() {
        boolean coChon = bang.getSelectedRow() >= 0;
        sua.setEnabled(coChon);
        xoa.setEnabled(coChon);
    }

    private final JPanel dau;
    private final JScrollPane cuon;

    private JPanel taoVungNhapBangGia() {
        JPanel nhap = new JPanel(new GridLayout(2, 2, 6, 4));
        nhap.setOpaque(false);
        nhap.add(hangNhap("Loài", loai));
        nhap.add(hangNhap("Giá dịch vụ (₫)", gia));
        nhap.add(hangNhap("Từ cân (kg)", tuKg));
        nhap.add(hangNhap("Đến cân (kg, trống = ∞)", denKg));
        gia.putClientProperty("JTextField.placeholderText", "Nhập giá...");
        Border viNut = BorderFactory.createEmptyBorder(3, 4, 3, 4);
        them.setBorder(viNut);
        taoNhanh.setBorder(viNut);
        sua.setBorder(viNut);
        xoa.setBorder(viNut);
        JPanel nut = new JPanel(new GridLayout(2, 2, 6, 6));
        nut.setOpaque(false);
        nut.add(them);
        nut.add(taoNhanh);
        nut.add(sua);
        nut.add(xoa);
        JPanel vungDau = new JPanel(new BorderLayout(0, 5));
        vungDau.setOpaque(false);
        vungDau.add(nhap, BorderLayout.CENTER);
        vungDau.add(nut, BorderLayout.SOUTH);
        return vungDau;
    }

    private void capNhatKichThuocBang() {
        DinhDangBang.coGianChieuCaoBangTheoNoiDung(bang, cuon, panel, model.getRowCount());
        if (bang.getColumnModel().getColumnCount() >= 3) {
            bang.getColumnModel().getColumn(0).setPreferredWidth(55);
            bang.getColumnModel().getColumn(1).setPreferredWidth(155);
            bang.getColumnModel().getColumn(2).setPreferredWidth(130);
        }
    }

    private void taoNhanhBonMocCan() {
        long giaCoBan = DinhDang.doc(gia.getText());
        if (giaCoBan <= 0) {
            HopThoai.hienThiCanhBao(
                    panel, "Nhập Giá cơ bản (₫) vào ô Giá dịch vụ trước khi tạo nhanh 4 mốc cân.");
            gia.requestFocusInWindow();
            return;
        }
        Object loaiChon = loai.getSelectedItem();
        boolean coMocCu = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (loaiChon.equals(model.getValueAt(i, 0))) {
                coMocCu = true;
                break;
            }
        }
        if (coMocCu
                && !HopThoai.xacNhanNguyHiem(
                        panel,
                        "Loài \""
                                + loaiChon
                                + "\" đã có bảng giá. Thay thế toàn bộ bằng 4 mốc cân mới?",
                        "Thay thế bảng giá")) {
            return;
        }
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            if (loaiChon.equals(model.getValueAt(i, 0))) {
                model.removeRow(i);
            }
        }
        String[] cacMoc = {"< 5kg", "5 - 10kg", "10 - 20kg", ">= 20kg"};
        for (int i = 0; i < cacMoc.length; i++) {
            model.addRow(
                    new Object[] {loaiChon, cacMoc[i], DinhDang.dinhDang(giaCoBan + i * 50_000L)});
        }
        gia.setText("");
        tuKg.setText("0");
        denKg.setText("5");
        capNhatTrangThaiNut();
        capNhatKichThuocBang();
    }

    @Override
    public boolean xacNhanDangNhap() {
        if (dau.isVisible() && !gia.getText().isBlank()) {
            return capNhat(bang.getSelectedRow() >= 0);
        }
        return true;
    }

    private static JPanel hangNhap(String nhan, JComponent oNhap) {
        oNhap.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        return DinhDangBang.taoHangNhap(nhan, oNhap, 4, 12);
    }

    private boolean capNhat(boolean laSua) {
        try {
            double tu = Double.parseDouble(tuKg.getText().trim().replace(',', '.'));
            double den =
                    denKg.getText().isBlank()
                            ? Double.POSITIVE_INFINITY
                            : Double.parseDouble(denKg.getText().trim().replace(',', '.'));
            long soTien = DinhDang.doc(gia.getText());
            if (!Double.isFinite(tu)
                    || Double.isNaN(den)
                    || (!denKg.getText().isBlank() && !Double.isFinite(den))
                    || tu < 0
                    || den <= tu)
                throw new IllegalArgumentException("Cân nặng phải thỏa 0 ≤ từ kg < đến kg.");
            if (soTien <= 0) throw new IllegalArgumentException("Giá phải lớn hơn 0.");
            int dong =
                    laSua && bang.getSelectedRow() >= 0
                            ? bang.convertRowIndexToModel(bang.getSelectedRow())
                            : -1;
            if (laSua && dong < 0) throw new IllegalArgumentException("Hãy chọn mức giá cần sửa.");
            String khoang =
                    Double.isInfinite(den)
                            ? ">= " + soCan(tu) + "kg"
                            : tu == 0
                                    ? "< " + soCan(den) + "kg"
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
                    throw new IllegalArgumentException(
                            "Khoảng cân trùng với mức giá khác của cùng loài.");
            }
            Object[] duLieu = {loai.getSelectedItem(), khoang, DinhDang.dinhDang(soTien)};
            DinhDangBang.capNhatHoacThemDong(bang, model, laSua, dong, duLieu);
            capNhatKichThuocBang();
            if (!laSua && Double.isFinite(den)) {
                tuKg.setText(soCan(den));
                denKg.setText("");
            }
            gia.setText("");
            return true;
        } catch (Exception ex) {
            HopThoai.hienThiCanhBao(panel, ex.getMessage());
            return false;
        }
    }

    @Override
    public JComponent layThanhPhan() {
        return panel;
    }

    private static String soCan(double kg) {
        return BigDecimal.valueOf(kg).stripTrailingZeros().toPlainString();
    }

    private static double[] docKhoang(String nhan) {
        Matcher m = Pattern.compile("\\d+(?:[.,]\\d+)?").matcher(nhan);
        List<Double> so = new ArrayList<>();
        while (m.find()) so.add(Double.parseDouble(m.group().replace(',', '.')));
        if (so.size() == 2) return new double[] {so.get(0), so.get(1)};
        if (so.size() == 1 && nhan.contains("<")) return new double[] {0, so.get(0)};
        if (so.size() == 1 && nhan.contains(">"))
            return new double[] {so.get(0), Double.POSITIVE_INFINITY};
        if (nhan.toLowerCase(Locale.ROOT).contains("mọi"))
            return new double[] {0, Double.POSITIVE_INFINITY};
        throw new IllegalArgumentException("Khoảng cân nặng không hợp lệ: " + nhan);
    }

    @Override
    public String lay() {
        StringBuilder kq = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i > 0) kq.append(';');
            kq.append(model.getValueAt(i, 0))
                    .append('|')
                    .append(model.getValueAt(i, 1))
                    .append('|')
                    .append(DinhDang.doc(String.valueOf(model.getValueAt(i, 2))));
        }
        return kq.toString();
    }

    @Override
    public void dat(String s) {
        model.setRowCount(0);
        if (s != null && !s.isBlank()) {
            for (String dong : s.split(";")) {
                String[] p = dong.split("\\|", -1);
                if (p.length == 3)
                    model.addRow(new Object[] {p[0], p[1], DinhDang.dinhDang(DinhDang.doc(p[2]))});
            }
        }
        loai.setSelectedIndex(0);
        tuKg.setText("0");
        denKg.setText("5");
        gia.setText("");
        capNhatKichThuocBang();
    }

    @Override
    public void datDuocBat(boolean bat) {
        dau.setVisible(bat);
        bang.setEnabled(bat);
        loai.setEnabled(bat);
        tuKg.setEnabled(bat);
        denKg.setEnabled(bat);
        gia.setEnabled(bat);
        them.setEnabled(bat);
        taoNhanh.setEnabled(bat);
        sua.setEnabled(bat);
        xoa.setEnabled(bat);
        capNhatKichThuocBang();
    }

    public static long layGiaNhoNhat(String s) {
        long min = -1;
        if (s == null) return min;
        for (String dong : s.split(";")) {
            String[] p = dong.split("\\|", -1);
            if (p.length != 3) continue;
            long v = DinhDang.doc(p[2]);
            if (v >= 0 && (min < 0 || v < min)) min = v;
        }
        return min;
    }
}
