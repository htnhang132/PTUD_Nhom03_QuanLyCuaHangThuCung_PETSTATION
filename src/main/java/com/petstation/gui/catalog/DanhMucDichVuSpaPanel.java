package com.petstation.gui.catalog;

import com.petstation.model.LoaiDanhMuc;
import com.petstation.service.DanhMucService;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.ThanhDanhSach;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.XuatExcel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Danh mục dịch vụ Spa: giao diện Swing độc lập, sử dụng Service/DAO của PETSTATION.
 * Cấu trúc dữ liệu dịch vụ: [mã, tên, mô tả, thời gian, bảng giá, trạng thái].
 */
public final class DanhMucDichVuSpaPanel extends JPanel {
    private final DanhMucService.CacDong duLieu =
        DanhMucService.chuanBi(LoaiDanhMuc.DICH_VU_SPA, false);
    private final List<Object[]> dangHienThi = new ArrayList<>();
    private Object[] banGhiDangChon;
    private boolean dangSua = false;

    private final JTextField txtTim = new JTextField(20);
    private final JComboBox<String> cboLoc = new JComboBox<>(
        new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"});
    private final DefaultTableModel modelDichVu = new DefaultTableModel(
        new String[]{"Mã DV", "Tên dịch vụ", "Thời gian", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tblDichVu = new JTable(modelDichVu);

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JSpinner spThoiGian = new JSpinner(new SpinnerNumberModel(60, 1, 1440, 5));
    private final JTextArea txtMoTa = new JTextArea(3, 20);
    private final JComboBox<String> cboTrangThai = new JComboBox<>(
        new String[]{"Đang hoạt động", "Ngừng hoạt động"});

    private final JComboBox<String> cboLoai = new JComboBox<>(new String[]{"Chó", "Mèo"});
    private final JTextField txtTuKg = new JTextField("0", 6);
    private final JTextField txtDenKg = new JTextField("5", 6);
    private final JTextField txtGia = new JTextField(10);
    private final DefaultTableModel modelGia = new DefaultTableModel(
        new String[]{"Loài", "Từ kg", "Đến kg", "Giá (VNĐ)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tblGia = new JTable(modelGia);
    private final JButton btnThemGia = new JButton("+ Thêm mức giá");
    private final JButton btnSuaGia = new JButton("Sửa mức giá");
    private final JButton btnXoaGia = new JButton("Xóa mức giá");

    private final JButton btnThem = new NutDinhDang("+ Thêm", NutDinhDang.KieuNut.CHINH);
    private final JButton btnSua = new NutDinhDang("Sửa", NutDinhDang.KieuNut.PHU);
    private final JButton btnLuu = new JButton("Lưu dịch vụ");
    private final JButton btnHuy = new JButton("Hủy");
    private final JButton btnTaiLai = new NutDinhDang("Tải lại", NutDinhDang.KieuNut.TRONG_SUOT);
    private final JLabel lblSoKetQua = new JLabel();
    private final JLabel lblTrangThai = new JLabel("Sẵn sàng");

    public DanhMucDichVuSpaPanel() {
        taoGiaoDien();
        trangTriGiaoDien();
        dangKySuKien();
        datCheDoNhap(false);
        taiDuLieu();
    }

    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel dauTrang = new JPanel(new BorderLayout(0, 4));
        JLabel tieuDe = new JLabel("Danh mục dịch vụ Spa");
        tieuDe.setForeground(MauUngDung.VAN_BAN_TOI);
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        dauTrang.add(ThanhDanhSach.tieuDe(tieuDe, lblSoKetQua), BorderLayout.NORTH);
        ThanhDanhSach thanhCongCu = new ThanhDanhSach();
        txtTim.setName("timKiemDanhMuc");
        txtTim.setPreferredSize(new Dimension(280, 36));
        thanhCongCu.add(txtTim);
        thanhCongCu.themBoLoc("Trạng thái:", cboLoc);
        JButton btnXoaLoc = new NutDinhDang("Xóa lọc", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXoaLoc.addActionListener(e -> {
            txtTim.setText("");
            cboLoc.setSelectedIndex(0);
        });
        thanhCongCu.add(btnXoaLoc);
        thanhCongCu.add(btnThem);
        thanhCongCu.add(btnSua);
        thanhCongCu.add(btnTaiLai);
        JButton btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(e -> XuatExcel.xuatBang(this, tblDichVu, "Danh mục dịch vụ Spa"));
        thanhCongCu.add(btnXuat);
        dauTrang.add(thanhCongCu, BorderLayout.CENTER);
        add(dauTrang, BorderLayout.NORTH);

        JPanel benTrai = new JPanel(new BorderLayout(8, 8));
        tblDichVu.setRowHeight(30);
        tblDichVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        benTrai.add(new JScrollPane(tblDichVu), BorderLayout.CENTER);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        form.add(tieuDeNhom("THÔNG TIN DỊCH VỤ"));
        form.add(hang("Mã dịch vụ", txtMa));
        form.add(hang("Tên dịch vụ *", txtTen));
        form.add(hang("Thời gian (phút) *", spThoiGian));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        form.add(hang("Mô tả", new JScrollPane(txtMoTa)));
        form.add(hang("Trạng thái", cboTrangThai));

        form.add(Box.createVerticalStrut(16));
        form.add(tieuDeNhom("BẢNG GIÁ DỊCH VỤ"));
        form.add(hang("Loài", cboLoai));
        form.add(hang("Từ cân nặng (kg)", txtTuKg));
        form.add(hang("Đến cân nặng (kg)", txtDenKg));
        form.add(hang("Giá dịch vụ (VNĐ)", txtGia));
        JPanel nutGia = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        nutGia.add(btnThemGia);
        nutGia.add(btnSuaGia);
        nutGia.add(btnXoaGia);
        nutGia.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(nutGia);
        tblGia.setRowHeight(28);
        tblGia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cuonGia = new JScrollPane(tblGia);
        cuonGia.setPreferredSize(new Dimension(500, 170));
        cuonGia.setMinimumSize(new Dimension(250, 150));
        cuonGia.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(cuonGia);
        form.add(Box.createVerticalStrut(10));

        JPanel nutLuu = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nutLuu.add(btnHuy);
        nutLuu.add(btnLuu);
        nutLuu.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(nutLuu);

        JScrollPane cuonForm = new JScrollPane(form);
        cuonForm.getVerticalScrollBar().setUnitIncrement(18);
        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, benTrai, cuonForm);
        chia.setResizeWeight(0.40);
        chia.setBorder(null);
        chia.setContinuousLayout(true);
        chia.setDividerLocation(440);
        add(chia, BorderLayout.CENTER);
        add(lblTrangThai, BorderLayout.SOUTH);
        txtMa.setEditable(false);
    }

    private void trangTriGiaoDien() {
        toNenTrang(this);
        setBackground(MauUngDung.NEN_KEM);
        JComponent dauTrang = (JComponent) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        dauTrang.setBackground(MauUngDung.NEN_KEM);
        dauTrang.setBorder(BorderFactory.createEmptyBorder(4, 0, 12, 0));
        txtTim.putClientProperty("JTextField.placeholderText", "Tìm theo mã hoặc tên dịch vụ...");
        txtMa.setBackground(MauUngDung.NEN_KEM);
        txtMa.setForeground(MauUngDung.NAU);
        txtMa.setToolTipText("Mã được tự sinh: SPA và 8 chữ số");
        for (JTable bang : new JTable[]{tblDichVu, tblGia}) {
            bang.setRowHeight(36);
            bang.setShowVerticalLines(false);
            bang.setGridColor(MauUngDung.VIEN);
            bang.setSelectionBackground(MauUngDung.DO_NHAT);
            bang.setSelectionForeground(MauUngDung.VAN_BAN_TOI);
            bang.getTableHeader().setBackground(MauUngDung.NEN_KEM);
            bang.getTableHeader().setForeground(MauUngDung.NAU);
            bang.getTableHeader().setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
            bang.getTableHeader().setPreferredSize(new Dimension(0, 38));
            bang.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean selected, boolean focus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, selected, focus, row, column);
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    if (!selected) {
                        setBackground(row % 2 == 0 ? Color.WHITE : MauUngDung.DONG_XEN_KE);
                        setForeground(MauUngDung.VAN_BAN_TOI);
                        if (table == tblDichVu && column == 3) {
                            setForeground("Đang hoạt động".equals(value)
                                    ? MauUngDung.XANH_LA : MauUngDung.CAM);
                        }
                    }
                    return this;
                }
            });
        }
        toMauNut(btnLuu, MauUngDung.XANH_LA, Color.WHITE);
        toMauNut(btnThemGia, MauUngDung.NEN_KEM, MauUngDung.NAU);
        toMauNut(btnSuaGia, MauUngDung.NEN_KEM, MauUngDung.NAU);
        toMauNut(btnXoaGia, MauUngDung.DO_NHAT, MauUngDung.VAN_BAN_NGUY_HIEM);
        toMauNut(btnHuy, Color.WHITE, MauUngDung.VAN_BAN_XAM);
        lblTrangThai.setOpaque(true);
        lblTrangThai.setBackground(MauUngDung.NEN_KEM);
        lblTrangThai.setForeground(MauUngDung.NAU);
        lblTrangThai.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
    }

