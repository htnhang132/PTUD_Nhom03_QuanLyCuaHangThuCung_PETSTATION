package com.petstation.dao.impl;

import com.petstation.dao.HoaDonDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC bán hàng DAO. */
public final class HoaDonDAOImpl implements HoaDonDAO {
    public List<Object[]> layNguonBienLai(String soHoaDon) {
        return ThaoTacSQL.truyVan(
                "SELECT CASE WHEN x.id IS NOT NULL THEN 'EXCHANGE' WHEN ht.id IS NOT NULL THEN"
                    + " 'HOTEL' WHEN spa.id IS NOT NULL THEN 'SPA' ELSE ''"
                    + " END,COALESCE(x.ma_phieu,ht.ma_hop_dong,spa.ma_phieu,''),COALESCE(x.gia_tri_duoc_doi,CASE"
                    + " WHEN ht.da_nhan_coc=1 THEN ht.tien_coc ELSE 0"
                    + " END,0),COALESCE(goc.so_hoa_don,'') FROM hoa_don h LEFT JOIN phieu_doi_tra x"
                    + " ON x.hoa_don_thay_the_id=h.id AND x.loai_phieu='EXCHANGE' AND"
                    + " x.trang_thai='COMPLETED' LEFT JOIN hoa_don goc ON goc.id=x.hoa_don_id LEFT"
                    + " JOIN hop_dong_luu_tru ht ON ht.hoa_don_id=h.id LEFT JOIN"
                    + " phieu_tiep_nhan_spa spa ON spa.hoa_don_id=h.id WHERE h.so_hoa_don=?",
                soHoaDon);
    }

    public HoaDonDAOImpl() {}

    public Object timHoaDonTheoKhoaThu(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT so_hoa_don FROM hoa_don WHERE idempotency_key=?", cacThamSo);
    }

