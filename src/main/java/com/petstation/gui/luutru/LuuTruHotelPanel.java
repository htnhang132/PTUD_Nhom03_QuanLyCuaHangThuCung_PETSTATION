package com.petstation.gui.luutru;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.ChinhFrame;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.MucLocTrangThai;
import com.petstation.gui.chung.NguCanhMoBanGhi;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Quản lý lưu trú Hotel (Mã: LUU_TRU_HOTEL)
// Luồng xử lý: GUI -> LuuTruService, ChamSocService -> HopDongLuuTruDAOImpl -> SQL
// (hop_dong_luu_tru, phong_hotel)
// Mô tả: Sơ đồ phòng lưu trú mặc định (Master-Detail), tiếp nhận phòng, đặt cọc, chăm sóc và trả
// phòng
// Phân hệ: luutru
public final class LuuTruHotelPanel extends NghiepVuHotelPanel {
    private static final int TAB_SO_DO = 0;
    private static final int TAB_DANH_SACH = 1;
    private static final String TAT_CA = "Tất cả";

    // 1. HẰNG SỐ, THUỘC TÍNH & HÀM KHỞI TẠO

    private final JTabbedPane tabWorkspace = new JTabbedPane();
    private final List<MucLocTrangThai> cacMucTrangThai =
            List.of(
                    new MucLocTrangThai(TAT_CA, TAT_CA),
                    new MucLocTrangThai(TrangThaiHopDong.DA_DAT.maDB(), "Đã đặt"),
                    new MucLocTrangThai(TrangThaiHopDong.DANG_O.maDB(), "Đang ở"),
                    new MucLocTrangThai(TrangThaiHopDong.DA_TRA_PHONG.maDB(), "Đã trả"),
                    new MucLocTrangThai(TrangThaiHopDong.DA_HUY.maDB(), "Đã hủy"));
    private final JComboBox<MucLocTrangThai> cboTrangThai =
            new JComboBox<>(cacMucTrangThai.toArray(MucLocTrangThai[]::new));
    private TraCuuPhongHotelPanel khungSoDoPhong;
    private NutDinhDang btnThuCoc;
    private NutDinhDang btnNhanPhong;
    private NutDinhDang btnPhieuChamSoc;
    private NutDinhDang btnQuyetToan;
    private NutDinhDang btnThaoTacKhac;
    private boolean coPhongDangDon;
    private String maHopDongChoMoNhatKy;
    private boolean dangDatLaiBoLoc;

