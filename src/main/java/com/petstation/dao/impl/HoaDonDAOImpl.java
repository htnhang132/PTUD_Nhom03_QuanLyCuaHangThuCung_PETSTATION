package com.petstation.dao.impl;

import com.petstation.dao.HoaDonDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.util.DuLieu;

import java.util.List;

public final class HoaDonDAOImpl implements HoaDonDAO {
    public List<Object[]> layNguonBienLai(String soHoaDon) {
        return ThaoTacSQL.truyVan(
                "SELECT CASE WHEN x.ma_phieu IS NOT NULL THEN 'DOI_HANG' WHEN ht.ma_hop_dong IS NOT NULL THEN"
                    + " 'LUU_TRU' WHEN spa.ma_phieu IS NOT NULL THEN 'SPA' ELSE ''"
                    + " END,COALESCE(x.ma_phieu,ht.ma_hop_dong,spa.ma_phieu,''),COALESCE(x.gia_tri_duoc_doi,CASE"
                    + " WHEN ht.da_nhan_coc=1 THEN ht.tien_coc ELSE 0"
                    + " END,0),COALESCE(goc.ma_hoa_don,'') FROM hoa_don h LEFT JOIN phieu_doi_tra x"
                    + " ON x.ma_hoa_don_thay_the=h.ma_hoa_don AND x.loai_phieu='DOI_HANG' AND"
                    + " x.trang_thai='HOAN_TAT' LEFT JOIN hoa_don goc ON goc.ma_hoa_don=x.ma_hoa_don LEFT"
                    + " JOIN hop_dong_luu_tru ht ON ht.ma_hoa_don=h.ma_hoa_don LEFT JOIN"
                    + " phieu_tiep_nhan_spa spa ON spa.ma_hoa_don=h.ma_hoa_don WHERE h.ma_hoa_don=?",
                soHoaDon);
    }

    public HoaDonDAOImpl() {}

    public Object timHoaDonTheoKhoaThu(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_hoa_don FROM hoa_don WHERE idempotency_key=?", cacThamSo);
    }

