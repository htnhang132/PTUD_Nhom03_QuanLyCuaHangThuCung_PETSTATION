package com.petstation.service.spa;

import com.petstation.dao.spa.PhieuSpaTreoDAO;
import com.petstation.dao.spa.impl.PhieuSpaTreoDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.SpaDTO;
import com.petstation.model.chung.Quyen;
import com.petstation.model.spa.PhieuSpaTreo;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DuLieu;

import java.util.List;
import java.util.Objects;

public final class PhieuSpaTreoService {
    private final PhieuSpaTreoDAO dao;
    private final PhienDangNhapService phienDangNhap;
    private final LapPhieuSpaService lapPhieuSpaService;

    public PhieuSpaTreoService() {
        this(new PhieuSpaTreoDAOImpl(), PhienDangNhapService.macDinh(), new LapPhieuSpaService());
    }

    public PhieuSpaTreoService(PhieuSpaTreoDAO dao) {
        this(dao, PhienDangNhapService.macDinh(), new LapPhieuSpaService());
    }

    public PhieuSpaTreoService(
            PhieuSpaTreoDAO dao,
            PhienDangNhapService phienDangNhap,
            LapPhieuSpaService lapPhieuSpaService) {
        this.dao = Objects.requireNonNull(dao, "dao không được null");
        this.phienDangNhap = Objects.requireNonNull(phienDangNhap, "phienDangNhap không được null");
        this.lapPhieuSpaService =
                Objects.requireNonNull(lapPhieuSpaService, "lapPhieuSpaService không được null");
    }

    public String treo(String maPhieu, PhieuSpaTreo p) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        if (!p.coNoiDung()) throw new IllegalArgumentException("Chưa có nội dung để treo phiếu.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            if (maPhieu == null || maPhieu.isBlank())
                                return dao.them(
                                        DuLieu.taoMa("TREOSPA"), phienDangNhap.layMaNhanVien(), p);
                            yeuCauConTonTai(maPhieu);
                            dao.capNhat(maPhieu, p);
                            return maPhieu;
                        });
    }

    public PhieuSpaTreo lay(String maPhieu) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        return yeuCauConTonTai(maPhieu);
    }

    private PhieuSpaTreo yeuCauConTonTai(String maPhieu) {
        var p = dao.lay(maPhieu);
        if (p == null)
            throw new IllegalStateException("Phiếu treo đã được tạo hoặc xóa ở nơi khác.");
        return p;
    }

    public List<SpaDTO.DongPhieuSpaTreo> danhSach() {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        return dao.danhSach();
    }

    public void xoa(String maPhieu) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            yeuCauConTonTai(maPhieu);
                            dao.xoa(maPhieu);
                            return null;
                        });
    }

    public String taoPhieu(
            String maPhieuTreo,
            String maThu,
            String maGoi,
            String maHen,
            double canNang,
            String tinhTrang,
            String yeuCau) {
        phienDangNhap.yeuCau(Quyen.QUAN_LY_SPA);
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            if (maPhieuTreo != null && !maPhieuTreo.isBlank()) {
                                yeuCauConTonTai(maPhieuTreo);
                                dao.xoa(maPhieuTreo);
                            }
                            return lapPhieuSpaService.luu(
                                    maThu, maGoi, maHen, canNang, tinhTrang, yeuCau);
                        });
    }
}
