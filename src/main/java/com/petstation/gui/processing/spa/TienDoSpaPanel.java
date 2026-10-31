package com.petstation.gui.processing.spa;

import static com.petstation.util.DuLieu.*;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.SpaService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Spa tiến độ vùng hiển thị. */
public final class TienDoSpaPanel extends NghiepVuSpaPanel {
    public TienDoSpaPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.TIEN_DO_SPA, banHang, dieuHuong);

        themNut(
            pnlCacThaoTac,
            "Bắt đầu làm",
            () -> SpaService.layTrangThaiSpa(layDongChon(), "IN_PROGRESS"));
        themNut(
            pnlCacThaoTac,
            "Hoàn thành",
            () -> SpaService.layTrangThaiSpa(layDongChon(), "COMPLETED"));
        themNut(pnlCacThaoTac, "Thanh toán", () -> quyetToan("SPA"));
        themNut(pnlCacThaoTac, "Chi tiết dịch vụ", this::laySpaChiTiet);
        var taiLieu = (javax.swing.text.AbstractDocument) txtTimKiem.getDocument();
        for (var nghe : taiLieu.getDocumentListeners()) taiLieu.removeDocumentListener(nghe);
        taiLieu.addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { locPhieu(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { locPhieu(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { locPhieu(); }
        });
        locPhieu();
    }
    @Override
    protected void sauKhiTaiLai() {
        locPhieu();
    }

    private void locPhieu() {
        if (tblBang == null) return;
        var boLoc = (javax.swing.table.TableRowSorter<?>) tblBang.getRowSorter();
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase(java.util.Locale.ROOT);
        boLoc.setRowFilter(new javax.swing.RowFilter<javax.swing.table.TableModel, Integer>() {
            @Override
            public boolean include(javax.swing.RowFilter.Entry<? extends javax.swing.table.TableModel, ? extends Integer> dong) {
                if (!java.util.Set.of("RECEIVED", "IN_PROGRESS", "COMPLETED")
                    .contains(dong.getStringValue(7))) return false;
                for (int i = 1; i < dong.getValueCount(); i++)
                    if (dong.getStringValue(i).toLowerCase(java.util.Locale.ROOT).contains(tuKhoa))
                        return true;
                return false;
            }
        });
    }

    @Override
    protected void capNhatThaoTac() {
        super.capNhatThaoTac();
        if (tblBang == null || pnlCacThaoTac == null) return;
        String trangThai = tblBang.getSelectedRow() < 0 ? "" : layNoiDungDongChon(7);
        for (Component c : pnlCacThaoTac.getComponents()) {
            if (!(c instanceof JButton nut)) continue;
            switch (nut.getText()) {
                case "Bắt đầu làm" -> nut.setVisible(trangThai.equals("RECEIVED"));
                case "Hoàn thành" -> nut.setVisible(trangThai.equals("IN_PROGRESS"));
                case "Thanh toán" -> nut.setVisible(trangThai.equals("COMPLETED"));
                default -> { }
            }
        }
        pnlCacThaoTac.revalidate();
        pnlCacThaoTac.repaint();
    }
}
