package com.petstation.dao.impl;

import com.petstation.dao.ThongKeDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

public final class ThongKeDAOImpl implements ThongKeDAO {
    public ThongKeDAOImpl() {}

    public List<Object[]> timKho(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " p.ma_san_pham,p.ten,COALESCE(SUM(l.so_luong_ton),0),p.ton_toi_thieu,p.gia_nhap_gan_nhat"
                    + " FROM san_pham p LEFT JOIN lo_hang l ON l.ma_san_pham=p.ma_san_pham GROUP BY"
                    + " p.ma_san_pham,p.ten,p.ton_toi_thieu,p.gia_nhap_gan_nhat ORDER BY"
                    + " p.ma_san_pham",
                cacThamSo);
    }

    public List<Object[]> timHanSuDung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham,p.ten,l.so_lo,l.han_su_dung,l.so_luong_ton FROM lo_hang l"
                        + " JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE l.so_luong_ton>0 AND"
                        + " l.han_su_dung<=DATEADD(day,?,CAST(GETDATE() AS date)) ORDER BY"
                        + " l.han_su_dung",
                cacThamSo);
    }

    public List<Object[]> timTrangThaiPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT trang_thai,COUNT(*) FROM phong_hotel GROUP BY trang_thai ORDER BY"
                        + " trang_thai",
                cacThamSo);
    }

    public List<Object[]> timCacKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " k.ma_khach_hang,k.ho_ten,k.hang_thanh_vien,k.diem_tich_luy,COALESCE(SUM(h.tong_tien),0)"
                    + " FROM khach_hang k LEFT JOIN hoa_don h ON h.ma_khach_hang=k.ma_khach_hang AND"
                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<? GROUP BY"
                    + " k.ma_khach_hang,k.ho_ten,k.hang_thanh_vien,k.diem_tich_luy ORDER BY"
                    + " COALESCE(SUM(h.tong_tien),0) DESC,k.ma_khach_hang",
                cacThamSo);
    }

    public List<Object[]> timCacVoucher(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_voucher,menh_gia,da_dung,so_luong,hieu_luc_den FROM voucher ORDER BY"
                        + " ma_voucher",
                cacThamSo);
    }

    public List<Object[]> timNhaCungCapCongNo(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ma_nha_cung_cap,n.ten,"
                    + " COALESCE(SUM(p.tong_tien),0) AS tong_nhap,"
                    + " COALESCE(SUM(p.da_tra),0) AS da_tra,"
                    + " COALESCE(SUM(p.tong_tien-p.da_tra),0) AS du_no"
                    + " FROM nha_cung_cap n"
                    + " LEFT JOIN phieu_nhap_kho p ON p.ma_nha_cung_cap=n.ma_nha_cung_cap"
                    + " AND p.trang_thai='DA_NHAP'"
                    + " AND p.ngay_nhap>=? AND p.ngay_nhap<?"
                    + " GROUP BY n.ma_nha_cung_cap,n.ten"
                    + " ORDER BY du_no DESC,n.ma_nha_cung_cap",
                cacThamSo);
    }

    public List<Object[]> timNhanVien(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ma_nhan_vien,n.ho_ten,n.chuc_vu,COALESCE(COUNT(l.ma_ca),0),COALESCE(SUM(DATEDIFF(minute,c.gio_bat_dau,c.gio_ket_thuc)),0)/60.0"
                    + " FROM nhan_vien n LEFT JOIN lich_lam_viec l ON l.ma_nhan_vien=n.ma_nhan_vien AND"
                    + " l.loai='DI_LAM' AND l.trang_thai<>'DA_HUY' AND l.ngay_lam>=? AND"
                    + " l.ngay_lam<? LEFT JOIN ca_lam_viec c ON c.ma_ca=l.ma_ca GROUP BY"
                    + " n.ma_nhan_vien,n.ho_ten,n.chuc_vu ORDER BY n.ma_nhan_vien",
                cacThamSo);
    }

    private static String dieuKienTaiKhoan(Long taiKhoan) {
        return taiKhoan == null
                ? ""
                : " AND s.ten_dang_nhap IN (SELECT tk2.ten_dang_nhap FROM tai_khoan tk2 WHERE tk2.ten_dang_nhap = CAST("
                        + taiKhoan
                        + " AS VARCHAR) OR TRY_CAST(SUBSTRING(tk2.ma_nhan_vien, PATINDEX('%[0-9]%', tk2.ma_nhan_vien), 20) AS BIGINT) = "
                        + taiKhoan
                        + ")";
    }

    public List<Object[]> layDoanhThuTheoNgay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        String loc = dieuKienTaiKhoan(taiKhoan);
        return ThaoTacSQL.truyVan(
                "WITH ledger AS (SELECT CAST(h.thanh_toan_luc AS date) ngay,h.tong_tien so_tien"
                    + " FROM hoa_don h JOIN phien_lam_viec s"
                    + " ON s.ma_phien=h.ma_phien WHERE h.trang_thai='DA_THANH_TOAN'"
                        + loc
                        + " UNION ALL SELECT CAST(p.tao_luc AS date),p.chenh_lech FROM"
                        + " phieu_doi_tra p JOIN hoa_don h ON h.ma_hoa_don=p.ma_hoa_don JOIN phien_lam_viec s"
                        + " ON s.ma_phien=h.ma_phien"
                        + " WHERE p.trang_thai='HOAN_TAT' AND p.loai_phieu='TRA_HANG_CU'"
                        + loc
                        + " UNION ALL SELECT CAST(k.nhan_coc_luc AS date),k.tien_coc FROM"
                        + " hop_dong_luu_tru k JOIN phien_lam_viec s ON s.ma_phien=k.ma_phien_nhan_coc"
                        + " WHERE k.da_nhan_coc=1 AND k.nhan_coc_luc IS NOT NULL"
                        + loc
                        + ") SELECT ngay,SUM(so_tien) FROM ledger WHERE ngay>=? AND ngay<? GROUP BY"
                        + " ngay ORDER BY ngay",
                tu,
                denNgay);
    }

    public List<Object[]> layDoanhThu(
            java.time.LocalDate tu,
            java.time.LocalDate denNgay,
            String nhomTongHop,
            Long taiKhoan,
            Long nhanVien) {
        String nhom =
                switch (nhomTongHop) {
                    case "method" -> "LEFT(sp.value, CHARINDEX(':', sp.value) - 1)";
                    case "staff" -> "n.ho_ten";
                    case "shift" -> "s.ten_ca";
                    default ->
                            "CASE WHEN EXISTS(SELECT 1 FROM phieu_tiep_nhan_spa t WHERE"
                                + " t.ma_hoa_don=h.ma_hoa_don) THEN 'Spa' WHEN EXISTS(SELECT 1 FROM"
                                + " hop_dong_luu_tru t WHERE t.ma_hoa_don=h.ma_hoa_don) THEN 'Hotel' ELSE"
                                + " N'Hàng hóa' END";
                };
        String nhomDoiTra =
                switch (nhomTongHop) {
                    case "method" -> "h.hinh_thuc_thanh_toan";
                    case "staff" -> "n.ho_ten";
                    case "shift" -> "s.ten_ca";
                    default ->
                            "CASE WHEN EXISTS(SELECT 1 FROM phieu_tiep_nhan_spa t WHERE"
                                + " t.ma_hoa_don=h.ma_hoa_don) THEN 'Spa' WHEN EXISTS(SELECT 1 FROM"
                                + " hop_dong_luu_tru t WHERE t.ma_hoa_don=h.ma_hoa_don) THEN 'Hotel' ELSE"
                                + " N'Hàng hóa' END";
                };
        String nhomCoc =
                switch (nhomTongHop) {
                	case "method" -> "k.phuong_thuc";
                    case "staff" -> "n.ho_ten";
                    case "shift" -> "s.ten_ca";
                    default -> "'Hotel'";
                };
        String loc = dieuKienTaiKhoan(taiKhoan);
        return ThaoTacSQL.truyVan(
                "WITH ledger AS (SELECT "
                        + nhom
                        + " nhan,CAST(SUBSTRING(sp.value, CHARINDEX(':', sp.value) + 1, CHARINDEX(':', sp.value + ':', CHARINDEX(':', sp.value) + 1) - CHARINDEX(':', sp.value) - 1) AS decimal(18,2)) so_tien,h.ma_hoa_don hoa_don,CAST(h.thanh_toan_luc AS date) ngay"
                        + " FROM hoa_don h JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien"
                        + " JOIN tai_khoan tk ON tk.ten_dang_nhap=s.ten_dang_nhap JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien=tk.ma_nhan_vien"
                        + " CROSS APPLY STRING_SPLIT(CASE WHEN h.chi_tiet_thanh_toan IS NOT NULL AND CHARINDEX(':', h.chi_tiet_thanh_toan) > 0 THEN h.chi_tiet_thanh_toan ELSE h.hinh_thuc_thanh_toan + ':' + CAST(h.tong_tien AS varchar(30)) END, ';') sp"
                        + " WHERE h.trang_thai='DA_THANH_TOAN' AND CHARINDEX(':', sp.value) > 0"
                        + loc
                        + " UNION ALL SELECT "
                        + nhomDoiTra
                        + ",p.chenh_lech,NULL,CAST(p.tao_luc AS date) FROM phieu_doi_tra p JOIN"
                        + " hoa_don h ON h.ma_hoa_don=p.ma_hoa_don"
                        + " JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien JOIN tai_khoan tk ON"
                        + " tk.ten_dang_nhap=s.ten_dang_nhap JOIN nhan_vien n ON n.ma_nhan_vien=tk.ma_nhan_vien WHERE"
                        + " p.trang_thai='HOAN_TAT' AND p.loai_phieu='TRA_HANG_CU'"
                        + loc
                        + " UNION ALL SELECT "
                        + nhomCoc
                        + ",k.so_tien,NULL,CAST(k.tao_luc AS date) FROM lich_su_thu_coc_hotel k"
                        + " LEFT JOIN phien_lam_viec s ON s.ma_phien=k.ma_phien"
                        + " LEFT JOIN tai_khoan tk ON tk.ten_dang_nhap=k.ten_dang_nhap_thu"
                        + " LEFT JOIN nhan_vien n ON n.ma_nhan_vien=tk.ma_nhan_vien WHERE 1=1"
                        + loc
                        + ") SELECT nhan,SUM(so_tien),COUNT(DISTINCT hoa_don) FROM ledger WHERE"
                        + " ngay>=? AND ngay<? GROUP BY nhan ORDER BY SUM(so_tien) DESC",
                tu,
                denNgay);
    }

    public List<Object[]> laySanPhamBanChay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(10) p.ma_san_pham,p.ten,SUM(i.so_luong)-SUM(COALESCE(r.qty,0))"
                    + " qty,SUM(CASE WHEN h.tam_tinh>0 THEN"
                    + " i.so_luong*i.don_gia*(h.tong_tien+COALESCE(ex.credit,0))/h.tam_tinh ELSE 0"
                    + " END)-SUM(COALESCE(r.refund,0)) amount FROM chi_tiet_hoa_don i JOIN"
                    + " san_pham p ON p.ma_san_pham=i.ma_san_pham JOIN hoa_don h ON"
                    + " h.ma_hoa_don=i.ma_hoa_don JOIN"
                    + " phien_lam_viec s ON s.ma_phien=h.ma_phien OUTER APPLY(SELECT"
                    + " SUM(c.so_luong) qty,SUM(c.tien_hoan) refund FROM chi_tiet_doi_tra c JOIN"
                    + " phieu_doi_tra x ON x.ma_phieu=c.ma_phieu WHERE"
                    + " c.ma_hoa_don=i.ma_hoa_don AND c.ma_san_pham=i.ma_san_pham AND x.trang_thai='HOAN_TAT') r OUTER"
                    + " APPLY(SELECT SUM(dx.gia_tri_duoc_doi) credit FROM phieu_doi_tra dx WHERE"
                    + " dx.ma_hoa_don_thay_the=h.ma_hoa_don AND dx.loai_phieu='DOI_HANG' AND"
                    + " dx.trang_thai='HOAN_TAT') ex WHERE h.trang_thai='DA_THANH_TOAN' AND"
                    + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                        + dieuKienTaiKhoan(taiKhoan)
                        + " GROUP BY p.ma_san_pham,p.ten ORDER BY qty DESC,p.ma_san_pham",
                tu,
                denNgay);
    }

    public List<Object[]> layDuLieuSpa(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT g.ma_goi,g.ten_goi,SUM(t.so_luong),SUM(CASE WHEN h.tam_tinh>0 THEN"
                    + " t.tong_tien*h.tong_tien/h.tam_tinh ELSE t.tong_tien END) FROM"
                    + " phieu_tiep_nhan_spa t JOIN goi_dich_vu_spa g ON g.ma_goi=t.ma_goi JOIN hoa_don"
                    + " h ON h.ma_hoa_don=t.ma_hoa_don JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien"
                    + " WHERE h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND t.trang_thai='DA_THANH_TOAN'"
                        + dieuKienTaiKhoan(taiKhoan)
                        + " GROUP BY g.ma_goi,g.ten_goi ORDER BY SUM(t.so_luong) DESC",
                tu,
                denNgay);
    }

    public List<Object[]> layLichHen(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT trang_thai,COUNT(*) FROM lich_hen WHERE bat_dau>=? AND bat_dau<?"
                        + (nhanVien == null ? "" : " AND (ma_nhan_vien = CAST(" + nhanVien + " AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = " + nhanVien + ")")
                        + " GROUP BY"
                        + " trang_thai ORDER BY trang_thai",
                tu,
                denNgay);
    }

    public List<Object[]> layDuLieuHotel(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT p.loai_phong,COUNT(*),SUM(CASE WHEN"
                    + " DATEDIFF(day,h.ngay_vao,h.ngay_ra_thuc_te)<1 THEN 1 ELSE"
                    + " DATEDIFF(day,h.ngay_vao,h.ngay_ra_thuc_te) END),SUM(d.tong_tien+CASE WHEN"
                    + " h.da_nhan_coc=1 THEN h.tien_coc ELSE 0 END) FROM hop_dong_luu_tru h JOIN"
                    + " phong_hotel p ON p.ma_phong=h.ma_phong JOIN hoa_don d ON d.ma_hoa_don=h.ma_hoa_don JOIN"
                    + " phien_lam_viec s ON s.ma_phien=d.ma_phien WHERE d.trang_thai='DA_THANH_TOAN' AND"
                    + " d.thanh_toan_luc>=? AND d.thanh_toan_luc<?"
                        + dieuKienTaiKhoan(taiKhoan)
                        + " GROUP BY p.loai_phong ORDER BY p.loai_phong",
                tu,
                denNgay);
    }

    public List<Object[]> layDoanhThuHoanTat(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT CAST(h.thanh_toan_luc AS date),"
                    + " COUNT(DISTINCT h.ma_hoa_don),"
                    + " SUM(h.tam_tinh + COALESCE(c.coc, 0)),"
                    + " SUM(h.giam_gia),"
                    + " SUM(COALESCE(dt.hoan_tra, 0)),"
                    + " SUM(h.tong_tien + COALESCE(c.coc, 0) - COALESCE(dt.hoan_tra, 0)),"
                    + " SUM(h.tong_tien + COALESCE(c.coc, 0) - COALESCE(dt.hoan_tra, 0) - COALESCE(gv.gia_von, 0))"
                    + " FROM hoa_don h"
                    + " OUTER APPLY (SELECT SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE ma_hoa_don=h.ma_hoa_don AND da_nhan_coc=1) c"
                    + " OUTER APPLY (SELECT SUM(CASE WHEN p.chenh_lech < 0 THEN -p.chenh_lech ELSE 0 END) hoan_tra FROM phieu_doi_tra p WHERE p.ma_hoa_don=h.ma_hoa_don AND p.trang_thai='HOAN_TAT') dt"
                    + " OUTER APPLY (SELECT SUM(ct.so_luong * COALESCE(NULLIF(l.gia_nhap, 0), sp.gia_nhap_gan_nhat, ct.don_gia * 0.6)) gia_von FROM chi_tiet_hoa_don ct JOIN san_pham sp ON sp.ma_san_pham=ct.ma_san_pham LEFT JOIN lo_hang l ON l.ma_lo=ct.ma_lo WHERE ct.ma_hoa_don=h.ma_hoa_don) gv"
                    + " JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien"
                    + " WHERE h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                    + " AND (? IS NULL OR s.ten_dang_nhap IN (SELECT tk2.ten_dang_nhap FROM tai_khoan tk2 WHERE tk2.ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(tk2.ma_nhan_vien, PATINDEX('%[0-9]%', tk2.ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)))"
                    + " GROUP BY CAST(h.thanh_toan_luc AS date) ORDER BY 1",
                thamSo[0], thamSo[1], thamSo[2], thamSo[2], thamSo[2]);
    }

    public List<Object[]> layBienDongKho(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "WITH dong AS (SELECT p.ma_san_pham,p.ten,N'Nhập kho' loai,c.so_luong FROM"
                    + " chi_tiet_phieu_nhap c JOIN phieu_nhap_kho n ON n.ma_phieu_nhap=c.ma_phieu_nhap JOIN"
                    + " san_pham p ON p.ma_san_pham=c.ma_san_pham WHERE n.trang_thai='DA_NHAP' AND"
                    + " n.ngay_nhap>=? AND n.ngay_nhap<? UNION ALL SELECT p.ma_san_pham,p.ten,N'Bán"
                    + " hàng',-c.so_luong FROM chi_tiet_hoa_don c JOIN san_pham p ON"
                    + " p.ma_san_pham=c.ma_san_pham JOIN hoa_don h ON h.ma_hoa_don=c.ma_hoa_don WHERE"
                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<? UNION"
                    + " ALL SELECT p.ma_san_pham,p.ten,N'Nhận hàng đổi',c.so_luong FROM"
                    + " chi_tiet_doi_tra c JOIN phieu_doi_tra n ON n.ma_phieu=c.ma_phieu JOIN"
                    + " lo_hang l ON l.ma_lo=c.ma_lo JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE"
                    + " n.trang_thai='HOAN_TAT' AND n.tao_luc>=? AND n.tao_luc<? UNION ALL SELECT"
                    + " p.ma_san_pham,p.ten,N'Xuất hủy',-c.so_luong FROM chi_tiet_phieu_xuat c JOIN"
                    + " phieu_xuat_kho n ON n.ma_phieu_xuat=c.ma_phieu_xuat JOIN lo_hang l ON"
                    + " l.ma_lo=c.ma_lo JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE"
                    + " n.trang_thai='DA_XUAT' AND n.tao_luc>=? AND n.tao_luc<?) SELECT"
                    + " ma_san_pham,ten,loai,SUM(so_luong) FROM dong GROUP BY ma_san_pham,ten,loai"
                    + " ORDER BY ma_san_pham,loai",
                thamSo);
    }

    public List<Object[]> layPhieuSpaTheoTrangThai(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT trang_thai,COUNT(*) FROM phieu_tiep_nhan_spa WHERE tao_luc>=? AND tao_luc<?"
                    + " AND (? IS NULL OR (ma_nhan_vien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))) GROUP BY trang_thai ORDER BY trang_thai",
                thamSo[0], thamSo[1], thamSo[2], thamSo[2], thamSo[2]);
    }

    public List<Object[]> layHopDongTrongKy(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.trang_thai,COUNT(*),SUM(h.gia_dem),SUM(CASE WHEN h.da_nhan_coc=1 THEN"
                    + " h.tien_coc ELSE 0 END) FROM hop_dong_luu_tru h WHERE h.ngay_vao>=? AND"
                    + " h.ngay_vao<? AND (? IS NULL OR EXISTS(SELECT 1 FROM phien_lam_viec s WHERE"
                    + " s.ma_phien=h.ma_phien_nhan_coc AND s.ten_dang_nhap IN (SELECT tk2.ten_dang_nhap FROM tai_khoan tk2 WHERE tk2.ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(tk2.ma_nhan_vien, PATINDEX('%[0-9]%', tk2.ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)))) GROUP BY h.trang_thai",
                thamSo[0], thamSo[1], thamSo[2], thamSo[2], thamSo[2]);
    }

    public List<Object[]> layCocTrongKy(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "WITH lan_thu AS ("
                        + " SELECT l.ma_hop_dong,l.khoa_thu_coc,"
                        + " MIN(l.tao_luc) ngay_nhan,SUM(l.so_tien) so_tien"
                        + " FROM lich_su_thu_coc_hotel l"
                        + " LEFT JOIN phien_lam_viec s ON s.ma_phien=l.ma_phien"
                        + " WHERE l.tao_luc>=? AND l.tao_luc<?"
                        + " AND (? IS NULL OR s.ten_dang_nhap IN (SELECT tk2.ten_dang_nhap FROM tai_khoan tk2 WHERE tk2.ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(tk2.ma_nhan_vien, PATINDEX('%[0-9]%', tk2.ma_nhan_vien), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)))"
                        + " GROUP BY l.ma_hop_dong,l.khoa_thu_coc)"
                        + " SELECT CAST(ngay_nhan AS date),SUM(so_tien),COUNT(*)"
                        + " FROM lan_thu GROUP BY CAST(ngay_nhan AS date) ORDER BY 1",
                thamSo[0], thamSo[1], thamSo[2], thamSo[2], thamSo[2]);
    }

    public List<Object[]> layLuotKhach(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "WITH khach AS (SELECT k.ma_khach_hang,CASE WHEN EXISTS(SELECT 1 FROM hoa_don cu"
                    + " WHERE cu.ma_khach_hang=k.ma_khach_hang AND"
                    + " cu.trang_thai='DA_THANH_TOAN' AND cu.thanh_toan_luc<?) THEN N'Khách quay lại' ELSE"
                    + " N'Khách mua lần đầu' END nhom FROM khach_hang k) SELECT"
                    + " k.nhom,COUNT(DISTINCT k.ma_khach_hang),COUNT(h.ma_hoa_don),SUM(h.tong_tien) FROM khach k JOIN"
                    + " hoa_don h ON h.ma_khach_hang=k.ma_khach_hang"
                    + " WHERE h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                    + " GROUP BY k.nhom",
                thamSo);
    }

    public List<Object[]> laySuDungVoucher(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT v.ma_voucher,k.ho_ten,COUNT(h.ma_hoa_don),SUM(h.giam_gia) FROM voucher v JOIN"
                    + " khach_hang k ON k.ma_khach_hang=v.ma_khach_hang JOIN hoa_don h ON"
                    + " h.ma_voucher=v.ma_voucher WHERE h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=?"
                    + " AND h.thanh_toan_luc<? GROUP BY v.ma_voucher,k.ho_ten ORDER BY"
                    + " v.ma_voucher",
                thamSo);
    }

    public List<Object[]> layTongHopCa(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ho_ten,s.ten_ca,s.mo_luc,s.dong_luc,s.tien_dau_ca,COALESCE(h.tien,0),COALESCE(c.coc,0),s.tien_thuc_te,s.chenh_lech"
                    + " FROM phien_lam_viec s JOIN tai_khoan t ON t.ten_dang_nhap=s.ten_dang_nhap JOIN"
                    + " nhan_vien n ON n.ma_nhan_vien=t.ma_nhan_vien OUTER APPLY(SELECT SUM(tong_tien) tien"
                    + " FROM hoa_don h WHERE h.ma_phien=s.ma_phien AND h.trang_thai='DA_THANH_TOAN')h"
                    + " OUTER APPLY(SELECT SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE"
                    + " ma_phien_nhan_coc=s.ma_phien AND da_nhan_coc=1)c WHERE s.mo_luc>=? AND s.mo_luc<?"
                    + " ORDER BY s.mo_luc DESC",
                thamSo);
    }

    public List<Object[]> layDuNoHienTai(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ma_nha_cung_cap,n.ten,"
                    + " COALESCE(SUM(p.tong_tien),0) AS tong_nhap,"
                    + " COALESCE(SUM(p.da_tra),0) AS da_tra,"
                    + " COALESCE(SUM(p.tong_tien-p.da_tra),0) AS du_no"
                    + " FROM nha_cung_cap n"
                    + " LEFT JOIN phieu_nhap_kho p ON p.ma_nha_cung_cap=n.ma_nha_cung_cap"
                    + " AND p.trang_thai='DA_NHAP'"
                    + " GROUP BY n.ma_nha_cung_cap,n.ten"
                    + " ORDER BY du_no DESC,n.ma_nha_cung_cap",
                thamSo);
    }

    public List<Object[]> layDoanhThuTheoGio(java.time.LocalDate tu, java.time.LocalDate denNgay) {
        return ThaoTacSQL.truyVan(
                "SELECT CAST(DATEPART(hour, thanh_toan_luc) AS varchar)+'h:00 -"
                        + " '+CAST(DATEPART(hour, thanh_toan_luc)+1 AS varchar)+'h:00' gio,"
                        + " SUM(tong_tien) doanh_thu, COUNT(ma_hoa_don) so_hoa_don FROM hoa_don WHERE"
                        + " trang_thai='DA_THANH_TOAN' AND thanh_toan_luc>=? AND thanh_toan_luc<? GROUP BY"
                        + " DATEPART(hour, thanh_toan_luc) ORDER BY DATEPART(hour, thanh_toan_luc)",
                tu,
                denNgay);
    }

    public List<Object[]> layDoanhThuTheoThu(java.time.LocalDate tu, java.time.LocalDate denNgay) {
        return ThaoTacSQL.truyVan(
                "SELECT CASE DATEPART(weekday, thanh_toan_luc) WHEN 1 THEN N'Chủ nhật' WHEN 2 THEN"
                        + " N'Thứ 2' WHEN 3 THEN N'Thứ 3' WHEN 4 THEN N'Thứ 4' WHEN 5 THEN N'Thứ 5'"
                        + " WHEN 6 THEN N'Thứ 6' ELSE N'Thứ 7' END thu, SUM(tong_tien) doanh_thu,"
                        + " COUNT(ma_hoa_don) so_hoa_don FROM hoa_don WHERE trang_thai='DA_THANH_TOAN' AND"
                        + " thanh_toan_luc>=? AND thanh_toan_luc<? GROUP BY DATEPART(weekday,"
                        + " thanh_toan_luc) ORDER BY DATEPART(weekday, thanh_toan_luc)",
                tu,
                denNgay);
    }

    public List<Object[]> layHangTonDong() {
        int soNgay = 45;
        try {
            soNgay =
                    Math.max(
                            1,
                            Integer.parseInt(
                                    String.valueOf(java.util.Objects.requireNonNullElse(new CaiDatDAOImpl().timGiaTri("SO_NGAY_TON_DONG"), "45"))
                                            .trim()));
        } catch (NumberFormatException ignored) {
        }
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham, p.ten, COALESCE(SUM(l.so_luong_ton), 0) ton,"
                    + " COALESCE(p.gia_nhap_gan_nhat, 0) gia_nhap, COALESCE(SUM(l.so_luong_ton), 0)"
                    + " * COALESCE(p.gia_nhap_gan_nhat, 0) von_chon FROM san_pham p JOIN lo_hang l"
                    + " ON l.ma_san_pham = p.ma_san_pham WHERE l.so_luong_ton > 0 AND NOT EXISTS (SELECT 1"
                    + " FROM chi_tiet_hoa_don ct JOIN hoa_don hd ON hd.ma_hoa_don = ct.ma_hoa_don WHERE"
                    + " ct.ma_san_pham = p.ma_san_pham AND hd.trang_thai = 'DA_THANH_TOAN' AND hd.thanh_toan_luc >="
                    + " DATEADD(day, -?, GETDATE())) GROUP BY p.ma_san_pham, p.ten,"
                    + " p.gia_nhap_gan_nhat ORDER BY von_chon DESC",
                soNgay);
    }

    public List<Object[]> layHaoHutKiemKe(java.time.LocalDate tu, java.time.LocalDate denNgay) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham, p.ten, l.so_lo, (ct.ton_thuc_te - ct.ton_he_thong)"
                    + " chenh_lech, (ct.ton_thuc_te - ct.ton_he_thong) *"
                    + " COALESCE(p.gia_nhap_gan_nhat, 0) gia_tri_lech, COALESCE(ct.nguyen_nhan,"
                    + " N'Chưa rõ') ly_do, CONVERT(varchar(10), bb.tao_luc, 103) ngay_tao FROM"
                    + " chi_tiet_kiem_ke ct JOIN bien_ban_kiem_ke bb ON bb.ma_bien_ban = ct.ma_bien_ban JOIN"
                    + " lo_hang l ON l.ma_lo = ct.ma_lo JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham"
                    + " WHERE ct.ton_thuc_te <> ct.ton_he_thong AND bb.tao_luc>=? AND bb.tao_luc<?"
                    + " ORDER BY bb.tao_luc DESC",
                tu,
                denNgay);
    }
}
