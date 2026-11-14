package com.petstation.gui.banhang;

import com.petstation.gui.chung.MauUngDung;
import com.petstation.util.DinhDang;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class BoDemMenhGia extends JPanel {
    public static final long[] CAC_MENH_GIA = {
        500_000, 200_000, 100_000, 50_000, 20_000, 10_000, 5_000, 2_000, 1_000
    };

    private final JTextField[] cacTruong = new JTextField[CAC_MENH_GIA.length];

    private final JLabel[] cacSoTien = new JLabel[CAC_MENH_GIA.length];

    private final JLabel nhanNhanTong = new JLabel("0₫", SwingConstants.RIGHT);

    private Runnable khiThayDoi = () -> {};

    private Runnable khiHoanTat = () -> {};

    public BoDemMenhGia() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        themTieuDe("Mệnh giá", 0, gbc, SwingConstants.LEFT);
        themTieuDe("Số tờ", 1, gbc, SwingConstants.CENTER);
        themTieuDe("Thành tiền", 2, gbc, SwingConstants.RIGHT);
        int gridRow = 1;
        for (int chiSo = 0; chiSo < CAC_MENH_GIA.length; chiSo++) {
            themDongMenhGia(chiSo, gridRow++, gbc);
            if (chiSo < CAC_MENH_GIA.length - 1)
                themDuongKe(gridRow++, gbc, MauUngDung.mau(0xF0ECE1), 1, new Insets(1, 6, 1, 6));
        }
        themDuongKe(gridRow++, gbc, MauUngDung.KEM_VIEN, 2, new Insets(6, 6, 6, 6));
        themHangTongCong(gridRow, gbc);
    }

    private void themDongMenhGia(int dong, int gridRow, GridBagConstraints gbc) {
        JLabel nhanTen = new JLabel(DinhDang.dinhDang(CAC_MENH_GIA[dong]));
        nhanTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTen.setForeground(MauUngDung.VAN_BAN_TOI);
        gbc.gridy = gridRow;
        gbc.gridx = 0;
        gbc.weightx = 0.30;
        add(nhanTen, gbc);
        JTextField oNhapLieu = taoOTruongSoTo(dong);
        JButton btnGiam = taoNutTangGiam("-");
        btnGiam.addActionListener(
                e -> {
                    int hienTai = dem(dong);
                    if (hienTai > 0) {
                        oNhapLieu.setText(String.valueOf(hienTai - 1));
                        lamMoi(dong);
                    }
                });
        JButton btnTang = taoNutTangGiam("+");
        btnTang.addActionListener(
                e -> {
                    oNhapLieu.setText(String.valueOf(dem(dong) + 1));
                    lamMoi(dong);
                });
        JPanel khungSoTo = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        khungSoTo.setOpaque(false);
        khungSoTo.setPreferredSize(new Dimension(110, 30));
        khungSoTo.add(btnGiam);
        khungSoTo.add(oNhapLieu);
        khungSoTo.add(btnTang);
        cacTruong[dong] = oNhapLieu;
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        add(khungSoTo, gbc);
        cacSoTien[dong] = new JLabel("0₫", SwingConstants.RIGHT);
        cacSoTien[dong].setFont(MauUngDung.taoPhongChu(Font.PLAIN, 14));
        cacSoTien[dong].setForeground(MauUngDung.VAN_BAN_XAM);
        gbc.gridx = 2;
        gbc.weightx = 0.35;
        add(cacSoTien[dong], gbc);
    }

    private JTextField taoOTruongSoTo(int dong) {
        JTextField oNhapLieu = new JTextField("0", 3);
        oNhapLieu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        oNhapLieu.setHorizontalAlignment(SwingConstants.CENTER);
        oNhapLieu.setPreferredSize(new Dimension(50, 28));
        oNhapLieu.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.KEM_VIEN),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        oNhapLieu.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent suKien) {
                        SwingUtilities.invokeLater(oNhapLieu::selectAll);
                    }

                    @Override
                    public void focusLost(FocusEvent suKien) {
                        chuanHoa(dong);
                    }
                });
        oNhapLieu.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent suKien) {
                        lamMoi(dong);
                    }

                    @Override
                    public void keyPressed(KeyEvent suKien) {
                        xuLyPhimSoTo(suKien, dong);
                    }
                });
        return oNhapLieu;
    }

    private void xuLyPhimSoTo(KeyEvent suKien, int dong) {
        int code = suKien.getKeyCode();
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_DOWN) {
            suKien.consume();
            chuanHoa(dong);
            if (dong + 1 < cacTruong.length) layTieuDiem(dong + 1);
            else if (code == KeyEvent.VK_ENTER) khiHoanTat.run();
        } else if (code == KeyEvent.VK_UP && dong > 0) {
            suKien.consume();
            chuanHoa(dong);
            layTieuDiem(dong - 1);
        }
    }

    private void themDuongKe(
            int gridRow, GridBagConstraints gbc, Color mau, int cao, Insets insets) {
        JPanel khungKe = new JPanel();
        khungKe.setBackground(mau);
        khungKe.setPreferredSize(new Dimension(1, cao));
        gbc.gridy = gridRow;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = insets;
        add(khungKe, gbc);
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.gridwidth = 1;
    }

    private void themHangTongCong(int gridRow, GridBagConstraints gbc) {
        JLabel nhanTenTong = new JLabel("Tổng cộng kiểm đếm");
        nhanTenTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        nhanTenTong.setForeground(MauUngDung.VAN_BAN_TOI);
        gbc.gridy = gridRow;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(nhanTenTong, gbc);
        nhanNhanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        nhanNhanTong.setForeground(MauUngDung.DO_NHAN_MANH);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        add(nhanNhanTong, gbc);
    }

    private static JButton taoNutTangGiam(String kyTu) {
        JButton btn = new JButton(kyTu);
        btn.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(26, 28));
        btn.setBackground(MauUngDung.mau(0xF3EFEA));
        btn.setForeground(MauUngDung.mau(0x3D1A0A));
        btn.setBorder(BorderFactory.createLineBorder(MauUngDung.KEM_VIEN));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    private void themTieuDe(String vanBan, int cot, GridBagConstraints giaTriDangXet, int canLe) {
        JLabel nhanDongDangXet = new JLabel(vanBan, canLe);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        giaTriDangXet.gridy = 0;
        giaTriDangXet.gridx = cot;
        giaTriDangXet.gridwidth = 1;
        add(nhanDongDangXet, giaTriDangXet);
    }

    public void layKhiThayDoi(Runnable dongDuLieu) {
        this.khiThayDoi = dongDuLieu == null ? () -> {} : dongDuLieu;
    }

    public void layKhiHoanTat(Runnable dongDuLieu) {
        this.khiHoanTat = dongDuLieu == null ? () -> {} : dongDuLieu;
    }

    public long tinhTong() {
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
        nhanNhanTong.setText("0₫");
        khiThayDoi.run();
    }

    private void layTieuDiem(int dong) {
        cacTruong[dong].requestFocusInWindow();
        cacTruong[dong].selectAll();
    }

    private int dem(int dong) {
        String cacChuSo = cacTruong[dong].getText().replaceAll("[^0-9]", "");
        if (cacChuSo.isEmpty() || cacChuSo.length() > 7) return 0;
        return Integer.parseInt(cacChuSo);
    }

    private void chuanHoa(int dong) {
        cacTruong[dong].setText(String.valueOf(dem(dong)));
        lamMoi(dong);
    }

    private void lamMoi(int dong) {
        cacSoTien[dong].setText(DinhDang.dinhDang(dem(dong) * CAC_MENH_GIA[dong]));
        cacSoTien[dong].setForeground(
                dem(dong) > 0 ? MauUngDung.VAN_BAN_TOI : MauUngDung.VAN_BAN_XAM);
        nhanNhanTong.setText(DinhDang.dinhDang(tinhTong()));
        khiThayDoi.run();
    }
}
