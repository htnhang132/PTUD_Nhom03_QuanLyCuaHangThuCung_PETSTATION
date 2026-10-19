package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC Spa DAO. */
public final class JdbcXuLySpaDAO {
    /** Khởi tạo JdbcXuLySpaDAO với các giá trị được cung cấp. */
    private JdbcXuLySpaDAO() {}

    /**
     * Cập nhật tiếp nhận trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật tiếp nhận
     */
    public static int capNhatTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='CHECKED_IN' WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật tiếp nhận 2 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật tiếp nhận 2
     */
    public static int capNhatTiepNhan2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE thu_cung SET can_nang=?,tinh_trang_suc_khoe=? WHERE id=?", cacThamSo);
    }

    /**
     * Đếm trạng thái Spa trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm trạng thái Spa
     */
    public static long demTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM chi_tiet_phieu_spa WHERE phieu_spa_id=?", cacThamSo);
    }

    /**
     * Đếm lịch hẹn trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm lịch hẹn
     */
    public static long demLichHen(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE id=? AND" + " trang_thai='ACTIVE'",
                cacThamSo);
    }

    /**
     * Đếm lịch hẹn 2 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm lịch hẹn 2
     */
    public static long demLichHen2(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " dang_lam_viec=1",
                cacThamSo);
    }

    /**
     * Đếm lịch hẹn 3 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm lịch hẹn 3
     */
    public static long demLichHen3(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE" + " id=?", cacThamSo);
    }

    /**
     * Đếm lịch hẹn 4 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm lịch hẹn 4
     */
    public static long demLichHen4(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (nhan_vien_id=? OR thu_cung_id=?) AND trang_thai NOT IN"
                        + " ('CANCELLED','NO_SHOW','COMPLETED') AND bat_dau<? AND"
                        + " ket_thuc>?",
                cacThamSo);
    }

    /**
     * Thêm lịch hẹn trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm lịch hẹn
     */
    public static long themLichHen(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id)"
                    + " VALUES(?,?,?,'SPA',?,?,'SCHEDULED',?,?)",
                cacThamSo);
    }

    /**
     * Tìm tiếp nhận trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm tiếp nhận
     */
    public static List<Object[]> timTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,nhan_vien_id FROM lich_hen"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_lich_hen=? AND"
                        + " thu_cung_id=? AND trang_thai='SCHEDULED'",
                cacThamSo);
    }

    /**
     * Thêm tiếp nhận trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm tiếp nhận
     */
    public static long themTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,nhan_vien_id,ghi_chu,trang_thai,tao_luc)"
                    + " VALUES(?,?,?,?,?,?,'RECEIVED',SYSDATETIME())",
                cacThamSo);
    }

    /**
     * Lấy tiếp nhận trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy tiếp nhận
     */
    public static Object layTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT g.ma_goi FROM lich_hen l JOIN goi_dich_vu_spa g ON"
                        + " g.id=l.dich_vu_id WHERE l.id=?",
                cacThamSo);
    }

    /**
     * Tìm thêm dịch vụ Spa trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm thêm dịch vụ Spa
     */
    public static List<Object[]> timThemDichVuSpa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT t.can_nang_thuc_te,p.loai,t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t WITH(UPDLOCK,HOLDLOCK) JOIN"
                        + " thu_cung p ON p.id=t.thu_cung_id WHERE t.id=?",
                cacThamSo);
    }

    /**
     * Tìm thêm dịch vụ Spa 2 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm thêm dịch vụ Spa 2
     */
    public static List<Object[]> timThemDichVuSpa2(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(1) b.goi_id,b.don_gia,g.ten_goi FROM bang_gia_spa b"
                        + " JOIN goi_dich_vu_spa g ON g.id=b.goi_id WHERE"
                        + " g.ma_goi=? AND g.trang_thai='ACTIVE' AND"
                        + " b.loai_thu_cung=? AND b.can_nang_tu<=? AND"
                        + " (b.can_nang_den IS NULL OR b.can_nang_den>?) ORDER BY"
                        + " b.can_nang_tu DESC,b.id",
                cacThamSo);
    }

    /**
     * Cập nhật thêm dịch vụ Spa trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật thêm dịch vụ Spa
     */
    public static int capNhatThemDichVuSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong)"
                        + " VALUES(?,?,?,?,1)",
                cacThamSo);
    }

    /**
     * Cập nhật thêm dịch vụ Spa 2 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật thêm dịch vụ Spa 2
     */
    public static int capNhatThemDichVuSpa2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=(SELECT"
                        + " COALESCE(SUM(thanh_tien),0) FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=?) WHERE id=?",
                cacThamSo);
    }

    /**
     * Cập nhật trạng thái Spa trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật trạng thái Spa
     */
    public static int capNhatTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE id=? AND" + " trang_thai=?",
                cacThamSo);
    }

    /**
     * Cập nhật Spa trạng thái 2 trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật Spa trạng thái 2
     */
    public static int capNhatSpaTrangThai2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='COMPLETED' WHERE id=(SELECT"
                        + " lich_hen_id FROM phieu_tiep_nhan_spa WHERE id=?)",
                cacThamSo);
    }

    /**
     * Hủy lịch hẹn bản ghi trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả hủy lịch hẹn bản ghi
     */
    public static int huyLichHenBanGhi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='CANCELLED' WHERE id=? AND"
                        + " trang_thai='SCHEDULED'",
                cacThamSo);
    }

    /**
     * Lấy mã lịch hẹn thú cưng trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy mã lịch hẹn thú cưng
     */
    public static Object layMaLichHenThuCung(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.id=l.thu_cung_id WHERE l.id=?",
                cacThamSo);
    }

    /**
     * Lấy phiếu Spa chi tiết trong JdbcXuLySpaDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy phiếu Spa chi tiết
     */
    public static List<Object[]> layPhieuSpaChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT mo_ta,don_gia,so_luong,thanh_tien FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=? ORDER BY id",
                cacThamSo);
    }
}
