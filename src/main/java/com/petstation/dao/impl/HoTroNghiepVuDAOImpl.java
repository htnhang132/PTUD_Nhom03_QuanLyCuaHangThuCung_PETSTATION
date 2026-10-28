package com.petstation.dao.impl;

import com.petstation.dao.HoTroNghiepVuDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC nghiệp vụ hỗ trợ DAO. */
public final class HoTroNghiepVuDAOImpl implements HoTroNghiepVuDAO {
    public HoTroNghiepVuDAOImpl() {}

    public List<Object[]> timThuCung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.id,p.khach_hang_id,p.loai,p.can_nang FROM thu_cung p JOIN"
                        + " khach_hang k ON k.id=p.khach_hang_id WHERE p.ma_thu_cung=? AND"
                        + " p.dang_theo_doi=1 AND k.dang_hoat_dong=1",
                cacThamSo);
    }
}
