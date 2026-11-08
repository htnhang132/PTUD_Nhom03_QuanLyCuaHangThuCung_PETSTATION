package com.petstation.gui.processing.inventory;

import static com.petstation.util.DuLieu.*;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

// Màn hình Xử lý > Lập phiếu nhập kho
public final class NhapKhoPanel extends NghiepVuKhoPanel {
    public NhapKhoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_NHAP_KHO, banHang, dieuHuong);

        // 1. KHỞI TẠO CÁC NÚT THAO TÁC TRÊN THANH CÔNG CỤ
        themNut(pnlCacThaoTac, "+ Lập phiếu nhập kho", com.petstation.gui.components.NutDinhDang.KieuNut.CHINH, this::nhapKho);
        var btnChiTiet = themNut(pnlCacThaoTac, "Chi tiết", com.petstation.gui.components.NutDinhDang.KieuNut.PHU, this::layNhapKhoChiTiet);
        var btnTraNo = themNut(pnlCacThaoTac, "Trả công nợ", com.petstation.gui.components.NutDinhDang.KieuNut.THANH_CONG, this::thanhToanNhaCungCap);

        // 2. XỬ LÝ SỰ KIỆN CHỌN DÒNG & KÍCH HOẠT NÚT TRẢ CÔNG NỢ / XEM CHI TIẾT
        Runnable capNhatNut = () -> {
            int row = tblBang.getSelectedRow();
            if (row < 0) {
                btnChiTiet.setEnabled(false);
                btnTraNo.setEnabled(false);
                return;
            }
            btnChiTiet.setEnabled(true);
            int dong = tblBang.convertRowIndexToModel(row);
            long tongTien = moHinh.getValueAt(dong, 4) instanceof Number n
                    ? n.longValue()
                    : com.petstation.util.TienTe.doc(String.valueOf(moHinh.getValueAt(dong, 4)));
            long daTra = moHinh.getValueAt(dong, 5) instanceof Number n
                    ? n.longValue()
                    : com.petstation.util.TienTe.doc(String.valueOf(moHinh.getValueAt(dong, 5)));
            btnTraNo.setEnabled(tongTien > daTra);
        };
        tblBang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) capNhatNut.run();
        });
        capNhatNut.run();
        ganSuKienDupChuot(this::layNhapKhoChiTiet);
    }
}
