package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public final class ThayDoiKichThuocCuaSo extends MouseAdapter {
    private static final int CANH = 6;

    private static final int SO_PHAN_TU = 1, CHUOI = 2, CHIEU_RONG = 4, SU_KIEN = 8;

    private final JFrame cuaSo;

    private final JComponent vungChu;

    private int huong;

    private Point nhanDiem;

    private Rectangle nhanGioiHan;

    private ThayDoiKichThuocCuaSo(JFrame cuaSo, JComponent vungChu) {
        this.cuaSo = cuaSo;
        this.vungChu = vungChu;
    }

    public static void thietLap(JFrame cuaSo, JComponent vungChu) {
        ThayDoiKichThuocCuaSo dongDuLieu = new ThayDoiKichThuocCuaSo(cuaSo, vungChu);
        vungChu.addMouseListener(dongDuLieu);
        vungChu.addMouseMotionListener(dongDuLieu);
    }

    private int layHuongTai(Point vungDangXet) {
        if ((cuaSo.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) return 0;

        int ngayDangXet = 0;
        if (vungDangXet.y < CANH) ngayDangXet |= SO_PHAN_TU;
        if (vungDangXet.y >= vungChu.getHeight() - CANH) ngayDangXet |= CHUOI;
        if (vungDangXet.x < CANH) ngayDangXet |= CHIEU_RONG;
        if (vungDangXet.x >= vungChu.getWidth() - CANH) ngayDangXet |= SU_KIEN;
        return ngayDangXet;
    }

    private static int layConTroCho(int ngayDangXet) {
        return switch (ngayDangXet) {
            case SO_PHAN_TU -> Cursor.N_RESIZE_CURSOR;
            case CHUOI -> Cursor.S_RESIZE_CURSOR;
            case CHIEU_RONG -> Cursor.W_RESIZE_CURSOR;
            case SU_KIEN -> Cursor.E_RESIZE_CURSOR;
            case SO_PHAN_TU | CHIEU_RONG -> Cursor.NW_RESIZE_CURSOR;
            case SO_PHAN_TU | SU_KIEN -> Cursor.NE_RESIZE_CURSOR;
            case CHUOI | CHIEU_RONG -> Cursor.SW_RESIZE_CURSOR;
            case CHUOI | SU_KIEN -> Cursor.SE_RESIZE_CURSOR;
            default -> Cursor.DEFAULT_CURSOR;
        };
    }

    @Override
    public void mouseMoved(MouseEvent suKien) {
        vungChu.setCursor(Cursor.getPredefinedCursor(layConTroCho(layHuongTai(suKien.getPoint()))));
    }

    @Override
    public void mouseExited(MouseEvent suKien) {
        if (nhanDiem == null) vungChu.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent suKien) {
        huong = layHuongTai(suKien.getPoint());
        if (huong == 0) return;
        nhanDiem = suKien.getLocationOnScreen();
        nhanGioiHan = cuaSo.getBounds();
    }

    @Override
    public void mouseReleased(MouseEvent suKien) {
        nhanDiem = null;
        huong = 0;
    }

    @Override
    public void mouseDragged(MouseEvent suKien) {
        if (huong == 0 || nhanDiem == null) return;

        Point hienTai = suKien.getLocationOnScreen();

        int doLechNgang = hienTai.x - nhanDiem.x;

        int doLechDoc = hienTai.y - nhanDiem.y;

        Rectangle giaTriSau = new Rectangle(nhanGioiHan);

        Dimension nhoNhat = cuaSo.getMinimumSize();
        if ((huong & SU_KIEN) != 0)
            giaTriSau.width = Math.max(nhoNhat.width, nhanGioiHan.width + doLechNgang);
        if ((huong & CHUOI) != 0)
            giaTriSau.height = Math.max(nhoNhat.height, nhanGioiHan.height + doLechDoc);
        if ((huong & CHIEU_RONG) != 0) {
            int chieuRong = Math.max(nhoNhat.width, nhanGioiHan.width - doLechNgang);
            giaTriSau.x = nhanGioiHan.x + nhanGioiHan.width - chieuRong;
            giaTriSau.width = chieuRong;
        }
        if ((huong & SO_PHAN_TU) != 0) {
            int chieuCao = Math.max(nhoNhat.height, nhanGioiHan.height - doLechDoc);
            giaTriSau.y = nhanGioiHan.y + nhanGioiHan.height - chieuCao;
            giaTriSau.height = chieuCao;
        }
        cuaSo.setBounds(giaTriSau);
    }
}
