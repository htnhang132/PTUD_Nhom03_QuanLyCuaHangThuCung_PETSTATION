package com.petstation.dao.impl;

import com.petstation.dao.CaThuNganDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC ca thu ngân DAO. */
public final class CaThuNganImpl implements CaThuNganDAO {
    public long layCocHotel(long ca, String phuongThuc) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(so_tien),0) FROM khoan_thu WHERE phien_id=? AND hop_dong_id IS"
                        + " NOT NULL AND phuong_thuc=?",
                ca,
                phuongThuc);
    }

    public CaThuNganImpl() {}

    /** Chỉ trả về tài khoản đang làm việc, có quyền nhận ca. */
    public List<Object[]> layNguoiNhan(long taiKhoanHienTai) {
        return ThaoTacSQL.truyVan(
                "SELECT t.id,n.ho_ten,t.ten_dang_nhap FROM tai_khoan t JOIN nhan_vien n ON"
                    + " n.id=t.nhan_vien_id WHERE t.trang_thai='ACTIVE' AND n.dang_lam_viec=1 AND"
                    + " t.id<>? AND t.vai_tro IN ('STORE_MANAGER','SALES_STAFF') AND EXISTS(SELECT"
                    + " 1 FROM vai_tro_quyen q WHERE q.vai_tro=t.vai_tro AND"
                    + " q.ma_quyen='CASH_SHIFT_MANAGE') ORDER BY n.ho_ten",
                taiKhoanHienTai);
    }

    public int dongCoBanGiao(
            long thucDem, long chenhLech, String lyDo, String hinhThuc, Long nguoiNhan, long id) {
        return ThaoTacSQL.capNhat(
                "UPDATE phien_lam_viec SET"
                    + " tien_thuc_te=?,chenh_lech=?,giai_trinh=?,hinh_thuc_ban_giao=?,nguoi_nhan_ban_giao_id=?,dong_luc=SYSDATETIME(),trang_thai='HANDED_OVER'"
                    + " WHERE id=? AND trang_thai='OPEN'",
                thucDem,
                chenhLech,
                lyDo,
                hinhThuc,
                nguoiNhan,
                id);
    }

    public long layTongCocDaNhan(long phien) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(so_tien),0) FROM khoan_thu WHERE phien_id=? AND hop_dong_id IS"
                        + " NOT NULL AND phuong_thuc='CASH'",
                phien);
    }

    public long demDong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE id=?", cacThamSo);
    }

    public List<Object[]> timHienTai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,ten_ca,mo_luc,tien_dau_ca FROM phien_lam_viec WHERE"
                        + " tai_khoan_id=? AND trang_thai='OPEN'",
                cacThamSo);
    }

    public long demBanHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(k.so_tien),0) FROM khoan_thu k JOIN hoa_don h ON"
                        + " h.id=k.hoa_don_id WHERE k.phien_id=? AND k.phuong_thuc=? AND"
                        + " h.trang_thai='PAID'",
                cacThamSo);
    }

    public long demSoLuongHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                        + " d.phien_lam_viec_id=? AND h.trang_thai='PAID'",
                cacThamSo);
    }

    public long demDuKienTienMat(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(-p.chenh_lech),0) FROM phieu_doi_tra p JOIN hoa_don h ON"
                        + " h.id=p.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                        + " d.phien_lam_viec_id=? AND p.trang_thai='COMPLETED' AND"
                        + " p.loai_phieu='LEGACY_RETURN' AND h.hinh_thuc_thanh_toan='CASH'",
                cacThamSo);
    }

    public int capNhatMo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO phien_lam_viec(tai_khoan_id,ten_ca,tien_dau_ca,mo_luc)"
                        + " VALUES(?,?,?,SYSDATETIME())",
                cacThamSo);
    }
}
