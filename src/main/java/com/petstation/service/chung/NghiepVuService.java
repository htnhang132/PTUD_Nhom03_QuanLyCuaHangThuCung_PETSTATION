package com.petstation.service.chung;

import com.petstation.dao.chung.LuaChonNghiepVuDAO;
import com.petstation.dao.chung.NghiepVuDAO;
import com.petstation.dao.chung.impl.LuaChonNghiepVuDAOImpl;
import com.petstation.dao.chung.impl.NghiepVuDAOImpl;
import com.petstation.dto.DongNghiepVu;
import com.petstation.dto.MucLuaChon;
import com.petstation.model.chung.LoaiNghiepVu;
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
        phienDangNhapService.yeuCauHienTai();
        return nghiepVuDAO.layCacDong(
                kieu,
                kieu == LoaiNghiepVu.GIO_HANG_TREO ? phienDangNhapService.layTenDangNhap() : "");
    }

    public List<MucLuaChon> layLuaChonThuCung() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonThuCung();
    }

    public List<MucLuaChon> layLuaChonGoiDichVu() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonGoiDichVu();
    }

    public List<MucLuaChon> layLuaChonLoHang() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonLoHang();
    }

    public List<MucLuaChon> layLuaChonPhong() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonPhong();
    }

    public List<MucLuaChon> layLuaChonNhaCungCap() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonNhaCungCap();
    }

    public List<MucLuaChon> layLuaChonSanPham() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonSanPham();
    }

    public List<MucLuaChon> layLuaChonNhanVien() {
        phienDangNhapService.yeuCauHienTai();
        return luaChonNghiepVuDAO.layLuaChonNhanVien();
    }
}
