package com.petstation.gui.chung;

import com.petstation.model.chung.MaManHinh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public final class ThanhQuyTrinh extends JPanel {
    private final JLabel nhanTen = new JLabel();

    private final Consumer<MaManHinh> boDieuHuong;

    private final JPanel khungVungChua = new JPanel(new BorderLayout());

    private QuyTrinhManHinh.QuyTrinh quyTrinh;

    private MaManHinh hienTaiManHinh;

    private final NutDinhDang btnTruoc =
            new NutDinhDang("Bước trước", NutDinhDang.KieuNut.TRONG_SUOT);

    private final NutDinhDang btnTiepTheo = new NutDinhDang("Bước tiếp", NutDinhDang.KieuNut.CHINH);

    private final JPanel khungDieuHuong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));

    public ThanhQuyTrinh(Consumer<MaManHinh> boDieuHuong) {
        super(new BorderLayout(12, 0));
        this.boDieuHuong = boDieuHuong;
        setBackground(Color.WHITE);
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN),
                        new EmptyBorder(0, 14, 0, 14)));
        nhanTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanTen.setForeground(MauUngDung.NAU);
        nhanTen.setPreferredSize(new Dimension(110, 40));
        add(nhanTen, BorderLayout.WEST);
        khungVungChua.setOpaque(false);
        add(khungVungChua, BorderLayout.CENTER);
        khungDieuHuong.setOpaque(false);
        khungDieuHuong.add(btnTruoc);
        khungDieuHuong.add(btnTiepTheo);
        add(khungDieuHuong, BorderLayout.EAST);
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
        Optional<QuyTrinhManHinh.QuyTrinh> timThay = QuyTrinhManHinh.tim(manHinh);
        if (timThay.isEmpty()) {
            quyTrinh = null;
            setVisible(false);
            return;
        }
        QuyTrinhManHinh.QuyTrinh truongDangXet = timThay.get();
        if (truongDangXet != quyTrinh) {
            quyTrinh = truongDangXet;
            khungVungChua.removeAll();
            ChiBaoBuoc moi = new ChiBaoBuoc(truongDangXet.cacNhan());
            moi.datKhiBam(
                    truongDangXet.tuanTu()
                            ? chiSo -> boDieuHuong.accept(truongDangXet.cacBuoc().get(chiSo))
                            : null);
            khungVungChua.add(moi, BorderLayout.CENTER);
            nhanTen.setText(truongDangXet.ten());
            khungVungChua.putClientProperty("indicator", moi);
        }
        ChiBaoBuoc hienTai = (ChiBaoBuoc) khungVungChua.getClientProperty("indicator");
        hienTai.datHienTai(truongDangXet.layChiSoCua(manHinh));
        hienTaiManHinh = manHinh;
        khungDieuHuong.setVisible(truongDangXet.tuanTu());
        btnTruoc.setVisible(truongDangXet.layTruocDo(manHinh).isPresent());
        btnTiepTheo.setText(truongDangXet.kiemTraCuoiCung(manHinh) ? "Thanh toán" : "Bước tiếp");
        setVisible(true);
        revalidate();
        repaint();
    }
}
