package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.LichSuKhachHangDAO;
import com.petstation.dao.khachhang.impl.LichSuKhachHangDAOImpl;
import com.petstation.dto.LichSuKhachHangDTO;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;

public final class LichSuKhachHangService {
    private final LichSuKhachHangDAO lichSuKhachHangDAO;
    private final PhienDangNhapService phienDangNhapService;

    public LichSuKhachHangService() {
        this(new LichSuKhachHangDAOImpl());
    }

    public LichSuKhachHangService(LichSuKhachHangDAO lichSuKhachHangDAO) {
        this(lichSuKhachHangDAO, PhienDangNhapService.macDinh());
    }

    public LichSuKhachHangService(
            LichSuKhachHangDAO lichSuKhachHangDAO, PhienDangNhapService phienDangNhapService) {
        this.lichSuKhachHangDAO =
                Objects.requireNonNull(lichSuKhachHangDAO, "lichSuKhachHangDAO không được null");
        this.phienDangNhapService =
                Objects.requireNonNull(
                        phienDangNhapService, "phienDangNhapService không được null");
    }

    public List<LichSuKhachHangDTO.LichSuThuCung> layThuCung(String maKhachHang) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        return lichSuKhachHangDAO.layThuCung(maKhachHang);
    }

    public List<LichSuKhachHangDTO.LichSuHoaDon> layCacHoaDon(String maKhachHang) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        return lichSuKhachHangDAO.layCacHoaDon(maKhachHang);
    }

    public List<LichSuKhachHangDTO.LichSuSpa> layLichSuSpa(String maKhachHang) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        return lichSuKhachHangDAO.layLichSuSpa(maKhachHang);
    }

    public List<LichSuKhachHangDTO.LichSuLuuTru> layLichSuLuuTru(String maKhachHang) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        return lichSuKhachHangDAO.layLichSuLuuTru(maKhachHang);
    }

    public List<LichSuKhachHangDTO.LichSuVoucher> layVoucher(String maKhachHang) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        return lichSuKhachHangDAO.layVoucher(maKhachHang);
    }
}