    public int capNhatThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-?+? WHERE id=?", cacThamSo);
    }

    public int capNhatCapNhatHang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE khach_hang SET hang_thanh_vien=? WHERE id=?", cacThamSo);
    }

    public long demMoLai(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM chi_tiet_hoa_don WHERE hoa_don_id=?", cacThamSo);
    }

    public int capNhatXoaTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("DELETE FROM chi_tiet_hoa_don WHERE hoa_don_id=?", cacThamSo);
    }

    public int xoaGioHangTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM hoa_don WHERE id=? AND trang_thai='GIO_TREO'", cacThamSo[0]);
    }

    public Object layQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT khach_hang_id FROM hop_dong_luu_tru WHERE id=?", cacThamSo);
    }

    public List<Object[]> timQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_khach_hang,ho_ten,diem_tich_luy FROM khach_hang WHERE id=?", cacThamSo);
    }

    public long demHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE hoa_don_id=?", cacThamSo);
    }

    public long demHopDongGanHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WHERE hoa_don_id=?", cacThamSo);
    }

    public int capNhatHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", cacThamSo);
    }

    public int hoanDiemKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE khach_hang SET diem_tich_luy=? WHERE id=?", cacThamSo);
    }

    public int danhDauHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET trang_thai='CANCELLED' WHERE id=?", cacThamSo);
    }

    public int danhDauHuyDonHang(Object... cacThamSo) {
        return 1;
    }

    public long demThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " tai_khoan_id=? AND trang_thai='OPEN'",
                cacThamSo);
    }

    public long demNgayDaChot(Object... cacThamSo) {
        return 0L;
    }

    public List<Object[]> timThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT diem_tich_luy,dang_hoat_dong FROM khach_hang"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                cacThamSo);
    }

    public int ghiNhanSuDungVoucher(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE voucher WITH(UPDLOCK,HOLDLOCK) SET"
                        + " da_dung=da_dung+1,su_dung_luc=SYSDATETIME() WHERE"
                        + " ma_voucher=? AND trang_thai='ACTIVE' AND"
                        + " da_dung<so_luong AND hieu_luc_den>SYSDATETIME() AND"
                        + " don_toi_thieu<=? AND (khach_hang_id IS NULL OR"
                        + " khach_hang_id=?) AND (chi_ap_dung_spa=0 OR"
                        + " ?='SPA')",
                cacThamSo);
    }

    public long themThanhToan(Object... cacThamSo) {
        return 0L;
    }

    public List<Object[]> khoaSanPhamDeBan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,gia_ban,trang_thai FROM san_pham"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?",
                cacThamSo);
    }

    public long themDongDonHang(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " chi_tiet_hoa_don(hoa_don_id,san_pham_id,ma_dich_vu,ten_hang,so_luong,don_gia)"
                    + " VALUES(?,?,?,?,?,?)",
                cacThamSo);
    }

    public List<Object[]> layLoTheoFEFO(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,so_luong_ton FROM lo_hang"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE san_pham_id=? AND"
                        + " trang_thai='AVAILABLE' AND so_luong_ton>0 AND"
                        + " (han_su_dung IS NULL OR"
                        + " han_su_dung>=CAST(GETDATE() AS DATE)) ORDER BY"
                        + " CASE WHEN han_su_dung IS NULL THEN 1 ELSE 0"
                        + " END,han_su_dung,id",
                cacThamSo);
    }

    public int truTonLo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-?"
                        + " WHERE id=? AND so_luong_ton>=?",
                cacThamSo);
    }

    public int ghiPhanBoLo(Object... cacThamSo) {
        long chiTietId = ((Number) cacThamSo[0]).longValue();
        Object loHangId = cacThamSo[1];
        int soLuong = ((Number) cacThamSo[2]).intValue();
        Object hienCo =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT lo_hang_id FROM chi_tiet_hoa_don WHERE id=?", chiTietId);
        if (hienCo == null) {
            return ThaoTacSQL.capNhat(
                    "UPDATE chi_tiet_hoa_don SET lo_hang_id=?, so_luong=? WHERE id=?",
                    loHangId,
                    soLuong,
                    chiTietId);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_hoa_don(hoa_don_id,san_pham_id,lo_hang_id,ma_dich_vu,ten_hang,so_luong,don_gia)"
                    + " SELECT hoa_don_id,san_pham_id,?,ma_dich_vu,ten_hang,?,don_gia FROM"
                    + " chi_tiet_hoa_don WHERE id=?",
                loHangId,
                soLuong,
                chiTietId);
    }

    public long themHoaDon(Object... cacThamSo) {
        // cacThamSo:
        // 0:so_hoa_don, 1:phien_lam_viec_id, 2:tong_tien, 3:hinh_thuc_thanh_toan,
        // 4:tien_khach_dua, 5:tien_thoi, 6:diem_cong, 7:idempotency_key,
        // 8:thanh_toan_luc, 9:tam_tinh, 10:giam_gia, 11:diem_dung, 12:ma_voucher
        // (and optional 13:khach_hang_id, 14:tai_khoan_id)
        Object khachHangId = cacThamSo.length > 13 ? cacThamSo[13] : null;
        Object taiKhoanId = cacThamSo.length > 14 ? cacThamSo[14] : null;
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " hoa_don(so_hoa_don,phien_lam_viec_id,tai_khoan_id,khach_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,idempotency_key,tao_luc,thanh_toan_luc,tam_tinh,giam_gia,diem_dung,ma_voucher,trang_thai)"
                    + " VALUES(?,?,COALESCE(?,(SELECT tai_khoan_id FROM phien_lam_viec WHERE"
                    + " id=?)),?,?,?,?,?,?,?,?,?,?,?,?,?,'PAID')",
                cacThamSo[0],
                cacThamSo[1],
                taiKhoanId,
                cacThamSo[1],
                khachHangId,
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[4],
                cacThamSo[5],
                cacThamSo[6],
                cacThamSo[7],
                cacThamSo[8],
                cacThamSo[8],
                cacThamSo[9],
                cacThamSo[10],
                cacThamSo[11],
                cacThamSo[12]);
    }

    public int ghiDiemThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET lich_su_diem = CASE WHEN lich_su_diem IS NULL OR"
                    + " lich_su_diem=N'' THEN N'' ELSE lich_su_diem + N';' END +"
                    + " CONVERT(NVARCHAR(19), SYSDATETIME(), 120) + N'|' + CAST(? AS NVARCHAR(20))"
                    + " + N'|' + CAST(? AS NVARCHAR(20)) + N'|' + CAST(diem_tich_luy AS"
                    + " NVARCHAR(20)) + N'|Thanh toán hóa đơn' WHERE id=?",
                cacThamSo);
    }

    public long layTienSpaChoThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT tong_tien FROM phieu_tiep_nhan_spa"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='COMPLETED' AND hoa_don_id IS NULL",
                cacThamSo);
    }

    public int ganHoaDonSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET"
                        + " hoa_don_id=?,trang_thai='PAID' WHERE id=? AND"
                        + " hoa_don_id IS NULL",
                cacThamSo);
    }

    public int ganHoaDonHotel(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET"
                        + " hoa_don_id=?,trang_thai='CHECKED_OUT',ngay_ra_thuc_te=SYSDATETIME()"
                        + " WHERE id=? AND trang_thai='CHECKED_IN' AND"
                        + " hoa_don_id IS NULL",
                cacThamSo);
    }

    public int chuyenPhongChoDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='CLEANING' WHERE id=(SELECT"
                        + " phong_id FROM hop_dong_luu_tru WHERE id=?)",
                cacThamSo);
    }

    public long demCapNhatHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h WHERE h.khach_hang_id=? AND"
                        + " h.trang_thai='PAID'",
                cacThamSo);
    }

    public List<Object[]> timBienLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " h.thanh_toan_luc,h.id,h.tam_tinh,h.giam_gia,h.tong_tien,h.hinh_thuc_thanh_toan,h.tien_khach_dua,h.tien_thoi,COALESCE(k.ho_ten,N'Khách"
                    + " lẻ'),COALESCE(h.ma_voucher,''),COALESCE(n.ho_ten,N'Thu ngân') FROM hoa_don"
                    + " h LEFT JOIN phien_lam_viec p ON p.id=h.phien_lam_viec_id LEFT JOIN"
                    + " tai_khoan t ON t.id=COALESCE(h.tai_khoan_id,p.tai_khoan_id) LEFT JOIN"
                    + " nhan_vien n ON n.id=t.nhan_vien_id LEFT JOIN khach_hang k ON"
                    + " k.id=h.khach_hang_id WHERE h.so_hoa_don=?",
                cacThamSo);
    }

    public List<Object[]> layDongBienLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " COALESCE(p.ma_san_pham,c.ma_dich_vu),COALESCE(c.ten_hang,p.ten),c.don_gia,SUM(c.so_luong)"
                    + " FROM chi_tiet_hoa_don c LEFT JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                    + " c.hoa_don_id=? GROUP BY"
                    + " COALESCE(p.ma_san_pham,c.ma_dich_vu),COALESCE(c.ten_hang,p.ten),c.don_gia"
                    + " ORDER BY MIN(c.id)",
                cacThamSo);
    }

    public long themTreo(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " hoa_don(so_hoa_don,phien_lam_viec_id,tai_khoan_id,khach_hang_id,tam_tinh,giam_gia,tong_tien,hinh_thuc_thanh_toan,trang_thai,tao_luc,thanh_toan_luc)"
                    + " VALUES(?,(SELECT TOP 1 id FROM phien_lam_viec WHERE tai_khoan_id=? AND"
                    + " trang_thai='OPEN'),?,?,0,0,0,'TIEN_MAT','GIO_TREO',SYSDATETIME(),SYSDATETIME())",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    public int capNhatTreo(Object... cacThamSo) {
        int r =
                ThaoTacSQL.capNhat(
                        "INSERT INTO"
                            + " chi_tiet_hoa_don(hoa_don_id,san_pham_id,ma_dich_vu,ten_hang,don_gia,so_luong)"
                            + " VALUES(?,(SELECT id FROM san_pham WHERE ma_san_pham=?),?,?,?,?)",
                        cacThamSo[0],
                        cacThamSo[1],
                        cacThamSo[1],
                        cacThamSo[2],
                        cacThamSo[3],
                        cacThamSo[4]);
        ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET tam_tinh=COALESCE((SELECT SUM(thanh_tien) FROM"
                    + " chi_tiet_hoa_don WHERE hoa_don_id=?),0), tong_tien=COALESCE((SELECT"
                    + " SUM(thanh_tien) FROM chi_tiet_hoa_don WHERE hoa_don_id=?),0) WHERE id=?",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[0]);
        return r;
    }

    public long demGioTreoCuaTaiKhoan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='GIO_TREO'",
                cacThamSo[0]);
    }

    public List<Object[]> timMoLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham,p.ten,p.gia_ban,c.so_luong FROM"
                        + " chi_tiet_hoa_don c JOIN san_pham p ON"
                        + " p.id=c.san_pham_id WHERE c.hoa_don_id=? AND"
                        + " p.trang_thai='SELLING'",
                cacThamSo);
    }

    public List<Object[]> layKhachCuaGioTreo(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT k.ma_khach_hang,k.ho_ten,k.diem_tich_luy FROM"
                        + " hoa_don g JOIN khach_hang k ON"
                        + " k.id=g.khach_hang_id WHERE g.id=? AND g.trang_thai='GIO_TREO'",
                cacThamSo);
    }

    public long demXoaTreo(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='GIO_TREO'",
                cacThamSo[0]);
    }

    public long demQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE id=? AND"
                        + " trang_thai='COMPLETED' AND hoa_don_id IS NULL",
                cacThamSo);
    }

    public List<Object[]> layDichVuSpaQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(g.ma_goi,'DV-SPA'), COALESCE(t.mo_ta_dich_vu,g.ten_goi,N'Dịch vụ"
                    + " Spa'), COALESCE(NULLIF(t.don_gia,0),t.tong_tien),"
                    + " COALESCE(NULLIF(t.so_luong,0),1) FROM phieu_tiep_nhan_spa t LEFT JOIN"
                    + " goi_dich_vu_spa g ON g.id=t.goi_id WHERE t.id=?",
                cacThamSo);
    }

    public Object layKhachCuaPhieuSpa(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT p.khach_hang_id FROM phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                        + " p.id=t.thu_cung_id WHERE t.id=?",
                cacThamSo);
    }

    public List<Object[]> timHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.id,h.diem_cong,h.diem_dung,h.ma_voucher,h.khach_hang_id"
                        + " FROM hoa_don h WITH(UPDLOCK,HOLDLOCK) WHERE h.id=? AND"
                        + " h.trang_thai='PAID'",
                cacThamSo);
    }

    public long demPhieuDoiTraGanHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_doi_tra WHERE hoa_don_id=? AND trang_thai IN"
                        + " ('COMPLETED','PENDING')",
                cacThamSo);
    }

    public List<Object[]> layPhanBoCanHoanTon(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT lo_hang_id,so_luong FROM chi_tiet_hoa_don WHERE hoa_don_id=? AND"
                        + " lo_hang_id IS NOT NULL",
                cacThamSo);
    }

    public long khoaDiemKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                cacThamSo);
    }

    public int ghiDiemHuyHoaDon(Object... cacThamSo) {
        // cacThamSo: 0:khach_hang_id, 1:hoa_don_id, 2:thay_doi, 3:so_du_sau
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET lich_su_diem = CASE WHEN lich_su_diem IS NULL OR"
                    + " lich_su_diem=N'' THEN N'' ELSE lich_su_diem + N';' END +"
                    + " CONVERT(NVARCHAR(19), SYSDATETIME(), 120) + N'|' + CAST(? AS NVARCHAR(20))"
                    + " + N'|' + CAST(? AS NVARCHAR(20)) + N'|' + CAST(? AS NVARCHAR(20)) + N'|Hủy"
                    + " hóa đơn' WHERE id=?",
                cacThamSo[1],
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[0]);
    }

    public int hoanLuotVoucher(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE voucher SET da_dung=CASE WHEN da_dung>0 THEN da_dung-1 ELSE 0 END WHERE"
                        + " ma_voucher=?",
                cacThamSo);
    }

    public List<Object[]> layCoTheBanCacSanPham(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,COALESCE(SUM(l.so_luong_ton),0),COALESCE(p.anh,'')"
                    + " FROM san_pham p LEFT JOIN lo_hang l ON l.san_pham_id=p.id AND"
                    + " l.trang_thai='AVAILABLE' AND (l.han_su_dung IS NULL OR"
                    + " l.han_su_dung>=CAST(GETDATE() AS date)) WHERE p.trang_thai='SELLING' GROUP"
                    + " BY p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,p.anh ORDER BY CASE WHEN"
                    + " NULLIF(p.anh,'') IS NULL THEN 1 ELSE 0 END,p.ma_san_pham",
                cacThamSo);
    }

    public List<Object[]> layThongTinIn(String soHoaDon) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(k.so_dien_thoai,''),h.diem_cong,k.diem_tich_luy FROM hoa_don h"
                        + " LEFT JOIN khach_hang k ON k.id=h.khach_hang_id WHERE h.so_hoa_don=?",
                soHoaDon);
    }
}
