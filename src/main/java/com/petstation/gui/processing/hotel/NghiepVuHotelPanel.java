package com.petstation.gui.processing.hotel;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.gui.components.TruongNghiepVu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.BanHangService;
import com.petstation.service.LuaChonNghiepVuService;
import com.petstation.service.LuuTruService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Hotel nghiệp vụ vùng hiển thị. */
public abstract class NghiepVuHotelPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo NghiepVuHotelPanel với các giá trị được cung cấp.
     *
     * @param kieu kiểu
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    protected NghiepVuHotelPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    /** Đặt phòng trong NghiepVuHotelPanel. */
    @Override
    protected void capNhatThaoTac() {
        if (tblBang == null || pnlCacThaoTac == null) return;
        String trangThai =
                tblBang.getSelectedRow() < 0
                        ? ""
                        : String.valueOf(
                                moHinh.getValueAt(
                                        tblBang.convertRowIndexToModel(tblBang.getSelectedRow()),
                                        9));
        for (Component thanhPhan : pnlCacThaoTac.getComponents())
            if (thanhPhan instanceof JButton btn) {
                switch (btn.getText()) {
                    case "Nhận phòng" -> btn.setEnabled(trangThai.equals("BOOKED"));
                    case "Ghi chăm sóc", "Quyết toán" ->
                            btn.setEnabled(trangThai.equals("CHECKED_IN"));
                    case "Xem nhật ký" -> btn.setEnabled(!trangThai.isEmpty());
                    default -> {}
                }
            }
    }

    protected void datPhong() {
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Đặt phòng lưu trú",
                        layThuCung(),
                        layTruongPhongDaChon(),
                        layTruongVanBan(
                                "start", "Ngày vào (yyyy-MM-dd HH:mm)", layThoiGianHienTai()),
                        layTruongVanBan(
                                "end",
                                "Ngày ra (yyyy-MM-dd HH:mm)",
                                LocalDateTime.now()
                                        .plusDays(1)
                                        .withSecond(0)
                                        .format(THOI_GIAN_NGAY)),
                        layTruongVanBan("note", "Ghi chú", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null) {
            long coc =
                    LuuTruService.tinhCocDuKien(
                            giaTri.get("room"),
                            docThoiGian(giaTri, "start"),
                            docThoiGian(giaTri, "end"));
            if (JOptionPane.showConfirmDialog(
                            this,
                            "Đã nhận cọc TIỀN MẶT 50% (ghi vào ca đang mở): "
                                    + com.petstation.util.TienTe.dinhDang(coc)
                                    + "?",
                            "Xác nhận nhận cọc",
                            JOptionPane.YES_NO_OPTION)
                    != JOptionPane.YES_OPTION) return;
            long id =
                    LuuTruService.datPhong(
                            giaTri.get("pet"),
                            giaTri.get("room"),
                            docThoiGian(giaTri, "start"),
                            docThoiGian(giaTri, "end"),
                            coc,
                            giaTri.get("note"),
                            true);
            banHang.layQuyTrinh().chonHotel(id);
        }
    }

    /** Ghi nhật ký trong NghiepVuHotelPanel. */
    protected void ghiNhatKy() {
        // Hợp đồng lưu trú.
        long hopDongLuuTru = layDongChon();
        // Giá trị.
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Nhật ký chăm sóc",
                        layTruongVanBan("food", "Ăn uống", ""),
                        layTruongVanBan("hygiene", "Vệ sinh", ""),
                        layTruongVanBan("health", "Tình trạng thú cưng", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri != null)
            LuuTruService.ghiNhatKy(
                    hopDongLuuTru, giaTri.get("food"), giaTri.get("hygiene"), giaTri.get("health"));
    }

    /** Ghi sự kiện vào nhật ký hệ thống chi tiết trong NghiepVuHotelPanel. */
    protected void layNhatKyChiTiet() {
        hienThiChiTiet(
                "Nhật ký lưu trú",
                new String[] {"Thời gian", "Nhân viên", "Ăn uống", "Vệ sinh", "Tình trạng"},
                LuuTruService.layNhatKyChiTiet(layDongChon()));
    }

    /**
     * Quyết toán trong NghiepVuHotelPanel.
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
    private BieuMauSQL.TruongDuLieu layTruongPhongDaChon() {
        var truong=taoLuaChon("room","Phòng",LuaChonNghiepVuService.layLuaChonPhong());
        long id=banHang.layQuyTrinh().layPhongHotel();
        return new BieuMauSQL.TruongDuLieu(truong.khoa(),truong.nhan(),id>0?banHang.layQuyTrinh().layMaPhongHotel():"",truong.cacLuaChon());
    }

}
