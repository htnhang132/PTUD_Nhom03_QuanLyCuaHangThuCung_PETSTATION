package com.petstation.dao.impl;

import com.petstation.dao.CaiDatDAO;
import com.petstation.database.ThaoTacSQL;

public final class CaiDatDAOImpl implements CaiDatDAO {
    public CaiDatDAOImpl() {}

    public Object timGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon("SELECT gia_tri FROM cai_dat WHERE ma_cai_dat=?", cacThamSo);
    }

    public int themGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO cai_dat(ma_cai_dat,gia_tri,ten_dang_nhap) VALUES(?,?,?)", cacThamSo);
    }

    public int capNhatGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE cai_dat WITH(UPDLOCK,HOLDLOCK) SET"
                        + " gia_tri=?,ten_dang_nhap=?,ngay_cap_nhat=SYSDATETIME()"
                        + " WHERE ma_cai_dat=?",
                cacThamSo);
    }
}
