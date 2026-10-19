package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcPermissionDao {
    private JdbcPermissionDao() {}

    public static List<Object[]> findOf(Object... parameters) {
        return SqlDatabase.query("SELECT ma_quyen FROM vai_tro_quyen WHERE vai_tro=?", parameters);
    }
}
