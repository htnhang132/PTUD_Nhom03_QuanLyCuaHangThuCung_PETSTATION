package com.petstation.gui.chung;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class ThanhCongCuChinh extends JPanel {
    public enum ThaoTac {
        MOI,
        LUU,
        TIM,
        IN,
        HOAN_TAC,
        TRO_GIUP
    }

    private final Map<ThaoTac, JButton> cacNut = new EnumMap<>(ThaoTac.class);

    private final Map<ThaoTac, String> cacGoiYKhiReChuot = new EnumMap<>(ThaoTac.class);

    private HoTroThanhCongCu hoatDongVungHienThi;

    private final JButton btnNutQuayLai;

    public ThanhCongCuChinh(Runnable diTrangChinh, Runnable diQuayLai, Runnable diLamMoi) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(0, 30));
        setBackground(MauUngDung.BE_MAT_XEN_KE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));
        btnNutQuayLai = taoNut("Quay lại", "Alt+Left", BieuTuong.Net.Kieu.QUAY_LAI);
        btnNutQuayLai.addActionListener(suKien -> diQuayLai.run());
        btnNutQuayLai.setEnabled(false);
        add(btnNutQuayLai);
        JButton btnTrangChinh =
                taoNut("Màn hình chính", "Alt+Home", BieuTuong.Net.Kieu.TRANG_CHINH);
        btnTrangChinh.addActionListener(suKien -> diTrangChinh.run());
        add(btnTrangChinh);
        JButton btnLamMoi = taoNut("Làm mới", "F5", BieuTuong.Net.Kieu.LAM_MOI);
        btnLamMoi.addActionListener(suKien -> diLamMoi.run());
        add(btnLamMoi);
        add(Box.createHorizontalStrut(4));
        add(layDuongPhanCach());
        add(Box.createHorizontalStrut(4));
        themThaoTac(ThaoTac.TRO_GIUP, "Trợ giúp", "F1", BieuTuong.Net.Kieu.TRO_GIUP);
        add(Box.createHorizontalGlue());
        datHoatDongVungHienThi(null);
    }

    public void datHoatDongVungHienThi(JPanel khungVungHienThi) {
        hoatDongVungHienThi = khungVungHienThi instanceof HoTroThanhCongCu hoTro ? hoTro : null;
        KhaNangThanhCongCu khaNang =
                hoatDongVungHienThi == null
                        ? KhaNangThanhCongCu.taoRong()
                        : hoatDongVungHienThi.layKhaNangThanhCongCu();
        datDuocBat(ThaoTac.MOI, khaNang.coTheTao());
        datDuocBat(ThaoTac.LUU, khaNang.coTheLuu());
        datDuocBat(ThaoTac.TIM, khaNang.coTheTim());
        datDuocBat(ThaoTac.IN, khaNang.coTheIn());
        datDuocBat(ThaoTac.HOAN_TAC, khaNang.coTheHoanTac());
        datDuocBat(ThaoTac.TRO_GIUP, khaNang.coTheTroGiup());
    }

    public void datCoTheDiQuayLai(boolean duocBat) {
        btnNutQuayLai.setEnabled(duocBat);
    }

    public void kichHoat(ThaoTac thaoTac) {
        if (hoatDongVungHienThi == null) return;
        KhaNangThanhCongCu khaNang = hoatDongVungHienThi.layKhaNangThanhCongCu();
        switch (thaoTac) {
            case MOI -> {
                if (khaNang.coTheTao()) hoatDongVungHienThi.xuLyTaoMoi();
            }
            case LUU -> {
                if (khaNang.coTheLuu()) hoatDongVungHienThi.xuLyLuu();
            }
            case TIM -> {
                if (khaNang.coTheTim()) hoatDongVungHienThi.xuLyTimKiem();
            }
            case IN -> {
                if (khaNang.coTheIn()) hoatDongVungHienThi.xuLyIn();
            }
            case HOAN_TAC -> {
                if (khaNang.coTheHoanTac()) hoatDongVungHienThi.xuLyHoanTac();
            }
            case TRO_GIUP -> {
                if (khaNang.coTheTroGiup()) hoatDongVungHienThi.xuLyTroGiup();
            }
        }
    }

    private void themThaoTac(
            ThaoTac thaoTac, String nhan, String phimTat, BieuTuong.Net.Kieu kieuBieuTuong) {
        JButton btnNut = taoNut(nhan, phimTat, kieuBieuTuong);
        btnNut.addActionListener(suKien -> kichHoat(thaoTac));
        cacNut.put(thaoTac, btnNut);
        cacGoiYKhiReChuot.put(thaoTac, nhan + " (" + phimTat + ")");
        add(btnNut);
    }

    private JButton taoNut(String nhan, String phimTat, BieuTuong.Net.Kieu kieuBieuTuong) {
        JButton btnNut =
                new JButton(nhan, new BieuTuong.Net(kieuBieuTuong, 18, MauUngDung.NAU)) {
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
                        if (isEnabled() && getModel().isRollover()) {
                            doHoa2.setColor(MauUngDung.DO_NHAT);
                            doHoa2.fillRect(0, 0, getWidth(), getHeight());
                        }
                        if (isFocusOwner()) {
                            doHoa2.setColor(MauUngDung.VANG);
                            doHoa2.setStroke(new BasicStroke(2));
                            doHoa2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
                        }
                        doHoa2.dispose();
                        super.paintComponent(doHoa);
                    }

                    @Override
                    public Dimension getMaximumSize() {
                        return new Dimension(getPreferredSize().width, Integer.MAX_VALUE);
                    }
                };
        btnNut.setDisabledIcon(new BieuTuong.Net(kieuBieuTuong, 18, MauUngDung.VIEN));
        btnNut.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        btnNut.setForeground(MauUngDung.VAN_BAN_TOI);
        btnNut.setIconTextGap(8);
        btnNut.setHorizontalAlignment(SwingConstants.LEFT);
        btnNut.setVerticalAlignment(SwingConstants.CENTER);
        btnNut.setAlignmentY(Component.CENTER_ALIGNMENT);
        btnNut.setToolTipText(nhan + " (" + phimTat + ")");
        btnNut.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnNut.setFocusPainted(false);
        btnNut.setRolloverEnabled(true);
        btnNut.setOpaque(false);
        btnNut.setContentAreaFilled(false);
        btnNut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNut.getAccessibleContext().setAccessibleName(nhan);
        return btnNut;
    }

    private JSeparator layDuongPhanCach() {
        JSeparator phanCach = new JSeparator(SwingConstants.VERTICAL);
        phanCach.setPreferredSize(new Dimension(2, 18));
        phanCach.setMaximumSize(new Dimension(2, 18));
        phanCach.setAlignmentY(Component.CENTER_ALIGNMENT);
        phanCach.setForeground(MauUngDung.VIEN);
        return phanCach;
    }

    private void datDuocBat(ThaoTac thaoTac, boolean duocBat) {
        JButton btnNut = cacNut.get(thaoTac);
        if (btnNut == null) return;
        btnNut.setEnabled(duocBat);
        btnNut.setToolTipText(
                duocBat
                        ? cacGoiYKhiReChuot.get(thaoTac)
                        : btnNut.getText() + " – Chức năng chưa khả dụng trên màn hình này");
    }
}
