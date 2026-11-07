package com.petstation.dao.impl;

import com.petstation.dao.HoTroNghiepVuDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.util.DuLieu;

import java.util.List;

public final class HoTroNghiepVuDAOImpl implements HoTroNghiepVuDAO {
    public HoTroNghiepVuDAOImpl() {}

    public List<Object[]> timThuCung(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT p.ma_thu_cung, p.ma_khach_hang, p.loai, p.can_nang FROM thu_cung p JOIN"
                        + " khach_hang k ON k.ma_khach_hang=p.ma_khach_hang WHERE (p.ma_thu_cung = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(p.ma_thu_cung, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " p.dang_theo_doi=1 AND k.dang_hoat_dong=1",
                cacThamSo[0], cacThamSo[0]);
        for (Object[] r : rows) {
            if (r[0] != null) r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            if (r[1] != null) r[1] = DuLieu.layIdCauNoi(null, String.valueOf(r[1]));
        }
        return rows;
    }
}
