package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho SQL biểu mẫu. */
public final class BieuMauSQL {
    /**
     * Cung cấp giao diện và xử lý tương tác cho lựa chọn.
     *
     * @param giaTri giá trị
     * @param nhan nhãn
     */
    public record LuaChon(String giaTri, String nhan) {
        /**
         * Biểu diễn đối tượng dưới dạng chuỗi.
         *
         * @return chuỗi mô tả đối tượng
         */
        @Override
        public String toString() {
            return nhan;
        }
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho trường.
     *
     * @param khoa khóa
     * @param nhan nhãn
     * @param banDau ban đầu
     * @param cacLuaChon các lựa chọn
     */
    public record TruongDuLieu(String khoa, String nhan, String banDau, List<LuaChon> cacLuaChon) {
        /**
         * Chuyển thành chuỗi trong TruongDuLieu.
         *
         * @param khoa khóa
         * @param nhan nhãn
         * @param banDau ban đầu
         * @return kết quả chuyển thành chuỗi
         */
        public static TruongDuLieu chuyenThanhChuoi(String khoa, String nhan, String banDau) {
            return new TruongDuLieu(khoa, nhan, banDau, null);
        }

        /**
         * Lấy lựa chọn trong TruongDuLieu.
         *
         * @param khoa khóa
         * @param nhan nhãn
         * @param cacDong các dòng
         * @return kết quả lấy lựa chọn
         */
        public static TruongDuLieu layLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
            return new TruongDuLieu(
                    khoa,
                    nhan,
                    "",
                    cacDong.stream()
                            .map(
                                    dongDuLieu ->
                                            new LuaChon(
                                                    String.valueOf(dongDuLieu[0]),
                                                    String.valueOf(dongDuLieu[1])))
                            .toList());
        }
    }

    /** Khởi tạo BieuMauSQL với các giá trị được cung cấp. */
    private BieuMauSQL() {}

    /**
     * Lấy yêu cầu nhập trong BieuMauSQL.
     *
     * @param cha cha
     * @param tieuDe tiêu đề
     * @param cacTruong các trường
     * @return kết quả lấy yêu cầu nhập
     */
    public static Map<String, String> layYeuCauNhap(
            Component cha, String tieuDe, TruongDuLieu... cacTruong) {
        // Biểu mẫu.
        JPanel pnlBieuMau = new JPanel(new GridBagLayout());
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(5, 5, 5, 5);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        // Các đầu vào.
        Map<String, JComponent> cacDauVao = new LinkedHashMap<>();
        for (int chiSo = 0; chiSo < cacTruong.length; chiSo++) {
            // Trường đang xét.
            TruongDuLieu truongDangXet = cacTruong[chiSo];
            giaTriDangXet.gridy = chiSo;
            giaTriDangXet.gridx = 0;
            giaTriDangXet.weightx = 0;
            pnlBieuMau.add(new JLabel(truongDangXet.nhan()), giaTriDangXet);
            // Đầu vào.
            JComponent dauVao;
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (truongDangXet.cacLuaChon() == null)
                dauVao = new JTextField(truongDangXet.banDau(), 28);
            else {
                // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                if (truongDangXet.cacLuaChon().isEmpty()) {
                    HopThoai.hienThiCanhBao(
                            cha,
                            "Chưa có "
                                    + truongDangXet.nhan().toLowerCase()
                                    + " phù hợp. Hãy bổ sung dữ liệu trước.");
                    return null;
                }
                dauVao = new JComboBox<>(truongDangXet.cacLuaChon().toArray(LuaChon[]::new));
            }
            cacDauVao.put(truongDangXet.khoa(), dauVao);
            giaTriDangXet.gridx = 1;
            giaTriDangXet.weightx = 1;
            pnlBieuMau.add(dauVao, giaTriDangXet);
        }
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (HopThoai.hienThiXacNhanHopThoai(
                        cha,
                        pnlBieuMau,
                        tieuDe,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE)
                != JOptionPane.OK_OPTION) return null;
        // Các giá trị.
        Map<String, String> cacGiaTri = new LinkedHashMap<>();
        for (TruongDuLieu truongDangXet : cacTruong) {
            // Đầu vào.
            JComponent dauVao = cacDauVao.get(truongDangXet.khoa());
            cacGiaTri.put(
                    truongDangXet.khoa(),
                    dauVao instanceof JTextField txtGiaTriTam
                            ? txtGiaTriTam.getText().trim()
                            : ((LuaChon) ((JComboBox<?>) dauVao).getSelectedItem()).giaTri());
        }
        return cacGiaTri;
    }
}
