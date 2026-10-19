package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcSettingsDao {
    private JdbcSettingsDao() {}

    public static Object findValue(Object... parameters) {
        return SqlDatabase.scalar("SELECT gia_tri FROM cai_dat WHERE khoa=?", parameters);
    }

    public static int insertValue(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO cai_dat(khoa,gia_tri,nguoi_cap_nhat_id) VALUES(?,?,?)", parameters);
    }

    public static int updateValue(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE cai_dat WITH(UPDLOCK,HOLDLOCK) SET"
                        + " gia_tri=?,nguoi_cap_nhat_id=?,ngay_cap_nhat=SYSDATETIME()"
                        + " WHERE khoa=?",
                parameters);
    }
}
