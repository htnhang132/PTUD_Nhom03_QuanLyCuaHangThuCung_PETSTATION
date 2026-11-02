package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.LichLamViecDAO;
import com.petstation.dao.impl.LichLamViecDAOImpl;
import com.petstation.model.Quyen;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho lịch làm việc dịch vụ. */
public final class LichLamViecService {
    private static final LichLamViecDAO lichLamViecDAO = new LichLamViecDAOImpl();

    /** Các ca làm việc theo chuẩn OOAD (2 ca/ngày: Ca sáng CA01, Ca chiều CA02). */
    public static final String[] CAC_CA_LAM_VIEC = {"Ca sáng", "Ca chiều"};

    /** Khung giờ các ca làm việc. */
    public static final String[] CA_LAM_VIEC_CAC_THOI_GIAN = {
        "07:30 – 14:30", "14:30 – 21:30"
    };

    /** Số nhân viên tối thiểu mỗi ca. */
    public static final int[] NHO_NHAT_NHAN_VIEN = {2, 2};

    /** Các mã ca làm việc trong CSDL. */
    private static final String[] CAC_MA = {"CA01", "CA02"};

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

        Muc(LocalDate ngay, int caLamViec, String ma, String loai) {
            this.ngay = ngay;
            this.caLamViec = caLamViec;
            this.maNhanVien = ma;
            this.loai = loai;
        }

