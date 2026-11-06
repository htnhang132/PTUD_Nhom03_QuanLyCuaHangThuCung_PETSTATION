package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.NhatKyChamSocDAO;
import com.petstation.util.DuLieu;

import java.util.List;

/** Truy vấn ChamSocService; quyền và transaction do tầng service quyết định. */
public final class NhatKyChamSocDAOImpl implements NhatKyChamSocDAO {
    public NhatKyChamSocDAOImpl() {}

    public List<Object[]> layLichSu(boolean spa, long id) {
        String fk = spa ? "ma_phieu_spa" : "ma_hop_dong";
        List<Object[]> rows = truyVan(
                "SELECT"
                    + " p.ma_nhat_ky, p.thoi_gian, n.ho_ten, p.tinh_trang, COALESCE(p.an_uong,N''), COALESCE(p.huong_dan_thuoc,N''), COALESCE(p.da_thuc_hien,p.ve_sinh,N''), p.anh_truoc, p.anh_sau"
                    + " FROM nhat_ky_cham_soc p JOIN nhan_vien n ON n.ma_nhan_vien=p.ma_nhan_vien WHERE (p."
                        + fk
                        + " = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(p." + fk + ", 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY p.thoi_gian DESC, p.ma_nhat_ky DESC",
                id, id);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    public List<Object[]> khoaPhieu(boolean spa, long id) {
        String table = spa ? "phieu_tiep_nhan_spa" : "hop_dong_luu_tru";
        String col = spa ? "ma_phieu" : "ma_hop_dong";
        return truyVan(
                "SELECT trang_thai FROM " + table + " WITH(UPDLOCK,HOLDLOCK) WHERE (" + col + " = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(" + col + ", 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", id, id);
    }

    public int themPhieu(
            Long spa,
            Long hotel,
            long nhanVien,
            String tinhTrang,
            String an,
            String thuoc,
            String viec,
            String anhTruoc,
            String anhSau) {

        String maSpa = null;
        if (spa != null) {
            maSpa = String.valueOf(spa);
            List<Object[]> r = truyVan("SELECT ma_phieu FROM phieu_tiep_nhan_spa WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", spa, spa);
            if (!r.isEmpty()) maSpa = String.valueOf(r.get(0)[0]);
        }
        String maHotel = null;
        if (hotel != null) {
            maHotel = String.valueOf(hotel);
            List<Object[]> r = truyVan("SELECT ma_hop_dong FROM hop_dong_luu_tru WHERE (ma_hop_dong = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", hotel, hotel);
            if (!r.isEmpty()) maHotel = String.valueOf(r.get(0)[0]);
        }
        String maNV = String.valueOf(nhanVien);
        List<Object[]> rNv = truyVan("SELECT ma_nhan_vien FROM nhan_vien WHERE (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", nhanVien, nhanVien);
        if (!rNv.isEmpty()) maNV = String.valueOf(rNv.get(0)[0]);

        return capNhat(
                "INSERT INTO"
                    + " nhat_ky_cham_soc(ma_nhat_ky, ma_phieu_spa, ma_hop_dong, ma_nhan_vien, tinh_trang, an_uong, ve_sinh, huong_dan_thuoc, da_thuc_hien, anh_truoc, anh_sau, thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,SYSDATETIME())",
                DuLieu.taoMa("NK"),
                maSpa,
                maHotel,
                maNV,
                tinhTrang,
                an,
                viec,
                thuoc,
                viec,
                anhTruoc,
                anhSau);
    }
}
