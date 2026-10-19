package com.petstation.gui.components;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Cung cấp giao diện và xử lý tương tác cho biểu mẫu hộp thoại.
 *
 * <p>Hộp thoại nhập nhanh vài trường, cùng kiểu nút Đồng ý/Hủy với {@link HopThoai}.
 */
public final class BieuMauDialog {
    /** Khởi tạo BieuMauDialog với các giá trị được cung cấp. */
    private BieuMauDialog() {}

    /**
     * Lấy yêu cầu nhập trong BieuMauDialog.
     *
     * <p>Trả về giá trị theo thứ tự nhãn, hoặc null nếu bấm Hủy.
     *
     * @param cha cha
     * @param tieuDe tiêu đề
     * @param cacNhan các nhãn
     * @param banDau ban đầu
     * @return kết quả lấy yêu cầu nhập
     */
    public static List<String> layYeuCauNhap(
            Component cha, String tieuDe, String[] cacNhan, String[] banDau) {
        // Vùng đang xét.
        JPanel pnlVungDangXet = new JPanel(new GridBagLayout());
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(4, 4, 4, 4);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        // Các trường.
        JTextField[] cacTruong = new JTextField[cacNhan.length];
        for (int chiSo = 0; chiSo < cacNhan.length; chiSo++) {
            giaTriDangXet.gridx = 0;
            giaTriDangXet.gridy = chiSo;
            giaTriDangXet.weightx = 0;
            pnlVungDangXet.add(new JLabel(cacNhan[chiSo]), giaTriDangXet);
            cacTruong[chiSo] =
                    new JTextField(
                            banDau != null && chiSo < banDau.length ? banDau[chiSo] : "", 22);
            giaTriDangXet.gridx = 1;
            giaTriDangXet.weightx = 1;
            pnlVungDangXet.add(cacTruong[chiSo], giaTriDangXet);
        }
        // Các lựa chọn.
        Object[] cacLuaChon = {"Đồng ý", "Hủy"};
        // Dòng dữ liệu.
        int dongDuLieu =
                JOptionPane.showOptionDialog(
                        cha,
                        pnlVungDangXet,
                        tieuDe,
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        cacLuaChon,
                        cacLuaChon[0]);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (dongDuLieu != 0) return null;
        // Ra.
        List<String> ra = new ArrayList<>();
        for (JTextField txtTruongDangXet : cacTruong) ra.add(txtTruongDangXet.getText().trim());
        return ra;
    }
}
