package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC quyền DAO. */
public final class JdbcQuyenVaiTroDAO {
    /** Khởi tạo JdbcQuyenVaiTroDAO với các giá trị được cung cấp. */
    private JdbcQuyenVaiTroDAO() {}

    /**
     * Tìm của trong JdbcQuyenVaiTroDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm của
     */
    public static List<Object[]> timCua(Object... cacThamSo) {
        return ThaoTacSQL.truyVan("SELECT ma_quyen FROM vai_tro_quyen WHERE vai_tro=?", cacThamSo);
    }
}
