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
    }

    protected void lichSuCoc() {
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
                LuuTruService.layCoc(layDongChon()));
    }

    public void moDonPhong(long id) {
        var cacPhongDangDon = LuuTruService.layPhongDangDon();
        if (cacPhongDangDon.isEmpty()) {
            com.petstation.gui.components.HopThoai.hienThiThongTin(
                    this, "Hiện không có phòng nào đang ở trạng thái chờ dọn vệ sinh.");
            return;
        }
        var field =
                com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu.layLuaChon(
                        "room", "Phòng đang dọn", cacPhongDangDon);
        if (id > 0)
            field =
                    new com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu(
                            field.khoa(), field.nhan(), String.valueOf(id), field.cacLuaChon());
        var values =
                com.petstation.gui.components.common.BieuMauSQL.layYeuCauNhap(
                        this, "Hoàn thành dọn phòng", field);
        if (values != null) {
            LuuTruService.hoanTatDonPhong(Long.parseLong(values.get("room")));
            taiLaiNen();
            com.petstation.gui.components.HopThoai.hienThiThanhCong(
                    this, "Đã xác nhận dọn phòng xong, phòng đã chuyển sang trạng thái Trống.");
        }
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

    protected long cocConThieu() {
        if (tblBang.getSelectedRow() < 0 || moHinh.getColumnCount() < 11) return Long.MAX_VALUE;
        int row = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
        return ((Number) moHinh.getValueAt(row, 10)).longValue()
                - ((Number) moHinh.getValueAt(row, 7)).longValue();
    }

    protected long cocDaThu() {
        if (tblBang.getSelectedRow() < 0 || moHinh.getColumnCount() < 8) return Long.MAX_VALUE;
        int row = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
        Object value = moHinh.getValueAt(row, 7);
        return value instanceof Number n ? n.longValue() : 0;
    }

    protected void datPhong() {
        String maPhong = banHang.getQuyTrinh().getMaPhongHotel();
        Long id = DatPhongHotelDialog.mo(this, null, maPhong);
        if (id != null) banHang.getQuyTrinh().chonHotel(id);
    }

    protected void suaDatPhong() {
        long id = layDongChon();
        Long result = DatPhongHotelDialog.mo(this, id, null);
        if (result != null) banHang.getQuyTrinh().chonHotel(result);
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
        banHang.getQuyTrinh().chonHotel(0);
    }

    protected void thuCoc() {
        if (!com.petstation.service.CaThuNganService.caDangMo()) {
            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Ca thu ngân hiện chưa mở. Cần mở ca để ghi nhận phiếu thu tiền cọc.\n"
                            + "Bạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay bây giờ không?",
                    "Chưa mở ca làm việc",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                dieuHuong.accept(MaManHinh.CA_THU_NGAN);
            }
            return;
        }
        long id = layDongChon();
        long[] muc = LuuTruService.layMucCoc(id);
        long thieu = muc[0] - muc[1];
        String input =
                JOptionPane.showInputDialog(
                        this,
                        "Yêu cầu cọc: "
                                + com.petstation.util.TienTe.dinhDang(muc[0])
                                + " · Đã thu: "
                                + com.petstation.util.TienTe.dinhDang(muc[1])
                                + "\nSố tiền thu lần này (tối đa "
                                + com.petstation.util.TienTe.dinhDang(thieu)
                                + "):",
                        String.valueOf(thieu));
        if (input == null) return;
        String daDon = input.trim().replaceAll("[.,\\s₫đ]", "");
        if (!daDon.matches("\\d+")) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ (chỉ gồm chữ số).");
        }
        long so;
        try {
            so = Long.parseLong(daDon);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền thu cọc vượt giới hạn hợp lệ.");
        }
        if (so <= 0 || so > thieu)
            throw new IllegalArgumentException("Số thu vượt phần cọc còn thiếu (" + com.petstation.util.TienTe.dinhDang(thieu) + ").");

        String maHopDong = layNoiDungDongChon(1);

        String khoa = "COC" + UUID.randomUUID().toString().replace("-", "").substring(0, 22);
        var dong =
                com.petstation.gui.components.common.ThuTienDialog.nhap(
                        this, so, maHopDong);
        if (dong != null) {
            try {
                LuuTruService.thuCoc(id, dong, khoa);
            } catch (RuntimeException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("Cần mở ca")) {
                    int luaChon = JOptionPane.showConfirmDialog(
                            this,
                            ex.getMessage() + "\nBạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay không?",
                            "Chưa mở ca làm việc",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (luaChon == JOptionPane.YES_OPTION) {
                        dieuHuong.accept(MaManHinh.CA_THU_NGAN);
                    }
                } else {
                    throw ex;
                }
            }
        }
    }

    protected void doiCoc() {
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
        if (v != null) LuuTruService.dieuChinhCoc(id, docSoTien(v, "muc"), v.get("lydo"));
    }

    protected void layNhatKyChiTiet() {
        hienThiChiTiet(
                "Nhật ký lưu trú",
                new String[] {"Thời gian", "Nhân viên", "Ăn uống", "Vệ sinh", "Tình trạng"},
                LuuTruService.layNhatKyChiTiet(layDongChon()));
    }

    protected void quyetToan(String loai) {
        if (!banHang.kiemTraTrong()) {
            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\nBạn có muốn làm trống giỏ hàng để chuyển sang quyết toán lưu trú không?",
                    "Giỏ hàng đang có sản phẩm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (luaChon == JOptionPane.YES_OPTION) {
                banHang.xoa();
            } else {
                return;
            }
        }
        BanHangService.quyetToan(loai, layDongChon(), banHang);
        dieuHuong.accept(MaManHinh.THANH_TOAN);
    }
}
