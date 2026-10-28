package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.ChamSocDAO;

import java.util.List;

/** Truy vấn ChamSocService; quyền và transaction do tầng service quyết định. */
public final class ChamSocDAOImpl implements ChamSocDAO {
    public ChamSocDAOImpl() {}

    public List<Object[]> layLichSu(boolean spa, long id) {
        String fk = spa ? "spa_id" : "hotel_id";
        return truyVan(
                "SELECT"
                    + " p.id,p.tao_luc,n.ho_ten,p.tinh_trang,p.an_uong,p.huong_dan_thuoc,p.da_thuc_hien,p.anh_truoc,p.anh_sau"
                    + " FROM phieu_cham_soc p JOIN nhan_vien n ON n.id=p.nhan_vien_id WHERE p."
                        + fk
                        + "=? ORDER BY p.id DESC",
                id);
    }

    public List<Object[]> khoaPhieu(boolean spa, long id) {
        String table = spa ? "phieu_tiep_nhan_spa" : "hop_dong_luu_tru";
        return truyVan(
                "SELECT trang_thai FROM " + table + " WITH(UPDLOCK,HOLDLOCK) WHERE id=?", id);
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

        return capNhat(
                "INSERT INTO"
                    + " phieu_cham_soc(spa_id,hotel_id,nhan_vien_id,tinh_trang,an_uong,huong_dan_thuoc,da_thuc_hien,anh_truoc,anh_sau)"
                    + " VALUES(?,?,?,?,?,?,?,?,?)",
                spa,
                hotel,
                nhanVien,
                tinhTrang,
                an,
                thuoc,
                viec,
                anhTruoc,
                anhSau);
    }
}
