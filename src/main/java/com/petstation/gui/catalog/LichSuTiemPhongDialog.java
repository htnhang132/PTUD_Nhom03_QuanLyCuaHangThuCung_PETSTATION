package com.petstation.gui.catalog;

import com.petstation.gui.components.HopThoai;
import com.petstation.service.TiemPhongService;

import java.awt.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Lịch sử tiêm phòng trong hồ sơ thú cưng. */
public final class LichSuTiemPhongDialog extends JDialog {
    private final String maThuCung;
    private final DefaultTableModel moHinh =
            new DefaultTableModel(
                    new String[] {"ID", "Vaccine", "Ngày tiêm", "Hạn", "Ghi chú"}, 0) {
                @Override
                public boolean isCellEditable(int dong, int cot) {
                    return false;
                }
            };
    private final JTable tblLichSu = new JTable(moHinh);

    public LichSuTiemPhongDialog(Window cha, String ma) {
        super(cha, "Lịch sử tiêm phòng — " + ma, ModalityType.APPLICATION_MODAL);
        maThuCung = ma;
        setLayout(new BorderLayout(8, 8));
        add(new JScrollPane(tblLichSu), BorderLayout.CENTER);
        JPanel pnlNut = new JPanel();
        for (String ten : new String[] {"Thêm mũi tiêm", "Sửa mũi tiêm", "Xóa mũi tiêm"}) {
            JButton btn = new JButton(ten);
            pnlNut.add(btn);
            btn.addActionListener(
                    e -> {
                        try {
                            if (ten.startsWith("Xóa")) xoa();
                            else sua(ten.startsWith("Sửa"));
                        } catch (RuntimeException loi) {
                            HopThoai.hienThiCanhBao(this, loi.getMessage());
                        }
                    });
        }
        add(pnlNut, BorderLayout.SOUTH);
        nap();
        setSize(750, 400);
        setLocationRelativeTo(cha);
    }

    private void nap() {
        moHinh.setRowCount(0);
        for (Object[] dong : TiemPhongService.layLichSu(maThuCung)) moHinh.addRow(dong);
    }

    private int chon() {
        int dong = tblLichSu.getSelectedRow();
        if (dong < 0) throw new IllegalArgumentException("Hãy chọn mũi tiêm.");
        return tblLichSu.convertRowIndexToModel(dong);
    }

    private void sua(boolean sua) {
        int dong = sua ? chon() : -1;
        JTextField txtTen =
                new JTextField(sua ? String.valueOf(moHinh.getValueAt(dong, 1)) : "", 25);
        JTextField txtNgay =
                new JTextField(
                        sua
                                ? String.valueOf(moHinh.getValueAt(dong, 2))
                                : LocalDate.now().toString());
        Object han = sua ? moHinh.getValueAt(dong, 3) : null;
        JTextField txtHan = new JTextField(han == null ? "" : han.toString());
        Object ghiChu = sua ? moHinh.getValueAt(dong, 4) : null;
        JTextField txtGhiChu = new JTextField(ghiChu == null ? "" : ghiChu.toString());
        Object[] form = {
            "Vaccine",
            txtTen,
            "Ngày tiêm (yyyy-MM-dd)",
            txtNgay,
            "Hạn (yyyy-MM-dd, tùy chọn)",
            txtHan,
            "Ghi chú",
            txtGhiChu
        };
        if (JOptionPane.showConfirmDialog(
                        this,
                        form,
                        sua ? "Sửa mũi tiêm" : "Thêm mũi tiêm",
                        JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) return;
        TiemPhongService.luu(
                maThuCung,
                sua ? ((Number) moHinh.getValueAt(dong, 0)).longValue() : 0,
                txtTen.getText(),
                LocalDate.parse(txtNgay.getText().trim()),
                txtHan.getText().isBlank() ? null : LocalDate.parse(txtHan.getText().trim()),
                txtGhiChu.getText());
        nap();
    }

    private void xoa() {
        int dong = chon();
        if (JOptionPane.showConfirmDialog(
                        this, "Xóa mũi tiêm đã chọn?", "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            TiemPhongService.xoa(maThuCung, ((Number) moHinh.getValueAt(dong, 0)).longValue());
            nap();
        }
    }
}
