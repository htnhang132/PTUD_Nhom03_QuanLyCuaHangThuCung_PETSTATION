package com.petstation.service;

import com.petstation.dao.impl.JdbcLuaChonNghiepVuDAO;

import java.util.List;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho nghiệp vụ lựa chọn dịch vụ. */
public final class LuaChonNghiepVuService {
    /** Khởi tạo LuaChonNghiepVuService với các giá trị được cung cấp. */
    private LuaChonNghiepVuService() {}

    /**
     * Lấy lựa chọn thú cưng trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn thú cưng
     */
    public static List<Object[]> layLuaChonThuCung() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonThuCung();
    }

    /**
     * Lấy lựa chọn gói dịch vụ trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn gói dịch vụ
     */
    public static List<Object[]> layLuaChonGoiDichVu() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonGoiDichVu();
    }

    /**
     * Lấy lựa chọn lô hàng trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn lô hàng
     */
    public static List<Object[]> layLuaChonLoHang() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonLoHang();
    }

    /**
     * Lấy lựa chọn phòng trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn phòng
     */
    public static List<Object[]> layLuaChonPhong() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonPhong();
    }

    /**
     * Lấy lựa chọn nhà cung cấp trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn nhà cung cấp
     */
    public static List<Object[]> layLuaChonNhaCungCap() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonNhaCungCap();
    }

    /**
     * Lấy lựa chọn sản phẩm trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn sản phẩm
     */
    public static List<Object[]> layLuaChonSanPham() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonSanPham();
    }

    /**
     * Lấy lựa chọn dòng hàng trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn dòng hàng
     */
    public static List<Object[]> layLuaChonDongHang() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonDongHang();
    }

    /**
     * Lấy lựa chọn nhân viên trong LuaChonNghiepVuService.
     *
     * @return kết quả lấy lựa chọn nhân viên
     */
    public static List<Object[]> layLuaChonNhanVien() {
        return JdbcLuaChonNghiepVuDAO.layLuaChonNhanVien();
    }
}
