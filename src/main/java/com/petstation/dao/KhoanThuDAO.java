package com.petstation.dao;

import com.petstation.model.DongThuTien;

import java.util.*;

/** Thao tác dữ liệu cho KhoanThu; service gọi qua hợp đồng này. */
public interface KhoanThuDAO {
    void ghi(Long hoaDon, Long hotel, long ca, long nguoi, String khoa, List<DongThuTien> dong);

    List<DongThuTien> lay(String soHoaDon);
}
