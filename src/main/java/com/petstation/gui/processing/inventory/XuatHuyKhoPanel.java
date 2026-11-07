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

public final class XuatHuyKhoPanel extends NghiepVuKhoPanel {
    public XuatHuyKhoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_XUAT_KHO, banHang, dieuHuong);
        themNut(pnlCacThaoTac, "+ Lập phiếu xuất hủy", this::ghiHuy);
    }
}
