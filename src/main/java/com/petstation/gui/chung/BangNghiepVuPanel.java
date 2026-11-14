package com.petstation.gui.chung;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dto.DongNghiepVu;
import com.petstation.dto.GioHang;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public abstract class BangNghiepVuPanel extends CoSoPanel {
    private final LoaiNghiepVu kieuNghiepVu;

    private final GioHang banHang;

    private final Consumer<MaManHinh> dieuHuong;

    private final NghiepVuService nghiepVuService;

    private final DefaultTableModel moHinh;

    private final JTable bangBang;

    private JPanel khungCacThaoTac;

    private JTextField oTimKiem;

    private final JLabel nhanSoLuong = new JLabel();

    protected final LoaiNghiepVu layKieuNghiepVu() {
        return kieuNghiepVu;
    }

    protected final GioHang layBanHang() {
        return banHang;
    }

    protected final Consumer<MaManHinh> layDieuHuong() {
        return dieuHuong;
    }

    protected final NghiepVuService layNghiepVuService() {
        return nghiepVuService;
    }

    protected final DefaultTableModel layMoHinh() {
        return moHinh;
    }

    protected final JTable layBangDuLieu() {
        return bangBang;
    }

    protected final JPanel layKhungThaoTac() {
        return khungCacThaoTac;
    }

    protected final JTextField layOTimKiem() {
        return oTimKiem;
    }

    protected final void thayBoLangNgheTimKiem(Runnable hanhDong) {
        thayBoLangNgheTaiLieu(oTimKiem, hanhDong);
    }

    protected BangNghiepVuPanel(
            LoaiNghiepVu kieuNghiepVu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        this(kieuNghiepVu, banHang, dieuHuong, new NghiepVuService());
    }

    protected BangNghiepVuPanel(
            LoaiNghiepVu kieuNghiepVu,
            GioHang banHang,
            Consumer<MaManHinh> dieuHuong,
            NghiepVuService nghiepVuService) {
        this.kieuNghiepVu = kieuNghiepVu;
        this.banHang = banHang;
        this.dieuHuong = dieuHuong;
        this.nghiepVuService = Objects.requireNonNull(nghiepVuService);
        NghiepVuService.DinhNghia hienThi = nghiepVuService.layDinhNghia(kieuNghiepVu);
        setLayout(new BorderLayout(0, 12));
        JPanel khungTren = taoThanhDauBangNghiepVu(hienThi);
        add(khungTren, BorderLayout.NORTH);
        moHinh = DinhDangBang.taoMoHinhChiDoc(hienThi.cacCot());
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        bangBang = layBang(scrCuon);
        bangBang.removeColumn(bangBang.getColumnModel().getColumn(0));
        if (kieuNghiepVu != LoaiNghiepVu.HOA_DON) lienKetBoLoc(oTimKiem, bangBang);
        bangBang.getRowSorter()
                .addRowSorterListener(
                        e ->
                                nhanSoLuong.setText(
                                        ThanhDanhSach.soKetQua(
                                                bangBang.getRowCount(), moHinh.getRowCount())));
        add(scrCuon, BorderLayout.CENTER);
        NutDinhDang btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(
                suKien -> XuatExcelDialog.xuatBang(this, bangBang, hienThi.tieuDe()));
        khungCacThaoTac.add(btnXuat);
        dangKyChonDongNghiepVu();
        lamMoi();
    }

    private JPanel taoThanhDauBangNghiepVu(NghiepVuService.DinhNghia hienThi) {
        JPanel khungTren = taoVungTrongSuot(new BorderLayout());
        khungTren.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh(hienThi.tieuDe()), nhanSoLuong),
                BorderLayout.NORTH);
        khungCacThaoTac = new ThanhDanhSach();
        oTimKiem = taoOTimKiem("Tìm trong danh sách...");
        khungCacThaoTac.add(oTimKiem);
        khungTren.add(khungCacThaoTac, BorderLayout.CENTER);
        return khungTren;
    }

    private void dangKyChonDongNghiepVu() {
        bangBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting() || bangBang.getSelectedRow() < 0) return;
                            String ma = layDongChon();
                            switch (kieuNghiepVu) {
                                case LICH_HEN -> banHang.getQuyTrinh().chonLichHen(ma);
                                case PHIEU_SPA,
                                                TIEP_NHAN_SPA,
                                                GOI_DICH_VU_SPA,
                                                TIEN_DO_SPA,
                                                HOAN_TAT_SPA ->
                                        banHang.getQuyTrinh().chonSpa(ma);
                                case HOP_DONG_LUU_TRU,
                                                DAT_PHONG_LUU_TRU,
                                                NHAT_KY_LUU_TRU,
                                                TRA_PHONG_LUU_TRU ->
                                        banHang.getQuyTrinh().chonHotel(ma);
                                default -> {}
                            }
                            capNhatThaoTac();
                        });
    }

    protected NutDinhDang themNut(
            JPanel khungVungChua, String nhan, NutDinhDang.KieuNut kieu, Runnable thaoTac) {
        NutDinhDang btnNut = new NutDinhDang(nhan, kieu);
        btnNut.addActionListener(
                suKien -> {
                    try {
                        thaoTac.run();
                        lamMoi();
                    } catch (RuntimeException ngoaiLe) {
                        HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
                    }
                });
        khungVungChua.add(btnNut);
        capNhatThaoTac();
        return btnNut;
    }

    protected NutDinhDang themNut(JPanel khungVungChua, String nhan, Runnable thaoTac) {
        return themNut(
                khungVungChua,
                nhan,
                nhan.startsWith("+") ? NutDinhDang.KieuNut.CHINH : NutDinhDang.KieuNut.TRONG_SUOT,
                thaoTac);
    }

    public void ganPhimTat(int keyCode, Runnable thaoTac) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, 0);
        String actionKey = "phimTat_" + keyCode;
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, actionKey);
        getActionMap()
                .put(
                        actionKey,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    thaoTac.run();
                                } catch (RuntimeException ex) {
                                    HopThoai.hienThiCanhBao(
                                            BangNghiepVuPanel.this, ex.getMessage());
                                }
                            }
                        });
    }

    protected void ganSuKienDupChuot(Runnable thaoTac) {
        bangBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2 && bangBang.getSelectedRow() >= 0) {
                            try {
                                thaoTac.run();
                                lamMoi();
                            } catch (RuntimeException ex) {
                                HopThoai.hienThiCanhBao(BangNghiepVuPanel.this, ex.getMessage());
                            }
                        }
                    }
                });
    }

    protected void ganMenuChuotPhai(Function<Integer, JPopupMenu> taoMenu) {
        bangBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        hienMenu(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        hienMenu(e);
                    }

                    private void hienMenu(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            int row = bangBang.rowAtPoint(e.getPoint());
                            if (row >= 0) {
                                bangBang.setRowSelectionInterval(row, row);
                                JPopupMenu menu = taoMenu.apply(row);
                                if (menu != null) {
                                    menu.show(e.getComponent(), e.getX(), e.getY());
                                }
                            }
                        }
                    }
                });
    }

    protected void capNhatThaoTac() {}

    protected List<Object[]> layDuLieu() {
        return nghiepVuService.layCacDong(kieuNghiepVu).stream()
                .map(DongNghiepVu::thanhHang)
                .toList();
    }

    protected void lamMoi() {
        if (SwingUtilities.isEventDispatchThread() && DieuKienTaiDuLieu.coTheTachLuong()) {
            taiLaiNen();
            return;
        }
        Object[][] duLieu = layDuLieu().toArray(Object[][]::new);
        apDungDuLieu(duLieu);
    }

    private void apDungDuLieu(Object[][] duLieu) {
        String daChon = bangBang.getSelectedRow() < 0 ? null : layDongChon();
        var khoaSapXep = new ArrayList<>(bangBang.getRowSorter().getSortKeys());
        moHinh.setRowCount(0);
        for (Object[] dong : duLieu) moHinh.addRow(dong);
        bangBang.getRowSorter().setSortKeys(khoaSapXep);
        nhanSoLuong.setText(ThanhDanhSach.soKetQua(bangBang.getRowCount(), moHinh.getRowCount()));
        String giu =
                switch (kieuNghiepVu) {
                    case LICH_HEN -> banHang.getQuyTrinh().getLichHen();
                    case PHIEU_SPA, TIEP_NHAN_SPA, GOI_DICH_VU_SPA, TIEN_DO_SPA, HOAN_TAT_SPA ->
                            banHang.getQuyTrinh().getPhieuSpa();
                    case HOP_DONG_LUU_TRU, DAT_PHONG_LUU_TRU, NHAT_KY_LUU_TRU, TRA_PHONG_LUU_TRU ->
                            banHang.getQuyTrinh().getHopDongHotel();
                    default -> "";
                };
        if (daChon != null && !daChon.isBlank()) giu = daChon;
        for (int dong = 0; giu != null && !giu.isBlank() && dong < moHinh.getRowCount(); dong++)
            if (giu.equals(String.valueOf(moHinh.getValueAt(dong, 0)))) {
                int hienThi = bangBang.convertRowIndexToView(dong);
                if (hienThi >= 0) bangBang.setRowSelectionInterval(hienThi, hienThi);
                break;
            }
        capNhatThaoTac();
        sauKhiTaiLai();
        NguCanhMoBanGhi.apDung(this);
    }

    protected void sauKhiTaiLai() {}

    protected void datLaiBoLoc() {}

    private int phienTaiLai;

    protected void taiLaiNen() {
        if (!DieuKienTaiDuLieu.coTheTachLuong()) {
            lamMoi();
            return;
        }
        int token = ++phienTaiLai;
        var session = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chay(
                () -> layDuLieu().toArray(Object[][]::new),
                duLieu -> {
                    if (token != phienTaiLai
                            || PhienDangNhapService.macDinh().layPhienHienTai() != session) return;
                    apDungDuLieu(duLieu);
                },
                ex -> {
                    if (token != phienTaiLai
                            || PhienDangNhapService.macDinh().layPhienHienTai() != session) return;
                    HopThoai.hienThiCanhBao(BangNghiepVuPanel.this, TacVuNen.thongDiepLoi(ex));
                });
    }

    protected String layDongChon() {
        if (bangBang.getSelectedRow() < 0) throw new IllegalArgumentException("Hãy chọn một dòng.");
        return chuyenThanhChuoi(
                moHinh.getValueAt(bangBang.convertRowIndexToModel(bangBang.getSelectedRow()), 0));
    }

    protected String layNoiDungDongChon(int cot) {
        layDongChon();
        return chuyenThanhChuoi(
                moHinh.getValueAt(bangBang.convertRowIndexToModel(bangBang.getSelectedRow()), cot));
    }

    protected void hienThiChiTiet(String tieuDe, String[] cacCot, List<Object[]> cacDong) {
        DefaultTableModel chiTiet =
                DinhDangBang.taoMoHinhChiDoc(cacDong.toArray(Object[][]::new), cacCot);
        JScrollPane scrCuon = taoBangDinhDang(chiTiet);
        scrCuon.setPreferredSize(new Dimension(800, 320));
        HopThoai.hienThiNoiDung(this, scrCuon, tieuDe);
    }

    public void xuLyHienThi() {
        lamMoi();
    }

    protected String layNoiDungTroGiup() {
        return "Chọn dòng trước khi xử lý. Các thay đổi được lưu vào SQL Server; nút Làm mới lấy dữ"
                + " liệu mới nhất.";
    }
}
