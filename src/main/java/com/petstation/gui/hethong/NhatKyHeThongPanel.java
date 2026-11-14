package com.petstation.gui.hethong;

import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BoChonKhoangNgay;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

// Màn hình: Nhật ký thao tác hệ thống (Mã: NHAT_KY_HE_THONG)
// Luồng xử lý: GUI -> NhatKyHeThongService -> NhatKyHeThongDAOImpl -> SQL (nhat_ky_he_thong,
// tai_khoan)
// Mô tả: Truy vết toàn bộ nhật ký (Audit Log) các hành vi thao tác, sửa đổi dữ liệu và đăng nhập hệ
// thống
// Phân hệ: hethong
public final class NhatKyHeThongPanel extends CoSoPanel {
    private JTable bangBang;

    private DefaultTableModel moHinhBang;

    private TableRowSorter<DefaultTableModel> boSapXep;

    private BoChonKhoangNgay khungKhoangNgayBoChon;

    private JComboBox<String> cboSuKienKieuHopChon;

    private JComboBox<String> cboKetQuaHopChon;

    private JTextField oTruongNguoiDungTimKiem;

    private JLabel nhanNhanCacChiSo;

    private int luotTai;

    public NhatKyHeThongPanel() {
        setLayout(new BorderLayout());
        JPanel khungNoiDung = new JPanel(new BorderLayout(0, 12));
        khungNoiDung.setOpaque(false);
        khungNoiDung.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel khungTieuDe = taoTieuDe();
        JPanel khungBoLocVungHienThi = taoBoLocVungHienThi();
        JPanel khungDauTrang = taoVungTrongSuot(new BorderLayout(0, 10));
        khungDauTrang.add(khungTieuDe, BorderLayout.NORTH);
        khungDauTrang.add(khungBoLocVungHienThi, BorderLayout.CENTER);
        khungNoiDung.add(khungDauTrang, BorderLayout.NORTH);
        JPanel khungBangVungHienThi = taoBangVungHienThi();
        khungNoiDung.add(khungBangVungHienThi, BorderLayout.CENTER);
        JPanel khungChanTrang = taoChanTrang();
        khungNoiDung.add(khungChanTrang, BorderLayout.SOUTH);
        add(khungNoiDung, BorderLayout.CENTER);
        khungKhoangNgayBoChon.chonKy("7 ngày qua");
        cboSuKienKieuHopChon.addActionListener(e -> apDungCacBoLoc());
        cboKetQuaHopChon.addActionListener(e -> apDungCacBoLoc());
        taiLai();
        NhatKyHeThongService.macDinh().themBoLangNghe(boLangNgheNhatKy);
    }

    private final BoLangNgheSuKien<String> boLangNgheNhatKy =
            thaoTac ->
                    SwingUtilities.invokeLater(
                            () -> {
                                if (isShowing()
                                        && PhienDangNhapService.macDinh().layPhienHienTai() != null)
                                    taiLai();
                            });

    @Override
    public void addNotify() {
        super.addNotify();
        NhatKyHeThongService.macDinh().themBoLangNghe(boLangNgheNhatKy);
    }

    @Override
    public void removeNotify() {
        NhatKyHeThongService.macDinh().xoaBoLangNghe(boLangNgheNhatKy);
        super.removeNotify();
    }

