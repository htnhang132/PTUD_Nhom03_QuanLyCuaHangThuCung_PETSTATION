package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.DonDoiTreoDAO;
import com.petstation.model.DonDoiTreo;

public final class DonDoiTreoDAOImpl implements DonDoiTreoDAO {
    @Override
    public DonDoiTreo lay(long id) {
        var ds = truyVan("""
                SELECT JSON_VALUE(j.noi_dung,'$.timHoaDon'),
                       CAST(JSON_VALUE(j.noi_dung,'$.dongGoc') AS bigint),
                       JSON_VALUE(j.noi_dung,'$.maThayThe'), JSON_VALUE(j.noi_dung,'$.timHang'),
                       CAST(JSON_VALUE(j.noi_dung,'$.soLuong') AS int),
                       JSON_VALUE(j.noi_dung,'$.lyDo'),
                       JSON_VALUE(j.noi_dung,'$.nguyenTem'), JSON_VALUE(j.noi_dung,'$.chuaDung')
                FROM hoa_don h WITH(UPDLOCK,HOLDLOCK)
                CROSS APPLY (SELECT CASE WHEN ISJSON(h.ghi_chu)=1 THEN h.ghi_chu ELSE N'{}' END noi_dung) j
                WHERE (h.ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hoa_don, PATINDEX('%[0-9]%', h.ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND h.trang_thai='GIO_TREO'
                  AND JSON_VALUE(j.noi_dung,'$.loai')='DOI_PHU_KIEN'
                  AND JSON_VALUE(j.noi_dung,'$.v')='1'
                """, id, id);
        if (ds.isEmpty()) return null;
        Object[] d = ds.get(0);
        if (d[1] == null || d[4] == null) return null;
        return new DonDoiTreo((String) d[0], ((Number) d[1]).longValue(),
                (String) d[2], (String) d[3], ((Number) d[4]).intValue(),
                (String) d[5], "true".equals(d[6]), "true".equals(d[7]));
    }

    @Override
    public void luu(long id, DonDoiTreo don) {
        long dong = don.dongGoc();
        long hdId = dong > 100_000L ? dong / 100_000L : dong;
        long spId = dong > 100_000L ? dong % 100_000L : dong;
        capNhat("""
                DECLARE @nhap nvarchar(max) = (SELECT 'DOI_PHU_KIEN' loai, 1 v,
                    CAST(? AS nvarchar(max)) timHoaDon, CAST(? AS bigint) dongGoc,
                    CAST(? AS nvarchar(max)) maThayThe, CAST(? AS nvarchar(max)) timHang,
                    CAST(? AS int) soLuong, CAST(? AS nvarchar(max)) lyDo,
                    CAST(? AS bit) nguyenTem, CAST(? AS bit) chuaDung
                    FOR JSON PATH, WITHOUT_ARRAY_WRAPPER);
                IF DATALENGTH(@nhap)>2000
                    THROW 50001, N'Nội dung nháp quá dài. Hãy rút gọn lý do hoặc từ khóa tìm kiếm.', 1;
                UPDATE hoa_don SET ghi_chu=@nhap, ma_khach_hang=(
                    SELECT TOP 1 h.ma_khach_hang FROM chi_tiet_hoa_don c
                    JOIN hoa_don h ON h.ma_hoa_don=c.ma_hoa_don
                    WHERE (h.ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hoa_don, PATINDEX('%[0-9]%', h.ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)
                           OR c.ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_san_pham, PATINDEX('%[0-9]%', c.ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)))
                WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='GIO_TREO';
                """, don.timHoaDon(), don.dongGoc(), don.maThayThe(), don.timHang(),
                don.soLuong(), don.lyDo(), don.nguyenTem(), don.chuaDung(),
                hdId, hdId, spId, spId, id, id);
    }

    @Override
    public String layMaHoaDonGoc(long dong) {
        if (dong > 100_000L) {
            long hdId = dong / 100_000L;
            long spId = dong % 100_000L;
            Object ma = layGiaTriDon("SELECT TOP 1 h.ma_hoa_don FROM hoa_don h JOIN chi_tiet_hoa_don c"
                    + " ON c.ma_hoa_don=h.ma_hoa_don WHERE (h.ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hoa_don, PATINDEX('%[0-9]%', h.ma_hoa_don), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))"
                    + " AND (c.ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_san_pham, PATINDEX('%[0-9]%', c.ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    hdId, hdId, spId, spId);
            if (ma == null) throw new IllegalArgumentException("Không tìm thấy hóa đơn gốc.");
            return ma.toString();
        }
        Object ma = layGiaTriDon("SELECT TOP 1 h.ma_hoa_don FROM hoa_don h JOIN chi_tiet_hoa_don c"
                + " ON c.ma_hoa_don=h.ma_hoa_don WHERE (c.ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_san_pham, PATINDEX('%[0-9]%', c.ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY h.thanh_toan_luc DESC", dong, dong);
        if (ma == null) throw new IllegalArgumentException("Không tìm thấy hóa đơn gốc.");
        return ma.toString();
    }
}
