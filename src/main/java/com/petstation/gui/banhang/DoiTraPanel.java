package com.petstation.gui.banhang;

import com.petstation.dto.ChiTietXemTruocPhieuDoi;
import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.chung.DieuKienTaiDuLieu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Đổi trả hàng hóa (Mã: DOI_TRA)
// Luồng xử lý: GUI -> DoiTraService, DonDoiTreoService -> PhieuDoiTraDAOImpl -> SQL
// (phieu_doi_tra, chi_tiet_doi_tra)
// Mô tả: Tiếp nhận và xử lý yêu cầu đổi phụ kiện, lọc trạng thái và xem chi tiết phiếu đổi trả
// Phân hệ: banhang
public final class DoiTraPanel extends BangNghiepVuPanel {
    private static final int COT_HOA_DON_GOC = 2;
    private static final int COT_HOA_DON_THAY_THE = 3;
    private static final int COT_TRANG_THAI = 7;

    private final NutDinhDang btnXemGoc;
    private final NutDinhDang btnXemThayThe;
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả trạng thái",
                        "Hoàn tất đổi hàng",
                        "Chờ xử lý đổi hàng",
                        "Đã hủy đổi hàng",
                        "Lịch sử hoàn tiền"
                    });
    private final JLabel nhanTieuDeChiTiet = new JLabel("Chọn phiếu đổi để xem chi tiết");
    private final ChiTietPhieuDoiPanel vungChiTiet = new ChiTietPhieuDoiPanel();
    private int luotXemChiTiet;

    public DoiTraPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.DOI_TRA, banHang, dieuHuong);
        themNut(layKhungThaoTac(), "+ Đổi phụ kiện (F7)", NutDinhDang.KieuNut.CHINH, this::traHang);
        btnXemGoc =
                themNut(
                        layKhungThaoTac(),
                        "Xem hóa đơn gốc",
                        NutDinhDang.KieuNut.PHU,
                        () -> xemBienLaiTheoCot(COT_HOA_DON_GOC, "hóa đơn gốc"));
        btnXemThayThe =
                themNut(
                        layKhungThaoTac(),
                        "Xem HĐ thay thế",
                        NutDinhDang.KieuNut.PHU,
                        () -> xemBienLaiTheoCot(COT_HOA_DON_THAY_THE, "hóa đơn thay thế"));
        cauHinhBoLocDoiTra();
        ganKhungXemTruocChiTiet(taoKhungXemTruocChiTiet());
        dangKySuKienDoiTra();
        capNhatTrangThaiChon();
        SwingUtilities.invokeLater(this::capNhatXemTruocChiTiet);
    }

    private void cauHinhBoLocDoiTra() {
        if (layKhungThaoTac() instanceof ThanhDanhSach thanh) {
            thanh.themBoLoc("Trạng thái:", cboTrangThai);
        } else {
            layKhungThaoTac().add(cboTrangThai);
        }
        cboTrangThai.addActionListener(e -> apDungBoLoc());
        if (Boolean.TRUE.equals(layOTimKiem().getClientProperty("locDoiTra"))) return;
        layOTimKiem().putClientProperty("locDoiTra", true);
        themBoLangNgheTaiLieu(layOTimKiem(), this::apDungBoLoc);
    }

    @Override
    protected void lienKetBoLoc(JTextField timKiem, JTable bang) {
        // Đổi trả dùng bộ lọc kết hợp từ khóa + cboTrangThai trong apDungBoLoc()
    }

    @Override
    protected void datLaiBoLoc() {
        if (cboTrangThai != null) {
            cboTrangThai.setSelectedIndex(0);
        }
        apDungBoLoc();
    }

    @Override
    protected void sauKhiTaiLai() {
        apDungBoLoc();
        if (layBangDuLieu().getSelectedRow() < 0 && layBangDuLieu().getRowCount() > 0) {
            layBangDuLieu().setRowSelectionInterval(0, 0);
        }
        capNhatTrangThaiChon();
        capNhatXemTruocChiTiet();
    }

    private void apDungBoLoc() {
        if (!(layBangDuLieu().getRowSorter() instanceof TableRowSorter<?> rawSorter)) return;
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) rawSorter;
        String tuKhoa = layOTimKiem().getText().trim();
        String tuKhoaThuong =
                tuKhoa.isEmpty() || "Tìm trong danh sách...".equals(tuKhoa)
                        ? ""
                        : tuKhoa.toLowerCase(Locale.ROOT);
        String trangThaiChon =
                cboTrangThai.getSelectedIndex() > 0
                        ? String.valueOf(cboTrangThai.getSelectedItem())
                        : "";
        sorter.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(
                            Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        if (!trangThaiChon.isEmpty()) {
                            String tt = String.valueOf(entry.getValue(COT_TRANG_THAI));
                            if (!trangThaiChon.equals(tt)) return false;
                        }
                        if (tuKhoaThuong.isEmpty()) return true;
                        for (int c = 1; c < entry.getValueCount(); c++) {
                            Object val = entry.getValue(c);
                            if (val != null
                                    && val.toString()
                                            .toLowerCase(Locale.ROOT)
                                            .contains(tuKhoaThuong)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    private JPanel taoKhungXemTruocChiTiet() {
        BoGocPanel khungPhai = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        khungPhai.setLayout(new BorderLayout(0, 8));
        khungPhai.setPreferredSize(new Dimension(380, 0));
        khungPhai.setMinimumSize(new Dimension(310, 0));
        khungPhai.setBorder(new EmptyBorder(12, 12, 12, 12));
        nhanTieuDeChiTiet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanTieuDeChiTiet.setForeground(MauUngDung.VAN_BAN_TOI);
        khungPhai.add(nhanTieuDeChiTiet, BorderLayout.NORTH);
        JScrollPane scr =
                new JScrollPane(
                        vungChiTiet,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(BorderFactory.createEmptyBorder());
        scr.getViewport().setBackground(MauUngDung.THE_NEN);
        scr.getVerticalScrollBar().setUnitIncrement(16);
        khungPhai.add(scr, BorderLayout.CENTER);
        return khungPhai;
    }

    private void ganKhungXemTruocChiTiet(JPanel khungPhai) {
        BorderLayout rootLayout = (BorderLayout) getLayout();
        Component centerComp = rootLayout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp == null) return;
        remove(centerComp);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerComp, khungPhai);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(1.0);
        splitPane.setContinuousLayout(true);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    private void dangKySuKienDoiTra() {
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) {
                                capNhatTrangThaiChon();
                                capNhatXemTruocChiTiet();
                            }
                        });
        ganPhimTat(KeyEvent.VK_F7, this::traHang);
        ganSuKienDupChuot(() -> xemBienLaiTheoCot(COT_HOA_DON_GOC, "hóa đơn gốc"));
        ganMenuChuotPhai(
                row -> {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem itemDoi = new JMenuItem("+ Đổi phụ kiện mới");
                    itemDoi.addActionListener(e -> traHang());
                    popup.add(itemDoi);
                    popup.addSeparator();
                    JMenuItem itemGoc = new JMenuItem("Xem hóa đơn gốc");
                    itemGoc.addActionListener(
                            e -> xemBienLaiTheoCot(COT_HOA_DON_GOC, "hóa đơn gốc"));
                    popup.add(itemGoc);
                    if (btnXemThayThe.isEnabled()) {
                        JMenuItem itemThayThe = new JMenuItem("Xem hóa đơn thay thế");
                        itemThayThe.addActionListener(
                                e -> xemBienLaiTheoCot(COT_HOA_DON_THAY_THE, "hóa đơn thay thế"));
                        popup.add(itemThayThe);
                    }
                    return popup;
                });
    }

    private void capNhatTrangThaiChon() {
        int row = layBangDuLieu().getSelectedRow();
        boolean coChon = row >= 0;
        btnXemGoc.setEnabled(coChon);
        if (!coChon) {
            btnXemThayThe.setEnabled(false);
            return;
        }
        int modelRow = layBangDuLieu().convertRowIndexToModel(row);
        String maThayThe = String.valueOf(layMoHinh().getValueAt(modelRow, COT_HOA_DON_THAY_THE));
        btnXemThayThe.setEnabled(
                maThayThe != null && !maThayThe.isBlank() && !"—".equals(maThayThe.trim()));
    }

    private void capNhatXemTruocChiTiet() {
        int row = layBangDuLieu().getSelectedRow();
        if (row < 0) {
            ++luotXemChiTiet;
            nhanTieuDeChiTiet.setText("Chọn phiếu đổi để xem chi tiết");
            vungChiTiet.hienThi(null);
            return;
        }
        int modelRow = layBangDuLieu().convertRowIndexToModel(row);
        String maPhieu = String.valueOf(layMoHinh().getValueAt(modelRow, 1));
        int luot = ++luotXemChiTiet;
        nhanTieuDeChiTiet.setText("Phiếu " + maPhieu + " (đang tải...)");
        if (!DieuKienTaiDuLieu.coTheTachLuong()) {
            hienThiChiTiet(
                    maPhieu, new DoiTraService().layChiTietXemTruocPhieu(maPhieu).orElse(null));
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotXemChiTiet,
                () -> new DoiTraService().layChiTietXemTruocPhieu(maPhieu).orElse(null),
                chiTiet -> hienThiChiTiet(maPhieu, chiTiet),
                ex -> nhanTieuDeChiTiet.setText("Lỗi tải chi tiết phiếu " + maPhieu));
    }

    private void hienThiChiTiet(String maPhieu, ChiTietXemTruocPhieuDoi dl) {
        nhanTieuDeChiTiet.setText("Chi tiết đổi trả");
        vungChiTiet.hienThi(dl);
    }

    private void xemBienLaiTheoCot(int cot, String moTa) {
        if (layBangDuLieu().getSelectedRow() < 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu để xem " + moTa + ".");
            return;
        }
        String maHoaDon = layNoiDungDongChon(cot);
        if (maHoaDon.isBlank() || "—".equals(maHoaDon.trim())) {
            HopThoai.hienThiCanhBao(this, "Phiếu chưa có " + moTa + ".");
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> new BanHangService().layBienLai(maHoaDon),
                bienLai -> {
                    layBanHang().setBienLaiCuoi(bienLai);
                    layDieuHuong().accept(MaManHinh.IN_HOA_DON);
                });
    }

    private void traHang() {
        new DoiPhuKienDialog(this, layBanHang(), layDieuHuong()).setVisible(true);
        lamMoi();
    }
}
