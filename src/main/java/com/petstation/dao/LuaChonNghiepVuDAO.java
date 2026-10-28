package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho LuaChonNghiepVu; service gọi qua hợp đồng này. */
public interface LuaChonNghiepVuDAO {
    List<Object[]> layLuaChonThuCung(Object... cacThamSo);

    List<Object[]> layLuaChonGoiDichVu(Object... cacThamSo);

    List<Object[]> layLuaChonLoHang(Object... cacThamSo);

    List<Object[]> layLuaChonPhong(Object... cacThamSo);

    List<Object[]> layLuaChonNhaCungCap(Object... cacThamSo);

    List<Object[]> layLuaChonSanPham(Object... cacThamSo);

    List<Object[]> layLuaChonDongHang(Object... cacThamSo);

    List<Object[]> layLuaChonNhanVien(Object... cacThamSo);
}
