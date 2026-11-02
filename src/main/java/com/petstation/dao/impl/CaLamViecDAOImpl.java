package com.petstation.dao.impl;

import com.petstation.dao.CaLamViecDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.PhuongThuc;
import com.petstation.util.DuLieu;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC ca thu ngân DAO. */
public final class CaLamViecDAOImpl implements CaLamViecDAO {
	public long layCocHotel(long ca, String phuongThuc) {
        PhuongThuc pt = PhuongThuc.docTuCSDL(phuongThuc);
        String maCu = switch (pt) {
            case TIEN_MAT -> "CASH";
            case CHUYEN_KHOAN -> "TRANSFER";
            case THE_NGAN_HANG -> "CARD";
            case DIEM_TICH_LUY -> "POINTS";
            case KET_HOP -> "MIXED";
        };
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(so_tien),0) FROM lich_su_thu_coc_hotel"
                        + " WHERE phien_lam_viec_id=?"
                        + " AND UPPER(LTRIM(RTRIM(phuong_thuc))) IN (?,?)",
                ca, pt.name(), maCu);
    }

    public CaLamViecDAOImpl() {}

    /** Chỉ trả về tài khoản đang làm việc, có quyền nhận ca. */
    public List<Object[]> layNguoiNhan(long taiKhoanHienTai) {
        return ThaoTacSQL.truyVan(
                "SELECT t.id,n.ho_ten,t.ten_dang_nhap FROM tai_khoan t JOIN nhan_vien n ON"
                    + " n.id=t.nhan_vien_id WHERE t.trang_thai IN ('HOAT_DONG','ACTIVE') AND"
                    + " n.dang_lam_viec=1 AND t.id<>? AND t.vai_tro IN"
                    + " ('QUAN_LY_CUA_HANG','NHAN_VIEN_BAN_HANG','STORE_MANAGER','SALES_STAFF')"
                    + " ORDER BY n.ho_ten",
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
        return layCocHotel(phien, PhuongThuc.TIEN_MAT.name());
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
        long phienId = ((Number) cacThamSo[0]).longValue();
        PhuongThuc mucTieu = PhuongThuc.docTuCSDL(String.valueOf(cacThamSo[1]));
        List<Object[]> cacHoaDon =
                ThaoTacSQL.truyVan(
                        "SELECT hinh_thuc_thanh_toan, tong_tien,"
                                + " COALESCE(chi_tiet_thanh_toan,N'') FROM hoa_don WHERE"
                                + " phien_lam_viec_id=? AND trang_thai='PAID'",
                        phienId);
        long tong = 0;
        for (Object[] hd : cacHoaDon) {
            String chiTiet = String.valueOf(hd[2]).trim();
            if (!chiTiet.isEmpty()) {
                for (String phan : chiTiet.split(";")) {
                    if (phan.isBlank()) continue;
                    String[] c = phan.split(":");
                    if (c.length >= 2 && PhuongThuc.docTuCSDL(c[0]) == mucTieu) {
                        tong += Long.parseLong(c[1]);
                    }
                }
            } else if (PhuongThuc.docTuCSDL(String.valueOf(hd[0])) == mucTieu) {
                tong += ((Number) hd[1]).longValue();
            }
        }
        return tong;
    }

    public long demSoLuongHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE phien_lam_viec_id=? AND trang_thai='PAID'",
                cacThamSo);
    }

    public long demDuKienTienMat(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(-p.chenh_lech),0) FROM phieu_doi_tra p JOIN hoa_don h ON"
                        + " h.id=p.hoa_don_id WHERE h.phien_lam_viec_id=? AND"
                        + " p.trang_thai='COMPLETED' AND p.loai_phieu='LEGACY_RETURN' AND"
                        + " h.hinh_thuc_thanh_toan IN ('TIEN_MAT','CASH')",
                cacThamSo);
    }

    public int capNhatMo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO phien_lam_viec(ma_phien,tai_khoan_id,ten_ca,tien_dau_ca,mo_luc)"
                        + " VALUES(?,?,?,?,SYSDATETIME())",
                DuLieu.taoMa("MPI"),
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2]);
    }
}
