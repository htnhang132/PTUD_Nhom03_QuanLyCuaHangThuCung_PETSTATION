package com.petstation.gui.processing.inventory;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho nhập kho vùng hiển thị. */
public final class NhapKhoPanel extends NghiepVuKhoPanel {
    /**
     * Khởi tạo NhapKhoPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public NhapKhoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PURCHASE, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Lập phiếu nhập kho", this::nhapKho);
        themNut(pnlCacThaoTac, "Chi tiết", this::layNhapKhoChiTiet);
        themNut(pnlCacThaoTac, "Trả công nợ", this::thanhToanNhaCungCap);
    }
}
