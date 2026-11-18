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
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.banhang.ChiTietDoiTra;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.PhieuDoiTra;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
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
    private final JEditorPane vungChiTiet = new JEditorPane("text/html", "");
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
        vungChiTiet.setEditable(false);
        vungChiTiet.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        vungChiTiet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        vungChiTiet.setBackground(MauUngDung.THE_NEN);
        JScrollPane scr =
                new JScrollPane(
                        vungChiTiet,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_NHE));
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
            vungChiTiet.setText("");
            return;
        }
        int modelRow = layBangDuLieu().convertRowIndexToModel(row);
        String maPhieu = String.valueOf(layMoHinh().getValueAt(modelRow, 1));
        int luot = ++luotXemChiTiet;
        nhanTieuDeChiTiet.setText("Phiếu " + maPhieu + " (đang tải...)");
        if (!DieuKienTaiDuLieu.coTheTachLuong()) {
            hienThiXemTruocHtml(
                    maPhieu, new DoiTraService().layChiTietXemTruocPhieu(maPhieu).orElse(null));
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotXemChiTiet,
                () -> new DoiTraService().layChiTietXemTruocPhieu(maPhieu).orElse(null),
                chiTiet -> hienThiXemTruocHtml(maPhieu, chiTiet),
                ex -> nhanTieuDeChiTiet.setText("Lỗi tải chi tiết phiếu " + maPhieu));
    }

    private void hienThiXemTruocHtml(String maPhieu, ChiTietXemTruocPhieuDoi dl) {
        if (dl == null || dl.getPhieu() == null) {
            nhanTieuDeChiTiet.setText("Phiếu " + maPhieu);
            vungChiTiet.setText("<html><body><p>Không có dữ liệu chi tiết.</p></body></html>");
            return;
        }
        PhieuDoiTra p = dl.getPhieu();
        nhanTieuDeChiTiet.setText("Chi tiết phiếu " + p.getMaPhieu());
        StringBuilder sb = new StringBuilder("<html><body style='font-family:sans-serif'>");
        themDongHtml(
                sb, "Hóa đơn gốc", p.getHoaDonGoc() != null ? p.getHoaDonGoc().getMaHoaDon() : "");
        themDongHtml(
                sb,
                "Hóa đơn thay thế",
                p.getHoaDonThayThe() != null ? p.getHoaDonThayThe().getMaHoaDon() : "—");
        themDongHtml(
                sb, "Nhân viên lập", p.getNguoiLap() != null ? p.getNguoiLap().getHoTen() : "");
        themDongHtml(sb, "Trạng thái", DinhDangBang.tenTrangThai(p.getTrangThai()));
        themDongHtml(sb, "Giá trị hàng đổi", DinhDang.dinhDang(p.getGiaTriDuocDoi()));
        themDongHtml(sb, "Chênh lệch", DinhDang.dinhDang(p.getChenhLech()));
        themDongHtml(sb, "Lý do", p.getLyDo());
        sb.append("<hr><b>Hàng khách trả lại:</b><ul>");
        List<ChiTietDoiTra> tra = dl.getHangTra();
        if (tra.isEmpty()) {
            sb.append("<li>Không có chi tiết hàng trả</li>");
        } else {
            for (ChiTietDoiTra c : tra) {
                String tenSp =
                        c.getChiTietHoaDon() != null && c.getChiTietHoaDon().getSanPham() != null
                                ? c.getChiTietHoaDon().getSanPham().getTenSanPham()
                                : "";
                String lo = c.getLoHang() != null ? c.getLoHang().getMaLo() : "";
                sb.append("<li>")
                        .append(DinhDang.thoatHtml(tenSp))
                        .append(" × ")
                        .append(c.getSoLuong())
                        .append(lo.isBlank() ? "" : " (Lô " + DinhDang.thoatHtml(lo) + ")")
                        .append(" · ")
                        .append(DinhDang.dinhDang(c.getTienHoan()))
                        .append("</li>");
            }
        }
        sb.append("</ul><b>Phụ kiện thay thế:</b><ul>");
        List<ChiTietHoaDon> moi = dl.getHangThayThe();
        if (moi.isEmpty()) {
            sb.append("<li>Chưa xuất hóa đơn thay thế</li>");
        } else {
            for (ChiTietHoaDon c : moi) {
                sb.append("<li>")
                        .append(DinhDang.thoatHtml(c.getTenHang()))
                        .append(" × ")
                        .append(c.getSoLuong())
                        .append(" · ")
                        .append(DinhDang.dinhDang(c.getDonGia() * c.getSoLuong()))
                        .append("</li>");
            }
        }
        sb.append("</ul></body></html>");
        vungChiTiet.setText(sb.toString());
        vungChiTiet.setCaretPosition(0);
    }

    private static void themDongHtml(StringBuilder sb, String nhan, String giaTri) {
        sb.append("<div><b>")
                .append(DinhDang.thoatHtml(nhan))
                .append(":</b> ")
                .append(DinhDang.thoatHtml(giaTri))
                .append("</div>");
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
