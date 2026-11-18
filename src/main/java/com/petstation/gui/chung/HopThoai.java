package com.petstation.gui.chung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class HopThoai {
    public static final String TIEU_DE_THONG_TIN = "Thông báo";

    public static final String TIEU_DE_THANH_CONG = "Thành công";

    public static final String TIEU_DE_CANH_BAO = "Cảnh báo";

    public static final String TIEU_DE_LOI = "Lỗi";

    public static final String TIEU_DE_XAC_NHAN = "Xác nhận";

    private static final String DONG = "Đóng";

    private static final String DONG_Y = "Đồng ý";

    private static final String HUY = "Hủy";

    private HopThoai() {}

    public static void thietLapCacGiaTriMacDinh() {
        UIManager.put("OptionPane.okButtonText", DONG_Y);
        UIManager.put("OptionPane.cancelButtonText", HUY);
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        UIManager.put("OptionPane.titleText", TIEU_DE_THONG_TIN);
        UIManager.put("OptionPane.background", MauUngDung.NEN_KEM);
        UIManager.put("Panel.background", MauUngDung.NEN_KEM);
        UIManager.put("defaultFont", MauUngDung.taoPhongChu(Font.PLAIN, 13));
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TextField.minimumHeight", MauUngDung.CAO_DAU_VAO);
        UIManager.put("PasswordField.minimumHeight", MauUngDung.CAO_DAU_VAO);
        UIManager.put("FormattedTextField.minimumHeight", MauUngDung.CAO_DAU_VAO);
        UIManager.put("ComboBox.minimumHeight", MauUngDung.CAO_DAU_VAO);
        UIManager.put("Spinner.minimumHeight", MauUngDung.CAO_DAU_VAO);
        UIManager.put("TextField.margin", new Insets(5, 10, 5, 10));
        UIManager.put("PasswordField.margin", new Insets(5, 10, 5, 10));
        UIManager.put("FormattedTextField.margin", new Insets(5, 10, 5, 10));
        UIManager.put("Spinner.padding", new Insets(5, 8, 5, 8));
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.borderColor", MauUngDung.VIEN);
        UIManager.put("Component.focusedBorderColor", MauUngDung.NAU);
        UIManager.put("Component.focusColor", MauUngDung.DO_NHAT);
        UIManager.put("ComboBox.padding", new Insets(5, 8, 5, 8));
        UIManager.put("ComboBox.selectionBackground", MauUngDung.DO_NHAT);
        UIManager.put("ComboBox.selectionForeground", MauUngDung.NAU);
        UIManager.put("TabbedPane.tabHeight", 36);
        UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
        UIManager.put("TabbedPane.selectedForeground", MauUngDung.DO_THUONG_HIEU);
        UIManager.put("TabbedPane.foreground", MauUngDung.NAU);
        UIManager.put("TabbedPane.underlineColor", MauUngDung.DO_THUONG_HIEU);
        UIManager.put("TabbedPane.hoverColor", MauUngDung.KEM_NEN_NHAN);
        UIManager.put("TabbedPane.focusColor", MauUngDung.DO_NHAT);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", MauUngDung.NEN_KEM);
        UIManager.put("ScrollBar.thumb", MauUngDung.KEM_VIEN);
        UIManager.put("ScrollBar.hoverThumbColor", MauUngDung.NAU_MO);
        UIManager.put("ToolTip.background", MauUngDung.NAU);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }

    public static void hienThiThongTin(Component cha, String thongBao) {
        hienThi(cha, thongBao, TIEU_DE_THONG_TIN, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void hienThiThanhCong(Component cha, String thongBao) {
        hienThi(cha, thongBao, TIEU_DE_THANH_CONG, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void hienThiCanhBao(Component cha, String thongBao) {
        hienThi(cha, thongBao, TIEU_DE_CANH_BAO, JOptionPane.WARNING_MESSAGE);
    }

    public static void hienThiLoi(Component cha, String thongBao) {
        hienThi(cha, thongBao, TIEU_DE_LOI, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean xacNhan(Component cha, String thongBao) {
        return xacNhan(cha, thongBao, DONG_Y, false);
    }

    public static boolean xacNhanNguyHiem(Component cha, String thongBao, String nhanXacNhan) {
        return xacNhan(cha, thongBao, nhanXacNhan, true);
    }

    public static boolean xacNhan(
            Component cha, String thongBao, String nhanXacNhan, boolean nguyHiem) {
        Object[] cacLuaChon = {nhanXacNhan, HUY};
        int luaChon =
                JOptionPane.showOptionDialog(
                        cha,
                        thongBao,
                        TIEU_DE_XAC_NHAN,
                        JOptionPane.DEFAULT_OPTION,
                        nguyHiem ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE,
                        null,
                        cacLuaChon,
                        nguyHiem ? HUY : nhanXacNhan);
        return luaChon == 0;
    }

    public static void hienThiThongBaoHopThoai(
            Component cha, Object thongBao, String tieuDe, int kieu) {
        String daChuanHoa;
        if (tieuDe != null && !tieuDe.isBlank()) {
            daChuanHoa = tieuDe.trim();
        } else {
            daChuanHoa =
                    switch (kieu) {
                        case JOptionPane.ERROR_MESSAGE -> TIEU_DE_LOI;
                        case JOptionPane.WARNING_MESSAGE -> TIEU_DE_CANH_BAO;
                        default -> TIEU_DE_THONG_TIN;
                    };
        }
        hienThi(cha, thongBao, daChuanHoa, kieu);
    }

    public static int hienThiXacNhanHopThoai(
            Component cha, Object thongBao, String tieuDe, int kieuLuaChon, int kieuThongBao) {
        boolean dongYKhong = kieuLuaChon == JOptionPane.YES_NO_OPTION;
        boolean nguyHiem =
                kieuThongBao == JOptionPane.WARNING_MESSAGE
                        || kieuThongBao == JOptionPane.ERROR_MESSAGE;
        Object[] cacLuaChon =
                dongYKhong ? new Object[] {"Có", "Không"} : new Object[] {DONG_Y, HUY};
        int luaChon =
                JOptionPane.showOptionDialog(
                        cha,
                        thongBao,
                        tieuDe == null || tieuDe.isBlank() ? TIEU_DE_XAC_NHAN : tieuDe,
                        JOptionPane.DEFAULT_OPTION,
                        nguyHiem ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE,
                        null,
                        cacLuaChon,
                        nguyHiem ? cacLuaChon[1] : cacLuaChon[0]);
        if (luaChon == 0) return dongYKhong ? JOptionPane.YES_OPTION : JOptionPane.OK_OPTION;
        return dongYKhong ? JOptionPane.NO_OPTION : JOptionPane.CANCEL_OPTION;
    }

    public static void chuanHoaCuaSo(JDialog dialog, Component cha) {
        if (dialog == null) return;
        Window owner =
                cha instanceof Window w
                        ? w
                        : (cha != null ? SwingUtilities.getWindowAncestor(cha) : null);
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane()
                .registerKeyboardAction(
                        e -> dialog.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void canChinhKichThuocVuaManHinh(
            JDialog dialog, int rongToiThieu, int caoToiThieu) {
        if (dialog == null) return;
        dialog.pack();
        Dimension uuTien = dialog.getPreferredSize();
        var cauHinh = dialog.getGraphicsConfiguration();
        if (cauHinh == null) {
            dialog.setSize(
                    Math.max(uuTien.width, rongToiThieu), Math.max(uuTien.height, caoToiThieu));
            return;
        }
        var vung = cauHinh.getBounds();
        Insets le = Toolkit.getDefaultToolkit().getScreenInsets(cauHinh);
        int rongToiDa = Math.max(320, vung.width - le.left - le.right - 36);
        int caoToiDa = Math.max(240, vung.height - le.top - le.bottom - 36);
        int rong = Math.min(rongToiDa, Math.max(uuTien.width, Math.min(rongToiThieu, rongToiDa)));
        int cao = Math.min(caoToiDa, Math.max(uuTien.height, Math.min(caoToiThieu, caoToiDa)));
        dialog.setSize(rong, cao);
    }

    public static void hienThiThongTin(Component cha, String thongBao, String tieuDe) {
        hienThiThongBaoHopThoai(cha, thongBao, tieuDe, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void hienThiCanhBao(Component cha, String thongBao, String tieuDe) {
        hienThiThongBaoHopThoai(cha, thongBao, tieuDe, JOptionPane.WARNING_MESSAGE);
    }

    public static void hienThiLoi(Component cha, String thongBao, String tieuDe) {
        hienThi(cha, thongBao, tieuDe, JOptionPane.ERROR_MESSAGE);
    }

    public static void hienThiNoiDung(Component cha, Object noiDung, String tieuDe) {
        hienThi(cha, noiDung, tieuDe, JOptionPane.PLAIN_MESSAGE);
    }

    public static boolean xacNhanDongY(Component cha, Object thongBao, String tieuDe) {
        return xacNhanDongY(cha, thongBao, tieuDe, false);
    }

    public static boolean xacNhanDongY(
            Component cha, Object thongBao, String tieuDe, boolean nguyHiem) {
        return hienThiXacNhanHopThoai(
                        cha,
                        thongBao,
                        tieuDe,
                        JOptionPane.YES_NO_OPTION,
                        nguyHiem ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    public static boolean xacNhanOk(Component cha, Object thongBao, String tieuDe) {
        return hienThiXacNhanHopThoai(
                        cha,
                        thongBao,
                        tieuDe,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.OK_OPTION;
    }

    public static String nhapChuoi(Component cha, String thongBao, String tieuDe) {
        return JOptionPane.showInputDialog(cha, thongBao, tieuDe, JOptionPane.QUESTION_MESSAGE);
    }

    public static String nhapChuoiThuan(Component cha, String thongBao, String tieuDe) {
        return JOptionPane.showInputDialog(cha, thongBao, tieuDe, JOptionPane.PLAIN_MESSAGE);
    }

    public static Object chonMuc(
            Component cha, String thongBao, String tieuDe, Object[] cacLuaChon, Object macDinh) {
        return JOptionPane.showInputDialog(
                cha, thongBao, tieuDe, JOptionPane.QUESTION_MESSAGE, null, cacLuaChon, macDinh);
    }

    public static int tuyChon(
            Component cha, Object noiDung, String tieuDe, Object[] cacLuaChon, boolean hoi) {
        return JOptionPane.showOptionDialog(
                cha,
                noiDung,
                tieuDe,
                JOptionPane.DEFAULT_OPTION,
                hoi ? JOptionPane.QUESTION_MESSAGE : JOptionPane.PLAIN_MESSAGE,
                null,
                cacLuaChon,
                cacLuaChon[0]);
    }

    public static void hienThiMatKhauTam(Component cha, String tenTaiKhoan, String matKhauTam) {
        JPanel noiDung = new JPanel(new BorderLayout(0, 8));
        noiDung.setOpaque(false);
        JLabel nhanMoTa =
                new JLabel(
                        "<html>Đã cấp / đặt lại mật khẩu tạm cho tài khoản <b>"
                                + tenTaiKhoan
                                + "</b>.<br>Tài khoản cần đổi mật khẩu ở lần đăng nhập kế"
                                + " tiếp.</html>");
        JTextField oMatKhau = new JTextField(matKhauTam);
        oMatKhau.setEditable(false);
        oMatKhau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        oMatKhau.setPreferredSize(new Dimension(240, 34));
        JButton btnSaoChep = new NutDinhDang("Sao chép", NutDinhDang.KieuNut.CHINH);
        JLabel nhanTrangThai = new JLabel(" ");
        nhanTrangThai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanTrangThai.setForeground(MauUngDung.XANH_LA);
        btnSaoChep.addActionListener(
                e -> {
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(new StringSelection(matKhauTam), null);
                    oMatKhau.selectAll();
                    nhanTrangThai.setText("Đã sao chép mật khẩu vào bộ nhớ tạm.");
                });
        JPanel hangCopy = new JPanel(new BorderLayout(8, 0));
        hangCopy.setOpaque(false);
        hangCopy.add(oMatKhau, BorderLayout.CENTER);
        hangCopy.add(btnSaoChep, BorderLayout.EAST);
        JPanel phanDuoi = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        phanDuoi.setOpaque(false);
        phanDuoi.add(nhanTrangThai);
        noiDung.add(nhanMoTa, BorderLayout.NORTH);
        noiDung.add(hangCopy, BorderLayout.CENTER);
        noiDung.add(phanDuoi, BorderLayout.SOUTH);
        oMatKhau.selectAll();
        hienThiNoiDung(cha, noiDung, "Mật khẩu tạm");
    }

    private static void hienThi(Component cha, Object thongBao, String tieuDe, int kieu) {
        if (thongBao instanceof Component) {
            JOptionPane tuyChon =
                    new JOptionPane(
                            thongBao,
                            kieu,
                            JOptionPane.DEFAULT_OPTION,
                            null,
                            new Object[] {DONG},
                            DONG);
            JDialog hopThoai = tuyChon.createDialog(cha, tieuDe);
            hopThoai.setResizable(true);
            hopThoai.getRootPane()
                    .registerKeyboardAction(
                            e -> hopThoai.dispose(),
                            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                            JComponent.WHEN_IN_FOCUSED_WINDOW);
            hopThoai.setVisible(true);
            hopThoai.dispose();
            return;
        }
        JOptionPane.showOptionDialog(
                cha,
                thongBao,
                tieuDe,
                JOptionPane.DEFAULT_OPTION,
                kieu,
                null,
                new Object[] {DONG},
                DONG);
    }
}
