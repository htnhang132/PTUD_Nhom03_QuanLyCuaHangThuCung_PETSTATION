package com.petstation.service.chung;

import com.petstation.dao.chung.LuaChonNghiepVuDAO;
import com.petstation.dao.chung.NghiepVuDAO;
import com.petstation.dao.chung.impl.LuaChonNghiepVuDAOImpl;
import com.petstation.dao.chung.impl.NghiepVuDAOImpl;
import com.petstation.dto.DongNghiepVu;
import com.petstation.dto.MucLuaChon;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;

public class NghiepVuService {
    private final NghiepVuDAO nghiepVuDAO;
    private final LuaChonNghiepVuDAO luaChonNghiepVuDAO;
    private final PhienDangNhapService phienDangNhapService;

    public NghiepVuService() {
        this(new NghiepVuDAOImpl(), new LuaChonNghiepVuDAOImpl(), PhienDangNhapService.macDinh());
    }

    public NghiepVuService(NghiepVuDAO nghiepVuDAO, LuaChonNghiepVuDAO luaChonNghiepVuDAO) {
        this(nghiepVuDAO, luaChonNghiepVuDAO, PhienDangNhapService.macDinh());
    }

    public NghiepVuService(
            NghiepVuDAO nghiepVuDAO,
            LuaChonNghiepVuDAO luaChonNghiepVuDAO,
            PhienDangNhapService phienDangNhapService) {
        this.nghiepVuDAO = Objects.requireNonNull(nghiepVuDAO);
        this.luaChonNghiepVuDAO = Objects.requireNonNull(luaChonNghiepVuDAO);
        this.phienDangNhapService = Objects.requireNonNull(phienDangNhapService);
    }

    public static final class DinhNghia {
        private final String tieuDe;
        private final String[] cacCot;

        public DinhNghia(String tieuDe, String[] cacCot) {
            this.tieuDe = tieuDe;
            this.cacCot = cacCot;
        }

        public String tieuDe() {
            return tieuDe;
        }

        public String[] cacCot() {
            return cacCot;
        }
    }

    public DinhNghia layDinhNghia(LoaiNghiepVu kieu) {
        phienDangNhapService.yeuCauHienTai();
        var hienThi = nghiepVuDAO.layThongTin(kieu);
        return new DinhNghia(hienThi.tieuDe(), hienThi.cacCot());
    }

    public List<DongNghiepVu> layCacDong(LoaiNghiepVu kieu) {
        kiemTraQuyen(kieu);
        return nghiepVuDAO.layCacDong(
                kieu,
                kieu == LoaiNghiepVu.GIO_HANG_TREO ? phienDangNhapService.layMaNhanVien() : "");
    }

    private void kiemTraQuyen(LoaiNghiepVu kieu) {
        Objects.requireNonNull(kieu, "Loại nghiệp vụ không được null");
        Quyen quyen =
                switch (kieu) {
                    case HOA_DON, DOI_TRA, GIO_HANG_TREO -> Quyen.BAN_HANG;
                    case LO_HANG -> Quyen.QUAN_LY_SAN_PHAM;
                    case LICH_HEN,
                                    PHIEU_SPA,
                                    TIEP_NHAN_SPA,
                                    GOI_DICH_VU_SPA,
                                    TIEN_DO_SPA,
                                    HOAN_TAT_SPA ->
                            Quyen.QUAN_LY_SPA;
                    case HOP_DONG_LUU_TRU,
                                    DAT_PHONG_LUU_TRU,
                                    NHAT_KY_LUU_TRU,
                                    TRA_PHONG_LUU_TRU,
                                    PHONG_LUU_TRU ->
                            Quyen.QUAN_LY_LUU_TRU;
                    case PHIEU_NHAP_KHO -> Quyen.NHAP_KHO;
                    case PHIEU_XUAT_KHO -> Quyen.XUAT_HUY_KHO;
                    case KIEM_KE -> Quyen.KIEM_KE_KHO;
                };
        phienDangNhapService.yeuCau(quyen);
    }

    public List<MucLuaChon> layLuaChonThuCung() {
        phienDangNhapService.yeuCauMotTrong(
                Quyen.QUAN_LY_THU_CUNG, Quyen.QUAN_LY_SPA, Quyen.QUAN_LY_LUU_TRU);
        return luaChonNghiepVuDAO.layLuaChonThuCung();
    }

    public List<MucLuaChon> layLuaChonGoiDichVu() {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        return luaChonNghiepVuDAO.layLuaChonGoiDichVu();
    }

    public List<MucLuaChon> layLuaChonLoHang() {
        phienDangNhapService.yeuCauMotTrong(
                Quyen.QUAN_LY_SAN_PHAM, Quyen.NHAP_KHO, Quyen.XUAT_HUY_KHO, Quyen.KIEM_KE_KHO);
        return luaChonNghiepVuDAO.layLuaChonLoHang();
    }

    public List<MucLuaChon> layLuaChonPhong() {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_LUU_TRU);
        return luaChonNghiepVuDAO.layLuaChonPhong();
    }

    public List<MucLuaChon> layLuaChonNhaCungCap() {
        phienDangNhapService.yeuCauMotTrong(Quyen.QUAN_LY_NHA_CUNG_CAP, Quyen.NHAP_KHO);
        return luaChonNghiepVuDAO.layLuaChonNhaCungCap();
    }

    public List<MucLuaChon> layLuaChonSanPham() {
        phienDangNhapService.yeuCauMotTrong(
                Quyen.QUAN_LY_SAN_PHAM,
                Quyen.NHAP_KHO,
                Quyen.XUAT_HUY_KHO,
                Quyen.KIEM_KE_KHO,
                Quyen.BAN_HANG);
        return luaChonNghiepVuDAO.layLuaChonSanPham();
    }

    public List<MucLuaChon> layLuaChonNhanVien() {
        phienDangNhapService.yeuCauMotTrong(
                Quyen.QUAN_LY_TAI_KHOAN,
                Quyen.QUAN_LY_LICH_LAM_VIEC,
                Quyen.XEM_LICH_LAM_VIEC,
                Quyen.QUAN_LY_SPA);
        return luaChonNghiepVuDAO.layLuaChonNhanVien();
    }
}
