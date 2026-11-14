package com.petstation.service.spa;

import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.spa.PhieuTiepNhanSpaDAO;
import com.petstation.dao.spa.impl.PhieuTiepNhanSpaDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.SpaDTO;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiLichHen;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.spa.LichHen;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.ThuCungService;
import com.petstation.service.khachhang.TiemPhongService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class SpaService {
    private final PhieuTiepNhanSpaDAO xuLySpaDAO;
    private final TiemPhongService tiemPhongService;

    public SpaService() {
        this(new PhieuTiepNhanSpaDAOImpl(), new TiemPhongService());
    }

    public SpaService(PhieuTiepNhanSpaDAO xuLySpaDAO) {
        this(xuLySpaDAO, new TiemPhongService());
    }

    public SpaService(PhieuTiepNhanSpaDAO xuLySpaDAO, TiemPhongService tiemPhongService) {
        this.xuLySpaDAO = Objects.requireNonNull(xuLySpaDAO, "xuLySpaDAO không được null");
        this.tiemPhongService =
                Objects.requireNonNull(tiemPhongService, "tiemPhongService không được null");
    }

    public String datLichHen(
            String maThuCung, String maGoiDichVu, String maNhanVien, LocalDateTime batDau) {
        return datLichHen(maThuCung, maGoiDichVu, maNhanVien, batDau, "");
    }

    public String datLichHen(
            String maThuCung,
            String maGoiDichVu,
            String maNhanVien,
            LocalDateTime batDau,
            String ghiChu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        if (batDau == null || batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0)))
            throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
        String ghiChuChuan = chuanHoaGhiChu(ghiChu);
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            ThuCung thuCung =
                                    ThuCungService.macDinh().layThuCungHoatDong(maThuCung);
                            if (xuLySpaDAO.demLichHen(maGoiDichVu) <= 0)
                                throw new IllegalArgumentException(
                                        "Gói dịch vụ Spa không tồn tại hoặc đã ngừng hoạt động.");
                            if (xuLySpaDAO.khoaNhanVienDangLam(maNhanVien) <= 0)
                                throw new IllegalArgumentException(
                                        "Nhân viên phụ trách không hoạt động hoặc không trong ca.");
                            LocalDateTime ketThuc =
                                    batDau.plusMinutes(xuLySpaDAO.layThoiLuongDichVu(maGoiDichVu));
                            if (xuLySpaDAO.demLichTrung(
                                            maNhanVien, thuCung.getMaThuCung(), ketThuc, batDau)
                                    > 0)
                                throw new IllegalArgumentException(
                                        "Nhân viên hoặc thú cưng đã có lịch trùng giờ.");
                            String maKhach =
                                    thuCung.getKhachHang() != null
                                            ? thuCung.getKhachHang().getMaKhachHang()
                                            : null;
                            String ketQua =
                                    xuLySpaDAO.themLichHen(
                                            taoMa("LH"),
                                            maKhach,
                                            thuCung.getMaThuCung(),
                                            batDau,
                                            ketThuc,
                                            maNhanVien,
                                            maGoiDichVu);
                            capNhatGhiChuLichHen(ketQua, ghiChuChuan);
                            NhatKyHeThongService.macDinh().ghiNhatKy("Tạo lịch hẹn", maThuCung);
                            return ketQua;
                        });
    }

    public LichHen layChiTietLichHen(String maLichHen) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        List<LichHen> rows = xuLySpaDAO.layChiTietLichHen(maLichHen);
        if (rows.isEmpty()) throw new IllegalArgumentException("Lịch hẹn không còn tồn tại.");
        LichHen lichHen = rows.get(0);
        Object ghiChu =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT ghi_chu FROM lich_hen WHERE ma_lich_hen=?", maLichHen);
        lichHen.setGhiChu(ghiChu == null ? "" : String.valueOf(ghiChu));
        return lichHen;
    }

    public void suaLichHen(
            String maLichHen,
            String maThuCung,
            String maGoi,
            String maNhanVien,
            LocalDateTime batDau) {
        suaLichHen(maLichHen, maThuCung, maGoi, maNhanVien, batDau, "");
    }

    public void suaLichHen(
            String maLichHen,
            String maThuCung,
            String maGoi,
            String maNhanVien,
            LocalDateTime batDau,
            String ghiChu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        if (batDau == null || batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0)))
            throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
        String ghiChuChuan = chuanHoaGhiChu(ghiChu);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            List<String> old = xuLySpaDAO.khoaLichHen(maLichHen);
                            if (old.isEmpty()
                                    || !TrangThaiLichHen.DA_XAC_NHAN.maDB().equals(old.get(0)))
                                throw new IllegalArgumentException(
                                        "Chỉ được sửa lịch chưa tiếp nhận.");
                            ThuCung thu = ThuCungService.macDinh().layThuCungHoatDong(maThuCung);
                            if (xuLySpaDAO.demLichHen(maGoi) <= 0)
                                throw new IllegalArgumentException(
                                        "Gói dịch vụ Spa không tồn tại hoặc đã ngừng hoạt động.");
                            if (xuLySpaDAO.khoaNhanVienDangLam(maNhanVien) <= 0)
                                throw new IllegalArgumentException(
                                        "Nhân viên phụ trách không hoạt động hoặc không trong ca.");
                            LocalDateTime end =
                                    batDau.plusMinutes(xuLySpaDAO.layThoiLuongDichVu(maGoi));
                            if (xuLySpaDAO.demLichTrungKhiSua(
                                            maLichHen, maNhanVien, thu.getMaThuCung(), end, batDau)
                                    > 0)
                                throw new IllegalArgumentException(
                                        "Nhân viên hoặc thú cưng đã có lịch trùng giờ.");
                            String maKhach =
                                    thu.getKhachHang() != null
                                            ? thu.getKhachHang().getMaKhachHang()
                                            : null;
                            xuLySpaDAO.suaLichHen(
                                    maKhach,
                                    thu.getMaThuCung(),
                                    maGoi,
                                    maNhanVien,
                                    batDau,
                                    end,
                                    maLichHen);
                            capNhatGhiChuLichHen(maLichHen, ghiChuChuan);
                            NhatKyHeThongService.macDinh().ghiNhatKy("Sửa lịch Spa", maLichHen);
                            return null;
                        });
    }

    private static String chuanHoaGhiChu(String ghiChu) {
        String kq = ghiChu == null ? "" : ghiChu.trim();
        if (kq.length() > 1000) {
            throw new IllegalArgumentException("Ghi chú lịch hẹn tối đa 1.000 ký tự.");
        }
        return kq;
    }

    private static void capNhatGhiChuLichHen(String maLichHen, String ghiChu) {
        ThaoTacSQL.capNhat("UPDATE lich_hen SET ghi_chu=? WHERE ma_lich_hen=?", ghiChu, maLichHen);
    }

    public String tiepNhan(String maThuCung, String maLichHen, double canNang, String ghiChu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        if (!Double.isFinite(canNang) || canNang <= 0)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            ThuCung thuCung =
                                    ThuCungService.macDinh().layThuCungHoatDong(maThuCung);
                            tiemPhongService.kiemTraNhanSpa(maThuCung);
                            String lichHen = null;

                            String nhanVien = PhienDangNhapService.macDinh().layMaNhanVien();
                            if (maLichHen != null && !maLichHen.isBlank()) {
                                List<PhieuTiepNhanSpaDAO.ThongTinTiepNhan> cacDong =
                                        xuLySpaDAO.timTiepNhan(maLichHen, thuCung.getMaThuCung());
                                if (cacDong.isEmpty())
                                    throw new IllegalArgumentException(
                                            "Lịch hẹn không phù hợp hoặc đã tiếp nhận.");
                                PhieuTiepNhanSpaDAO.ThongTinTiepNhan dong = cacDong.get(0);
                                lichHen = dong.maLichHen;
                                if (dong.maNhanVien != null) nhanVien = dong.maNhanVien;
                                xuLySpaDAO.capNhatTiepNhan(lichHen);
                            }
                            String ketQua =
                                    xuLySpaDAO.themTiepNhan(
                                            taoMa("SPA"),
                                            lichHen,
                                            thuCung.getMaThuCung(),
                                            canNang,
                                            nhanVien,
                                            ghiChu);
                            xuLySpaDAO.capNhatSucKhoeThuCung(
                                    canNang, ghiChu, thuCung.getMaThuCung());
                            if (lichHen != null) {
                                String goiDichVu = xuLySpaDAO.layTiepNhan(lichHen);
                                if (goiDichVu != null) themDichVuSpa(ketQua, goiDichVu);
                            }
                            NhatKyHeThongService.macDinh().ghiNhatKy("Tiếp nhận Spa", maThuCung);
                            return ketQua;
                        });
    }

    public void themDichVuSpa(String maPhieuSpa, String maGoiDichVu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            List<PhieuTiepNhanSpaDAO.ThongTinThemDichVu> cacPhieuSpa =
                                    xuLySpaDAO.timThemDichVuSpa(maPhieuSpa);
                            if (cacPhieuSpa.isEmpty()
                                    || !Set.of(
                                                    TrangThaiPhieuSpa.TIEP_NHAN.maDB(),
                                                    TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB())
                                            .contains(cacPhieuSpa.get(0).trangThai))
                                throw new IllegalArgumentException(
                                        "Phiếu không còn cho phép thêm dịch vụ.");
                            PhieuTiepNhanSpaDAO.ThongTinThemDichVu giaTriTam = cacPhieuSpa.get(0);

                            List<PhieuTiepNhanSpaDAO.GiaDichVuCanNang> cacGia =
                                    xuLySpaDAO.layGiaDichVuTheoCanNang(
                                            maGoiDichVu,
                                            giaTriTam.loaiThuCung,
                                            giaTriTam.canNangThucTe);
                            if (cacGia.isEmpty())
                                throw new IllegalArgumentException(
                                        "Chưa có bảng giá phù hợp với loài và cân nặng.");

                            PhieuTiepNhanSpaDAO.GiaDichVuCanNang gia = cacGia.get(0);
                            xuLySpaDAO.capNhatThemDichVuSpa(
                                    maPhieuSpa, gia.maGoi, gia.tenGoi, gia.donGia);
                            xuLySpaDAO.tinhLaiTongPhieuSpa(maPhieuSpa);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy(
                                            "Thêm dịch vụ Spa", maGoiDichVu + " / " + maPhieuSpa);
                            return null;
                        });
    }

    public void layTrangThaiSpa(String maPhieuSpa, String tiepTheo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        TrangThaiPhieuSpa trangThaiDich;
        try {
            trangThaiDich = TrangThaiPhieuSpa.tuMaDB(tiepTheo);
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        if (trangThaiDich != TrangThaiPhieuSpa.DANG_THUC_HIEN
                && trangThaiDich != TrangThaiPhieuSpa.HOAN_TAT) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        String trangThaiMoi = trangThaiDich.maDB();
        String duKien =
                (trangThaiDich == TrangThaiPhieuSpa.DANG_THUC_HIEN
                                ? TrangThaiPhieuSpa.TIEP_NHAN
                                : TrangThaiPhieuSpa.DANG_THUC_HIEN)
                        .maDB();
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (xuLySpaDAO.demTrangThaiSpa(maPhieuSpa) == 0)
                                throw new IllegalArgumentException("Cần chọn dịch vụ cho phiếu.");
                            if (xuLySpaDAO.capNhatTrangThaiSpa(trangThaiMoi, maPhieuSpa, duKien)
                                    != 1)
                                throw new IllegalArgumentException(
                                        "Phiếu đã chuyển trạng thái. Hãy tải lại.");
                            if (trangThaiDich == TrangThaiPhieuSpa.HOAN_TAT)
                                xuLySpaDAO.hoanTatLichHen(maPhieuSpa);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Cập nhật Spa", maPhieuSpa + " / " + trangThaiMoi);
                            return null;
                        });
    }

    public void huyLichHen(String maLichHen) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (xuLySpaDAO.huyLichHenBanGhi(maLichHen) != 1)
                                throw new IllegalArgumentException(
                                        "Chỉ có thể hủy lịch chưa tiếp nhận.");
                            NhatKyHeThongService.macDinh().ghiNhatKy("Hủy lịch hẹn", maLichHen);
                            return null;
                        });
    }

    public String layMaLichHenThuCung(String maLichHen) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        return xuLySpaDAO.layMaLichHenThuCung(maLichHen);
    }

    public List<SpaDTO.DongChiTietSpa> layPhieuSpaChiTiet(String maPhieuSpa) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        return xuLySpaDAO.layPhieuSpaChiTiet(maPhieuSpa);
    }

    public List<SpaDTO.DongTimKiemSpa> timKiem(
            LocalDate tuNgay, LocalDate denNgay, String trangThai) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);

        if ((tuNgay == null) != (denNgay == null)) {
            throw new IllegalArgumentException("Vui lòng chọn đủ ngày bắt đầu và ngày kết thúc.");
        }

        if (tuNgay != null && tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }

        String maTrangThai = trangThai == null ? "" : trangThai.trim();

        if (!maTrangThai.isEmpty()) {
            try {
                maTrangThai = TrangThaiPhieuSpa.tuMaDB(maTrangThai).maDB();
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Trạng thái không hợp lệ.");
            }
        }

        return xuLySpaDAO.timKiem(tuNgay, denNgay, maTrangThai);
    }
}
