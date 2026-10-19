package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcSalesDao {
    private JdbcSalesDao() {}

    public static Object getPay(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT so_hoa_don FROM hoa_don WHERE idempotency_key=?", parameters);
    }

    public static Object getPay2(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT so_hoa_don FROM hoa_don WHERE idempotency_key=?", parameters);
    }

    public static int updatePay(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-?+? WHERE id=?", parameters);
    }

    public static int updateUpdateTier(Object... parameters) {
        return SqlDatabase.update("UPDATE khach_hang SET hang_thanh_vien=? WHERE id=?", parameters);
    }

    public static long countResume(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM chi_tiet_gio_hang_treo WHERE gio_id=?", parameters);
    }

    public static int updateDeleteHold(Object... parameters) {
        return SqlDatabase.update("DELETE FROM chi_tiet_gio_hang_treo WHERE gio_id=?", parameters);
    }

    public static int updateDeleteHold2(Object... parameters) {
        return SqlDatabase.update(
                "DELETE FROM gio_hang_treo WHERE id=? AND tai_khoan_id=?", parameters);
    }

    public static Object getCheckout(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT khach_hang_id FROM hop_dong_luu_tru WHERE id=?", parameters);
    }

    public static List<Object[]> findCheckout(Object... parameters) {
        return SqlDatabase.query(
                "SELECT ma_khach_hang,ho_ten,diem_tich_luy FROM khach_hang WHERE id=?", parameters);
    }

    public static long countCancelInvoice(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE hoa_don_id=?", parameters);
    }

    public static long countCancelInvoice2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WHERE hoa_don_id=?", parameters);
    }

    public static int updateCancelInvoice(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", parameters);
    }

    public static int updateCancelInvoice2(Object... parameters) {
        return SqlDatabase.update("UPDATE khach_hang SET diem_tich_luy=? WHERE id=?", parameters);
    }

    public static int updateCancelInvoice3(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE hoa_don SET trang_thai='CANCELLED' WHERE id=?", parameters);
    }

    public static int updateCancelInvoice4(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE don_hang SET trang_thai='CANCELLED' WHERE id=?", parameters);
    }

    public static long countPay(Object... parameters) {
        return SqlDatabase.number(
                "SELECT id FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " tai_khoan_id=? AND trang_thai='OPEN'",
                parameters);
    }

    public static long countPay2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM chot_ngay WHERE ngay_chot=CAST(GETDATE()"
                        + " AS DATE) AND trang_thai='LOCKED'",
                parameters);
    }

    public static List<Object[]> findPay(Object... parameters) {
        return SqlDatabase.query(
                "SELECT diem_tich_luy,dang_hoat_dong FROM khach_hang"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                parameters);
    }

    public static int updatePay2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE voucher WITH(UPDLOCK,HOLDLOCK) SET"
                        + " da_dung=da_dung+1,su_dung_luc=SYSDATETIME() WHERE"
                        + " ma_voucher=? AND trang_thai='ACTIVE' AND"
                        + " da_dung<so_luong AND hieu_luc_den>SYSDATETIME() AND"
                        + " don_toi_thieu<=? AND (khach_hang_id IS NULL OR"
                        + " khach_hang_id=?) AND (chi_ap_dung_spa=0 OR"
                        + " ?='SPA')",
                parameters);
    }

    public static long insertPay(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                        + " don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai,tao_luc)"
                        + " VALUES(?,?,?,'PAID',?)",
                parameters);
    }

    public static List<Object[]> findPay2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT id,gia_ban,trang_thai FROM san_pham"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?",
                parameters);
    }

    public static long insertPay2(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " chi_tiet_don_hang(don_hang_id,san_pham_id,ma_dich_vu,ten_hang,so_luong,don_gia)"
                    + " VALUES(?,?,?,?,?,?)",
                parameters);
    }

    public static List<Object[]> findPay3(Object... parameters) {
        return SqlDatabase.query(
                "SELECT id,so_luong_ton FROM lo_hang"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE san_pham_id=? AND"
                        + " trang_thai='AVAILABLE' AND so_luong_ton>0 AND"
                        + " (han_su_dung IS NULL OR"
                        + " han_su_dung>=CAST(GETDATE() AS DATE)) ORDER BY"
                        + " CASE WHEN han_su_dung IS NULL THEN 1 ELSE 0"
                        + " END,han_su_dung,id",
                parameters);
    }

    public static int updatePay3(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-?"
                        + " WHERE id=? AND so_luong_ton>=?",
                parameters);
    }

    public static int updatePay4(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                        + " phan_bo_lo_hang(chi_tiet_don_hang_id,lo_hang_id,so_luong)"
                        + " VALUES(?,?,?)",
                parameters);
    }

    public static long insertPay3(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " hoa_don(so_hoa_don,don_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,idempotency_key,thanh_toan_luc,tam_tinh,giam_gia,diem_dung,ma_voucher)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                parameters);
    }

    public static int updatePay5(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                        + " lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do,tao_luc)"
                        + " SELECT id,?,?,diem_tich_luy,N'Thanh toán hóa"
                        + " đơn',SYSDATETIME() FROM khach_hang WHERE id=?",
                parameters);
    }

    public static long countPay3(Object... parameters) {
        return SqlDatabase.number(
                "SELECT tong_tien FROM phieu_tiep_nhan_spa"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='COMPLETED' AND hoa_don_id IS NULL",
                parameters);
    }

    public static int updatePay6(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_tiep_nhan_spa SET"
                        + " hoa_don_id=?,trang_thai='PAID' WHERE id=? AND"
                        + " hoa_don_id IS NULL",
                parameters);
    }

    public static int updatePay7(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE hop_dong_luu_tru SET"
                        + " hoa_don_id=?,trang_thai='CHECKED_OUT',ngay_ra_thuc_te=SYSDATETIME()"
                        + " WHERE id=? AND trang_thai='CHECKED_IN' AND"
                        + " hoa_don_id IS NULL",
                parameters);
    }

    public static int updatePay8(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phong_hotel SET trang_thai='CLEANING' WHERE id=(SELECT"
                        + " phong_id FROM hop_dong_luu_tru WHERE id=?)",
                parameters);
    }

    public static long countUpdateTier(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h JOIN don_hang d ON"
                        + " d.id=h.don_hang_id WHERE d.khach_hang_id=? AND h.trang_thai='PAID'",
                parameters);
    }

    public static List<Object[]> findReceipt(Object... parameters) {
        return SqlDatabase.query(
                "SELECT"
                    + " h.thanh_toan_luc,h.don_hang_id,h.tam_tinh,h.giam_gia,h.tong_tien,h.hinh_thuc_thanh_toan,h.tien_khach_dua,h.tien_thoi,COALESCE(k.ho_ten,N'Khách"
                    + " lẻ'),COALESCE(h.ma_voucher,''),n.ho_ten FROM hoa_don h JOIN don_hang d ON"
                    + " d.id=h.don_hang_id JOIN phien_lam_viec p ON p.id=d.phien_lam_viec_id JOIN"
                    + " tai_khoan t ON t.id=p.tai_khoan_id JOIN nhan_vien n ON n.id=t.nhan_vien_id"
                    + " LEFT JOIN khach_hang k ON k.id=d.khach_hang_id WHERE h.so_hoa_don=?",
                parameters);
    }

    public static List<Object[]> findReceipt2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT"
                    + " COALESCE(p.ma_san_pham,c.ma_dich_vu),COALESCE(c.ten_hang,p.ten),c.don_gia,c.so_luong"
                    + " FROM chi_tiet_don_hang c LEFT JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                    + " c.don_hang_id=? ORDER BY c.id",
                parameters);
    }

    public static long insertHold(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO gio_hang_treo(ma_gio,tai_khoan_id,khach_hang_id)" + " VALUES(?,?,?)",
                parameters);
    }

    public static int updateHold(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                        + " chi_tiet_gio_hang_treo(gio_id,ma_san_pham,ten,don_gia,so_luong)"
                        + " VALUES(?,?,?,?,?)",
                parameters);
    }

    public static long countResume2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM gio_hang_treo WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND tai_khoan_id=?",
                parameters);
    }

    public static List<Object[]> findResume(Object... parameters) {
        return SqlDatabase.query(
                "SELECT p.ma_san_pham,p.ten,p.gia_ban,g.so_luong FROM"
                        + " chi_tiet_gio_hang_treo g JOIN san_pham p ON"
                        + " p.ma_san_pham=g.ma_san_pham WHERE g.gio_id=? AND"
                        + " p.trang_thai='SELLING'",
                parameters);
    }

    public static List<Object[]> findResume2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT k.ma_khach_hang,k.ho_ten,k.diem_tich_luy FROM"
                        + " gio_hang_treo g JOIN khach_hang k ON"
                        + " k.id=g.khach_hang_id WHERE g.id=?",
                parameters);
    }

    public static long countDeleteHold(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM gio_hang_treo WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND tai_khoan_id=?",
                parameters);
    }

    public static long countCheckout(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE id=? AND"
                        + " trang_thai='COMPLETED' AND hoa_don_id IS NULL",
                parameters);
    }

    public static List<Object[]> findCheckout2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT COALESCE(g.ma_goi,'DV-PHU-PHI'),c.mo_ta,c.don_gia,c.so_luong"
                        + " FROM chi_tiet_phieu_spa c LEFT JOIN goi_dich_vu_spa g ON"
                        + " g.id=c.goi_id WHERE c.phieu_spa_id=?",
                parameters);
    }

    public static Object getCheckout2(Object... parameters) {
        return SqlDatabase.scalar(
                "SELECT p.khach_hang_id FROM phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                        + " p.id=t.thu_cung_id WHERE t.id=?",
                parameters);
    }

    public static List<Object[]> findCancelInvoice(Object... parameters) {
        return SqlDatabase.query(
                "SELECT h.don_hang_id,h.diem_cong,h.diem_dung,h.ma_voucher,d.khach_hang_id"
                        + " FROM hoa_don h WITH(UPDLOCK,HOLDLOCK) JOIN don_hang d ON"
                        + " d.id=h.don_hang_id WHERE h.id=? AND h.trang_thai='PAID'",
                parameters);
    }

    public static long countCancelInvoice3(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM phieu_doi_tra WHERE hoa_don_id=? AND trang_thai IN"
                        + " ('COMPLETED','PENDING')",
                parameters);
    }

    public static List<Object[]> findCancelInvoice2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT b.lo_hang_id,b.so_luong FROM phan_bo_lo_hang b JOIN"
                        + " chi_tiet_don_hang i ON i.id=b.chi_tiet_don_hang_id WHERE"
                        + " i.don_hang_id=?",
                parameters);
    }

    public static long countCancelInvoice4(Object... parameters) {
        return SqlDatabase.number(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK)" + " WHERE id=?",
                parameters);
    }

    public static int updateCancelInvoice5(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do)"
                        + " VALUES(?,?,?,?,N'Hủy hóa đơn')",
                parameters);
    }

    public static int updateCancelInvoice6(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE voucher SET da_dung=CASE WHEN da_dung>0 THEN da_dung-1 ELSE 0 END WHERE"
                        + " ma_voucher=?",
                parameters);
    }

    public static List<Object[]> sellableProducts(Object... parameters) {
        return SqlDatabase.query(
                "SELECT p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,COALESCE(SUM(l.so_luong_ton),0)"
                    + " FROM san_pham p LEFT JOIN lo_hang l ON l.san_pham_id=p.id AND"
                    + " l.trang_thai='AVAILABLE' AND (l.han_su_dung IS NULL OR"
                    + " l.han_su_dung>=CAST(GETDATE() AS date)) WHERE p.trang_thai='SELLING' GROUP"
                    + " BY p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban ORDER BY p.ma_san_pham",
                parameters);
    }
}
