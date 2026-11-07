package com.petstation.gui.components;

import java.awt.Component;

import javax.swing.JOptionPane;
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

    public static void hienThiThongBaoHopThoai(Component cha, Object thongBao) {
        hienThi(cha, String.valueOf(thongBao), TIEU_DE_THONG_TIN, JOptionPane.INFORMATION_MESSAGE);
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
            Component cha, Object thongBao, String tieuDe, int kieuLuaChon) {
        return hienThiXacNhanHopThoai(
                cha, thongBao, tieuDe, kieuLuaChon, JOptionPane.QUESTION_MESSAGE);
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
