package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.XuLySpaDAO;
import com.petstation.database.ThaoTacSQL;

import java.time.LocalDateTime;
import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC Spa DAO. */
public final class XuLySpaImpl implements XuLySpaDAO {
    public XuLySpaImpl() {}

    public int capNhatTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='CHECKED_IN' WHERE id=?", cacThamSo);
    }

    public int capNhatSucKhoeThuCung(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE thu_cung SET can_nang=?,tinh_trang_suc_khoe=? WHERE id=?", cacThamSo);
    }

    public long demTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM chi_tiet_phieu_spa WHERE phieu_spa_id=?", cacThamSo);
    }

    public long demLichHen(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE id=? AND" + " trang_thai='ACTIVE'",
                cacThamSo);
    }

    public long khoaNhanVienDangLam(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " dang_lam_viec=1",
                cacThamSo);
    }

    public long layThoiLuongDichVu(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE" + " id=?", cacThamSo);
    }

    public long demLichTrung(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (nhan_vien_id=? OR thu_cung_id=?) AND trang_thai NOT IN"
                        + " ('CANCELLED','NO_SHOW','COMPLETED') AND bat_dau<? AND"
                        + " ket_thuc>?",
                cacThamSo);
    }

    public long themLichHen(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id)"
                    + " VALUES(?,?,?,'SPA',?,?,'SCHEDULED',?,?)",
                cacThamSo);
    }

    public List<Object[]> timTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,nhan_vien_id FROM lich_hen"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_lich_hen=? AND"
                        + " thu_cung_id=? AND trang_thai='SCHEDULED'",
                cacThamSo);
    }

    public long themTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,nhan_vien_id,ghi_chu,trang_thai,tao_luc)"
                    + " VALUES(?,?,?,?,?,?,'RECEIVED',SYSDATETIME())",
                cacThamSo);
    }

    public Object layTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT g.ma_goi FROM lich_hen l JOIN goi_dich_vu_spa g ON"
                        + " g.id=l.dich_vu_id WHERE l.id=?",
                cacThamSo);
    }

    public List<Object[]> timThemDichVuSpa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT t.can_nang_thuc_te,p.loai,t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t WITH(UPDLOCK,HOLDLOCK) JOIN"
                        + " thu_cung p ON p.id=t.thu_cung_id WHERE t.id=?",
                cacThamSo);
    }

    public List<Object[]> layGiaDichVuTheoCanNang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(1) b.goi_id,b.don_gia,g.ten_goi FROM bang_gia_spa b"
                        + " JOIN goi_dich_vu_spa g ON g.id=b.goi_id WHERE"
                        + " g.ma_goi=? AND g.trang_thai='ACTIVE' AND"
                        + " b.loai_thu_cung=? AND b.can_nang_tu<=? AND"
                        + " (b.can_nang_den IS NULL OR b.can_nang_den>?) ORDER BY"
                        + " b.can_nang_tu DESC,b.id",
                cacThamSo);
    }

    public int capNhatThemDichVuSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong)"
                        + " VALUES(?,?,?,?,1)",
                cacThamSo);
    }

    public int tinhLaiTongPhieuSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=(SELECT"
                        + " COALESCE(SUM(thanh_tien),0) FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=?) WHERE id=?",
                cacThamSo);
    }

    public int capNhatTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE id=? AND" + " trang_thai=?",
                cacThamSo);
    }

    public int hoanTatLichHen(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='COMPLETED' WHERE id=(SELECT"
                        + " lich_hen_id FROM phieu_tiep_nhan_spa WHERE id=?)",
                cacThamSo);
    }

    public int huyLichHenBanGhi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='CANCELLED' WHERE id=? AND"
                        + " trang_thai='SCHEDULED'",
                cacThamSo);
    }

    public Object layMaLichHenThuCung(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.id=l.thu_cung_id WHERE l.id=?",
                cacThamSo);
    }

    public List<Object[]> layPhieuSpaChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT mo_ta,don_gia,so_luong,thanh_tien FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=? ORDER BY id",
                cacThamSo);
    }

    public List<Object[]> layChiTietLichHen(long id) {

        return truyVan(
                "SELECT p.ma_thu_cung,g.ma_goi,n.ma_nhan_vien,l.bat_dau FROM lich_hen l"
                        + " JOIN thu_cung p ON p.id=l.thu_cung_id LEFT JOIN goi_dich_vu_spa g"
                        + " ON g.id=l.dich_vu_id LEFT JOIN nhan_vien n ON n.id=l.nhan_vien_id"
                        + " WHERE l.id=?",
                id);
    }

    public List<Object[]> khoaLichHen(long id) {

        return truyVan(
                "SELECT trang_thai FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE" + " id=?", id);
    }

    public long demLichTrungKhiSua(
            long id, long nhanVien, Object thuCung, LocalDateTime ketThuc, LocalDateTime batDau) {

        return laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " id<>? AND (nhan_vien_id=? OR thu_cung_id=?) AND"
                        + " trang_thai NOT IN ('CANCELLED','NO_SHOW','COMPLETED')"
                        + " AND bat_dau<? AND ket_thuc>?",
                id,
                nhanVien,
                thuCung,
                ketThuc,
                batDau);
    }

    public int suaLichHen(
            Object khachHang,
            Object thuCung,
            long goi,
            long nhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long id) {

        return capNhat(
                "UPDATE lich_hen SET"
                    + " khach_hang_id=?,thu_cung_id=?,dich_vu_id=?,nhan_vien_id=?,bat_dau=?,ket_thuc=?"
                    + " WHERE id=?",
                khachHang,
                thuCung,
                goi,
                nhanVien,
                batDau,
                ketThuc,
                id);
    }
}
