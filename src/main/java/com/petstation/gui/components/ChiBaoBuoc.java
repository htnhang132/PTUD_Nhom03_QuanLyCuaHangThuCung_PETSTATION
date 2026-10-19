package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho bước chỉ báo.
 *
 * <p>Thanh các bước của một quy trình. Bước đã qua hiện dấu tick, bước hiện tại tô đỏ.
 */
public final class ChiBaoBuoc extends JComponent {
    /** Các bước. */
    private final List<String> cacBuoc;

    /** Hiện tại. */
    private int hienTai;

    /** Khi bấm. */
    private IntConsumer khiBam;

    /**
     * Khởi tạo ChiBaoBuoc với các giá trị được cung cấp.
     *
     * @param cacBuoc các bước
     */
    public ChiBaoBuoc(List<String> cacBuoc) {
        this.cacBuoc = List.copyOf(cacBuoc);
        setPreferredSize(new Dimension(0, 44));
        setOpaque(false);
        addMouseListener(
                new MouseAdapter() {
                    /**
                     * Lấy chuột đã bấm trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (khiBam == null) return;
                        // Chỉ số.
                        int chiSo = layChiSoTai(suKien.getX());
                        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (chiSo >= 0) khiBam.accept(chiSo);
                    }
                });
    }

    /**
     * Đặt hiện tại trong ChiBaoBuoc.
     *
     * @param chiSo chỉ số
     */
    public void datHienTai(int chiSo) {
        this.hienTai = Math.max(0, Math.min(chiSo, cacBuoc.size() - 1));
        repaint();
    }

    /**
     * Lấy hiện tại trong ChiBaoBuoc.
     *
     * @return kết quả lấy hiện tại
     */
    public int layHienTai() {
        return hienTai;
    }

    /**
     * Đặt khi bấm trong ChiBaoBuoc.
     *
     * <p>Cho phép bấm vào từng bước (dùng trong thanh luồng của khung chính).
     *
     * @param khiBam khi bấm
     */
    public void datKhiBam(IntConsumer khiBam) {
        this.khiBam = khiBam;
        setCursor(
                khiBam == null
                        ? Cursor.getDefaultCursor()
                        : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Lấy chỉ số tại trong ChiBaoBuoc.
     *
     * @param toaDoNgang tọa độ ngang
     * @return kết quả lấy chỉ số tại
     */
    private int layChiSoTai(int toaDoNgang) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cacBuoc.isEmpty()) return -1;
        // Ô.
        int o = getWidth() / cacBuoc.size();
        // Chỉ số.
        int chiSo = toaDoNgang / Math.max(o, 1);
        return chiSo >= 0 && chiSo < cacBuoc.size() ? chiSo : -1;
    }

    /**
     * Vẽ thành phần trong ChiBaoBuoc.
     *
     * @param doHoa đồ họa
     */
    @Override
    protected void paintComponent(Graphics doHoa) {
        // Đồ họa.
        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
        doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        doHoa2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Số phần tử.
        int soPhanTu = cacBuoc.size();
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (soPhanTu == 0) {
            doHoa2.dispose();
            return;
        }
        // Ô.
        int o = getWidth() / soPhanTu;
        // Tâm dọc.
        int tamDoc = getHeight() / 2;
        // Dòng dữ liệu.
        int dongDuLieu = 11;
        doHoa2.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        // Thông số phông chữ.
        FontMetrics thongSoPhongChu = doHoa2.getFontMetrics();
        for (int chiSo = 0; chiSo < soPhanTu; chiSo++) {
            // Trái.
            int trai = chiSo * o;
            // Tâm ngang.
            int tamNgang = trai + 16 + dongDuLieu;
            // Đã hoàn tất.
            boolean daHoanTat = chiSo < hienTai;
            // Hoạt động.
            boolean hoatDong = chiSo == hienTai;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (chiSo < soPhanTu - 1) {
                doHoa2.setColor(daHoanTat ? MauUngDung.XANH_LA : MauUngDung.VIEN);
                doHoa2.setStroke(new BasicStroke(2f));
                // Nhãn kết thúc.
                int nhanKetThuc =
                        tamNgang + dongDuLieu + 8 + thongSoPhongChu.stringWidth(cacBuoc.get(chiSo));
                doHoa2.drawLine(
                        Math.min(nhanKetThuc + 8, trai + o - 12), tamDoc, trai + o - 6, tamDoc);
            }
            doHoa2.setColor(
                    hoatDong
                            ? MauUngDung.DO_THUONG_HIEU
                            : daHoanTat ? MauUngDung.XANH_LA : Color.WHITE);
            doHoa2.fillOval(
                    tamNgang - dongDuLieu, tamDoc - dongDuLieu, 2 * dongDuLieu, 2 * dongDuLieu);
            doHoa2.setColor(hoatDong || daHoanTat ? Color.WHITE : MauUngDung.VAN_BAN_XAM);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!hoatDong && !daHoanTat) {
                doHoa2.setColor(MauUngDung.VIEN);
                doHoa2.setStroke(new BasicStroke(1.5f));
                doHoa2.drawOval(
                        tamNgang - dongDuLieu, tamDoc - dongDuLieu, 2 * dongDuLieu, 2 * dongDuLieu);
                doHoa2.setColor(MauUngDung.VAN_BAN_XAM);
            }
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (daHoanTat) {
                doHoa2.setStroke(
                        new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                doHoa2.drawLine(tamNgang - 4, tamDoc, tamNgang - 1, tamDoc + 3);
                doHoa2.drawLine(tamNgang - 1, tamDoc + 3, tamNgang + 5, tamDoc - 3);
            } else {
                // Số.
                String so = String.valueOf(chiSo + 1);
                doHoa2.drawString(
                        so,
                        tamNgang - thongSoPhongChu.stringWidth(so) / 2,
                        tamDoc + thongSoPhongChu.getAscent() / 2 - 1);
            }
            doHoa2.setColor(hoatDong ? MauUngDung.VAN_BAN_TOI : MauUngDung.VAN_BAN_XAM);
            doHoa2.drawString(
                    cacBuoc.get(chiSo),
                    tamNgang + dongDuLieu + 8,
                    tamDoc + thongSoPhongChu.getAscent() / 2 - 1);
        }
        doHoa2.dispose();
    }
}
