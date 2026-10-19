package com.petstation.service;

import com.petstation.dao.impl.JdbcLichSuKhachHangDAO;

import java.util.List;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho lịch sử khách hàng dịch vụ. */
public final class LichSuKhachHangService {
    /** Khởi tạo LichSuKhachHangService với các giá trị được cung cấp. */
    private LichSuKhachHangService() {}

    /**
     * Lấy thú cưng trong LichSuKhachHangService.
     *
     * @param khachHang khách hàng
     * @return kết quả lấy thú cưng
     */
    public static List<Object[]> layThuCung(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return JdbcLichSuKhachHangDAO.layThuCung(khachHang);
    }

    /**
     * Lấy các hóa đơn trong LichSuKhachHangService.
     *
     * @param khachHang khách hàng
     * @return kết quả lấy các hóa đơn
     */
    public static List<Object[]> layCacHoaDon(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return JdbcLichSuKhachHangDAO.layCacHoaDon(khachHang);
    }

    /**
     * Lấy lịch sử Spa trong LichSuKhachHangService.
     *
     * @param khachHang khách hàng
     * @return kết quả lấy lịch sử Spa
     */
    public static List<Object[]> layLichSuSpa(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return JdbcLichSuKhachHangDAO.layLichSuSpa(khachHang);
    }

    /**
     * Lấy lịch sử lưu trú trong LichSuKhachHangService.
     *
     * @param khachHang khách hàng
     * @return kết quả lấy lịch sử lưu trú
     */
    public static List<Object[]> layLichSuLuuTru(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return JdbcLichSuKhachHangDAO.layLichSuLuuTru(khachHang);
    }

    /**
     * Lấy lịch sử điểm trong LichSuKhachHangService.
     *
     * @param khachHang khách hàng
     * @return kết quả lấy lịch sử điểm
     */
    public static List<Object[]> layLichSuDiem(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return JdbcLichSuKhachHangDAO.layLichSuDiem(khachHang);
    }

    /**
     * Lấy voucher trong LichSuKhachHangService.
     *
     * @param khachHang khách hàng
     * @return kết quả lấy voucher
     */
    public static List<Object[]> layVoucher(long khachHang) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.CUSTOMER_MANAGE);
        return JdbcLichSuKhachHangDAO.layVoucher(khachHang);
    }
}
