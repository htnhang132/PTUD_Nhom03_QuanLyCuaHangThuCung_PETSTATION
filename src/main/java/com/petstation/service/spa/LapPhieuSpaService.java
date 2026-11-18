package com.petstation.service.spa;

import com.petstation.dao.spa.PhieuTiepNhanSpaDAO;
import com.petstation.dao.spa.impl.PhieuTiepNhanSpaDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.SpaDTO;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.ThuCungService;

import java.util.List;
import java.util.Objects;

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
        if (maGoi == null || maGoi.isBlank()) {
            throw new IllegalArgumentException("Cần chọn dịch vụ.");
        }
        return luuNhieuDichVu(maThu, List.of(maGoi), maHen, canNang, tinhTrang, yeuCau, false);
    }

    public String luuNhieuDichVu(
            String maThu,
            List<String> dsMaGoi,
            String maHen,
            double canNang,
            String tinhTrang,
            String yeuCau,
            boolean batDauNgay) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        kiemTraCanNang(canNang);
        List<String> dsHopLe = chuanHoaDanhSachGoi(dsMaGoi);
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
                            themCacGoiChuaCo(maPhieu, maThu, canNang, dsHopLe);
                            if (batDauNgay) {
                                spaService.layTrangThaiSpa(
                                        maPhieu, TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB());
                            }
                            return maPhieu;
                        });
    }

    private List<String> chuanHoaDanhSachGoi(List<String> dsMaGoi) {
        if (dsMaGoi == null) {
            throw new IllegalArgumentException("Cần chọn ít nhất 1 dịch vụ.");
        }
        List<String> dsHopLe = dsMaGoi.stream().filter(ma -> ma != null && !ma.isBlank()).toList();
        if (dsHopLe.isEmpty()) {
            throw new IllegalArgumentException("Cần chọn ít nhất 1 dịch vụ.");
        }
        return dsHopLe;
    }

    private void themCacGoiChuaCo(
            String maPhieu, String maThu, double canNang, List<String> dsMaGoi) {
        List<SpaDTO.DongChiTietSpa> daCo = spaService.layPhieuSpaChiTiet(maPhieu);
        String tenGoiSanCo = daCo.isEmpty() ? null : daCo.get(0).getTenDichVu();
        ThuCung thu =
                tenGoiSanCo == null ? null : ThuCungService.macDinh().layThuCungHoatDong(maThu);
        boolean daBoQuaGoiSanCo = false;
        for (String maGoi : dsMaGoi) {
            if (!daBoQuaGoiSanCo
                    && tenGoiSanCo != null
                    && laCungTenGoi(maGoi, thu, canNang, tenGoiSanCo)) {
                daBoQuaGoiSanCo = true;
                continue;
            }
            spaService.themDichVuSpa(maPhieu, maGoi);
        }
    }

    private boolean laCungTenGoi(String maGoi, ThuCung thu, double canNang, String tenGoiSanCo) {
        List<PhieuTiepNhanSpaDAO.GiaDichVuCanNang> gia =
                dao.layGiaDichVuTheoCanNang(maGoi, thu.getLoai(), canNang);
        return !gia.isEmpty() && tenGoiSanCo.equalsIgnoreCase(gia.get(0).tenGoi);
    }

    private static void kiemTraCanNang(double canNang) {
        if (!Double.isFinite(canNang) || canNang <= 0 || canNang > 100)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0 và không quá 100 kg.");
    }
}
