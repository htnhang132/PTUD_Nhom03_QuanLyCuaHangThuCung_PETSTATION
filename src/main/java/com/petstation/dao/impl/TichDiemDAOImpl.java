package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.TichDiemDAO;
import com.petstation.util.DuLieu;

import java.util.List;

public final class TichDiemDAOImpl implements TichDiemDAO {
    public TichDiemDAOImpl() {}

    @Override
    public List<Object[]> layRangBuocVoucher(String ma) {
        return truyVan(
                "SELECT da_dung, khoa_doi_diem, ma_khach_hang FROM voucher WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE ma_voucher=?",
                ma);
    }

    @Override
    public List<Object[]> timKhachHang(String ma) {
        List<Object[]> rows = truyVan(
                "SELECT ma_khach_hang, diem_tich_luy, dang_hoat_dong FROM khach_hang WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                ma, ma);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    @Override
    public List<Object[]> timTheoKhoa(String khoa) {
        List<Object[]> rows = truyVan(
                "SELECT ma_voucher, ma_khach_hang FROM voucher WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " khoa_doi_diem=?",
                khoa);
        for (Object[] r : rows) {
            if (r[1] != null) {
                r[1] = DuLieu.layIdCauNoi(null, String.valueOf(r[1]));
            }
        }
        return rows;
    }

    @Override
    public String taoVoucher(long khachHang, String khoa) {
        String ma = taoMa("VOUCHER");
        String maKH = String.valueOf(khachHang);
        List<Object[]> rKh = truyVan(
                "SELECT ma_khach_hang FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                khachHang, khachHang);
        if (!rKh.isEmpty()) {
            maKH = String.valueOf(rKh.get(0)[0]);
        }
        capNhat(
                "INSERT"
                    + " voucher(ma_voucher, ma_khach_hang, menh_gia, hieu_luc_den, chi_ap_dung_spa, so_luong, da_dung, don_toi_thieu, trang_thai, khoa_doi_diem)"
                    + " VALUES(?,?,150000,DATEADD(day,60,SYSDATETIME()),1,1,0,0,'CON_HAN',?)",
                ma,
                maKH,
                khoa);
        capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-200,"
                    + " lich_su_diem = CASE WHEN lich_su_diem IS NULL OR lich_su_diem=N'' THEN N''"
                    + " ELSE lich_su_diem + N';' END + CONVERT(NVARCHAR(19), SYSDATETIME(), 120) +"
                    + " N'||-200|' + CAST(diem_tich_luy-200 AS NVARCHAR(20)) + N'|Đổi 200 điểm lấy"
                    + " voucher ' + ? WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND diem_tich_luy>=200",
                ma,
                khachHang,
                khachHang);
        return ma;
    }

    @Override
    public List<Object[]> timVoucher(String ma) {
        return truyVan(
                "SELECT"
                    + " v.menh_gia, v.hieu_luc_den, v.da_dung, v.so_luong, v.don_toi_thieu, v.trang_thai, v.chi_ap_dung_spa, COALESCE(v.ma_khach_hang,'')"
                    + " FROM voucher v WHERE v.ma_voucher=?",
                ma);
    }

    @Override
    public List<Object[]> layCuaKhach(String ma) {
        return truyVan(
                "SELECT v.ma_voucher, v.menh_gia, v.hieu_luc_den, CASE WHEN v.da_dung>=v.so_luong THEN"
                    + " N'Đã dùng' WHEN v.hieu_luc_den<=SYSDATETIME() THEN N'Hết hạn' ELSE N'Còn"
                    + " hiệu lực' END FROM voucher v"
                    + " WHERE (v.ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(v.ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY v.ma_voucher DESC",
                ma, ma);
    }
}
