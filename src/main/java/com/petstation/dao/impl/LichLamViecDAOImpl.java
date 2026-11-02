package com.petstation.dao.impl;

import com.petstation.dao.LichLamViecDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC lịch làm việc DAO. */
public final class LichLamViecDAOImpl implements LichLamViecDAO {
    public LichLamViecDAOImpl() {}

    public long demPhanCong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=?", cacThamSo);
    }

    public long demNhanVienDangLam(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nhan_vien WHERE id=? AND dang_lam_viec=1", cacThamSo);
    }

    public List<Object[]> timKhoang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.ngay_lam,c.ma_ca,n.ma_nhan_vien,l.loai FROM lich_lam_viec l JOIN"
                        + " ca_lam_viec c ON c.id=l.ca_lam_viec_id JOIN nhan_vien n ON"
                        + " n.id=l.nhan_vien_id WHERE l.ngay_lam>=? AND l.ngay_lam<? AND"
                        + " l.trang_thai<>'CANCELLED' ORDER BY"
                        + " l.ngay_lam,c.gio_bat_dau,n.ma_nhan_vien",
                cacThamSo);
    }

    public long demLichTrungNgayCa(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_lam_viec WHERE nhan_vien_id=? AND"
                        + " ngay_lam=? AND trang_thai<>'CANCELLED' AND"
                        + " (loai='LEAVE' OR ca_lam_viec_id=(SELECT id FROM"
                        + " ca_lam_viec WHERE ma_ca=?))",
                cacThamSo);
    }

    public int capNhatPhanCong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM lich_lam_viec WHERE nhan_vien_id=? AND ngay_lam=? AND"
                        + " ca_lam_viec_id=(SELECT id FROM ca_lam_viec WHERE ma_ca=?) AND"
                        + " trang_thai='CANCELLED'",
                cacThamSo);
    }

    public int themLichLamViec(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_lam_viec(nhan_vien_id,ngay_lam,ca_lam_viec_id,loai)"
                        + " SELECT ?,?,id,'WORK' FROM ca_lam_viec WHERE ma_ca=?",
                cacThamSo);
    }

    public int capNhatXoa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET trang_thai='CANCELLED' WHERE nhan_vien_id=(SELECT id FROM"
                    + " nhan_vien WHERE ma_nhan_vien=?) AND ngay_lam=? AND ca_lam_viec_id=(SELECT"
                    + " id FROM ca_lam_viec WHERE ma_ca=?)",
                cacThamSo);
    }

    public int capNhatDanhDauNghiPhepDaDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET loai='LEAVE',trang_thai='OPEN' WHERE"
                        + " nhan_vien_id=? AND ngay_lam=?",
                cacThamSo);
    }

    public int themLichNghiPhep(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " lich_lam_viec(nhan_vien_id,ngay_lam,ca_lam_viec_id,loai)"
                        + " SELECT ?,?,id,'LEAVE' FROM ca_lam_viec WHERE"
                        + " ma_ca='CA01'",
                cacThamSo);
    }

    public long demThangSoGio(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(DATEDIFF(minute,c.gio_bat_dau,c.gio_ket_thuc)),0)/60"
                        + " FROM lich_lam_viec l JOIN ca_lam_viec c ON c.id=l.ca_lam_viec_id"
                        + " JOIN nhan_vien n ON n.id=l.nhan_vien_id WHERE n.ma_nhan_vien=? AND"
                        + " l.ngay_lam>=? AND l.ngay_lam<? AND l.loai='WORK' AND"
                        + " l.trang_thai<>'CANCELLED'",
                cacThamSo);
    }

    public int khoaLichKhoang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET trang_thai='LOCKED' WHERE ngay_lam>=? AND ngay_lam<? AND"
                        + " trang_thai='OPEN'",
                cacThamSo);
    }
}
