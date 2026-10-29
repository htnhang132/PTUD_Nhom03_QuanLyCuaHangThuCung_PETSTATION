package com.petstation.gui.components;

import com.petstation.gui.navigation.QuyTrinhManHinh;
import com.petstation.model.MaManHinh;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho thanh quy trình.
 *
 * <p>Thanh quy trình: hiển thị bước hiện tại của luồng nhiều bước (bán hàng, Spa, Hotel); bấm để
 * chuyển bước.
 */
public final class ThanhQuyTrinh extends JPanel {
    /** Tên. */
    private final JLabel lblTen = new JLabel();

    /** Chỉ báo. */
    private final ChiBaoBuoc chiBao = new ChiBaoBuoc(java.util.List.of(""));

    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Vùng chứa. */
    private final JPanel pnlVungChua = new JPanel(new BorderLayout());

    /** Quy trình. */
    private QuyTrinhManHinh.QuyTrinh quyTrinh;

    /** Hiện tại màn hình. */
    private MaManHinh hienTaiManHinh;

    /** Trước. */
    private final NutDinhDang btnTruoc =
            new NutDinhDang("‹ Bước trước", NutDinhDang.KieuNut.TRONG_SUOT);

    /** Tiếp theo. */
    private final NutDinhDang btnTiepTheo =
            new NutDinhDang("Bước tiếp ›", NutDinhDang.KieuNut.CHINH);

    /** Điều hướng. */
    private final JPanel pnlDieuHuong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));

    public ThanhQuyTrinh(Consumer<MaManHinh> boDieuHuong) {
        super(new BorderLayout(12, 0));
        this.boDieuHuong = boDieuHuong;
        setBackground(Color.WHITE);
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN),
                        new EmptyBorder(0, 14, 0, 14)));
        lblTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblTen.setForeground(MauUngDung.NAU);
        lblTen.setPreferredSize(new Dimension(110, 40));
        add(lblTen, BorderLayout.WEST);
        pnlVungChua.setOpaque(false);
        add(pnlVungChua, BorderLayout.CENTER);
        pnlDieuHuong.setOpaque(false);
        pnlDieuHuong.add(btnTruoc);
        pnlDieuHuong.add(btnTiepTheo);
        add(pnlDieuHuong, BorderLayout.EAST);
        btnTruoc.addActionListener(
                suKien -> {
                    if (quyTrinh != null)
                        quyTrinh.layTruocDo(hienTaiManHinh).ifPresent(boDieuHuong);
                });
        btnTiepTheo.addActionListener(
                suKien -> {
                    if (quyTrinh == null) return;
                    if (quyTrinh.kiemTraCuoiCung(hienTaiManHinh))
                        boDieuHuong.accept(MaManHinh.THANH_TOAN);
                    else quyTrinh.layTiepTheo(hienTaiManHinh).ifPresent(boDieuHuong);
                });
        setPreferredSize(new Dimension(0, 44));
        setVisible(false);
    }

    public void hienThi(MaManHinh manHinh) {
        // Tìm thấy.
        Optional<QuyTrinhManHinh.QuyTrinh> timThay = QuyTrinhManHinh.tim(manHinh);
        if (timThay.isEmpty()) {
            quyTrinh = null;
            setVisible(false);
            return;
        }
        QuyTrinhManHinh.QuyTrinh truongDangXet = timThay.get();
        if (truongDangXet != quyTrinh) {
            quyTrinh = truongDangXet;
            pnlVungChua.removeAll();
            // Mới.
            ChiBaoBuoc moi = new ChiBaoBuoc(truongDangXet.cacNhan());
            moi.datKhiBam(chiSo -> boDieuHuong.accept(truongDangXet.cacBuoc().get(chiSo)));
            pnlVungChua.add(moi, BorderLayout.CENTER);
            lblTen.setText(truongDangXet.ten());
            pnlVungChua.putClientProperty("indicator", moi);
        }
        // Hiện tại.
        ChiBaoBuoc hienTai = (ChiBaoBuoc) pnlVungChua.getClientProperty("indicator");
        hienTai.datHienTai(truongDangXet.layChiSoCua(manHinh));
        hienTaiManHinh = manHinh;
        pnlDieuHuong.setVisible(truongDangXet.tuanTu());
        btnTruoc.setVisible(truongDangXet.layTruocDo(manHinh).isPresent());
        btnTiepTheo.setText(
                truongDangXet.kiemTraCuoiCung(manHinh) ? "Thanh toán ›" : "Bước tiếp ▶");
        setVisible(true);
        revalidate();
        repaint();
    }
}
