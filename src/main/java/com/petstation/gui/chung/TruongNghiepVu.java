package com.petstation.gui.chung;

import com.petstation.dto.MucLuaChon;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.LuaChon;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.TruongDuLieu;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.NghiepVuService;

import java.util.List;
import java.util.Map;

public final class TruongNghiepVu {
    private TruongNghiepVu() {}

    public static TruongDuLieu taoLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
        return TruongDuLieu.layLuaChon(khoa, nhan, cacDong);
    }

    public static TruongDuLieu taoLuaChonMuc(String khoa, String nhan, List<MucLuaChon> cacDong) {
        return new TruongDuLieu(
                khoa,
                nhan,
                "",
                cacDong.stream().map(m -> new LuaChon(m.giaTri(), m.nhan())).toList());
    }

    public static TruongDuLieu layTruongVanBan(String khoa, String nhan, String banDau) {
        return TruongDuLieu.chuyenThanhChuoi(khoa, nhan, banDau);
    }

    public static long docSoTien(Map<String, String> giaTri, String khoa) {
        String s = giaTri == null ? null : giaTri.get(khoa);
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ.");
        try {
            return Long.parseLong(s.trim().replaceAll("[.,\\s₫đ]", ""));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền nhập vào không hợp lệ.");
        }
    }

    public static int docSoLuong(Map<String, String> giaTri, String khoa) {
        String s = giaTri == null ? null : giaTri.get(khoa);
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập số lượng hợp lệ.");
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số lượng phải là số nguyên hợp lệ.");
        }
    }

    public static TruongDuLieu layThuCung() {
        return taoLuaChonMuc("pet", "Thú cưng", new NghiepVuService().layLuaChonThuCung());
    }

    /** Nạp danh sách lựa chọn khách hàng đang hoạt động cho các hộp thoại đặt lịch / đặt phòng. */
    public static List<LuaChon> taiLuaChonKhachHoatDong() {
        return DanhMucService.macDinh().layKhachHang().stream()
                .filter(k -> "Đang hoạt động".equals(k[8]))
                .map(
                        k ->
                                new LuaChon(
                                        String.valueOf(k[0]),
                                        k[0] + " — " + k[1] + " (" + k[2] + ")"))
                .toList();
    }
}
