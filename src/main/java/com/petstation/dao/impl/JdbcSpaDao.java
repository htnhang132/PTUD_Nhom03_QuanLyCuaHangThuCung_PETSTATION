package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcSpaDao {
    private JdbcSpaDao() {}

    public static int updateIntake(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lich_hen SET trang_thai='CHECKED_IN' WHERE id=?", parameters);
    }

    public static int updateIntake2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE thu_cung SET can_nang=?,tinh_trang_suc_khoe=? WHERE id=?", parameters);
    }

    public static long countSpaStatus(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM chi_tiet_phieu_spa WHERE phieu_spa_id=?", parameters);
    }

    public static long countAppointment(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE id=? AND" + " trang_thai='ACTIVE'",
                parameters);
    }

    public static long countAppointment2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " dang_lam_viec=1",
                parameters);
    }

    public static long countAppointment3(Object... parameters) {
        return SqlDatabase.number(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE" + " id=?", parameters);
    }

    public static long countAppointment4(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (nhan_vien_id=? OR thu_cung_id=?) AND trang_thai NOT IN"
                        + " ('CANCELLED','NO_SHOW','COMPLETED') AND bat_dau<? AND"
                        + " ket_thuc>?",
                parameters);
    }

    public static long insertAppointment(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id)"
                    + " VALUES(?,?,?,'SPA',?,?,'SCHEDULED',?,?)",
                parameters);
    }

    public static List<Object[]> findIntake(Object... parameters) {
        return SqlDatabase.query(
                "SELECT id,nhan_vien_id FROM lich_hen"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_lich_hen=? AND"
                        + " thu_cung_id=? AND trang_thai='SCHEDULED'",
                parameters);
    }

    public static long insertIntake(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,nhan_vien_id,ghi_chu,trang_thai,tao_luc)"
                    + " VALUES(?,?,?,?,?,?,'RECEIVED',SYSDATETIME())",
                parameters);
    }

    public static Object getIntake(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT g.ma_goi FROM lich_hen l JOIN goi_dich_vu_spa g ON"
                        + " g.id=l.dich_vu_id WHERE l.id=?",
                parameters);
    }

    public static List<Object[]> findAddSpaService(Object... parameters) {
        return SqlDatabase.query(
                "SELECT t.can_nang_thuc_te,p.loai,t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t WITH(UPDLOCK,HOLDLOCK) JOIN"
                        + " thu_cung p ON p.id=t.thu_cung_id WHERE t.id=?",
                parameters);
    }

    public static List<Object[]> findAddSpaService2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT TOP(1) b.goi_id,b.don_gia,g.ten_goi FROM bang_gia_spa b"
                        + " JOIN goi_dich_vu_spa g ON g.id=b.goi_id WHERE"
                        + " g.ma_goi=? AND g.trang_thai='ACTIVE' AND"
                        + " b.loai_thu_cung=? AND b.can_nang_tu<=? AND"
                        + " (b.can_nang_den IS NULL OR b.can_nang_den>?) ORDER BY"
                        + " b.can_nang_tu DESC,b.id",
                parameters);
    }

    public static int updateAddSpaService(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                        + " chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong)"
                        + " VALUES(?,?,?,?,1)",
                parameters);
    }

    public static int updateAddSpaService2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=(SELECT"
                        + " COALESCE(SUM(thanh_tien),0) FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=?) WHERE id=?",
                parameters);
    }

    public static int updateSpaStatus(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE id=? AND" + " trang_thai=?",
                parameters);
    }

    public static int updateSpaStatus2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lich_hen SET trang_thai='COMPLETED' WHERE id=(SELECT"
                        + " lich_hen_id FROM phieu_tiep_nhan_spa WHERE id=?)",
                parameters);
    }

    public static int cancelAppointmentRecord(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lich_hen SET trang_thai='CANCELLED' WHERE id=? AND"
                        + " trang_thai='SCHEDULED'",
                parameters);
    }

    public static Object appointmentPetCode(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.id=l.thu_cung_id WHERE l.id=?",
                parameters);
    }

    public static List<Object[]> ticketDetails(Object... parameters) {
        return SqlDatabase.query(
                "SELECT mo_ta,don_gia,so_luong,thanh_tien FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=? ORDER BY id",
                parameters);
    }
}
