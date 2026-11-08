package com.petstation.gui.processing.hotel;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.search.TraCuuPhongHotelPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.LuuTruService;
import com.petstation.util.DuLieu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.*;

public final class LuuTruHotelPanel extends NghiepVuHotelPanel {
    private final JTabbedPane tabWorkspace = new JTabbedPane();
    private TraCuuPhongHotelPanel pnlSoDoPhong;

    public LuuTruHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOP_DONG_LUU_TRU, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng (F6)", NutDinhDang.KieuNut.CHINH, () -> {
            datPhong();
            taiLaiNen();
        });
        themNut(pnlCacThaoTac, "Nhận phòng", NutDinhDang.KieuNut.THANH_CONG, () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Quyết toán", NutDinhDang.KieuNut.THANH_CONG, () -> quyetToan("HOTEL"));
        themNut(pnlCacThaoTac, "Dọn phòng xong", NutDinhDang.KieuNut.TRONG_SUOT, () -> moDonPhong(0));

        NutDinhDang btnThaoTacKhac = new NutDinhDang("Thao tác khác...", NutDinhDang.KieuNut.PHU);
        btnThaoTacKhac.addActionListener(e -> hienThiMenuThaoTacKhac(btnThaoTacKhac));
        pnlCacThaoTac.add(btnThaoTacKhac);

        ganPhimTat(KeyEvent.VK_F6, () -> {
            datPhong();
            taiLaiNen();
        });

        ganSuKienDupChuot(() -> {
            int row = tblBang.getSelectedRow();
            if (row < 0) return;
            int dong = tblBang.convertRowIndexToModel(row);
            String trangThai = String.valueOf(moHinh.getValueAt(dong, 9));
            if ("DA_DAT".equals(trangThai)) {
                if (cocConThieu() <= 0) {
                    LuuTruService.kiemTraVao(layDongChon());
                } else {
                    thuCoc();
                }
            } else if ("DANG_O".equals(trangThai) || "DA_TRA_PHONG".equals(trangThai)) {
                layNhatKyChiTiet();
            }
        });

        ganMenuChuotPhai(row -> {
            int dong = tblBang.convertRowIndexToModel(row);
            String trangThai = String.valueOf(moHinh.getValueAt(dong, 9));
            return taoMenuNguCanh(trangThai, true);
        });

        Component compNorth = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component compCenter = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        removeAll();

        JPanel pnlDanhSach = new JPanel(new BorderLayout(0, 12));
        pnlDanhSach.setOpaque(false);
        if (compNorth != null) pnlDanhSach.add(compNorth, BorderLayout.NORTH);
        if (compCenter != null) pnlDanhSach.add(compCenter, BorderLayout.CENTER);

        pnlSoDoPhong = new TraCuuPhongHotelPanel(banHang, dieuHuong, phong -> {
            if ("TRONG".equals(phong.getTrangThai())) {
                try {
                    long idPhong = DuLieu.layIdCauNoi(phong, phong.getMaPhong());
                    banHang.getQuyTrinh().chonPhongHotel(idPhong, phong.getMaPhong());
                    long hdTruoc = banHang.getQuyTrinh().getHopDongHotel();
                    datPhong();
                    taiLaiNen();
                    pnlSoDoPhong.lamMoiSoDo();
                    if (banHang.getQuyTrinh().getHopDongHotel() != hdTruoc
                            && banHang.getQuyTrinh().getHopDongHotel() > 0) {
                        tabWorkspace.setSelectedIndex(0);
                    }
                } catch (RuntimeException ex) {
                    HopThoai.hienThiCanhBao(this, ex.getMessage());
                }
            } else if ("DANG_O".equals(phong.getTrangThai()) && phong.getDangO() != null) {
                long idHd = DuLieu.layIdCauNoi(phong.getDangO(), phong.getDangO().getMaHopDong());
                banHang.getQuyTrinh().chonHotel(idHd);
                tabWorkspace.setSelectedIndex(0);
                chonHopDongTheoId(idHd);
            }
        });

        tabWorkspace.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabWorkspace.addTab("Danh sách hợp đồng lưu trú", pnlDanhSach);
        tabWorkspace.addTab("Sơ đồ phòng lưu trú", pnlSoDoPhong);

        tabWorkspace.addChangeListener(e -> {
            if (tabWorkspace.getSelectedIndex() == 0) {
                lamMoi();
            } else {
                pnlSoDoPhong.lamMoiSoDo();
            }
        });

        add(tabWorkspace, BorderLayout.CENTER);
    }

    public void chonTab(int index) {
        if (index >= 0 && index < tabWorkspace.getTabCount()) {
            tabWorkspace.setSelectedIndex(index);
        }
    }

    private void chonHopDongTheoId(long id) {
        if (id <= 0) return;
        for (int i = 0; i < moHinh.getRowCount(); i++) {
            Object val = moHinh.getValueAt(i, 0);
            if (val instanceof Number n && n.longValue() == id) {
                int viewRow = tblBang.convertRowIndexToView(i);
                if (viewRow >= 0) {
                    tblBang.setRowSelectionInterval(viewRow, viewRow);
                    tblBang.scrollRectToVisible(tblBang.getCellRect(viewRow, 0, true));
                }
                return;
            }
        }
    }

    private void hienThiMenuThaoTacKhac(Component neo) {
        int row = tblBang.getSelectedRow();
        String trangThai =
                row < 0
                        ? ""
                        : String.valueOf(
                                moHinh.getValueAt(tblBang.convertRowIndexToModel(row), 9));
        JPopupMenu popup = taoMenuNguCanh(trangThai, row >= 0);
        popup.show(neo, 0, neo.getHeight() + 2);
    }

    private JPopupMenu taoMenuNguCanh(String trangThai, boolean coChon) {
        JPopupMenu popup = new JPopupMenu();
        if ("DA_DAT".equals(trangThai)) {
            JMenuItem itemNhanPhong = new JMenuItem("Nhận phòng (Check-in)");
            itemNhanPhong.setEnabled(cocConThieu() <= 0);
            itemNhanPhong.addActionListener(e -> chayThaoTac(() -> LuuTruService.kiemTraVao(layDongChon())));
            popup.add(itemNhanPhong);

            JMenuItem itemThuCoc = new JMenuItem("Thu cọc phòng");
            itemThuCoc.setEnabled(cocConThieu() > 0);
            itemThuCoc.addActionListener(e -> chayThaoTac(this::thuCoc));
            popup.add(itemThuCoc);
            popup.addSeparator();
        } else if ("DANG_O".equals(trangThai)) {
            JMenuItem itemPhieuCS = new JMenuItem("Nhập phiếu chăm sóc hôm nay");
            itemPhieuCS.addActionListener(e -> chayThaoTac(() ->
                    com.petstation.gui.components.common.NhatKyChamSocDialog.mo(this, "HOTEL", layDongChon())));
            popup.add(itemPhieuCS);

            JMenuItem itemTraPhong = new JMenuItem("Trả phòng & Quyết toán (POS)");
            itemTraPhong.addActionListener(e -> chayThaoTac(() -> quyetToan("HOTEL")));
            popup.add(itemTraPhong);
            popup.addSeparator();
        }

        JMenuItem itemNhatKy = new JMenuItem("Xem nhật ký chăm sóc");
        itemNhatKy.setEnabled("DANG_O".equals(trangThai) || "DA_TRA_PHONG".equals(trangThai));
        itemNhatKy.addActionListener(e -> chayThaoTac(this::layNhatKyChiTiet));
        popup.add(itemNhatKy);

        JMenuItem itemLichSuCoc = new JMenuItem("Lịch sử thu cọc");
        itemLichSuCoc.setEnabled(coChon);
        itemLichSuCoc.addActionListener(e -> chayThaoTac(this::lichSuCoc));
        popup.add(itemLichSuCoc);

        if (com.petstation.service.PhienDangNhapService.layPhien().docVaiTro()
                == com.petstation.model.VaiTro.QUAN_LY_CUA_HANG) {
            JMenuItem itemDieuChinhCoc = new JMenuItem("Điều chỉnh mức cọc");
            itemDieuChinhCoc.setEnabled("DA_DAT".equals(trangThai));
            itemDieuChinhCoc.addActionListener(e -> chayThaoTac(this::doiCoc));
            popup.add(itemDieuChinhCoc);
        }

        popup.addSeparator();

        JMenuItem itemSua = new JMenuItem("Sửa thông tin đặt phòng");
        itemSua.setEnabled("DA_DAT".equals(trangThai));
        itemSua.addActionListener(e -> chayThaoTac(this::suaDatPhong));
        popup.add(itemSua);

        JMenuItem itemHuy = new JMenuItem("Hủy đặt phòng");
        itemHuy.setEnabled("DA_DAT".equals(trangThai) && cocDaThu() == 0);
        itemHuy.addActionListener(e -> chayThaoTac(this::huyDatPhong));
        popup.add(itemHuy);

        return popup;
    }

    private void chayThaoTac(Runnable thaoTac) {
        try {
            thaoTac.run();
            taiLaiNen();
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }
}
