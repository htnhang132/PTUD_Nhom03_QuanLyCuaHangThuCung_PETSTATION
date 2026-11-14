package com.petstation.gui.banhang;

import com.petstation.dto.ChiTietBienLai;
import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.pheduyet.PheDuyetService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Tìm kiếm hóa đơn (Mã: TIM_KIEM_HOA_DON)
// Luồng xử lý: GUI -> BanHangService -> HoaDonDAOImpl -> SQL (hoa_don, nhan_vien, khach_hang)
// Mô tả: Tra cứu danh sách lịch sử hóa đơn bán hàng theo mã, số điện thoại, ngày lập
// Phân hệ: banhang
public final class TimKiemHoaDonPanel extends BangNghiepVuPanel {
    private final HoaDonDocPanel khungHoaDon = new HoaDonDocPanel();
    private final JLabel nhanTrangThaiHoaDon = new JLabel("Chọn hóa đơn bên trái để xem trước");
    private NutDinhDang btnInLai;
    private NutDinhDang btnXuatHtml;
    private NutDinhDang btnYeuCauPheDuyet;

    private final BoChonNgay oTuNgay;
    private final BoChonNgay oDenNgay;
    private final JComboBox<String> cboPhuongThuc;
    private final JComboBox<String> cboTrangThai;

    private GioHang.BienLai bienLaiDangChon = null;
    private int luotXemTruoc = 0;

    private static final class DuLieuXemTruoc {
        final GioHang.BienLai bienLai;
        final ChiTietBienLai chiTiet;

        DuLieuXemTruoc(GioHang.BienLai bienLai, ChiTietBienLai chiTiet) {
            this.bienLai = bienLai;
            this.chiTiet = chiTiet;
        }
    }

