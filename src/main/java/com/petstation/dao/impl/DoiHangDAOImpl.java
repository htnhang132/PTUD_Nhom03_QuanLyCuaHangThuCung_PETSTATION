package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.DoiHangDAO;

import java.util.List;

/** Đổi hàng mới được phân biệt với các phiếu hoàn tiền lịch sử. */
public final class DoiHangDAOImpl implements DoiHangDAO {
    public DoiHangDAOImpl() {}

    @Override
    public List<Object[]> timHoaDon(String tuKhoa) {
        return truyVan(
            "SELECT h.id,h.so_hoa_don,"
                + "COALESCE(k.ho_ten,N'Khách lẻ'),"
                + "h.tao_luc,h.tong_tien"
                + " FROM hoa_don h"
                + " LEFT JOIN khach_hang k ON k.id=h.khach_hang_id"
                + " WHERE h.trang_thai='PAID'"
                + " AND (h.so_hoa_don=? OR k.so_dien_thoai=?)"
                + " ORDER BY h.tao_luc DESC,h.id DESC",
            tuKhoa, tuKhoa);
    }

    @Override
    public List<Object[]> layDongHoaDon(long hoaDon) {
        return truyVan(
            "SELECT i.id,p.ten,p.nhom_hang,i.so_luong,"
                + "COALESCE(d.da_doi,0),i.don_gia"
                + " FROM chi_tiet_hoa_don i"
                + " JOIN san_pham p ON p.id=i.san_pham_id"
                + " OUTER APPLY ("
                + " SELECT SUM(c.so_luong) da_doi"
                + " FROM chi_tiet_doi_tra c"
                + " JOIN phieu_doi_tra f ON f.id=c.phieu_doi_tra_id"
                + " WHERE c.chi_tiet_hoa_don_id=i.id"
                + " AND f.trang_thai IN ('PENDING','COMPLETED')"
                + " ) d"
                + " WHERE i.hoa_don_id=? ORDER BY i.id",
            hoaDon);
    }

    @Override
    public List<Object[]> timPhuKien(String tuKhoa) {
        return truyVan(
            "SELECT p.ma_san_pham,p.ten,p.gia_ban,"
                + "COALESCE(SUM(l.so_luong_ton),0)"
                + " FROM san_pham p"
                + " LEFT JOIN lo_hang l ON l.san_pham_id=p.id"
                + " AND l.trang_thai='AVAILABLE'"
                + " AND (l.han_su_dung IS NULL"
                + " OR l.han_su_dung>=CAST(GETDATE() AS date))"
                + " WHERE p.trang_thai='SELLING'"
                + " AND p.nhom_hang=N'Phụ kiện'"
                + " AND (p.ma_san_pham=? OR p.ten LIKE ?)"
                + " GROUP BY p.ma_san_pham,p.ten,p.gia_ban"
                + " ORDER BY p.ten",
            tuKhoa, "%" + tuKhoa + "%");
    }

    public List<Object[]> timDongGoc(long id) {
        return truyVan(
                "SELECT"
                    + " h.id,h.tong_tien,h.tam_tinh,h.thanh_toan_luc,i.don_gia,i.so_luong,h.trang_thai,p.nhom_hang,COALESCE(k.ma_khach_hang,''),COALESCE(k.ho_ten,''),COALESCE(k.diem_tich_luy,0)"
                    + " FROM chi_tiet_hoa_don i WITH(UPDLOCK,HOLDLOCK) JOIN hoa_don h"
                    + " WITH(UPDLOCK,HOLDLOCK) ON h.id=i.hoa_don_id JOIN san_pham p ON"
                    + " p.id=i.san_pham_id LEFT JOIN khach_hang k ON k.id=h.khach_hang_id WHERE"
                    + " i.id=?",
                id);
    }

    public List<Object[]> timThayThe(String ma) {
        return truyVan(
                "SELECT id,ten,gia_ban,nhom_hang FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_san_pham=? AND trang_thai='SELLING'",
                ma);
    }

    public long taoPhieu(
            long hoaDon, long nguoi, long chenhLech, String lyDo, long giaTri, String khoa) {
        return them(
                "INSERT"
                    + " phieu_doi_tra(ma_phieu,hoa_don_id,nguoi_lap_id,chenh_lech,trang_thai,ly_do,loai_phieu,gia_tri_duoc_doi,khoa_thao_tac)"
                    + " VALUES(?,?,?,?,'PENDING',?,'EXCHANGE',?,?)",
                taoMa("DT"),
                hoaDon,
                nguoi,
                chenhLech,
                lyDo,
                giaTri,
                khoa);
    }

    public void hoanTat(long phieu, long hoaDon) {
        if (capNhat(
                        "UPDATE phieu_doi_tra SET trang_thai='COMPLETED',hoa_don_thay_the_id=?"
                                + " WHERE id=? AND trang_thai='PENDING' AND loai_phieu='EXCHANGE'",
                        hoaDon,
                        phieu)
                != 1) throw new IllegalStateException("Phiếu đổi hàng đã xử lý.");
    }

    public long layChenhLechTruoc(long hoaDon) {
        return laySo(
                "SELECT COALESCE(SUM(chenh_lech),0) FROM phieu_doi_tra WHERE hoa_don_id=? AND"
                        + " loai_phieu='EXCHANGE' AND trang_thai='COMPLETED'",
                hoaDon);
    }

    public boolean laHoaDonThayThe(long hoaDon) {
        return laySo(
                        "SELECT COUNT(*) FROM phieu_doi_tra WHERE hoa_don_thay_the_id=? AND"
                                + " trang_thai='COMPLETED'",
                        hoaDon)
                > 0;
    }
}
