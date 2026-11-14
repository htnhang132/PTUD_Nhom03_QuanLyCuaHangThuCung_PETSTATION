package com.petstation.gui.kho;

import com.petstation.dto.GioHang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;

import java.util.function.Consumer;

// Màn hình Xử lý > Xuất hủy hàng hóa
// Màn hình: Xuất hủy hàng hóa (Mã: XUAT_HUY_KHO)
// Luồng xử lý: GUI -> NhapXuatKhoService -> NhapXuatKhoDAOImpl -> SQL (phieu_xuat_kho,
// chi_tiet_phieu_xuat, lo_hang)
// Mô tả: Lập phiếu xuất hủy đối với các lô hàng hết hạn sử dụng hoặc hư hại, trình quản lý phê
// duyệt
// Phân hệ: kho
public final class XuatHuyKhoPanel extends NghiepVuKhoPanel {
    public XuatHuyKhoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_XUAT_KHO, banHang, dieuHuong);

        // KHỞI TẠO NÚT THAO TÁC LẬP PHIẾU XUẤT HỦY
        themNut(layKhungThaoTac(), "+ Lập phiếu xuất hủy", this::ghiHuy);
    }
}
