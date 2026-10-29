package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.DeXuatGiaDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Giá đề xuất được lưu trực tiếp trong cột de_xuat_gia của bảng san_pham. */
public final class DeXuatGiaDAOImpl implements DeXuatGiaDAO {
    private static final DateTimeFormatter DINH_DANG_ISO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    public DeXuatGiaDAOImpl() {}

    public List<Object[]> timSanPham(String ma) {
        return truyVan(
                "SELECT id,gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?", ma);
    }

    public long them(long sanPham, long nguoi, long cu, long moi, String lyDo) {
        String chuoi =
                "PENDING|"
                        + nguoi
                        + "|"
                        + cu
                        + "|"
                        + moi
                        + "|"
                        + (lyDo == null ? "" : lyDo.replace("|", "/").replace(";", ","))
                        + "|"
                        + LocalDateTime.now().format(DINH_DANG_ISO);
        capNhat("UPDATE san_pham SET de_xuat_gia=? WHERE id=?", chuoi, sanPham);
        return sanPham;
    }

    public List<Object[]> timChoDuyet(long id) {
        List<Object[]> dong =
                truyVan(
                        "SELECT id, gia_ban, COALESCE(de_xuat_gia, N'') FROM san_pham"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                        id);
        if (dong.isEmpty()) return List.of();
        Object[] d = dong.get(0);
        String dxg = String.valueOf(d[2]).trim();
        if (dxg.isEmpty()) return List.of();
        String[] p = dxg.split("\\|");
        if (p.length < 4 || !"PENDING".equals(p[0].trim())) return List.of();
        long giaHienTai = Long.parseLong(p[2].trim());
        long giaDeXuat = Long.parseLong(p[3].trim());
        return List.<Object[]>of(new Object[] {d[0], giaHienTai, giaDeXuat, d[1]});
    }

    public void apDung(long id, long sanPham, long gia, long nguoi) {
        List<Object[]> choDuyet = timChoDuyet(id);
        long giaCu = choDuyet.isEmpty() ? gia : ((Number) choDuyet.get(0)[1]).longValue();
        capNhat(
                "UPDATE san_pham SET gia_ban=?,"
                    + " de_xuat_gia=REPLACE(COALESCE(de_xuat_gia,N''),N'PENDING|',N'APPROVED|'),"
                    + " lich_su_gia = CASE WHEN lich_su_gia IS NULL OR lich_su_gia=N'' THEN N''"
                    + " ELSE lich_su_gia + N';' END + CONVERT(NVARCHAR(19), SYSDATETIME(), 120) +"
                    + " N'|' + CAST(? AS NVARCHAR(20)) + N'|' + CAST(? AS NVARCHAR(20)) + N'|' +"
                    + " CAST(? AS NVARCHAR(20)) WHERE id=?",
                gia,
                giaCu,
                gia,
                nguoi,
                sanPham);
    }

    public void tuChoi(long id) {
        capNhat(
                "UPDATE san_pham SET"
                        + " de_xuat_gia=REPLACE(COALESCE(de_xuat_gia,N''),N'PENDING|',N'REJECTED|')"
                        + " WHERE id=?",
                id);
    }
}
