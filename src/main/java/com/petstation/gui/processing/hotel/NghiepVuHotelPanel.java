package com.petstation.gui.processing.hotel;

import static com.petstation.gui.components.TruongNghiepVu.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
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
    protected NghiepVuHotelPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
        themNut(pnlCacThaoTac, "Thu cọc", this::thuCoc);
        themNut(
                pnlCacThaoTac,
                "Phiếu chăm sóc",
                () ->
                        com.petstation.gui.components.common.PhieuChamSocDialog.mo(
                                this, "HOTEL", layDongChon()));
        themNut(
                pnlCacThaoTac,
                "Lịch sử cọc",
                () ->
                        hienThiChiTiet(
                                "Lịch sử thu cọc",
                                new String[] {
                                    "Phương thức",
                                    "Thực thu",
                                    "Khách đưa",
                                    "Tiền thừa",
                                    "Lúc nhận",
                                    "Ca"
                                },
                                LuuTruService.layCoc(layDongChon())));
        if (com.petstation.service.PhienDangNhapService.layPhien().docVaiTro()
                == com.petstation.model.VaiTro.STORE_MANAGER)
            themNut(pnlCacThaoTac, "Điều chỉnh cọc", this::doiCoc);
    }

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
                    case "Thu cọc" ->
                            btn.setEnabled(trangThai.equals("BOOKED") && cocConThieu() > 0);
                    case "Điều chỉnh cọc" -> btn.setEnabled(trangThai.equals("BOOKED"));
                    case "Nhận phòng" ->
                            btn.setEnabled(trangThai.equals("BOOKED") && cocConThieu() <= 0);
                    case "Ghi chăm sóc", "Quyết toán" ->
                            btn.setEnabled(trangThai.equals("CHECKED_IN"));
                    case "Xem nhật ký" -> btn.setEnabled(!trangThai.isEmpty());
                    default -> {}
                }
            }
    }

    private long cocConThieu() {
        if (tblBang.getSelectedRow() < 0 || moHinh.getColumnCount() < 11) return Long.MAX_VALUE;
        int row = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
        return ((Number) moHinh.getValueAt(row, 10)).longValue()
                - ((Number) moHinh.getValueAt(row, 7)).longValue();
    }

    protected void datPhong() {
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
        if (giaTri != null) {
            long id =
                    LuuTruService.giuPhong(
                            giaTri.get("pet"),
                            giaTri.get("room"),
                            docThoiGian(giaTri, "start"),
                            docThoiGian(giaTri, "end"),
                            giaTri.get("note"));
            banHang.layQuyTrinh().chonHotel(id);
        }
    }

    private void thuCoc() {
        long id = layDongChon();
        long[] muc = LuuTruService.layMucCoc(id);
        long thieu = muc[0] - muc[1];
        String input =
                JOptionPane.showInputDialog(
                        this,
                        "Yêu cầu: "
                                + muc[0]
                                + " · Đã thu: "
                                + muc[1]
                                + "\nSố thu lần này (tối đa "
                                + thieu
                                + "):",
                        String.valueOf(thieu));
        if (input == null) return;
        long so = Long.parseLong(input.trim());
        if (so <= 0 || so > thieu)
            throw new IllegalArgumentException("Số thu vượt phần cọc còn thiếu.");
        String khoa = "COC-" + UUID.randomUUID();
        var dong = com.petstation.gui.components.common.ThuTienDialog.nhap(this, so, khoa);
        if (dong != null) LuuTruService.thuCoc(id, dong, khoa);
    }

    private void doiCoc() {
        long id = layDongChon();
        Map<String, String> v =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Điều chỉnh cọc",
                        layTruongVanBan(
                                "muc",
                                "Mức yêu cầu mới (đồng)",
                                String.valueOf(LuuTruService.layMucCoc(id)[0])),
                        layTruongVanBan("lydo", "Lý do", ""));
        if (v != null) LuuTruService.dieuChinhCoc(id, Long.parseLong(v.get("muc")), v.get("lydo"));
    }

    protected void ghiNhatKy() {
        // Hợp đồng lưu trú.
        long hopDongLuuTru = layDongChon();
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Nhật ký chăm sóc",
                        layTruongVanBan("food", "Ăn uống", ""),
                        layTruongVanBan("hygiene", "Vệ sinh", ""),
                        layTruongVanBan("health", "Tình trạng thú cưng", ""));
        if (giaTri != null)
            LuuTruService.ghiNhatKy(
                    hopDongLuuTru, giaTri.get("food"), giaTri.get("hygiene"), giaTri.get("health"));
    }

    protected void layNhatKyChiTiet() {
        hienThiChiTiet(
                "Nhật ký lưu trú",
                new String[] {"Thời gian", "Nhân viên", "Ăn uống", "Vệ sinh", "Tình trạng"},
                LuuTruService.layNhatKyChiTiet(layDongChon()));
    }

    protected void quyetToan(String loai) {
        if (!banHang.kiemTraTrong())
            throw new IllegalArgumentException("Cần thanh toán hoặc treo giỏ hiện tại trước.");
        BanHangService.quyetToan(loai, layDongChon(), banHang);
        dieuHuong.accept(MaManHinh.PAYMENT);
    }

    private BieuMauSQL.TruongDuLieu layTruongPhongDaChon() {
        var truong = taoLuaChon("room", "Phòng", LuaChonNghiepVuService.layLuaChonPhong());
        long id = banHang.layQuyTrinh().layPhongHotel();
        return new BieuMauSQL.TruongDuLieu(
                truong.khoa(),
                truong.nhan(),
                id > 0 ? banHang.layQuyTrinh().layMaPhongHotel() : "",
                truong.cacLuaChon());
    }
}
