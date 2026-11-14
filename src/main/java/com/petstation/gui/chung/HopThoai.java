package com.petstation.gui.chung;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
        String daChuanHoa =
                switch (kieu) {
                    case JOptionPane.ERROR_MESSAGE -> TIEU_DE_LOI;
                    case JOptionPane.WARNING_MESSAGE -> TIEU_DE_CANH_BAO;
                    default ->
                            tieuDe != null && tieuDe.toLowerCase().contains("thành công")
                                    ? TIEU_DE_THANH_CONG
                                    : TIEU_DE_THONG_TIN;
                };
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

    public static String nhapChuoiMacDinh(Component cha, String thongBao, String giaTriMacDinh) {
        return JOptionPane.showInputDialog(cha, thongBao, giaTriMacDinh);
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

    private static void hienThi(Component cha, Object thongBao, String tieuDe, int kieu) {
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
