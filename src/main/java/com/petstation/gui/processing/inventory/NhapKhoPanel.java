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

/** Cung cấp giao diện và xử lý tương tác cho nhập kho vùng hiển thị. */
public final class NhapKhoPanel extends NghiepVuKhoPanel {
    public NhapKhoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_NHAP_KHO, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Lập phiếu nhập kho", this::nhapKho);
        themNut(pnlCacThaoTac, "Chi tiết", this::layNhapKhoChiTiet);
        themNut(pnlCacThaoTac, "Trả công nợ", this::thanhToanNhaCungCap);
    }
}
