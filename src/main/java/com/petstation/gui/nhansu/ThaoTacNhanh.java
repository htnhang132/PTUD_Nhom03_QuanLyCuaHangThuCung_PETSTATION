package com.petstation.gui.nhansu;

import com.petstation.gui.chung.HopThoai;
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public final class ThaoTacNhanh {
    private ThaoTacNhanh() {}

    public static void layNghiPhep(Component cha) {
        Window cuaSoCha = cha == null ? null : SwingUtilities.getWindowAncestor(cha);
        JDialog hopThoai =
                new JDialog(cuaSoCha, "Đăng ký nghỉ phép", Dialog.ModalityType.APPLICATION_MODAL);
        BoChonNgay chonNgay = new BoChonNgay(LocalDate.now());
        JTextField oLyDo = new JTextField(24);
        boolean[] xacNhan = {false};

        JPanel khungForm = taoKhungForm(chonNgay, oLyDo);
        JPanel khungNut = taoKhungNut(hopThoai, chonNgay, oLyDo, xacNhan);
        hopThoai.setLayout(new BorderLayout(8, 8));
        hopThoai.add(khungForm, BorderLayout.CENTER);
        hopThoai.add(khungNut, BorderLayout.SOUTH);
        hopThoai.pack();
        hopThoai.setLocationRelativeTo(cha);
        hopThoai.setVisible(true);
        if (!xacNhan[0]) return;

        LocalDate ngay = chonNgay.layNgayDuocChon();
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
                                        "Lịch làm việc ngày " + ngay.format(DinhDang.NGAY),
                                        "Nghỉ phép",
                                        LoaiLichLamViec.NGHI_PHEP.maDB(),
                                        maNv,
                                        ngay.toString()),
                () -> HopThoai.hienThiThanhCong(cha, "Đã gửi yêu cầu nghỉ phép."));
    }

    private static JPanel taoKhungForm(BoChonNgay chonNgay, JTextField oLyDo) {
        JPanel khung = new JPanel(new GridBagLayout());
        khung.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        khung.add(new JLabel("Ngày nghỉ phép:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        khung.add(chonNgay, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        khung.add(new JLabel("Lý do:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        khung.add(oLyDo, gbc);
        return khung;
    }

    private static JPanel taoKhungNut(
            JDialog hopThoai, BoChonNgay chonNgay, JTextField oLyDo, boolean[] xacNhan) {
        JButton nutGui = new NutDinhDang("Gửi yêu cầu", NutDinhDang.KieuNut.CHINH);
        JButton nutHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.PHU);
        nutGui.addActionListener(
                e -> {
                    LocalDate ngay = chonNgay.layNgayDuocChon();
                    if (ngay == null || ngay.isBefore(LocalDate.now())) {
                        HopThoai.hienThiCanhBao(
                                hopThoai, "Ngày nghỉ phép không hợp lệ hoặc nằm trong quá khứ.");
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
