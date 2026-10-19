package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcAuditDao {
    private JdbcAuditDao() {}

    public static int updateLog(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                    + " nhat_ky_he_thong(tai_khoan_id,hanh_dong,ghi_chu,nguoi_dung,loai_su_kien,ket_qua,thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,SYSDATETIME())",
                parameters);
    }

    public static List<Object[]> findEntries(Object... parameters) {
        return SqlDatabase.query(
                "SELECT TOP(2000) a.thoi_gian,COALESCE(a.nguoi_dung,n.ho_ten,N'Hệ"
                    + " thống'),a.hanh_dong,COALESCE(a.ghi_chu,a.ma_doi_tuong,N''),COALESCE(a.loai_su_kien,'SYSTEM'),COALESCE(a.ket_qua,N'Thành"
                    + " công') FROM nhat_ky_he_thong a LEFT JOIN tai_khoan t ON t.id=a.tai_khoan_id"
                    + " LEFT JOIN nhan_vien n ON n.id=t.nhan_vien_id ORDER BY a.thoi_gian DESC,a.id"
                    + " DESC",
                parameters);
    }
}
