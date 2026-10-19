package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcAccountDao {
    private JdbcAccountDao() {}

    public static Object passwordHash(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT mat_khau_hash FROM tai_khoan WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                parameters);
    }

    public static int updatePassword(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE tai_khoan SET mat_khau_hash=?,bat_buoc_doi_mat_khau=0 WHERE id=?",
                parameters);
    }
}
