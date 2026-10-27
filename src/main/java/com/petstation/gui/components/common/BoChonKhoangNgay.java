package com.petstation.gui.components.common;

import java.awt.*;
import java.time.LocalDate;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho khoảng ngày bộ chọn.
 *
 * <p>Component chọn khoảng thời gian với preset options Dùng cho các màn hình lọc theo ngày (Audit
 * Log, Hóa đơn, Báo cáo)
 */
public class BoChonKhoangNgay extends JPanel {
    /** Giá trị có sẵn hộp chọn. */
    private JComboBox<String> cboGiaTriCoSanHopChon;

    private boolean dangDatKy;

    /** Ngày bắt đầu bộ chọn. */
    private BoChonNgay pnlNgayBatDauBoChon;

    /** Ngày kết thúc bộ chọn. */
    private BoChonNgay pnlNgayKetThucBoChon;

    /** Tùy chỉnh ngày vùng hiển thị. */
    private JPanel pnlTuyChinhNgayVungHienThi;

    public BoChonKhoangNgay() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setOpaque(false);

        // Các khoảng ngày có sẵn
        // Các giá trị có sẵn.
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

        // Khung chọn khoảng ngày tùy chỉnh
        pnlTuyChinhNgayVungHienThi = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlTuyChinhNgayVungHienThi.setOpaque(false);
        pnlTuyChinhNgayVungHienThi.add(new JLabel("Từ ngày:"));
        pnlNgayBatDauBoChon = new BoChonNgay();
        pnlTuyChinhNgayVungHienThi.add(pnlNgayBatDauBoChon);

        pnlTuyChinhNgayVungHienThi.add(new JLabel("Đến ngày:"));
        pnlNgayKetThucBoChon = new BoChonNgay();
        pnlTuyChinhNgayVungHienThi.add(pnlNgayKetThucBoChon);

        pnlTuyChinhNgayVungHienThi.setVisible(true);

        // Bố cục
        // Trên vùng hiển thị.
        JPanel pnlTrenVungHienThi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTrenVungHienThi.setOpaque(false);
        pnlTrenVungHienThi.add(new JLabel("Khoảng thời gian:"));
        pnlTrenVungHienThi.add(cboGiaTriCoSanHopChon);

        add(pnlTrenVungHienThi, BorderLayout.NORTH);
        add(pnlTuyChinhNgayVungHienThi, BorderLayout.CENTER);

        pnlNgayBatDauBoChon.addPropertyChangeListener("date", e -> chonTuyChinh());
        pnlNgayKetThucBoChon.addPropertyChangeListener("date", e -> chonTuyChinh());

        // Chọn sẵn khoảng ngày hôm nay
        cboGiaTriCoSanHopChon.setSelectedIndex(0);
        xuLyGiaTriCoSanThayDoi();
    }

    private void xuLyGiaTriCoSanThayDoi() {
        // Đã chọn.
        String daChon = (String) cboGiaTriCoSanHopChon.getSelectedItem();
        // Hôm nay.
        LocalDate homNay = LocalDate.now();
        // Từ.
        LocalDate tu = null;
        // Đến.
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
                // Cuối cùng tuần bắt đầu.
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
                // Cuối cùng tháng.
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
                // Để trống ngày bắt đầu và kết thúc cho người dùng tự chọn
                return;
        }

        // Gán ngày tương ứng với lựa chọn có sẵn
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

    // Truy xuất thuộc tính
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
