package com.petstation.gui.components.common;

import java.awt.*;
import java.time.LocalDate;

import javax.swing.*;

public class BoChonKhoangNgay extends JPanel {
    private JComboBox<String> cboGiaTriCoSanHopChon;

    private boolean dangDatKy;

    private BoChonNgay pnlNgayBatDauBoChon;

    private BoChonNgay pnlNgayKetThucBoChon;

    private JPanel pnlTuyChinhNgayVungHienThi;

    public BoChonKhoangNgay() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
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
        cboGiaTriCoSanHopChon.addActionListener(suKien -> xuLyGiaTriCoSanThayDoi());

        pnlTuyChinhNgayVungHienThi = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlTuyChinhNgayVungHienThi.setOpaque(false);
        pnlTuyChinhNgayVungHienThi.add(new JLabel("Từ ngày:"));
        pnlNgayBatDauBoChon = new BoChonNgay();
        pnlTuyChinhNgayVungHienThi.add(pnlNgayBatDauBoChon);

        pnlTuyChinhNgayVungHienThi.add(new JLabel("Đến ngày:"));
        pnlNgayKetThucBoChon = new BoChonNgay();
        pnlTuyChinhNgayVungHienThi.add(pnlNgayKetThucBoChon);

        pnlTuyChinhNgayVungHienThi.setVisible(true);

        JPanel pnlTrenVungHienThi = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlTrenVungHienThi.setOpaque(false);
        pnlTrenVungHienThi.add(new JLabel("Khoảng thời gian:"));
        pnlTrenVungHienThi.add(cboGiaTriCoSanHopChon);

        add(pnlTrenVungHienThi, BorderLayout.NORTH);
        add(pnlTuyChinhNgayVungHienThi, BorderLayout.CENTER);

        pnlNgayBatDauBoChon.addPropertyChangeListener("date", e -> chonTuyChinh());
        pnlNgayKetThucBoChon.addPropertyChangeListener("date", e -> chonTuyChinh());

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
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Hôm qua":
                tu = den = homNay.minusDays(1);
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "7 ngày qua":
                tu = homNay.minusDays(6);
                den = homNay;
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tuần này":
                tu = homNay.minusDays(homNay.getDayOfWeek().getValue() - 1);
                den = homNay;
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tuần trước":

                LocalDate cuoiCungTuanBatDau =
                        homNay.minusDays(homNay.getDayOfWeek().getValue() + 6);
                tu = cuoiCungTuanBatDau;
                den = cuoiCungTuanBatDau.plusDays(6);
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tháng này":
                tu = homNay.withDayOfMonth(1);
                den = homNay;
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Tháng trước":

                LocalDate cuoiCungThang = homNay.minusMonths(1);
                tu = cuoiCungThang.withDayOfMonth(1);
                den = cuoiCungThang.withDayOfMonth(cuoiCungThang.lengthOfMonth());
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "30 ngày qua":
                tu = homNay.minusDays(29);
                den = homNay;
                pnlTuyChinhNgayVungHienThi.setVisible(true);
                break;
            case "Năm nay":
                tu = homNay.withDayOfYear(1);
                den = homNay;
                break;
            case "Tùy chọn...":
                pnlTuyChinhNgayVungHienThi.setVisible(true);

                return;
        }

        if (tu != null && den != null) {
            dangDatKy = true;
            pnlNgayBatDauBoChon.datNgayDuocChon(tu);
            pnlNgayKetThucBoChon.datNgayDuocChon(den);
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
        return pnlNgayBatDauBoChon.layNgayDuocChon();
    }

    public LocalDate layNgayKetThuc() {
        return pnlNgayKetThucBoChon.layNgayDuocChon();
    }

    public java.sql.Date layNgayBatDauSQL() {
        return pnlNgayBatDauBoChon.layNgaySQL();
    }

    public java.sql.Date layNgayKetThucSQL() {
        return pnlNgayKetThucBoChon.layNgaySQL();
    }

    public void datNgayBatDau(LocalDate ngay) {
        cboGiaTriCoSanHopChon.setSelectedItem("Tùy chọn...");
        pnlNgayBatDauBoChon.datNgayDuocChon(ngay);
    }

    public void datNgayKetThuc(LocalDate ngay) {
        cboGiaTriCoSanHopChon.setSelectedItem("Tùy chọn...");
        pnlNgayKetThucBoChon.datNgayDuocChon(ngay);
    }

    public void datLai() {
        cboGiaTriCoSanHopChon.setSelectedIndex(0);
        xuLyGiaTriCoSanThayDoi();
    }

    public void chonKy(String tenKy) {
        cboGiaTriCoSanHopChon.setSelectedItem(tenKy);
    }
}
