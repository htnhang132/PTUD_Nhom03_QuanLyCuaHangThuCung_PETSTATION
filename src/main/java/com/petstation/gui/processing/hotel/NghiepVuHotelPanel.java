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
                        com.petstation.gui.components.common.NhatKyChamSocDialog.mo(
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
                == com.petstation.model.VaiTro.QUAN_LY_CUA_HANG)
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
        boolean daChon = !trangThai.isEmpty();
        for (Component thanhPhan : pnlCacThaoTac.getComponents())
            if (thanhPhan instanceof JButton btn) {
                switch (btn.getText()) {
                    case "Thu cọc" ->
                            btn.setEnabled(trangThai.equals("DA_DAT") && cocConThieu() > 0);
                    case "Điều chỉnh cọc" -> btn.setEnabled(trangThai.equals("DA_DAT"));
                    case "Nhận phòng" ->
                            btn.setEnabled(trangThai.equals("DA_DAT") && cocConThieu() <= 0);
                    case "Phiếu chăm sóc" -> btn.setEnabled(trangThai.equals("DANG_O"));
                    case "Quyết toán" ->
                            btn.setEnabled(trangThai.equals("DANG_O")
                                    && kieuNghiepVu != LoaiNghiepVu.NHAT_KY_LUU_TRU);
                    case "Xem nhật ký" ->
                            btn.setEnabled(trangThai.equals("DANG_O")
                                    || trangThai.equals("DA_TRA_PHONG"));
                    case "Lịch sử cọc" -> btn.setEnabled(daChon);
                    case "Sửa đặt phòng" -> btn.setEnabled(trangThai.equals("DA_DAT"));
                    case "Hủy đặt phòng" ->
                            btn.setEnabled(trangThai.equals("DA_DAT") && cocDaThu() == 0);
                    default -> {
                        if (btn.getText().startsWith("+ Đặt phòng"))
                            btn.setEnabled(kieuNghiepVu != LoaiNghiepVu.TRA_PHONG_LUU_TRU
                                    && kieuNghiepVu != LoaiNghiepVu.NHAT_KY_LUU_TRU);
                    }
                }
            }
    }

    private long cocConThieu() {
        if (tblBang.getSelectedRow() < 0 || moHinh.getColumnCount() < 11) return Long.MAX_VALUE;
        int row = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
        return ((Number) moHinh.getValueAt(row, 10)).longValue()
                - ((Number) moHinh.getValueAt(row, 7)).longValue();
    }

    private long cocDaThu() {
        if (tblBang.getSelectedRow() < 0 || moHinh.getColumnCount() < 8) return Long.MAX_VALUE;
        int row = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
        Object value = moHinh.getValueAt(row, 7);
        return value instanceof Number n ? n.longValue() : 0;
    }

    protected void datPhong() {
        String maPhong = banHang.layQuyTrinh().layMaPhongHotel();
        Long id = DatPhongHotelDialog.mo(this, null, maPhong);
        if (id != null) banHang.layQuyTrinh().chonHotel(id);
    }

    protected void suaDatPhong() {
        long id = layDongChon();
        Long result = DatPhongHotelDialog.mo(this, id, null);
        if (result != null) banHang.layQuyTrinh().chonHotel(result);
    }

    protected void huyDatPhong() {
        long id = layDongChon();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hủy hợp đồng đặt phòng #" + id + "?\n"
                        + "Chỉ có thể hủy trực tiếp khi hợp đồng chưa thu cọc.",
                "Xác nhận hủy đặt phòng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String reason = JOptionPane.showInputDialog(
                this, "Lý do hủy đặt phòng:", "Hủy đặt phòng",
                JOptionPane.PLAIN_MESSAGE);
        if (reason == null) return;
        LuuTruService.huyDatPhong(id, reason);
        banHang.layQuyTrinh().chonHotel(0);
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
        // QR hiển thị mã hợp đồng để dễ đối chiếu khoản chuyển.
        String maHopDong = layNoiDungDongChon(1);
        // Mỗi lần thu vẫn có khóa riêng để cho phép thu cọc nhiều đợt trên cùng hợp đồng.
        String khoa = "COC" + UUID.randomUUID().toString().replace("-", "").substring(0, 22);
        var dong =
                com.petstation.gui.components.common.ThuTienDialog.nhap(
                        this, so, maHopDong);
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
        dieuHuong.accept(MaManHinh.THANH_TOAN);
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