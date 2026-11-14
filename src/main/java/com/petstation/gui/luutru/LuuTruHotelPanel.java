package com.petstation.gui.luutru;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

// Màn hình: Quản lý lưu trú Hotel (Mã: LUU_TRU_HOTEL)
// Luồng xử lý: GUI -> LuuTruService, ChamSocService -> HopDongLuuTruDAOImpl -> SQL
// (hop_dong_luu_tru, phong_hotel)
// Mô tả: Tiếp nhận phòng, đặt cọc lưu trú, theo dõi nhật ký chăm sóc và trả phòng
// Phân hệ: luutru
public final class LuuTruHotelPanel extends NghiepVuHotelPanel {
    private final JTabbedPane tabWorkspace = new JTabbedPane();
    private TraCuuPhongHotelPanel khungSoDoPhong;

    public LuuTruHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOP_DONG_LUU_TRU, banHang, dieuHuong);
        cauHinhThanhThaoTacLuuTru();
        ganSuKienBangLuuTru();
        khungSoDoPhong = taoSoDoPhongLuuTru(banHang, dieuHuong);
        xayDungTabLamViec();
    }

    private void cauHinhThanhThaoTacLuuTru() {
        themNut(
                layKhungThaoTac(),
                "+ Đặt phòng (F6)",
                NutDinhDang.KieuNut.CHINH,
                this::moDatPhongVaTaiLai);
        NutDinhDang btnNhanPhong = new NutDinhDang("Nhận phòng", NutDinhDang.KieuNut.THANH_CONG);
        btnNhanPhong.addActionListener(e -> chayThaoTac(this::nhanPhong));
        layKhungThaoTac().add(btnNhanPhong);
        NutDinhDang btnQuyetToan = new NutDinhDang("Quyết toán", NutDinhDang.KieuNut.THANH_CONG);
        btnQuyetToan.addActionListener(e -> chayThaoTac(() -> quyetToan("HOTEL")));
        layKhungThaoTac().add(btnQuyetToan);
        NutDinhDang btnDonPhong = new NutDinhDang("Dọn phòng xong", NutDinhDang.KieuNut.TRONG_SUOT);
        btnDonPhong.addActionListener(e -> chayThaoTac(() -> moDonPhong(null)));
        layKhungThaoTac().add(btnDonPhong);
        NutDinhDang btnThaoTacKhac = new NutDinhDang("Thao tác khác...", NutDinhDang.KieuNut.PHU);
        btnThaoTacKhac.addActionListener(e -> hienThiMenuThaoTacKhac(btnThaoTacKhac));
        layKhungThaoTac().add(btnThaoTacKhac);
        ganPhimTat(KeyEvent.VK_F6, this::moDatPhongVaTaiLai);
    }

    private void nhanPhong() {
        String ma = layDongChon();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.kiemTraVao(ma),
                () -> {
                    taiLaiNen();
                    if (khungSoDoPhong != null) khungSoDoPhong.lamMoiSoDo();
                });
    }

    private void moDatPhongVaTaiLai() {
        datPhong();
        taiLaiNen();
    }

    private void ganSuKienBangLuuTru() {
        ganSuKienDupChuot(
                () -> {
                    int row = layBangDuLieu().getSelectedRow();
                    if (row < 0) return;
                    int dong = layBangDuLieu().convertRowIndexToModel(row);
                    String trangThai = String.valueOf(layMoHinh().getValueAt(dong, 9));
                    if (TrangThaiHopDong.DA_DAT.maDB().equals(trangThai)) {
                        if (cocConThieu() <= 0) {
                            chayThaoTac(this::nhanPhong);
                        } else {
                            thuCoc();
                        }
                    } else if (TrangThaiHopDong.DANG_O.maDB().equals(trangThai)
                            || TrangThaiHopDong.DA_TRA_PHONG.maDB().equals(trangThai)) {
                        layNhatKyChiTiet();
                    }
                });
        ganMenuChuotPhai(
                row -> {
                    int dong = layBangDuLieu().convertRowIndexToModel(row);
                    return taoMenuNguCanh(String.valueOf(layMoHinh().getValueAt(dong, 9)), true);
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
                        tabWorkspace.setSelectedIndex(0);
                        chonHopDongTheoMa(maHd);
                    }
                });
    }

    private void xuLyChonPhongTrong(PhongHotel phong) {
        try {
            layBanHang().getQuyTrinh().chonPhongHotel(phong.getMaPhong());
            String hdTruoc = layBanHang().getQuyTrinh().getHopDongHotel();
            moDatPhongVaTaiLai();
            khungSoDoPhong.lamMoiSoDo();
            String hdSau = layBanHang().getQuyTrinh().getHopDongHotel();
            if (!hdSau.equals(hdTruoc) && !hdSau.isBlank()) {
                tabWorkspace.setSelectedIndex(0);
            }
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    private void xayDungTabLamViec() {
        Component compNorth = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component compCenter = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        removeAll();
        JPanel khungDanhSach = new JPanel(new BorderLayout(0, 12));
        khungDanhSach.setOpaque(false);
        khungDanhSach.setBorder(new EmptyBorder(12, 0, 0, 0));
        if (compNorth != null) khungDanhSach.add(compNorth, BorderLayout.NORTH);
        if (compCenter != null) khungDanhSach.add(compCenter, BorderLayout.CENTER);
        khungSoDoPhong.setBorder(new EmptyBorder(12, 0, 0, 0));
        tabWorkspace.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabWorkspace.addTab("Danh sách hợp đồng lưu trú", khungDanhSach);
        tabWorkspace.addTab("Sơ đồ phòng lưu trú", khungSoDoPhong);
        tabWorkspace.addChangeListener(
                e -> {
                    if (tabWorkspace.getSelectedIndex() == 0) {
                        lamMoi();
                    } else {
                        khungSoDoPhong.lamMoiSoDo();
                    }
                });
        add(tabWorkspace, BorderLayout.CENTER);
    }

    @Override
    public void xuLyHienThi() {
        if (tabWorkspace.getSelectedIndex() == 1 && khungSoDoPhong != null) {
            khungSoDoPhong.lamMoiSoDo();
        } else {
            super.xuLyHienThi();
        }
    }

    @Override
    public void xuLyLamMoi() {
        if (tabWorkspace.getSelectedIndex() == 1 && khungSoDoPhong != null) {
            khungSoDoPhong.xuLyLamMoi();
        } else {
            super.xuLyLamMoi();
        }
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

    private void hienThiMenuThaoTacKhac(Component neo) {
        int row = layBangDuLieu().getSelectedRow();
        String trangThai =
                row < 0
                        ? ""
                        : String.valueOf(
                                layMoHinh()
                                        .getValueAt(
                                                layBangDuLieu().convertRowIndexToModel(row), 9));
        JPopupMenu popup = taoMenuNguCanh(trangThai, row >= 0);
        popup.show(neo, 0, neo.getHeight() + 2);
    }

    private JPopupMenu taoMenuNguCanh(String trangThai, boolean coChon) {
        JPopupMenu popup = new JPopupMenu();
        if (TrangThaiHopDong.DA_DAT.maDB().equals(trangThai)) {
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
            JMenuItem itemPhieuCS = new JMenuItem("Nhập phiếu chăm sóc hôm nay");
            itemPhieuCS.addActionListener(
                    e ->
                            chayThaoTacVaTaiLai(
                                    () -> NhatKyChamSocDialog.mo(this, "HOTEL", layDongChon())));
            popup.add(itemPhieuCS);
            JMenuItem itemTraPhong = new JMenuItem("Trả phòng & Quyết toán (POS)");
            itemTraPhong.addActionListener(e -> chayThaoTac(() -> quyetToan("HOTEL")));
            popup.add(itemTraPhong);
            popup.addSeparator();
        }
        JMenuItem itemNhatKy = new JMenuItem("Xem nhật ký chăm sóc");
        itemNhatKy.setEnabled(
                TrangThaiHopDong.DANG_O.maDB().equals(trangThai)
                        || TrangThaiHopDong.DA_TRA_PHONG.maDB().equals(trangThai));
        itemNhatKy.addActionListener(e -> chayThaoTac(this::layNhatKyChiTiet));
        popup.add(itemNhatKy);
        JMenuItem itemLichSuCoc = new JMenuItem("Lịch sử thu cọc");
        itemLichSuCoc.setEnabled(coChon);
        itemLichSuCoc.addActionListener(e -> chayThaoTac(this::lichSuCoc));
        popup.add(itemLichSuCoc);
        if (PhienDangNhapService.macDinh().layPhien().docVaiTro() == VaiTro.QUAN_LY_CUA_HANG) {
            JMenuItem itemDieuChinhCoc = new JMenuItem("Điều chỉnh mức cọc");
            itemDieuChinhCoc.setEnabled(TrangThaiHopDong.DA_DAT.maDB().equals(trangThai));
            itemDieuChinhCoc.addActionListener(e -> chayThaoTac(this::doiCoc));
            popup.add(itemDieuChinhCoc);
        }
        popup.addSeparator();
        JMenuItem itemSua = new JMenuItem("Sửa thông tin đặt phòng");
        itemSua.setEnabled(TrangThaiHopDong.DA_DAT.maDB().equals(trangThai));
        itemSua.addActionListener(e -> chayThaoTacVaTaiLai(this::suaDatPhong));
        popup.add(itemSua);
        JMenuItem itemHuy = new JMenuItem("Hủy đặt phòng");
        itemHuy.setEnabled(TrangThaiHopDong.DA_DAT.maDB().equals(trangThai) && cocDaThu() == 0);
        itemHuy.addActionListener(e -> chayThaoTac(this::huyDatPhong));
        popup.add(itemHuy);
        return popup;
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