    public int capNhatThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-?+? WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[1], cacThamSo[2], cacThamSo[2]);
    }

    public int capNhatCapNhatHang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET hang_thanh_vien=? WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[1], cacThamSo[1]);
    }

    public long demMoLai(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM chi_tiet_hoa_don WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int capNhatXoaTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM chi_tiet_hoa_don WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int xoaGioHangTreo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM hoa_don WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND trang_thai='GIO_TREO'",
                cacThamSo[0], cacThamSo[0]);
    }

    public Object layQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_khach_hang FROM hop_dong_luu_tru WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, PATINDEX('%[0-9]%', ma_hop_dong), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> timQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_khach_hang,ho_ten,diem_tich_luy FROM khach_hang WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demHopDongGanHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int capNhatHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE (ma_lo=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[1], cacThamSo[1]);
    }

    public int hoanDiemKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET diem_tich_luy=? WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[1], cacThamSo[1]);
    }

    public int danhDauHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET trang_thai='DA_HUY' WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int danhDauHuyDonHang(Object... cacThamSo) {
        return 1;
    }

    public long demThanhToan(Object... cacThamSo) {
        String username = null;
        if (cacThamSo != null && cacThamSo.length > 0 && cacThamSo[0] != null) {
            String s = String.valueOf(cacThamSo[0]).trim();
            if (!s.isEmpty() && !"0".equals(s)) {
                username = s;
            }
        }
        if (username == null) {
            try {
                username = com.petstation.util.BoiCanhDangNhap.layTenDangNhap();
            } catch (Exception ignored) {}
        }
        Object val = null;
        if (username != null && !username.isBlank()) {
            val = ThaoTacSQL.layGiaTriDon(
                    "SELECT TOP 1 ma_phien FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE"
                            + " (ten_dang_nhap=? OR ten_dang_nhap IN (SELECT ten_dang_nhap FROM tai_khoan WHERE ma_nhan_vien=? OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))) AND trang_thai='DANG_MO'",
                    username, username, username);
        }
        if (val == null) {
            try {
                String cur = com.petstation.util.BoiCanhDangNhap.layTenDangNhap();
                if (cur != null && !cur.isBlank()) {
                    val = ThaoTacSQL.layGiaTriDon(
                            "SELECT TOP 1 ma_phien FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE ten_dang_nhap=? AND trang_thai='DANG_MO'",
                            cur);
                }
            } catch (Exception ignored) {}
        }
        if (val == null) return 0L;
        long id = DuLieu.layIdCauNoi(null, String.valueOf(val));
        return id > 0 ? id : 1L;
    }

    public long demNgayDaChot(Object... cacThamSo) {
        return 0L;
    }

    public List<Object[]> timThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT diem_tich_luy, CAST(COALESCE(dang_hoat_dong, 1) AS BIT) FROM khach_hang"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int ghiNhanSuDungVoucher(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE voucher WITH(UPDLOCK,HOLDLOCK) SET"
                        + " da_dung=da_dung+1,su_dung_luc=SYSDATETIME() WHERE"
                        + " ma_voucher=? AND trang_thai='CON_HAN' AND"
                        + " da_dung<so_luong AND hieu_luc_den>SYSDATETIME() AND"
                        + " don_toi_thieu<=? AND (ma_khach_hang IS NULL OR"
                        + " ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND (chi_ap_dung_spa=0 OR"
                        + " ?='SPA')",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2],
                cacThamSo[2],
                cacThamSo[3]);
    }

    public long themThanhToan(Object... cacThamSo) {
        return 0L;
    }

    public List<Object[]> khoaSanPhamDeBan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_san_pham,gia_ban,trang_thai FROM san_pham"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?",
                cacThamSo);
    }

    public long themDongDonHang(Object... cacThamSo) {
        Object maSpHoacDv = cacThamSo[1] != null ? cacThamSo[1] : cacThamSo[2];
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,ma_dich_vu,ten_hang,so_luong,don_gia)"
                    + " VALUES(COALESCE((SELECT TOP 1 ma_hoa_don FROM hoa_don WHERE ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), CAST(? AS VARCHAR)),"
                    + "COALESCE((SELECT TOP 1 ma_san_pham FROM san_pham WHERE ma_san_pham=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), CAST(? AS VARCHAR)),?,?,?,?)",
                cacThamSo[0], cacThamSo[0], cacThamSo[0],
                maSpHoacDv, maSpHoacDv, maSpHoacDv,
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[4],
                cacThamSo[5]);
        return 1L;
    }

    public List<Object[]> layLoTheoFEFO(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo,so_luong_ton FROM lo_hang"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE (ma_san_pham=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='SAN_SANG' AND so_luong_ton>0 AND"
                        + " (han_su_dung IS NULL OR"
                        + " han_su_dung>=CAST(GETDATE() AS DATE)) ORDER BY"
                        + " CASE WHEN han_su_dung IS NULL THEN 1 ELSE 0"
                        + " END,han_su_dung,ma_lo",
                cacThamSo[0], cacThamSo[0]);
    }

    public int truTonLo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-?"
                        + " WHERE (ma_lo=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND so_luong_ton>=?",
                cacThamSo[0], cacThamSo[1], cacThamSo[1], cacThamSo[2]);
    }

    public int ghiPhanBoLo(Object... cacThamSo) {
        Object muc = cacThamSo[0];
        Object loHangId = cacThamSo[1];
        return ThaoTacSQL.capNhat(
                "UPDATE TOP (1) chi_tiet_hoa_don SET ma_lo=COALESCE((SELECT TOP 1 ma_lo FROM lo_hang WHERE ma_lo=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), CAST(? AS VARCHAR))"
                        + " WHERE ma_lo IS NULL AND ma_dich_vu IS NULL"
                        + " AND (ma_san_pham=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))"
                        + " AND ma_hoa_don=(SELECT TOP 1 ma_hoa_don FROM hoa_don WHERE trang_thai='DA_THANH_TOAN' ORDER BY thanh_toan_luc DESC, tao_luc DESC)",
                loHangId, loHangId, loHangId, muc, muc);
    }

    public long themHoaDon(Object... cacThamSo) {
        String maHoaDon = String.valueOf(cacThamSo[0]);
        Object ca = cacThamSo[1];
        Object tong = cacThamSo[2];
        Object phuongThuc = cacThamSo[3];
        Object daNhan = cacThamSo[4];
        Object tienThua = cacThamSo[5];
        Object diemCong = cacThamSo[6];
        Object idempotencyKey = cacThamSo[7];
        Object thanhToanLuc = cacThamSo[8];
        Object tamTinh = cacThamSo[9];
        Object giamGia = cacThamSo[10];
        Object diemDung = cacThamSo[11];
        Object maVoucher = cacThamSo[12];
        if (maVoucher != null && String.valueOf(maVoucher).trim().isEmpty()) {
            maVoucher = null;
        }
        Object khachHang = cacThamSo.length > 13 ? cacThamSo[13] : null;
        Object taiKhoan = cacThamSo.length > 14 ? cacThamSo[14] : null;

        ThaoTacSQL.capNhat(
                "INSERT INTO hoa_don("
                    + "ma_hoa_don,ma_phien,ten_dang_nhap,ma_khach_hang,tong_tien,hinh_thuc_thanh_toan,"
                    + "tien_khach_dua,tien_thoi,diem_cong,idempotency_key,tao_luc,thanh_toan_luc,"
                    + "tam_tinh,giam_gia,diem_dung,ma_voucher,trang_thai) "
                    + "VALUES(?, "
                    + "(SELECT TOP 1 ma_phien FROM phien_lam_viec WHERE (ma_phien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phien, PATINDEX('%[0-9]%', ma_phien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT) OR ten_dang_nhap=CAST(? AS VARCHAR) OR ten_dang_nhap IN (SELECT ten_dang_nhap FROM tai_khoan WHERE TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) OR trang_thai='DANG_MO') AND trang_thai='DANG_MO' ORDER BY mo_luc DESC), "
                    + "COALESCE((SELECT TOP 1 ten_dang_nhap FROM tai_khoan WHERE ten_dang_nhap=CAST(? AS VARCHAR) OR ma_nhan_vien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), (SELECT TOP 1 ten_dang_nhap FROM phien_lam_viec WHERE ma_phien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phien, PATINDEX('%[0-9]%', ma_phien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), (SELECT TOP 1 ten_dang_nhap FROM phien_lam_viec WHERE trang_thai='DANG_MO'), 'admin'), "
                    + "(SELECT TOP 1 ma_khach_hang FROM khach_hang WHERE ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), "
                    + "?,?,?,?,?,?,?,?,?,?,?,(SELECT TOP 1 ma_voucher FROM voucher WHERE ma_voucher=CAST(? AS VARCHAR)),'DA_THANH_TOAN')",
                maHoaDon,
                ca, ca, taiKhoan, taiKhoan,
                taiKhoan, taiKhoan, taiKhoan, ca, ca,
                khachHang, khachHang,
                tong, phuongThuc, daNhan, tienThua, diemCong, idempotencyKey, thanhToanLuc, thanhToanLuc,
                tamTinh, giamGia, diemDung, maVoucher);
        return DuLieu.layIdCauNoi(null, maHoaDon);
    }

    public int ghiDiemThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET lich_su_diem = CASE WHEN lich_su_diem IS NULL OR"
                    + " lich_su_diem=N'' THEN N'' ELSE lich_su_diem + N';' END +"
                    + " CONVERT(NVARCHAR(19), SYSDATETIME(), 120) + N'|' + CAST(? AS NVARCHAR(20))"
                    + " + N'|' + CAST(? AS NVARCHAR(20)) + N'|' + CAST(diem_tich_luy AS"
                    + " NVARCHAR(20)) + N'|Thanh toán hóa đơn' WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2],
                cacThamSo[2]);
    }

    public long layTienSpaChoThanhToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT tong_tien FROM phieu_tiep_nhan_spa"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE (ma_phieu=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='HOAN_TAT' AND ma_hoa_don IS NULL",
                cacThamSo[0], cacThamSo[0]);
    }

    public int ganHoaDonSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET"
                        + " ma_hoa_don=COALESCE((SELECT TOP 1 ma_hoa_don FROM hoa_don WHERE ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), CAST(? AS VARCHAR)),trang_thai='DA_THANH_TOAN' WHERE (ma_phieu=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " ma_hoa_don IS NULL",
                cacThamSo[0], cacThamSo[0], cacThamSo[0], cacThamSo[1], cacThamSo[1]);
    }

    public int ganHoaDonHotel(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET"
                        + " ma_hoa_don=COALESCE((SELECT TOP 1 ma_hoa_don FROM hoa_don WHERE ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), CAST(? AS VARCHAR)),trang_thai='DA_TRA_PHONG',ngay_ra_thuc_te=SYSDATETIME()"
                        + " WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, PATINDEX('%[0-9]%', ma_hop_dong), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND trang_thai='DANG_O' AND"
                        + " ma_hoa_don IS NULL",
                cacThamSo[0], cacThamSo[0], cacThamSo[0], cacThamSo[1], cacThamSo[1]);
    }

    public int chuyenPhongChoDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='DANG_DON' WHERE ma_phong=(SELECT"
                        + " ma_phong FROM hop_dong_luu_tru WHERE ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, PATINDEX('%[0-9]%', ma_hop_dong), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))"
                        + " AND NOT EXISTS (SELECT 1 FROM hop_dong_luu_tru h2 WHERE h2.ma_phong=phong_hotel.ma_phong AND h2.trang_thai='DANG_O')",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demCapNhatHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h WHERE (h.ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_khach_hang, PATINDEX('%[0-9]%', h.ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " h.trang_thai='DA_THANH_TOAN'",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> timBienLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " h.thanh_toan_luc,h.ma_hoa_don,h.tam_tinh,h.giam_gia,h.tong_tien,h.hinh_thuc_thanh_toan,h.tien_khach_dua,h.tien_thoi,COALESCE(k.ho_ten,N'Khách"
                    + " lẻ'),COALESCE(h.ma_voucher,''),COALESCE(n.ho_ten,N'Thu ngân') FROM hoa_don"
                    + " h LEFT JOIN phien_lam_viec p ON p.ma_phien=h.ma_phien LEFT JOIN"
                    + " tai_khoan t ON t.ten_dang_nhap=COALESCE(h.ten_dang_nhap,p.ten_dang_nhap) LEFT JOIN"
                    + " nhan_vien n ON n.ma_nhan_vien=t.ma_nhan_vien LEFT JOIN khach_hang k ON"
                    + " k.ma_khach_hang=h.ma_khach_hang WHERE h.ma_hoa_don=?",
                cacThamSo);
    }

    public List<Object[]> layDongBienLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " COALESCE(p.ma_san_pham,c.ma_dich_vu),COALESCE(c.ten_hang,p.ten),c.don_gia,SUM(c.so_luong)"
                    + " FROM chi_tiet_hoa_don c LEFT JOIN san_pham p ON p.ma_san_pham=c.ma_san_pham WHERE"
                    + " c.ma_hoa_don=? GROUP BY"
                    + " COALESCE(p.ma_san_pham,c.ma_dich_vu),COALESCE(c.ten_hang,p.ten),c.don_gia",
                cacThamSo);
    }

    public long themTreo(Object... cacThamSo) {
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " hoa_don(ma_hoa_don,ma_phien,ten_dang_nhap,ma_khach_hang,tam_tinh,giam_gia,tong_tien,hinh_thuc_thanh_toan,trang_thai,tao_luc,thanh_toan_luc)"
                    + " VALUES(?,(SELECT TOP 1 ma_phien FROM phien_lam_viec WHERE (ten_dang_nhap=CAST(? AS VARCHAR) OR ten_dang_nhap IN (SELECT ten_dang_nhap FROM tai_khoan WHERE ma_nhan_vien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) OR trang_thai='DANG_MO') AND"
                    + " trang_thai='DANG_MO' ORDER BY mo_luc DESC),COALESCE((SELECT TOP 1 ten_dang_nhap FROM tai_khoan WHERE ten_dang_nhap=CAST(? AS VARCHAR) OR ma_nhan_vien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, PATINDEX('%[0-9]%', ma_nhan_vien), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), (SELECT TOP 1 ten_dang_nhap FROM phien_lam_viec WHERE trang_thai='DANG_MO'), 'admin'),(SELECT TOP 1 ma_khach_hang FROM khach_hang WHERE ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)),0,0,0,'TIEN_MAT','GIO_TREO',SYSDATETIME(),SYSDATETIME())",
                cacThamSo[0],
                cacThamSo[1], cacThamSo[1], cacThamSo[1],
                cacThamSo[1], cacThamSo[1], cacThamSo[1],
                cacThamSo[2], cacThamSo[2]);
        return DuLieu.layIdCauNoi(null, String.valueOf(cacThamSo[0]));
    }

    public int capNhatTreo(Object... cacThamSo) {
        int r =
                ThaoTacSQL.capNhat(
                        "INSERT INTO"
                            + " chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,ma_dich_vu,ten_hang,don_gia,so_luong)"
                            + " VALUES(COALESCE((SELECT TOP 1 ma_hoa_don FROM hoa_don WHERE ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)), CAST(? AS VARCHAR)),?,?,?,?,?)",
                        cacThamSo[0],
                        cacThamSo[0],
                        cacThamSo[0],
                        cacThamSo[1],
                        cacThamSo[1],
                        cacThamSo[2],
                        cacThamSo[3],
                        cacThamSo[4]);
        ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET tam_tinh=COALESCE((SELECT SUM(so_luong*don_gia) FROM"
                    + " chi_tiet_hoa_don WHERE ma_hoa_don=hoa_don.ma_hoa_don),0), tong_tien=COALESCE((SELECT"
                    + " SUM(so_luong*don_gia) FROM chi_tiet_hoa_don WHERE ma_hoa_don=hoa_don.ma_hoa_don),0)"
                    + " WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
        return r;
    }

    public long demGioTreoCuaTaiKhoan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WITH(UPDLOCK,HOLDLOCK) WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='GIO_TREO'",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> timMoLai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham,p.ten,p.gia_ban,c.so_luong FROM"
                        + " chi_tiet_hoa_don c JOIN san_pham p ON"
                        + " p.ma_san_pham=c.ma_san_pham WHERE (c.ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_hoa_don, PATINDEX('%[0-9]%', c.ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " p.trang_thai='DANG_BAN'",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> layKhachCuaGioTreo(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT k.ma_khach_hang,k.ho_ten,k.diem_tich_luy FROM"
                        + " hoa_don g JOIN khach_hang k ON"
                        + " k.ma_khach_hang=g.ma_khach_hang WHERE (g.ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(g.ma_hoa_don, PATINDEX('%[0-9]%', g.ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND g.trang_thai='GIO_TREO'",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demXoaTreo(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WITH(UPDLOCK,HOLDLOCK) WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='GIO_TREO'",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE (ma_phieu=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, PATINDEX('%[0-9]%', ma_phieu), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='HOAN_TAT' AND ma_hoa_don IS NULL",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> layDichVuSpaQuyetToan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(g.ma_goi,'DV-SPA'), COALESCE(t.mo_ta_dich_vu,g.ten_goi,N'Dịch vụ"
                    + " Spa'), COALESCE(NULLIF(t.don_gia,0),t.tong_tien),"
                    + " COALESCE(NULLIF(t.so_luong,0),1) FROM phieu_tiep_nhan_spa t LEFT JOIN"
                    + " goi_dich_vu_spa g ON g.ma_goi=t.ma_goi WHERE (t.ma_phieu=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(t.ma_phieu, PATINDEX('%[0-9]%', t.ma_phieu), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public Object layKhachCuaPhieuSpa(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT p.ma_khach_hang FROM phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                        + " p.ma_thu_cung=t.ma_thu_cung WHERE (t.ma_phieu=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(t.ma_phieu, PATINDEX('%[0-9]%', t.ma_phieu), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> timHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.ma_hoa_don,h.diem_cong,h.diem_dung,h.ma_voucher,h.ma_khach_hang"
                        + " FROM hoa_don h WITH(UPDLOCK,HOLDLOCK) WHERE (h.ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hoa_don, PATINDEX('%[0-9]%', h.ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " h.trang_thai='DA_THANH_TOAN'",
                cacThamSo[0], cacThamSo[0]);
    }

    public long demPhieuDoiTraGanHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_doi_tra WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND trang_thai IN"
                        + " ('HOAN_TAT','CHO_XU_LY')",
                cacThamSo[0], cacThamSo[0]);
    }

    public List<Object[]> layPhanBoCanHoanTon(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo,so_luong FROM chi_tiet_hoa_don WHERE (ma_hoa_don=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, PATINDEX('%[0-9]%', ma_hoa_don), 20) AS BIGINT)=TRY_CAST(? AS BIGINT)) AND"
                        + " ma_lo IS NOT NULL",
                cacThamSo[0], cacThamSo[0]);
    }

    public long khoaDiemKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK) WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[0], cacThamSo[0]);
    }

    public int ghiDiemHuyHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE khach_hang SET lich_su_diem = CASE WHEN lich_su_diem IS NULL OR"
                    + " lich_su_diem=N'' THEN N'' ELSE lich_su_diem + N';' END +"
                    + " CONVERT(NVARCHAR(19), SYSDATETIME(), 120) + N'|' + CAST(? AS NVARCHAR(20))"
                    + " + N'|' + CAST(? AS NVARCHAR(20)) + N'|' + CAST(? AS NVARCHAR(20)) + N'|Hủy"
                    + " hóa đơn' WHERE (ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, PATINDEX('%[0-9]%', ma_khach_hang), 20) AS BIGINT)=TRY_CAST(? AS BIGINT))",
                cacThamSo[1],
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[0], cacThamSo[0]);
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
                    + " p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,COALESCE(SUM(l.so_luong_ton),0),"
                    + " COALESCE(p.anh,''),COALESCE(p.barcode,'')"
                    + " FROM san_pham p LEFT JOIN lo_hang l ON l.ma_san_pham=p.ma_san_pham AND"
                    + " l.trang_thai='SAN_SANG' AND (l.han_su_dung IS NULL OR"
                    + " l.han_su_dung>=CAST(GETDATE() AS date)) WHERE p.trang_thai='DANG_BAN' GROUP"
                    + " BY p.ma_san_pham,p.ten,p.nhom_hang,p.gia_ban,p.anh,p.barcode ORDER BY CASE WHEN"
                    + " NULLIF(p.anh,'') IS NULL THEN 1 ELSE 0 END,p.ma_san_pham",
                cacThamSo);
    }

    public List<Object[]> layThongTinIn(String soHoaDon) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(k.so_dien_thoai,''),h.diem_cong,k.diem_tich_luy FROM hoa_don h"
                        + " LEFT JOIN khach_hang k ON k.ma_khach_hang=h.ma_khach_hang WHERE h.ma_hoa_don=?",
                soHoaDon);
    }
}
