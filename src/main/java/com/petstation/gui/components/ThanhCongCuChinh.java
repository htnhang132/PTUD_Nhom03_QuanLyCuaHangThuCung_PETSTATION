package com.petstation.gui.components;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** Cung cấp giao diện và xử lý tương tác cho màn hình thanh công cụ. */
public final class ThanhCongCuChinh extends JPanel {
    /** Cung cấp giao diện và xử lý tương tác cho thao tác. */
    public enum ThaoTac {
        MOI,
        LUU,
        TIM,
        IN,
        HOAN_TAC,
        TRO_GIUP
    }

    /** Các nút. */
    private final Map<ThaoTac, JButton> cacNut = new EnumMap<>(ThaoTac.class);

    /** Các gợi ý khi rê chuột. */
    private final Map<ThaoTac, String> cacGoiYKhiReChuot = new EnumMap<>(ThaoTac.class);

    /** Hoạt động vùng hiển thị. */
    private HoTroThanhCongCu hoatDongVungHienThi;

    /** Nút quay lại. */
    private final JButton btnNutQuayLai;

    public ThanhCongCuChinh(Runnable diTrangChinh, Runnable diQuayLai) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 4));
        setPreferredSize(new Dimension(0, 48));
        setBackground(MauUngDung.BE_MAT_XEN_KE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));

        btnNutQuayLai = taoNut("Quay lại", "Alt+←", BieuTuongNet.Kieu.QUAY_LAI);
        btnNutQuayLai.addActionListener(suKien -> diQuayLai.run());
        btnNutQuayLai.setEnabled(false);
        add(btnNutQuayLai);
        // Trang chính.
        JButton btnTrangChinh = taoNut("Màn hình chính", "Alt+Home", BieuTuongNet.Kieu.TRANG_CHINH);
        btnTrangChinh.addActionListener(suKien -> diTrangChinh.run());
        add(btnTrangChinh);
        add(layDuongPhanCach());
        themThaoTac(ThaoTac.TRO_GIUP, "Trợ giúp", "F1", BieuTuongNet.Kieu.TRO_GIUP);
        datHoatDongVungHienThi(null);
    }

    public void datHoatDongVungHienThi(JPanel pnlVungHienThi) {
        hoatDongVungHienThi = pnlVungHienThi instanceof HoTroThanhCongCu hoTro ? hoTro : null;
        // Khả năng.
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

    public void diQuayLaiNeuCoThe() {
        if (btnNutQuayLai.isEnabled()) btnNutQuayLai.doClick(0);
    }

    public void kichHoat(ThaoTac thaoTac) {
        // Nút.
        JButton btnNut = cacNut.get(thaoTac);
        if (btnNut == null || !btnNut.isEnabled() || hoatDongVungHienThi == null) return;
        switch (thaoTac) {
            case MOI -> hoatDongVungHienThi.xuLyTaoMoi();
            case LUU -> hoatDongVungHienThi.xuLyLuu();
            case TIM -> hoatDongVungHienThi.xuLyTimKiem();
            case IN -> hoatDongVungHienThi.xuLyIn();
            case HOAN_TAC -> hoatDongVungHienThi.xuLyHoanTac();
            case TRO_GIUP -> hoatDongVungHienThi.xuLyTroGiup();
        }
    }

    private void themThaoTac(
            ThaoTac thaoTac, String nhan, String phimTat, BieuTuongNet.Kieu kieuBieuTuong) {
        // Nút.
        JButton btnNut = taoNut(nhan, phimTat, kieuBieuTuong);
        btnNut.addActionListener(suKien -> kichHoat(thaoTac));
        cacNut.put(thaoTac, btnNut);
        cacGoiYKhiReChuot.put(thaoTac, nhan + "(" + phimTat + ")");
        add(btnNut);
    }

    private JButton taoNut(String nhan, String phimTat, BieuTuongNet.Kieu kieuBieuTuong) {
        // Nút.
        JButton btnNut =
                new JButton(nhan, new BieuTuongNet(kieuBieuTuong, 20, MauUngDung.NAU)) {
                    /**
                     * Vẽ thành phần trong lớp xử lý giao diện.
                     *
                     * @param doHoa đồ họa
                     */
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        // Đồ họa.
                        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
                        if (isEnabled() && getModel().isRollover()) {
                            doHoa2.setColor(MauUngDung.DO_NHAT);
                            doHoa2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
                        }
                        if (isFocusOwner()) {
                            doHoa2.setColor(MauUngDung.VANG);
                            doHoa2.setStroke(new BasicStroke(2));
                            doHoa2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                        }
                        doHoa2.dispose();
                        super.paintComponent(doHoa);
                    }
                };
        btnNut.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnNut.setForeground(MauUngDung.VAN_BAN_TOI);
        btnNut.setToolTipText(nhan + "(" + phimTat + ")");
        btnNut.setBorder(new EmptyBorder(5, 8, 5, 8));
        btnNut.setFocusPainted(false);
        btnNut.setRolloverEnabled(true);
        btnNut.setOpaque(false);
        btnNut.setContentAreaFilled(false);
        btnNut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNut.getAccessibleContext().setAccessibleName(nhan);
        return btnNut;
    }

    private JSeparator layDuongPhanCach() {
        // Phân cách.
        JSeparator phanCach = new JSeparator(SwingConstants.VERTICAL);
        phanCach.setPreferredSize(new Dimension(7, 30));
        phanCach.setForeground(MauUngDung.VIEN);
        return phanCach;
    }

    private void datDuocBat(ThaoTac thaoTac, boolean duocBat) {
        // Nút.
        JButton btnNut = cacNut.get(thaoTac);
        if (btnNut == null) return;
        btnNut.setEnabled(duocBat);
        btnNut.setToolTipText(
                duocBat
                        ? cacGoiYKhiReChuot.get(thaoTac)
                        : btnNut.getText() + "– Chức năng chưa khả dụng trên màn hình này");
    }
}
