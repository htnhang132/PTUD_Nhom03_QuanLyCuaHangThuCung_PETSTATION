package com.petstation.dao.impl;

import com.petstation.dao.LichLamViecDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

public final class LichLamViecDAOImpl implements LichLamViecDAO {
    public LichLamViecDAOImpl() {}

    public long demPhanCong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT 1 FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public long demNhanVienDangLam(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND dang_lam_viec=1",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> timKhoang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.ngay_lam, l.ma_ca, l.ma_nhan_vien, l.loai FROM lich_lam_viec l JOIN"
                        + " ca_lam_viec c ON c.ma_ca=l.ma_ca WHERE l.ngay_lam>=? AND l.ngay_lam<? AND"
                        + " l.trang_thai<>'DA_HUY' ORDER BY"
                        + " l.ngay_lam, c.gio_bat_dau, l.ma_nhan_vien",
                cacThamSo);
    }

    public long demLichTrungNgayCa(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_lam_viec WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " ngay_lam=? AND trang_thai<>'DA_HUY' AND"
                        + " (loai='NGHI_PHEP' OR ma_ca=?)",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int capNhatPhanCong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM lich_lam_viec WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND ngay_lam=? AND"
                        + " ma_ca=? AND trang_thai='DA_HUY'",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int themLichLamViec(Object... cacThamSo) {
        String maNV = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maNV = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_lam_viec(ma_nhan_vien, ngay_lam, ma_ca, loai, trang_thai) VALUES(?, ?, ?, 'DI_LAM', 'DANG_MO')",
                maNV,
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int capNhatXoa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET trang_thai='DA_HUY' WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND ngay_lam=? AND ma_ca=?",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int capNhatDanhDauNghiPhepDaDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET loai='NGHI_PHEP',trang_thai='DANG_MO' WHERE"
                        + " (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND ngay_lam=?",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1]);
    }

    public int themLichNghiPhep(Object... cacThamSo) {
        String maNV = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maNV = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_lam_viec(ma_nhan_vien, ngay_lam, ma_ca, loai, trang_thai) VALUES(?, ?, 'CA01', 'NGHI_PHEP', 'DANG_MO')",
                maNV,
                cacThamSo[1]);
    }

    public long demThangSoGio(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(DATEDIFF(minute, c.gio_bat_dau, c.gio_ket_thuc)),0)/60"
                        + " FROM lich_lam_viec l JOIN ca_lam_viec c ON c.ma_ca=l.ma_ca"
                        + " WHERE (l.ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_nhan_vien, PATINDEX('%[0-9]%', l.ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " l.ngay_lam>=? AND l.ngay_lam<? AND l.loai='DI_LAM' AND"
                        + " l.trang_thai<>'DA_HUY'",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int khoaLichKhoang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET trang_thai='DA_KHOA' WHERE ngay_lam>=? AND ngay_lam<? AND"
                        + " trang_thai='DANG_MO'",
                cacThamSo);
    }
}