    private void toNenTrang(Container cha) {
        for (Component con : cha.getComponents()) {
            if (con instanceof JPanel || con instanceof JViewport) con.setBackground(Color.WHITE);
            if (con instanceof JScrollPane cuon) {
                cuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
            }
            if (con instanceof Container nhom) toNenTrang(nhom);
        }
    }

    private void toMauNut(JButton nut, Color nen, Color chu) {
        nut.setBackground(nen);
        nut.setForeground(chu);
        nut.setOpaque(true);
        nut.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nut.setMargin(new Insets(8, 14, 8, 14));
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.putClientProperty("JButton.buttonType", "roundRect");
    }

    private JLabel tieuDeNhom(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setForeground(MauUngDung.NAU);
        label.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
        return label;
    }

    private JPanel hang(String nhan, JComponent oNhap) {
        JPanel p = new JPanel(new BorderLayout(8, 5));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, oNhap instanceof JScrollPane ? 105 : 66));
        p.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));
        p.add(new JLabel(nhan), BorderLayout.NORTH);
        p.add(oNhap, BorderLayout.CENTER);
        return p;
    }

    private void dangKySuKien() {
        txtTim.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locDanhSach(); }
            public void removeUpdate(DocumentEvent e) { locDanhSach(); }
            public void changedUpdate(DocumentEvent e) { locDanhSach(); }
        });
        cboLoc.addActionListener(e -> locDanhSach());
        btnThem.addActionListener(e -> batDauThem());
        btnSua.addActionListener(e -> batDauSua());
        btnLuu.addActionListener(e -> luuDichVu());
        btnHuy.addActionListener(e -> huyThaoTac());
        btnTaiLai.addActionListener(e -> {
            if (xacNhanBoThayDoi()) taiDuLieu();
        });
        btnThemGia.addActionListener(e -> themMucGia());
        btnSuaGia.addActionListener(e -> suaMucGia());
        btnXoaGia.addActionListener(e -> {
            int row = tblGia.getSelectedRow();
            if (row >= 0) modelGia.removeRow(row);
        });
        tblGia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblGia.getSelectedRow() >= 0) {
                int row = tblGia.getSelectedRow();
                cboLoai.setSelectedItem(modelGia.getValueAt(row, 0));
                txtTuKg.setText(String.valueOf(modelGia.getValueAt(row, 1)));
                txtDenKg.setText(String.valueOf(modelGia.getValueAt(row, 2)));
                txtGia.setText(String.valueOf(modelGia.getValueAt(row, 3)));
            }
        });
        tblDichVu.getSelectionModel().addListSelectionListener(e -> {
            btnSua.setEnabled(!dangSua && tblDichVu.getSelectedRow() >= 0);
            if (e.getValueIsAdjusting() || tblDichVu.getSelectedRow() < 0) return;
            if (dangSua && !xacNhanBoThayDoi()) return;
            int row = tblDichVu.getSelectedRow();
            if (row < dangHienThi.size()) hienThiBanGhi(dangHienThi.get(row));
        });
    }

    private void taiDuLieu() {
        lblTrangThai.setText("Đang tải danh sách...");
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                return duLieu.taiBanSao();
            }
            @Override protected void done() {
                try {
                    duLieu.clear();
                    duLieu.addAll(get());
                    locDanhSach();
                    lblTrangThai.setText("Đã tải " + duLieu.size() + " dịch vụ");
                } catch (Exception ex) { baoLoi("Không tải được dịch vụ", ex); }
            }
        }.execute();
    }

    private void locDanhSach() {
        String tim = txtTim.getText().trim().toLowerCase(Locale.ROOT);
        String trangThai = String.valueOf(cboLoc.getSelectedItem());
        dangHienThi.clear();
        modelDichVu.setRowCount(0);
        for (Object[] d : duLieu) {
            String ma = String.valueOf(d[0]);
            String ten = String.valueOf(d[1]);
            if (!((ma + " " + ten).toLowerCase(Locale.ROOT).contains(tim))) continue;
            if (!"Tất cả".equals(trangThai) && !trangThai.equals(String.valueOf(d[5]))) continue;
            dangHienThi.add(d);
            modelDichVu.addRow(new Object[]{ma, ten, d[3], d[5]});
        }
        lblSoKetQua.setText(ThanhDanhSach.soKetQua(dangHienThi.size(), duLieu.size()));
    }

    private void hienThiBanGhi(Object[] d) {
        banGhiDangChon = d;
        dangSua = false;
        txtMa.setText(String.valueOf(d[0]));
        txtTen.setText(String.valueOf(d[1]));
        txtMoTa.setText(String.valueOf(d[2]));
        try { spThoiGian.setValue(Integer.parseInt(String.valueOf(d[3]))); }
        catch (Exception ex) { spThoiGian.setValue(60); }
        cboTrangThai.setSelectedItem(String.valueOf(d[5]));
        napBangGia(String.valueOf(d[4]));
        datCheDoNhap(false);
    }

    private void napBangGia(String chuoi) {
        modelGia.setRowCount(0);
        if (chuoi == null || chuoi.isBlank()) return;
        for (String muc : chuoi.split(";")) {
            String[] p = muc.split("\\|", -1);
            if (p.length != 3) continue;
            String khoang = p[1].trim();
            String tu = "0", den = "999";
            java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("\\d+(?:[.,]\\d+)?").matcher(khoang);
            List<String> so = new ArrayList<>();
            while (matcher.find()) so.add(matcher.group().replace(',', '.'));
            if (so.size() >= 2) { tu = so.get(0); den = so.get(1); }
            else if (so.size() == 1) {
                if (khoang.contains(">")) tu = so.get(0);
                else den = so.get(0);
            }
            modelGia.addRow(new Object[]{p[0], tu, den, p[2]});
        }
    }

    private void datCheDoNhap(boolean choNhap) {
        dangSua = choNhap;
        txtTen.setEditable(choNhap);
        txtMoTa.setEditable(choNhap);
        spThoiGian.setEnabled(choNhap);
        cboTrangThai.setEnabled(choNhap);
        cboLoai.setEnabled(choNhap);
        txtTuKg.setEditable(choNhap);
        txtDenKg.setEditable(choNhap);
        txtGia.setEditable(choNhap);
        btnThemGia.setEnabled(choNhap);
        btnSuaGia.setEnabled(choNhap);
        btnXoaGia.setEnabled(choNhap);
        btnLuu.setEnabled(choNhap);
        btnHuy.setEnabled(choNhap);
        btnSua.setEnabled(!choNhap && banGhiDangChon != null);
    }

    private void batDauThem() {
        if (!xacNhanBoThayDoi()) return;
        banGhiDangChon = null;
        tblDichVu.clearSelection();
        txtMa.setText(taoMaMoi());
        txtTen.setText("");
        txtMoTa.setText("");
        spThoiGian.setValue(60);
        cboTrangThai.setSelectedItem("Đang hoạt động");
        modelGia.setRowCount(0);
        datCheDoNhap(true);
        txtTen.requestFocusInWindow();
        lblTrangThai.setText("Đang thêm dịch vụ mới");
    }

    private String taoMaMoi() {
        long lonNhat = 0;
        for (Object[] d : duLieu) {
            String so = String.valueOf(d[0]).replaceAll("\\D", "");
            if (!so.isBlank()) {
                try { lonNhat = Math.max(lonNhat, Long.parseLong(so)); }
                catch (NumberFormatException ignored) { }
            }
        }
        return "SPA" + String.format(Locale.ROOT, "%08d", lonNhat + 1);
    }

    private void batDauSua() {
        if (banGhiDangChon == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dịch vụ trước.");
            return;
        }
        datCheDoNhap(true);
        lblTrangThai.setText("Đang sửa " + banGhiDangChon[0]);
    }

    private boolean xacNhanBoThayDoi() {
        return !dangSua || JOptionPane.showConfirmDialog(this,
            "Bỏ các thay đổi chưa lưu?", "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void huyThaoTac() {
        if (!xacNhanBoThayDoi()) return;
        if (banGhiDangChon != null) hienThiBanGhi(banGhiDangChon);
        else {
            txtMa.setText("");
            txtTen.setText("");
            txtMoTa.setText("");
            modelGia.setRowCount(0);
            datCheDoNhap(false);
        }
        lblTrangThai.setText("Đã hủy thao tác");
    }

    private BigDecimal docSo(String s, String ten) {
        try { return new BigDecimal(s.trim().replace(',', '.')); }
        catch (Exception ex) { throw new IllegalArgumentException(ten + " phải là số hợp lệ."); }
    }

    private void themMucGia() { capNhatMucGia(false); }
    private void suaMucGia() { capNhatMucGia(true); }

    private void capNhatMucGia(boolean sua) {
        try {
            BigDecimal tu = docSo(txtTuKg.getText(), "Cân nặng bắt đầu");
            BigDecimal den = docSo(txtDenKg.getText(), "Cân nặng kết thúc");
            String giaChuoi = txtGia.getText().trim().replace(".", "").replace(",", "");
            if (!giaChuoi.matches("\\d+")) throw new IllegalArgumentException("Giá phải là số nguyên dương.");
            long gia = Long.parseLong(giaChuoi);
            if (tu.signum() < 0 || den.compareTo(tu) <= 0 || den.compareTo(new BigDecimal("999")) > 0)
                throw new IllegalArgumentException("Khoảng cân phải thỏa 0 ≤ từ kg < đến kg ≤ 999.");
            if (gia <= 0) throw new IllegalArgumentException("Giá phải lớn hơn 0.");
            int dongSua = sua ? tblGia.getSelectedRow() : -1;
            if (sua && dongSua < 0) throw new IllegalArgumentException("Chọn một mức giá cần sửa.");
            String loai = String.valueOf(cboLoai.getSelectedItem());
            for (int i = 0; i < modelGia.getRowCount(); i++) {
                if (i == dongSua || !loai.equals(modelGia.getValueAt(i, 0))) continue;
                BigDecimal tuKhac = docSo(String.valueOf(modelGia.getValueAt(i, 1)), "Từ kg");
                BigDecimal denKhac = docSo(String.valueOf(modelGia.getValueAt(i, 2)), "Đến kg");
                if (tu.compareTo(denKhac) < 0 && den.compareTo(tuKhac) > 0)
                    throw new IllegalArgumentException("Khoảng cân trùng với dòng " + (i + 1) + ".");
            }
            Object[] dong = {loai, tu.stripTrailingZeros().toPlainString(),
                den.stripTrailingZeros().toPlainString(), gia};
            if (sua) for (int j = 0; j < 4; j++) modelGia.setValueAt(dong[j], dongSua, j);
            else modelGia.addRow(dong);
            tblGia.clearSelection();
            txtGia.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Bảng giá chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private String ghepBangGia() {
        if (modelGia.getRowCount() == 0)
            throw new IllegalArgumentException("Cần thêm ít nhất một mức giá.");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < modelGia.getRowCount(); i++) {
            if (s.length() > 0) s.append(';');
            s.append(modelGia.getValueAt(i, 0)).append('|')
                .append(modelGia.getValueAt(i, 1)).append('-')
                .append(modelGia.getValueAt(i, 2)).append("kg|")
                .append(modelGia.getValueAt(i, 3));
        }
        return s.toString();
    }

    private void luuDichVu() {
        try {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) throw new IllegalArgumentException("Tên dịch vụ không được trống.");
            for (Object[] d : duLieu) {
                if (d != banGhiDangChon && ten.equalsIgnoreCase(String.valueOf(d[1]).trim()))
                    throw new IllegalArgumentException("Tên dịch vụ đã tồn tại.");
            }
            Object[] giaTri = {
                txtMa.getText().trim(), ten, txtMoTa.getText().trim(),
                String.valueOf(spThoiGian.getValue()), ghepBangGia(),
                String.valueOf(cboTrangThai.getSelectedItem())
            };
            Object[] truoc = banGhiDangChon == null ? null : banGhiDangChon.clone();
            DanhMucService.luu(duLieu, truoc, giaTri);
            datCheDoNhap(false);
            banGhiDangChon = null;
            JOptionPane.showMessageDialog(this, "Đã lưu dịch vụ " + giaTri[0]);
            taiDuLieu();
        } catch (Exception ex) { baoLoi("Không thể lưu dịch vụ", ex); }
    }

    private void baoLoi(String tieuDe, Exception ex) {
        String thongBao = ex.getMessage();
        if (thongBao == null && ex.getCause() != null) thongBao = ex.getCause().getMessage();
        JOptionPane.showMessageDialog(this, thongBao == null ? tieuDe : thongBao,
            tieuDe, JOptionPane.ERROR_MESSAGE);
        lblTrangThai.setText(tieuDe);
    }
}