    public LuuTruHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOP_DONG_LUU_TRU, banHang, dieuHuong);
        cauHinhBoLocVaThanhThaoTac();
        ganSuKienBangLuuTru();
        thayBoLangNgheTimKiem(this::apDungBoLocBangLuuTru);
        khungSoDoPhong = taoSoDoPhongLuuTru(banHang, dieuHuong);
        khungSoDoPhong.datBoDieuKhienHopDong(this, this::lamMoiTrongTab);
        xayDungTabLamViec();
        capNhatThaoTac();
    }

    // 2. XÂY DỰNG THANH TÁC VỤ & CÁC TAB LÀM VIỆC

    private void cauHinhBoLocVaThanhThaoTac() {
        ThanhDanhSach bar = (ThanhDanhSach) layKhungThaoTac();
        Component btnXuat = null;
        for (Component c : bar.getComponents()) {
            if (c instanceof JButton b && "Xuất Excel".equals(b.getText())) {
                btnXuat = b;
            }
        }
        bar.removeAll();
        layOTimKiem().setPreferredSize(new Dimension(200, 34));
        cboTrangThai.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) {
                        apDungBoLocBangLuuTru();
                    }
                });
        bar.add(layOTimKiem());
        bar.themBoLoc("Trạng thái:", cboTrangThai);

        themNut(bar, "+ Đặt phòng", NutDinhDang.KieuNut.CHINH, this::moDatPhongVaTaiLai);
        btnThuCoc =
                taoNutThanhCongCu(
                        "Thu cọc", NutDinhDang.KieuNut.PHU, () -> chayThaoTac(this::thuCoc));
        btnNhanPhong =
                taoNutThanhCongCu(
                        "Nhận phòng",
                        NutDinhDang.KieuNut.THANH_CONG,
                        () -> chayThaoTac(this::nhanPhong));
        btnPhieuChamSoc =
                taoNutThanhCongCu(
                        "Phiếu chăm sóc",
                        NutDinhDang.KieuNut.PHU,
                        () ->
                                chayThaoTacVaTaiLai(
                                        () ->
                                                NhatKyChamSocDialog.mo(
                                                        this, "HOTEL", layDongChon())));
        btnQuyetToan =
                taoNutThanhCongCu(
                        "Quyết toán",
                        NutDinhDang.KieuNut.THANH_CONG,
                        () -> chayThaoTac(this::quyetToanHopDongAnToan));
        btnThaoTacKhac = new NutDinhDang("Thao tác khác ▾", NutDinhDang.KieuNut.TRONG_SUOT);
        btnThaoTacKhac.addActionListener(e -> hienThiMenuThaoTacKhac(btnThaoTacKhac));
        bar.add(btnThaoTacKhac);
        if (btnXuat != null) {
            bar.add(btnXuat);
        }
        capNhatTrangThaiNutDonPhong();
        capNhatThaoTac();
    }

    private NutDinhDang taoNutThanhCongCu(
            String nhan, NutDinhDang.KieuNut kieu, Runnable hanhDong) {
        NutDinhDang btn = new NutDinhDang(nhan, kieu);
        btn.addActionListener(e -> hanhDong.run());
        layKhungThaoTac().add(btn);
        return btn;
    }

    @Override
    protected void capNhatThaoTac() {
        super.capNhatThaoTac();
        if (btnThuCoc == null || btnThaoTacKhac == null || layBangDuLieu() == null) {
            return;
        }
        String trangThai = layTrangThaiDongDangChon();
        boolean coChon = !trangThai.isEmpty();
        boolean daDat = TrangThaiHopDong.DA_DAT.maDB().equals(trangThai);
        boolean thieuCoc = daDat && cocConThieu() > 0;
        boolean dangO = TrangThaiHopDong.DANG_O.maDB().equals(trangThai);
        boolean quyenThanhToan = coQuyenThanhToan();

        btnThuCoc.setEnabled(thieuCoc);
        btnNhanPhong.setEnabled(daDat && !thieuCoc);
        btnPhieuChamSoc.setEnabled(dangO);
        btnQuyetToan.setEnabled(dangO && quyenThanhToan);
        btnQuyetToan.setToolTipText(
                quyenThanhToan ? null : "Bạn không có quyền thanh toán / quyết toán.");
        btnThaoTacKhac.setEnabled(coChon || coPhongDangDon);

        layKhungThaoTac().revalidate();
        layKhungThaoTac().repaint();
    }

    private void quyetToanHopDongAnToan() {
        if (!coQuyenThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Bạn không có quyền thanh toán / quyết toán.");
            return;
        }
        quyetToan("HOTEL");
    }

    private String layTrangThaiDongDangChon() {
        int row = layBangDuLieu().getSelectedRow();
        if (row < 0) {
            return "";
        }
        int dong = layBangDuLieu().convertRowIndexToModel(row);
        return String.valueOf(layMoHinh().getValueAt(dong, 9));
    }

    private void capNhatTrangThaiNutDonPhong() {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                luuTruService::layPhongDangDon,
                ds -> {
                    coPhongDangDon = ds != null && !ds.isEmpty();
                    if (btnThaoTacKhac != null && layBangDuLieu() != null) {
                        btnThaoTacKhac.setEnabled(
                                !layTrangThaiDongDangChon().isEmpty() || coPhongDangDon);
                    }
                },
                ex -> coPhongDangDon = false);
    }

    private void lamMoiTrongTab() {
        taiLaiNen();
        capNhatTrangThaiNutDonPhong();
    }

    private void nhanPhong() {
        String ma = layDongChon();
        if (!HopThoai.xacNhan(this, "Xác nhận check-in (nhận phòng) cho hợp đồng #" + ma + "?")) {
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.kiemTraVao(ma),
                () -> {
                    taiLaiNen();
                    if (khungSoDoPhong != null) khungSoDoPhong.lamMoiSoDo();
                    capNhatTrangThaiNutDonPhong();
                });
    }

    private void moDatPhongVaTaiLai() {
        if (!datPhong()) return;
        taiLaiNen();
        if (khungSoDoPhong != null && tabWorkspace.getSelectedIndex() == TAB_SO_DO) {
            khungSoDoPhong.lamMoiSoDo();
        }
    }

    private void ganSuKienBangLuuTru() {
        ganSuKienDupChuot(
                () -> {
                    int row = layBangDuLieu().getSelectedRow();
                    if (row < 0) return;
                    String trangThai = layTrangThaiDongDangChon();
                    if (TrangThaiHopDong.DA_DAT.maDB().equals(trangThai)) {
                        chayThaoTacVaTaiLai(this::suaDatPhong);
                    } else if (TrangThaiHopDong.DANG_O.maDB().equals(trangThai)) {
                        chayThaoTacVaTaiLai(
                                () -> NhatKyChamSocDialog.mo(this, "HOTEL", layDongChon()));
                    } else if (TrangThaiHopDong.DA_TRA_PHONG.maDB().equals(trangThai)) {
                        chayThaoTac(this::layNhatKyChiTiet);
                    }
                });
        ganMenuChuotPhai(
                row -> {
                    int dong = layBangDuLieu().convertRowIndexToModel(row);
                    return taoMenuNguCanh(
                            String.valueOf(layMoHinh().getValueAt(dong, 9)), true, true);
                });
    }

    private TraCuuPhongHotelPanel taoSoDoPhongLuuTru(
            GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        return new TraCuuPhongHotelPanel(
                banHang,
                dieuHuong,
                phong -> {
                    if (phong.kiemTraPhongTrong()) {
                        xuLyChonPhongTrong(phong);
                    } else if (TrangThaiPhongHotel.DANG_O.maDB().equals(phong.getTrangThai())
                            && phong.getDangO() != null) {
                        String maHd =
                                phong.getDangO().getHopDong() != null
                                        ? phong.getDangO().getHopDong().getMaHopDong()
                                        : "";
                        layBanHang().getQuyTrinh().chonHotel(maHd);
                        tabWorkspace.setSelectedIndex(TAB_DANH_SACH);
                        chonHopDongTheoMa(maHd);
                    }
                });
    }

    private void xuLyChonPhongTrong(PhongHotel phong) {
        try {
            layBanHang().getQuyTrinh().chonPhongHotel(phong.getMaPhong());
            moDatPhongVaTaiLai();
            layBanHang().getQuyTrinh().chonPhongHotel("");
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    private void xayDungTabLamViec() {
        Component compNorth = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component compCenter = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        removeAll();
        setLayout(new BorderLayout());

        JPanel khungDanhSach = new JPanel(new BorderLayout(0, 10));
        khungDanhSach.setOpaque(true);
        khungDanhSach.setBackground(MauUngDung.NEN_KEM);
        khungDanhSach.setBorder(new EmptyBorder(12, 12, 12, 12));
        if (compNorth != null) {
            khungDanhSach.add(compNorth, BorderLayout.NORTH);
        }
        if (compCenter != null) {
            khungDanhSach.add(compCenter, BorderLayout.CENTER);
        }
        khungSoDoPhong.setBorder(new EmptyBorder(12, 12, 12, 12));

        tabWorkspace.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabWorkspace.addTab("Sơ đồ phòng lưu trú", khungSoDoPhong);
        tabWorkspace.addTab("Hợp đồng lưu trú", khungDanhSach);
        tabWorkspace.setSelectedIndex(TAB_SO_DO);
        tabWorkspace.addChangeListener(
                e -> {
                    if (tabWorkspace.getSelectedIndex() == TAB_SO_DO) {
                        khungSoDoPhong.lamMoiSoDo();
                    } else {
                        lamMoi();
                        capNhatThaoTac();
                    }
                    Window w = SwingUtilities.getWindowAncestor(this);
                    if (w instanceof ChinhFrame cf) {
                        cf.capNhatThanhCongCu();
                    }
                });
        add(tabWorkspace, BorderLayout.CENTER);
        capNhatSoLuongTrangThai();
        apDungBoLocBangLuuTru();
    }

    // 3. VÒNG ĐỜI, THANH CÔNG CỤ & BỘ LỌC BẢNG

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        if (tabWorkspace.getSelectedIndex() == TAB_SO_DO && khungSoDoPhong != null) {
            return khungSoDoPhong.layKhaNangThanhCongCu();
        }
        return new KhaNangThanhCongCu(true, false, layOTimKiem() != null, true, false, true);
    }

    @Override
    public void xuLyTaoMoi() {
        if (tabWorkspace.getSelectedIndex() == TAB_SO_DO && khungSoDoPhong != null) {
            khungSoDoPhong.xuLyTaoMoi();
        } else {
            moDatPhongVaTaiLai();
        }
    }

    @Override
    public void xuLyTimKiem() {
        if (tabWorkspace.getSelectedIndex() == TAB_SO_DO && khungSoDoPhong != null) {
            khungSoDoPhong.xuLyTimKiem();
        } else if (layOTimKiem() != null) {
            layOTimKiem().requestFocusInWindow();
            layOTimKiem().selectAll();
        }
    }

    @Override
    public void xuLyIn() {
        if (tabWorkspace.getSelectedIndex() == TAB_SO_DO && khungSoDoPhong != null) {
            khungSoDoPhong.xuLyIn();
        } else {
            super.xuLyIn();
        }
    }

    @Override
    protected void datLaiBoLoc() {
        dangDatLaiBoLoc = true;
        cboTrangThai.setSelectedIndex(0);
        dangDatLaiBoLoc = false;
        apDungBoLocBangLuuTru();
    }

    private void capNhatSoLuongTrangThai() {
        capNhatSoLuongMucLoc(cacMucTrangThai, cboTrangThai, 9, TAT_CA);
    }

    private void apDungBoLocBangLuuTru() {
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
                                && !mucTrangThai.equalsIgnoreCase(entry.getStringValue(9))) {
                            return false;
                        }
                        if (tuKhoa.isEmpty()) {
                            return true;
                        }
                        for (int c = 0; c < entry.getValueCount(); c++) {
                            if (entry.getStringValue(c).toLowerCase(Locale.ROOT).contains(tuKhoa)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    @Override
    public void xuLyHienThi() {
        xuLyDieuHuongTuSoDo();
        if (tabWorkspace.getSelectedIndex() == TAB_SO_DO && khungSoDoPhong != null) {
            khungSoDoPhong.lamMoiSoDo();
        } else {
            super.xuLyHienThi();
        }
        capNhatTrangThaiNutDonPhong();
    }

    private void xuLyDieuHuongTuSoDo() {
        var yeuCau = NguCanhMoBanGhi.lay();
        if (yeuCau != null
                && (yeuCau.manHinh() == MaManHinh.DAT_PHONG_LUU_TRU
                        || yeuCau.manHinh() == MaManHinh.QUAN_LY_LUU_TRU)) {
            moDatPhongTuNguCanh(yeuCau.ma());
            return;
        }
        if (yeuCau != null && yeuCau.manHinh() == MaManHinh.NHAT_KY_CHAM_SOC) {
            moNhatKyTuNguCanh(yeuCau.ma());
            return;
        }
        Object manHinh = getClientProperty("maManHinh");
        if (MaManHinh.DAT_PHONG_LUU_TRU.equals(manHinh)
                && !layBanHang().getQuyTrinh().getMaPhongHotel().isBlank()) {
            tabWorkspace.setSelectedIndex(TAB_SO_DO);
            SwingUtilities.invokeLater(
                    () -> {
                        moDatPhongVaTaiLai();
                        layBanHang().getQuyTrinh().chonPhongHotel("");
                    });
        } else if (MaManHinh.NHAT_KY_CHAM_SOC.equals(manHinh)
                && !layBanHang().getQuyTrinh().getHopDongHotel().isBlank()) {
            tabWorkspace.setSelectedIndex(TAB_DANH_SACH);
            maHopDongChoMoNhatKy = layBanHang().getQuyTrinh().getHopDongHotel();
        }
    }

    private void moDatPhongTuNguCanh(String maPhong) {
        NguCanhMoBanGhi.xoa();
        tabWorkspace.setSelectedIndex(TAB_SO_DO);
        if (maPhong != null && !maPhong.isBlank()) {
            layBanHang().getQuyTrinh().chonPhongHotel(maPhong);
            SwingUtilities.invokeLater(
                    () -> {
                        moDatPhongVaTaiLai();
                        layBanHang().getQuyTrinh().chonPhongHotel("");
                    });
        }
    }

    private void moNhatKyTuNguCanh(String maHd) {
        NguCanhMoBanGhi.xoa();
        tabWorkspace.setSelectedIndex(TAB_DANH_SACH);
        if (maHd != null && !maHd.isBlank()) {
            layBanHang().getQuyTrinh().chonHotel(maHd);
            maHopDongChoMoNhatKy = maHd;
        }
    }

    @Override
    protected void sauKhiTaiLai() {
        super.sauKhiTaiLai();
        capNhatTrangThaiNutDonPhong();
        if (tabWorkspace != null) {
            capNhatSoLuongTrangThai();
            apDungBoLocBangLuuTru();
        }
        if (tabWorkspace != null
                && tabWorkspace.getSelectedIndex() == TAB_SO_DO
                && khungSoDoPhong != null) {
            khungSoDoPhong.lamMoiSoDo();
        }
        String maChon =
                maHopDongChoMoNhatKy != null
                        ? maHopDongChoMoNhatKy
                        : layBanHang().getQuyTrinh().getHopDongHotel();
        if (maChon != null && !maChon.isBlank()) {
            chonHopDongTheoMa(maChon);
        }
        if (maHopDongChoMoNhatKy != null) {
            String maMo = maHopDongChoMoNhatKy;
            maHopDongChoMoNhatKy = null;
            SwingUtilities.invokeLater(
                    () -> {
                        chonHopDongTheoMa(maMo);
                        NhatKyChamSocDialog.mo(this, "HOTEL", maMo);
                        taiLaiNen();
                    });
        }
    }

    @Override
    public void xuLyLamMoi() {
        if (tabWorkspace.getSelectedIndex() == TAB_SO_DO && khungSoDoPhong != null) {
            khungSoDoPhong.xuLyLamMoi();
        } else {
            super.xuLyLamMoi();
        }
        capNhatTrangThaiNutDonPhong();
    }

    private void chonHopDongTheoMa(String maHopDong) {
        if (maHopDong == null || maHopDong.isBlank()) return;
        for (int i = 0; i < layMoHinh().getRowCount(); i++) {
            Object val = layMoHinh().getValueAt(i, 0);
            if (maHopDong.equals(String.valueOf(val))) {
                int viewRow = layBangDuLieu().convertRowIndexToView(i);
                if (viewRow >= 0) {
                    layBangDuLieu().setRowSelectionInterval(viewRow, viewRow);
                    layBangDuLieu()
                            .scrollRectToVisible(layBangDuLieu().getCellRect(viewRow, 0, true));
                }
                return;
            }
        }
    }

    // 4. MENU NGỮ CẢNH & ĐIỀU PHỐI THAO TÁC

    private void hienThiMenuThaoTacKhac(Component neo) {
        int row = layBangDuLieu().getSelectedRow();
        String trangThai = layTrangThaiDongDangChon();
        JPopupMenu popup = taoMenuNguCanh(trangThai, row >= 0, false);
        popup.show(neo, 0, neo.getHeight() + 2);
    }

    private JPopupMenu taoMenuNguCanh(String trangThai, boolean coChon, boolean dayDu) {
        JPopupMenu popup = new JPopupMenu();
        themMucTheoTrangThai(popup, trangThai, dayDu);
        if (coChon && TrangThaiHopDong.DA_TRA_PHONG.maDB().equals(trangThai)) {
            JMenuItem itemNhatKy = new JMenuItem("Xem nhật ký chăm sóc");
            itemNhatKy.addActionListener(e -> chayThaoTac(this::layNhatKyChiTiet));
            popup.add(itemNhatKy);
        }
        JMenuItem itemLichSuCoc = new JMenuItem("Lịch sử thu cọc");
        itemLichSuCoc.setEnabled(coChon);
        itemLichSuCoc.addActionListener(e -> chayThaoTac(this::lichSuCoc));
        popup.add(itemLichSuCoc);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        if (phien != null && phien.docVaiTro() == VaiTro.QUAN_LY_CUA_HANG) {
            JMenuItem itemDieuChinhCoc = new JMenuItem("Điều chỉnh mức cọc");
            itemDieuChinhCoc.setEnabled(coChon && TrangThaiHopDong.DA_DAT.maDB().equals(trangThai));
            itemDieuChinhCoc.addActionListener(e -> chayThaoTac(this::doiCoc));
            popup.add(itemDieuChinhCoc);
        }
        if (!coChon || TrangThaiHopDong.DA_DAT.maDB().equals(trangThai)) {
            popup.addSeparator();
            JMenuItem itemSua = new JMenuItem("Sửa thông tin đặt phòng");
            itemSua.setEnabled(coChon && TrangThaiHopDong.DA_DAT.maDB().equals(trangThai));
            itemSua.addActionListener(e -> chayThaoTacVaTaiLai(this::suaDatPhong));
            popup.add(itemSua);
            JMenuItem itemHuy = new JMenuItem("Hủy đặt phòng");
            itemHuy.setEnabled(
                    coChon && TrangThaiHopDong.DA_DAT.maDB().equals(trangThai) && cocDaThu() == 0);
            itemHuy.addActionListener(e -> chayThaoTac(this::huyDatPhong));
            popup.add(itemHuy);
        }
        if (!dayDu) {
            popup.addSeparator();
            JMenuItem itemDonPhong = new JMenuItem("Xác nhận dọn phòng xong");
            itemDonPhong.setEnabled(coPhongDangDon);
            itemDonPhong.addActionListener(e -> chayThaoTac(() -> moDonPhong(null)));
            popup.add(itemDonPhong);
        }
        return popup;
    }

    private void themMucTheoTrangThai(JPopupMenu popup, String trangThai, boolean dayDu) {
        if (dayDu && TrangThaiHopDong.DA_DAT.maDB().equals(trangThai)) {
            JMenuItem itemNhanPhong = new JMenuItem("Nhận phòng (Check-in)");
            itemNhanPhong.setEnabled(cocConThieu() <= 0);
            itemNhanPhong.addActionListener(e -> chayThaoTac(this::nhanPhong));
            popup.add(itemNhanPhong);
            JMenuItem itemThuCoc = new JMenuItem("Thu cọc phòng");
            itemThuCoc.setEnabled(cocConThieu() > 0);
            itemThuCoc.addActionListener(e -> chayThaoTac(this::thuCoc));
            popup.add(itemThuCoc);
            popup.addSeparator();
        } else if (TrangThaiHopDong.DANG_O.maDB().equals(trangThai)) {
            if (dayDu) {
                JMenuItem itemPhieuCS = new JMenuItem("Phiếu chăm sóc (Xem / Thêm)");
                itemPhieuCS.addActionListener(
                        e ->
                                chayThaoTacVaTaiLai(
                                        () ->
                                                NhatKyChamSocDialog.mo(
                                                        this, "HOTEL", layDongChon())));
                popup.add(itemPhieuCS);
            }
            JMenuItem itemGiaHan = new JMenuItem("Gia hạn phòng");
            itemGiaHan.addActionListener(e -> chayThaoTacVaTaiLai(this::suaDatPhong));
            popup.add(itemGiaHan);
            if (dayDu) {
                JMenuItem itemTraPhong = new JMenuItem("Trả phòng & Quyết toán (POS)");
                itemTraPhong.setEnabled(coQuyenThanhToan());
                itemTraPhong.addActionListener(e -> chayThaoTac(this::quyetToanHopDongAnToan));
                popup.add(itemTraPhong);
                popup.addSeparator();
            }
        }
    }

    private void chayThaoTac(Runnable thaoTac) {
        try {
            thaoTac.run();
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    private void chayThaoTacVaTaiLai(Runnable thaoTac) {
        try {
            thaoTac.run();
            taiLaiNen();
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }
}
