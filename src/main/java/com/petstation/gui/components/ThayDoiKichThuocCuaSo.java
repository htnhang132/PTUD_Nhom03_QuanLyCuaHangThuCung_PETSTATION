package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho cửa sổ điều chỉnh kích thước.
 *
 * <p>Cho phép kéo cạnh/góc của cửa sổ không viền hệ thống để đổi kích thước.
 */
public final class ThayDoiKichThuocCuaSo extends MouseAdapter {
    /** Cạnh. */
    private static final int CANH = 6;

    /** Số phần tử. */
    private static final int SO_PHAN_TU = 1, CHUOI = 2, CHIEU_RONG = 4, SU_KIEN = 8;

    /** Cửa sổ. */
    private final JFrame cuaSo;

    /** Vùng chủ. */
    private final JComponent vungChu;

    /** Hướng. */
    private int huong;

    /** Nhấn điểm. */
    private Point nhanDiem;

    /** Nhấn giới hạn. */
    private Rectangle nhanGioiHan;

    /**
     * Khởi tạo ThayDoiKichThuocCuaSo với các giá trị được cung cấp.
     *
     * @param cuaSo cửa sổ
     * @param vungChu vùng chủ
     */
    private ThayDoiKichThuocCuaSo(JFrame cuaSo, JComponent vungChu) {
        this.cuaSo = cuaSo;
        this.vungChu = vungChu;
    }

    /**
     * Thiết lập trong ThayDoiKichThuocCuaSo.
     *
     * <p>host là panel có viền dày EDGE px; chỉ vùng viền mới nhận sự kiện chuột.
     *
     * @param cuaSo cửa sổ
     * @param vungChu vùng chủ
     */
    public static void thietLap(JFrame cuaSo, JComponent vungChu) {
        // Dòng dữ liệu.
        ThayDoiKichThuocCuaSo dongDuLieu = new ThayDoiKichThuocCuaSo(cuaSo, vungChu);
        vungChu.addMouseListener(dongDuLieu);
        vungChu.addMouseMotionListener(dongDuLieu);
    }

    /**
     * Lấy hướng tại trong ThayDoiKichThuocCuaSo.
     *
     * @param vungDangXet vùng đang xét
     * @return kết quả lấy hướng tại
     */
    private int layHuongTai(Point vungDangXet) {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if ((cuaSo.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) return 0;
        // Ngày đang xét.
        int ngayDangXet = 0;
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (vungDangXet.y < CANH) ngayDangXet |= SO_PHAN_TU;
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (vungDangXet.y >= vungChu.getHeight() - CANH) ngayDangXet |= CHUOI;
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (vungDangXet.x < CANH) ngayDangXet |= CHIEU_RONG;
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (vungDangXet.x >= vungChu.getWidth() - CANH) ngayDangXet |= SU_KIEN;
        return ngayDangXet;
    }

    /**
     * Lấy con trỏ cho trong ThayDoiKichThuocCuaSo.
     *
     * @param ngayDangXet ngày đang xét
     * @return kết quả lấy con trỏ cho
     */
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

    /**
     * Lấy chuột đã di chuyển trong ThayDoiKichThuocCuaSo.
     *
     * @param suKien sự kiện
     */
    @Override
    public void mouseMoved(MouseEvent suKien) {
        vungChu.setCursor(Cursor.getPredefinedCursor(layConTroCho(layHuongTai(suKien.getPoint()))));
    }

    /**
     * Lấy chuột đã ra trong ThayDoiKichThuocCuaSo.
     *
     * @param suKien sự kiện
     */
    @Override
    public void mouseExited(MouseEvent suKien) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (nhanDiem == null) vungChu.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Lấy chuột đã nhấn trong ThayDoiKichThuocCuaSo.
     *
     * @param suKien sự kiện
     */
    @Override
    public void mousePressed(MouseEvent suKien) {
        huong = layHuongTai(suKien.getPoint());
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (huong == 0) return;
        nhanDiem = suKien.getLocationOnScreen();
        nhanGioiHan = cuaSo.getBounds();
    }

    /**
     * Lấy chuột đã thả trong ThayDoiKichThuocCuaSo.
     *
     * @param suKien sự kiện
     */
    @Override
    public void mouseReleased(MouseEvent suKien) {
        nhanDiem = null;
        huong = 0;
    }

    /**
     * Lấy chuột đã kéo trong ThayDoiKichThuocCuaSo.
     *
     * @param suKien sự kiện
     */
    @Override
    public void mouseDragged(MouseEvent suKien) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (huong == 0 || nhanDiem == null) return;
        // Hiện tại.
        Point hienTai = suKien.getLocationOnScreen();
        // Độ lệch ngang.
        int doLechNgang = hienTai.x - nhanDiem.x;
        // Độ lệch dọc.
        int doLechDoc = hienTai.y - nhanDiem.y;
        // Giá trị sau.
        Rectangle giaTriSau = new Rectangle(nhanGioiHan);
        // Nhỏ nhất.
        Dimension nhoNhat = cuaSo.getMinimumSize();
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if ((huong & SU_KIEN) != 0)
            giaTriSau.width = Math.max(nhoNhat.width, nhanGioiHan.width + doLechNgang);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if ((huong & CHUOI) != 0)
            giaTriSau.height = Math.max(nhoNhat.height, nhanGioiHan.height + doLechDoc);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if ((huong & CHIEU_RONG) != 0) {
            // Chiều rộng.
            int chieuRong = Math.max(nhoNhat.width, nhanGioiHan.width - doLechNgang);
            giaTriSau.x = nhanGioiHan.x + nhanGioiHan.width - chieuRong;
            giaTriSau.width = chieuRong;
        }
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if ((huong & SO_PHAN_TU) != 0) {
            // Chiều cao.
            int chieuCao = Math.max(nhoNhat.height, nhanGioiHan.height - doLechDoc);
            giaTriSau.y = nhanGioiHan.y + nhanGioiHan.height - chieuCao;
            giaTriSau.height = chieuCao;
        }
        cuaSo.setBounds(giaTriSau);
    }
}
