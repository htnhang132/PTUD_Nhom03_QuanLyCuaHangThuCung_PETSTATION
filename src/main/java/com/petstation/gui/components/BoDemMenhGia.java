package com.petstation.gui.components;

import com.petstation.util.TienTe;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Cung cấp giao diện và xử lý tương tác cho mệnh giá bộ đếm.
 *
 * <p>Bảng kiểm đếm tiền theo mệnh giá. Gõ số tờ rồi Enter (hoặc mũi tên xuống) để sang mệnh giá kế
 * tiếp; Enter ở dòng cuối gọi {@code onComplete}. Tổng tiền cập nhật ngay khi gõ.
 */
public final class BoDemMenhGia extends JPanel {
    /** Các mệnh giá. */
    public static final long[] CAC_MENH_GIA = {
        500_000, 200_000, 100_000, 50_000, 20_000, 10_000, 5_000, 2_000, 1_000
    };

    /** Các trường. */
    private final JTextField[] cacTruong = new JTextField[CAC_MENH_GIA.length];

    /** Các số tiền. */
    private final JLabel[] cacSoTien = new JLabel[CAC_MENH_GIA.length];

    /** Nhãn tổng. */
    private final JLabel lblNhanTong = new JLabel("0₫", SwingConstants.RIGHT);

    /** Khi thay đổi. */
    private Runnable khiThayDoi = () -> {};

    /** Khi hoàn tất. */
    private Runnable khiHoanTat = () -> {};

    public BoDemMenhGia() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(3, 6, 3, 6);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;

        themTieuDe("Mệnh giá", 0, giaTriDangXet);
        themTieuDe("Số tờ", 1, giaTriDangXet);
        themTieuDe("Thành tiền", 2, giaTriDangXet);

        for (int chiSo = 0; chiSo < CAC_MENH_GIA.length; chiSo++) {
            // Dòng.
            final int dong = chiSo;
            // Tên.
            JLabel lblTen = new JLabel(TienTe.dinhDang(CAC_MENH_GIA[chiSo]));
            lblTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
            giaTriDangXet.gridy = chiSo + 1;
            giaTriDangXet.gridx = 0;
            giaTriDangXet.weightx = 0.3;
            add(lblTen, giaTriDangXet);

            JTextField txtTruongDangXet = new JTextField("0", 5);
            txtTruongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
            txtTruongDangXet.setHorizontalAlignment(SwingConstants.RIGHT);
            txtTruongDangXet.setPreferredSize(new Dimension(80, 32));
            txtTruongDangXet.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(MauUngDung.VIEN),
                            BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            txtTruongDangXet.addFocusListener(
                    new FocusAdapter() {
                        /**
                         * Lấy tiêu điểm nhận trong lớp xử lý giao diện.
                         *
                         * @param suKien sự kiện
                         */
                        @Override
                        public void focusGained(FocusEvent suKien) {
                            SwingUtilities.invokeLater(txtTruongDangXet::selectAll);
                        }

                        /**
                         * Lấy tiêu điểm mất trong lớp xử lý giao diện.
                         *
                         * @param suKien sự kiện
                         */
                        @Override
                        public void focusLost(FocusEvent suKien) {
                            chuanHoa(dong);
                        }
                    });
            txtTruongDangXet.addKeyListener(
                    new KeyAdapter() {
                        /**
                         * Lấy khóa đã thả trong lớp xử lý giao diện.
                         *
                         * @param suKien sự kiện
                         */
                        @Override
                        public void keyReleased(KeyEvent suKien) {
                            lamMoi(dong);
                        }

                        /**
                         * Lấy khóa đã nhấn trong lớp xử lý giao diện.
                         *
                         * @param suKien sự kiện
                         */
                        @Override
                        public void keyPressed(KeyEvent suKien) {
                            if (suKien.getKeyCode() == KeyEvent.VK_ENTER
                                    || suKien.getKeyCode() == KeyEvent.VK_DOWN) {
                                suKien.consume();
                                chuanHoa(dong);
                                if (dong + 1 < cacTruong.length) layTieuDiem(dong + 1);
                                else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                                if (suKien.getKeyCode() == KeyEvent.VK_ENTER) khiHoanTat.run();
                            } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (suKien.getKeyCode() == KeyEvent.VK_UP && dong > 0) {
                                suKien.consume();
                                chuanHoa(dong);
                                layTieuDiem(dong - 1);
                            }
                        }
                    });
            cacTruong[chiSo] = txtTruongDangXet;
            giaTriDangXet.gridx = 1;
            giaTriDangXet.weightx = 0.2;
            add(txtTruongDangXet, giaTriDangXet);

            cacSoTien[chiSo] = new JLabel("0₫", SwingConstants.RIGHT);
            cacSoTien[chiSo].setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            cacSoTien[chiSo].setForeground(MauUngDung.VAN_BAN_XAM);
            giaTriDangXet.gridx = 2;
            giaTriDangXet.weightx = 0.5;
            add(cacSoTien[chiSo], giaTriDangXet);
        }

