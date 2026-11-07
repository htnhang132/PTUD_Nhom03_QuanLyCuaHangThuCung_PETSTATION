package com.petstation.dao.impl;

import com.petstation.dao.PhieuDoiTraDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

public final class PhieuDoiTraDAOImpl implements PhieuDoiTraDAO {
    public PhieuDoiTraDAOImpl() {}

    @Override
    public int capNhatApDungDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    @Override
    public long demDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(c.so_luong),0) FROM chi_tiet_doi_tra c"
                        + " JOIN phieu_doi_tra p ON p.ma_phieu=c.ma_phieu WHERE"
                        + " (c.ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_san_pham, PATINDEX('%[0-9]%', c.ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND p.trang_thai IN"
                        + " ('CHO_XU_LY','HOAN_TAT')",
                cacThamSo[0],
                cacThamSo[0]);
    }

    @Override
    public List<Object[]> laySoLuongLoConDuocDoi(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT b.ma_lo, b.so_luong-COALESCE((SELECT"
                        + " SUM(c.so_luong) FROM chi_tiet_doi_tra c JOIN"
                        + " phieu_doi_tra p ON p.ma_phieu=c.ma_phieu WHERE"
                        + " c.ma_hoa_don=b.ma_hoa_don AND c.ma_san_pham=b.ma_san_pham AND"
                        + " c.ma_lo=b.ma_lo AND p.trang_thai IN"
                        + " ('CHO_XU_LY','HOAN_TAT')),0) FROM chi_tiet_hoa_don b"
                        + " WHERE (b.ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(b.ma_san_pham, PATINDEX('%[0-9]%', b.ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND b.ma_lo IS NOT NULL ORDER BY b.ma_san_pham",
                cacThamSo[0],
                cacThamSo[0]);
    }

    @Override
    public int capNhatDoiTraMuc(Object... cacThamSo) {
        String maSP = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_san_pham FROM san_pham WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1],
                    cacThamSo[1]);
            if (!r.isEmpty()) {
                maSP = String.valueOf(r.get(0)[0]);
            }
        }
        String maLo = String.valueOf(cacThamSo[2]);
        if (cacThamSo[2] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[2],
                    cacThamSo[2]);
            if (!r.isEmpty()) {
                maLo = String.valueOf(r.get(0)[0]);
            }
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_doi_tra(ma_phieu, ma_hoa_don, ma_san_pham, ma_lo, so_luong, tien_hoan) "
                        + "SELECT p.ma_phieu, p.ma_hoa_don, ?, ?, ?, ? "
                        + "FROM phieu_doi_tra p "
                        + "WHERE (p.ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(p.ma_phieu, PATINDEX('%[0-9]%', p.ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                maSP,
                maLo,
                cacThamSo[3],
                cacThamSo[4],
                cacThamSo[0],
                cacThamSo[0]);
    }
}
