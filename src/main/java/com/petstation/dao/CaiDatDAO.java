package com.petstation.dao;

public interface CaiDatDAO {
    Object timGiaTri(String maCaiDat);

    int themGiaTri(String maCaiDat, String giaTri, String tenDangNhap);

    int capNhatGiaTri(String giaTri, String tenDangNhap, String maCaiDat);
}
