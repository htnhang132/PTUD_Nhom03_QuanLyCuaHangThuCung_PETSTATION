package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.HopDongLuuTruDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.util.DuLieu;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC Hotel DAO. */
public final class HopDongLuuTruDAOImpl implements HopDongLuuTruDAO {
    public HopDongLuuTruDAOImpl() {}

    @Override
    public List<Object[]> layPhongDangDon() {
        return ThaoTacSQL.truyVan(
                "SELECT ma_phong AS ma_khoa,ma_phong FROM phong_hotel WHERE trang_thai='DANG_DON' ORDER BY"
                        + " ma_phong");
    }

    @Override
    public int capNhatNhanPhong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET trang_thai='DANG_O' WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?)",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public List<Object[]> timDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_phong,gia_dem,trang_thai,loai_phong,suc_chua FROM phong_hotel"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?",
                cacThamSo);
    }

    @Override
    public long demPhongDatTrung(
            long phongId, java.time.LocalDateTime ketThuc, java.time.LocalDateTime batDau) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_phong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phong, 3, 20) AS BIGINT)=?) AND trang_thai IN ('DA_DAT','DANG_O')"
                        + " AND ngay_vao<? AND ngay_ra_du_kien>?",
                phongId, phongId, ketThuc, batDau);
    }

    @Override
    public long demThuCungDatTrung(
            long thuCungId, java.time.LocalDateTime ketThuc, java.time.LocalDateTime batDau) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_thu_cung=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT)=?) AND trang_thai IN ('DA_DAT','DANG_O')"
                        + " AND ngay_vao<? AND ngay_ra_du_kien>?",
                thuCungId, thuCungId, ketThuc, batDau);
    }

    @Override
    public long demPhongDatTrungBoQua(
            long phongId, java.time.LocalDateTime ketThuc,
            java.time.LocalDateTime batDau, long boQuaId) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_phong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phong, 3, 20) AS BIGINT)=?) AND trang_thai IN ('DA_DAT','DANG_O')"
                        + " AND ngay_vao<? AND ngay_ra_du_kien>? AND (ma_hop_dong<>CAST(? AS VARCHAR) AND TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)<>?)",
                phongId, phongId, ketThuc, batDau, boQuaId, boQuaId);
    }

    @Override
    public long demThuCungDatTrungBoQua(
            long thuCungId, java.time.LocalDateTime ketThuc,
            java.time.LocalDateTime batDau, long boQuaId) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_thu_cung=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT)=?) AND trang_thai IN ('DA_DAT','DANG_O')"
                        + " AND ngay_vao<? AND ngay_ra_du_kien>? AND (ma_hop_dong<>CAST(? AS VARCHAR) AND TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)<>?)",
                thuCungId, thuCungId, ketThuc, batDau, boQuaId, boQuaId);
    }

    @Override
    public List<Object[]> layDatPhongSua(long id) {
        return ThaoTacSQL.truyVan(
                "SELECT h.trang_thai,p.ma_thu_cung,r.ma_phong,h.ngay_vao,"
                        + " h.ngay_ra_du_kien,h.ghi_chu,h.tien_coc,h.da_nhan_coc,"
                        + " COALESCE(h.coc_yeu_cau,h.tien_coc),COALESCE(h.gia_dem,r.gia_dem)"
                        + " FROM hop_dong_luu_tru h WITH(UPDLOCK,HOLDLOCK)"
                        + " JOIN thu_cung p ON p.ma_thu_cung=h.ma_thu_cung"
                        + " JOIN phong_hotel r ON r.ma_phong=h.ma_phong WHERE (h.ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hop_dong, 5, 20) AS BIGINT)=?)",
                id, id);
    }

    @Override
    public int capNhatDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET ma_khach_hang=?,ma_thu_cung=?,ma_phong=?,"
                        + " ngay_vao=?,ngay_ra_du_kien=?,gia_dem=?,coc_yeu_cau=?,ghi_chu=?"
                        + " WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?) AND trang_thai='DA_DAT' AND ma_hoa_don IS NULL",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[4],
                cacThamSo[5],
                cacThamSo[6],
                cacThamSo[7],
                cacThamSo[8], cacThamSo[8]);
    }

    @Override
    public int huyDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET trang_thai='DA_HUY',ghi_chu=COALESCE(ghi_chu + N' | ', N'') + ?"
                        + " WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?) AND trang_thai='DA_DAT' AND ma_hoa_don IS NULL"
                        + " AND da_nhan_coc=0 AND COALESCE(tien_coc,0)=0",
                cacThamSo[0],
                cacThamSo[1], cacThamSo[1]);
    }

    @Override
    public long themDatPhong(Object... cacThamSo) {
        ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " hop_dong_luu_tru(ma_hop_dong,ma_khach_hang,ma_thu_cung,ma_phong,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu)"
                    + " VALUES(?,"
                    + "(SELECT TOP 1 ma_khach_hang FROM khach_hang WHERE ma_khach_hang=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT)=?),"
                    + "(SELECT TOP 1 ma_thu_cung FROM thu_cung WHERE ma_thu_cung=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_thu_cung, 3, 20) AS BIGINT)=?),"
                    + "(SELECT TOP 1 ma_phong FROM phong_hotel WHERE ma_phong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phong, 3, 20) AS BIGINT)=?),"
                    + "?,?,?,'DA_DAT',?,?)",
                cacThamSo[0],
                cacThamSo[1], cacThamSo[1],
                cacThamSo[2], cacThamSo[2],
                cacThamSo[3], cacThamSo[3],
                cacThamSo[4],
                cacThamSo[5],
                cacThamSo[6],
                cacThamSo[7],
                cacThamSo[8]);
        return DuLieu.layIdCauNoi(null, String.valueOf(cacThamSo[0]));
    }

    @Override
    public Object layNhanPhong(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT ma_phong FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?) AND"
                        + " trang_thai='DA_DAT' AND (CASE WHEN da_nhan_coc=1 THEN tien_coc ELSE 0"
                        + " END)>=COALESCE(coc_yeu_cau,tien_coc)",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public int danhDauPhongDangO(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel WITH(UPDLOCK,HOLDLOCK) SET"
                        + " trang_thai='DANG_O' WHERE (ma_phong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phong, 3, 20) AS BIGINT)=?) AND"
                        + " trang_thai='TRONG'",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public long demNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?) AND trang_thai='DANG_O'",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public int capNhatNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " nhat_ky_cham_soc(ma_nhat_ky,ma_hop_dong,ma_nhan_vien,an_uong,ve_sinh,tinh_trang,thoi_gian)"
                    + " VALUES(?,"
                    + "(SELECT TOP 1 ma_hop_dong FROM hop_dong_luu_tru WHERE ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?),"
                    + "(SELECT TOP 1 ma_nhan_vien FROM nhan_vien WHERE ma_nhan_vien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT)=?),"
                    + "?,?,?,SYSDATETIME())",
                DuLieu.taoMa("NK"),
                cacThamSo[0], cacThamSo[0],
                cacThamSo[1], cacThamSo[1],
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[4]);
    }

    @Override
    public List<Object[]> timHotelConPhaiThu(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.ngay_vao,h.ngay_ra_du_kien,COALESCE(h.gia_dem,p.gia_dem),CASE WHEN"
                    + " h.da_nhan_coc=1 THEN h.tien_coc ELSE 0 END FROM hop_dong_luu_tru h"
                    + " WITH(UPDLOCK,HOLDLOCK) JOIN phong_hotel p ON p.ma_phong=h.ma_phong WHERE (h.ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hop_dong, 5, 20) AS BIGINT)=?)"
                    + " AND h.trang_thai='DANG_O' AND h.ma_hoa_don IS NULL",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public List<Object[]> layNhatKyChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT j.thoi_gian,n.ho_ten,j.an_uong,j.ve_sinh,j.tinh_trang FROM"
                        + " nhat_ky_cham_soc j JOIN nhan_vien n ON n.ma_nhan_vien=j.ma_nhan_vien WHERE"
                        + " (j.ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(j.ma_hop_dong, 5, 20) AS BIGINT)=?) ORDER BY j.thoi_gian DESC",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public int danhDauPhongKhaDung(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='TRONG'"
                        + " WHERE (ma_phong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phong, 3, 20) AS BIGINT)=?) AND trang_thai='DANG_DON'",
                cacThamSo[0], cacThamSo[0]);
    }

    @Override
    public int datMucCoc(long muc, long id) {
        return capNhat("UPDATE hop_dong_luu_tru SET coc_yeu_cau=? WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?)", muc, id, id);
    }

    @Override
    public List<Object[]> layLichSuThuCoc(long id) {
        return truyVan(
                "SELECT l.phuong_thuc,l.so_tien,l.khach_dua,l.tien_thua,l.tao_luc,"
                        + " COALESCE(p.ten_ca,l.ma_phien)"
                        + " FROM lich_su_thu_coc_hotel l LEFT JOIN phien_lam_viec p"
                        + " ON p.ma_phien=l.ma_phien WHERE (l.ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_hop_dong, 5, 20) AS BIGINT)=?)"
                        + " ORDER BY l.tao_luc",
                id, id);
    }

    @Override
    public List<Object[]> layMucCoc(long id) {
        return truyVan(
                "SELECT COALESCE(coc_yeu_cau,tien_coc),CASE WHEN da_nhan_coc=1 THEN"
                        + " tien_coc ELSE 0 END FROM hop_dong_luu_tru WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?)",
                id, id);
    }

    @Override
    public List<Object[]> khoaHopDongThuCoc(long id) {
        return truyVan(
                "SELECT trang_thai,COALESCE(coc_yeu_cau,tien_coc),CASE WHEN"
                        + " da_nhan_coc=1 THEN tien_coc ELSE 0 END FROM"
                        + " hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?)",
                id, id);
    }

    @Override
    public long demKhoaThuCoc(long id, String tienToKhoa) {
        return laySo(
                "SELECT COUNT(*) FROM lich_su_thu_coc_hotel"
                        + " WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?) AND khoa_thu_coc=?",
                id, id,
                tienToKhoa);
    }

    @Override
    public int ghiNhanTongCoc(long tongCoc, long taiKhoan, long ca, long id) {
        return capNhat(
                "UPDATE hop_dong_luu_tru SET"
                    + " tien_coc=?,da_nhan_coc=1,nhan_coc_luc=SYSDATETIME(),"
                    + " nhan_coc_boi=(SELECT TOP 1 ten_dang_nhap FROM tai_khoan WHERE ten_dang_nhap=CAST(? AS VARCHAR) OR ma_nhan_vien=CAST(? AS VARCHAR)),"
                    + " ma_phien_nhan_coc=(SELECT TOP 1 ma_phien FROM phien_lam_viec WHERE ma_phien=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phien, 4, 20) AS BIGINT)=?)"
                    + " WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?)",
                tongCoc,
                taiKhoan, taiKhoan,
                ca, ca,
                id, id);
    }

    @Override
    public List<Object[]> khoaHopDongDoiCoc(long id) {
        return truyVan(
                "SELECT ngay_vao,ngay_ra_du_kien,COALESCE(gia_dem,(SELECT"
                        + " p.gia_dem FROM phong_hotel p WHERE"
                        + " p.ma_phong=h.ma_phong)),CASE WHEN da_nhan_coc=1 THEN tien_coc"
                        + " ELSE 0 END,trang_thai FROM hop_dong_luu_tru h"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE (h.ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_hop_dong, 5, 20) AS BIGINT)=?)",
                id, id);
    }

    @Override
    public int dieuChinhMucCoc(long muc, String lyDo, long id) {
        return capNhat(
                "UPDATE hop_dong_luu_tru SET coc_yeu_cau=?,ly_do_doi_coc=? WHERE (ma_hop_dong=CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 5, 20) AS BIGINT)=?)",
                muc,
                lyDo,
                id, id);
    }
}
