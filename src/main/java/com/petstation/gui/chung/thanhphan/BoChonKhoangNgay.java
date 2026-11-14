package com.petstation.gui.chung.thanhphan;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BoChonKhoangNgay extends JPanel {
    private JComboBox<String> cboGiaTriCoSanHopChon;

    private boolean dangDatKy;

    private BoChonNgay khungNgayBatDauBoChon;

    private BoChonNgay khungNgayKetThucBoChon;

    private JPanel khungTuyChinhNgayVungHienThi;

    public BoChonKhoangNgay() {
        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        String[] cacGiaTriCoSan = {
            "Hôm nay",
            "Hôm qua",
            "7 ngày qua",
            "Tuần này",
            "Tuần trước",
            "Tháng này",
            "Tháng trước",
            "30 ngày qua",
            "Năm nay",
            "Tùy chọn..."
        };
        cboGiaTriCoSanHopChon = new JComboBox<>(cacGiaTriCoSan);
        cboGiaTriCoSanHopChon.setPreferredSize(new Dimension(140, 34));
        cboGiaTriCoSanHopChon.addActionListener(suKien -> xuLyGiaTriCoSanThayDoi());
        khungTuyChinhNgayVungHienThi = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        khungTuyChinhNgayVungHienThi.setOpaque(false);
        khungTuyChinhNgayVungHienThi.add(new JLabel("Từ ngày:"));
        khungNgayBatDauBoChon = new BoChonNgay();
        khungNgayBatDauBoChon.setPreferredSize(new Dimension(135, 34));
        khungTuyChinhNgayVungHienThi.add(khungNgayBatDauBoChon);
        khungTuyChinhNgayVungHienThi.add(new JLabel("Đến ngày:"));
        khungNgayKetThucBoChon = new BoChonNgay();
        khungNgayKetThucBoChon.setPreferredSize(new Dimension(135, 34));
        khungTuyChinhNgayVungHienThi.add(khungNgayKetThucBoChon);
        khungTuyChinhNgayVungHienThi.setVisible(true);
        JPanel khungTrenVungHienThi = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        khungTrenVungHienThi.setOpaque(false);
        khungTrenVungHienThi.add(new JLabel("Khoảng thời gian:"));
        khungTrenVungHienThi.add(cboGiaTriCoSanHopChon);
        add(khungTrenVungHienThi, BorderLayout.NORTH);
        add(khungTuyChinhNgayVungHienThi, BorderLayout.CENTER);
        khungNgayBatDauBoChon.addPropertyChangeListener("date", e -> chonTuyChinh());
        khungNgayKetThucBoChon.addPropertyChangeListener("date", e -> chonTuyChinh());
        cboGiaTriCoSanHopChon.setSelectedIndex(0);
        xuLyGiaTriCoSanThayDoi();
    }

    private void xuLyGiaTriCoSanThayDoi() {
        String daChon = (String) cboGiaTriCoSanHopChon.getSelectedItem();
        LocalDate homNay = LocalDate.now();
        LocalDate tu = null;
        LocalDate den = null;
        switch (daChon) {
            case "Hôm nay":
                tu = den = homNay;
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Hôm qua":
                tu = den = homNay.minusDays(1);
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "7 ngày qua":
                tu = homNay.minusDays(6);
                den = homNay;
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tuần này":
                tu = homNay.minusDays(homNay.getDayOfWeek().getValue() - 1);
                den = homNay;
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tuần trước":
                LocalDate cuoiCungTuanBatDau =
                        homNay.minusDays(homNay.getDayOfWeek().getValue() + 6);
                tu = cuoiCungTuanBatDau;
                den = cuoiCungTuanBatDau.plusDays(6);
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tháng này":
                tu = homNay.withDayOfMonth(1);
                den = homNay;
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tháng trước":
                LocalDate cuoiCungThang = homNay.minusMonths(1);
                tu = cuoiCungThang.withDayOfMonth(1);
                den = cuoiCungThang.withDayOfMonth(cuoiCungThang.lengthOfMonth());
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "30 ngày qua":
                tu = homNay.minusDays(29);
                den = homNay;
                khungTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Năm nay":
                tu = homNay.withDayOfYear(1);
                den = homNay;
                break;
            case "Tùy chọn...":
                khungTuyChinhNgayVungHienThi.setVisible(true);
                return;
        }
        if (tu != null && den != null) {
            dangDatKy = true;
            khungNgayBatDauBoChon.datNgayDuocChon(tu);
            khungNgayKetThucBoChon.datNgayDuocChon(den);
            dangDatKy = false;
        }
        firePropertyChange("dateRange", null, new LocalDate[] {tu, den});
    }

    private void chonTuyChinh() {
        if (!dangDatKy) {
            cboGiaTriCoSanHopChon.setSelectedItem("Tùy chọn...");
            firePropertyChange(
                    "dateRange", null, new LocalDate[] {layNgayBatDau(), layNgayKetThuc()});
        }
    }

    public LocalDate layNgayBatDau() {
        return khungNgayBatDauBoChon.layNgayDuocChon();
    }

    public LocalDate layNgayKetThuc() {
        return khungNgayKetThucBoChon.layNgayDuocChon();
    }

    public void chonKy(String tenKy) {
        cboGiaTriCoSanHopChon.setSelectedItem(tenKy);
    }
}
