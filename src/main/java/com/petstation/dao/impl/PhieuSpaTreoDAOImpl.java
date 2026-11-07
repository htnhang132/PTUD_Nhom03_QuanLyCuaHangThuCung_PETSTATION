package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.PhieuSpaTreoDAO;
import com.petstation.model.PhieuSpaTreo;
import com.petstation.util.BoiCanhDangNhap;
import com.petstation.util.DuLieu;
import java.util.List;

public final class PhieuSpaTreoDAOImpl implements PhieuSpaTreoDAO {
    @Override
    public long them(String ma, long nguoiLap, PhieuSpaTreo p) {
        String maPhieu = (ma == null || ma.isBlank()) ? DuLieu.taoMa("TREO") : ma;
        String tenDangNhap = null;
        try {
            tenDangNhap = BoiCanhDangNhap.layTenDangNhap();
        } catch (Exception ignored) {}
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            List<Object[]> rTk = truyVan("SELECT TOP 1 ten_dang_nhap FROM tai_khoan WHERE (ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ten_dang_nhap, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", nguoiLap, nguoiLap);
            if (!rTk.isEmpty()) {
                tenDangNhap = String.valueOf(rTk.get(0)[0]);
            } else {
                tenDangNhap = "admin";
            }
        }
        com.petstation.database.ThaoTacSQL.capNhat("""
            INSERT INTO phieu_spa_treo(ma_phieu, ten_dang_nhap, ma_khach, so_dien_thoai, ma_thu,
                ma_goi, ma_hen, can_nang, tinh_trang, yeu_cau) VALUES(?,?,?,?,?,?,?,?,?,?)
            """, maPhieu, tenDangNhap, p.maKhach(), p.soDienThoai(), p.maThu(), p.maGoi(),
            p.maHen(), p.canNang(), p.tinhTrang(), p.yeuCau());
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    @Override
    public void capNhat(long id, PhieuSpaTreo p) {
        com.petstation.database.ThaoTacSQL.capNhat("""
            UPDATE phieu_spa_treo SET ma_khach=?, so_dien_thoai=?, ma_thu=?, ma_goi=?,
                ma_hen=?, can_nang=?, tinh_trang=?, yeu_cau=?, cap_nhat_luc=SYSDATETIME()
            WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))
            """, p.maKhach(), p.soDienThoai(), p.maThu(), p.maGoi(), p.maHen(),
            p.canNang(), p.tinhTrang(), p.yeuCau(), id, id);
    }

    @Override
    public PhieuSpaTreo lay(long id) {
        var ds = truyVan("""
            SELECT ma_khach, so_dien_thoai, ma_thu, ma_goi, ma_hen, can_nang, tinh_trang, yeu_cau
            FROM phieu_spa_treo WITH(UPDLOCK,HOLDLOCK)
            WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))
            """, id, id);
        if (ds.isEmpty()) return null;
        Object[] d = ds.get(0);
        return new PhieuSpaTreo((String)d[0], (String)d[1], (String)d[2], (String)d[3],
            (String)d[4], (String)d[5], (String)d[6], (String)d[7]);
    }

    @Override
    public List<Object[]> danhSach() {
        var ds = truyVan("""
            SELECT p.ma_phieu AS ma_khoa, p.ma_phieu, COALESCE(k.ho_ten, N'Chưa chọn khách'), p.so_dien_thoai,
                COALESCE(t.ten, N'Chưa chọn thú cưng'), COALESCE(g.ten_goi, N'Chưa chọn dịch vụ'),
                p.cap_nhat_luc
            FROM phieu_spa_treo p
            LEFT JOIN khach_hang k ON k.ma_khach_hang = p.ma_khach
            LEFT JOIN thu_cung t ON t.ma_thu_cung = p.ma_thu
            LEFT JOIN goi_dich_vu_spa g ON g.ma_goi = p.ma_goi
            ORDER BY p.cap_nhat_luc DESC, p.ma_phieu DESC
            """);
        for (Object[] r : ds) {
            r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
        }
        return ds;
    }

    @Override
    public void xoa(long id) {
        com.petstation.database.ThaoTacSQL.capNhat("""
            DELETE FROM phieu_spa_treo
            WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))
            """, id, id);
    }
}
