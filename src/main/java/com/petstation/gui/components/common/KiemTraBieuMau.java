package com.petstation.gui.components.common;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Cung cấp giao diện và xử lý tương tác cho biểu mẫu bộ kiểm tra.
 *
 * <p>Form validation real-time Hiển thị lỗi ngay khi nhập không đợi đến lúc Submit
 */
public class KiemTraBieuMau {

    /** Các quy tắc. */
    private Map<JComponent, QuyTacKiemTra> cacQuyTac = new HashMap<>();

    /** Lỗi các nhãn. */
    private Map<JComponent, JLabel> loiCacNhan = new HashMap<>();

    /** Ban đầu các viền. */
    private Map<JComponent, Border> banDauCacVien = new HashMap<>();

    /** Màu lỗi. */
    private static final Color MAU_LOI = new Color(220, 53, 69);

    /** Lỗi viền. */
    private static final Border LOI_VIEN = BorderFactory.createLineBorder(MAU_LOI, 2);

    /** Cung cấp giao diện và xử lý tương tác cho kiểm tra quy tắc. */
    public static class QuyTacKiemTra {
        /** Bộ kiểm tra. */
        Function<String, String> boKiemTra; // Trả về thông báo lỗi hoặc null nếu hợp lệ

        /** Bắt buộc. */
        boolean batBuoc;

        /**
         * Khởi tạo QuyTacKiemTra với các giá trị được cung cấp.
         *
         * @param boKiemTra bộ kiểm tra
         * @param batBuoc bắt buộc
         */
        public QuyTacKiemTra(Function<String, String> boKiemTra, boolean batBuoc) {
            this.boKiemTra = boKiemTra;
            this.batBuoc = batBuoc;
        }
    }

