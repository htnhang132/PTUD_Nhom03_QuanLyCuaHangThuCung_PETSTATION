package com.petstation.gui.components.common;

import java.awt.*;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho mật khẩu trường với bật tắt.
 *
 * <p>JPasswordField với nút toggle hiển thị/ẩn mật khẩu Icon con mắt ở cuối ô nhập
 */
public class TruongMatKhauAnHien extends JPanel {
    /** Trường mật khẩu. */
    private JPasswordField txtTruongMatKhau;

    /** Nút bật tắt. */
    private JToggleButton btnNutBatTat;

    /** Mật khẩu hiển thị. */
    private boolean matKhauHienThi = false;

    /** Khởi tạo TruongMatKhauAnHien với các giá trị được cung cấp. */
    public TruongMatKhauAnHien() {
        this(20);
    }

    /**
     * Khởi tạo TruongMatKhauAnHien với các giá trị được cung cấp.
     *
     * @param cacCot các cột
     */
    public TruongMatKhauAnHien(int cacCot) {
        setLayout(new BorderLayout(5, 0));

        txtTruongMatKhau = new JPasswordField(cacCot);

        // Nút bật tắt có biểu tượng con mắt
        btnNutBatTat = new JToggleButton();
        btnNutBatTat.setIcon(taoBieuTuongMat(false));
        btnNutBatTat.setSelectedIcon(taoBieuTuongMat(true));
        btnNutBatTat.setPreferredSize(new Dimension(34, 28));
        btnNutBatTat.setBorderPainted(false);
        btnNutBatTat.setContentAreaFilled(false);
        btnNutBatTat.getAccessibleContext().setAccessibleName("Hiển thị hoặc ẩn mật khẩu");
        btnNutBatTat.setFocusPainted(false);
        btnNutBatTat.setToolTipText("Hiển thị/Ẩn mật khẩu");

        btnNutBatTat.addActionListener(suKien -> batTatMatKhauHienThi());

        add(txtTruongMatKhau, BorderLayout.CENTER);
        txtTruongMatKhau.putClientProperty("JTextField.trailingComponent", btnNutBatTat);

        // Đặt đường viền cho khung chứa các thành phần bên trong
        setBorder(BorderFactory.createEmptyBorder());
    }

    /** Bật tắt mật khẩu hiển thị trong TruongMatKhauAnHien. */
    private void batTatMatKhauHienThi() {
        matKhauHienThi = !matKhauHienThi;

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (matKhauHienThi) {
            txtTruongMatKhau.setEchoChar((char) 0); // Hiển thị nội dung thật
            btnNutBatTat.setToolTipText("Ẩn mật khẩu");
            btnNutBatTat.setSelected(true);
        } else {
            txtTruongMatKhau.setEchoChar('●'); // Ẩn với ký tự ●
            btnNutBatTat.setToolTipText("Hiển thị mật khẩu");
            btnNutBatTat.setSelected(false);
        }
    }

    /** Vẽ biểu tượng bằng nét để giữ độ rõ trên màn hình có tỷ lệ phóng lớn. */
    private static Icon taoBieuTuongMat(boolean an) {
        return new Icon() {
            public int getIconWidth() {
                return 20;
            }

            public int getIconHeight() {
                return 20;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D net = (Graphics2D) g.create();
                try {
                    net.translate(x, y);
                    net.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    net.setColor(c.isEnabled() ? new Color(97, 52, 29) : Color.GRAY);
                    net.setStroke(
                            new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    java.awt.geom.Path2D mat = new java.awt.geom.Path2D.Double();
                    mat.moveTo(1, 10);
                    mat.curveTo(6, 2, 14, 2, 19, 10);
                    mat.curveTo(14, 18, 6, 18, 1, 10);
                    net.draw(mat);
                    net.drawOval(7, 7, 6, 6);
                    if (an) net.drawLine(2, 2, 18, 18);
                } finally {
                    net.dispose();
                }
            }
        };
    }

    // Chuyển thao tác cho trường nhập mật khẩu bên trong
    /**
     * Lấy mật khẩu trong TruongMatKhauAnHien.
     *
     * @return kết quả lấy mật khẩu
     */
    public char[] layMatKhau() {
        return txtTruongMatKhau.getPassword();
    }

    /**
     * Lấy văn bản mật khẩu trong TruongMatKhauAnHien.
     *
     * @return kết quả lấy văn bản mật khẩu
     */
    public String layVanBanMatKhau() {
        return new String(txtTruongMatKhau.getPassword());
    }

    /**
     * Đặt mật khẩu trong TruongMatKhauAnHien.
     *
     * @param matKhau mật khẩu
     */
    public void datMatKhau(String matKhau) {
        txtTruongMatKhau.setText(matKhau);
    }

    /** Xóa trong TruongMatKhauAnHien. */
    public void xoa() {
        txtTruongMatKhau.setText("");
    }

    /**
     * Lấy trường mật khẩu trong TruongMatKhauAnHien.
     *
     * @return kết quả lấy trường mật khẩu
     */
    public JPasswordField layTruongMatKhau() {
        return txtTruongMatKhau;
    }

    /**
     * Đặt được bật trong TruongMatKhauAnHien.
     *
     * @param duocBat được bật
     */
    @Override
    public void setEnabled(boolean duocBat) {
        super.setEnabled(duocBat);
        txtTruongMatKhau.setEnabled(duocBat);
        btnNutBatTat.setEnabled(duocBat);
    }

    /**
     * Đặt các cột trong TruongMatKhauAnHien.
     *
     * @param cacCot các cột
     */
    public void datCacCot(int cacCot) {
        txtTruongMatKhau.setColumns(cacCot);
    }

    /**
     * Đặt hiển thị thay thế ký tự trong TruongMatKhauAnHien.
     *
     * @param giaTriDangXet giá trị đang xét
     */
    public void datHienThiThayTheKyTu(char giaTriDangXet) {
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!matKhauHienThi) {
            txtTruongMatKhau.setEchoChar(giaTriDangXet);
        }
    }

    /**
     * Thêm bộ lắng nghe thao tác trong TruongMatKhauAnHien.
     *
     * @param boLangNghe bộ lắng nghe
     */
    public void themBoLangNgheThaoTac(java.awt.event.ActionListener boLangNghe) {
        txtTruongMatKhau.addActionListener(boLangNghe);
    }

    /** Yêu cầu tiêu điểm trong TruongMatKhauAnHien. */
    @Override
    public void requestFocus() {
        txtTruongMatKhau.requestFocus();
    }
}
