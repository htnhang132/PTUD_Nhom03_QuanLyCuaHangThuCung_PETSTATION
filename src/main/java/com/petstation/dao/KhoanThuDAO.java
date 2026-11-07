package com.petstation.dao;

import com.petstation.model.DongThuTien;

import java.util.*;

public interface KhoanThuDAO {
    void ghi(Long hoaDon, Long hotel, long ca, long nguoi, String khoa, List<DongThuTien> dong);

    List<DongThuTien> lay(String soHoaDon);
}
