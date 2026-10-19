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
        btnNutBatTat = new JToggleButton("Hiện");
        btnNutBatTat.setPreferredSize(new Dimension(64, 28));
        btnNutBatTat.setFocusPainted(false);
        btnNutBatTat.setToolTipText("Hiển thị/Ẩn mật khẩu");

        btnNutBatTat.addActionListener(suKien -> batTatMatKhauHienThi());

        add(txtTruongMatKhau, BorderLayout.CENTER);
        add(btnNutBatTat, BorderLayout.EAST);

        // Đặt đường viền cho khung chứa các thành phần bên trong
        setBorder(BorderFactory.createEmptyBorder());
        txtTruongMatKhau.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(3, 5, 3, 5)));
    }

    /** Bật tắt mật khẩu hiển thị trong TruongMatKhauAnHien. */
    private void batTatMatKhauHienThi() {
        matKhauHienThi = !matKhauHienThi;

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (matKhauHienThi) {
            txtTruongMatKhau.setEchoChar((char) 0); // Hiển thị nội dung thật
            btnNutBatTat.setText("Ẩn");
            btnNutBatTat.setSelected(true);
        } else {
            txtTruongMatKhau.setEchoChar('●'); // Ẩn với ký tự ●
            btnNutBatTat.setText("Hiện");
            btnNutBatTat.setSelected(false);
        }
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
