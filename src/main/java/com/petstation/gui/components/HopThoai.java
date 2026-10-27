package com.petstation.gui.components;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Cung cấp giao diện và xử lý tương tác cho hộp thoại.
 *
 * <p>Hộp thoại dùng chung toàn hệ thống: cùng tiêu đề theo loại, cùng nhãn nút tiếng Việt. Mọi màn
 * gọi lớp này thay vì JOptionPane để giao diện nhất quán.
 *
 * <ul>
 *   <li>Thông báo: tiêu đề "Thông báo", nút "Đóng"
 *   <li>Thành công: tiêu đề "Thành công", nút "Đóng"
 *   <li>Cảnh báo / Lỗi: tiêu đề "Cảnh báo" / "Lỗi", nút "Đóng"
 *   <li>Xác nhận: tiêu đề "Xác nhận", nút "Đồng ý" và "Hủy" (mặc định chọn Hủy khi là thao tác nguy
 *       hiểm)
 * </ul>
 */
public final class HopThoai {
    /** Tiêu đề thông tin. */
    public static final String TIEU_DE_THONG_TIN = "Thông báo";

    /** Tiêu đề thành công. */
    public static final String TIEU_DE_THANH_CONG = "Thành công";

    /** Tiêu đề cảnh báo. */
    public static final String TIEU_DE_CANH_BAO = "Cảnh báo";

    /** Tiêu đề lỗi. */
    public static final String TIEU_DE_LOI = "Lỗi";

    /** Tiêu đề xác nhận. */
    public static final String TIEU_DE_XAC_NHAN = "Xác nhận";

    /** Đóng. */
    private static final String DONG = "Đóng";

    /** Đồng ý. */
    private static final String DONG_Y = "Đồng ý";

    /** Hủy. */
    private static final String HUY = "Hủy";

    private HopThoai() {}

    /**
     * Thiết lập các giá trị mặc định trong HopThoai.
     *
     * <p>Gọi một lần lúc khởi động để các hộp thoại còn lại (nhập liệu, chọn file) cũng dùng tiếng
     * Việt.
     */
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

    /**
     * Xác nhận trong HopThoai.
     *
     * <p>Hỏi xác nhận. Trả true nếu bấm "Đồng ý".
     *
     * @param cha cha
     * @param thongBao thông báo
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public static boolean xacNhan(Component cha, String thongBao) {
        return xacNhan(cha, thongBao, DONG_Y, false);
    }

    /**
     * Xác nhận nguy hiểm trong HopThoai.
     *
     * <p>Hỏi xác nhận cho thao tác nguy hiểm (xóa, hủy...): nút mặc định là "Hủy".
     *
     * @param cha cha
     * @param thongBao thông báo
     * @param nhanXacNhan nhãn xác nhận
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public static boolean xacNhanNguyHiem(Component cha, String thongBao, String nhanXacNhan) {
        return xacNhan(cha, thongBao, nhanXacNhan, true);
    }

    public static boolean xacNhan(
            Component cha, String thongBao, String nhanXacNhan, boolean nguyHiem) {
        // Các lựa chọn.
        Object[] cacLuaChon = {nhanXacNhan, HUY};
        // Lựa chọn.
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

    // ── Tương thích với chữ ký JOptionPane cũ để các màn chuyển sang mà không đổi logic ──

    public static void hienThiThongBaoHopThoai(Component cha, Object thongBao) {
        hienThi(cha, String.valueOf(thongBao), TIEU_DE_THONG_TIN, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void hienThiThongBaoHopThoai(
            Component cha, Object thongBao, String tieuDe, int kieu) {
        // Đã chuẩn hóa.
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

    /**
     * Hiển thị xác nhận hộp thoại trong HopThoai.
     *
     * <p>Trả về YES_OPTION/OK_OPTION khi đồng ý, NO_OPTION/CANCEL_OPTION khi không (giữ mã trả về
     * cũ).
     *
     * @param cha cha
     * @param thongBao thông báo
     * @param tieuDe tiêu đề
     * @param kieuLuaChon kiểu lựa chọn
     * @param kieuThongBao kiểu thông báo
     * @return kết quả hiển thị xác nhận hộp thoại
     */
    public static int hienThiXacNhanHopThoai(
            Component cha, Object thongBao, String tieuDe, int kieuLuaChon, int kieuThongBao) {
        // Đồng ý không.
        boolean dongYKhong = kieuLuaChon == JOptionPane.YES_NO_OPTION;
        // Nguy hiểm.
        boolean nguyHiem =
                kieuThongBao == JOptionPane.WARNING_MESSAGE
                        || kieuThongBao == JOptionPane.ERROR_MESSAGE;
        // Các lựa chọn.
        Object[] cacLuaChon =
                dongYKhong ? new Object[] {"Có", "Không"} : new Object[] {DONG_Y, HUY};
        // Lựa chọn.
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