    /**
     * Thêm quy tắc trong KiemTraBieuMau.
     *
     * <p>Thêm rule cho text field
     *
     * @param txtTruong trường
     * @param quyTac quy tắc
     * @param cha cha
     */
    public void themQuyTac(JTextField txtTruong, QuyTacKiemTra quyTac, Container cha) {
        cacQuyTac.put(txtTruong, quyTac);
        banDauCacVien.put(txtTruong, txtTruong.getBorder());

        // Tạo nhãn báo lỗi
        // Nhãn lỗi.
        JLabel lblNhanLoi = new JLabel();
        lblNhanLoi.setForeground(MAU_LOI);
        lblNhanLoi.setFont(lblNhanLoi.getFont().deriveFont(Font.PLAIN, 11f));
        lblNhanLoi.setVisible(false);
        loiCacNhan.put(txtTruong, lblNhanLoi);

        // Đặt nhãn lỗi dưới trường nhập nếu bố cục khung cha hỗ trợ
        // Không hiển thị nhãn lỗi khi bố cục khung cha không hỗ trợ
        try {
            // Ràng buộc bố cục.
            GridBagConstraints rangBuocBoCuc = new GridBagConstraints();
            rangBuocBoCuc.gridx = 0;
            rangBuocBoCuc.gridy = layThanhPhanDong(cha, txtTruong) + 1;
            rangBuocBoCuc.gridwidth = 2;
            rangBuocBoCuc.anchor = GridBagConstraints.WEST;
            rangBuocBoCuc.insets = new Insets(2, 20, 5, 5);
            cha.add(lblNhanLoi, rangBuocBoCuc);
        } catch (Exception loi) {
            // Bỏ qua nhãn lỗi nếu không thể thêm vào khung cha
        }

        // Kiểm tra trực tiếp khi nội dung thay đổi
        txtTruong
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            /**
                             * Thêm cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void insertUpdate(DocumentEvent suKien) {
                                kiemTraTruong(txtTruong);
                            }

                            /**
                             * Xóa cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void removeUpdate(DocumentEvent suKien) {
                                kiemTraTruong(txtTruong);
                            }

                            /**
                             * Lấy đã thay đổi cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void changedUpdate(DocumentEvent suKien) {
                                kiemTraTruong(txtTruong);
                            }
                        });
    }

    /**
     * Lấy thành phần dòng trong KiemTraBieuMau.
     *
     * @param cha cha
     * @param thanhPhan thành phần
     * @return kết quả lấy thành phần dòng
     */
    private int layThanhPhanDong(Container cha, Component thanhPhan) {
        // Các thành phần.
        Component[] cacThanhPhan = cha.getComponents();
        for (int chiSo = 0; chiSo < cacThanhPhan.length; chiSo++) {
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (cacThanhPhan[chiSo] == thanhPhan) {
                return chiSo / 2; // Ước lượng gần đúng
            }
        }
        return 0;
    }

    /**
     * Kiểm tra trường trong KiemTraBieuMau.
     *
     * <p>Validate một field
     *
     * @param txtTruong trường
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public boolean kiemTraTruong(JTextField txtTruong) {
        // Quy tắc.
        QuyTacKiemTra quyTac = cacQuyTac.get(txtTruong);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (quyTac == null) return true;

        // Giá trị.
        String giaTri = txtTruong.getText().trim();

        // Kiểm tra trường bắt buộc
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (quyTac.batBuoc && giaTri.isEmpty()) {
            hienThiLoi(txtTruong, "Trường này là bắt buộc");
            return false;
        }

        // Chạy quy tắc kiểm tra riêng
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (!giaTri.isEmpty() && quyTac.boKiemTra != null) {
            // Lỗi.
            String loi = quyTac.boKiemTra.apply(giaTri);
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (loi != null) {
                hienThiLoi(txtTruong, loi);
                return false;
            }
        }

        xoaLoi(txtTruong);
        return true;
    }

    /**
     * Kiểm tra tất cả trong KiemTraBieuMau.
     *
     * <p>Validate tất cả fields
     *
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public boolean kiemTraTatCa() {
        // Tất cả hợp lệ.
        boolean tatCaHopLe = true;

        for (JComponent truong : cacQuyTac.keySet()) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (truong instanceof JTextField) {
                // Hợp lệ.
                boolean hopLe = kiemTraTruong((JTextField) truong);
                tatCaHopLe = tatCaHopLe && hopLe;
            }
        }

        return tatCaHopLe;
    }

    /**
     * Hiển thị lỗi trong KiemTraBieuMau.
     *
     * @param txtTruong trường
     * @param thongBao thông báo
     */
    private void hienThiLoi(JTextField txtTruong, String thongBao) {
        txtTruong.setBorder(LOI_VIEN);

        // Nhãn lỗi.
        JLabel lblNhanLoi = loiCacNhan.get(txtTruong);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (lblNhanLoi != null) {
            lblNhanLoi.setText("" + thongBao);
            lblNhanLoi.setVisible(true);
        }
    }

    /**
     * Xóa lỗi trong KiemTraBieuMau.
     *
     * @param txtTruong trường
     */
    private void xoaLoi(JTextField txtTruong) {
        // Ban đầu viền.
        Border banDauVien = banDauCacVien.get(txtTruong);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (banDauVien != null) {
            txtTruong.setBorder(banDauVien);
        }

        // Nhãn lỗi.
        JLabel lblNhanLoi = loiCacNhan.get(txtTruong);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (lblNhanLoi != null) {
            lblNhanLoi.setVisible(false);
        }
    }

    /**
     * Xóa tất cả các lỗi trong KiemTraBieuMau.
     *
     * <p>Clear tất cả errors
     */
    public void xoaTatCaCacLoi() {
        for (JComponent truong : cacQuyTac.keySet()) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (truong instanceof JTextField) {
                xoaLoi((JTextField) truong);
            }
        }
    }

    // === COMMON VALIDATORS ===

    /**
     * Lấy bắt buộc trong KiemTraBieuMau.
     *
     * @return kết quả lấy bắt buộc
     */
    public static QuyTacKiemTra layBatBuoc() {
        return new QuyTacKiemTra(null, true);
    }

    /**
     * Lấy email trong KiemTraBieuMau.
     *
     * @return kết quả lấy email
     */
    public static QuyTacKiemTra layEmail() {
        // Mẫu.
        Pattern mau = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        return new QuyTacKiemTra(
                giaTri -> {
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (!mau.matcher(giaTri).matches()) {
                        return "Email không đúng định dạng";
                    }
                    return null;
                },
                false);
    }

