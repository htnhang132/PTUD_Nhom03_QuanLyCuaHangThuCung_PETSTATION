package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class BoSoanThaoTiemPhong implements BoSoanThaoTruong {
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Loại vắc-xin", "Ngày tiêm", "Ngày hết hạn", "Ghi chú"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private final JTable bang = new JTable(model);
    private final JPanel panel = new JPanel(new BorderLayout(5, 6));

    private final JComboBox<String> cboLoai = new JComboBox<>(new String[]{
            "Vắc-xin phòng Dại (Rabies)",
            "Vắc-xin 7 bệnh (Chó - Care, Parvo,...)",
            "Vắc-xin 5 bệnh (Chó)",
            "Vắc-xin 4 bệnh (Mèo)",
            "Vắc-xin tổng hợp",
            "Mũi nhắc lại định kỳ"
    });

    private final JTextField txtNgayTiem = new JTextField(LocalDate.now().format(DINH_DANG_NGAY), 9);
    private final JTextField txtNgayHan = new JTextField(LocalDate.now().plusYears(1).format(DINH_DANG_NGAY), 9);
    private final JTextField txtGhiChu = new JTextField(12);

    private final JButton btnThem = new NutDinhDang("+ Thêm mũi tiêm", NutDinhDang.KieuNut.PHU);
    private final JButton btnSua = new NutDinhDang("Sửa mũi tiêm", NutDinhDang.KieuNut.PHU);
    private final JButton btnXoa = new NutDinhDang("Xóa mũi tiêm", NutDinhDang.KieuNut.NGUY_HIEM);

    public BoSoanThaoTiemPhong() {
        DinhDangBang.apDung(bang);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.setOpaque(false);

        cboLoai.setEditable(true);
        cboLoai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtNgayTiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtNgayHan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtGhiChu.putClientProperty("JTextField.placeholderText", "Ghi chú / Số lô / Sổ tiêm...");

        JButton btnLichTiem = BoChonNgay.taoNutLich(txtNgayTiem, DINH_DANG_NGAY);
        btnLichTiem.setPreferredSize(new Dimension(28, 26));

        JButton btnLichHan = BoChonNgay.taoNutLich(txtNgayHan, DINH_DANG_NGAY);
        btnLichHan.setPreferredSize(new Dimension(28, 26));

        JPanel pnlNgayTiem = new JPanel(new BorderLayout(2, 0));
        pnlNgayTiem.setOpaque(false);
        pnlNgayTiem.add(txtNgayTiem, BorderLayout.CENTER);
        pnlNgayTiem.add(btnLichTiem, BorderLayout.EAST);

        JPanel pnlNgayHan = new JPanel(new BorderLayout(2, 0));
        pnlNgayHan.setOpaque(false);
        pnlNgayHan.add(txtNgayHan, BorderLayout.CENTER);
        pnlNgayHan.add(btnLichHan, BorderLayout.EAST);

        JPanel nhap = new JPanel(new GridLayout(2, 2, 6, 4));
        nhap.setOpaque(false);
        nhap.add(hangNhap("Loại vắc-xin", cboLoai));
        nhap.add(hangNhap("Ghi chú / Phòng khám", txtGhiChu));
        nhap.add(hangNhap("Ngày tiêm *", pnlNgayTiem));
        nhap.add(hangNhap("Ngày hết hạn (tùy chọn)", pnlNgayHan));

        javax.swing.border.Border viNut = BorderFactory.createEmptyBorder(3, 4, 3, 4);
        btnThem.setBorder(viNut);
        btnSua.setBorder(viNut);
        btnXoa.setBorder(viNut);
        JPanel nut = new JPanel(new GridLayout(1, 3, 6, 0));
        nut.setOpaque(false);
        nut.add(btnThem);
        nut.add(btnSua);
        nut.add(btnXoa);

        JPanel dau = new JPanel(new BorderLayout(0, 6));
        dau.setOpaque(false);
        dau.add(nhap, BorderLayout.CENTER);
        dau.add(nut, BorderLayout.SOUTH);
        panel.add(dau, BorderLayout.NORTH);

        bang.setPreferredScrollableViewportSize(new Dimension(320, 110));
        if (bang.getColumnModel().getColumnCount() >= 4) {
            bang.getColumnModel().getColumn(0).setPreferredWidth(130);
            bang.getColumnModel().getColumn(1).setPreferredWidth(85);
            bang.getColumnModel().getColumn(2).setPreferredWidth(85);
            bang.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        JScrollPane cuon = new JScrollPane(bang);
        cuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        cuon.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        cuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(cuon, BorderLayout.CENTER);
        this.cuon = cuon;

        btnThem.addActionListener(e -> capNhat(false));
        btnSua.addActionListener(e -> capNhat(true));
        btnXoa.addActionListener(e -> {
            int r = bang.getSelectedRow();
            if (r >= 0) {
                model.removeRow(bang.convertRowIndexToModel(r));
                capNhatKichThuocBang();
            }
        });

        bang.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || bang.getSelectedRow() < 0) return;
            int r = bang.convertRowIndexToModel(bang.getSelectedRow());
            cboLoai.setSelectedItem(String.valueOf(model.getValueAt(r, 0)));
            txtNgayTiem.setText(String.valueOf(model.getValueAt(r, 1)));
            txtNgayHan.setText(model.getValueAt(r, 2) == null ? "" : String.valueOf(model.getValueAt(r, 2)));
            txtGhiChu.setText(model.getValueAt(r, 3) == null ? "" : String.valueOf(model.getValueAt(r, 3)));
        });

        capNhatKichThuocBang();
    }

    private final JScrollPane cuon;

    private void capNhatKichThuocBang() {
        int soDong = model.getRowCount();
        int caoDong = Math.max(26, bang.getRowHeight());
        int caoTieuDe = bang.getTableHeader() != null ? bang.getTableHeader().getPreferredSize().height : 26;
        if (caoTieuDe <= 0) caoTieuDe = 26;
        int caoMoi = caoTieuDe + Math.max(1, soDong) * caoDong + 4;
        cuon.setPreferredSize(new Dimension(340, caoMoi));
        cuon.setMinimumSize(new Dimension(280, caoMoi));
        panel.setPreferredSize(null);
        panel.revalidate();
        panel.repaint();
    }

    private static JPanel hangNhap(String nhan, JComponent oNhap) {
        JPanel hang = new JPanel(new BorderLayout(0, 2));
        hang.setOpaque(false);
        JLabel tieuDe = new JLabel(nhan);
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        tieuDe.setForeground(MauUngDung.VAN_BAN_XAM);
        hang.add(tieuDe, BorderLayout.NORTH);
        hang.add(oNhap, BorderLayout.CENTER);
        return hang;
    }

    private void capNhat(boolean laSua) {
        try {
            Object chon = cboLoai.getSelectedItem();
            String loai = chon == null ? "" : chon.toString().trim();
            if (loai.isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn hoặc nhập tên loại vắc-xin.");
            }
            LocalDate ngayTiem = BoChonNgay.docNgayLinhHoat(txtNgayTiem.getText().trim(), DINH_DANG_NGAY);
            if (ngayTiem == null) {
                throw new IllegalArgumentException("Ngày tiêm không hợp lệ (định dạng dd/MM/yyyy).");
            }
            if (ngayTiem.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày tiêm không được ở tương lai.");
            }
            String hanStr = txtNgayHan.getText().trim();
            LocalDate ngayHan = hanStr.isEmpty() ? null : BoChonNgay.docNgayLinhHoat(hanStr, DINH_DANG_NGAY);
            if (ngayHan != null && ngayHan.isBefore(ngayTiem)) {
                throw new IllegalArgumentException("Ngày hết hạn không được trước ngày tiêm.");
            }

            int dong = laSua && bang.getSelectedRow() >= 0
                    ? bang.convertRowIndexToModel(bang.getSelectedRow()) : -1;
            if (laSua && dong < 0) {
                throw new IllegalArgumentException("Hãy chọn một mũi tiêm trong bảng bên dưới để sửa.");
            }

            Object[] duLieu = {
                    loai,
                    ngayTiem.format(DINH_DANG_NGAY),
                    ngayHan == null ? "" : ngayHan.format(DINH_DANG_NGAY),
                    txtGhiChu.getText().trim()
            };

            if (laSua) {
                for (int c = 0; c < 4; c++) model.setValueAt(duLieu[c], dong, c);
                bang.setRowSelectionInterval(dong, dong);
            } else {
                model.addRow(duLieu);
                int moi = model.getRowCount() - 1;
                bang.setRowSelectionInterval(moi, moi);
                bang.scrollRectToVisible(bang.getCellRect(moi, 0, true));
            }
            capNhatKichThuocBang();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, ex.getMessage(), "Thông tin tiêm phòng", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public JComponent layThanhPhan() {
        return panel;
    }

    @Override
    public String lay() {
        StringBuilder kq = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i > 0) kq.append(';');
            String loai = String.valueOf(model.getValueAt(i, 0)).replace("|", "/").replace(";", ",");
            String ngayTiemStr = String.valueOf(model.getValueAt(i, 1));
            LocalDate nt = BoChonNgay.docNgayLinhHoat(ngayTiemStr, DINH_DANG_NGAY);
            String ntIso = nt == null ? ngayTiemStr : nt.toString();

            String ngayHanStr = model.getValueAt(i, 2) == null ? "" : String.valueOf(model.getValueAt(i, 2)).trim();
            LocalDate nh = ngayHanStr.isEmpty() ? null : BoChonNgay.docNgayLinhHoat(ngayHanStr, DINH_DANG_NGAY);
            String nhIso = nh == null ? "" : nh.toString();

            String ghiChu = model.getValueAt(i, 3) == null ? "" : String.valueOf(model.getValueAt(i, 3)).replace("|", "/").replace(";", ",");

            kq.append(loai).append('|')
                    .append(ntIso).append('|')
                    .append(nhIso).append('|')
                    .append(ghiChu);
        }
        return kq.toString();
    }

    @Override
    public void dat(String giaTri) {
        model.setRowCount(0);
        if (giaTri != null && !giaTri.isBlank()) {
            for (String dong : giaTri.split(";")) {
                if (dong.isBlank()) continue;
                String[] p = dong.split("\\|", -1);
                if (p.length >= 2) {
                    String loai = p[0].trim();
                    LocalDate nt = BoChonNgay.docNgayLinhHoat(p[1].trim(), DINH_DANG_NGAY);
                    String ntHienThi = nt != null ? nt.format(DINH_DANG_NGAY) : p[1].trim();

                    String nhHienThi = "";
                    if (p.length >= 3 && !p[2].isBlank()) {
                        LocalDate nh = BoChonNgay.docNgayLinhHoat(p[2].trim(), DINH_DANG_NGAY);
                        nhHienThi = nh != null ? nh.format(DINH_DANG_NGAY) : p[2].trim();
                    }

                    String ghiChu = p.length >= 4 ? p[3].trim() : "";
                    model.addRow(new Object[]{loai, ntHienThi, nhHienThi, ghiChu});
                }
            }
        }
        capNhatKichThuocBang();
    }

    @Override
    public void datDuocBat(boolean duocBat) {
        bang.setEnabled(duocBat);
        cboLoai.setEnabled(duocBat);
        txtNgayTiem.setEnabled(duocBat);
        txtNgayHan.setEnabled(duocBat);
        txtGhiChu.setEnabled(duocBat);
        btnThem.setEnabled(duocBat);
        btnSua.setEnabled(duocBat);
        btnXoa.setEnabled(duocBat);
    }
}

