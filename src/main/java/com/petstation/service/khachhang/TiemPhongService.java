package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.TiemPhongDAO;
import com.petstation.dao.khachhang.impl.TiemPhongDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;
import com.petstation.util.DuLieu;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public final class TiemPhongService {
    private final TiemPhongDAO tiemPhongDAO;
    private final NhatKyHeThongService nhatKyHeThongService;
    private final PhienDangNhapService phienDangNhapService;

    public TiemPhongService() {
        this(new TiemPhongDAOImpl());
    }

    public TiemPhongService(TiemPhongDAO tiemPhongDAO) {
        this(tiemPhongDAO, NhatKyHeThongService.macDinh(), PhienDangNhapService.macDinh());
    }

    public TiemPhongService(
            TiemPhongDAO tiemPhongDAO,
            NhatKyHeThongService nhatKyHeThongService,
            PhienDangNhapService phienDangNhapService) {
        this.tiemPhongDAO = Objects.requireNonNull(tiemPhongDAO, "tiemPhongDAO không được null");
        this.nhatKyHeThongService =
                Objects.requireNonNull(
                        nhatKyHeThongService, "nhatKyHeThongService không được null");
        this.phienDangNhapService =
                Objects.requireNonNull(
                        phienDangNhapService, "phienDangNhapService không được null");
    }

    private TiemPhongDAO.ThongTinThuCung timThuCung(String ma) {
        TiemPhongDAO.ThongTinThuCung thu = tiemPhongDAO.layThuCung(ma);
        if (thu == null)
            throw new IllegalArgumentException("Không tìm thấy thú cưng đang hoạt động.");
        return thu;
    }

    public void luu(String ma, long id, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_THU_CUNG);
        if (ten == null
                || ten.isBlank()
                || ten.length() > 120
                || ngay == null
                || ngay.isAfter(LocalDate.now())
                || han != null && han.isBefore(ngay))
            throw new IllegalArgumentException(
                    "Cần tên vaccine, ngày tiêm không ở tương lai và hạn không trước ngày tiêm.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            TiemPhongDAO.ThongTinThuCung thu = timThuCung(ma);
                            if (thu.ngaySinh != null && ngay.isBefore(thu.ngaySinh))
                                throw new IllegalArgumentException(
                                        "Ngày tiêm không được trước ngày sinh.");
                            tiemPhongDAO.luu(id, thu.maThuCung, ten.trim(), ngay, han, ghiChu);
                            nhatKyHeThongService.ghiNhatKy(
                                    "Lưu tiêm phòng", ma + " / " + ten + " / " + ngay);
                            return null;
                        });
    }

    public static final class KetQuaTiemPhong {
        public final boolean duDieuKien;
        public final boolean duTuoi;
        public final int thangTuoi;
        public final int soMui;
        public final LocalDate ngayMuiCuoi;
        public final long soNgayCachMuiCuoi;
        public final String thongDiep;
        public final LocalDate ngaySomNhatCoTheNhan;

        public KetQuaTiemPhong(
                boolean duDieuKien,
                boolean duTuoi,
                int thangTuoi,
                int soMui,
                LocalDate ngayMuiCuoi,
                long soNgayCachMuiCuoi,
                String thongDiep,
                LocalDate ngaySomNhatCoTheNhan) {
            this.duDieuKien = duDieuKien;
            this.duTuoi = duTuoi;
            this.thangTuoi = thangTuoi;
            this.soMui = soMui;
            this.ngayMuiCuoi = ngayMuiCuoi;
            this.soNgayCachMuiCuoi = soNgayCachMuiCuoi;
            this.thongDiep = thongDiep;
            this.ngaySomNhatCoTheNhan = ngaySomNhatCoTheNhan;
        }
    }

    public KetQuaTiemPhong danhGiaNhanSpa(String ma) {
        if (ma == null || ma.isBlank())
            return khongDat(false, 0, 0, null, 0, "Chưa chọn thú cưng để kiểm tra hồ sơ.", null);
        TiemPhongDAO.ThongTinThuCung thu = timThuCungAnToan(ma);
        if (thu == null)
            return khongDat(
                    false, 0, 0, null, 0, "Không tìm thấy hồ sơ thú cưng đang hoạt động.", null);
        LocalDate homNay = LocalDate.now();
        if (thu.ngaySinh == null)
            return khongDat(
                    false,
                    0,
                    0,
                    null,
                    0,
                    "Chưa có thông tin ngày sinh; thú cưng cần đủ 2 tháng tuổi để nhận Spa.",
                    null);
        int thangTuoi = DuLieu.tinhThangTuoi(thu.ngaySinh, homNay);
        LocalDate ngayDuTuoi = thu.ngaySinh.plusMonths(2);
        if (ngayDuTuoi.isAfter(homNay)) {
            long soNgayConLai = ChronoUnit.DAYS.between(homNay, ngayDuTuoi);
            String tuoi =
                    thangTuoi <= 0
                            ? (ChronoUnit.DAYS.between(thu.ngaySinh, homNay) + " ngày tuổi")
                            : (thangTuoi + " tháng tuổi");
            return khongDat(
                    false,
                    thangTuoi,
                    0,
                    null,
                    0,
                    "Chưa đủ tuổi: thú cưng hiện "
                            + tuoi
                            + ". Quy định cần đủ từ 2 tháng tuổi (sớm nhất từ "
                            + ngayDuTuoi.format(DinhDang.NGAY)
                            + ", còn "
                            + soNgayConLai
                            + " ngày).",
                    ngayDuTuoi);
        }
        return danhGiaSoMuiTiem(thu.maThuCung, thangTuoi, homNay);
    }

    private TiemPhongDAO.ThongTinThuCung timThuCungAnToan(String ma) {
        try {
            return timThuCung(ma);
        } catch (Exception e) {
            return null;
        }
    }

    private KetQuaTiemPhong danhGiaSoMuiTiem(String maThuCung, int thangTuoi, LocalDate homNay) {
        List<TiemPhongDAO.MucTiemPhong> lichSu = tiemPhongDAO.layLichSu(maThuCung);
        int soMui = lichSu.size();
        if (soMui == 0)
            return khongDat(
                    true,
                    thangTuoi,
                    0,
                    null,
                    0,
                    "Chưa ghi nhận hồ sơ tiêm phòng. Quy định cửa hàng yêu cầu tối thiểu 2 mũi"
                            + " vắc-xin trước khi nhận Spa.",
                    null);
        if (soMui < 2)
            return khongDat(
                    true,
                    thangTuoi,
                    soMui,
                    null,
                    0,
                    "Mới ghi nhận 1 mũi tiêm phòng. Quy định cửa hàng yêu cầu tối thiểu 2 mũi"
                            + " vắc-xin để đảm bảo kháng thể.",
                    null);
        LocalDate ngayMuiCuoi =
                lichSu.stream()
                        .map(r -> r.ngayTiem)
                        .filter(Objects::nonNull)
                        .max(LocalDate::compareTo)
                        .orElse(null);
        if (ngayMuiCuoi == null)
            return khongDat(
                    true,
                    thangTuoi,
                    soMui,
                    null,
                    0,
                    "Hồ sơ tiêm phòng thiếu ngày tiêm hợp lệ. Vui lòng kiểm tra lại sổ tiêm.",
                    null);
        long soNgayCachMuiCuoi = ChronoUnit.DAYS.between(ngayMuiCuoi, homNay);
        if (soNgayCachMuiCuoi < 14) {
            LocalDate ngaySomNhat = ngayMuiCuoi.plusDays(14);
            long soNgayCho = ChronoUnit.DAYS.between(homNay, ngaySomNhat);
            return khongDat(
                    true,
                    thangTuoi,
                    soMui,
                    ngayMuiCuoi,
                    soNgayCachMuiCuoi,
                    "Mũi tiêm gần nhất ("
                            + ngayMuiCuoi.format(DinhDang.NGAY)
                            + ") mới qua "
                            + soNgayCachMuiCuoi
                            + " ngày. Quy định cần cách ít nhất 14 ngày để an toàn (sớm nhất từ "
                            + ngaySomNhat.format(DinhDang.NGAY)
                            + ", còn "
                            + soNgayCho
                            + " ngày).",
                    ngaySomNhat);
        }
        return new KetQuaTiemPhong(
                true,
                true,
                thangTuoi,
                soMui,
                ngayMuiCuoi,
                soNgayCachMuiCuoi,
                "Đạt điều kiện hồ sơ: đủ tuổi ("
                        + thangTuoi
                        + " tháng), đã ghi nhận "
                        + soMui
                        + " mũi tiêm, mũi gần nhất cách "
                        + soNgayCachMuiCuoi
                        + " ngày.",
                null);
    }

    private static KetQuaTiemPhong khongDat(
            boolean duTuoi,
            int thangTuoi,
            int soMui,
            LocalDate ngayMuiCuoi,
            long ngayCach,
            String tb,
            LocalDate somNhat) {
        return new KetQuaTiemPhong(
                false, duTuoi, thangTuoi, soMui, ngayMuiCuoi, ngayCach, tb, somNhat);
    }

    public void kiemTraNhanSpa(String ma) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        KetQuaTiemPhong kq = danhGiaNhanSpa(ma);
        if (!kq.duDieuKien) {
            throw new IllegalArgumentException(kq.thongDiep);
        }
    }
}
