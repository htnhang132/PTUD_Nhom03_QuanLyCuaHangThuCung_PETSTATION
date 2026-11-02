package com.petstation.dao.impl;

import com.petstation.dao.QuyenVaiTroDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.VaiTro;

import java.util.ArrayList;
import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC quyền DAO. */
public final class QuyenVaiTroDAOImpl implements QuyenVaiTroDAO {
    public QuyenVaiTroDAOImpl() {}

    public List<Object[]> timCua(Object... cacThamSo) {
        String maVaiTro = cacThamSo.length > 0 ? String.valueOf(cacThamSo[0]) : "";
        VaiTro vt = VaiTro.docTuCSDL(maVaiTro);
        Object chuoi =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT TOP 1 quyen FROM tai_khoan WHERE vai_tro=? AND quyen IS NOT NULL"
                                + " AND quyen<>N''",
                        vt.name());
        String ds = chuoi != null ? chuoi.toString() : macDinhTheoVaiTro(vt);
        List<Object[]> ketQua = new ArrayList<>();
        for (String q : ds.split("[,;]")) {
            if (!q.isBlank()) {
                ketQua.add(new Object[] {q.trim()});
            }
        }
        return ketQua;
    }

    public static String macDinhTheoVaiTro(VaiTro vt) {
        return switch (vt) {
            case QUAN_LY_CUA_HANG ->
                    "XEM_TONG_QUAN,QUAN_LY_TAI_KHOAN,QUAN_LY_LICH_LAM_VIEC,XEM_LICH_LAM_VIEC,QUAN_LY_KHACH_HANG,QUAN_LY_THU_CUNG,QUAN_LY_SAN_PHAM,QUAN_LY_NHA_CUNG_CAP,NHAP_KHO,XUAT_HUY_KHO,KIEM_KE_KHO,DE_XUAT_GIA,DUYET_GIA,BAN_HANG,THANH_TOAN,QUAN_LY_CA_THU_NGAN,QUAN_LY_KHUYEN_MAI,QUAN_LY_SPA,QUAN_LY_LUU_TRU,XU_LY_PHE_DUYET,XEM_NHAT_KY,XEM_BAO_CAO_CHUNG,XEM_BAO_CAO_CA";
            case NHAN_VIEN_BAN_HANG ->
                    "XEM_TONG_QUAN,XEM_LICH_LAM_VIEC,QUAN_LY_KHACH_HANG,QUAN_LY_THU_CUNG,BAN_HANG,THANH_TOAN,QUAN_LY_CA_THU_NGAN,QUAN_LY_SPA,QUAN_LY_LUU_TRU,XEM_BAO_CAO_CA";
            case NHAN_VIEN_KHO ->
                    "XEM_TONG_QUAN,XEM_LICH_LAM_VIEC,QUAN_LY_SAN_PHAM,QUAN_LY_NHA_CUNG_CAP,NHAP_KHO,XUAT_HUY_KHO,KIEM_KE_KHO,DE_XUAT_GIA";
            case NHAN_VIEN_SPA_HOTEL ->
                    "XEM_TONG_QUAN,XEM_LICH_LAM_VIEC,QUAN_LY_THU_CUNG,QUAN_LY_SPA,QUAN_LY_LUU_TRU";
        };
    }
}
