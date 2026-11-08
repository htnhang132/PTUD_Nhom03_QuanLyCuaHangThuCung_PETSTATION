package com.petstation.dao.impl;

import com.petstation.dao.CaLamViecDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.PhuongThuc;
import com.petstation.util.DuLieu;

import java.util.List;

public final class CaLamViecDAOImpl implements CaLamViecDAO {
    @Override
    public long layCocHotel(String maPhien, String phuongThuc) {
        PhuongThuc pt = PhuongThuc.docTuCSDL(phuongThuc);
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(so_tien),0) FROM lich_su_thu_coc_hotel"
                        + " WHERE ma_phien=?"
                        + " AND UPPER(LTRIM(RTRIM(phuong_thuc))) = ?",
                maPhien, pt.name());
    }

    public CaLamViecDAOImpl() {}

    @Override
    public List<Object[]> layNguoiNhan(String tenDangNhapHienTai) {
        return ThaoTacSQL.truyVan(
                "SELECT t.ten_dang_nhap AS ma_khoa,n.ho_ten,t.ten_dang_nhap FROM tai_khoan t JOIN nhan_vien n ON"
                    + " n.ma_nhan_vien=t.ma_nhan_vien WHERE t.trang_thai='HOAT_DONG' AND"
                    + " n.dang_lam_viec=1 AND t.ten_dang_nhap<>? AND t.vai_tro IN"
                    + " ('QUAN_LY_CUA_HANG','NHAN_VIEN_BAN_HANG')"
                    + " ORDER BY n.ho_ten",
                tenDangNhapHienTai);
    }

    @Override
    public int dongCoBanGiao(
            long thucDem, long chenhLech, String lyDo, String hinhThuc, String nguoiNhan, String maPhien) {
        return ThaoTacSQL.capNhat(
                "UPDATE phien_lam_viec SET"
                    + " tien_thuc_te=?,chenh_lech=?,giai_trinh=?,hinh_thuc_ban_giao=?,nguoi_nhan_ban_giao=?,dong_luc=SYSDATETIME(),trang_thai='DA_BAN_GIAO'"
                    + " WHERE ma_phien=? AND trang_thai='DANG_MO'",
                thucDem,
                chenhLech,
                lyDo,
                hinhThuc,
                nguoiNhan,
                maPhien);
    }

    @Override
    public long layTongCocDaNhan(String maPhien) {
        return layCocHotel(maPhien, PhuongThuc.TIEN_MAT.name());
    }

    @Override
    public long demDong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE ma_phien=?", cacThamSo);
    }

    @Override
    public List<Object[]> timHienTai(Object... cacThamSo) {
        Object tk = cacThamSo != null && cacThamSo.length > 0 ? cacThamSo[0] : null;
        String cur = null;
        try {
            cur = com.petstation.util.BoiCanhDangNhap.layTenDangNhap();
        } catch (Exception ignored) {}
        if (cur != null && !cur.isBlank()) {
            List<Object[]> kq = ThaoTacSQL.truyVan(
                    "SELECT ma_phien,ten_ca,mo_luc,tien_dau_ca FROM phien_lam_viec WHERE"
                            + " ten_dang_nhap=? AND trang_thai='DANG_MO' ORDER BY mo_luc DESC",
                    cur);
            if (!kq.isEmpty()) return kq;
        }
        if (tk == null) return java.util.List.of();
        return ThaoTacSQL.truyVan(
                "SELECT ma_phien,ten_ca,mo_luc,tien_dau_ca FROM phien_lam_viec WHERE"
                        + " (ten_dang_nhap=CAST(? AS VARCHAR) OR ten_dang_nhap IN (SELECT ten_dang_nhap FROM tai_khoan WHERE ma_nhan_vien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)))"
                        + " AND trang_thai='DANG_MO' ORDER BY mo_luc DESC",
                tk, tk, tk);
    }

    @Override
    public long demBanHang(Object... cacThamSo) {
        String maPhien = String.valueOf(cacThamSo[0]);
        PhuongThuc mucTieu = PhuongThuc.docTuCSDL(String.valueOf(cacThamSo[1]));
        List<Object[]> cacHoaDon =
                ThaoTacSQL.truyVan(
                        "SELECT hinh_thuc_thanh_toan, tong_tien,"
                                + " COALESCE(chi_tiet_thanh_toan,N'') FROM hoa_don WHERE"
                                + " ma_phien=? AND trang_thai='DA_THANH_TOAN'",
                        maPhien);
        long tong = 0;
        for (Object[] hd : cacHoaDon) {
            String chiTiet = String.valueOf(hd[2]).trim();
            if (!chiTiet.isEmpty()) {
                for (String phan : chiTiet.split(";")) {
                    if (phan.isBlank()) continue;
                    String[] c = phan.split(":");
                    if (c.length >= 2 && PhuongThuc.docTuCSDL(c[0]) == mucTieu) {
                        tong += new java.math.BigDecimal(c[1].trim()).longValue();
                    }
                }
            } else if (PhuongThuc.docTuCSDL(String.valueOf(hd[0])) == mucTieu) {
                tong += ((Number) hd[1]).longValue();
            }
        }
        return tong;
    }

    @Override
    public long demSoLuongHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE ma_phien=? AND trang_thai='DA_THANH_TOAN'",
                cacThamSo);
    }

    @Override
    public long demDuKienTienMat(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(-p.chenh_lech),0) FROM phieu_doi_tra p JOIN hoa_don h ON"
                        + " h.ma_hoa_don=p.ma_hoa_don WHERE h.ma_phien=? AND"
                        + " p.trang_thai='HOAN_TAT' AND p.loai_phieu='TRA_HANG_CU' AND"
                        + " h.hinh_thuc_thanh_toan = 'TIEN_MAT'",
                cacThamSo);
    }

    @Override
    public int capNhatMo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO phien_lam_viec(ma_phien,ten_dang_nhap,ma_ca,ten_ca,tien_dau_ca,mo_luc,trang_thai)"
                        + " VALUES(?,?,COALESCE((SELECT TOP 1 ma_ca FROM ca_lam_viec WHERE ten_ca=?), N'CA01'),?,?,SYSDATETIME(),N'DANG_MO')",
                DuLieu.taoMa("MPI"),
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }
}
