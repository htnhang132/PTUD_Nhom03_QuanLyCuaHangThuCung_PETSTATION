package com.petstation.dao.spa.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.spa.GoiDichVuSpaDAO;
import com.petstation.model.spa.GoiDichVuSpa;

import java.util.List;

public final class GoiDichVuSpaDAOImpl implements GoiDichVuSpaDAO {
    public GoiDichVuSpaDAOImpl() {}

    @Override
    public List<GoiDichVuSpa> layTatCa() {
        return truyVanDanhSach(
                "SELECT ma_goi, ten_goi, mo_ta, thoi_gian_du_kien, bang_gia, trang_thai"
                        + " FROM goi_dich_vu_spa ORDER BY ma_goi ASC",
                r -> chuyenDong(r));
    }

    @Override
    public GoiDichVuSpa timTheoMa(String maDichVu) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_goi, ten_goi, mo_ta, thoi_gian_du_kien, bang_gia, trang_thai"
                                + " FROM goi_dich_vu_spa WHERE ma_goi = ?",
                        maDichVu);
        if (rows.isEmpty()) return null;
        return chuyenDong(rows.get(0));
    }

    @Override
    public List<GoiDichVuSpa> layDanhSachDichVu() {
        return layTatCa();
    }

    private GoiDichVuSpa chuyenDong(Object[] r) {
        GoiDichVuSpa g = new GoiDichVuSpa();
        g.setMaGoi(r[0] != null ? r[0].toString() : null);
        g.setTenGoi(r[1] != null ? r[1].toString() : null);
        g.setMoTa(r[2] != null ? r[2].toString() : null);
        g.setThoiGianDuKien(r[3] != null ? ((Number) r[3]).intValue() : 0);
        g.setBangGia(r[4] != null ? r[4].toString() : null);
        g.setTrangThai(r[5] != null ? r[5].toString() : null);
        return g;
    }
}
