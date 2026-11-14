package com.petstation.gui.chung;

import com.petstation.model.chung.MaManHinh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class ThanhTieuDe extends JPanel {
    private final JFrame cuaSo;

    private Point keoGoc;

    private Rectangle binhThuongGioiHan;

    public ThanhTieuDe(JFrame cuaSo, Consumer<MaManHinh> boDieuHuong, Runnable dongThaoTac) {
        this.cuaSo = cuaSo;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 34));
        setBackground(MauUngDung.NAU);
        JButton btnThuongHieu = new JButton("Pet Station – Quản lý cửa hàng thú cưng");
        btnThuongHieu.setIcon(napLogo());
        btnThuongHieu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        btnThuongHieu.setForeground(Color.WHITE);
        btnThuongHieu.setHorizontalAlignment(SwingConstants.LEFT);
        btnThuongHieu.setBorder(new EmptyBorder(0, 10, 0, 10));
        btnThuongHieu.setOpaque(false);
        btnThuongHieu.setContentAreaFilled(false);
        btnThuongHieu.setFocusPainted(false);
        btnThuongHieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThuongHieu.setToolTipText("Về màn hình chính (Alt+Home)");
        btnThuongHieu.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.TONG_QUAN));
        add(btnThuongHieu, BorderLayout.CENTER);
        JPanel khungCacControl = new JPanel(new GridLayout(1, 3));
        khungCacControl.setOpaque(false);
        khungCacControl.add(layNutCuaSo("–", suKien -> cuaSo.setState(Frame.ICONIFIED), false));
        khungCacControl.add(layNutCuaSo("□", suKien -> batTatPhongTo(), false));
        khungCacControl.add(layNutCuaSo("×", suKien -> dongThaoTac.run(), true));
        add(khungCacControl, BorderLayout.EAST);
        MouseAdapter keoBoXuLy =
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent suKien) {
                        keoGoc = suKien.getPoint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent suKien) {
                        if (keoGoc == null || kiemTraPhongTo()) return;
                        Point manHinh = suKien.getLocationOnScreen();
                        cuaSo.setLocation(manHinh.x - keoGoc.x, manHinh.y - keoGoc.y);
                    }

                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (suKien.getClickCount() == 2) batTatPhongTo();
                    }
                };
        addMouseListener(keoBoXuLy);
        addMouseMotionListener(keoBoXuLy);
        btnThuongHieu.addMouseListener(keoBoXuLy);
        btnThuongHieu.addMouseMotionListener(keoBoXuLy);
    }

    private JButton layNutCuaSo(String vanBan, ActionListener boLangNghe, boolean dong) {
        JButton btnNut =
                new JButton(vanBan) {
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        if (getModel().isRollover()) {
                            doHoa.setColor(
                                    dong
                                            ? MauUngDung.DO_BAO_LOI
                                            : MauUngDung.voiDoTrong(Color.WHITE, 28));
                            doHoa.fillRect(0, 0, getWidth(), getHeight());
                        }
                        super.paintComponent(doHoa);
                    }
                };
        btnNut.setPreferredSize(new Dimension(46, 34));
        btnNut.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 16));
        btnNut.setForeground(Color.WHITE);
        btnNut.setBorder(null);
        btnNut.setOpaque(false);
        btnNut.setContentAreaFilled(false);
        btnNut.setFocusPainted(false);
        btnNut.addActionListener(boLangNghe);
        return btnNut;
    }

    private void batTatPhongTo() {
        if (kiemTraPhongTo()) {
            cuaSo.setExtendedState(Frame.NORMAL);
            if (binhThuongGioiHan != null) cuaSo.setBounds(binhThuongGioiHan);
        } else {
            binhThuongGioiHan = cuaSo.getBounds();
            cuaSo.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    private boolean kiemTraPhongTo() {
        return (cuaSo.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    private Icon napLogo() {
        URL taiNguyen = ThanhTieuDe.class.getResource("/images/pet-station-logo.png");
        if (taiNguyen == null) return null;
        Image anh =
                new ImageIcon(taiNguyen).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        return new ImageIcon(anh);
    }
}
