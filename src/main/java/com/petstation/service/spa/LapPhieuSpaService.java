package com.petstation.service.spa;

import com.petstation.dao.spa.PhieuTiepNhanSpaDAO;
import com.petstation.dao.spa.impl.PhieuTiepNhanSpaDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.spa.PhieuTiepNhanSpa;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.ThuCungService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class LapPhieuSpaService {
    private final PhieuTiepNhanSpaDAO dao;
    private final PhienDangNhapService phienDangNhap;
    private final SpaService spaService;

    public LapPhieuSpaService() {
        this(new PhieuTiepNhanSpaDAOImpl());
    }

    public LapPhieuSpaService(PhieuTiepNhanSpaDAO dao) {
        this(dao, PhienDangNhapService.macDinh(), new SpaService(dao));
    }

    public LapPhieuSpaService(
            PhieuTiepNhanSpaDAO dao, PhienDangNhapService phienDangNhap, SpaService spaService) {
        this.dao = Objects.requireNonNull(dao, "dao không được null");
        this.phienDangNhap = Objects.requireNonNull(phienDangNhap, "phienDangNhap không được null");
        this.spaService = Objects.requireNonNull(spaService, "spaService không được null");
    }

    public long baoGia(String maThu, String maGoi, double canNang) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        kiemTraCanNang(canNang);
        ThuCung thu = ThuCungService.macDinh().layThuCungHoatDong(maThu);
        List<PhieuTiepNhanSpaDAO.GiaDichVuCanNang> gia =
                dao.layGiaDichVuTheoCanNang(maGoi, thu.getLoai(), canNang);
        if (gia.isEmpty())
            throw new IllegalArgumentException("Chưa có giá phù hợp với loài và cân nặng này.");
        return gia.get(0).donGia;
    }

    public String luu(
            String maThu,
            String maGoi,
            String maHen,
            double canNang,
            String tinhTrang,
            String yeuCau) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        kiemTraCanNang(canNang);
        if (maGoi == null || maGoi.isBlank())
            throw new IllegalArgumentException("Cần chọn dịch vụ.");
        if (tinhTrang == null || tinhTrang.isBlank())
            throw new IllegalArgumentException("Cần nhập tình trạng khi nhận.");
        String ghiChu =
                "Tình trạng khi nhận: "
                        + tinhTrang.trim()
                        + "\nYêu cầu khách hàng / vật dụng: "
                        + (yeuCau == null ? "" : yeuCau.trim());
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            String maPhieu = spaService.tiepNhan(maThu, maHen, canNang, ghiChu);

                            if (maHen == null
                                    || maHen.isBlank()
                                    || spaService.layPhieuSpaChiTiet(maPhieu).isEmpty()) {
                                spaService.themDichVuSpa(maPhieu, maGoi);
                            }
                            return maPhieu;
                        });
    }

    private static void kiemTraCanNang(double canNang) {
        if (!Double.isFinite(canNang) || canNang <= 0 || canNang > 100)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0 và không quá 100 kg.");
    }

    public Optional<PhieuTiepNhanSpa> timPhieu(String maPhieu) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        return dao.timTheoMa(maPhieu);
    }
}
