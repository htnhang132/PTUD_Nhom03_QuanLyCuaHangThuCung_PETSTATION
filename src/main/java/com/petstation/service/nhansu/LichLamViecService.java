package com.petstation.service.nhansu;

import com.petstation.dao.nhansu.LichLamViecDAO;
import com.petstation.dao.nhansu.impl.LichLamViecDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.LoaiLichLamViec;
import com.petstation.model.chung.Quyen;
import com.petstation.model.nhansu.LichLamViec;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.hethong.PhienDangNhapService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LichLamViecService {
    private static final LichLamViecService MAC_DINH = new LichLamViecService();

    private final LichLamViecDAO lichLamViecDAO;
    private final PhienDangNhapService phienService;
    private final List<BoLangNgheSuKien<String>> cacBoLangNghe = new CopyOnWriteArrayList<>();

    public static final String[] CAC_CA_LAM_VIEC = {"Ca sáng", "Ca chiều"};

    public static final String[] CA_LAM_VIEC_CAC_THOI_GIAN = {"07:30 – 14:30", "14:30 – 21:30"};

    public static final int[] NHO_NHAT_NHAN_VIEN = {2, 2};

    private static final String[] CAC_MA = {"CA01", "CA02"};

    public static final class Muc {
        private final LocalDate ngay;
        private final int caLamViec;
        private final String maNhanVien;
        private final String loai;

        Muc(LocalDate ngay, int caLamViec, String ma, String loai) {
            this.ngay = ngay;
            this.caLamViec = caLamViec;
            this.maNhanVien = ma;
            this.loai = loai;
        }

        public LocalDate layNgay() {
            return ngay;
        }

        public int layCaLamViec() {
            return caLamViec;
        }

        public String layMaNhanVien() {
            return maNhanVien;
        }

        public boolean kiemTraNghiPhep() {
            return LoaiLichLamViec.NGHI_PHEP.maDB().equals(loai);
        }
    }

    public LichLamViecService() {
        this(new LichLamViecDAOImpl(), PhienDangNhapService.macDinh());
    }

    public LichLamViecService(LichLamViecDAO lichLamViecDAO) {
        this(lichLamViecDAO, PhienDangNhapService.macDinh());
    }

    public LichLamViecService(LichLamViecDAO lichLamViecDAO, PhienDangNhapService phienService) {
        this.lichLamViecDAO = Objects.requireNonNull(lichLamViecDAO);
        this.phienService = Objects.requireNonNull(phienService);
    }

    public static LichLamViecService macDinh() {
        return MAC_DINH;
    }

    public LocalDate layThuHaiCuaTuan(LocalDate ngay) {
        return ngay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private int anhXaMaCa(String maCa) {
        if ("CA01".equalsIgnoreCase(maCa) || "CA_SANG".equalsIgnoreCase(maCa)) return 0;
        if ("CA02".equalsIgnoreCase(maCa)
                || "CA_CHIEU".equalsIgnoreCase(maCa)
                || "CA_TOI".equalsIgnoreCase(maCa)) return 1;
        return Arrays.asList(CAC_MA).indexOf(maCa);
    }

    public List<Muc> layKhoang(LocalDate tu, LocalDate den) {
        phienService.yeuCau(Quyen.XEM_LICH_LAM_VIEC);
        List<Muc> ketQua = new ArrayList<>();
        for (LichLamViec dongDuLieu : lichLamViecDAO.timKhoang(tu, den)) {
            String maCa =
                    dongDuLieu.getCaLamViec() != null ? dongDuLieu.getCaLamViec().getMaCa() : "";
            String maNhanVien =
                    dongDuLieu.getNhanVien() != null
                            ? dongDuLieu.getNhanVien().getMaNhanVien()
                            : "";
            int caLamViec = anhXaMaCa(maCa);
            if (caLamViec >= 0) {
                ketQua.add(
                        new Muc(
                                dongDuLieu.getNgayLam(),
                                caLamViec,
                                maNhanVien,
                                dongDuLieu.getLoai()));
            }
        }
        return ketQua;
    }

    public List<Muc> layLichTrongTuan(LocalDate thuHai) {
        return layKhoang(thuHai, thuHai.plusDays(7));
    }

    public List<Muc> layO(LocalDate ngay, int caLamViec) {
        return layKhoang(ngay, ngay.plusDays(1)).stream()
                .filter(suKien -> suKien.caLamViec == caLamViec)
                .toList();
    }

    public String phanCong(LocalDate ngay, int caLamViec, String ma) {
        phienService.yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (caLamViec < 0 || caLamViec >= CAC_MA.length) return "Ca không hợp lệ.";
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (lichLamViecDAO.demPhanCong(ma) == 0)
                                return "Nhân viên không tồn tại.";
                            if (lichLamViecDAO.demNhanVienDangLam(ma) == 0)
                                return "Nhân viên đã nghỉ việc.";
                            if (lichLamViecDAO.demLichTrungNgayCa(ma, ngay, CAC_MA[caLamViec]) > 0)
                                return "Nhân viên đã có ca hoặc nghỉ phép ngày này.";
                            lichLamViecDAO.capNhatPhanCong(ma, ngay, CAC_MA[caLamViec]);
                            lichLamViecDAO.themLichLamViec(ma, ngay, CAC_MA[caLamViec]);
                            thongBao("PHAN_CONG");
                            return null;
                        });
    }

    public void xoa(Muc muc) {
        phienService.yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (muc == null || muc.caLamViec < 0 || muc.caLamViec >= CAC_MA.length)
            throw new IllegalArgumentException("Ca làm việc không hợp lệ.");
        lichLamViecDAO.capNhatXoa(muc.maNhanVien, muc.ngay, CAC_MA[muc.caLamViec]);
        thongBao("XOA_LICH");
    }

    public void danhDauNghiPhepDaDuyet(String maNhanVien, LocalDate ngay) {
        phienService.yeuCau(Quyen.XU_LY_PHE_DUYET);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            lichLamViecDAO.demPhanCong(maNhanVien);
                            if (lichLamViecDAO.capNhatDanhDauNghiPhepDaDuyet(maNhanVien, ngay) == 0)
                                lichLamViecDAO.themLichNghiPhep(maNhanVien, ngay);
                            return null;
                        });
        thongBao(LoaiLichLamViec.NGHI_PHEP.maDB());
    }

    public int tinhGioTrongThang(String ma, LocalDate ngay) {
        phienService.yeuCau(Quyen.XEM_LICH_LAM_VIEC);
        LocalDate dauTien = ngay.withDayOfMonth(1);
        return (int) lichLamViecDAO.demThangSoGio(ma, dauTien, dauTien.plusMonths(1));
    }

    public int tinhGioTrongTuan(String ma, LocalDate thuHai) {
        phienService.yeuCau(Quyen.XEM_LICH_LAM_VIEC);
        return (int) lichLamViecDAO.demThangSoGio(ma, thuHai, thuHai.plusDays(7));
    }

    public Map<String, Integer> tinhGioTatCaTrongTuan(LocalDate thuHai) {
        phienService.yeuCau(Quyen.XEM_LICH_LAM_VIEC);
        return lichLamViecDAO.layGioTheoNhanVienKhoang(thuHai, thuHai.plusDays(7));
    }

    public String saoChepLichTuan(LocalDate tuTuan, LocalDate denTuan) {
        phienService.yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (tuTuan.equals(denTuan)) return "Tuần nguồn và tuần đích không được trùng nhau.";
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            List<Muc> danhSachCu = layLichTrongTuan(tuTuan);
                            if (danhSachCu.isEmpty())
                                return "Tuần trước chưa có lịch làm việc để sao chép.";
                            int daChep = 0;
                            for (Muc m : danhSachCu) {
                                if (m.kiemTraNghiPhep()) continue;
                                long offsetDays = ChronoUnit.DAYS.between(tuTuan, m.ngay);
                                LocalDate ngayMoi = denTuan.plusDays(offsetDays);
                                String loi = phanCong(ngayMoi, m.caLamViec, m.maNhanVien);
                                if (loi == null) daChep++;
                            }
                            thongBao("SAO_CHEP_TUAN");
                            return "Đã sao chép thành công "
                                    + daChep
                                    + " lượt phân ca sang tuần mới.";
                        });
    }

    public String hoanDoiCa(
            LocalDate ngay1, int ca1, String ma1, LocalDate ngay2, int ca2, String ma2) {
        phienService.yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        if (ma1.equals(ma2)) return "Không thể hoán đổi ca với chính mình.";
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            lichLamViecDAO.capNhatXoa(ma1, ngay1, CAC_MA[ca1]);
                            lichLamViecDAO.capNhatXoa(ma2, ngay2, CAC_MA[ca2]);
                            String err1 = phanCong(ngay1, ca1, ma2);
                            if (err1 != null)
                                throw new IllegalArgumentException(
                                        "Không thể gán " + ma2 + ": " + err1);
                            String err2 = phanCong(ngay2, ca2, ma1);
                            if (err2 != null)
                                throw new IllegalArgumentException(
                                        "Không thể gán " + ma1 + ": " + err2);
                            thongBao("HOAN_DOI_CA");
                            return null;
                        });
    }

    public void khoaLichTuan(LocalDate thuHai) {
        phienService.yeuCau(Quyen.QUAN_LY_LICH_LAM_VIEC);
        lichLamViecDAO.khoaLichKhoang(thuHai, thuHai.plusDays(7));
        thongBao("KHOA_LICH_TUAN");
    }

    public void themBoLangNghe(BoLangNgheSuKien<String> boLangNghe) {
        if (boLangNghe != null && !cacBoLangNghe.contains(boLangNghe)) {
            cacBoLangNghe.add(boLangNghe);
        }
    }

    public void xoaBoLangNghe(BoLangNgheSuKien<String> boLangNghe) {
        cacBoLangNghe.remove(boLangNghe);
    }

    private void thongBao(String suKien) {
        QuanLyGiaoDich.sauKhiCommit(
                () -> {
                    for (BoLangNgheSuKien<String> boLangNghe : cacBoLangNghe) {
                        boLangNghe.khiThayDoi(suKien);
                    }
                });
    }

    public void xoaBoLangNghe() {
        cacBoLangNghe.clear();
    }
}
