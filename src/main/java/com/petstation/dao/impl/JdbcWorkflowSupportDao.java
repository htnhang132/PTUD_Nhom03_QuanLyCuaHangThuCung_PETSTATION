package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcWorkflowSupportDao {
    private JdbcWorkflowSupportDao() {}

    public static List<Object[]> findPet(Object... parameters) {
        return SqlDatabase.query(
                "SELECT p.id,p.khach_hang_id,p.loai,p.can_nang FROM thu_cung p JOIN"
                        + " khach_hang k ON k.id=p.khach_hang_id WHERE p.ma_thu_cung=? AND"
                        + " p.dang_theo_doi=1 AND k.dang_hoat_dong=1",
                parameters);
    }
}
