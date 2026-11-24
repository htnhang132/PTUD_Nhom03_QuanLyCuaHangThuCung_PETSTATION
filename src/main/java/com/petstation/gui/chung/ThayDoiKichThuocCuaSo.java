package com.petstation.gui.chung;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

public final class ThayDoiKichThuocCuaSo extends MouseAdapter {
    private static final int CANH = 6;

    private static final int CANH_TREN = 1, CANH_DUOI = 2, CANH_TRAI = 4, CANH_PHAI = 8;

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
        if (vungDangXet.y < CANH) ngayDangXet |= CANH_TREN;
        if (vungDangXet.y >= vungChu.getHeight() - CANH) ngayDangXet |= CANH_DUOI;
        if (vungDangXet.x < CANH) ngayDangXet |= CANH_TRAI;
        if (vungDangXet.x >= vungChu.getWidth() - CANH) ngayDangXet |= CANH_PHAI;
        return ngayDangXet;
    }

    private static int layConTroCho(int ngayDangXet) {
        return switch (ngayDangXet) {
            case CANH_TREN -> Cursor.N_RESIZE_CURSOR;
            case CANH_DUOI -> Cursor.S_RESIZE_CURSOR;
            case CANH_TRAI -> Cursor.W_RESIZE_CURSOR;
            case CANH_PHAI -> Cursor.E_RESIZE_CURSOR;
            case CANH_TREN | CANH_TRAI -> Cursor.NW_RESIZE_CURSOR;
            case CANH_TREN | CANH_PHAI -> Cursor.NE_RESIZE_CURSOR;
            case CANH_DUOI | CANH_TRAI -> Cursor.SW_RESIZE_CURSOR;
            case CANH_DUOI | CANH_PHAI -> Cursor.SE_RESIZE_CURSOR;
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
        if ((huong & CANH_PHAI) != 0)
            giaTriSau.width = Math.max(nhoNhat.width, nhanGioiHan.width + doLechNgang);
        if ((huong & CANH_DUOI) != 0)
            giaTriSau.height = Math.max(nhoNhat.height, nhanGioiHan.height + doLechDoc);
        if ((huong & CANH_TRAI) != 0) {
            int chieuRong = Math.max(nhoNhat.width, nhanGioiHan.width - doLechNgang);
            giaTriSau.x = nhanGioiHan.x + nhanGioiHan.width - chieuRong;
            giaTriSau.width = chieuRong;
        }
        if ((huong & CANH_TREN) != 0) {
            int chieuCao = Math.max(nhoNhat.height, nhanGioiHan.height - doLechDoc);
            giaTriSau.y = nhanGioiHan.y + nhanGioiHan.height - chieuCao;
            giaTriSau.height = chieuCao;
        }
        cuaSo.setBounds(giaTriSau);
    }
}
