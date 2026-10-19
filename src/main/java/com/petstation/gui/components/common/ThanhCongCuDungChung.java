package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;

import java.awt.*;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho dùng chung thanh công cụ.
 *
 * <p>Toolbar tối giản xuất hiện ở tất cả các panels [ < Quay lại ] [ Màn hình chính ] [ ? Trợ giúp
 * ]
 */
public class ThanhCongCuDungChung extends JPanel {
    /** Nút quay lại. */
    private JButton btnNutQuayLai;

    /** Nút trang chính. */
    private JButton btnNutTrangChinh;

    /** Nút trợ giúp. */
    private JButton btnNutTroGiup;

    /** Khởi tạo ThanhCongCuDungChung với các giá trị được cung cấp. */
    public ThanhCongCuDungChung() {
        this(true, true, true);
    }

    /**
     * Khởi tạo ThanhCongCuDungChung với các giá trị được cung cấp.
     *
     * @param hienThiQuayLai hiển thị quay lại
     * @param hienThiTrangChinh hiển thị trang chính
     * @param hienThiTroGiup hiển thị trợ giúp
     */
    public ThanhCongCuDungChung(
            boolean hienThiQuayLai, boolean hienThiTrangChinh, boolean hienThiTroGiup) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (hienThiQuayLai) {
            btnNutQuayLai = new JButton("← Quay lại");
            btnNutQuayLai.setFocusPainted(false);
            btnNutQuayLai.setToolTipText("Quay lại màn hình trước");
            add(btnNutQuayLai);
        }

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (hienThiTrangChinh) {
            btnNutTrangChinh = new JButton("Màn hình chính");
            btnNutTrangChinh.setFocusPainted(false);
            btnNutTrangChinh.setToolTipText("Về trang chủ");
            add(btnNutTrangChinh);
        }

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (hienThiTroGiup) {
            btnNutTroGiup = new JButton("? Trợ giúp");
            btnNutTroGiup.setFocusPainted(false);
            btnNutTroGiup.setToolTipText("Xem hướng dẫn");
            add(btnNutTroGiup);
        }
    }

    // Truy xuất các nút để màn hình đăng ký xử lý sự kiện
    /**
     * Lấy nút quay lại trong ThanhCongCuDungChung.
     *
     * @return kết quả lấy nút quay lại
     */
    public JButton layNutQuayLai() {
        return btnNutQuayLai;
    }

    /**
     * Lấy nút trang chính trong ThanhCongCuDungChung.
     *
     * @return kết quả lấy nút trang chính
     */
    public JButton layNutTrangChinh() {
        return btnNutTrangChinh;
    }

    /**
     * Lấy nút trợ giúp trong ThanhCongCuDungChung.
     *
     * @return kết quả lấy nút trợ giúp
     */
    public JButton layNutTroGiup() {
        return btnNutTroGiup;
    }

    /**
     * Đặt quay lại thao tác trong ThanhCongCuDungChung.
     *
     * <p>Set action cho nút Quay lại
     *
     * @param thaoTac thao tác
     */
    public void datQuayLaiThaoTac(Runnable thaoTac) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (btnNutQuayLai != null) {
            btnNutQuayLai.addActionListener(suKien -> thaoTac.run());
        }
    }

    /**
     * Đặt trang chính thao tác trong ThanhCongCuDungChung.
     *
     * <p>Set action cho nút Màn hình chính
     *
     * @param thaoTac thao tác
     */
    public void datTrangChinhThaoTac(Runnable thaoTac) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (btnNutTrangChinh != null) {
            btnNutTrangChinh.addActionListener(suKien -> thaoTac.run());
        }
    }

    /**
     * Đặt trợ giúp thao tác trong ThanhCongCuDungChung.
     *
     * <p>Set action cho nút Trợ giúp
     *
     * @param thaoTac thao tác
     */
    public void datTroGiupThaoTac(Runnable thaoTac) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (btnNutTroGiup != null) {
            btnNutTroGiup.addActionListener(suKien -> thaoTac.run());
        }
    }

    /**
     * Tạo với điều hướng trong ThanhCongCuDungChung.
     *
     * <p>Tạo toolbar với navigation context (biết nơi quay về)
     *
     * @param khiQuayLai khi quay lại
     * @param khiTrangChinh khi trang chính
     * @return kết quả tạo với điều hướng
     */
    public static ThanhCongCuDungChung taoVoiDieuHuong(
            Runnable khiQuayLai, Runnable khiTrangChinh) {
        // Thanh công cụ.
        ThanhCongCuDungChung pnlThanhCongCu = new ThanhCongCuDungChung();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (khiQuayLai != null) {
            pnlThanhCongCu.datQuayLaiThaoTac(khiQuayLai);
        } else {
            pnlThanhCongCu.layNutQuayLai().setEnabled(false);
        }

        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (khiTrangChinh != null) {
            pnlThanhCongCu.datTrangChinhThaoTac(khiTrangChinh);
        }

        // Thao tác trợ giúp mặc định: hiển thị hướng dẫn chung
        pnlThanhCongCu.datTroGiupThaoTac(
                () -> {
                    HopThoai.hienThiThongBaoHopThoai(
                            pnlThanhCongCu,
                            "Hướng dẫn sử dụng:\n\n"
                                    + "• F2: Thêm mới\n"
                                    + "• F3: Tìm kiếm\n"
                                    + "• F4: Lưu\n"
                                    + "• F8: Thanh toán (POS)\n"
                                    + "• ESC: Hủy/Đóng\n"
                                    + "• Ctrl+K: Tìm kiếm toàn cục\n\n"
                                    + "Liên hệ hỗ trợ: support@petstation.com",
                            "Trợ giúp",
                            JOptionPane.INFORMATION_MESSAGE);
                });

        return pnlThanhCongCu;
    }

    /**
     * Đặt quay lại được bật trong ThanhCongCuDungChung.
     *
     * <p>Disable nút Back khi không có màn hình trước
     *
     * @param duocBat được bật
     */
    public void datQuayLaiDuocBat(boolean duocBat) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (btnNutQuayLai != null) {
            btnNutQuayLai.setEnabled(duocBat);
        }
    }
}
