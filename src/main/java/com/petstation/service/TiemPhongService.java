package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.dao.impl.JdbcTiemPhongDAO;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.util.List;

/** Quản lý mũi tiêm và sàng lọc điều kiện tiếp nhận Spa. */
public final class TiemPhongService {
    private TiemPhongService() {}

    private static Object[] timThuCung(String ma) {
        var cacDong = JdbcTiemPhongDAO.layThuCung(ma);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Không tìm thấy thú cưng đang hoạt động.");
        return cacDong.get(0);
    }

    public static List<Object[]> layLichSu(String ma) {
        PhienDangNhapService.yeuCau(Quyen.PET_MANAGE);
        return thucHienGiaoDich(
                c -> JdbcTiemPhongDAO.layLichSu(((Number) timThuCung(ma)[0]).longValue()));
    }

    public static void luu(
            String ma, long id, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        PhienDangNhapService.yeuCau(Quyen.PET_MANAGE);
        if (ten == null
                || ten.isBlank()
                || ten.length() > 120
                || ngay == null
                || ngay.isAfter(LocalDate.now())
                || han != null && han.isBefore(ngay))
            throw new IllegalArgumentException(
                    "Cần tên vaccine, ngày tiêm không ở tương lai và hạn không trước ngày tiêm.");
        thucHienGiaoDich(
                c -> {
                    Object[] thu = timThuCung(ma);
                    if (thu[1] != null && ngay.isBefore(((java.sql.Date) thu[1]).toLocalDate()))
                        throw new IllegalArgumentException("Ngày tiêm không được trước ngày sinh.");
                    JdbcTiemPhongDAO.luu(
                            id, ((Number) thu[0]).longValue(), ten.trim(), ngay, han, ghiChu);
                    NhatKyHeThongService.ghiNhatKy(
                            "Lưu tiêm phòng", ma + " / " + ten + " / " + ngay);
                    return null;
                });
    }

    public static void xoa(String ma, long id) {
        PhienDangNhapService.yeuCau(Quyen.PET_MANAGE);
        thucHienGiaoDich(
                c -> {
                    JdbcTiemPhongDAO.xoa(id, ((Number) timThuCung(ma)[0]).longValue());
                    NhatKyHeThongService.ghiNhatKy("Xóa tiêm phòng", ma + " / " + id);
                    return null;
                });
    }

    /** Gọi trong transaction tiếp nhận, khóa hồ sơ cùng lúc với việc kiểm tra. */
    public static void kiemTraNhanSpa(String ma) {
        PhienDangNhapService.yeuCau(Quyen.SPA_MANAGE);
        Object[] thu = timThuCung(ma);
        LocalDate homNay = LocalDate.now();
        if (thu[1] == null || ((java.sql.Date) thu[1]).toLocalDate().plusMonths(2).isAfter(homNay))
            throw new IllegalArgumentException(
                    "Cần bổ sung ngày sinh; thú cưng phải đủ 2 tháng tuổi để nhận Spa.");
        var lichSu = JdbcTiemPhongDAO.layLichSu(((Number) thu[0]).longValue());
        if (lichSu.size() < 2)
            throw new IllegalArgumentException(
                    "Cần bổ sung hồ sơ đủ 2 mũi tiêm phòng trước khi nhận Spa.");
        LocalDate ganNhat = ((java.sql.Date) lichSu.get(0)[2]).toLocalDate();
        if (ganNhat.plusDays(14).isAfter(homNay))
            throw new IllegalArgumentException(
                    "Mũi tiêm gần nhất phải cách ngày nhận Spa ít nhất 14 ngày.");
    }
}
