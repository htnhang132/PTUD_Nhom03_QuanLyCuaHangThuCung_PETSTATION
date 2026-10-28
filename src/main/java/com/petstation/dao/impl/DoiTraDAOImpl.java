package com.petstation.dao.impl;

import com.petstation.dao.DoiTraDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC đổi trả DAO. */
public final class DoiTraDAOImpl implements DoiTraDAO {
    public DoiTraDAOImpl() {}

    public int capNhatApDungDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", cacThamSo);
    }

    public long demDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(c.so_luong),0) FROM chi_tiet_doi_tra c"
                        + " JOIN phieu_doi_tra p ON p.id=c.phieu_doi_tra_id WHERE"
                        + " c.chi_tiet_don_hang_id=? AND p.trang_thai IN"
                        + " ('PENDING','COMPLETED')",
                cacThamSo);
    }

    public List<Object[]> laySoLuongLoConDuocDoi(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT b.lo_hang_id,b.so_luong-COALESCE((SELECT"
                        + " SUM(c.so_luong) FROM chi_tiet_doi_tra c JOIN"
                        + " phieu_doi_tra p ON p.id=c.phieu_doi_tra_id WHERE"
                        + " c.chi_tiet_don_hang_id=b.chi_tiet_don_hang_id AND"
                        + " c.lo_hang_id=b.lo_hang_id AND p.trang_thai IN"
                        + " ('PENDING','COMPLETED')),0) FROM phan_bo_lo_hang b"
                        + " WHERE b.chi_tiet_don_hang_id=? ORDER BY b.id",
                cacThamSo);
    }

    public int capNhatDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_doi_tra(phieu_doi_tra_id,chi_tiet_don_hang_id,lo_hang_id,so_luong,tien_hoan)"
                    + " VALUES(?,?,?,?,?)",
                cacThamSo);
    }
}
