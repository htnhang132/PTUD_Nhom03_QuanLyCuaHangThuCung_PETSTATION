package com.petstation.service;

import com.petstation.dao.LichSuKhachHangDAO;
import com.petstation.dao.impl.LichSuKhachHangImpl;

import java.util.List;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho lịch sử khách hàng dịch vụ. */
public final class LichSuKhachHangService {
    private static final LichSuKhachHangDAO lichSuKhachHangDAO = new LichSuKhachHangImpl();

    private LichSuKhachHangService() {}

    public static List<Object[]> layThuCung(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return lichSuKhachHangDAO.layThuCung(khachHang);
    }

    public static List<Object[]> layCacHoaDon(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return lichSuKhachHangDAO.layCacHoaDon(khachHang);
    }

    public static List<Object[]> layLichSuSpa(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return lichSuKhachHangDAO.layLichSuSpa(khachHang);
    }

    public static List<Object[]> layLichSuLuuTru(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return lichSuKhachHangDAO.layLichSuLuuTru(khachHang);
    }

    public static List<Object[]> layLichSuDiem(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return lichSuKhachHangDAO.layLichSuDiem(khachHang);
    }

    public static List<Object[]> layVoucher(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return lichSuKhachHangDAO.layVoucher(khachHang);
    }
}
