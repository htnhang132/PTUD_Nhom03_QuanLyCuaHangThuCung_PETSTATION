package com.petstation.gui.kho;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiLoHang;
import com.petstation.service.chung.NghiepVuService;

import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Tra cứu tồn theo lô (Mã: TIM_KIEM_TON_THEO_LO)
// Luồng xử lý: GUI -> SanPhamService -> SanPhamDAOImpl -> SQL (lo_hang, san_pham)
// Mô tả: Tra cứu số lượng tồn khả dụng, hạn sử dụng theo từng lô hàng cụ thể theo nguyên tắc FEFO
// Phân hệ: kho
public final class TimKiemTonTheoLoPanel extends BangNghiepVuPanel {
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(new String[] {"Tất cả", "Còn hạn", "Sắp hết hạn", "Hết hạn"});

    public TimKiemTonTheoLoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        this(banHang, dieuHuong, new NghiepVuService());
    }

    public TimKiemTonTheoLoPanel(
            GioHang banHang, Consumer<MaManHinh> dieuHuong, NghiepVuService nghiepVuService) {
        super(LoaiNghiepVu.LO_HANG, banHang, dieuHuong, nghiepVuService);
        if (layKhungThaoTac() instanceof ThanhDanhSach thanh) {
            thanh.themBoLoc("Trạng thái:", cboTrangThai);
        } else {
            layKhungThaoTac().add(cboTrangThai);
        }
        thayBoLangNgheTimKiem(this::loc);
        cboTrangThai.addActionListener(e -> loc());
    }

    @Override
    protected void datLaiBoLoc() {
        layOTimKiem().setText("");
        if (cboTrangThai != null) cboTrangThai.setSelectedIndex(0);
        loc();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (cboTrangThai != null) loc();
    }

    @SuppressWarnings("unchecked")
    private void loc() {
        if (!(layBangDuLieu().getRowSorter() instanceof TableRowSorter<?> raw)) return;
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) raw;
        String tuKhoa = layOTimKiem().getText().trim().toLowerCase(Locale.ROOT);
        String ttChon =
                cboTrangThai == null ? "Tất cả" : String.valueOf(cboTrangThai.getSelectedItem());
        sorter.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ? extends Integer> e) {
                        if (!"Tất cả".equals(ttChon)) {
                            String maTt = e.getStringValue(6);
                            String tenTt = DinhDangBang.tenTrangThai(maTt);
                            boolean khop =
                                    switch (ttChon) {
                                        case "Còn hạn" ->
                                                "Còn hạn".equalsIgnoreCase(tenTt)
                                                        || "OK".equalsIgnoreCase(maTt);
                                        case "Sắp hết hạn" ->
                                                tenTt.toLowerCase(Locale.ROOT).contains("sắp hết")
                                                        || maTt.toUpperCase(Locale.ROOT)
                                                                .startsWith("SAP_HET_HAN");
                                        case "Hết hạn" ->
                                                "Hết hạn".equalsIgnoreCase(tenTt)
                                                        || TrangThaiLoHang.HET_HAN
                                                                .maDB()
                                                                .equalsIgnoreCase(maTt);
                                        default ->
                                                ttChon.equalsIgnoreCase(maTt)
                                                        || ttChon.equalsIgnoreCase(tenTt);
                                    };
                            if (!khop) return false;
                        }
                        if (tuKhoa.isEmpty()) return true;
                        for (int i = 0; i < e.getValueCount(); i++) {
                            if (e.getStringValue(i).toLowerCase(Locale.ROOT).contains(tuKhoa)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }
}
