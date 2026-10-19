package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC đổi trả DAO. */
public final class JdbcDoiTraDAO {
    /** Khởi tạo JdbcDoiTraDAO với các giá trị được cung cấp. */
    private JdbcDoiTraDAO() {}

    /**
     * Tìm áp dụng đổi trả trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm áp dụng đổi trả
     */
    public static List<Object[]> timApDungDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT lo_hang_id,so_luong FROM chi_tiet_doi_tra WHERE phieu_doi_tra_id=?",
                cacThamSo);
    }

    /**
     * Cập nhật áp dụng đổi trả trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng đổi trả
     */
    public static int capNhatApDungDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật áp dụng đổi trả 2 trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng đổi trả 2
     */
    public static int capNhatApDungDoiTra2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_doi_tra SET trang_thai='COMPLETED' WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật áp dụng đổi trả 3 trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng đổi trả 3
     */
    public static int capNhatApDungDoiTra3(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE khach_hang SET diem_tich_luy=? WHERE id=?", cacThamSo);
    }

    /**
     * Tìm đổi trả mục trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm đổi trả mục
     */
    public static List<Object[]> timDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " h.id,h.tong_tien,h.tam_tinh,h.thanh_toan_luc,i.don_gia,i.so_luong,h.trang_thai"
                    + " FROM chi_tiet_don_hang i WITH(UPDLOCK,HOLDLOCK) JOIN hoa_don h ON"
                    + " h.don_hang_id=i.don_hang_id WHERE i.id=? AND i.san_pham_id IS NOT NULL",
                cacThamSo);
    }

    /**
     * Đếm đổi trả mục trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm đổi trả mục
     */
    public static long demDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(c.so_luong),0) FROM chi_tiet_doi_tra c"
                        + " JOIN phieu_doi_tra p ON p.id=c.phieu_doi_tra_id WHERE"
                        + " c.chi_tiet_don_hang_id=? AND p.trang_thai IN"
                        + " ('PENDING','COMPLETED')",
                cacThamSo);
    }

    /**
     * Thêm đổi trả mục trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm đổi trả mục
     */
    public static long themDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_doi_tra(ma_phieu,hoa_don_id,nguoi_lap_id,chenh_lech,trang_thai,ly_do)"
                    + " VALUES(?,?,?,?,'PENDING',?)",
                cacThamSo);
    }

    /**
     * Tìm đổi trả mục 2 trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm đổi trả mục 2
     */
    public static List<Object[]> timDoiTraMuc2(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT b.lo_hang_id,b.so_luong-COALESCE((SELECT"
                        + " SUM(c.so_luong) FROM chi_tiet_doi_tra c JOIN"
                        + " phieu_doi_tra p ON p.id=c.phieu_doi_tra_id WHERE"
                        + " c.chi_tiet_don_hang_id=b.chi_tiet_don_hang_id AND"
                        + " c.lo_hang_id=b.lo_hang_id AND p.trang_thai IN"
                        + " ('PENDING','COMPLETED')),0) FROM phan_bo_lo_hang b"
                        + " WHERE b.chi_tiet_don_hang_id=? ORDER BY b.id",
                cacThamSo);
    }

    /**
     * Cập nhật đổi trả mục trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật đổi trả mục
     */
    public static int capNhatDoiTraMuc(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_doi_tra(phieu_doi_tra_id,chi_tiet_don_hang_id,lo_hang_id,so_luong,tien_hoan)"
                    + " VALUES(?,?,?,?,?)",
                cacThamSo);
    }

    /**
     * Đếm áp dụng đổi trả trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm áp dụng đổi trả
     */
    public static long demApDungDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_doi_tra WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='PENDING'",
                cacThamSo);
    }

    /**
     * Tìm áp dụng đổi trả 2 trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm áp dụng đổi trả 2
     */
    public static List<Object[]> timApDungDoiTra2(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                        + " h.id,h.diem_cong,h.diem_dung,h.tong_tien,d.khach_hang_id,p.chenh_lech"
                        + " FROM phieu_doi_tra p JOIN hoa_don h WITH(UPDLOCK,HOLDLOCK)"
                        + " ON h.id=p.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id"
                        + " WHERE p.id=? AND h.trang_thai='PAID'",
                cacThamSo);
    }

    /**
     * Đếm áp dụng đổi trả 2 trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm áp dụng đổi trả 2
     */
    public static long demApDungDoiTra2(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK) WHERE" + " id=?",
                cacThamSo);
    }

    /**
     * Cập nhật áp dụng đổi trả 4 trong JdbcDoiTraDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng đổi trả 4
     */
    public static int capNhatApDungDoiTra4(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do)"
                        + " VALUES(?,?,?,?,N'Đổi trả hàng')",
                cacThamSo);
    }
}
