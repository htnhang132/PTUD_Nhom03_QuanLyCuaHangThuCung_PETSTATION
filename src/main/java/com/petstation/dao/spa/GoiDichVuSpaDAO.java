package com.petstation.dao.spa;

import com.petstation.model.spa.GoiDichVuSpa;

import java.util.List;

// Giao diện DAO quản lý gói dịch vụ Spa
public interface GoiDichVuSpaDAO {
    List<GoiDichVuSpa> layTatCa();

    GoiDichVuSpa timTheoMa(String maDichVu);

    List<GoiDichVuSpa> layDanhSachDichVu();
}
