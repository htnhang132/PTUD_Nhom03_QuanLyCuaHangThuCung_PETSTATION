package com.petstation.gui.spa;

import com.petstation.dto.GioHang;
import com.petstation.dto.SpaDTO;
import com.petstation.gui.chung.ChinhFrame;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.MucLocTrangThai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.gui.luutru.NhatKyChamSocDialog;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.PhieuTiepNhanSpa;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class TiepNhanSpaPanel extends NghiepVuSpaPanel {
    private static final String TAT_CA = "Tất cả";

    // 1. THUỘC TÍNH & HÀM KHỞI TẠO

    private final JTabbedPane tabWorkspace = new JTabbedPane();
    private final Map<String, String> sdtTheoMaPhieu = new ConcurrentHashMap<>();
    private final List<MucLocTrangThai> cacMucTrangThai =
            List.of(
                    new MucLocTrangThai(TAT_CA, TAT_CA),
                    new MucLocTrangThai(TrangThaiPhieuSpa.TIEP_NHAN.maDB(), "Tiếp nhận"),
                    new MucLocTrangThai(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB(), "Đang thực hiện"),
                    new MucLocTrangThai(TrangThaiPhieuSpa.HOAN_TAT.maDB(), "Hoàn tất"),
                    new MucLocTrangThai(TrangThaiPhieuSpa.DA_THANH_TOAN.maDB(), "Đã thanh toán"),
                    new MucLocTrangThai(TrangThaiPhieuSpa.DA_HUY.maDB(), "Đã hủy"));
    private final JComboBox<MucLocTrangThai> cboTrangThai =
            new JComboBox<>(cacMucTrangThai.toArray(MucLocTrangThai[]::new));
    private final BoChonNgay oTuNgay = new BoChonNgay(LocalDate.now().minusDays(29));
    private final BoChonNgay oDenNgay = new BoChonNgay(LocalDate.now());
    private boolean dangDatLaiBoLoc;
    private volatile boolean daKhoiTaoXong;

    private LapPhieuSpaPanel khungLapPhieu;
    private LichHenSpaPanel khungLichHen;
    private NutDinhDang btnBatDauLam;
    private NutDinhDang btnHoanThanh;
    private NutDinhDang btnThanhToan;
    private NutDinhDang btnThemDichVu;
    private NutDinhDang btnThaoTacKhac;

    public TiepNhanSpaPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_SPA, banHang, dieuHuong);
        cauHinhBoLocVaThanhTacVu();
        xayDungCacTabSpa(banHang, dieuHuong);
        thayBoLangNgheTimKiem(this::apDungBoLocBangSpa);
        daKhoiTaoXong = true;
        lamMoi();
    }

    // 2. XÂY DỰNG GIAO DIỆN, NẠP DỮ LIỆU & BỘ LỌC

    private void cauHinhBoLocVaThanhTacVu() {
        ThanhDanhSach bar = (ThanhDanhSach) layKhungThaoTac();
        layOTimKiem().setPreferredSize(new Dimension(200, MauUngDung.CAO_DAU_VAO));
        oTuNgay.setPreferredSize(new Dimension(155, MauUngDung.CAO_DAU_VAO));
        oDenNgay.setPreferredSize(new Dimension(155, MauUngDung.CAO_DAU_VAO));
        oTuNgay.addPropertyChangeListener(
                "date",
                e -> {
                    if (dangDatLaiBoLoc) return;
                    LocalDate tu = oTuNgay.layNgayDuocChon();
                    LocalDate den = oDenNgay.layNgayDuocChon();
                    if (tu != null && den != null && tu.isAfter(den)) {
                        dangDatLaiBoLoc = true;
                        oDenNgay.datNgayDuocChon(tu);
                        dangDatLaiBoLoc = false;
                    }
                    lamMoi();
                });
        oDenNgay.addPropertyChangeListener(
                "date",
                e -> {
                    if (dangDatLaiBoLoc) return;
                    LocalDate tu = oTuNgay.layNgayDuocChon();
                    LocalDate den = oDenNgay.layNgayDuocChon();
                    if (tu != null && den != null && den.isBefore(tu)) {
                        dangDatLaiBoLoc = true;
                        oTuNgay.datNgayDuocChon(den);
                        dangDatLaiBoLoc = false;
                    }
                    lamMoi();
                });
        cboTrangThai.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) {
                        apDungBoLocBangSpa();
                    }
                });

        bar.themBoLoc("Từ ngày:", oTuNgay);
        bar.themBoLoc("Đến ngày:", oDenNgay);
        bar.themBoLoc("Trạng thái:", cboTrangThai);

        NutDinhDang btnLapPhieu = new NutDinhDang("+ Lập phiếu", NutDinhDang.KieuNut.CHINH);
        btnLapPhieu.addActionListener(e -> tiepNhan());
        bar.add(btnLapPhieu);
        btnBatDauLam =
                themNut(
                        bar,
                        "Bắt đầu làm",
                        NutDinhDang.KieuNut.PHU,
                        () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB()));
        btnHoanThanh =
                themNut(
                        bar,
                        "Hoàn thành",
                        NutDinhDang.KieuNut.THANH_CONG,
                        () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.HOAN_TAT.maDB()));
        btnThanhToan =
                themNut(bar, "Thanh toán", NutDinhDang.KieuNut.THANH_CONG, () -> quyetToan("SPA"));
        btnThemDichVu = themNut(bar, "+ Dịch vụ", NutDinhDang.KieuNut.PHU, this::themDichVu);
        btnThaoTacKhac =
                themNut(
                        bar,
                        "Thao tác khác ▾",
                        NutDinhDang.KieuNut.TRONG_SUOT,
                        this::moMenuThaoTacKhac);

        ganSuKienDupChuot(this::xuLyDupChuotBangSpa);
        ganMenuChuotPhai(this::taoMenuChuotPhaiSpa);
    }

    private void xayDungCacTabSpa(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        Component compNorth = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component compCenter = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        removeAll();
        setLayout(new BorderLayout());

        JPanel khungDanhSach = new JPanel(new BorderLayout(0, 10));
        khungDanhSach.setOpaque(true);
        khungDanhSach.setBackground(MauUngDung.NEN_KEM);
        khungDanhSach.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        if (compNorth != null) {
            khungDanhSach.add(compNorth, BorderLayout.NORTH);
        }
        if (compCenter != null) {
            khungDanhSach.add(compCenter, BorderLayout.CENTER);
        }

        khungLapPhieu = taoKhungLapPhieu();
        khungLapPhieu.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        khungLichHen = taoKhungLichHen(banHang, dieuHuong);
        khungLichHen.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tabWorkspace.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabWorkspace.addTab("Điều phối phiếu Spa", khungDanhSach);
        tabWorkspace.addTab("Lập phiếu mới", khungLapPhieu);
        tabWorkspace.addTab("Lịch hẹn Spa", khungLichHen);
        tabWorkspace.addChangeListener(e -> khiChuyenTabSpa());
        add(tabWorkspace, BorderLayout.CENTER);
        capNhatThaoTac();
    }

    private LapPhieuSpaPanel taoKhungLapPhieu() {
        return new LapPhieuSpaPanel(
                maPhieu -> {
                    layBanHang().getQuyTrinh().chonSpa(maPhieu);
                    chonTab(0);
                    lamMoi();
                },
                () -> chonTab(2),
                null);
    }

    private LichHenSpaPanel taoKhungLichHen(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        return new LichHenSpaPanel(
                banHang,
                dieuHuong,
                maLichHen -> {
                    khungLapPhieu.nhanLichHen(maLichHen);
                    chonTab(1);
                });
    }

    @Override
    protected List<Object[]> layDuLieu() {
        if (!daKhoiTaoXong || spaService == null || oTuNgay == null || oDenNgay == null) {
            return List.of();
        }
        LocalDate homNay = LocalDate.now();
        LocalDate tuNgay =
                oTuNgay.layNgayDuocChon() == null
                        ? homNay.minusDays(29)
                        : oTuNgay.layNgayDuocChon();
        LocalDate denNgay =
                oDenNgay.layNgayDuocChon() == null ? homNay : oDenNgay.layNgayDuocChon();
        List<SpaDTO.DongTimKiemSpa> ds = spaService.timKiem(tuNgay, denNgay, null);
        List<Object[]> ketQua = new ArrayList<>(ds.size());
        Map<String, String> sdtMoi = new HashMap<>(ds.size());
        for (SpaDTO.DongTimKiemSpa d : ds) {
            PhieuTiepNhanSpa p = d.getPhieuSpa();
            KhachHang k = d.getKhachHang();
            ThuCung tc = d.getThuCung();
            NhanVien nv = d.getNhanVien();
            if (k != null && k.getSoDienThoai() != null) {
                sdtMoi.put(d.getMaKhoa(), k.getSoDienThoai());
            }
            ketQua.add(
                    new Object[] {
                        d.getMaKhoa(),
                        p.getMaPhieu(),
                        k != null ? k.getHoTen() : "",
                        tc != null ? tc.getTen() : "",
                        p.getCanNangThucTe(),
                        nv != null ? nv.getHoTen() : "",
                        p.getTongTien(),
                        p.getTrangThai() != null ? p.getTrangThai().maDB() : "",
                        DinhDang.ngayGio(p.getTaoLuc())
                    });
        }
        Runnable capNhatMap =
                () -> {
                    sdtTheoMaPhieu.clear();
                    sdtTheoMaPhieu.putAll(sdtMoi);
                };
        if (SwingUtilities.isEventDispatchThread()) {
            capNhatMap.run();
        } else {
            SwingUtilities.invokeLater(capNhatMap);
        }
        return ketQua;
    }

    @Override
    protected void sauKhiTaiLai() {
        if (!daKhoiTaoXong) {
            return;
        }
        capNhatSoLuongTrangThai();
        apDungBoLocBangSpa();
        capNhatThaoTac();
    }

    @Override
    protected void datLaiBoLoc() {
        dangDatLaiBoLoc = true;
        oTuNgay.datNgayDuocChon(LocalDate.now().minusDays(29));
        oDenNgay.datNgayDuocChon(LocalDate.now());
        cboTrangThai.setSelectedIndex(0);
        dangDatLaiBoLoc = false;
        apDungBoLocBangSpa();
    }

    private void capNhatSoLuongTrangThai() {
        capNhatSoLuongMucLoc(cacMucTrangThai, cboTrangThai, 7, TAT_CA);
    }

    private void apDungBoLocBangSpa() {
        if (layBangDuLieu() == null
                || !(layBangDuLieu().getRowSorter() instanceof TableRowSorter)) {
            return;
        }
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter =
                (TableRowSorter<DefaultTableModel>) layBangDuLieu().getRowSorter();
        String tuKhoa =
                layOTimKiem() == null
                        ? ""
                        : layOTimKiem().getText().trim().toLowerCase(Locale.ROOT);
        MucLocTrangThai mucChon = (MucLocTrangThai) cboTrangThai.getSelectedItem();
        String mucTrangThai = mucChon == null ? TAT_CA : mucChon.layMa();
        sorter.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(
                            Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        if (!TAT_CA.equals(mucTrangThai)
                                && !mucTrangThai.equalsIgnoreCase(entry.getStringValue(7))) {
                            return false;
                        }
                        return khopTuKhoaDong(entry, tuKhoa);
                    }
                });
    }

    private boolean khopTuKhoaDong(
            RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry, String tuKhoa) {
        if (tuKhoa.isEmpty()) {
            return true;
        }
        String sdt = sdtTheoMaPhieu.getOrDefault(entry.getStringValue(0), "");
        if (sdt.toLowerCase(Locale.ROOT).contains(tuKhoa)) {
            return true;
        }
        for (int c = 0; c < entry.getValueCount(); c++) {
            if (entry.getStringValue(c).toLowerCase(Locale.ROOT).contains(tuKhoa)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void capNhatThaoTac() {
        super.capNhatThaoTac();
        if (btnBatDauLam == null
                || btnHoanThanh == null
                || btnThanhToan == null
                || btnThemDichVu == null
                || btnThaoTacKhac == null
                || layBangDuLieu() == null) {
            return;
        }
        String trangThai = layTrangThaiDongChon();
        boolean coChon = !trangThai.isEmpty();
        boolean coDichVu = coChon && laDongCoDichVu();
        boolean quyenThanhToan = coQuyenThanhToan();

        btnBatDauLam.setEnabled(TrangThaiPhieuSpa.TIEP_NHAN.maDB().equals(trangThai) && coDichVu);
        btnHoanThanh.setEnabled(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equals(trangThai));
        btnThanhToan.setEnabled(
                TrangThaiPhieuSpa.HOAN_TAT.maDB().equals(trangThai) && quyenThanhToan);
        btnThemDichVu.setEnabled(
                Set.of(TrangThaiPhieuSpa.TIEP_NHAN.maDB(), TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB())
                        .contains(trangThai));
        btnThaoTacKhac.setEnabled(coChon);
        layKhungThaoTac().revalidate();
        layKhungThaoTac().repaint();
    }

    private String layTrangThaiDongChon() {
        int dong = layBangDuLieu().getSelectedRow();
        if (dong < 0) {
            return "";
        }
        return String.valueOf(
                layMoHinh().getValueAt(layBangDuLieu().convertRowIndexToModel(dong), 7));
    }

    private boolean laDongCoDichVu() {
        int dong = layBangDuLieu().getSelectedRow();
        if (dong < 0) {
            return false;
        }
        Object tong = layMoHinh().getValueAt(layBangDuLieu().convertRowIndexToModel(dong), 6);
        if (tong instanceof Number so) {
            return so.longValue() > 0;
        }
        return DinhDang.doc(String.valueOf(tong)) > 0;
    }

    private void moMenuThaoTacKhac() {
        int dong = layBangDuLieu().getSelectedRow();
        if (dong < 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu Spa.");
            return;
        }
        JPopupMenu menu = taoMenuThaoTacBoSung(layTrangThaiDongChon());
        menu.show(btnThaoTacKhac, 0, btnThaoTacKhac.getHeight());
    }

    private JPopupMenu taoMenuThaoTacBoSung(String trangThai) {
        JPopupMenu popup = new JPopupMenu();
        popup.add(taoMucMenu("Chi tiết dịch vụ", this::laySpaChiTiet));
        if (laDongCoDichVu()
                && Set.of(
                                TrangThaiPhieuSpa.TIEP_NHAN.maDB(),
                                TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB(),
                                TrangThaiPhieuSpa.HOAN_TAT.maDB())
                        .contains(trangThai)) {
            popup.add(taoMucMenu("Xóa dịch vụ", this::xoaDichVu));
        }
        boolean daDong =
                TrangThaiPhieuSpa.DA_THANH_TOAN.maDB().equals(trangThai)
                        || TrangThaiPhieuSpa.DA_HUY.maDB().equals(trangThai);
        popup.add(
                taoMucMenu(
                        daDong ? "Xem phiếu chăm sóc" : "Phiếu chăm sóc",
                        () -> {
                            NhatKyChamSocDialog.mo(this, "SPA", layDongChon(), !daDong);
                            lamMoi();
                        }));
        return popup;
    }

    // 3. VÒNG ĐỜI & THANH CÔNG CỤ

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        int idx = tabWorkspace.getSelectedIndex();
        return new KhaNangThanhCongCu(
                idx != 2, false, idx == 0 || idx == 2, idx == 0 || idx == 2, false, true);
    }

    @Override
    public void xuLyTaoMoi() {
        chonTab(1);
    }

    @Override
    public void xuLyTimKiem() {
        int idx = tabWorkspace.getSelectedIndex();
        if (idx == 0) {
            super.xuLyTimKiem();
        } else if (idx == 2 && khungLichHen != null) {
            khungLichHen.xuLyTimKiem();
        }
    }

    @Override
    public void xuLyIn() {
        int idx = tabWorkspace.getSelectedIndex();
        if (idx == 0) {
            super.xuLyIn();
        } else if (idx == 2 && khungLichHen != null) {
            khungLichHen.xuLyIn();
        }
    }

    @Override
    public void xuLyHienThi() {
        super.xuLyHienThi();
        khiChuyenTabSpa();
    }

    @Override
    public void xuLyLamMoi() {
        int idx = tabWorkspace.getSelectedIndex();
        if (idx == 0) {
            super.xuLyLamMoi();
        } else if (idx == 1) {
            khungLapPhieu.xuLyLamMoi();
        } else if (idx == 2) {
            khungLichHen.xuLyLamMoi();
        }
    }

    // 4. TƯƠNG TÁC BẢNG & XỬ LÝ TRẠNG THÁI SPA

    private void khiChuyenTabSpa() {
        int idx = tabWorkspace.getSelectedIndex();
        if (idx == 0) {
            lamMoi();
        } else if (idx == 1) {
            khungLapPhieu.xuLyHienThi();
        } else if (idx == 2) {
            khungLichHen.xuLyHienThi();
        }
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof ChinhFrame cf) {
            cf.capNhatThanhCongCu();
        }
    }

    private void xuLyDupChuotBangSpa() {
        if (layBangDuLieu().getSelectedRow() >= 0) {
            laySpaChiTiet();
        }
    }

    private JPopupMenu taoMenuChuotPhaiSpa(int row) {
        String trangThai =
                String.valueOf(
                        layMoHinh().getValueAt(layBangDuLieu().convertRowIndexToModel(row), 7));
        boolean coDichVu = laDongCoDichVu();
        boolean quyenThanhToan = coQuyenThanhToan();
        JPopupMenu popup = new JPopupMenu();
        if (TrangThaiPhieuSpa.TIEP_NHAN.maDB().equals(trangThai)) {
            JMenuItem itemBatDau =
                    taoMucMenu(
                            "Bắt đầu làm",
                            () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB()));
            itemBatDau.setEnabled(coDichVu);
            popup.add(itemBatDau);
            popup.add(taoMucMenu("+ Dịch vụ", this::themDichVu));
            if (coDichVu) popup.add(taoMucMenu("Xóa dịch vụ", this::xoaDichVu));
        } else if (TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equals(trangThai)) {
            popup.add(
                    taoMucMenu(
                            "Hoàn thành",
                            () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.HOAN_TAT.maDB())));
            popup.add(taoMucMenu("+ Dịch vụ", this::themDichVu));
            if (coDichVu) popup.add(taoMucMenu("Xóa dịch vụ", this::xoaDichVu));
        } else if (TrangThaiPhieuSpa.HOAN_TAT.maDB().equals(trangThai)) {
            JMenuItem itemThanhToan = taoMucMenu("Thanh toán", () -> quyetToan("SPA"));
            itemThanhToan.setEnabled(quyenThanhToan);
            popup.add(itemThanhToan);
            if (coDichVu) popup.add(taoMucMenu("Xóa dịch vụ", this::xoaDichVu));
        }
        if (popup.getComponentCount() > 0) {
            popup.addSeparator();
        }
        boolean daDong =
                TrangThaiPhieuSpa.DA_THANH_TOAN.maDB().equals(trangThai)
                        || TrangThaiPhieuSpa.DA_HUY.maDB().equals(trangThai);
        popup.add(taoMucMenu("Chi tiết dịch vụ", this::laySpaChiTiet));
        popup.add(
                taoMucMenu(
                        daDong ? "Xem phiếu chăm sóc" : "Phiếu chăm sóc",
                        () -> {
                            NhatKyChamSocDialog.mo(this, "SPA", layDongChon(), !daDong);
                            lamMoi();
                        }));
        return popup;
    }

    private JMenuItem taoMucMenu(String nhan, Runnable hanhDong) {
        JMenuItem item = new JMenuItem(nhan);
        item.addActionListener(
                e -> {
                    try {
                        hanhDong.run();
                    } catch (RuntimeException loi) {
                        HopThoai.hienThiCanhBao(this, loi.getMessage());
                    }
                });
        return item;
    }

    private void capNhatTrangThaiSpaNen(String trangThaiMoi) {
        if (layBangDuLieu().getSelectedRow() < 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu Spa cần cập nhật.");
            return;
        }
        String ma = layDongChon();
        String trangThaiHienTai = layTrangThaiDongChon();
        boolean batDauLam = TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equals(trangThaiMoi);
        String yeuCauHienTai =
                batDauLam
                        ? TrangThaiPhieuSpa.TIEP_NHAN.maDB()
                        : TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB();
        if (!yeuCauHienTai.equals(trangThaiHienTai)) {
            HopThoai.hienThiCanhBao(
                    this,
                    batDauLam
                            ? "Chỉ phiếu ở trạng thái Tiếp nhận mới có thể Bắt đầu làm."
                            : "Chỉ phiếu Đang thực hiện mới có thể đánh dấu Hoàn thành.");
            return;
        }
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    spaService.layTrangThaiSpa(ma, trangThaiMoi);
                    return null;
                },
                null,
                v -> lamMoi(),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    public void chonTab(int chiSo) {
        if (chiSo >= 0 && chiSo < tabWorkspace.getTabCount()) {
            tabWorkspace.setSelectedIndex(chiSo);
        }
    }

    @Override
    protected void tiepNhan() {
        chonTab(1);
    }
}
