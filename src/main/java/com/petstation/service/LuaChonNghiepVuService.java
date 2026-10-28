package com.petstation.service;

import com.petstation.dao.LuaChonNghiepVuDAO;
import com.petstation.dao.impl.LuaChonNghiepVuDAOImpl;

import java.util.List;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho nghiệp vụ lựa chọn dịch vụ. */
public final class LuaChonNghiepVuService {
    private static final LuaChonNghiepVuDAO luaChonNghiepVuDAO = new LuaChonNghiepVuDAOImpl();

    private LuaChonNghiepVuService() {}

    public static List<Object[]> layLuaChonThuCung() {
        return luaChonNghiepVuDAO.layLuaChonThuCung();
    }

    public static List<Object[]> layLuaChonGoiDichVu() {
        return luaChonNghiepVuDAO.layLuaChonGoiDichVu();
    }

    public static List<Object[]> layLuaChonLoHang() {
        return luaChonNghiepVuDAO.layLuaChonLoHang();
    }

    public static List<Object[]> layLuaChonPhong() {
        return luaChonNghiepVuDAO.layLuaChonPhong();
    }

    public static List<Object[]> layLuaChonNhaCungCap() {
        return luaChonNghiepVuDAO.layLuaChonNhaCungCap();
    }

    public static List<Object[]> layLuaChonSanPham() {
        return luaChonNghiepVuDAO.layLuaChonSanPham();
    }

    public static List<Object[]> layLuaChonDongHang() {
        return luaChonNghiepVuDAO.layLuaChonDongHang();
    }

    public static List<Object[]> layLuaChonNhanVien() {
        return luaChonNghiepVuDAO.layLuaChonNhanVien();
    }
}
