package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcLichLamViecDAO;
import com.petstation.model.Quyen;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho lịch làm việc dịch vụ. */
public final class LichLamViecService {
    /** Các ca làm việc. */
    public static final String[] CAC_CA_LAM_VIEC = {"Sáng", "Chiều", "Tối"};

    /** Ca làm việc các thời gian. */
    public static final String[] CA_LAM_VIEC_CAC_THOI_GIAN = {
        "07:00 – 12:00", "12:00 – 17:00", "17:00 – 21:00"
    };

    /** Nhỏ nhất nhân viên. */
    public static final int[] NHO_NHAT_NHAN_VIEN = {2, 2, 1};

    /** Các mã. */
    private static final String[] CAC_MA = {"CA_SANG", "CA_CHIEU", "CA_TOI"};

    /** Các bộ lắng nghe. */
    private static final List<Runnable> CAC_BO_LANG_NGHE = new ArrayList<>();

    /** Điều phối nghiệp vụ và kiểm tra dữ liệu cho mục. */
    public static final class Muc {
        /** Ngày. */
        public final LocalDate ngay;

        /** Ca làm việc. */
        public final int caLamViec;

        /** Mã nhân viên. */
        public final String maNhanVien;

        /** Loại. */
        public String loai;

        /**
         * Khởi tạo Muc với các giá trị được cung cấp.
         *
         * @param ngay ngày
         * @param caLamViec ca làm việc
         * @param ma mã
         * @param loai loại
         */
        Muc(LocalDate ngay, int caLamViec, String ma, String loai) {
            this.ngay = ngay;
            this.caLamViec = caLamViec;
            this.maNhanVien = ma;
            this.loai = loai;
        }

        /**
         * Kiểm tra nghỉ phép trong Muc.
         *
         * @return kết quả kiểm tra điều kiện xử lý
         */
        public boolean kiemTraNghiPhep() {
            return "LEAVE".equals(loai);
        }
    }

    /** Khởi tạo LichLamViecService với các giá trị được cung cấp. */
    private LichLamViecService() {}

