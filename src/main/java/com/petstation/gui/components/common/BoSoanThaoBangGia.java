package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho giá bảng bộ soạn thảo.
 *
 * <p>Bảng giá dịch vụ theo loài và khoảng cân nặng. Lưu dạng "Loài|Cân nặng|Giá;Loài|Cân nặng|Giá".
 */
public final class BoSoanThaoBangGia implements BoSoanThaoTruong {
    /** Mô hình. */
    private final DefaultTableModel moHinh =
            new DefaultTableModel(new String[] {"Loài", "Khoảng cân nặng", "Giá (₫)"}, 0);

    /** Bảng. */
    private final JTable tblBang = new JTable(moHinh);

    /** Vùng hiển thị. */
    private final JPanel pnlVungHienThi = new JPanel(new BorderLayout(0, 4));

    /** Thêm. */
    private final JButton btnThem = new JButton("+ Thêm dòng");

    /** Xóa. */
    private final JButton btnXoa = new JButton("Xóa dòng");

    public BoSoanThaoBangGia() {
        DinhDangBang.apDung(tblBang);
        tblBang.setRowHeight(30);
        javax.swing.table.TableRowSorter<DefaultTableModel> sapXep =
                new javax.swing.table.TableRowSorter<>(moHinh);
        tblBang.setRowSorter(sapXep);
        javax.swing.JComboBox<String> cboLoai =
                new javax.swing.JComboBox<>(new String[] {"Tất cả", "Chó", "Mèo"});
        javax.swing.JTextField txtKhoangCan = new javax.swing.JTextField(8);
        Runnable loc =
                () ->
                        sapXep.setRowFilter(
                                new javax.swing.RowFilter<DefaultTableModel, Integer>() {
                                    public boolean include(
                                            Entry<? extends DefaultTableModel, ? extends Integer>
                                                    dong) {
                                        return (cboLoai.getSelectedIndex() == 0
                                                        || dong.getStringValue(0)
                                                                .equals(cboLoai.getSelectedItem()))
                                                && dong.getStringValue(1)
                                                        .toLowerCase(java.util.Locale.ROOT)
                                                        .contains(
                                                                txtKhoangCan
                                                                        .getText()
                                                                        .trim()
                                                                        .toLowerCase(
                                                                                java.util.Locale
                                                                                        .ROOT));
                                    }
                                });
        cboLoai.addActionListener(e -> loc.run());
        txtKhoangCan
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                loc.run();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                loc.run();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                loc.run();
                            }
                        });
        JPanel pnlLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlLoc.setOpaque(false);
        pnlLoc.add(new javax.swing.JLabel("Loài:"));
        pnlLoc.add(cboLoai);
        pnlLoc.add(new javax.swing.JLabel("Khoảng cân:"));
        pnlLoc.add(txtKhoangCan);
        pnlVungHienThi.add(pnlLoc, BorderLayout.NORTH);
        // Khung cuộn.
        JScrollPane scrKhungCuon = new JScrollPane(tblBang);
        scrKhungCuon.setPreferredSize(new Dimension(300, 150));
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        btnThem.addActionListener(suKien -> moHinh.addRow(new Object[] {"Chó", "", ""}));
        btnXoa.addActionListener(
                suKien -> {
                    if (tblBang.isEditing()) tblBang.getCellEditor().stopCellEditing();
                    // Dòng dữ liệu.
                    int dongDuLieu = tblBang.getSelectedRow();
                    if (dongDuLieu >= 0)
                        moHinh.removeRow(tblBang.convertRowIndexToModel(dongDuLieu));
                });
        // Thanh.
        JPanel pnlThanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlThanh.setOpaque(false);
        pnlThanh.add(btnThem);
        pnlThanh.add(btnXoa);
        pnlVungHienThi.setOpaque(false);
        pnlVungHienThi.add(scrKhungCuon, BorderLayout.CENTER);
        pnlVungHienThi.add(pnlThanh, BorderLayout.SOUTH);
    }

    @Override
    public JComponent layThanhPhan() {
        return pnlVungHienThi;
    }

    @Override
    public String lay() {
        if (tblBang.isEditing()) tblBang.getCellEditor().stopCellEditing();
        // Bộ ghép chuỗi.
        StringBuilder boGhepChuoi = new StringBuilder();
        for (int dongDuLieu = 0; dongDuLieu < moHinh.getRowCount(); dongDuLieu++) {
            // Loài.
            String loai = String.valueOf(moHinh.getValueAt(dongDuLieu, 0)).trim();
            // Cân nặng.
            String canNang = String.valueOf(moHinh.getValueAt(dongDuLieu, 1)).trim();
            // Giá.
            long gia = TienTe.doc(String.valueOf(moHinh.getValueAt(dongDuLieu, 2)));
            if (loai.isEmpty() && canNang.isEmpty() && gia < 0) continue;
            if (gia <= 0) {
                throw new IllegalArgumentException(
                    "Dòng " + (dongDuLieu + 1)
                        + ": Giá dịch vụ phải lớn hơn 0."
                );
            }
            if (boGhepChuoi.length() > 0) boGhepChuoi.append(';');
            boGhepChuoi
                    .append(loai)
                    .append('|')
                    .append(canNang)
                    .append('|')
                    .append(gia);
        }
        return boGhepChuoi.toString();
    }

    @Override
    public void dat(String giaTri) {
        moHinh.setRowCount(0);
        if (giaTri == null || giaTri.isBlank()) return;
        for (String phan : giaTri.split(";")) {
            String[] truongDangXet = phan.split("\\|", -1);
            if (truongDangXet.length == 3)
                moHinh.addRow(
                        new Object[] {
                            truongDangXet[0],
                            truongDangXet[1],
                            truongDangXet[2].isEmpty()
                                    ? ""
                                    : TienTe.dinhDang(TienTe.doc(truongDangXet[2]))
                        });
        }
    }

    @Override
    public void datDuocBat(boolean duocBat) {
        tblBang.setEnabled(duocBat);
        btnThem.setEnabled(duocBat);
        btnXoa.setEnabled(duocBat);
    }

    /**
     * Lấy giá nhỏ nhất trong BoSoanThaoBangGia.
     *
     * <p>Giá thấp nhất trong bảng giá, -1 nếu chưa có.
     *
     * @param giaTri giá trị
     * @return kết quả lấy giá nhỏ nhất
     */
    public static long layGiaNhoNhat(String giaTri) {
        // Nhỏ nhất.
        long nhoNhat = -1;
        if (giaTri == null) return nhoNhat;
        for (String phan : giaTri.split(";")) {
            String[] truongDangXet = phan.split("\\|", -1);
            if (truongDangXet.length == 3) {
                long vungDangXet = TienTe.doc(truongDangXet[2]);
                if (vungDangXet >= 0 && (nhoNhat < 0 || vungDangXet < nhoNhat))
                    nhoNhat = vungDangXet;
            }
        }
        return nhoNhat;
    }

    /**
     * Lấy giá cho trong BoSoanThaoBangGia.
     *
     * <p>Tìm giá theo loài và cân nặng (kg); trả -1 nếu không có dòng phù hợp. Dùng cho bước Chọn
     * dịch vụ Spa.
     *
     * @param giaTri giá trị
     * @param loai loài
     * @param canNangKg cân nặng kg
     * @return kết quả lấy giá cho
     */
    public static long layGiaCho(String giaTri, String loai, double canNangKg) {
        if (giaTri == null) return -1;
        for (String phan : giaTri.split(";")) {
            String[] truongDangXet = phan.split("\\|", -1);
            if (truongDangXet.length != 3 || !truongDangXet[0].trim().equalsIgnoreCase(loai))
                continue;
            if (layKhopCanNang(truongDangXet[1], canNangKg)) return TienTe.doc(truongDangXet[2]);
        }
        return -1;
    }

    private static boolean layKhopCanNang(String khoang, double kg) {
        // Dòng dữ liệu.
        String dongDuLieu = khoang.toLowerCase().replace("kg", "").replace(" ", "");
        if (dongDuLieu.isEmpty() || dongDuLieu.contains("mọi")) return true;
        try {
            if (dongDuLieu.startsWith("<")) return kg < Double.parseDouble(dongDuLieu.substring(1));
            if (dongDuLieu.startsWith(">")) return kg > Double.parseDouble(dongDuLieu.substring(1));
            if (dongDuLieu.contains("-")) {
                String[] vungDangXet = dongDuLieu.split("-");
                return kg >= Double.parseDouble(vungDangXet[0])
                        && kg <= Double.parseDouble(vungDangXet[1]);
            }
        } catch (NumberFormatException boQua) {
        }
        return false;
    }
}