        public boolean kiemTraNghiPhep() {
            return "LEAVE".equals(loai);
        }
    }

    private LichLamViecService() {}

    public static LocalDate layThuHaiCuaTuan(LocalDate ngay) {
        return ngay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private static int anhXaMaCa(String maCa) {
        if ("CA01".equalsIgnoreCase(maCa) || "CA_SANG".equalsIgnoreCase(maCa)) return 0;
        if ("CA02".equalsIgnoreCase(maCa)
                || "CA_CHIEU".equalsIgnoreCase(maCa)
                || "CA_TOI".equalsIgnoreCase(maCa)) return 1;
        return Arrays.asList(CAC_MA).indexOf(maCa);
    }

    public static List<Muc> layKhoang(LocalDate tu, LocalDate den) {
        List<Muc> ketQua = new ArrayList<>();
        for (Object[] dongDuLieu : lichLamViecDAO.timKhoang(tu, den)) {
            int caLamViec = anhXaMaCa(chuyenThanhChuoi(dongDuLieu[1]));
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

    public static List<Muc> layLichTrongTuan(LocalDate thuHai) {
        return layKhoang(thuHai, thuHai.plusDays(7));
    }

    public static List<Muc> layO(LocalDate ngay, int caLamViec) {
        return layKhoang(ngay, ngay.plusDays(1)).stream()
                .filter(suKien -> suKien.caLamViec == caLamViec)
                .toList();
    }

    public static long demCaLamViec(LocalDate ngay, int caLamViec) {
        return layO(ngay, caLamViec).stream().filter(suKien -> !suKien.kiemTraNghiPhep()).count();
    }

    public static String phanCong(LocalDate ngay, int caLamViec, String ma) {
        yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (caLamViec < 0 || caLamViec >= CAC_MA.length) return "Ca không hợp lệ.";
        return thucHienGiaoDich(
                ketNoi -> {
                    long nhanVien = DanhMucService.layId("nhan_vien", "ma_nhan_vien", ma);
                    lichLamViecDAO.demPhanCong(nhanVien);
                    if (lichLamViecDAO.demNhanVienDangLam(nhanVien) == 0)
                        return "Nhân viên đã nghỉ việc.";
                    if (lichLamViecDAO.demLichTrungNgayCa(nhanVien, ngay, CAC_MA[caLamViec]) > 0)
                        return "Nhân viên đã có ca hoặc nghỉ phép ngày này.";
                    lichLamViecDAO.capNhatPhanCong(nhanVien, ngay, CAC_MA[caLamViec]);
                    lichLamViecDAO.themLichLamViec(nhanVien, ngay, CAC_MA[caLamViec]);
                    thongBao();
                    return null;
                });
    }

    public static void xoa(Muc muc) {
        yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        lichLamViecDAO.capNhatXoa(muc.maNhanVien, muc.ngay, CAC_MA[muc.caLamViec]);
        thongBao();
    }

    public static void danhDauNghiPhepDaDuyet(long nhanVien, LocalDate ngay) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);
        thucHienGiaoDich(
                ketNoi -> {
                    lichLamViecDAO.demPhanCong(nhanVien);
                    if (lichLamViecDAO.capNhatDanhDauNghiPhepDaDuyet(nhanVien, ngay) == 0)
                        lichLamViecDAO.themLichNghiPhep(nhanVien, ngay);
                    return null;
                });
        thongBao();
    }

    public static void danhDauNghiPhep(String ma, LocalDate ngay) {
        danhDauNghiPhepDaDuyet(DanhMucService.layId("nhan_vien", "ma_nhan_vien", ma), ngay);
    }

    public static int tinhGioTrongThang(String ma, LocalDate ngay) {
        LocalDate dauTien = ngay.withDayOfMonth(1);
        return (int) lichLamViecDAO.demThangSoGio(ma, dauTien, dauTien.plusMonths(1));
    }

    public static int tinhGioTrongTuan(String ma, LocalDate thuHai) {
        return (int) lichLamViecDAO.demThangSoGio(ma, thuHai, thuHai.plusDays(7));
    }

    public static String saoChepLichTuan(LocalDate tuTuan, LocalDate denTuan) {
        yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (tuTuan.equals(denTuan)) return "Tuần nguồn và tuần đích không được trùng nhau.";
        return thucHienGiaoDich(
                ketNoi -> {
                    List<Muc> danhSachCu = layLichTrongTuan(tuTuan);
                    if (danhSachCu.isEmpty())
                        return "Tuần trước chưa có lịch làm việc để sao chép.";
                    int daChep = 0;
                    for (Muc m : danhSachCu) {
                        if (m.kiemTraNghiPhep()) continue;
                        long offsetDays =
                                java.time.temporal.ChronoUnit.DAYS.between(tuTuan, m.ngay);
                        LocalDate ngayMoi = denTuan.plusDays(offsetDays);
                        String loi = phanCong(ngayMoi, m.caLamViec, m.maNhanVien);
                        if (loi == null) daChep++;
                    }
                    thongBao();
                    return "Đã sao chép thành công " + daChep + " lượt phân ca sang tuần mới.";
                });
    }

    public static String hoanDoiCa(
            LocalDate ngay1, int ca1, String ma1, LocalDate ngay2, int ca2, String ma2) {
        yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (ma1.equals(ma2)) return "Không thể hoán đổi ca với chính mình.";
        return thucHienGiaoDich(
                ketNoi -> {
                    lichLamViecDAO.capNhatXoa(ma1, ngay1, CAC_MA[ca1]);
                    lichLamViecDAO.capNhatXoa(ma2, ngay2, CAC_MA[ca2]);
                    String err1 = phanCong(ngay1, ca1, ma2);
                    if (err1 != null)
                        throw new IllegalArgumentException("Không thể gán " + ma2 + ": " + err1);
                    String err2 = phanCong(ngay2, ca2, ma1);
                    if (err2 != null)
                        throw new IllegalArgumentException("Không thể gán " + ma1 + ": " + err2);
                    thongBao();
                    return null;
                });
    }

    public static void khoaLichTuan(LocalDate thuHai) {
        yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        lichLamViecDAO.khoaLichKhoang(thuHai, thuHai.plusDays(7));
        thongBao();
    }

    public static void themBoLangNghe(Runnable boLangNghe) {
        CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    private static void thongBao() {
        for (Runnable boLangNghe : List.copyOf(CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    public static void xoaBoLangNghe() {
        CAC_BO_LANG_NGHE.clear();
    }
}
