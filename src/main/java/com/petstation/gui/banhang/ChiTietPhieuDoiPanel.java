package com.petstation.gui.banhang;

import com.petstation.dto.ChiTietXemTruocPhieuDoi;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.banhang.PhieuDoiTra;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/** Read-only exchange detail with wrapping item names and separate financial totals. */
public final class ChiTietPhieuDoiPanel extends JPanel implements Scrollable {
    public ChiTietPhieuDoiPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(MauUngDung.THE_NEN);
        hienThi(null);
    }

    public void hienThi(ChiTietXemTruocPhieuDoi duLieu) {
        removeAll();
        if (duLieu == null || duLieu.getPhieu() == null) {
            them(nhan("Chọn một phiếu để xem thông tin đổi trả.", false));
        } else {
            PhieuDoiTra phieu = duLieu.getPhieu();
            JPanel dau = new JPanel(new BorderLayout(8, 0));
            dau.setOpaque(false);
            dau.add(nhan(phieu.getMaPhieu(), true), BorderLayout.WEST);
            JLabel trangThai = nhan(DinhDangBang.tenTrangThai(phieu.getTrangThai()), false);
            trangThai.setForeground(
                    "HOAN_TAT".equals(phieu.getTrangThai())
                            ? MauUngDung.XANH_LA
                            : MauUngDung.VAN_BAN_XAM);
            dau.add(trangThai, BorderLayout.EAST);
            them(dau);
            them(taoThongTin(phieu));
            them(nhan("Hàng khách trả lại", true));
            if (duLieu.getHangTra().isEmpty()) them(nhan("Không có hàng trả.", false));
            duLieu.getHangTra()
                    .forEach(
                            c -> {
                                String ten =
                                        c.getChiTietHoaDon() != null
                                                        && c.getChiTietHoaDon().getSanPham() != null
                                                ? c.getChiTietHoaDon().getSanPham().getTenSanPham()
                                                : "Sản phẩm";
                                String lo =
                                        c.getLoHang() == null
                                                ? ""
                                                : "  ·  Lô " + c.getLoHang().getMaLo();
                                them(
                                        taoHang(
                                                ten,
                                                "Số lượng: " + c.getSoLuong() + lo,
                                                c.getTienHoan()));
                            });
            them(nhan("Phụ kiện thay thế", true));
            if (duLieu.getHangThayThe().isEmpty()) them(nhan("Chưa có hóa đơn thay thế.", false));
            duLieu.getHangThayThe()
                    .forEach(
                            c ->
                                    them(
                                            taoHang(
                                                    c.getTenHang(),
                                                    "Số lượng: "
                                                            + c.getSoLuong()
                                                            + "  ·  Đơn giá: "
                                                            + DinhDang.dinhDang(c.getDonGia()),
                                                    c.getDonGia() * c.getSoLuong())));
            them(taoTongTien(phieu));
            if (phieu.getLyDo() != null && !phieu.getLyDo().isBlank()) {
                them(nhan("Lý do đổi trả", true));
                them(vanBan(phieu.getLyDo()));
            }
        }
        add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }

    private JPanel taoThongTin(PhieuDoiTra phieu) {
        JPanel thongTin = new JPanel(new GridLayout(0, 1, 0, 10));
        thongTin.setOpaque(false);
        thongTin.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
        thongTin.add(
                taoMuc(
                        "Hóa đơn gốc",
                        phieu.getHoaDonGoc() == null ? "—" : phieu.getHoaDonGoc().getMaHoaDon()));
        thongTin.add(
                taoMuc(
                        "Hóa đơn thay thế",
                        phieu.getHoaDonThayThe() == null
                                ? "—"
                                : phieu.getHoaDonThayThe().getMaHoaDon()));
        thongTin.add(
                taoMuc(
                        "Nhân viên lập",
                        phieu.getNguoiLap() == null ? "—" : phieu.getNguoiLap().getHoTen()));
        return thongTin;
    }

    private static JPanel taoMuc(String nhan, String giaTri) {
        JPanel muc = new JPanel(new BorderLayout(0, 3));
        muc.setOpaque(false);
        muc.add(nhan(nhan, false), BorderLayout.NORTH);
        muc.add(nhan(giaTri, true), BorderLayout.CENTER);
        return muc;
    }

    private static JPanel taoHang(String ten, String moTa, long tien) {
        JPanel hang = new JPanel(new BorderLayout(0, 8));
        hang.setBackground(MauUngDung.NEN_KEM);
        hang.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_NHE),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JTextArea tenHang = vanBan(ten);
        tenHang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        hang.add(tenHang, BorderLayout.NORTH);
        hang.add(vanBan(moTa), BorderLayout.CENTER);
        JLabel thanhTien = nhan(DinhDang.dinhDang(tien), true);
        thanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
        hang.add(thanhTien, BorderLayout.SOUTH);
        return hang;
    }

    private static JPanel taoTongTien(PhieuDoiTra phieu) {
        JPanel tong = new JPanel(new GridLayout(0, 1, 0, 10));
        tong.setBackground(MauUngDung.NEN_KEM);
        tong.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
        tong.add(taoDongTien("Giá trị hàng đổi", phieu.getGiaTriDuocDoi()));
        String nhan =
                phieu.getChenhLech() > 0
                        ? "Khách trả thêm"
                        : phieu.getChenhLech() < 0 ? "Hoàn lại khách" : "Không chênh lệch";
        tong.add(taoDongTien(nhan, Math.abs(phieu.getChenhLech())));
        return tong;
    }

    private static JPanel taoDongTien(String ten, long tien) {
        JPanel dong = new JPanel(new BorderLayout(8, 0));
        dong.setOpaque(false);
        dong.add(nhan(ten, false), BorderLayout.WEST);
        JLabel giaTri = nhan(DinhDang.dinhDang(tien), true);
        giaTri.setForeground(MauUngDung.DO_SAU);
        dong.add(giaTri, BorderLayout.EAST);
        return dong;
    }

    private void them(JComponent component) {
        component.setAlignmentX(LEFT_ALIGNMENT);
        component.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
        add(component);
        add(Box.createVerticalStrut(12));
    }

    @Override
    public void doLayout() {
        capNhatChieuCao();
        super.doLayout();
    }

    @Override
    public Dimension getPreferredSize() {
        capNhatChieuCao();
        return super.getPreferredSize();
    }

    private void capNhatChieuCao() {
        int rong = getWidth() > 0 ? getWidth() : 360;
        for (Component component : getComponents()) {
            if (!(component instanceof JComponent muc) || component instanceof Box.Filler) continue;
            doVanBan(muc, rong);
            Dimension toiDa = new Dimension(Integer.MAX_VALUE, muc.getPreferredSize().height);
            if (!toiDa.equals(muc.getMaximumSize())) muc.setMaximumSize(toiDa);
        }
    }

    private static void doVanBan(Component component, int rong) {
        if (component instanceof JTextArea text) {
            text.setSize(Math.max(80, rong), Short.MAX_VALUE);
        } else if (component instanceof Container container) {
            int benTrong = rong - container.getInsets().left - container.getInsets().right;
            for (Component child : container.getComponents()) doVanBan(child, benTrong);
        }
    }

    private static JLabel nhan(String chu, boolean dam) {
        JLabel label = new JLabel(chu == null || chu.isBlank() ? "—" : chu);
        label.setFont(MauUngDung.taoPhongChu(dam ? Font.BOLD : Font.PLAIN, dam ? 13 : 12));
        label.setForeground(dam ? MauUngDung.VAN_BAN_TOI : MauUngDung.VAN_BAN_XAM);
        return label;
    }

    private static JTextArea vanBan(String chu) {
        JTextArea text = new JTextArea(chu == null ? "" : chu);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        text.setForeground(MauUngDung.VAN_BAN_TOI);
        text.setBorder(null);
        return text;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(360, 600);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        return Math.max(16, r.height - 32);
    }
}
