package com.petstation.gui.search;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BoChonNgay;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.gui.components.common.HoaDonDocPanel;
import com.petstation.gui.processing.sales.InHoaDonPanel;
import com.petstation.gui.processing.sales.NghiepVuBanHangPanel;
import com.petstation.model.ChiTietBienLai;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.BanHangService;

import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class TimKiemHoaDonPanel extends NghiepVuBanHangPanel {
    private final HoaDonDocPanel pnlHoaDon = new HoaDonDocPanel();
    private final JLabel lblBillStatus = new JLabel("Chọn hóa đơn bên trái để xem trước");
    private final NutDinhDang btnInLai;
    private final NutDinhDang btnXuatHtml;

    private final BoChonNgay txtTuNgay;
    private final BoChonNgay txtDenNgay;
    private final JComboBox<String> cboPhuongThuc;
    private final JComboBox<String> cboTrangThai;

    private GioHang.BienLai bienLaiDangChon = null;

    public TimKiemHoaDonPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOA_DON, banHang, dieuHuong);

        LocalDate macDinhTuNgay = LocalDate.now().minusDays(60);
        LocalDate homNay = LocalDate.now();

        txtTuNgay = new BoChonNgay(macDinhTuNgay);
        txtDenNgay = new BoChonNgay(homNay);

        cboPhuongThuc =
                new JComboBox<>(
                        new String[] {
                            "TẤT CẢ",
                            "Tiền mặt",
                            "Chuyển khoản",
                            "Thẻ",
                            "Điểm tích lũy",
                            "Kết hợp"
                        });

        cboTrangThai =
                new JComboBox<>(
                        new String[] {
                            "TẤT CẢ",
                            "Đã thanh toán",
                            "Đã hủy",
                            "Đã hoàn"
                        });

        if (pnlCacThaoTac instanceof com.petstation.gui.components.ThanhDanhSach thanh) {
            Component btnLamMoi = null;
            Component btnXuat = null;
            for (Component c : thanh.getComponents()) {
                if (c instanceof JButton b) {
                    if ("Làm mới".equals(b.getText())) btnLamMoi = b;
                    else if ("Xuất Excel".equals(b.getText())) btnXuat = b;
                }
            }
            thanh.removeAll();
            txtTimKiem.setPreferredSize(new Dimension(190, 32));
            txtTuNgay.setPreferredSize(new Dimension(130, 32));
            txtDenNgay.setPreferredSize(new Dimension(130, 32));
            thanh.add(txtTimKiem);
            thanh.themBoLoc("Từ ngày:", txtTuNgay);
            thanh.themBoLoc("Đến ngày:", txtDenNgay);
            thanh.themBoLoc("Phương thức:", cboPhuongThuc);
            thanh.themBoLoc("Trạng thái:", cboTrangThai);
            if (btnLamMoi != null) thanh.add(btnLamMoi);
            if (btnXuat != null) thanh.add(btnXuat);
        }

        JPanel pnlRightDetail = new JPanel(new BorderLayout(0, 8));
        pnlRightDetail.setPreferredSize(new Dimension(340, 0));
        pnlRightDetail.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)),
                        new EmptyBorder(0, 10, 0, 4)));

        JPanel pnlTitleDetail = new JPanel(new BorderLayout());
        lblBillStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBillStatus.setForeground(new Color(30, 41, 59));
        pnlTitleDetail.add(lblBillStatus, BorderLayout.CENTER);
        pnlRightDetail.add(pnlTitleDetail, BorderLayout.NORTH);

        JScrollPane scrHoaDon =
                new JScrollPane(
                        pnlHoaDon,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrHoaDon.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        pnlRightDetail.add(scrHoaDon, BorderLayout.CENTER);

        JPanel pnlActionBill = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlActionBill.setBorder(new EmptyBorder(6, 0, 4, 0));

        btnInLai = new NutDinhDang("In lại hóa đơn (K80 / A5)", NutDinhDang.KieuNut.CHINH);
        btnXuatHtml = new NutDinhDang("Xuất file HTML / PDF", NutDinhDang.KieuNut.PHU);

        btnInLai.setEnabled(false);
        btnXuatHtml.setEnabled(false);

        btnInLai.addActionListener(e -> xuLyInHoaDon());
        btnXuatHtml.addActionListener(e -> xuLyXuatHoaDon());

        pnlActionBill.add(btnInLai);
        pnlActionBill.add(btnXuatHtml);
        pnlRightDetail.add(pnlActionBill, BorderLayout.SOUTH);

        BorderLayout rootLayout = (BorderLayout) getLayout();
        Component centerComp = rootLayout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            remove(centerComp);
            JSplitPane splitPane =
                    new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerComp, pnlRightDetail);
            splitPane.setResizeWeight(1.0);
            splitPane.setContinuousLayout(true);
            splitPane.setBorder(null);
            add(splitPane, BorderLayout.CENTER);
        }

        tblBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting() || tblBang.getSelectedRow() < 0) return;
                            capNhatXemTruocHoaDon();
                        });

        txtTuNgay.addPropertyChangeListener("date", e -> apDungBoLocNangCao());
        txtDenNgay.addPropertyChangeListener("date", e -> apDungBoLocNangCao());
        cboPhuongThuc.addActionListener(e -> apDungBoLocNangCao());
        cboTrangThai.addActionListener(e -> apDungBoLocNangCao());
        lienKetBoLoc(txtTimKiem, tblBang);
        apDungBoLocNangCao();
    }

    @Override
    protected void lienKetBoLoc(JTextField timKiem, JTable bang) {
        if (Boolean.TRUE.equals(timKiem.getClientProperty("locHoaDon"))) return;
        timKiem.putClientProperty("locHoaDon", true);
        timKiem.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                apDungBoLocNangCao();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                apDungBoLocNangCao();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                apDungBoLocNangCao();
                            }
                        });
    }

    @Override
    protected void datLaiBoLoc() {
        txtTuNgay.datNgayDuocChon(LocalDate.now().minusDays(60));
        txtDenNgay.datNgayDuocChon(LocalDate.now());
        cboPhuongThuc.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        txtTimKiem.setText("");
        apDungBoLocNangCao();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (txtTuNgay != null) apDungBoLocNangCao();
        if (tblBang.getSelectedRow() < 0 && tblBang.getRowCount() > 0) {
            tblBang.setRowSelectionInterval(0, 0);
        }
    }

    private void capNhatXemTruocHoaDon() {
        int row = tblBang.getSelectedRow();
        if (row < 0) {
            btnInLai.setEnabled(false);
            btnXuatHtml.setEnabled(false);
            lblBillStatus.setText("Chưa chọn hóa đơn");
            return;
        }

        try {
            String soHoaDon = tblBang.getValueAt(row, 0).toString();
            String trangThai =
                    tblBang.getValueAt(row, 6) == null ? "" : tblBang.getValueAt(row, 6).toString();
            bienLaiDangChon = BanHangService.layBienLai(soHoaDon);
            ChiTietBienLai chiTiet = BanHangService.layChiTietBienLai(soHoaDon);

            pnlHoaDon.datDuLieu(chiTiet);
            lblBillStatus.setText("Hóa đơn: " + soHoaDon + " (" + DinhDangBang.tenTrangThai(trangThai) + ")");

            btnInLai.setEnabled(true);
            btnXuatHtml.setEnabled(true);
        } catch (Exception ex) {
            lblBillStatus.setText("Lỗi nạp hóa đơn: " + ex.getMessage());
        }
    }

    private void apDungBoLocNangCao() {
        if (txtTuNgay == null || cboTrangThai == null) return;
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter =
                (TableRowSorter<DefaultTableModel>) tblBang.getRowSorter();
        if (sorter == null) return;

        List<RowFilter<DefaultTableModel, Object>> cacBoLoc = new ArrayList<>();

        String tuKhoa = txtTimKiem.getText().trim();
        if (!tuKhoa.isEmpty() && !"Tìm trong danh sách...".equals(tuKhoa)) {
            cacBoLoc.add(RowFilter.regexFilter("(?iu)" + java.util.regex.Pattern.quote(tuKhoa)));
        }

        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        if (phuongThuc != null && !"TẤT CẢ".equals(phuongThuc)) {
            String maPt =
                    switch (phuongThuc) {
                        case "Tiền mặt" -> "TIEN_MAT|Tiền mặt";
                        case "Chuyển khoản" -> "CHUYEN_KHOAN|Chuyển khoản.*";
                        case "Thẻ" -> "THE_NGAN_HANG|THE|Thẻ";
                        case "Điểm tích lũy" -> "DIEM_TICH_LUY|Điểm tích lũy";
                        default -> "KET_HOP|Kết hợp";
                    };
            cacBoLoc.add(RowFilter.regexFilter("(?iu)^(" + maPt + ")$", 6));
        }

        String trangThai = (String) cboTrangThai.getSelectedItem();
        if (trangThai != null && !"TẤT CẢ".equals(trangThai)) {
            String maTt =
                    switch (trangThai) {
                        case "Đã thanh toán" -> "DA_THANH_TOAN|Đã thanh toán";
                        case "Đã hủy" -> "DA_HUY|Đã hủy";
                        default -> "DA_HOAN|Trả hàng|Đã hoàn";
                    };
            cacBoLoc.add(RowFilter.regexFilter("(?iu)^(" + maTt + ")$", 7));
        }

        LocalDate tuNgay = txtTuNgay.layNgayDuocChon();
        String tuNgayStr = tuNgay == null ? "" : tuNgay.toString();
        LocalDate denNgay = txtDenNgay.layNgayDuocChon();
        String denNgayStr = denNgay == null ? "" : denNgay.toString();
        if (!tuNgayStr.isEmpty() || !denNgayStr.isEmpty()) {
            cacBoLoc.add(
                    new RowFilter<DefaultTableModel, Object>() {
                        @Override
                        public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                            Object val = entry.getValue(4);
                            if (val == null) return false;
                            String s =
                                    val.toString()
                                            .substring(0, Math.min(10, val.toString().length()));
                            try {
                                LocalDate d = LocalDate.parse(s);
                                if (!tuNgayStr.isEmpty()) {
                                    LocalDate tu = LocalDate.parse(tuNgayStr);
                                    if (d.isBefore(tu)) return false;
                                }
                                if (!denNgayStr.isEmpty()) {
                                    LocalDate den = LocalDate.parse(denNgayStr);
                                    if (d.isAfter(den)) return false;
                                }
                                return true;
                            } catch (Exception ex) {
                                return true;
                            }
                        }
                    });
        }

        if (cacBoLoc.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(cacBoLoc));
        }
    }

    private void xuLyInHoaDon() {
        if (bienLaiDangChon == null) return;
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(pnlHoaDon);
        if (job.printDialog()) {
            try {
                job.print();
                HopThoai.hienThiThanhCong(
                        this, "Đã gửi lệnh in hóa đơn " + bienLaiDangChon.soHoaDon());
            } catch (PrinterException ex) {
                HopThoai.hienThiLoi(this, "Lỗi khi in hóa đơn: " + ex.getMessage());
            }
        }
    }

    private void xuLyXuatHoaDon() {
        if (bienLaiDangChon == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("HoaDon_" + bienLaiDangChon.soHoaDon() + ".html"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                String html = InHoaDonPanel.hienThiHTML(bienLaiDangChon);
                writer.write(html);
                HopThoai.hienThiThanhCong(
                        this, "Đã xuất hóa đơn ra file:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                HopThoai.hienThiLoi(this, "Lỗi khi ghi file: " + ex.getMessage());
            }
        }
    }
}
