package com.petstation.dao.impl;

import com.petstation.dao.BanHangDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC bán hàng DAO. */
public final class BanHangImpl implements BanHangDAO {
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

    public BanHangImpl() {}

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
                "SELECT COUNT(*) FROM chi_tiet_gio_hang_treo WHERE gio_id=?", cacThamSo);
    }

    public int capNhatXoaTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("DELETE FROM chi_tiet_gio_hang_treo WHERE gio_id=?", cacThamSo);
    }

    public int xoaGioHangTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM gio_hang_treo WHERE id=? AND tai_khoan_id=?", cacThamSo);
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
        return ThaoTacSQL.capNhat(
                "UPDATE don_hang SET trang_thai='CANCELLED' WHERE id=?", cacThamSo);
    }

    public long demThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " tai_khoan_id=? AND trang_thai='OPEN'",
                cacThamSo);
    }

    public long demNgayDaChot(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM chot_ngay WHERE ngay_chot=CAST(GETDATE()"
                        + " AS DATE) AND trang_thai='LOCKED'",
                cacThamSo);
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
        return ThaoTacSQL.them(
                "INSERT INTO"
                        + " don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai,tao_luc)"
                        + " VALUES(?,?,?,'PAID',?)",
                cacThamSo);
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
                    + " chi_tiet_don_hang(don_hang_id,san_pham_id,ma_dich_vu,ten_hang,so_luong,don_gia)"
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
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " phan_bo_lo_hang(chi_tiet_don_hang_id,lo_hang_id,so_luong)"
                        + " VALUES(?,?,?)",
                cacThamSo);
    }

    public long themHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " hoa_don(so_hoa_don,don_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,idempotency_key,thanh_toan_luc,tam_tinh,giam_gia,diem_dung,ma_voucher)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                cacThamSo);
    }

    public int ghiDiemThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do,tao_luc)"
                        + " SELECT id,?,?,diem_tich_luy,N'Thanh toán hóa"
                        + " đơn',SYSDATETIME() FROM khach_hang WHERE id=?",
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
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h JOIN don_hang d ON"
                        + " d.id=h.don_hang_id WHERE d.khach_hang_id=? AND h.trang_thai='PAID'",
                cacThamSo);
    }

    public List<Object[]> timBienLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " h.thanh_toan_luc,h.don_hang_id,h.tam_tinh,h.giam_gia,h.tong_tien,h.hinh_thuc_thanh_toan,h.tien_khach_dua,h.tien_thoi,COALESCE(k.ho_ten,N'Khách"
                    + " lẻ'),COALESCE(h.ma_voucher,''),n.ho_ten FROM hoa_don h JOIN don_hang d ON"
                    + " d.id=h.don_hang_id JOIN phien_lam_viec p ON p.id=d.phien_lam_viec_id JOIN"
                    + " tai_khoan t ON t.id=p.tai_khoan_id JOIN nhan_vien n ON n.id=t.nhan_vien_id"
                    + " LEFT JOIN khach_hang k ON k.id=d.khach_hang_id WHERE h.so_hoa_don=?",
                cacThamSo);
    }

    public List<Object[]> layDongBienLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " COALESCE(p.ma_san_pham,c.ma_dich_vu),COALESCE(c.ten_hang,p.ten),c.don_gia,c.so_luong"
                    + " FROM chi_tiet_don_hang c LEFT JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                    + " c.don_hang_id=? ORDER BY c.id",
                cacThamSo);
    }

    public long themTreo(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO gio_hang_treo(ma_gio,tai_khoan_id,khach_hang_id)" + " VALUES(?,?,?)",
                cacThamSo);
    }

    public int capNhatTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " chi_tiet_gio_hang_treo(gio_id,ma_san_pham,ten,don_gia,so_luong)"
                        + " VALUES(?,?,?,?,?)",
                cacThamSo);
    }

    public long demGioTreoCuaTaiKhoan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM gio_hang_treo WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND tai_khoan_id=?",
                cacThamSo);
    }

    public List<Object[]> timMoLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham,p.ten,p.gia_ban,g.so_luong FROM"
                        + " chi_tiet_gio_hang_treo g JOIN san_pham p ON"
                        + " p.ma_san_pham=g.ma_san_pham WHERE g.gio_id=? AND"
                        + " p.trang_thai='SELLING'",
                cacThamSo);
    }

    public List<Object[]> layKhachCuaGioTreo(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT k.ma_khach_hang,k.ho_ten,k.diem_tich_luy FROM"
                        + " gio_hang_treo g JOIN khach_hang k ON"
                        + " k.id=g.khach_hang_id WHERE g.id=?",
                cacThamSo);
    }

    public long demXoaTreo(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM gio_hang_treo WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND tai_khoan_id=?",
                cacThamSo);
    }

    public long demQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE id=? AND"
                        + " trang_thai='COMPLETED' AND hoa_don_id IS NULL",
                cacThamSo);
    }

    public List<Object[]> layDichVuSpaQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(g.ma_goi,'DV-PHU-PHI'),c.mo_ta,c.don_gia,c.so_luong"
                        + " FROM chi_tiet_phieu_spa c LEFT JOIN goi_dich_vu_spa g ON"
                        + " g.id=c.goi_id WHERE c.phieu_spa_id=?",
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
                "SELECT h.don_hang_id,h.diem_cong,h.diem_dung,h.ma_voucher,d.khach_hang_id"
                        + " FROM hoa_don h WITH(UPDLOCK,HOLDLOCK) JOIN don_hang d ON"
                        + " d.id=h.don_hang_id WHERE h.id=? AND h.trang_thai='PAID'",
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
                "SELECT b.lo_hang_id,b.so_luong FROM phan_bo_lo_hang b JOIN"
                        + " chi_tiet_don_hang i ON i.id=b.chi_tiet_don_hang_id WHERE"
                        + " i.don_hang_id=?",
                cacThamSo);
    }

    public long khoaDiemKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK)" + " WHERE id=?",
                cacThamSo);
    }

    public int ghiDiemHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do)"
                        + " VALUES(?,?,?,?,N'Hủy hóa đơn')",
                cacThamSo);
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
                "SELECT COALESCE(k.so_dien_thoai,''),h.diem_cong,diem.so_du_sau FROM hoa_don h JOIN"
                    + " don_hang d ON d.id=h.don_hang_id LEFT JOIN khach_hang k ON"
                    + " k.id=d.khach_hang_id OUTER APPLY(SELECT TOP(1) l.so_du_sau FROM"
                    + " lich_su_diem l WHERE l.hoa_don_id=h.id AND l.khach_hang_id=d.khach_hang_id"
                    + " AND l.thay_doi=h.diem_cong ORDER BY l.id)diem WHERE h.so_hoa_don=?",
                soHoaDon);
    }
}
