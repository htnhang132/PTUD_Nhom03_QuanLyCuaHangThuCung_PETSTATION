package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import com.petstation.dao.PhieuSpaTreoDAO;
import com.petstation.model.PhieuSpaTreo;
import java.util.List;

public final class PhieuSpaTreoDAOImpl implements PhieuSpaTreoDAO {
    public long them(String ma, long nguoiLap, PhieuSpaTreo p) {
        return com.petstation.database.ThaoTacSQL.them("""
            INSERT phieu_spa_treo(ma_phieu,nguoi_lap_id,ma_khach,so_dien_thoai,ma_thu,
                ma_goi,ma_hen,can_nang,tinh_trang,yeu_cau) VALUES(?,?,?,?,?,?,?,?,?,?)
            """, ma, nguoiLap, p.maKhach(), p.soDienThoai(), p.maThu(), p.maGoi(),
            p.maHen(), p.canNang(), p.tinhTrang(), p.yeuCau());
    }

    public void capNhat(long id, PhieuSpaTreo p) {
        com.petstation.database.ThaoTacSQL.capNhat("""
            UPDATE phieu_spa_treo SET ma_khach=?,so_dien_thoai=?,ma_thu=?,ma_goi=?,
                ma_hen=?,can_nang=?,tinh_trang=?,yeu_cau=?,cap_nhat_luc=SYSDATETIME() WHERE id=?
            """, p.maKhach(), p.soDienThoai(), p.maThu(), p.maGoi(), p.maHen(),
            p.canNang(), p.tinhTrang(), p.yeuCau(), id);
    }

    public PhieuSpaTreo lay(long id) {
        var ds = truyVan("""
            SELECT ma_khach,so_dien_thoai,ma_thu,ma_goi,ma_hen,can_nang,tinh_trang,yeu_cau
            FROM phieu_spa_treo WITH(UPDLOCK,HOLDLOCK) WHERE id=?
            """, id);
        if (ds.isEmpty()) return null;
        Object[] d = ds.get(0);
        return new PhieuSpaTreo((String)d[0], (String)d[1], (String)d[2], (String)d[3],
            (String)d[4], (String)d[5], (String)d[6], (String)d[7]);
    }

    public List<Object[]> danhSach() {
        return truyVan("""
            SELECT p.id,p.ma_phieu,COALESCE(k.ho_ten,N'Chưa chọn khách'),p.so_dien_thoai,
                COALESCE(t.ten,N'Chưa chọn thú cưng'),COALESCE(g.ten_goi,N'Chưa chọn dịch vụ'),
                p.cap_nhat_luc
            FROM phieu_spa_treo p LEFT JOIN khach_hang k ON k.ma_khach_hang=p.ma_khach
            LEFT JOIN thu_cung t ON t.ma_thu_cung=p.ma_thu
            LEFT JOIN goi_dich_vu_spa g ON g.ma_goi=p.ma_goi
            ORDER BY p.cap_nhat_luc DESC,p.id DESC
            """);
    }

    public void xoa(long id) {
        com.petstation.database.ThaoTacSQL.capNhat("DELETE phieu_spa_treo WHERE id=?", id);
    }
}
