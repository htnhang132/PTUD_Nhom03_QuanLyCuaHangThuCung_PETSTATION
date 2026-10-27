package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.DoiHangDAO;

import java.util.List;

/** Đổi hàng mới được phân biệt với các phiếu hoàn tiền lịch sử. */
public final class DoiHangImpl implements DoiHangDAO {
    public DoiHangImpl() {}

    public List<Object[]> timDongGoc(long id) {
        return truyVan(
                "SELECT"
                    + " h.id,h.tong_tien,h.tam_tinh,h.thanh_toan_luc,i.don_gia,i.so_luong,h.trang_thai,p.nhom_hang,COALESCE(k.ma_khach_hang,''),COALESCE(k.ho_ten,''),COALESCE(k.diem_tich_luy,0)"
                    + " FROM chi_tiet_don_hang i WITH(UPDLOCK,HOLDLOCK) JOIN hoa_don h"
                    + " WITH(UPDLOCK,HOLDLOCK) ON h.don_hang_id=i.don_hang_id JOIN san_pham p ON"
                    + " p.id=i.san_pham_id JOIN don_hang d ON d.id=i.don_hang_id LEFT JOIN"
                    + " khach_hang k ON k.id=d.khach_hang_id WHERE i.id=?",
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
                taoMa("DOI"),
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
