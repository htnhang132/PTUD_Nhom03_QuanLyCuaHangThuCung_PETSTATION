package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.LuuTruDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC Hotel DAO. */
public final class LuuTruDAOImpl implements LuuTruDAO {
    public LuuTruDAOImpl() {}

    public List<Object[]> layPhongDangDon() {
        return ThaoTacSQL.truyVan(
                "SELECT id,ma_phong FROM phong_hotel WHERE trang_thai='CLEANING' ORDER BY"
                        + " ma_phong");
    }

    public int capNhatNhanPhong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET trang_thai='CHECKED_IN' WHERE id=?", cacThamSo);
    }

    public List<Object[]> timDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,gia_dem,trang_thai,loai_phong FROM phong_hotel"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?",
                cacThamSo);
    }

    public long demDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (phong_id=? OR thu_cung_id=?) AND trang_thai IN"
                        + " ('BOOKED','CHECKED_IN') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>?",
                cacThamSo);
    }

    public long themDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu)"
                    + " VALUES(?,?,?,?,?,?,?,'BOOKED',?,?)",
                cacThamSo);
    }

    public Object layNhanPhong(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT phong_id FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='BOOKED' AND (CASE WHEN da_nhan_coc=1 THEN tien_coc ELSE 0"
                        + " END)>=COALESCE(coc_yeu_cau,tien_coc)",
                cacThamSo);
    }

    public int danhDauPhongDangO(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel WITH(UPDLOCK,HOLDLOCK) SET"
                        + " trang_thai='OCCUPIED' WHERE id=? AND"
                        + " trang_thai='AVAILABLE'",
                cacThamSo);
    }

    public long demNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND trang_thai='CHECKED_IN'",
                cacThamSo);
    }

    public int capNhatNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,an_uong,ve_sinh,tinh_trang,thoi_gian)"
                    + " VALUES(?,?,?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    public List<Object[]> timHotelConPhaiThu(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.ngay_vao,h.ngay_ra_du_kien,COALESCE(h.gia_dem,p.gia_dem),CASE WHEN"
                    + " h.da_nhan_coc=1 THEN h.tien_coc ELSE 0 END FROM hop_dong_luu_tru h"
                    + " WITH(UPDLOCK,HOLDLOCK) JOIN phong_hotel p ON p.id=h.phong_id WHERE h.id=?"
                    + " AND h.trang_thai='CHECKED_IN' AND h.hoa_don_id IS NULL",
                cacThamSo);
    }

    public List<Object[]> layNhatKyChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT j.thoi_gian,n.ho_ten,j.an_uong,j.ve_sinh,j.tinh_trang FROM"
                        + " nhat_ky_cham_soc j JOIN nhan_vien n ON n.id=j.nhan_vien_id WHERE"
                        + " j.hop_dong_id=? ORDER BY j.thoi_gian DESC",
                cacThamSo);
    }

    public int danhDauPhongKhaDung(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='AVAILABLE'"
                        + " WHERE id=? AND trang_thai='CLEANING'",
                cacThamSo);
    }

    public int datMucCoc(long muc, long id) {

        return capNhat("UPDATE hop_dong_luu_tru SET coc_yeu_cau=? WHERE id=?", muc, id);
    }

    public List<Object[]> layLichSuThuCoc(long id) {

        return truyVan(
                "SELECT phuong_thuc,so_tien,khach_dua,tien_thua,nhan_luc,phien_id FROM khoan_thu"
                        + " WHERE hop_dong_id=? ORDER BY id",
                id);
    }

    public List<Object[]> layMucCoc(long id) {

        return truyVan(
                "SELECT COALESCE(coc_yeu_cau,tien_coc),CASE WHEN da_nhan_coc=1 THEN"
                        + " tien_coc ELSE 0 END FROM hop_dong_luu_tru WHERE id=?",
                id);
    }

    public List<Object[]> khoaHopDongThuCoc(long id) {

        return truyVan(
                "SELECT trang_thai,COALESCE(coc_yeu_cau,tien_coc),CASE WHEN"
                        + " da_nhan_coc=1 THEN tien_coc ELSE 0 END FROM"
                        + " hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                id);
    }

    public long demKhoaThuCoc(long id, String tienToKhoa) {

        return laySo(
                "SELECT COUNT(*) FROM khoan_thu WHERE hop_dong_id=? AND khoa" + " LIKE ?",
                id,
                tienToKhoa);
    }

    public int ghiNhanTongCoc(long tongCoc, long taiKhoan, long ca, long id) {

        return capNhat(
                "UPDATE hop_dong_luu_tru SET"
                    + " tien_coc=?,da_nhan_coc=1,nhan_coc_luc=SYSDATETIME(),nhan_coc_boi=?,phien_nhan_coc_id=?"
                    + " WHERE id=?",
                tongCoc,
                taiKhoan,
                ca,
                id);
    }

    public List<Object[]> khoaHopDongDoiCoc(long id) {

        return truyVan(
                "SELECT ngay_vao,ngay_ra_du_kien,COALESCE(gia_dem,(SELECT"
                        + " p.gia_dem FROM phong_hotel p WHERE"
                        + " p.id=h.phong_id)),CASE WHEN da_nhan_coc=1 THEN tien_coc"
                        + " ELSE 0 END,trang_thai FROM hop_dong_luu_tru h"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE id=?",
                id);
    }

    public int dieuChinhMucCoc(long muc, String lyDo, long id) {

        return capNhat(
                "UPDATE hop_dong_luu_tru SET coc_yeu_cau=?,ly_do_doi_coc=? WHERE id=?",
                muc,
                lyDo,
                id);
    }
}
