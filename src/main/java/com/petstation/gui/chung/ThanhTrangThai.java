package com.petstation.gui.chung;

import com.petstation.dto.PhienDangNhap;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.util.DinhDang;

import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public final class ThanhTrangThai extends JPanel {
    private final JLabel nhanDongHo = layNhanGiaTri("");

    private final JLabel nhanGoiY = layNhanGiaTri("");

    private final JLabel nhanNhanCaLamViec = layNhanGiaTri("");

    private final CaThuNganService caLamViec;

    private final BoLangNgheSuKien<CaThuNganService> boLangNgheCa;

    private final Timer dongHoTimer;

    public ThanhTrangThai(PhienDangNhap phienDangNhap, CaThuNganService caLamViec) {
        this.caLamViec = caLamViec;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(0, 26));
        setBackground(MauUngDung.KEM_NEN_NHAN);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN));
        add(layNhanGiaTri("Người dùng: " + phienDangNhap.getTenHienThi()));
        add(layPhanCach());
        add(layNhanGiaTri("Vai trò: " + phienDangNhap.docVaiTro().getTenHienThi()));
        add(layPhanCach());
        add(nhanNhanCaLamViec);
        nhanNhanCaLamViec.setText("Ca hiện tại: " + caLamViec.layNhan());
        boLangNgheCa =
                ca -> {
                    Runnable capNhat =
                            () -> nhanNhanCaLamViec.setText("Ca hiện tại: " + ca.layNhan());
                    if (SwingUtilities.isEventDispatchThread()) {
                        capNhat.run();
                    } else {
                        SwingUtilities.invokeLater(capNhat);
                    }
                };
        caLamViec.themBoLangNghe(boLangNgheCa);
        add(layPhanCach());
        add(nhanDongHo);
        add(layPhanCach());
        nhanGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        add(nhanGoiY);
        add(Box.createHorizontalGlue());
        capNhatDongHo();
        dongHoTimer = new Timer(15000, suKien -> capNhatDongHo());
        dongHoTimer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        caLamViec.themBoLangNghe(boLangNgheCa);
        if (!dongHoTimer.isRunning()) {
            dongHoTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        caLamViec.xoaBoLangNghe(boLangNgheCa);
        dongHoTimer.stop();
        super.removeNotify();
    }

    public void datGoiY(String vanBan) {
        nhanGoiY.setText(vanBan);
    }

    private void capNhatDongHo() {
        nhanDongHo.setText(LocalTime.now().format(DinhDang.GIO_PHUT));
    }

    private static JLabel layNhanGiaTri(String giaTri) {
        JLabel nhanHienThi = new JLabel(giaTri);
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanHienThi.setForeground(MauUngDung.VAN_BAN_TOI);
        nhanHienThi.setBorder(new EmptyBorder(0, 10, 0, 10));
        return nhanHienThi;
    }

    private JSeparator layPhanCach() {
        JSeparator phanCach = new JSeparator(SwingConstants.VERTICAL);
        phanCach.setMaximumSize(new Dimension(1, 16));
        phanCach.setForeground(MauUngDung.VIEN);
        return phanCach;
    }
}
