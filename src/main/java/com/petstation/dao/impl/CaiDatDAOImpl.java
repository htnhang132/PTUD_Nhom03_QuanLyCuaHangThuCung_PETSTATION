package com.petstation.dao.impl;

import com.petstation.dao.CaiDatDAO;
import com.petstation.database.ThaoTacSQL;

public final class CaiDatDAOImpl implements CaiDatDAO {
    public CaiDatDAOImpl() {}

    @Override
    public Object timGiaTri(String maCaiDat) {
        return ThaoTacSQL.layGiaTriDon("SELECT gia_tri FROM cai_dat WHERE ma_cai_dat=?", maCaiDat);
    }

    @Override
    public int themGiaTri(String maCaiDat, String giaTri, String tenDangNhap) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO cai_dat(ma_cai_dat,gia_tri,ten_dang_nhap) VALUES(?,?,?)",
                maCaiDat, giaTri, tenDangNhap);
    }

    @Override
    public int capNhatGiaTri(String giaTri, String tenDangNhap, String maCaiDat) {
        return ThaoTacSQL.capNhat(
                "UPDATE cai_dat WITH(UPDLOCK,HOLDLOCK) SET"
                        + " gia_tri=?,ten_dang_nhap=?,ngay_cap_nhat=SYSDATETIME()"
                        + " WHERE ma_cai_dat=?",
                giaTri, tenDangNhap, maCaiDat);
    }
}
