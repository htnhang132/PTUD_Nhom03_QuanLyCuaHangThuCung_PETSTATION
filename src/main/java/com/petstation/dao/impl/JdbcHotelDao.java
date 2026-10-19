package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcHotelDao {
    private JdbcHotelDao() {}

    public static int updateCheckIn(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE hop_dong_luu_tru SET trang_thai='CHECKED_IN' WHERE id=?", parameters);
    }

    public static List<Object[]> findBook(Object... parameters) {
        return SqlDatabase.query(
                "SELECT id,gia_dem,trang_thai,loai_phong FROM phong_hotel"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?",
                parameters);
    }

    public static long countBook(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (phong_id=? OR thu_cung_id=?) AND trang_thai IN"
                        + " ('BOOKED','CHECKED_IN') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>?",
                parameters);
    }

    public static long insertBook(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu)"
                    + " VALUES(?,?,?,?,?,?,?,'BOOKED',?,?)",
                parameters);
    }

    public static Object getCheckIn(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT phong_id FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND trang_thai='BOOKED'",
                parameters);
    }

    public static int updateCheckIn2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phong_hotel WITH(UPDLOCK,HOLDLOCK) SET"
                        + " trang_thai='OCCUPIED' WHERE id=? AND"
                        + " trang_thai='AVAILABLE'",
                parameters);
    }

    public static long countJournal(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND trang_thai='CHECKED_IN'",
                parameters);
    }

    public static int updateJournal(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                    + " nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,an_uong,ve_sinh,tinh_trang,thoi_gian)"
                    + " VALUES(?,?,?,?,?,SYSDATETIME())",
                parameters);
    }

    public static List<Object[]> findHotelBalance(Object... parameters) {
        return SqlDatabase.query(
                "SELECT"
                        + " h.ngay_vao,h.ngay_ra_du_kien,COALESCE(h.gia_dem,p.gia_dem),h.tien_coc"
                        + " FROM hop_dong_luu_tru h WITH(UPDLOCK,HOLDLOCK) JOIN phong_hotel p"
                        + " ON p.id=h.phong_id WHERE h.id=? AND h.trang_thai='CHECKED_IN' AND"
                        + " h.hoa_don_id IS NULL",
                parameters);
    }

    public static List<Object[]> journalDetails(Object... parameters) {
        return SqlDatabase.query(
                "SELECT j.thoi_gian,n.ho_ten,j.an_uong,j.ve_sinh,j.tinh_trang FROM"
                        + " nhat_ky_cham_soc j JOIN nhan_vien n ON n.id=j.nhan_vien_id WHERE"
                        + " j.hop_dong_id=? ORDER BY j.thoi_gian DESC",
                parameters);
    }

    public static int markRoomAvailable(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phong_hotel SET trang_thai='AVAILABLE'"
                        + " WHERE id=? AND trang_thai='CLEANING'",
                parameters);
    }
}
