package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.DoiHangDAO;
import com.petstation.util.BoiCanhDangNhap;
import com.petstation.util.DuLieu;

import java.util.List;

public final class DoiHangDAOImpl implements DoiHangDAO {
    public DoiHangDAOImpl() {}

    @Override
    public List<Object[]> timHoaDon(String tuKhoa) {
        List<Object[]> rows = truyVan(
            "SELECT h.ma_hoa_don AS ma_khoa, h.ma_hoa_don,"
                + "COALESCE(k.ho_ten, N'Khách lẻ'),"
                + "h.tao_luc, h.tong_tien"
                + " FROM hoa_don h"
                + " LEFT JOIN khach_hang k ON k.ma_khach_hang=h.ma_khach_hang"
                + " WHERE h.trang_thai='DA_THANH_TOAN'"
                + " AND (h.ma_hoa_don=? OR k.so_dien_thoai=?)"
                + " ORDER BY h.tao_luc DESC, h.ma_hoa_don DESC",
            tuKhoa, tuKhoa);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    @Override
    public List<Object[]> layDongHoaDon(long hoaDon) {
        List<Object[]> rows = truyVan(
            "SELECT i.ma_san_pham, p.ten, p.nhom_hang, i.so_luong,"
                + "COALESCE(d.da_doi,0), i.don_gia"
                + " FROM chi_tiet_hoa_don i"
                + " JOIN san_pham p ON p.ma_san_pham=i.ma_san_pham"
                + " OUTER APPLY ("
                + " SELECT SUM(c.so_luong) da_doi"
                + " FROM chi_tiet_doi_tra c"
                + " JOIN phieu_doi_tra f ON f.ma_phieu=c.ma_phieu"
                + " WHERE c.ma_hoa_don=i.ma_hoa_don AND c.ma_san_pham=i.ma_san_pham"
                + " AND f.trang_thai IN ('CHO_XU_LY','HOAN_TAT')"
                + " ) d"
                + " WHERE (i.ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(i.ma_hoa_don, PATINDEX('%[0-9]%', i.ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY p.ten",
            hoaDon, hoaDon);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    @Override
    public List<Object[]> timPhuKien(String tuKhoa) {
        return truyVan(
            "SELECT p.ma_san_pham, p.ten, p.gia_ban,"
                + "COALESCE(SUM(l.so_luong_ton),0)"
                + " FROM san_pham p"
                + " LEFT JOIN lo_hang l ON (l.ma_san_pham=p.ma_san_pham"
                + " AND l.trang_thai='SAN_SANG'"
                + " AND (l.han_su_dung IS NULL"
                + " OR l.han_su_dung>=CAST(GETDATE() AS date)))"
                + " WHERE p.trang_thai='DANG_BAN'"
                + " AND p.nhom_hang=N'Phụ kiện'"
                + " AND (p.ma_san_pham=? OR p.ten LIKE ?)"
                + " GROUP BY p.ma_san_pham, p.ten, p.gia_ban"
                + " ORDER BY p.ten",
            tuKhoa, "%" + tuKhoa + "%");
    }

    public List<Object[]> timDongGoc(long id) {
        return truyVan(
                "SELECT"
                    + " h.ma_hoa_don,h.tong_tien,h.tam_tinh,h.thanh_toan_luc,i.don_gia,i.so_luong,h.trang_thai,p.nhom_hang,COALESCE(k.ma_khach_hang,''),COALESCE(k.ho_ten,''),COALESCE(k.diem_tich_luy,0)"
                    + " FROM chi_tiet_hoa_don i WITH(UPDLOCK,HOLDLOCK) JOIN hoa_don h"
                    + " WITH(UPDLOCK,HOLDLOCK) ON h.ma_hoa_don=i.ma_hoa_don JOIN san_pham p ON"
                    + " p.ma_san_pham=i.ma_san_pham LEFT JOIN khach_hang k ON k.ma_khach_hang=h.ma_khach_hang WHERE"
                    + " (i.ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(i.ma_san_pham, PATINDEX('%[0-9]%', i.ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    public List<Object[]> timThayThe(String ma) {
        return truyVan(
                "SELECT ma_san_pham, ten, gia_ban, nhom_hang FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (ma_san_pham=? OR barcode=?) AND trang_thai='DANG_BAN'",
                ma, ma);
    }

    public long taoPhieu(
            long hoaDon, long nguoi, long chenhLech, String lyDo, long giaTri, String khoa) {
        String maPhieu = taoMa("DT");
        String maHoaDon = String.valueOf(hoaDon);
        List<Object[]> rHd = truyVan("SELECT ma_hoa_don FROM hoa_don WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", hoaDon, hoaDon);
        if (!rHd.isEmpty()) maHoaDon = String.valueOf(rHd.get(0)[0]);

        String tenDangNhap = BoiCanhDangNhap.layTenDangNhap();
        capNhat(
                "INSERT INTO"
                    + " phieu_doi_tra(ma_phieu,ma_hoa_don,ten_dang_nhap,chenh_lech,trang_thai,ly_do,loai_phieu,gia_tri_duoc_doi,khoa_thao_tac)"
                    + " VALUES(?,?,?,?, 'CHO_XU_LY',?,'DOI_HANG',?,?)",
                maPhieu,
                maHoaDon,
                tenDangNhap,
                chenhLech,
                lyDo,
                giaTri,
                khoa);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    public void hoanTat(long phieu, long hoaDon) {
        String maHdThayThe = String.valueOf(hoaDon);
        List<Object[]> rHd = truyVan("SELECT ma_hoa_don FROM hoa_don WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", hoaDon, hoaDon);
        if (!rHd.isEmpty()) maHdThayThe = String.valueOf(rHd.get(0)[0]);

        if (capNhat(
                        "UPDATE phieu_doi_tra SET trang_thai='HOAN_TAT', ma_hoa_don_thay_the=?"
                                + " WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='CHO_XU_LY' AND loai_phieu='DOI_HANG'",
                        maHdThayThe,
                        phieu,
                        phieu)
                != 1) throw new IllegalStateException("Phiếu đổi hàng đã xử lý.");
    }

    public long layChenhLechTruoc(long hoaDon) {
        return laySo(
                "SELECT COALESCE(SUM(chenh_lech),0) FROM phieu_doi_tra WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " loai_phieu='DOI_HANG' AND trang_thai='HOAN_TAT'",
                hoaDon, hoaDon);
    }

    public boolean laHoaDonThayThe(long hoaDon) {
        return laySo(
                        "SELECT COUNT(*) FROM phieu_doi_tra WHERE (ma_hoa_don_thay_the = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don_thay_the, PATINDEX('%[0-9]%', ma_hoa_don_thay_the), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                                + " trang_thai='HOAN_TAT'",
                        hoaDon, hoaDon)
                > 0;
    }
}
