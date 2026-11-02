package com.petstation.gui.components;

import com.petstation.model.PhienDangNhap;
import com.petstation.service.CaThuNganService;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/** Cung cấp giao diện và xử lý tương tác cho màn hình thanh trạng thái. */
public final class ThanhTrangThai extends JPanel {
    /** Đồng hồ. */
    private final JLabel lblDongHo = layNhanGiaTri("");

    /** Gợi ý. */
    private final JLabel lblGoiY = layNhanGiaTri("");

    /** Nhãn ca làm việc. */
    private final JLabel lblNhanCaLamViec = layNhanGiaTri("");

    public ThanhTrangThai(PhienDangNhap phienDangNhap, CaThuNganService caLamViec) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(0, 26));
        setBackground(new Color(0xF3, 0xED, 0xE5));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN));

        add(layNhanGiaTri("Người dùng: " + phienDangNhap.layTenHienThi()));
        add(layPhanCach());
        add(layNhanGiaTri("Vai trò: " + phienDangNhap.docVaiTro().layTenHienThi()));
        add(layPhanCach());
        add(lblNhanCaLamViec);
        lblNhanCaLamViec.setText("Ca hiện tại: " + caLamViec.layNhan());
        caLamViec.themBoLangNghe(
                () -> lblNhanCaLamViec.setText("Ca hiện tại: " + caLamViec.layNhan()));
        add(layPhanCach());
        add(lblDongHo);
        add(layPhanCach());
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        add(lblGoiY);
        add(Box.createHorizontalGlue());

        capNhatDongHo();
        new Timer(15000, suKien -> capNhatDongHo()).start();
    }

    public void datGoiY(String vanBan) {
        lblGoiY.setText(vanBan);
    }

    private void capNhatDongHo() {
        lblDongHo.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private static JLabel layNhanGiaTri(String giaTri) {
        // Nhãn.
        JLabel lblNhan = new JLabel(giaTri);
        lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblNhan.setForeground(MauUngDung.VAN_BAN_TOI);
        lblNhan.setBorder(new EmptyBorder(0, 10, 0, 10));
        return lblNhan;
    }

    private JSeparator layPhanCach() {
        // Phân cách.
        JSeparator phanCach = new JSeparator(SwingConstants.VERTICAL);
        phanCach.setMaximumSize(new Dimension(1, 16));
        phanCach.setForeground(MauUngDung.VIEN);
        return phanCach;
    }
}
