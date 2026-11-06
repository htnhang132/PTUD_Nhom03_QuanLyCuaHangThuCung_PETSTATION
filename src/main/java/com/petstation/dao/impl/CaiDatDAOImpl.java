package com.petstation.dao.impl;

import com.petstation.dao.CaiDatDAO;
import com.petstation.database.ThaoTacSQL;

/** Thực hiện truy vấn JDBC cho JDBC cài đặt DAO. */
public final class CaiDatDAOImpl implements CaiDatDAO {
    public CaiDatDAOImpl() {}

    public Object timGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon("SELECT gia_tri FROM cai_dat WHERE ma_cai_dat=?", cacThamSo);
    }

    public int themGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO cai_dat(ma_cai_dat,gia_tri,nguoi_cap_nhat) VALUES(?,?,?)", cacThamSo);
    }

    public int capNhatGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE cai_dat WITH(UPDLOCK,HOLDLOCK) SET"
                        + " gia_tri=?,nguoi_cap_nhat=?,ngay_cap_nhat=SYSDATETIME()"
                        + " WHERE ma_cai_dat=?",
                cacThamSo);
    }
}
