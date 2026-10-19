package com.petstation.gui.processing.inventory;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.gui.components.TruongNghiepVu.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.KhoService;
import com.petstation.service.LuaChonNghiepVuService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho kho nghiệp vụ vùng hiển thị. */
public abstract class NghiepVuKhoPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo NghiepVuKhoPanel với các giá trị được cung cấp.
     *
     * @param kieu kiểu
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    protected NghiepVuKhoPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    /** Nhập kho trong NghiepVuKhoPanel. */
    protected void nhapKho() {
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Lập phiếu nhập kho",
                        taoLuaChon(
                                "supplier",
                                "Nhà cung cấp",
                                LuaChonNghiepVuService.layLuaChonNhaCungCap()),
                        taoLuaChon(
                                "product", "Sản phẩm", LuaChonNghiepVuService.layLuaChonSanPham()),
                        layTruongVanBan("batch", "Số lô", ""),
                        layTruongVanBan("expiry", "Hạn sử dụng (yyyy-MM-dd, có thể trống)", ""),
                        layTruongVanBan("qty", "Số lượng", "1"),
                        layTruongVanBan("cost", "Giá nhập / đơn vị (đồng)", ""),
                        layTruongVanBan("paid", "Đã trả (đồng)", "0"));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null)
            KhoService.nhapKho(
                    giaTri.get("supplier"),
                    giaTri.get("product"),
                    giaTri.get("batch"),
                    giaTri.get("expiry").isBlank() ? null : LocalDate.parse(giaTri.get("expiry")),
                    docSoLuong(giaTri, "qty"),
                    docSoTien(giaTri, "cost"),
                    docSoTien(giaTri, "paid"));
    }

    /** Thanh toán nhà cung cấp trong NghiepVuKhoPanel. */
    protected void thanhToanNhaCungCap() {
        yeuCau(com.petstation.model.Quyen.INVENTORY_RECEIVE);
        // Id.
        long id = layDongChon();
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Trả công nợ",
                        layTruongVanBan("paid", "Số tiền trả thêm (đồng)", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri == null) return;
        KhoService.thanhToanNhaCungCap(id, docSoTien(giaTri, "paid"));
    }

    /** Ghi hủy trong NghiepVuKhoPanel. */
    protected void ghiHuy() {
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Xuất hủy hàng hóa",
                        layCacLoHang(),
                        layTruongVanBan("qty", "Số lượng", "1"),
                        layTruongVanBan("reason", "Lý do", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null) {
            KhoService.ghiHuy(
                    docSoTien(giaTri, "batch"), docSoLuong(giaTri, "qty"), giaTri.get("reason"));
            HopThoai.hienThiThanhCong(this, "Đã lưu phiếu. Phiếu vượt ngưỡng sẽ chờ phê duyệt.");
        }
    }

    /** Kiểm kê trong NghiepVuKhoPanel. */
    protected void kiemKe() {
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Kiểm kê",
                        layCacLoHang(),
                        layTruongVanBan("actual", "Tồn thực tế", ""),
                        layTruongVanBan("reason", "Lý do chênh lệch", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null) {
            KhoService.kiemKe(
                    docSoTien(giaTri, "batch"), docSoLuong(giaTri, "actual"), giaTri.get("reason"));
            HopThoai.hienThiThanhCong(
                    this, "Đã lưu biên bản. Chênh lệch chỉ điều chỉnh sau phê duyệt.");
        }
    }

    /** Lấy nhập kho chi tiết trong NghiepVuKhoPanel. */
    protected void layNhapKhoChiTiet() {
        hienThiChiTiet(
                "Chi tiết nhập kho",
                new String[] {"Sản phẩm", "Số lô", "Hạn dùng", "Số lượng", "Giá nhập"},
                KhoService.layNhapKhoChiTiet(layDongChon()));
    }
}
