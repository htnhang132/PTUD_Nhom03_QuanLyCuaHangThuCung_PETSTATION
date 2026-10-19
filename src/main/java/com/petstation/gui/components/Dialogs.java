package com.petstation.gui.components;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Hộp thoại dùng chung toàn hệ thống: cùng tiêu đề theo loại, cùng nhãn nút tiếng Việt. Mọi màn gọi
 * lớp này thay vì JOptionPane để giao diện nhất quán.
 *
 * <ul>
 *   <li>Thông báo: tiêu đề "Thông báo", nút "Đóng"
 *   <li>Thành công: tiêu đề "Thành công", nút "Đóng"
 *   <li>Cảnh báo / Lỗi: tiêu đề "Cảnh báo" / "Lỗi", nút "Đóng"
 *   <li>Xác nhận: tiêu đề "Xác nhận", nút "Đồng ý" và "Hủy" (mặc định chọn Hủy khi là thao tác nguy
 *       hiểm)
 * </ul>
 */
public final class Dialogs {
    public static final String INFO_TITLE = "Thông báo";
    public static final String SUCCESS_TITLE = "Thành công";
    public static final String WARNING_TITLE = "Cảnh báo";
    public static final String ERROR_TITLE = "Lỗi";
    public static final String CONFIRM_TITLE = "Xác nhận";

    private static final String CLOSE = "Đóng";
    private static final String OK = "Đồng ý";
    private static final String CANCEL = "Hủy";

    private Dialogs() {}

    /**
     * Gọi một lần lúc khởi động để các hộp thoại còn lại (nhập liệu, chọn file) cũng dùng tiếng
     * Việt.
     */
    public static void installDefaults() {
        UIManager.put("OptionPane.okButtonText", OK);
        UIManager.put("OptionPane.cancelButtonText", CANCEL);
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        UIManager.put("OptionPane.titleText", INFO_TITLE);
    }

    public static void info(Component parent, String message) {
        show(parent, message, INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void success(Component parent, String message) {
        show(parent, message, SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        show(parent, message, WARNING_TITLE, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        show(parent, message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /** Hỏi xác nhận. Trả true nếu bấm "Đồng ý". */
    public static boolean confirm(Component parent, String message) {
        return confirm(parent, message, OK, false);
    }

    /** Hỏi xác nhận cho thao tác nguy hiểm (xóa, hủy...): nút mặc định là "Hủy". */
    public static boolean confirmDanger(Component parent, String message, String confirmLabel) {
        return confirm(parent, message, confirmLabel, true);
    }

    public static boolean confirm(
            Component parent, String message, String confirmLabel, boolean danger) {
        Object[] options = {confirmLabel, CANCEL};
        int choice =
                JOptionPane.showOptionDialog(
                        parent,
                        message,
                        CONFIRM_TITLE,
                        JOptionPane.DEFAULT_OPTION,
                        danger ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        danger ? CANCEL : confirmLabel);
        return choice == 0;
    }

    // ── Tương thích với chữ ký JOptionPane cũ để các màn chuyển sang mà không đổi logic ──

    public static void showMessageDialog(Component parent, Object message) {
        show(parent, String.valueOf(message), INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showMessageDialog(Component parent, Object message, String title, int type) {
        String normalized =
                switch (type) {
                    case JOptionPane.ERROR_MESSAGE -> ERROR_TITLE;
                    case JOptionPane.WARNING_MESSAGE -> WARNING_TITLE;
                    default ->
                            title != null && title.toLowerCase().contains("thành công")
                                    ? SUCCESS_TITLE
                                    : INFO_TITLE;
                };
        show(parent, message, normalized, type);
    }

    public static int showConfirmDialog(
            Component parent, Object message, String title, int optionType) {
        return showConfirmDialog(parent, message, title, optionType, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Trả về YES_OPTION/OK_OPTION khi đồng ý, NO_OPTION/CANCEL_OPTION khi không (giữ mã trả về cũ).
     */
    public static int showConfirmDialog(
            Component parent, Object message, String title, int optionType, int messageType) {
        boolean yesNo = optionType == JOptionPane.YES_NO_OPTION;
        boolean danger =
                messageType == JOptionPane.WARNING_MESSAGE
                        || messageType == JOptionPane.ERROR_MESSAGE;
        Object[] options = yesNo ? new Object[] {"Có", "Không"} : new Object[] {OK, CANCEL};
        int choice =
                JOptionPane.showOptionDialog(
                        parent,
                        message,
                        CONFIRM_TITLE,
                        JOptionPane.DEFAULT_OPTION,
                        danger ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        danger ? options[1] : options[0]);
        if (choice == 0) return yesNo ? JOptionPane.YES_OPTION : JOptionPane.OK_OPTION;
        return yesNo ? JOptionPane.NO_OPTION : JOptionPane.CANCEL_OPTION;
    }

    private static void show(Component parent, Object message, String title, int type) {
        JOptionPane.showOptionDialog(
                parent,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                type,
                null,
                new Object[] {CLOSE},
                CLOSE);
    }
}
