package com.petstation.dao.impl;

import com.petstation.dao.PheDuyetDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC phê duyệt DAO. */
public final class PheDuyetImpl implements PheDuyetDAO {
    public PheDuyetImpl() {}

    public long demSoLuongDangCho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM yeu_cau_phe_duyet WHERE trang_thai='PENDING'", cacThamSo);
    }

    public int capNhatTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='REJECTED' WHERE id=?", cacThamSo);
    }

    public int tuChoiBienBanKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='REJECTED' WHERE id=?", cacThamSo);
    }

    public long demApDungPheDuyet(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE id=? AND trang_thai='PAID'", cacThamSo);
    }

    public int capNhatApDungPheDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE hoa_don SET ghi_chu=? WHERE id=?", cacThamSo);
    }

    public int capNhatGuiThaoTac(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload,gui_luc)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    public List<Object[]> timTatCa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " y.ma_yeu_cau,y.loai,n.ho_ten,y.gui_luc,y.ly_do,y.noi_dung_truoc,y.noi_dung_sau,y.trang_thai,COALESCE(a.ho_ten,N''),COALESCE(y.phan_hoi,N'')"
                    + " FROM yeu_cau_phe_duyet y JOIN tai_khoan t ON t.id=y.nguoi_gui_id JOIN"
                    + " nhan_vien n ON n.id=t.nhan_vien_id LEFT JOIN tai_khoan u ON"
                    + " u.id=y.nguoi_xu_ly_id LEFT JOIN nhan_vien a ON a.id=u.nhan_vien_id ORDER BY"
                    + " y.gui_luc DESC,y.id DESC",
                cacThamSo);
    }

    public List<Object[]> timDuyet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT loai_doi_tuong,ma_doi_tuong,payload FROM"
                        + " yeu_cau_phe_duyet WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_yeu_cau=? AND trang_thai='PENDING'",
                cacThamSo);
    }

    public int capNhatDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                        + " trang_thai='APPROVED',nguoi_xu_ly_id=?,xu_ly_luc=SYSDATETIME()"
                        + " WHERE ma_yeu_cau=?",
                cacThamSo);
    }

    public List<Object[]> timTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT loai_doi_tuong,ma_doi_tuong FROM yeu_cau_phe_duyet"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_yeu_cau=? AND"
                        + " trang_thai='PENDING'",
                cacThamSo);
    }

    public int tuChoiPhieuDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_doi_tra SET trang_thai='REJECTED' WHERE id=? AND"
                        + " trang_thai='PENDING'",
                cacThamSo);
    }

    public int danhDauYeuCauTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                    + " trang_thai='REJECTED',nguoi_xu_ly_id=?,phan_hoi=?,xu_ly_luc=SYSDATETIME()"
                    + " WHERE ma_yeu_cau=?",
                cacThamSo);
    }
}
