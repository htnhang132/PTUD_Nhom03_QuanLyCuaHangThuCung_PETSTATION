package com.petstation.dao.impl;

import com.petstation.dao.QuyenVaiTroDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC quyền DAO. */
public final class QuyenVaiTroDAOImpl implements QuyenVaiTroDAO {
    public QuyenVaiTroDAOImpl() {}

    public List<Object[]> timCua(Object... cacThamSo) {
        return ThaoTacSQL.truyVan("SELECT ma_quyen FROM vai_tro_quyen WHERE vai_tro=?", cacThamSo);
    }
}
