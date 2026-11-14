package com.petstation.gui.banhang;

import com.petstation.dto.ChiTietBienLai;
import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.BoGocPanel;
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
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

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
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private ChiTietBienLai chiTietDangChon = null;
    private int luotXemTruoc = 0;

    private boolean dangDatLaiBoLoc;

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
        Component btnXuat = null;
        for (Component c : thanh.getComponents()) {
            if (c instanceof JButton b && "Xuất Excel".equals(b.getText())) {
                btnXuat = b;
            }
        }
        thanh.removeAll();
        layOTimKiem().setPreferredSize(new Dimension(190, 34));
        oTuNgay.setPreferredSize(new Dimension(135, 34));
        oDenNgay.setPreferredSize(new Dimension(135, 34));
        thanh.add(layOTimKiem());
        thanh.themBoLoc("Từ ngày:", oTuNgay);
        thanh.themBoLoc("Đến ngày:", oDenNgay);
        thanh.themBoLoc("Phương thức:", cboPhuongThuc);
        thanh.themBoLoc("Trạng thái:", cboTrangThai);
        if (btnXuat != null) thanh.add(btnXuat);
    }

    private JPanel taoKhungXemTruocHoaDon() {
        BoGocPanel khungChiTietPhai = new BoGocPanel(12, Color.WHITE, true);
        khungChiTietPhai.setLayout(new BorderLayout(0, 8));
        khungChiTietPhai.setPreferredSize(new Dimension(350, 0));
        khungChiTietPhai.setBorder(new EmptyBorder(12, 14, 12, 14));
        JPanel khungTieuDeChiTietPhai = new JPanel(new BorderLayout());
        khungTieuDeChiTietPhai.setOpaque(false);
        nhanTrangThaiHoaDon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanTrangThaiHoaDon.setForeground(MauUngDung.NAU);
        khungTieuDeChiTietPhai.add(nhanTrangThaiHoaDon, BorderLayout.CENTER);
        khungChiTietPhai.add(khungTieuDeChiTietPhai, BorderLayout.NORTH);
        JScrollPane scrHoaDon =
                new JScrollPane(
                        khungHoaDon,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrHoaDon.setBorder(BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_NHE));
        khungChiTietPhai.add(scrHoaDon, BorderLayout.CENTER);
        JPanel khungThaoTacHoaDon = new JPanel(new GridLayout(3, 1, 0, 8));
        khungThaoTacHoaDon.setOpaque(false);
        khungThaoTacHoaDon.setBorder(new EmptyBorder(6, 0, 0, 0));
        btnInLai =
                taoNutXemTruocHoaDon(
                        "In lại hóa đơn (K80 / A5)",
                        NutDinhDang.KieuNut.CHINH,
                        e -> inHoaDonDangChon());
        btnXuatHtml =
                taoNutXemTruocHoaDon(
                        "Xuất file HTML", NutDinhDang.KieuNut.PHU, e -> xuLyXuatHoaDon());
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
        splitPane.setDividerSize(8);
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
                            if (!e.getValueIsAdjusting()) {
                                capNhatXemTruocHoaDon();
                            }
                        });
        layBangDuLieu()
                .addMouseListener(
                        new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2
                                        && layBangDuLieu().getSelectedRow() >= 0
                                        && chiTietDangChon != null) {
                                    inHoaDonDangChon();
                                }
                            }
                        });
        layOTimKiem()
                .addActionListener(
                        e -> {
                            if (!dangDatLaiBoLoc) {
                                apDungBoLocNangCao();
                                if (layBangDuLieu().getSelectedRow() < 0
                                        && layBangDuLieu().getRowCount() > 0) {
                                    layBangDuLieu().setRowSelectionInterval(0, 0);
                                }
                            }
                        });
        oTuNgay.addPropertyChangeListener(
                "date",
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocNangCao();
                });
        oDenNgay.addPropertyChangeListener(
                "date",
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocNangCao();
                });
        cboPhuongThuc.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocNangCao();
                });
        cboTrangThai.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocNangCao();
                });
        lienKetBoLoc(layOTimKiem(), layBangDuLieu());
        apDungBoLocNangCao();
    }

    @Override
    protected void lienKetBoLoc(JTextField timKiem, JTable bang) {
        if (Boolean.TRUE.equals(timKiem.getClientProperty("locHoaDon"))) return;
        timKiem.putClientProperty("locHoaDon", true);
        themBoLangNgheTaiLieu(
                timKiem,
                () -> {
                    if (!dangDatLaiBoLoc) apDungBoLocNangCao();
                });
    }

    @Override
    protected void datLaiBoLoc() {
        dangDatLaiBoLoc = true;
        try {
            oTuNgay.datNgayDuocChon(LocalDate.now().minusDays(60));
            oDenNgay.datNgayDuocChon(LocalDate.now());
            cboPhuongThuc.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
            layOTimKiem().setText("");
        } finally {
            dangDatLaiBoLoc = false;
        }
        apDungBoLocNangCao();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (oTuNgay != null) apDungBoLocNangCao();
        if (layBangDuLieu().getSelectedRow() < 0 && layBangDuLieu().getRowCount() > 0) {
            layBangDuLieu().setRowSelectionInterval(0, 0);
        } else if (layBangDuLieu().getSelectedRow() < 0) {
            xoaXemTruoc();
        }
    }

    private void xoaXemTruoc() {
        ++luotXemTruoc;
        bienLaiDangChon = null;
        chiTietDangChon = null;
        khungHoaDon.datDuLieu(null);
        btnInLai.setEnabled(false);
        btnXuatHtml.setEnabled(false);
        btnYeuCauPheDuyet.setEnabled(false);
        nhanTrangThaiHoaDon.setText("");
    }

    private void capNhatXemTruocHoaDon() {
        int row = layBangDuLieu().getSelectedRow();
        if (row < 0) {
            xoaXemTruoc();
            return;
        }
        int dongMoHinh = layBangDuLieu().convertRowIndexToModel(row);
        String soHoaDon = String.valueOf(layMoHinh().getValueAt(dongMoHinh, 0));
        Object trangThaiObj = layMoHinh().getValueAt(dongMoHinh, 7);
        String trangThai = trangThaiObj == null ? "" : trangThaiObj.toString();
        int luot = ++luotXemTruoc;
        bienLaiDangChon = null;
        chiTietDangChon = null;
        khungHoaDon.datDuLieu(null);
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
                    chiTietDangChon = duLieu.chiTiet;
                    khungHoaDon.datDuLieu(duLieu.chiTiet);
                    String trangThaiHienThi = DinhDangBang.tenTrangThai(trangThai);
                    nhanTrangThaiHoaDon.setText(
                            "Hóa đơn: " + soHoaDon + " (" + trangThaiHienThi + ")");
                    btnInLai.setEnabled(duLieu.chiTiet != null);
                    btnXuatHtml.setEnabled(duLieu.bienLai != null);
                    btnYeuCauPheDuyet.setEnabled(!daKhoaPheDuyet(trangThai, trangThaiHienThi));
                },
                ex -> nhanTrangThaiHoaDon.setText("Lỗi nạp hóa đơn: " + ex.getMessage()));
    }

    private static boolean daKhoaPheDuyet(String trangThai, String trangThaiHienThi) {
        return "DA_HUY".equalsIgnoreCase(trangThai)
                || "Đã hủy".equalsIgnoreCase(trangThai)
                || "Đã hủy".equalsIgnoreCase(trangThaiHienThi)
                || "DA_HOAN".equalsIgnoreCase(trangThai)
                || "Đã hoàn".equalsIgnoreCase(trangThai)
                || "Đã hoàn".equalsIgnoreCase(trangThaiHienThi)
                || "Trả hàng".equalsIgnoreCase(trangThaiHienThi);
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
            cacBoLoc.add(taoBoLocTuKhoa(tuKhoa));
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
        LocalDate denNgay = oDenNgay.layNgayDuocChon();
        if (tuNgay != null || denNgay != null) {
            cacBoLoc.add(taoBoLocNgay(tuNgay, denNgay));
        }
        sorter.setRowFilter(cacBoLoc.isEmpty() ? null : RowFilter.andFilter(cacBoLoc));
        if (layBangDuLieu().getSelectedRow() < 0) {
            xoaXemTruoc();
        }
    }

    private static RowFilter<DefaultTableModel, Object> taoBoLocTuKhoa(String tuKhoa) {
        String tuKhoaThuong = tuKhoa.toLowerCase(Locale.ROOT);
        return new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                for (int col = 1; col < entry.getValueCount(); col++) {
                    Object val = entry.getValue(col);
                    if (val == null) continue;
                    String text = dinhDangGiaTriTimKiem(col, val).toLowerCase(Locale.ROOT);
                    if (text.contains(tuKhoaThuong)
                            || val.toString().toLowerCase(Locale.ROOT).contains(tuKhoaThuong)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private static String dinhDangGiaTriTimKiem(int col, Object val) {
        if (col == 4) {
            if (val instanceof LocalDateTime ldt) return DinhDang.ngayGio(ldt);
            if (val instanceof LocalDate ld) return DinhDang.ngay(ld);
            if (val instanceof Date d) {
                return DinhDang.ngayGio(
                        LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault()));
            }
        }
        if (col == 7) return DinhDangBang.tenTrangThai(val.toString());
        return val.toString();
    }

    private static RowFilter<DefaultTableModel, Object> taoBoLocNgay(
            LocalDate tuNgay, LocalDate denNgay) {
        return new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                LocalDate ngay = docNgay(entry.getValue(4));
                if (ngay == null) return false;
                if (tuNgay != null && ngay.isBefore(tuNgay)) return false;
                return denNgay == null || !ngay.isAfter(denNgay);
            }
        };
    }

    private static LocalDate docNgay(Object val) {
        if (val == null) return null;
        if (val instanceof LocalDate d) return d;
        if (val instanceof LocalDateTime dt) return dt.toLocalDate();
        if (val instanceof Date d) {
            return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String chuoi = val.toString().trim();
        if (chuoi.isEmpty()) return null;
        try {
            if (chuoi.length() >= 10 && chuoi.charAt(4) == '-') {
                return LocalDate.parse(chuoi.substring(0, 10));
            }
            if (chuoi.length() >= 16 && chuoi.charAt(2) == '/') {
                return LocalDateTime.parse(chuoi.substring(0, 16), DinhDang.NGAY_GIO).toLocalDate();
            }
            if (chuoi.length() >= 10 && chuoi.charAt(2) == '/') {
                return LocalDate.parse(chuoi.substring(0, 10), DinhDang.NGAY);
            }
        } catch (RuntimeException loi) {
            return null;
        }
        return null;
    }

    @Override
    public void xuLyIn() {
        if (chiTietDangChon != null && bienLaiDangChon != null) {
            inHoaDonDangChon();
            return;
        }
        super.xuLyIn();
    }

    private void inHoaDonDangChon() {
        if (bienLaiDangChon == null || chiTietDangChon == null) return;
        String soHoaDon = bienLaiDangChon.getSoHoaDon();
        HoaDonDocPanel banIn = new HoaDonDocPanel();
        banIn.datDuLieu(chiTietDangChon);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(banIn);
        if (job.printDialog()) {
            TacVuNen.chay(
                    () -> {
                        job.print();
                        return null;
                    },
                    v -> HopThoai.hienThiThanhCong(this, "Đã gửi lệnh in hóa đơn " + soHoaDon),
                    ex ->
                            HopThoai.hienThiLoi(
                                    this, "Lỗi khi in hóa đơn: " + TacVuNen.thongDiepLoi(ex)));
        }
    }

    private void xuLyXuatHoaDon() {
        if (bienLaiDangChon == null) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp HTML (*.html)", "html"));
        chooser.setSelectedFile(new File("HoaDon_" + bienLaiDangChon.getSoHoaDon() + ".html"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File chon = chooser.getSelectedFile();
        File file =
                chon.getName().toLowerCase(Locale.ROOT).endsWith(".html")
                        ? chon
                        : new File(chon.getParentFile(), chon.getName() + ".html");
        if (file.exists()
                && !HopThoai.xacNhanDongY(
                        this,
                        "Tệp \"" + file.getName() + "\" đã tồn tại. Bạn có muốn ghi đè không?",
                        "Xác nhận ghi đè")) {
            return;
        }
        GioHang.BienLai bienLaiXuat = bienLaiDangChon;
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> {
                    try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                        writer.write(InHoaDonPanel.hienThiHTML(bienLaiXuat));
                        return file.getAbsolutePath();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                },
                duongDan ->
                        HopThoai.hienThiThanhCong(this, "Đã xuất hóa đơn ra file:\n" + duongDan),
                ex -> HopThoai.hienThiLoi(this, "Lỗi khi ghi file: " + ex.getMessage()));
    }
}