    /**
     * Lấy thứ hai của tuần trong LichLamViecService.
     *
     * @param ngay ngày
     * @return kết quả lấy thứ hai của tuần
     */
    public static LocalDate layThuHaiCuaTuan(LocalDate ngay) {
        return ngay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Lấy khoảng trong LichLamViecService.
     *
     * @param tu từ
     * @param den đến
     * @return kết quả lấy khoảng
     */
    private static List<Muc> layKhoang(LocalDate tu, LocalDate den) {
        // Kết quả.
        List<Muc> ketQua = new ArrayList<>();
        for (Object[] dongDuLieu : JdbcLichLamViecDAO.timKhoang(tu, den)) {
            // Ca làm việc.
            int caLamViec = Arrays.asList(CAC_MA).indexOf(chuyenThanhChuoi(dongDuLieu[1]));
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (caLamViec >= 0)
                ketQua.add(
                        new Muc(
                                ((java.sql.Date) dongDuLieu[0]).toLocalDate(),
                                caLamViec,
                                chuyenThanhChuoi(dongDuLieu[2]),
                                chuyenThanhChuoi(dongDuLieu[3])));
        }
        return ketQua;
    }

    /**
     * Lấy lịch trong tuần trong LichLamViecService.
     *
     * @param thuHai thứ hai
     * @return kết quả lấy lịch trong tuần
     */
    public static List<Muc> layLichTrongTuan(LocalDate thuHai) {
        return layKhoang(thuHai, thuHai.plusDays(7));
    }

    /**
     * Lấy ô trong LichLamViecService.
     *
     * @param ngay ngày
     * @param caLamViec ca làm việc
     * @return kết quả lấy ô
     */
    public static List<Muc> layO(LocalDate ngay, int caLamViec) {
        return layKhoang(ngay, ngay.plusDays(1)).stream()
                .filter(suKien -> suKien.caLamViec == caLamViec)
                .toList();
    }

    /**
     * Đếm ca làm việc trong LichLamViecService.
     *
     * @param ngay ngày
     * @param caLamViec ca làm việc
     * @return kết quả đếm ca làm việc
     */
    public static long demCaLamViec(LocalDate ngay, int caLamViec) {
        return layO(ngay, caLamViec).stream().filter(suKien -> !suKien.kiemTraNghiPhep()).count();
    }

    /**
     * Phân công trong LichLamViecService.
     *
     * @param ngay ngày
     * @param caLamViec ca làm việc
     * @param ma mã
     * @return kết quả phân công
     */
    public static String phanCong(LocalDate ngay, int caLamViec, String ma) {
        yeuCau(Quyen.SCHEDULE_MANAGE);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (caLamViec < 0 || caLamViec >= CAC_MA.length) return "Ca không hợp lệ.";
        return thucHienGiaoDich(
                ketNoi -> {
                    // Nhân viên.
                    long nhanVien = DanhMucService.layId("nhan_vien", "ma_nhan_vien", ma);
                    JdbcLichLamViecDAO.demPhanCong(nhanVien);
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (JdbcLichLamViecDAO.demPhanCong2(nhanVien) == 0)
                        return "Nhân viên đã nghỉ việc.";
                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                    if (JdbcLichLamViecDAO.demPhanCong3(nhanVien, ngay, CAC_MA[caLamViec]) > 0)
                        return "Nhân viên đã có ca hoặc nghỉ phép ngày này.";
                    JdbcLichLamViecDAO.capNhatPhanCong(nhanVien, ngay, CAC_MA[caLamViec]);
                    JdbcLichLamViecDAO.capNhatPhanCong2(nhanVien, ngay, CAC_MA[caLamViec]);
                    thongBao();
                    return null;
                });
    }

    /**
     * Xóa trong LichLamViecService.
     *
     * @param muc mục
     */
    public static void xoa(Muc muc) {
        yeuCau(Quyen.SCHEDULE_MANAGE);
        JdbcLichLamViecDAO.capNhatXoa(muc.maNhanVien, muc.ngay, CAC_MA[muc.caLamViec]);
        thongBao();
    }

    /**
     * Đánh dấu nghỉ phép đã duyệt trong LichLamViecService.
     *
     * @param nhanVien nhân viên
     * @param ngay ngày
     */
    public static void danhDauNghiPhepDaDuyet(long nhanVien, LocalDate ngay) {
        yeuCau(Quyen.APPROVAL_PROCESS);
        thucHienGiaoDich(
                ketNoi -> {
                    JdbcLichLamViecDAO.demDanhDauNghiPhepDaDuyet(nhanVien);
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (JdbcLichLamViecDAO.capNhatDanhDauNghiPhepDaDuyet(nhanVien, ngay) == 0)
                        JdbcLichLamViecDAO.capNhatDanhDauNghiPhepDaDuyet2(nhanVien, ngay);
                    return null;
                });
        thongBao();
    }

    /**
     * Đánh dấu nghỉ phép trong LichLamViecService.
     *
     * @param ma mã
     * @param ngay ngày
     */
    public static void danhDauNghiPhep(String ma, LocalDate ngay) {
        danhDauNghiPhepDaDuyet(DanhMucService.layId("nhan_vien", "ma_nhan_vien", ma), ngay);
    }

    /**
     * Tính giờ trong tháng trong LichLamViecService.
     *
     * @param ma mã
     * @param ngay ngày
     * @return kết quả tính giờ trong tháng
     */
    public static int tinhGioTrongThang(String ma, LocalDate ngay) {
        // Đầu tiên.
        LocalDate dauTien = ngay.withDayOfMonth(1);
        return (int) JdbcLichLamViecDAO.demThangSoGio(ma, dauTien, dauTien.plusMonths(1));
    }

    /**
     * Thêm bộ lắng nghe trong LichLamViecService.
     *
     * @param boLangNghe bộ lắng nghe
     */
    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    /** Thông báo trong LichLamViecService. */
    private static void thongBao() {
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }
}