        // Dòng.
        JPanel pnlDong = new JPanel();
        pnlDong.setBackground(MauUngDung.VIEN);
        pnlDong.setPreferredSize(new Dimension(1, 1));
        giaTriDangXet.gridy = CAC_MENH_GIA.length + 1;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridwidth = 3;
        add(pnlDong, giaTriDangXet);

        // Tên tổng.
        JLabel lblTenTong = new JLabel("Tổng cộng");
        lblTenTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        giaTriDangXet.gridy = CAC_MENH_GIA.length + 2;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridwidth = 2;
        add(lblTenTong, giaTriDangXet);
        lblNhanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        lblNhanTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        giaTriDangXet.gridx = 2;
        giaTriDangXet.gridwidth = 1;
        add(lblNhanTong, giaTriDangXet);
    }

    private void themTieuDe(String vanBan, int cot, GridBagConstraints giaTriDangXet) {
        // Dòng đang xét.
        JLabel lblDongDangXet =
                new JLabel(vanBan, cot == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        giaTriDangXet.gridy = 0;
        giaTriDangXet.gridx = cot;
        giaTriDangXet.gridwidth = 1;
        add(lblDongDangXet, giaTriDangXet);
    }

    public void layKhiThayDoi(Runnable dongDuLieu) {
        this.khiThayDoi = dongDuLieu == null ? () -> {} : dongDuLieu;
    }

    public void layKhiHoanTat(Runnable dongDuLieu) {
        this.khiHoanTat = dongDuLieu == null ? () -> {} : dongDuLieu;
    }

    public long tinhTong() {
        // Tổng.
        long tong = 0;
        for (int chiSo = 0; chiSo < cacTruong.length; chiSo++)
            tong += dem(chiSo) * CAC_MENH_GIA[chiSo];
        return tong;
    }

    public void datLai() {
        for (int chiSo = 0; chiSo < cacTruong.length; chiSo++) {
            cacTruong[chiSo].setText("0");
            cacSoTien[chiSo].setText("0₫");
        }
        lblNhanTong.setText("0₫");
        khiThayDoi.run();
    }

    public void layTieuDiemDauTien() {
        SwingUtilities.invokeLater(
                () -> {
                    cacTruong[0].requestFocusInWindow();
                    cacTruong[0].selectAll();
                });
    }

    private void layTieuDiem(int dong) {
        cacTruong[dong].requestFocusInWindow();
        cacTruong[dong].selectAll();
    }

    private int dem(int dong) {
        // Các chữ số.
        String cacChuSo = cacTruong[dong].getText().replaceAll("[^0-9]", "");
        if (cacChuSo.isEmpty() || cacChuSo.length() > 7) return 0;
        return Integer.parseInt(cacChuSo);
    }

    private void chuanHoa(int dong) {
        cacTruong[dong].setText(String.valueOf(dem(dong)));
        lamMoi(dong);
    }

    private void lamMoi(int dong) {
        cacSoTien[dong].setText(TienTe.dinhDang(dem(dong) * CAC_MENH_GIA[dong]));
        cacSoTien[dong].setForeground(
                dem(dong) > 0 ? MauUngDung.VAN_BAN_TOI : MauUngDung.VAN_BAN_XAM);
        lblNhanTong.setText(TienTe.dinhDang(tinhTong()));
        khiThayDoi.run();
    }
}
