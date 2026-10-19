package com.petstation.gui.processing.sales;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.gui.components.TruongNghiepVu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.BanHangService;
import com.petstation.service.DoiTraService;
import com.petstation.service.LuaChonNghiepVuService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho bán hàng nghiệp vụ vùng hiển thị. */
public abstract class NghiepVuBanHangPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo NghiepVuBanHangPanel với các giá trị được cung cấp.
     *
     * @param kieu kiểu
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    protected NghiepVuBanHangPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    /** Trả hàng trong NghiepVuBanHangPanel. */
    protected void traHang() {
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Lập phiếu trả hàng",
                        taoLuaChon(
                                "item",
                                "Hàng trên hóa đơn",
                                LuaChonNghiepVuService.layLuaChonDongHang()),
                        layTruongVanBan("qty", "Số lượng trả", "1"),
                        layTruongVanBan("reason", "Lý do", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null) {
            DoiTraService.traHang(
                    docSoTien(giaTri, "item"), docSoLuong(giaTri, "qty"), giaTri.get("reason"));
            HopThoai.hienThiThanhCong(
                    this, "Đã lưu phiếu trả. Phiếu vượt hạn mức hoặc thời hạn sẽ chờ duyệt.");
        }
    }

    /** Hiển thị hóa đơn trong NghiepVuBanHangPanel. */
    protected void hienThiHoaDon() {
        banHang.datBienLaiCuoi(BanHangService.layBienLai(layNoiDungDongChon(1)));
        dieuHuong.accept(MaManHinh.PRINT_INVOICE);
    }

    /**
     * Quyết toán trong NghiepVuBanHangPanel.
     *
     * @param loai loại
     */
    protected void quyetToan(String loai) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (!banHang.kiemTraTrong())
            throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
        BanHangService.quyetToan(loai, layDongChon(), banHang);
        dieuHuong.accept(MaManHinh.PAYMENT);
    }
}