    /**
     * Lấy số điện thoại trong KiemTraBieuMau.
     *
     * <p>Số điện thoại (10 hoặc 11 số)
     *
     * @return kết quả lấy số điện thoại
     */
    public static QuyTacKiemTra laySoDienThoai() {
        // Mẫu.
        Pattern mau = Pattern.compile("^0\\d{9,10}$");
        return new QuyTacKiemTra(
                giaTri -> {
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (!mau.matcher(giaTri).matches()) {
                        return "Số điện thoại phải có 10-11 số và bắt đầu bằng 0";
                    }
                    return null;
                },
                false);
    }

    /**
     * Lấy số nguyên trong KiemTraBieuMau.
     *
     * <p>Số (integer)
     *
     * @return kết quả lấy số nguyên
     */
    public static QuyTacKiemTra laySoNguyen() {
        return new QuyTacKiemTra(
                giaTri -> {
                    try {
                        Integer.parseInt(giaTri);
                        return null;
                    } catch (NumberFormatException loi) {
                        return "Phải là số nguyên";
                    }
                },
                false);
    }

    /**
     * Đọc số thập phân trong KiemTraBieuMau.
     *
     * <p>Số (double)
     *
     * @return kết quả đọc số thập phân
     */
    public static QuyTacKiemTra docSoThapPhan() {
        return new QuyTacKiemTra(
                giaTri -> {
                    try {
                        Double.parseDouble(giaTri);
                        return null;
                    } catch (NumberFormatException loi) {
                        return "Phải là số";
                    }
                },
                false);
    }

    /**
     * Lấy nhỏ nhất độ dài trong KiemTraBieuMau.
     *
     * @param nhoNhat nhỏ nhất
     * @return kết quả lấy nhỏ nhất độ dài
     */
    public static QuyTacKiemTra layNhoNhatDoDai(int nhoNhat) {
        return new QuyTacKiemTra(
                giaTri -> {
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (giaTri.length() < nhoNhat) {
                        return "Phải có ít nhất " + nhoNhat + "ký tự";
                    }
                    return null;
                },
                false);
    }

    /**
     * Lấy lớn nhất độ dài trong KiemTraBieuMau.
     *
     * @param lonNhat lớn nhất
     * @return kết quả lấy lớn nhất độ dài
     */
    public static QuyTacKiemTra layLonNhatDoDai(int lonNhat) {
        return new QuyTacKiemTra(
                giaTri -> {
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (giaTri.length() > lonNhat) {
                        return "Không được quá " + lonNhat + "ký tự";
                    }
                    return null;
                },
                false);
    }

    /**
     * Lấy khoảng trong KiemTraBieuMau.
     *
     * @param nhoNhat nhỏ nhất
     * @param lonNhat lớn nhất
     * @return kết quả lấy khoảng
     */
    public static QuyTacKiemTra layKhoang(double nhoNhat, double lonNhat) {
        return new QuyTacKiemTra(
                giaTri -> {
                    try {
                        // Số.
                        double so = Double.parseDouble(giaTri);
                        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                        if (so < nhoNhat || so > lonNhat) {
                            return String.format("Phải trong khoảng %.0f - %.0f", nhoNhat, lonNhat);
                        }
                        return null;
                    } catch (NumberFormatException loi) {
                        return "Phải là số";
                    }
                },
                false);
    }

    /**
     * Lấy tùy chỉnh trong KiemTraBieuMau.
     *
     * @param boKiemTra bộ kiểm tra
     * @return kết quả lấy tùy chỉnh
     */
    public static QuyTacKiemTra layTuyChinh(Function<String, String> boKiemTra) {
        return new QuyTacKiemTra(boKiemTra, false);
    }

    /**
     * Kết hợp trong KiemTraBieuMau.
     *
     * @param cacQuyTac các quy tắc
     * @return kết quả kết hợp
     */
    public static QuyTacKiemTra ketHop(QuyTacKiemTra... cacQuyTac) {
        return new QuyTacKiemTra(
                giaTri -> {
                    for (QuyTacKiemTra quyTac : cacQuyTac) {
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (quyTac.boKiemTra != null) {
                            // Lỗi.
                            String loi = quyTac.boKiemTra.apply(giaTri);
                            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                            if (loi != null) return loi;
                        }
                    }
                    return null;
                },
                false);
    }
}
