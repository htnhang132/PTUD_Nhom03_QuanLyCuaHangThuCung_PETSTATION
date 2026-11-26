package com.petstation.gui.chung;

import com.petstation.gui.chung.trogiup.SoTayTroGiup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public final class HopThoaiTroGiup {
    private HopThoaiTroGiup() {}

    public static NutDinhDang taoNutTroGiup(JDialog hopThoai, Class<?> loaiBieuMau) {
        NoiDungHuongDan hd = SoTayTroGiup.layTheoLop(loaiBieuMau);
        NutDinhDang nut = new NutDinhDang("Trợ giúp (Alt+F1)", NutDinhDang.KieuNut.TRONG_SUOT);
        nut.addActionListener(e -> hienThi(hopThoai, hd));
        hopThoai.getRootPane()
                .registerKeyboardAction(
                        e -> hienThi(hopThoai, hd),
                        KeyStroke.getKeyStroke("alt F1"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        return nut;
    }

    public static final class NoiDungHuongDan {
        public final String tieuDe;
        public final String mucDich;
        public final List<String> cacBuoc;
        public final List<String> luuY;
        public final List<String[]> phimTat;

        public NoiDungHuongDan(
                String tieuDe,
                String mucDich,
                List<String> cacBuoc,
                List<String> luuY,
                List<String[]> phimTat) {
            this.tieuDe = tieuDe;
            this.mucDich = mucDich;
            this.cacBuoc = cacBuoc;
            this.luuY = luuY;
            this.phimTat = phimTat;
        }
    }

    public static void hienThi(Component cha, String tieuDeGoc, String noiDungGoc) {
        hienThi(cha, xayDungNoiDung(cha, tieuDeGoc, noiDungGoc));
    }

    public static void hienThi(Component cha, NoiDungHuongDan hd) {
        JPanel khungChinh = taoGiaoDien(hd);
        Window win = cha == null ? null : SwingUtilities.getWindowAncestor(cha);
        JDialog dialog =
                new JDialog(
                        win,
                        HopThoai.chuanHoaTieuDe("Hướng dẫn sử dụng — " + hd.tieuDe),
                        Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel khungBao = new JPanel(new BorderLayout(0, 12));
        khungBao.setBackground(MauUngDung.NEN_KEM);
        khungBao.setBorder(new EmptyBorder(16, 18, 14, 18));
        khungBao.add(khungChinh, BorderLayout.CENTER);
        JPanel khungChan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        khungChan.setOpaque(false);
        NutDinhDang btnDong = new NutDinhDang("Đã hiểu (Esc)", NutDinhDang.KieuNut.CHINH);
        btnDong.setPreferredSize(new Dimension(130, 34));
        btnDong.addActionListener(e -> dialog.dispose());
        khungChan.add(btnDong);
        khungBao.add(khungChan, BorderLayout.SOUTH);
        dialog.getRootPane()
                .registerKeyboardAction(
                        e -> dialog.dispose(),
                        KeyStroke.getKeyStroke("ESCAPE"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.getRootPane().setDefaultButton(btnDong);
        dialog.setContentPane(khungBao);
        dialog.setMinimumSize(new Dimension(580, 380));
        HopThoai.canChinhKichThuocVuaManHinh(dialog, 660, 480);
        dialog.setResizable(true);
        dialog.setLocationRelativeTo(win);
        dialog.setVisible(true);
    }

    public static JPanel taoGiaoDien(NoiDungHuongDan hd) {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);
        BoGocPanel khungDau = new BoGocPanel(12, Color.WHITE, true);
        khungDau.setLayout(new BorderLayout(12, 4));
        khungDau.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel nhanHuyHieu = new JLabel("HƯỚNG DẪN NHANH", SwingConstants.CENTER);
        nhanHuyHieu.setOpaque(true);
        nhanHuyHieu.setBackground(MauUngDung.DO_NHAT);
        nhanHuyHieu.setForeground(MauUngDung.DO_THUONG_HIEU);
        nhanHuyHieu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        nhanHuyHieu.setBorder(new EmptyBorder(4, 10, 4, 10));
        JPanel khungTieuDeDau = new JPanel(new BorderLayout(8, 2));
        khungTieuDeDau.setOpaque(false);
        JTextArea nhanTen = taoVanBan(hd.tieuDe);
        nhanTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 17));
        nhanTen.setForeground(MauUngDung.VAN_BAN_TOI);
        JPanel huyHieu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        huyHieu.setOpaque(false);
        huyHieu.add(nhanHuyHieu);
        khungTieuDeDau.add(huyHieu, BorderLayout.NORTH);
        khungTieuDeDau.add(nhanTen, BorderLayout.CENTER);
        JTextArea nhanMucDich = taoVanBan(hd.mucDich);
        nhanMucDich.setForeground(MauUngDung.VAN_BAN_XAM);
        khungDau.add(khungTieuDeDau, BorderLayout.NORTH);
        khungDau.add(nhanMucDich, BorderLayout.CENTER);
        root.add(khungDau, BorderLayout.NORTH);
        JPanel khungThan = new KhungCuonTroGiup();
        khungThan.setLayout(new BoxLayout(khungThan, BoxLayout.Y_AXIS));
        khungThan.setOpaque(false);
        khungThan.add(taoTheMuc("1. Các bước thao tác chính", hd.cacBuoc, true));
        khungThan.add(Box.createVerticalStrut(10));
        khungThan.add(taoTheMuc("2. Lưu ý", hd.luuY, false));
        khungThan.add(Box.createVerticalStrut(10));
        khungThan.add(taoThePhimTat("3. Phím tắt", hd.phimTat));
        JScrollPane scr =
                new JScrollPane(
                        khungThan,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(null);
        scr.setOpaque(false);
        scr.getViewport().setOpaque(false);
        scr.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scr, BorderLayout.CENTER);
        return root;
    }

    private static BoGocPanel taoTheMuc(String tieuDe, List<String> dong, boolean danhSo) {
        BoGocPanel the = new BoGocPanel(12, Color.WHITE, true);
        the.setLayout(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel nhanTieuDe = new JLabel(tieuDe);
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTieuDe.setForeground(MauUngDung.NAU);
        the.add(nhanTieuDe, BorderLayout.NORTH);
        JPanel khungDanhSach = new JPanel();
        khungDanhSach.setLayout(new BoxLayout(khungDanhSach, BoxLayout.Y_AXIS));
        khungDanhSach.setOpaque(false);
        for (int i = 0; i < dong.size(); i++) {
            JPanel hang = new JPanel(new BorderLayout(10, 0));
            hang.setOpaque(false);
            JLabel icon = new JLabel(danhSo ? "Bước " + (i + 1) : "•");
            icon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
            icon.setForeground(danhSo ? MauUngDung.DO_THUONG_HIEU : MauUngDung.NAU);
            if (danhSo) {
                icon.setPreferredSize(new Dimension(56, 22));
            } else {
                icon.setPreferredSize(new Dimension(16, 22));
            }
            JTextArea oNhapLieu = taoVanBan(dong.get(i));
            hang.add(icon, BorderLayout.WEST);
            hang.add(oNhapLieu, BorderLayout.CENTER);
            khungDanhSach.add(hang);
            if (i < dong.size() - 1) khungDanhSach.add(Box.createVerticalStrut(8));
        }
        the.add(khungDanhSach, BorderLayout.CENTER);
        return the;
    }

    private static BoGocPanel taoThePhimTat(String tieuDe, List<String[]> phimTat) {
        BoGocPanel the = new BoGocPanel(12, Color.WHITE, true);
        the.setLayout(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel nhanTieuDe = new JLabel(tieuDe);
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTieuDe.setForeground(MauUngDung.NAU);
        the.add(nhanTieuDe, BorderLayout.NORTH);
        JPanel luoi = new JPanel(new GridLayout(0, 1, 0, 8));
        luoi.setOpaque(false);
        for (String[] cap : phimTat) {
            JPanel item = new JPanel(new BorderLayout(12, 0));
            item.setOpaque(false);
            JLabel phim = new JLabel(cap[0]);
            phim.setOpaque(true);
            phim.setBackground(MauUngDung.XAM_NEN_NHAT);
            phim.setForeground(MauUngDung.VAN_BAN_TOI);
            phim.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            phim.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(MauUngDung.VIEN),
                            new EmptyBorder(2, 7, 2, 7)));
            JTextArea moTa = taoVanBan(cap[1]);
            moTa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            moTa.setForeground(MauUngDung.VAN_BAN_XAM);
            item.add(phim, BorderLayout.WEST);
            item.add(moTa, BorderLayout.CENTER);
            luoi.add(item);
        }
        the.add(luoi, BorderLayout.CENTER);
        return the;
    }

    public static NoiDungHuongDan xayDungNoiDung(
            Component cha, String tieuDeGoc, String noiDungGoc) {
        return SoTayTroGiup.lay(cha, tieuDeGoc, noiDungGoc);
    }

    private static JTextArea taoVanBan(String noiDung) {
        JTextArea vanBan = new JTextArea(noiDung);
        vanBan.setEditable(false);
        vanBan.setOpaque(false);
        vanBan.setLineWrap(true);
        vanBan.setWrapStyleWord(true);
        vanBan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        vanBan.setForeground(MauUngDung.VAN_BAN_TOI);
        vanBan.setBorder(null);
        return vanBan;
    }

    private static final class KhungCuonTroGiup extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(600, 360);
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle vung, int huong, int chieu) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle vung, int huong, int chieu) {
            return Math.max(16, vung.height - 16);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