    private JPanel taoTieuDe() {
        JPanel khungTieuDe = new JPanel(new BorderLayout(12, 8));
        khungTieuDe.setOpaque(false);
        JPanel khungTraiMuc = new JPanel();
        khungTraiMuc.setOpaque(false);
        khungTraiMuc.setLayout(new BoxLayout(khungTraiMuc, BoxLayout.Y_AXIS));
        JLabel nhanTieuDe = taoTieuDeMuc("Nhật ký hệ thống");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        khungTraiMuc.add(nhanTieuDe);
        khungTraiMuc.add(Box.createVerticalStrut(5));
        JLabel nhanTieuDePhu = new JLabel("Tra cứu lịch sử thao tác và sự kiện bảo mật");
        nhanTieuDePhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanTieuDePhu.setForeground(MauUngDung.VAN_BAN_XAM);
        khungTraiMuc.add(nhanTieuDePhu);
        khungTraiMuc.add(Box.createVerticalStrut(5));
        nhanNhanCacChiSo = new JLabel("Đang hiển thị: 0 sự kiện");
        nhanNhanCacChiSo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanNhanCacChiSo.setForeground(MauUngDung.VAN_BAN_XAM);
        khungTraiMuc.add(nhanNhanCacChiSo);
        khungTieuDe.add(khungTraiMuc, BorderLayout.WEST);
        JPanel khungPhaiMuc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        khungPhaiMuc.setOpaque(false);
        NutDinhDang btnXuatNut = new NutDinhDang("Xuất CSV", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuatNut.setToolTipText("Xuất kết quả tìm kiếm ra file CSV");
        btnXuatNut.addActionListener(suKien -> xuLyXuat());
        khungPhaiMuc.add(btnXuatNut);
        khungTieuDe.add(khungPhaiMuc, BorderLayout.EAST);
        return khungTieuDe;
    }

    private JPanel taoBoLocVungHienThi() {
        JPanel khungVungHienThi = layThe(new BorderLayout(0, 10));
        JLabel nhanTieuDeBoLoc = new JLabel("Bộ lọc tra cứu");
        nhanTieuDeBoLoc.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        khungVungHienThi.add(nhanTieuDeBoLoc, BorderLayout.NORTH);
        JPanel khungBoLocNoiDung = new JPanel(new GridBagLayout());
        khungBoLocNoiDung.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 4;
        gc.weightx = 1.0;
        khungKhoangNgayBoChon = new BoChonKhoangNgay();
        khungBoLocNoiDung.add(khungKhoangNgayBoChon, gc);
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.weightx = 0.0;
        gc.gridx = 0;
        khungBoLocNoiDung.add(taoTruongLocLoaiSuKien(), gc);
        gc.gridx = 1;
        khungBoLocNoiDung.add(taoTruongLocKetQua(), gc);
        gc.gridx = 2;
        khungBoLocNoiDung.add(taoTruongLocNguoiDung(), gc);
        gc.gridx = 3;
        gc.weightx = 1.0;
        khungBoLocNoiDung.add(taoCumNutLoc(), gc);
        khungVungHienThi.add(khungBoLocNoiDung, BorderLayout.CENTER);
        return khungVungHienThi;
    }

    private JPanel taoTruongLocLoaiSuKien() {
        String[] suKienCacKieu = {
            "Tất cả",
            "LOGIN - Đăng nhập / Đăng xuất",
            "CREATE - Thêm mới",
            "UPDATE - Sửa",
            "DELETE - Xóa / Ngừng hoạt động",
            "APPROVAL - Phê duyệt",
            "INVENTORY - Kho",
            "PAYMENT - Thanh toán / Đổi trả",
            "SYSTEM - Hệ thống"
        };
        cboSuKienKieuHopChon = new JComboBox<>(suKienCacKieu);
        cboSuKienKieuHopChon.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        cboSuKienKieuHopChon.setPreferredSize(new Dimension(210, 34));
        return bocNhanVaTruong("Loại sự kiện:", 105, cboSuKienKieuHopChon);
    }

    private JPanel taoTruongLocKetQua() {
        String[] cacKetQua = {"Tất cả", "Thành công", "Thất bại", "Cảnh báo"};
        cboKetQuaHopChon = new JComboBox<>(cacKetQua);
        cboKetQuaHopChon.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        cboKetQuaHopChon.setPreferredSize(new Dimension(120, 34));
        return bocNhanVaTruong("Kết quả:", 0, cboKetQuaHopChon);
    }

    private JPanel taoTruongLocNguoiDung() {
        oTruongNguoiDungTimKiem = new JTextField();
        oTruongNguoiDungTimKiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        oTruongNguoiDungTimKiem.setPreferredSize(new Dimension(130, 34));
        oTruongNguoiDungTimKiem.setToolTipText("Tìm theo tên người dùng");
        return bocNhanVaTruong("Người dùng:", 0, oTruongNguoiDungTimKiem);
    }

    private static JPanel bocNhanVaTruong(String tieuDe, int chieuRongNhan, JComponent thanhPhan) {
        JPanel khungTam = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        khungTam.setOpaque(false);
        JLabel nhanChu = new JLabel(tieuDe);
        nhanChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        if (chieuRongNhan > 0) nhanChu.setPreferredSize(new Dimension(chieuRongNhan, 34));
        khungTam.add(nhanChu);
        khungTam.add(thanhPhan);
        return khungTam;
    }

    private JPanel taoCumNutLoc() {
        JPanel khungTam = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        khungTam.setOpaque(false);
        khungTam.add(taoNutLoc("Làm mới (F5)", 110, NutDinhDang.KieuNut.TRONG_SUOT, e -> taiLai()));
        khungTam.add(
                taoNutLoc("Xóa bộ lọc", 95, NutDinhDang.KieuNut.TRONG_SUOT, e -> xoaCacBoLoc()));
        khungTam.add(taoNutLoc("Áp dụng", 90, NutDinhDang.KieuNut.CHINH, e -> apDungCacBoLoc()));
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "refreshLog");
        getActionMap()
                .put(
                        "refreshLog",
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                taiLai();
                            }
                        });
        return khungTam;
    }

    private static NutDinhDang taoNutLoc(
            String nhan, int rong, NutDinhDang.KieuNut kieu, ActionListener hanhDong) {
        NutDinhDang nut = new NutDinhDang(nhan, kieu);
        nut.setPreferredSize(new Dimension(rong, 34));
        nut.addActionListener(hanhDong);
        return nut;
    }

    private JPanel taoBangVungHienThi() {
        JPanel khungVungHienThi = layThe(new BorderLayout(0, 10));
        JPanel khungBangTieuDe = new JPanel(new BorderLayout());
        khungBangTieuDe.setOpaque(false);
        khungBangTieuDe.add(taoTieuDeMuc("Kết quả tra cứu"), BorderLayout.WEST);
        JLabel nhanGoiY = new JLabel("Click vào dòng để xem chi tiết");
        nhanGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        khungBangTieuDe.add(nhanGoiY, BorderLayout.EAST);
        khungVungHienThi.add(khungBangTieuDe, BorderLayout.NORTH);
        String[] cacCot = {"Thời gian", "Người dùng", "Hành động", "Loại sự kiện", "Kết quả"};
        moHinhBang =
                new DefaultTableModel(new Object[0][], cacCot) {
                    @Override
                    public boolean isCellEditable(int dong, int cot) {
                        return false;
                    }
                };
        bangBang = new JTable(moHinhBang);
        DinhDangBang.apDung(bangBang);
        bangBang.setRowHeight(38);
        bangBang.setFillsViewportHeight(true);
        boSapXep = new TableRowSorter<>(moHinhBang);
        bangBang.setRowSorter(boSapXep);
        cauHinhCotHienThiNhatKy();
        bangBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (suKien.getClickCount() == 2 && bangBang.getSelectedRow() >= 0) {
                            xuLyHienThiChiTiet(bangBang.getSelectedRow());
                        }
                    }
                });
        JScrollPane scrCuonKhung = new JScrollPane(bangBang);
        scrCuonKhung.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        khungVungHienThi.add(scrCuonKhung, BorderLayout.CENTER);
        return khungVungHienThi;
    }

    private void cauHinhCotHienThiNhatKy() {
        bangBang.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        (b, giaTri, daChon, foc, dong, cot) -> {
                            JLabel nhanChu =
                                    taoNhanBangNhatKy(
                                            giaTri, SwingConstants.LEFT, Font.PLAIN, daChon, dong);
                            nhanChu.setBorder(new EmptyBorder(0, 8, 0, 16));
                            return nhanChu;
                        });
        bangBang.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        (b, giaTri, daChon, foc, dong, cot) -> {
                            JLabel nhanChu =
                                    taoNhanBangNhatKy(
                                            giaTri, SwingConstants.CENTER, Font.BOLD, daChon, dong);
                            if (!daChon) {
                                String s = giaTri == null ? "" : giaTri.toString();
                                if (s.contains("WARNING")
                                        || s.contains("ERROR")
                                        || s.contains("DELETE")) {
                                    nhanChu.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
                                } else if (s.contains("LOGIN") || s.contains("LOGOUT")) {
                                    nhanChu.setForeground(MauUngDung.CHINH);
                                }
                            }
                            return nhanChu;
                        });
        bangBang.getColumnModel().getColumn(4).setCellRenderer(new PillBadgeCellRenderer());
    }

    private JLabel taoNhanBangNhatKy(
            Object giaTri, int canLe, int kieuChu, boolean daChon, int dong) {
        JLabel nhanChu = new JLabel(giaTri == null ? "" : giaTri.toString());
        nhanChu.setOpaque(true);
        nhanChu.setHorizontalAlignment(canLe);
        nhanChu.setFont(MauUngDung.taoPhongChu(kieuChu, 12));
        nhanChu.setBackground(
                daChon
                        ? bangBang.getSelectionBackground()
                        : (dong % 2 == 0 ? Color.WHITE : MauUngDung.DONG_XEN_KE));
        nhanChu.setForeground(daChon ? bangBang.getSelectionForeground() : MauUngDung.VAN_BAN_TOI);
        return nhanChu;
    }

    private JPanel taoChanTrang() {
        JPanel khungChanTrang = new JPanel(new BorderLayout());
        khungChanTrang.setBackground(MauUngDung.CANH_BAO_NEN);
        khungChanTrang.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel nhanGhiChu =
                new JLabel(
                        "Nhật ký được lưu trên SQL Server. Chọn khoảng thời gian và bộ lọc để tra"
                                + " cứu.");
        nhanGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanGhiChu.setForeground(MauUngDung.NAU);
        khungChanTrang.add(nhanGhiChu, BorderLayout.WEST);
        return khungChanTrang;
    }

    private void apDungCacBoLoc() {
        taiLai();
    }

    private void taiLai() {
        LocalDate tu = khungKhoangNgayBoChon.layNgayBatDau();
        LocalDate den = khungKhoangNgayBoChon.layNgayKetThuc();
        if (tu != null && den != null && tu.isAfter(den)) {
            HopThoai.hienThiCanhBao(this, "Từ ngày không được lớn hơn Đến ngày.");
            return;
        }
        String kieu = String.valueOf(cboSuKienKieuHopChon.getSelectedItem());
        String kieuKhoa = kieu.startsWith("Tất cả") ? "" : kieu.split(" ")[0];
        String ketQua = String.valueOf(cboKetQuaHopChon.getSelectedItem());
        String nguoiDung = oTruongNguoiDungTimKiem.getText().trim().toLowerCase();
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> new ArrayList<>(NhatKyHeThongService.macDinh().layCacMuc()),
                cacMuc -> apDungDanhSachNhatKy(cacMuc, tu, den, kieuKhoa, ketQua, nguoiDung));
    }

    private void apDungDanhSachNhatKy(
            List<DongNhatKyHeThong> cacMuc,
            LocalDate tu,
            LocalDate den,
            String kieuKhoa,
            String ketQua,
            String nguoiDung) {
        moHinhBang.setRowCount(0);
        int cacCanhBao = 0;
        for (DongNhatKyHeThong suKien : cacMuc) {
            LocalDate ngayDangXet = suKien.thoiGian().toLocalDate();
            if (tu != null && ngayDangXet.isBefore(tu)) continue;
            if (den != null && ngayDangXet.isAfter(den)) continue;
            if (!kieuKhoa.isEmpty() && !kieuKhoa.equals(suKien.kieu())) continue;
            if (!ketQua.equals("Tất cả") && !ketQua.equals(suKien.ketQua())) continue;
            String tenNguoiDung =
                    suKien.nguoiDung() != null && suKien.nguoiDung().getHoTen() != null
                            ? suKien.nguoiDung().getHoTen()
                            : "";
            if (!nguoiDung.isEmpty() && !tenNguoiDung.toLowerCase().contains(nguoiDung)) continue;
            if ("Cảnh báo".equals(suKien.ketQua()) || "Thất bại".equals(suKien.ketQua()))
                cacCanhBao++;
            moHinhBang.addRow(
                    new Object[] {
                        suKien.layVanBanThoiGian(),
                        tenNguoiDung,
                        suKien.thaoTac()
                                + (suKien.doiTuong().isBlank() ? "" : ": " + suKien.doiTuong()),
                        suKien.kieu(),
                        suKien.ketQua()
                    });
        }
        nhanNhanCacChiSo.setText(
                "Đang hiển thị: "
                        + moHinhBang.getRowCount()
                        + " sự kiện • "
                        + cacCanhBao
                        + " cảnh báo/thất bại");
    }

    private void xoaCacBoLoc() {
        khungKhoangNgayBoChon.chonKy("7 ngày qua");
        cboSuKienKieuHopChon.setSelectedIndex(0);
        cboKetQuaHopChon.setSelectedIndex(0);
        oTruongNguoiDungTimKiem.setText("");
        taiLai();
    }

    private void xuLyHienThiChiTiet(int dong) {
        int moHinhDong = bangBang.convertRowIndexToModel(dong);
        String thoiGian = moHinhBang.getValueAt(moHinhDong, 0).toString();
        String nguoiDung = moHinhBang.getValueAt(moHinhDong, 1).toString();
        String thaoTac = moHinhBang.getValueAt(moHinhDong, 2).toString();
        String kieuSuKien = moHinhBang.getValueAt(moHinhDong, 3).toString();
        String ketQua = moHinhBang.getValueAt(moHinhDong, 4).toString();
        String chiTiet =
                String.format(
                        "CHI TIẾT SỰ KIỆN\n\n"
                                + "═══════════════════════════\n"
                                + "Thời gian: %s\n"
                                + "Người dùng: %s\n"
                                + "Hành động: %s\n"
                                + "Loại: %s\n"
                                + "Kết quả: %s\n"
                                + "═══════════════════════════\n\n"
                                + "Nhật ký chỉ đọc, không thể sửa hoặc xóa.",
                        thoiGian, nguoiDung, thaoTac, kieuSuKien, ketQua);
        HopThoai.hienThiThongTin(this, chiTiet, "Chi tiết Audit Log");
    }

    private void xuLyXuat() {
        XuatExcelDialog.xuatBang(this, bangBang, "Audit_Log");
    }

    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Nhật ký hệ thống";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Hướng dẫn tra cứu Audit Log:\n\n"
                + "【 Bộ lọc 】\n"
                + "• Date Range: BẮT BUỘC phải chọn khoảng thời gian\n"
                + "→ Database có hàng triệu bản ghi, không thể load hết\n"
                + "• Loại sự kiện: Lọc theo LOGIN, PAYMENT, DELETE...\n"
                + "• Kết quả: Lọc Thành công/Thất bại/Cảnh báo\n"
                + "• Người dùng: Tìm theo tên\n\n"
                + "【 Tra soát gian lận 】\n"
                + "Các loại sự kiện quan trọng cần kiểm tra:\n"
                + "• DELETE: Xóa hóa đơn, xóa dữ liệu\n"
                + "• WARNING/ERROR: Cảnh báo bất thường\n"
                + "• LOGIN thất bại nhiều lần: Nghi vấn tấn công\n\n"
                + "【 Export 】\n"
                + "Xuất kết quả ra CSV để phân tích chi tiết\n\n"
                + "Mẹo: Double-click vào dòng để xem chi tiết đầy đủ.";
    }

    private static class PillBadgeCellRenderer extends JPanel implements TableCellRenderer {
        private final BadgePillLabel huyHieu = new BadgePillLabel();

        public PillBadgeCellRenderer() {
            super(new GridBagLayout());
            setOpaque(true);
            add(huyHieu);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            String val = value == null ? "" : value.toString();
            huyHieu.setText(val);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : MauUngDung.DONG_XEN_KE);
            }
            switch (val) {
                case "Thành công" ->
                        huyHieu.datMauHuyHieu(MauUngDung.mau(0xDCFCE7), MauUngDung.XANH_LA);
                case "Thất bại" ->
                        huyHieu.datMauHuyHieu(
                                MauUngDung.KHONG_HOP_LE_NEN, MauUngDung.VAN_BAN_NGUY_HIEM);
                case "Cảnh báo" ->
                        huyHieu.datMauHuyHieu(MauUngDung.mau(0xFEF3C7), MauUngDung.CAM_NHAN);
                default ->
                        huyHieu.datMauHuyHieu(MauUngDung.XAM_NEN_TRUNG, MauUngDung.mau(0x4B5563));
            }
            return this;
        }
    }

    private static class BadgePillLabel extends JLabel {
        private Color nenHuyHieu = Color.WHITE;

        public BadgePillLabel() {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 12, 4, 12));
        }

        public void datMauHuyHieu(Color bg, Color fg) {
            this.nenHuyHieu = bg;
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getText() != null && !getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(nenHuyHieu);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
