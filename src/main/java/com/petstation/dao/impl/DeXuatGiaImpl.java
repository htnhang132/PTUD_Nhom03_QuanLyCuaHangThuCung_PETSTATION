package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.DeXuatGiaDAO;

import java.util.List;

/** Giá đề xuất được tách khỏi giá bán đang áp dụng. */
public final class DeXuatGiaImpl implements DeXuatGiaDAO {
    public DeXuatGiaImpl() {}

    public List<Object[]> timSanPham(String ma) {
        return truyVan(
                "SELECT id,gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?", ma);
    }

    public long them(long sanPham, long nguoi, long cu, long moi, String lyDo) {
        return com.petstation.database.ThaoTacSQL.them(
                "INSERT de_xuat_gia(san_pham_id,nguoi_de_xuat_id,gia_hien_tai,gia_de_xuat,ly_do)"
                        + " VALUES(?,?,?,?,?)",
                sanPham,
                nguoi,
                cu,
                moi,
                lyDo);
    }

    public List<Object[]> timChoDuyet(long id) {
        return truyVan(
                "SELECT d.san_pham_id,d.gia_hien_tai,d.gia_de_xuat,p.gia_ban FROM de_xuat_gia d"
                        + " WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p WITH(UPDLOCK,HOLDLOCK) ON"
                        + " p.id=d.san_pham_id WHERE d.id=? AND d.trang_thai='PENDING'",
                id);
    }

    public void apDung(long id, long sanPham, long gia, long nguoi) {
        capNhat("UPDATE san_pham SET gia_ban=? WHERE id=?", gia, sanPham);
        capNhat(
                "INSERT lich_su_gia(san_pham_id,gia_cu,gia_moi,nguoi_thay_doi_id) SELECT"
                        + " san_pham_id,gia_hien_tai,gia_de_xuat,? FROM de_xuat_gia WHERE id=?",
                nguoi,
                id);
        capNhat(
                "UPDATE de_xuat_gia SET trang_thai='APPROVED' WHERE id=? AND trang_thai='PENDING'",
                id);
    }

    public void tuChoi(long id) {
        capNhat(
                "UPDATE de_xuat_gia SET trang_thai='REJECTED' WHERE id=? AND trang_thai='PENDING'",
                id);
    }
}
