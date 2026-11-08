package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.TiemPhongDAO;
import com.petstation.dao.impl.TiemPhongDAOImpl;
import com.petstation.model.Quyen;

import java.time.LocalDate;
import java.util.List;

public final class TiemPhongService {
    private static final TiemPhongDAO tiemPhongDAO = new TiemPhongDAOImpl();

    private TiemPhongService() {}

    private static Object[] timThuCung(String ma) {
        var cacDong = tiemPhongDAO.layThuCung(ma);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Không tìm thấy thú cưng đang hoạt động.");
        return cacDong.get(0);
    }

    public static List<Object[]> layLichSu(String ma) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_THU_CUNG);
        return thucHienGiaoDich(
                c -> tiemPhongDAO.layLichSu(((Number) timThuCung(ma)[0]).longValue()));
    }

    public static void luu(
            String ma, long id, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_THU_CUNG);
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
                    tiemPhongDAO.luu(
                            id, ((Number) thu[0]).longValue(), ten.trim(), ngay, han, ghiChu);
                    NhatKyHeThongService.ghiNhatKy(
                            "Lưu tiêm phòng", ma + " / " + ten + " / " + ngay);
                    return null;
                });
    }

    public static void xoa(String ma, long id) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_THU_CUNG);
        thucHienGiaoDich(
                c -> {
                    tiemPhongDAO.xoa(id, ((Number) timThuCung(ma)[0]).longValue());
                    NhatKyHeThongService.ghiNhatKy("Xóa tiêm phòng", ma + " / " + id);
                    return null;
                });
    }

    public record KetQuaTiemPhong(
            boolean duDieuKien,
            boolean duTuoi,
            int thangTuoi,
            int soMui,
            LocalDate ngayMuiCuoi,
            long soNgayCachMuiCuoi,
            String thongDiep,
            LocalDate ngaySomNhatCoTheNhan) {
    }

    public static KetQuaTiemPhong danhGiaNhanSpa(String ma) {
        if (ma == null || ma.isBlank()) {
            return new KetQuaTiemPhong(false, false, 0, 0, null, 0, "Chưa chọn thú cưng để kiểm tra hồ sơ.", null);
        }
        Object[] thu;
        try {
            thu = timThuCung(ma);
        } catch (Exception e) {
            return new KetQuaTiemPhong(false, false, 0, 0, null, 0, "Không tìm thấy hồ sơ thú cưng đang hoạt động.", null);
        }
        LocalDate homNay = LocalDate.now();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (thu[1] == null) {
            return new KetQuaTiemPhong(false, false, 0, 0, null, 0, "Chưa có thông tin ngày sinh; thú cưng cần đủ 2 tháng tuổi để nhận Spa.", null);
        }
        LocalDate ngaySinh = ((java.sql.Date) thu[1]).toLocalDate();
        int thangTuoi = (int) java.time.temporal.ChronoUnit.MONTHS.between(ngaySinh, homNay);
        boolean duTuoi = !ngaySinh.plusMonths(2).isAfter(homNay);
        if (!duTuoi) {
            LocalDate ngayDuTuoi = ngaySinh.plusMonths(2);
            long soNgayConLai = java.time.temporal.ChronoUnit.DAYS.between(homNay, ngayDuTuoi);
            return new KetQuaTiemPhong(false, false, Math.max(0, thangTuoi), 0, null, 0,
                    "Chưa đủ tuổi: thú cưng hiện " + (thangTuoi <= 0 ? (java.time.temporal.ChronoUnit.DAYS.between(ngaySinh, homNay) + " ngày tuổi") : (thangTuoi + " tháng tuổi"))
                            + ". Quy định cần đủ từ 2 tháng tuổi (sớm nhất từ " + ngayDuTuoi.format(dtf) + ", còn " + soNgayConLai + " ngày).",
                    ngayDuTuoi);
        }

        var lichSu = tiemPhongDAO.layLichSu(((Number) thu[0]).longValue());
        int soMui = lichSu.size();
        if (soMui == 0) {
            return new KetQuaTiemPhong(false, true, thangTuoi, 0, null, 0,
                    "Chưa ghi nhận hồ sơ tiêm phòng. Quy định cửa hàng yêu cầu tối thiểu 2 mũi vắc-xin trước khi nhận Spa.", null);
        }
        if (soMui < 2) {
            return new KetQuaTiemPhong(false, true, thangTuoi, soMui, null, 0,
                    "Mới ghi nhận 1 mũi tiêm phòng. Quy định cửa hàng yêu cầu tối thiểu 2 mũi vắc-xin để đảm bảo kháng thể.", null);
        }

        LocalDate ngayMuiCuoi = lichSu.stream()
                .filter(r -> r[2] instanceof java.sql.Date)
                .map(r -> ((java.sql.Date) r[2]).toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(null);

        if (ngayMuiCuoi == null) {
            return new KetQuaTiemPhong(false, true, thangTuoi, soMui, null, 0,
                    "Hồ sơ tiêm phòng thiếu ngày tiêm hợp lệ. Vui lòng kiểm tra lại sổ tiêm.", null);
        }

        long soNgayCachMuiCuoi = java.time.temporal.ChronoUnit.DAYS.between(ngayMuiCuoi, homNay);
        if (soNgayCachMuiCuoi < 14) {
            LocalDate ngaySomNhat = ngayMuiCuoi.plusDays(14);
            long soNgayCho = java.time.temporal.ChronoUnit.DAYS.between(homNay, ngaySomNhat);
            return new KetQuaTiemPhong(false, true, thangTuoi, soMui, ngayMuiCuoi, soNgayCachMuiCuoi,
                    "Mũi tiêm gần nhất (" + ngayMuiCuoi.format(dtf) + ") mới qua " + soNgayCachMuiCuoi
                            + " ngày. Quy định cần cách ít nhất 14 ngày để an toàn (sớm nhất từ " + ngaySomNhat.format(dtf) + ", còn " + soNgayCho + " ngày).",
                    ngaySomNhat);
        }

        return new KetQuaTiemPhong(true, true, thangTuoi, soMui, ngayMuiCuoi, soNgayCachMuiCuoi,
                "Đạt điều kiện hồ sơ: đủ tuổi (" + thangTuoi + " tháng), đã ghi nhận " + soMui + " mũi tiêm, mũi gần nhất cách " + soNgayCachMuiCuoi + " ngày.",
                null);
    }

    public static void kiemTraNhanSpa(String ma) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        KetQuaTiemPhong kq = danhGiaNhanSpa(ma);
        if (!kq.duDieuKien()) {
            throw new IllegalArgumentException(kq.thongDiep());
        }
    }
}
