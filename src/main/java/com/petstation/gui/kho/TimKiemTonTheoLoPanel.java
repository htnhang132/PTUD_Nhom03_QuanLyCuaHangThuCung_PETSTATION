package com.petstation.gui.kho;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiLoHang;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.util.DinhDang;

import java.awt.Component;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Tra cứu tồn theo lô (Mã: TIM_KIEM_TON_THEO_LO)
// Luồng xử lý: GUI -> SanPhamService -> SanPhamDAOImpl -> SQL (lo_hang, san_pham)
// Mô tả: Tra cứu số lượng tồn khả dụng, hạn sử dụng theo từng lô hàng cụ thể theo nguyên tắc FEFO
// Phân hệ: kho
public final class TimKiemTonTheoLoPanel extends BangNghiepVuPanel {
    private static final int NGAY_CANH_BAO_MAC_DINH = 30;

    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả",
                        "Sẵn sàng",
                        "Còn hạn",
                        "Sắp hết hạn",
                        "Hết hạn",
                        "Tạm khóa",
                        "Hết hàng"
                    });

    private volatile int soNgayCanhBao;

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
        doiTenCotMaSanPham();
        cauHinhToMauCanhBaoHan();
        thayBoLangNgheTimKiem(this::loc);
        cboTrangThai.addActionListener(e -> loc());
    }

    @Override
    protected List<Object[]> layDuLieu() {
        try {
            int nguong = CaiDatService.macDinh().layHanSuDungCanhBaoSoNgay();
            if (nguong > 0) soNgayCanhBao = nguong;
        } catch (RuntimeException loi) {
            soNgayCanhBao = NGAY_CANH_BAO_MAC_DINH;
        }
        return super.layDuLieu();
    }

    private int nguongCanhBao() {
        return soNgayCanhBao > 0 ? soNgayCanhBao : NGAY_CANH_BAO_MAC_DINH;
    }

    private void doiTenCotMaSanPham() {
        var cotModel = layBangDuLieu().getColumnModel();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            TableColumn cot = cotModel.getColumn(i);
            if ("Sản phẩm".equals(cot.getHeaderValue())) {
                cot.setHeaderValue("Mã sản phẩm");
            }
        }
        layBangDuLieu().getTableHeader().repaint();
    }

    private void cauHinhToMauCanhBaoHan() {
        JTable bang = layBangDuLieu();
        TableCellRenderer trinhVeMacDinh = bang.getDefaultRenderer(Object.class);
        bang.setDefaultRenderer(
                Object.class,
                (table, value, isSelected, hasFocus, row, column) -> {
                    Component comp =
                            trinhVeMacDinh.getTableCellRendererComponent(
                                    table, value, isSelected, hasFocus, row, column);
                    int dongMoHinh = table.convertRowIndexToModel(row);
                    boolean hetHan = laLoHetHan(dongMoHinh);
                    boolean sapHetHan = !hetHan && laLoSapHetHan(dongMoHinh);
                    if (!isSelected) {
                        if (hetHan) comp.setBackground(MauUngDung.KHONG_HOP_LE_NEN);
                        else if (sapHetHan) comp.setBackground(MauUngDung.CANH_BAO_NEN);
                    }
                    if (comp instanceof JComponent jc) {
                        if (hetHan) jc.setToolTipText("Lô hàng đã hết hạn sử dụng");
                        else if (sapHetHan) {
                            jc.setToolTipText(
                                    "Lô hàng sắp hết hạn (trong " + nguongCanhBao() + " ngày)");
                        } else {
                            jc.setToolTipText(null);
                        }
                    }
                    return comp;
                });
    }

    private boolean laLoHetHan(int dongMoHinh) {
        LocalDate han = docNgay(layMoHinh().getValueAt(dongMoHinh, 4));
        String maTt = chuoiTrangThai(layMoHinh().getValueAt(dongMoHinh, 6));
        return laHetHan(maTt, han, LocalDate.now());
    }

    private boolean laLoSapHetHan(int dongMoHinh) {
        LocalDate han = docNgay(layMoHinh().getValueAt(dongMoHinh, 4));
        long ton = docSoNguyen(layMoHinh().getValueAt(dongMoHinh, 5));
        String maTt = chuoiTrangThai(layMoHinh().getValueAt(dongMoHinh, 6));
        return laSapHetHan(maTt, han, ton, LocalDate.now());
    }

    @Override
    protected void datLaiBoLoc() {
        layOTimKiem().setText("");
        if (cboTrangThai != null) cboTrangThai.setSelectedIndex(0);
        loc();
    }

    @Override
    protected void sauKhiTaiLai() {
        doiTenCotMaSanPham();
        if (cboTrangThai != null) loc();
    }

    @SuppressWarnings("unchecked")
    private void loc() {
        if (!(layBangDuLieu().getRowSorter() instanceof TableRowSorter<?> raw)) return;
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) raw;
        String tuKhoa = layOTimKiem().getText().trim().toLowerCase(Locale.ROOT);
        String ttChon =
                cboTrangThai == null ? "Tất cả" : String.valueOf(cboTrangThai.getSelectedItem());
        LocalDate homNay = LocalDate.now();
        sorter.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ? extends Integer> e) {
                        if (!khopTrangThai(e, ttChon, homNay)) return false;
                        return khopTuKhoa(e, tuKhoa, homNay);
                    }
                });
    }

    private boolean khopTrangThai(
            RowFilter.Entry<? extends TableModel, ? extends Integer> e,
            String ttChon,
            LocalDate homNay) {
        if ("Tất cả".equals(ttChon)) return true;
        LocalDate han = docNgay(e.getValue(4));
        long ton = docSoNguyen(e.getValue(5));
        String maTt = chuoiTrangThai(e.getValue(6));
        String tenTt = DinhDangBang.tenTrangThai(maTt);
        return switch (ttChon) {
            case "Sẵn sàng" ->
                    TrangThaiLoHang.SAN_SANG.maDB().equalsIgnoreCase(maTt)
                            || "Sẵn sàng".equalsIgnoreCase(tenTt);
            case "Còn hạn" -> laConHan(maTt, han, ton, homNay);
            case "Sắp hết hạn" -> laSapHetHan(maTt, han, ton, homNay);
            case "Hết hạn" -> laHetHan(maTt, han, homNay);
            case "Tạm khóa" -> laTamKhoa(maTt, tenTt);
            case "Hết hàng" -> laHetHang(maTt, tenTt);
            default -> ttChon.equalsIgnoreCase(maTt) || ttChon.equalsIgnoreCase(tenTt);
        };
    }

    private boolean khopTuKhoa(
            RowFilter.Entry<? extends TableModel, ? extends Integer> e,
            String tuKhoa,
            LocalDate homNay) {
        if (tuKhoa.isEmpty()) return true;
        for (int i = 1; i < e.getValueCount(); i++) {
            Object val = e.getValue(i);
            if (val == null) continue;
            if (val.toString().toLowerCase(Locale.ROOT).contains(tuKhoa)) return true;
            if (i == 4) {
                LocalDate han = docNgay(val);
                if (han != null && DinhDang.ngay(han).toLowerCase(Locale.ROOT).contains(tuKhoa)) {
                    return true;
                }
            } else if (i == 6) {
                String tenTt = DinhDangBang.tenTrangThai(val.toString());
                if (tenTt.toLowerCase(Locale.ROOT).contains(tuKhoa)) return true;
                if (laSapHetHan(
                                val.toString().trim(),
                                docNgay(e.getValue(4)),
                                docSoNguyen(e.getValue(5)),
                                homNay)
                        && "sắp hết hạn".contains(tuKhoa)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String chuoiTrangThai(Object val) {
        return val == null ? "" : val.toString().trim();
    }

    private static boolean laTamKhoa(String maTt, String tenTt) {
        return TrangThaiLoHang.TAM_KHOA.maDB().equalsIgnoreCase(maTt)
                || "Tạm khóa".equalsIgnoreCase(tenTt);
    }

    private static boolean laHetHang(String maTt, String tenTt) {
        return TrangThaiLoHang.HET_HANG.maDB().equalsIgnoreCase(maTt)
                || "Hết hàng".equalsIgnoreCase(tenTt);
    }

    private static boolean laHetHan(String maTt, LocalDate han, LocalDate homNay) {
        String tenTt = DinhDangBang.tenTrangThai(maTt);
        return TrangThaiLoHang.HET_HAN.maDB().equalsIgnoreCase(maTt)
                || "Hết hạn".equalsIgnoreCase(tenTt)
                || (han != null && han.isBefore(homNay));
    }

    private boolean laSapHetHan(String maTt, LocalDate han, long ton, LocalDate homNay) {
        String tenTt = DinhDangBang.tenTrangThai(maTt);
        if (ton <= 0 || han == null) return false;
        if (laHetHan(maTt, han, homNay) || laTamKhoa(maTt, tenTt) || laHetHang(maTt, tenTt)) {
            return false;
        }
        return !han.isAfter(homNay.plusDays(nguongCanhBao()));
    }

    private static boolean laConHan(String maTt, LocalDate han, long ton, LocalDate homNay) {
        String tenTt = DinhDangBang.tenTrangThai(maTt);
        if (ton <= 0) return false;
        if (laHetHan(maTt, han, homNay) || laTamKhoa(maTt, tenTt) || laHetHang(maTt, tenTt)) {
            return false;
        }
        return han == null || !han.isBefore(homNay);
    }

    private static long docSoNguyen(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(val.toString().trim());
        } catch (NumberFormatException ignored) {
            return 0L;
        }
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
            if (chuoi.length() >= 10 && chuoi.charAt(2) == '/') {
                return LocalDate.parse(chuoi.substring(0, 10), DinhDang.NGAY);
            }
        } catch (RuntimeException ignored) {
            return null;
        }
        return null;
    }
}
