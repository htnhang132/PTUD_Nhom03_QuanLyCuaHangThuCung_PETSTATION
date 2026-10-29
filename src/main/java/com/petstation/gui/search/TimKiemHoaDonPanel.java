package com.petstation.gui.search;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BoChonNgay;
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

/**
 * Cung cấp giao diện Master-Detail Split-Pane cho tra cứu và tìm kiếm hóa đơn. Hỗ trợ bộ lọc đa
 * tiêu chí (ngày tháng, trạng thái, phương thức thanh toán) và xem trước chi tiết hóa đơn chuẩn máy
 * in nhiệt K80 (80mm) ngay bên cạnh.
 */
public final class TimKiemHoaDonPanel extends NghiepVuBanHangPanel {
    private final HoaDonDocPanel pnlHoaDon = new HoaDonDocPanel();
    private final JLabel lblBillStatus = new JLabel("Chọn hóa đơn bên trái để xem trước");
    private final NutDinhDang btnInLai;
    private final NutDinhDang btnXuatHtml;

    // Bộ lọc đa tiêu chí
    private final BoChonNgay txtTuNgay;
    private final BoChonNgay txtDenNgay;
    private final JComboBox<String> cboPhuongThuc;
    private final JComboBox<String> cboTrangThai;

    private GioHang.BienLai bienLaiDangChon = null;

    public TimKiemHoaDonPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOA_DON, banHang, dieuHuong);

        // 1. Tạo thanh bộ lọc đa tiêu chí
        JPanel pnlBoLocNangCao = new JPanel();
        pnlBoLocNangCao.setLayout(new BoxLayout(pnlBoLocNangCao, BoxLayout.Y_AXIS));
        pnlBoLocNangCao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                        new EmptyBorder(6, 4, 8, 4)));

        JPanel pnlHangLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));

        LocalDate macDinhTuNgay = LocalDate.now().minusDays(30);
        LocalDate homNay = LocalDate.now();

        txtTuNgay = new BoChonNgay(macDinhTuNgay);
        txtDenNgay = new BoChonNgay(homNay);

        pnlHangLoc.add(new JLabel("Từ ngày:"));
        pnlHangLoc.add(txtTuNgay);
        pnlHangLoc.add(new JLabel("Đến ngày:"));
        pnlHangLoc.add(txtDenNgay);

        cboPhuongThuc =
                new JComboBox<>(
                        new String[] {
                            "TẤT CẢ",
                            "Tiền mặt (CASH)",
                            "Chuyển khoản (TRANSFER)",
                            "Thẻ (CARD)",
                            "Kết hợp (SPLIT)"
                        });
        pnlHangLoc.add(new JLabel("Phương thức:"));
        pnlHangLoc.add(cboPhuongThuc);

        cboTrangThai =
                new JComboBox<>(
                        new String[] {
                            "TẤT CẢ",
                            "Đã thanh toán (PAID)",
                            "Đã hủy (CANCELLED/VOIDED)",
                            "Đã hoàn (REFUNDED)"
                        });
        pnlHangLoc.add(new JLabel("Trạng thái:"));
        pnlHangLoc.add(cboTrangThai);

        pnlBoLocNangCao.add(pnlHangLoc);

        // Chèn thanh bộ lọc lên trên bảng
        BorderLayout rootLayout = (BorderLayout) getLayout();
        Component northComp = rootLayout.getLayoutComponent(BorderLayout.NORTH);
        if (northComp instanceof JPanel pnlNorth) {
            pnlNorth.add(pnlBoLocNangCao, BorderLayout.SOUTH);
        }

        // 2. Tạo Preview Panel biên nhận K80 bên phải
        JPanel pnlRightDetail = new JPanel(new BorderLayout(0, 8));
        pnlRightDetail.setPreferredSize(new Dimension(380, 0));
        pnlRightDetail.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)),
                        new EmptyBorder(0, 10, 0, 4)));

        JPanel pnlTitleDetail = new JPanel(new BorderLayout());
        lblBillStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBillStatus.setForeground(new Color(30, 41, 59));
        pnlTitleDetail.add(lblBillStatus, BorderLayout.WEST);
        pnlRightDetail.add(pnlTitleDetail, BorderLayout.NORTH);

        JScrollPane scrHoaDon =
                new JScrollPane(
                        pnlHoaDon,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrHoaDon.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        pnlRightDetail.add(scrHoaDon, BorderLayout.CENTER);

        // Các nút thao tác trên hóa đơn bên phải
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

        // 3. Tái cấu trúc Center thành JSplitPane
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

        // 4. Lắng nghe chọn dòng để nạp dữ liệu vào hóa đơn K80
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
        txtTuNgay.datNgayDuocChon(LocalDate.now().minusDays(30));
        txtDenNgay.datNgayDuocChon(LocalDate.now());
        cboPhuongThuc.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        txtTimKiem.setText("");
        apDungBoLocNangCao();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (txtTuNgay != null) apDungBoLocNangCao();
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
            lblBillStatus.setText("Hóa đơn: " + soHoaDon + " (" + trangThai + ")");

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

        // 1. Lọc từ khóa tìm kiếm chung
        String tuKhoa = txtTimKiem.getText().trim();
        if (!tuKhoa.isEmpty() && !"Tìm trong danh sách...".equals(tuKhoa)) {
            cacBoLoc.add(RowFilter.regexFilter("(?iu)" + java.util.regex.Pattern.quote(tuKhoa)));
        }

        // 2. Lọc phương thức thanh toán (cột 6 trong model)
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        if (phuongThuc != null && !"TẤT CẢ".equals(phuongThuc)) {
            String maPt =
                    phuongThuc.contains("CASH")
                            ? "CASH|TIEN_MAT|Tiền mặt"
                            : phuongThuc.contains("TRANSFER")
                                    ? "TRANSFER|CHUYEN_KHOAN|Chuyển khoản"
                                    : phuongThuc.contains("CARD")
                                            ? "CARD|THE|Thẻ"
                                            : "SPLIT|MIXED|KET_HOP|Kết hợp";
            cacBoLoc.add(RowFilter.regexFilter("(?iu)^(" + maPt + ")$", 6));
        }

        // 3. Lọc trạng thái (cột 7 trong model)
        String trangThai = (String) cboTrangThai.getSelectedItem();
        if (trangThai != null && !"TẤT CẢ".equals(trangThai)) {
            String maTt =
                    trangThai.contains("PAID")
                            ? "PAID|Đã thanh toán"
                            : trangThai.contains("CANCELLED")
                                    ? "CANCELLED|VOIDED|Đã hủy"
                                    : "REFUNDED|RETURNED|Đã hoàn";
            cacBoLoc.add(RowFilter.regexFilter("(?iu)^(" + maTt + ")$", 7));
        }

        // 4. Lọc ngày tháng (cột 4 trong model: thanh_toan_luc)
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
