package com.petstation.gui.processing.spa;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.gui.components.TruongNghiepVu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.BanHangService;
import com.petstation.service.LuaChonNghiepVuService;
import com.petstation.service.SpaService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Spa nghiệp vụ vùng hiển thị. */
public abstract class NghiepVuSpaPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo NghiepVuSpaPanel với các giá trị được cung cấp.
     *
     * @param kieu kiểu
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    protected NghiepVuSpaPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    /** Đặt lịch hẹn trong NghiepVuSpaPanel. */
    protected void datLichHen() {
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Đặt lịch Spa",
                        layThuCung(),
                        layCacGoiDichVu(),
                        taoLuaChon(
                                "staff", "Nhân viên", LuaChonNghiepVuService.layLuaChonNhanVien()),
                        layTruongVanBan(
                                "start", "Bắt đầu (yyyy-MM-dd HH:mm)", layThoiGianHienTai()));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null)
            SpaService.datLichHen(
                    giaTri.get("pet"),
                    giaTri.get("package"),
                    giaTri.get("staff"),
                    docThoiGian(giaTri, "start"));
    }

    /** Hủy lịch hẹn trong NghiepVuSpaPanel. */
    protected void huyLichHen() {
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (HopThoai.xacNhan(this, "Hủy lịch hẹn đã chọn?"))
            SpaService.huyLichHen(layDongChon(), layNoiDungDongChon(1));
    }

    /** Tiếp nhận trong NghiepVuSpaPanel. */
    protected void tiepNhan() {
        // Lịch hẹn.
        String lichHen = "", thuCung = "";
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (kieuNghiepVu == LoaiNghiepVu.APPOINTMENTS && tblBang.getSelectedRow() >= 0) {
            lichHen = layNoiDungDongChon(1);
            thuCung = chuyenThanhChuoi(SpaService.layMaLichHenThuCung(layDongChon()));
        }
        // Trường thú cưng.
        TruongDuLieu truongThuCung =
                thuCung.isEmpty()
                        ? layThuCung()
                        : TruongDuLieu.layLuaChon(
                                "pet",
                                "Thú cưng",
                                List.<Object[]>of(new Object[] {thuCung, thuCung}));
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Tiếp nhận Spa",
                        truongThuCung,
                        layTruongVanBan("weight", "Cân nặng thực tế (kg)", ""),
                        layTruongVanBan("note", "Tình trạng / Lưu ý", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null)
            SpaService.tiepNhan(
                    giaTri.get("pet"),
                    lichHen,
                    Double.parseDouble(giaTri.get("weight").replace(',', '.')),
                    giaTri.get("note"));
    }

    /** Thêm dịch vụ trong NghiepVuSpaPanel. */
    protected void themDichVu() {
        // Phiếu Spa.
        long phieuSpa = layDongChon();
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(this, "Thêm dịch vụ vào phiếu", layCacGoiDichVu());
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null) SpaService.themDichVuSpa(phieuSpa, giaTri.get("package"));
    }

    /** Lấy Spa chi tiết trong NghiepVuSpaPanel. */
    protected void laySpaChiTiet() {
        hienThiChiTiet(
                "Chi tiết phiếu Spa",
                new String[] {"Dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"},
                SpaService.layPhieuSpaChiTiet(layDongChon()));
    }

    /**
     * Quyết toán trong NghiepVuSpaPanel.
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
