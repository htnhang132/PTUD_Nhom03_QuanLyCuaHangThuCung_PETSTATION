package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho nút định dạng.
 *
 * <p>Nút bấm thống nhất. PRIMARY: hành động chính (nền đỏ). DANGER: hành động nguy hiểm (viền đỏ,
 * nền trắng - khác hẳn PRIMARY).
 */
public class NutDinhDang extends JButton {
    /** Cung cấp giao diện và xử lý tương tác cho kiểu. */
    public enum KieuNut {
        CHINH,
        PHU,
        NGUY_HIEM,
        THANH_CONG,
        TRONG_SUOT
    }

    /** Màu nền. */
    private Color mauNen;

    /** Màu rê chuột. */
    private Color mauReChuot;

    /** Màu văn bản. */
    private Color mauVanBan;

    /** Màu viền. */
    private Color mauVien;

    /** Bán kính. */
    private final int banKinh = 10;

    /**
     * Khởi tạo NutDinhDang với các giá trị được cung cấp.
     *
     * @param vanBan văn bản
     * @param kieu kiểu
     */
    public NutDinhDang(String vanBan, KieuNut kieu) {
        super(vanBan);
        apDungKieu(kieu);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(
                new MouseAdapter() {
                    /**
                     * Lấy chuột đã vào trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void mouseEntered(MouseEvent suKien) {
                        repaint();
                    }

                    /**
                     * Lấy chuột đã ra trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void mouseExited(MouseEvent suKien) {
                        repaint();
                    }
                });
    }

    /**
     * Áp dụng kiểu trong NutDinhDang.
     *
     * @param giaTri giá trị
     */
    private void apDungKieu(KieuNut giaTri) {
        mauVien = null;
        switch (giaTri) {
            case CHINH -> {
                mauNen = MauUngDung.DO_THUONG_HIEU;
                mauReChuot = MauUngDung.DO_RE_CHUOT;
                mauVanBan = Color.WHITE;
            }
            case PHU -> {
                mauNen = MauUngDung.VANG;
                mauReChuot = new Color(0xE5, 0xBF, 0x2A);
                mauVanBan = MauUngDung.NAU;
            }
            case NGUY_HIEM -> {
                mauNen = Color.WHITE;
                mauReChuot = MauUngDung.KHONG_HOP_LE_NEN;
                mauVanBan = MauUngDung.VAN_BAN_NGUY_HIEM;
                mauVien = MauUngDung.VAN_BAN_NGUY_HIEM;
            }
            case THANH_CONG -> {
                mauNen = new Color(0xD1, 0xFA, 0xE5);
                mauReChuot = new Color(0xA7, 0xF3, 0xD0);
                mauVanBan = new Color(0x06, 0x59, 0x42);
            }
            case TRONG_SUOT -> {
                mauNen = new Color(0xF3, 0xF4, 0xF6);
                mauReChuot = new Color(0xE5, 0xE7, 0xEB);
                mauVanBan = MauUngDung.VAN_BAN_TOI;
                mauVien = MauUngDung.VIEN;
            }
        }
    }

    /**
     * Vẽ thành phần trong NutDinhDang.
     *
     * @param doHoa đồ họa
     */
    @Override
    protected void paintComponent(Graphics doHoa) {
        // Đồ họa 2.
        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
        doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Được bật.
        boolean duocBat = isEnabled();
        // Rê chuột.
        boolean reChuot = duocBat && getModel().isRollover();
        doHoa2.setColor(!duocBat ? new Color(0xE5, 0xE7, 0xEB) : reChuot ? mauReChuot : mauNen);
        doHoa2.fillRoundRect(0, 0, getWidth(), getHeight(), banKinh, banKinh);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (mauVien != null && duocBat) {
            doHoa2.setColor(mauVien);
            doHoa2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, banKinh, banKinh);
        }
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (isFocusOwner() && duocBat) {
            doHoa2.setColor(MauUngDung.VANG);
            doHoa2.setStroke(new BasicStroke(2));
            doHoa2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, banKinh, banKinh);
        }
        doHoa2.dispose();
        setForeground(duocBat ? mauVanBan : MauUngDung.VO_HIEU_HOA);
        super.paintComponent(doHoa);
    }

    /**
     * Lấy kích thước ưu tiên trong NutDinhDang.
     *
     * @return kết quả lấy kích thước ưu tiên
     */
    @Override
    public Dimension getPreferredSize() {
        // Ngày đang xét.
        Dimension ngayDangXet = super.getPreferredSize();
        return new Dimension(ngayDangXet.width + 20, Math.max(ngayDangXet.height, 36));
    }
}
