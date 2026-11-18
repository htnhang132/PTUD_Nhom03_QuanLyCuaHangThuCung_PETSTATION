package com.petstation.gui.nhansu;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.model.chung.LoaiLichLamViec;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public final class ThaoTacNhanh {
    private ThaoTacNhanh() {}

    public static void layNghiPhep(Component cha) {
        Window cuaSoCha = cha == null ? null : SwingUtilities.getWindowAncestor(cha);
        JDialog hopThoai =
                new JDialog(cuaSoCha, "Đăng ký nghỉ phép", Dialog.ModalityType.APPLICATION_MODAL);
        BoChonNgay chonTuNgay = new BoChonNgay(LocalDate.now());
        BoChonNgay chonDenNgay = new BoChonNgay(LocalDate.now());
        JTextField oLyDo = new JTextField(24);
        boolean[] xacNhan = {false};

        JPanel khungForm = taoKhungForm(chonTuNgay, chonDenNgay, oLyDo);
        JPanel khungNut = taoKhungNut(hopThoai, chonTuNgay, chonDenNgay, oLyDo, xacNhan);
        hopThoai.setLayout(new BorderLayout(8, 8));
        hopThoai.add(khungForm, BorderLayout.CENTER);
        hopThoai.add(khungNut, BorderLayout.SOUTH);
        hopThoai.getRootPane()
                .registerKeyboardAction(
                        e -> hopThoai.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        hopThoai.pack();
        HopThoai.chuanHoaCuaSo(hopThoai, cuaSoCha);
        hopThoai.setVisible(true);
        if (!xacNhan[0]) return;

        LocalDate tuNgay = chonTuNgay.layNgayDuocChon();
        LocalDate denNgay = chonDenNgay.layNgayDuocChon();
        boolean nhieuNgay = !tuNgay.equals(denNgay);
        String moTaNgay =
                nhieuNgay
                        ? "Lịch làm việc từ "
                                + tuNgay.format(DinhDang.NGAY)
                                + " đến "
                                + denNgay.format(DinhDang.NGAY)
                        : "Lịch làm việc ngày " + tuNgay.format(DinhDang.NGAY);
        String giaTriNgay = nhieuNgay ? tuNgay + ".." + denNgay : tuNgay.toString();
        String lyDo = oLyDo.getText().trim();
        String maNv = PhienDangNhapService.macDinh().layMaNhanVien();
        TacVuNen.chayCoPhien(
                cha,
                () -> true,
                () ->
                        new PheDuyetService()
                                .guiThaoTac(
                                        PheDuyetService.Loai.NGHI_PHEP,
                                        lyDo,
                                        moTaNgay,
                                        "Nghỉ phép",
                                        LoaiLichLamViec.NGHI_PHEP.maDB(),
                                        maNv,
                                        giaTriNgay),
                () -> HopThoai.hienThiThanhCong(cha, "Đã gửi yêu cầu nghỉ phép."));
    }

    private static JPanel taoKhungForm(
            BoChonNgay chonTuNgay, BoChonNgay chonDenNgay, JTextField oLyDo) {
        JPanel khung = new JPanel(new GridBagLayout());
        khung.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        chonTuNgay.setPreferredSize(new Dimension(240, MauUngDung.CAO_DAU_VAO));
        chonDenNgay.setPreferredSize(new Dimension(240, MauUngDung.CAO_DAU_VAO));
        oLyDo.setPreferredSize(new Dimension(240, MauUngDung.CAO_DAU_VAO));
        gbc.gridx = 0;
        gbc.gridy = 0;
        khung.add(CoSoPanel.taoNhanTruong("Từ ngày", true), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        khung.add(chonTuNgay, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        khung.add(CoSoPanel.taoNhanTruong("Đến ngày", true), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        khung.add(chonDenNgay, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        khung.add(CoSoPanel.taoNhanTruong("Lý do", true), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        khung.add(oLyDo, gbc);
        return khung;
    }

    private static JPanel taoKhungNut(
            JDialog hopThoai,
            BoChonNgay chonTuNgay,
            BoChonNgay chonDenNgay,
            JTextField oLyDo,
            boolean[] xacNhan) {
        JButton nutGui = new NutDinhDang("Gửi yêu cầu", NutDinhDang.KieuNut.CHINH);
        JButton nutHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);
        nutGui.addActionListener(
                e -> {
                    LocalDate tuNgay = chonTuNgay.layNgayDuocChon();
                    LocalDate denNgay = chonDenNgay.layNgayDuocChon();
                    if (tuNgay == null
                            || denNgay == null
                            || tuNgay.isBefore(LocalDate.now())
                            || denNgay.isBefore(tuNgay)) {
                        HopThoai.hienThiCanhBao(
                                hopThoai,
                                "Khoảng ngày nghỉ phép không hợp lệ (Từ ngày phải từ hôm nay và"
                                        + " Đến ngày không nhỏ hơn Từ ngày).");
                        return;
                    }
                    if (oLyDo.getText() == null || oLyDo.getText().isBlank()) {
                        HopThoai.hienThiCanhBao(hopThoai, "Vui lòng nhập lý do nghỉ phép.");
                        oLyDo.requestFocusInWindow();
                        return;
                    }
                    xacNhan[0] = true;
                    hopThoai.dispose();
                });
        nutHuy.addActionListener(e -> hopThoai.dispose());
        hopThoai.getRootPane().setDefaultButton(nutGui);
        JPanel khung = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        khung.add(nutHuy);
        khung.add(nutGui);
        return khung;
    }
}
