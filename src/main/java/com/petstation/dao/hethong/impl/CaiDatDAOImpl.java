package com.petstation.dao.hethong.impl;

import com.petstation.dao.hethong.CaiDatDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.hethong.CaiDat;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DuLieu;

import java.util.List;
import java.util.Optional;

public final class CaiDatDAOImpl implements CaiDatDAO {
    public CaiDatDAOImpl() {}

    @Override
    public String timGiaTri(String maCaiDat) {
        Object giaTri =
                ThaoTacSQL.layGiaTriDon("SELECT gia_tri FROM cai_dat WHERE ma_cai_dat=?", maCaiDat);
        return giaTri == null ? null : DuLieu.chuyenThanhChuoi(giaTri);
    }

    @Override
    public Optional<CaiDat> timCaiDat(String maCaiDat) {
        List<Object[]> dong =
                ThaoTacSQL.truyVan(
                        "SELECT c.ma_cai_dat, c.gia_tri, c.ma_nhan_vien, n.ho_ten, c.ngay_cap_nhat"
                                + " FROM cai_dat c LEFT JOIN nhan_vien n ON"
                                + " n.ma_nhan_vien=c.ma_nhan_vien WHERE c.ma_cai_dat=?",
                        maCaiDat);
        if (dong.isEmpty()) return Optional.empty();
        Object[] d = dong.get(0);
        NhanVien nguoiCapNhat = null;
        if (d[2] != null) {
            nguoiCapNhat = new NhanVien();
            nguoiCapNhat.setMaNhanVien(d[2].toString());
            if (d[3] != null) {
                nguoiCapNhat.setHoTen(d[3].toString());
            }
        }
        return Optional.of(
                new CaiDat(
                        DuLieu.chuyenThanhChuoi(d[0]),
                        nguoiCapNhat,
                        DuLieu.chuyenThanhChuoi(d[1]),
                        DuLieu.chuyenLocalDateTime(d[4])));
    }

    @Override
    public int themGiaTri(String maCaiDat, String giaTri, String maNhanVien) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO cai_dat(ma_cai_dat,gia_tri,ma_nhan_vien) VALUES(?,?,?)",
                maCaiDat,
                giaTri,
                maNhanVien);
    }

    @Override
    public int capNhatGiaTri(String giaTri, String maNhanVien, String maCaiDat) {
        return ThaoTacSQL.capNhat(
                "UPDATE cai_dat WITH(UPDLOCK,HOLDLOCK) SET"
                        + " gia_tri=?,ma_nhan_vien=?,ngay_cap_nhat=SYSDATETIME()"
                        + " WHERE ma_cai_dat=?",
                giaTri,
                maNhanVien,
                maCaiDat);
    }
}
