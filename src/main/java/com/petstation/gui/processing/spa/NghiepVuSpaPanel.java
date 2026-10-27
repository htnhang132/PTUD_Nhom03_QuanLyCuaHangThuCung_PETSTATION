package com.petstation.gui.processing.spa;

import static com.petstation.gui.components.TruongNghiepVu.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
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
    protected NghiepVuSpaPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
        if (kieu != LoaiNghiepVu.APPOINTMENTS && kieu != LoaiNghiepVu.SPA_TICKETS)
            themNut(
                    pnlCacThaoTac,
                    "Phiếu chăm sóc",
                    () ->
                            com.petstation.gui.components.common.PhieuChamSocDialog.mo(
                                    this, "SPA", layDongChon()));
    }

    @Override
    protected void capNhatThaoTac() {
        if (tblBang == null || pnlCacThaoTac == null) return;
        String trangThai = "";
        boolean coDichVu = false;
        if (tblBang.getSelectedRow() >= 0 && kieuNghiepVu != LoaiNghiepVu.APPOINTMENTS) {
            int dong = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
            trangThai = String.valueOf(moHinh.getValueAt(dong, 7));
            coDichVu = ((Number) moHinh.getValueAt(dong, 6)).longValue() > 0;
        }
        for (Component thanhPhan : pnlCacThaoTac.getComponents())
            if (thanhPhan instanceof JButton btn) {
                switch (btn.getText()) {
                    case "Thêm dịch vụ" ->
                            btn.setEnabled(Set.of("RECEIVED", "IN_PROGRESS").contains(trangThai));
                    case "Bắt đầu làm" -> btn.setEnabled(trangThai.equals("RECEIVED") && coDichVu);
                    case "Hoàn thành" -> btn.setEnabled(trangThai.equals("IN_PROGRESS"));
                    case "Thanh toán" ->
                            btn.setEnabled(
                                    trangThai.equals("COMPLETED")
                                            && com.petstation.service.PhienDangNhapService
                                                    .layPhien()
                                                    .kiemTraCo(
                                                            com.petstation.model.Quyen
                                                                    .PAYMENT_PROCESS));
                    default -> {}
                }
            }
    }

    protected void datLichHen() {
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
        if (giaTri != null) {
            long id =
                    SpaService.datLichHen(
                            giaTri.get("pet"),
                            giaTri.get("package"),
                            giaTri.get("staff"),
                            docThoiGian(giaTri, "start"));
            banHang.layQuyTrinh().chonLichHen(id);
        }
    }

    protected void huyLichHen() {
        if (HopThoai.xacNhan(this, "Hủy lịch hẹn đã chọn?"))
            SpaService.huyLichHen(layDongChon(), layNoiDungDongChon(1));
    }

    protected void tiepNhan() {
        // Lịch hẹn.
        String lichHen = "", thuCung = "";
        if (kieuNghiepVu == LoaiNghiepVu.APPOINTMENTS && tblBang.getSelectedRow() >= 0) {
            lichHen = layNoiDungDongChon(1);
            thuCung = chuyenThanhChuoi(SpaService.layMaLichHenThuCung(layDongChon()));
        }
        // Trường thú cưng.
        if (lichHen.isEmpty() && banHang.layQuyTrinh().layLichHen() > 0) {
            var hen =
                    com.petstation.service.QuyTrinhService.layLichHen(
                            banHang.layQuyTrinh().layLichHen());
            if (!hen.isEmpty()) {
                lichHen = hen.get(0)[0].toString();
                thuCung = hen.get(0)[1].toString();
            }
        }
        TruongDuLieu truongThuCung =
                thuCung.isEmpty()
                        ? layThuCung()
                        : TruongDuLieu.layLuaChon(
                                "pet",
                                "Thú cưng",
                                List.<Object[]>of(new Object[] {thuCung, thuCung}));
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Tiếp nhận Spa",
                        truongThuCung,
                        layTruongVanBan("weight", "Cân nặng thực tế (kg)", ""),
                        layTruongVanBan("note", "Tình trạng / Lưu ý", ""));
        if (giaTri != null) {
            long id =
                    SpaService.tiepNhan(
                            giaTri.get("pet"),
                            lichHen,
                            Double.parseDouble(giaTri.get("weight").replace(',', '.')),
                            giaTri.get("note"));
            banHang.layQuyTrinh().chonSpa(id);
            dieuHuong.accept(MaManHinh.SPA_SERVICE_SELECTION);
        }
    }

    protected void themDichVu() {
        // Phiếu Spa.
        long phieuSpa = layDongChon();
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(this, "Thêm dịch vụ vào phiếu", layCacGoiDichVu());
        if (giaTri != null) SpaService.themDichVuSpa(phieuSpa, giaTri.get("package"));
    }

    protected void laySpaChiTiet() {
        hienThiChiTiet(
                "Chi tiết phiếu Spa",
                new String[] {"Dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"},
                SpaService.layPhieuSpaChiTiet(layDongChon()));
    }

    protected void quyetToan(String loai) {
        if (!banHang.kiemTraTrong())
            throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
        BanHangService.quyetToan(loai, layDongChon(), banHang);
        dieuHuong.accept(MaManHinh.PAYMENT);
    }
}
