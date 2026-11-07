package com.petstation.gui.processing.spa;

import com.petstation.gui.catalog.DanhMucKhachHangPanel;
import com.petstation.gui.catalog.DanhMucThuCungPanel;
import com.petstation.gui.components.BoGocPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.service.DanhMucService;
import com.petstation.service.LuaChonNghiepVuService;
import com.petstation.service.SpaService;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class DatLichSpaDialog {
    private final Component parent;
    private final JDialog dialog;
    private final JComboBox<BieuMauSQL.LuaChon> cboKhachHang;
    private final JComboBox<BieuMauSQL.LuaChon> cboThuCung;
    private final JComboBox<BieuMauSQL.LuaChon> cboGoiDichVu;
    private final JComboBox<BieuMauSQL.LuaChon> cboNhanVien;
    private final JSpinner spThoiGian;
    private final JTextField txtGhiChu;
    private final JButton btnThemKhach;
    private final JButton btnThemThuCung;
    private final NutDinhDang btnLuu;
    private final NutDinhDang btnHuy;
    private final Runnable onThanhCong;

    public static void mo(Component parent, Runnable onThanhCong) {
        new DatLichSpaDialog(parent, onThanhCong).dialog.setVisible(true);
    }

    private DatLichSpaDialog(Component parent, Runnable onThanhCong) {
        this.parent = parent;
        this.onThanhCong = onThanhCong;

        this.dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "Đặt lịch hẹn dịch vụ Spa",
                Dialog.ModalityType.APPLICATION_MODAL);

        cboKhachHang = new JComboBox<>();
        cboThuCung = new JComboBox<>();
        cboGoiDichVu = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        txtGhiChu = new JTextField();

        btnThemKhach = new NutDinhDang("+ Thêm khách", NutDinhDang.KieuNut.PHU);
        btnThemKhach.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnThemKhach.setPreferredSize(new Dimension(110, 32));

        btnThemThuCung = new NutDinhDang("+ Thêm thú cưng", NutDinhDang.KieuNut.PHU);
        btnThemThuCung.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnThemThuCung.setPreferredSize(new Dimension(125, 32));

        Date macDinh = Date.from(
                LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0)
                        .atZone(ZoneId.systemDefault()).toInstant());
        spThoiGian = new JSpinner(new SpinnerDateModel(macDinh, null, null, java.util.Calendar.MINUTE));
        spThoiGian.setEditor(new JSpinner.DateEditor(spThoiGian, "dd/MM/yyyy HH:mm"));
        spThoiGian.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));

        btnLuu = new NutDinhDang("Đặt lịch hẹn", NutDinhDang.KieuNut.CHINH);
        btnHuy = new NutDinhDang("Hủy bỏ (ESC)", NutDinhDang.KieuNut.PHU);

        napDuLieu();
        thietKeGiaoDien();
        caiDatSuKien();
    }

    private void napDuLieu() {
        cboKhachHang.removeAllItems();
        var dsKhach = DanhMucService.layKhachHang().stream()
                .filter(k -> "Đang hoạt động".equals(k[8]))
                .map(k -> new BieuMauSQL.LuaChon(String.valueOf(k[0]), k[0] + " — " + k[1] + " (" + k[2] + ")"))
                .toList();
        for (var khach : dsKhach) {
            cboKhachHang.addItem(khach);
        }

        cboGoiDichVu.removeAllItems();
        for (Object[] g : LuaChonNghiepVuService.layLuaChonGoiDichVu()) {
            cboGoiDichVu.addItem(new BieuMauSQL.LuaChon(String.valueOf(g[0]), String.valueOf(g[1])));
        }

        cboNhanVien.removeAllItems();
        for (Object[] nv : LuaChonNghiepVuService.layLuaChonNhanVien()) {
            cboNhanVien.addItem(new BieuMauSQL.LuaChon(String.valueOf(nv[0]), String.valueOf(nv[1])));
        }

        capNhatDanhSachThuCung();
    }

    private void capNhatDanhSachThuCung() {
        cboThuCung.removeAllItems();
        BieuMauSQL.LuaChon khachDangChon = (BieuMauSQL.LuaChon) cboKhachHang.getSelectedItem();
        if (khachDangChon == null) {
            btnThemThuCung.setEnabled(false);
            return;
        }

        btnThemThuCung.setEnabled(true);
        String maKhach = khachDangChon.giaTri();
        var dsThu = DanhMucService.layThuCung().stream()
                .filter(t -> "Đang hoạt động".equals(t[13])
                        && maKhach.equals(DanhMucService.layMaTuLuaChon(t[8])))
                .map(t -> new BieuMauSQL.LuaChon(String.valueOf(t[0]), t[0] + " — " + t[1] + " (" + t[2] + ")"))
                .toList();

        if (dsThu.isEmpty()) {
            cboThuCung.addItem(new BieuMauSQL.LuaChon("", "(Khách chưa có thú cưng - Bấm Thêm thú cưng)"));
        } else {
            for (var thu : dsThu) {
                cboThuCung.addItem(thu);
            }
        }
    }

    private void thietKeGiaoDien() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.setBackground(MauUngDung.NEN_KEM);

        JPanel pnlTieuDe = new JPanel(new BorderLayout(0, 4));
        pnlTieuDe.setOpaque(false);
        JLabel lblTieuDe = new JLabel("Đặt lịch hẹn dịch vụ Spa");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        lblTieuDe.setForeground(MauUngDung.NAU);
        JLabel lblPhu = new JLabel("Chọn khách hàng, thú cưng và gói dịch vụ để lên lịch tiếp đón");
        lblPhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblPhu.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlTieuDe.add(lblTieuDe, BorderLayout.NORTH);
        pnlTieuDe.add(lblPhu, BorderLayout.CENTER);
        root.add(pnlTieuDe, BorderLayout.NORTH);

        BoGocPanel pnlForm = new BoGocPanel(14, MauUngDung.THE_NEN);
        pnlForm.setLayout(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(16, 18, 16, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Khách hàng
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        pnlForm.add(taoNhan("Khách hàng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        pnlForm.add(cboKhachHang, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        pnlForm.add(btnThemKhach, gbc);

        // Thú cưng
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        pnlForm.add(taoNhan("Thú cưng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        pnlForm.add(cboThuCung, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        pnlForm.add(btnThemThuCung, gbc);

        // Gói Spa
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        pnlForm.add(taoNhan("Gói dịch vụ Spa:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        pnlForm.add(cboGoiDichVu, gbc);
        gbc.gridwidth = 1;

        // Thợ phụ trách
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        pnlForm.add(taoNhan("Thợ phụ trách:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        pnlForm.add(cboNhanVien, gbc);
        gbc.gridwidth = 1;

        // Thời gian hẹn
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        pnlForm.add(taoNhan("Thời gian hẹn:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        pnlForm.add(spThoiGian, gbc);
        gbc.gridwidth = 1;

        // Ghi chú
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        pnlForm.add(taoNhan("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        pnlForm.add(txtGhiChu, gbc);
        gbc.gridwidth = 1;

        root.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlDuoi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlDuoi.setOpaque(false);
        pnlDuoi.add(btnHuy);
        pnlDuoi.add(btnLuu);
        root.add(pnlDuoi, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 640), 440);
        HopThoai.chuanHoaCuaSo(dialog, parent);
    }

    private JLabel taoNhan(String tieuDe) {
        JLabel lbl = new JLabel(tieuDe);
        lbl.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lbl.setForeground(MauUngDung.VAN_BAN_TOI);
        return lbl;
    }

    private void caiDatSuKien() {
        cboKhachHang.addActionListener(e -> capNhatDanhSachThuCung());

        btnThemKhach.addActionListener(e -> {
            Object[] khachMoi = DanhMucKhachHangPanel.moThem(dialog, "");
            if (khachMoi != null) {
                napDuLieu();
                String maMoi = String.valueOf(khachMoi[0]);
                for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
                    if (cboKhachHang.getItemAt(i).giaTri().equals(maMoi)) {
                        cboKhachHang.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        btnThemThuCung.addActionListener(e -> {
            BieuMauSQL.LuaChon khachDangChon = (BieuMauSQL.LuaChon) cboKhachHang.getSelectedItem();
            if (khachDangChon == null || khachDangChon.giaTri().isBlank()) {
                HopThoai.hienThiCanhBao(dialog, "Vui lòng chọn khách hàng trước khi thêm thú cưng.");
                return;
            }
            Object[] thuMoi = DanhMucThuCungPanel.moThem(dialog, khachDangChon.giaTri());
            if (thuMoi != null) {
                capNhatDanhSachThuCung();
                String maThuMoi = String.valueOf(thuMoi[0]);
                for (int i = 0; i < cboThuCung.getItemCount(); i++) {
                    if (cboThuCung.getItemAt(i).giaTri().equals(maThuMoi)) {
                        cboThuCung.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dialog.dispose());
    }

    private void xuLyLuu() {
        try {
            BieuMauSQL.LuaChon khach = (BieuMauSQL.LuaChon) cboKhachHang.getSelectedItem();
            BieuMauSQL.LuaChon thu = (BieuMauSQL.LuaChon) cboThuCung.getSelectedItem();
            BieuMauSQL.LuaChon goi = (BieuMauSQL.LuaChon) cboGoiDichVu.getSelectedItem();
            BieuMauSQL.LuaChon tho = (BieuMauSQL.LuaChon) cboNhanVien.getSelectedItem();

            if (khach == null || khach.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn khách hàng.");
            }
            if (thu == null || thu.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn thú cưng của khách (hoặc bấm + Thêm thú cưng).");
            }
            if (goi == null || goi.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn gói dịch vụ Spa.");
            }
            if (tho == null || tho.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn thợ phụ trách.");
            }

            Date dateVal = (Date) spThoiGian.getValue();
            LocalDateTime batDau = LocalDateTime.ofInstant(dateVal.toInstant(), ZoneId.systemDefault())
                    .withSecond(0).withNano(0);

            if (batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0))) {
                throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
            }

            btnLuu.setEnabled(false);
            long id = SpaService.datLichHen(thu.giaTri(), goi.giaTri(), tho.giaTri(), batDau);
            HopThoai.hienThiThanhCong(dialog, "Đã đặt lịch hẹn Spa thành công cho thú cưng.");
            if (onThanhCong != null) {
                onThanhCong.run();
            }
            dialog.dispose();
        } catch (RuntimeException ex) {
            btnLuu.setEnabled(true);
            HopThoai.hienThiCanhBao(dialog, ex.getMessage());
        }
    }
}
