package com.petstation.dao.chung;

import com.petstation.dto.MucLuaChon;

import java.util.List;

public interface LuaChonNghiepVuDAO {
    List<MucLuaChon> layLuaChonThuCung();

    List<MucLuaChon> layLuaChonGoiDichVu();

    List<MucLuaChon> layLuaChonLoHang();

    List<MucLuaChon> layLuaChonPhong();

    List<MucLuaChon> layLuaChonNhaCungCap();

    List<MucLuaChon> layLuaChonSanPham();

    List<MucLuaChon> layLuaChonDongHang();

    List<MucLuaChon> layLuaChonNhanVien();
}