    public TimKiemHoaDonPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOA_DON, banHang, dieuHuong);
        oTuNgay = new BoChonNgay(LocalDate.now().minusDays(60));
        oDenNgay = new BoChonNgay(LocalDate.now());
        cboPhuongThuc =
                new JComboBox<>(
                        new String[] {
                            "TẤT CẢ", "Tiền mặt", "Chuyển khoản", "Thẻ", "Điểm tích lũy"
                        });
        cboTrangThai =
                new JComboBox<>(new String[] {"TẤT CẢ", "Đã thanh toán", "Đã hủy", "Đã hoàn"});
        cauHinhThanhBoLocHoaDon();
        ganKhungXemTruocHoaDon(taoKhungXemTruocHoaDon());
        dangKySuKienTimKiemHoaDon();
    }

    private void cauHinhThanhBoLocHoaDon() {
        if (!(layKhungThaoTac() instanceof ThanhDanhSach thanh)) return;
        Component btnLamMoi = null;
        Component btnXuat = null;
        for (Component c : thanh.getComponents()) {
            if (c instanceof JButton b) {
                if (b.getText().startsWith("Làm mới")) btnLamMoi = b;
                else if ("Xuất Excel".equals(b.getText())) btnXuat = b;
            }
        }
        thanh.removeAll();
        layOTimKiem().setPreferredSize(new Dimension(190, 32));
        oTuNgay.setPreferredSize(new Dimension(130, 32));
        oDenNgay.setPreferredSize(new Dimension(130, 32));
        thanh.add(layOTimKiem());
        thanh.themBoLoc("Từ ngày:", oTuNgay);
        thanh.themBoLoc("Đến ngày:", oDenNgay);
        thanh.themBoLoc("Phương thức:", cboPhuongThuc);
        thanh.themBoLoc("Trạng thái:", cboTrangThai);
        if (btnLamMoi != null) thanh.add(btnLamMoi);
        if (btnXuat != null) thanh.add(btnXuat);
    }

    private JPanel taoKhungXemTruocHoaDon() {
        JPanel khungChiTietPhai = new JPanel(new BorderLayout(0, 8));
        khungChiTietPhai.setPreferredSize(new Dimension(340, 0));
        khungChiTietPhai.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 0, 0, MauUngDung.XAM_VIEN_NHE),
                        new EmptyBorder(0, 10, 0, 4)));
        JPanel khungTieuDeChiTietPhai = new JPanel(new BorderLayout());
        nhanTrangThaiHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nhanTrangThaiHoaDon.setForeground(MauUngDung.XAM_TIEU_DE);
        khungTieuDeChiTietPhai.add(nhanTrangThaiHoaDon, BorderLayout.CENTER);
        khungChiTietPhai.add(khungTieuDeChiTietPhai, BorderLayout.NORTH);
        JScrollPane scrHoaDon =
                new JScrollPane(
                        khungHoaDon,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrHoaDon.setBorder(BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_TRUNG));
        khungChiTietPhai.add(scrHoaDon, BorderLayout.CENTER);
        JPanel khungThaoTacHoaDon = new JPanel(new GridLayout(3, 1, 0, 6));
        khungThaoTacHoaDon.setBorder(new EmptyBorder(6, 0, 4, 0));
        btnInLai =
                taoNutXemTruocHoaDon(
                        "In lại hóa đơn (K80 / A5)",
                        NutDinhDang.KieuNut.CHINH,
                        e -> xuLyInHoaDon());
        btnXuatHtml =
                taoNutXemTruocHoaDon(
                        "Xuất file HTML / PDF", NutDinhDang.KieuNut.PHU, e -> xuLyXuatHoaDon());
        btnYeuCauPheDuyet =
                taoNutXemTruocHoaDon(
                        "Yêu cầu hủy / sửa HĐ",
                        NutDinhDang.KieuNut.NGUY_HIEM,
                        e -> xuLyYeuCauHuySua());
        khungThaoTacHoaDon.add(btnInLai);
        khungThaoTacHoaDon.add(btnXuatHtml);
        khungThaoTacHoaDon.add(btnYeuCauPheDuyet);
        khungChiTietPhai.add(khungThaoTacHoaDon, BorderLayout.SOUTH);
        return khungChiTietPhai;
    }

    private static NutDinhDang taoNutXemTruocHoaDon(
            String nhan, NutDinhDang.KieuNut kieu, ActionListener hanhDong) {
        NutDinhDang nut = new NutDinhDang(nhan, kieu);
        nut.setEnabled(false);
        nut.addActionListener(hanhDong);
        return nut;
    }

    private void ganKhungXemTruocHoaDon(JPanel khungChiTietPhai) {
        BorderLayout rootLayout = (BorderLayout) getLayout();
        Component centerComp = rootLayout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp == null) return;
        remove(centerComp);
        JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerComp, khungChiTietPhai);
        splitPane.setResizeWeight(1.0);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    private void dangKySuKienTimKiemHoaDon() {
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && layBangDuLieu().getSelectedRow() >= 0) {
                                capNhatXemTruocHoaDon();
                            }
                        });
        oTuNgay.addPropertyChangeListener("date", e -> apDungBoLocNangCao());
        oDenNgay.addPropertyChangeListener("date", e -> apDungBoLocNangCao());
        cboPhuongThuc.addActionListener(e -> apDungBoLocNangCao());
        cboTrangThai.addActionListener(e -> apDungBoLocNangCao());
        lienKetBoLoc(layOTimKiem(), layBangDuLieu());
        apDungBoLocNangCao();
    }

    @Override
    protected void lienKetBoLoc(JTextField timKiem, JTable bang) {
        if (Boolean.TRUE.equals(timKiem.getClientProperty("locHoaDon"))) return;
        timKiem.putClientProperty("locHoaDon", true);
        themBoLangNgheTaiLieu(timKiem, this::apDungBoLocNangCao);
    }

    @Override
    protected void datLaiBoLoc() {
        oTuNgay.datNgayDuocChon(LocalDate.now().minusDays(60));
        oDenNgay.datNgayDuocChon(LocalDate.now());
        cboPhuongThuc.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        layOTimKiem().setText("");
        apDungBoLocNangCao();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (oTuNgay != null) apDungBoLocNangCao();
        if (layBangDuLieu().getSelectedRow() < 0 && layBangDuLieu().getRowCount() > 0) {
            layBangDuLieu().setRowSelectionInterval(0, 0);
        }
    }

    private void capNhatXemTruocHoaDon() {
        int row = layBangDuLieu().getSelectedRow();
        if (row < 0) {
            ++luotXemTruoc;
            bienLaiDangChon = null;
            khungHoaDon.datDuLieu(null);
            btnInLai.setEnabled(false);
            btnXuatHtml.setEnabled(false);
            btnYeuCauPheDuyet.setEnabled(false);
            nhanTrangThaiHoaDon.setText("Chưa chọn hóa đơn");
            return;
        }
        String soHoaDon = layBangDuLieu().getValueAt(row, 0).toString();
        String trangThai =
                layBangDuLieu().getValueAt(row, 6) == null
                        ? ""
                        : layBangDuLieu().getValueAt(row, 6).toString();
        int luot = ++luotXemTruoc;
        btnInLai.setEnabled(false);
        btnXuatHtml.setEnabled(false);
        btnYeuCauPheDuyet.setEnabled(false);
        nhanTrangThaiHoaDon.setText("Đang tải hóa đơn " + soHoaDon + "...");
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotXemTruoc,
                () -> {
                    BanHangService banHangService = new BanHangService();
                    return new DuLieuXemTruoc(
                            banHangService.layBienLai(soHoaDon),
                            banHangService.layChiTietBienLai(soHoaDon));
                },
                duLieu -> {
                    bienLaiDangChon = duLieu.bienLai;
                    khungHoaDon.datDuLieu(duLieu.chiTiet);
                    nhanTrangThaiHoaDon.setText(
                            "Hóa đơn: "
                                    + soHoaDon
                                    + " ("
                                    + DinhDangBang.tenTrangThai(trangThai)
                                    + ")");
                    btnInLai.setEnabled(true);
                    btnXuatHtml.setEnabled(true);
                    btnYeuCauPheDuyet.setEnabled(true);
                },
                ex -> nhanTrangThaiHoaDon.setText("Lỗi nạp hóa đơn: " + ex.getMessage()));
    }

    private void xuLyYeuCauHuySua() {
        if (bienLaiDangChon == null) return;
        String soHoaDon = bienLaiDangChon.getSoHoaDon();
        String[] luaChon = {"Hủy hóa đơn", "Sửa ghi chú hóa đơn"};
        int loai =
                HopThoai.tuyChon(
                        this,
                        "Chọn loại yêu cầu gửi Cửa hàng trưởng phê duyệt cho hóa đơn "
                                + soHoaDon
                                + ":",
                        "Yêu cầu phê duyệt hóa đơn",
                        luaChon,
                        true);
        if (loai < 0) return;
        boolean laHuy = (loai == 0);
        String tieuDeLyDo = laHuy ? "Nhập lý do hủy hóa đơn:" : "Nhập nội dung/lý do sửa:";
        String lyDo = HopThoai.nhapChuoi(this, tieuDeLyDo, "Lý do yêu cầu");
        if (lyDo == null || lyDo.trim().isEmpty()) return;
        String lyDoChuan = lyDo.trim();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () ->
                        new PheDuyetService()
                                .layHoaDonYeuCau(
                                        soHoaDon,
                                        laHuy,
                                        lyDoChuan,
                                        laHuy ? "Hủy hóa đơn" : lyDoChuan),
                () ->
                        HopThoai.hienThiThanhCong(
                                this,
                                "Đã gửi yêu cầu "
                                        + (laHuy ? "hủy" : "sửa")
                                        + " hóa đơn "
                                        + soHoaDon
                                        + " tới Trung tâm phê duyệt."),
                ex -> HopThoai.hienThiLoi(this, "Không thể gửi yêu cầu: " + ex.getMessage()));
    }

    private void apDungBoLocNangCao() {
        if (oTuNgay == null || cboTrangThai == null) return;
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter =
                (TableRowSorter<DefaultTableModel>) layBangDuLieu().getRowSorter();
        if (sorter == null) return;
        List<RowFilter<DefaultTableModel, Object>> cacBoLoc = new ArrayList<>();
        String tuKhoa = layOTimKiem().getText().trim();
        if (!tuKhoa.isEmpty() && !"Tìm trong danh sách...".equals(tuKhoa)) {
            cacBoLoc.add(RowFilter.regexFilter("(?iu)" + Pattern.quote(tuKhoa)));
        }
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        if (phuongThuc != null && !"TẤT CẢ".equals(phuongThuc)) {
            String maPt =
                    switch (phuongThuc) {
                        case "Tiền mặt" -> "TIEN_MAT|Tiền mặt";
                        case "Chuyển khoản" -> "CHUYEN_KHOAN|Chuyển khoản.*";
                        case "Thẻ" -> "THE_NGAN_HANG|THE|Thẻ";
                        default -> "DIEM_TICH_LUY|Điểm tích lũy";
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
        LocalDate tuNgay = oTuNgay.layNgayDuocChon();
        String tuNgayStr = tuNgay == null ? "" : tuNgay.toString();
        LocalDate denNgay = oDenNgay.layNgayDuocChon();
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
        job.setPrintable(khungHoaDon);
        if (job.printDialog()) {
            try {
                job.print();
                HopThoai.hienThiThanhCong(
                        this, "Đã gửi lệnh in hóa đơn " + bienLaiDangChon.getSoHoaDon());
            } catch (PrinterException ex) {
                HopThoai.hienThiLoi(this, "Lỗi khi in hóa đơn: " + ex.getMessage());
            }
        }
    }

    private void xuLyXuatHoaDon() {
        if (bienLaiDangChon == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("HoaDon_" + bienLaiDangChon.getSoHoaDon() + ".html"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            GioHang.BienLai bienLaiXuat = bienLaiDangChon;
            TacVuNen.chayCoPhien(
                    this,
                    () -> true,
                    () -> {
                        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                            String html = InHoaDonPanel.hienThiHTML(bienLaiXuat);
                            writer.write(html);
                            return file.getAbsolutePath();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex.getMessage(), ex);
                        }
                    },
                    duongDan ->
                            HopThoai.hienThiThanhCong(
                                    this, "Đã xuất hóa đơn ra file:\n" + duongDan),
                    ex -> HopThoai.hienThiLoi(this, "Lỗi khi ghi file: " + ex.getMessage()));
        }
    }
}
