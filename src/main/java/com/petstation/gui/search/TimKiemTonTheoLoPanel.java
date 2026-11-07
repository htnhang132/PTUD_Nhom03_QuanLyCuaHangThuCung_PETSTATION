package com.petstation.gui.search;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.ThanhDanhSach;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;

public final class TimKiemTonTheoLoPanel extends BangNghiepVuPanel {
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(new String[] {"Tất cả", "Còn hạn", "Sắp hết hạn", "Hết hạn"});

    public TimKiemTonTheoLoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.LO_HANG, banHang, dieuHuong);
        if (pnlCacThaoTac instanceof ThanhDanhSach thanh) {
            thanh.themBoLoc("Trạng thái:", cboTrangThai);
        } else {
            pnlCacThaoTac.add(cboTrangThai);
        }
        for (DocumentListener l :
                ((AbstractDocument) txtTimKiem.getDocument()).getDocumentListeners()) {
            txtTimKiem.getDocument().removeDocumentListener(l);
        }
        txtTimKiem
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            public void insertUpdate(DocumentEvent e) {
                                loc();
                            }

                            public void removeUpdate(DocumentEvent e) {
                                loc();
                            }

                            public void changedUpdate(DocumentEvent e) {
                                loc();
                            }
                        });
        cboTrangThai.addActionListener(e -> loc());
    }

    @Override
    protected void datLaiBoLoc() {
        txtTimKiem.setText("");
        if (cboTrangThai != null) cboTrangThai.setSelectedIndex(0);
        loc();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (cboTrangThai != null) loc();
    }

    @SuppressWarnings("unchecked")
    private void loc() {
        if (!(tblBang.getRowSorter() instanceof TableRowSorter<?> raw)) return;
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) raw;
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase(Locale.ROOT);
        String ttChon =
                cboTrangThai == null ? "Tất cả" : String.valueOf(cboTrangThai.getSelectedItem());
        sorter.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ? extends Integer> e) {
                        if (!"Tất cả".equals(ttChon)) {
                            String maTt = e.getStringValue(6);
                            String tenTt = DinhDangBang.tenTrangThai(maTt);
                            if (!ttChon.equalsIgnoreCase(maTt) && !ttChon.equalsIgnoreCase(tenTt)) {
                                return false;
                            }
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
