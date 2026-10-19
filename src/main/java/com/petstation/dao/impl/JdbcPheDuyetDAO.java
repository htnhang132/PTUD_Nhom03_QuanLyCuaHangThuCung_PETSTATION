package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC phê duyệt DAO. */
public final class JdbcPheDuyetDAO {
    /** Khởi tạo JdbcPheDuyetDAO với các giá trị được cung cấp. */
    private JdbcPheDuyetDAO() {}

    /**
     * Đếm số lượng đang chờ trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm số lượng đang chờ
     */
    public static long demSoLuongDangCho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM yeu_cau_phe_duyet WHERE trang_thai='PENDING'", cacThamSo);
    }

    /**
     * Cập nhật từ chối trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật từ chối
     */
    public static int capNhatTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='REJECTED' WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật từ chối 2 trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật từ chối 2
     */
    public static int capNhatTuChoi2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='REJECTED' WHERE id=?", cacThamSo);
    }

    /**
     * Đếm áp dụng phê duyệt trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm áp dụng phê duyệt
     */
    public static long demApDungPheDuyet(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE id=? AND trang_thai='PAID'", cacThamSo);
    }

    /**
     * Cập nhật áp dụng phê duyệt trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng phê duyệt
     */
    public static int capNhatApDungPheDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE hoa_don SET ghi_chu=? WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật gửi thao tác trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật gửi thao tác
     */
    public static int capNhatGuiThaoTac(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload,gui_luc)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    /**
     * Tìm tất cả trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm tất cả
     */
    public static List<Object[]> timTatCa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " y.ma_yeu_cau,y.loai,n.ho_ten,y.gui_luc,y.ly_do,y.noi_dung_truoc,y.noi_dung_sau,y.trang_thai,COALESCE(a.ho_ten,N''),COALESCE(y.phan_hoi,N'')"
                    + " FROM yeu_cau_phe_duyet y JOIN tai_khoan t ON t.id=y.nguoi_gui_id JOIN"
                    + " nhan_vien n ON n.id=t.nhan_vien_id LEFT JOIN tai_khoan u ON"
                    + " u.id=y.nguoi_xu_ly_id LEFT JOIN nhan_vien a ON a.id=u.nhan_vien_id ORDER BY"
                    + " y.gui_luc DESC,y.id DESC",
                cacThamSo);
    }

    /**
     * Tìm duyệt trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm duyệt
     */
    public static List<Object[]> timDuyet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT loai_doi_tuong,ma_doi_tuong,payload FROM"
                        + " yeu_cau_phe_duyet WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_yeu_cau=? AND trang_thai='PENDING'",
                cacThamSo);
    }

    /**
     * Cập nhật duyệt trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật duyệt
     */
    public static int capNhatDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                        + " trang_thai='APPROVED',nguoi_xu_ly_id=?,xu_ly_luc=SYSDATETIME()"
                        + " WHERE ma_yeu_cau=?",
                cacThamSo);
    }

    /**
     * Tìm từ chối trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm từ chối
     */
    public static List<Object[]> timTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT loai_doi_tuong,ma_doi_tuong FROM yeu_cau_phe_duyet"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_yeu_cau=? AND"
                        + " trang_thai='PENDING'",
                cacThamSo);
    }

    /**
     * Cập nhật từ chối 3 trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật từ chối 3
     */
    public static int capNhatTuChoi3(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_doi_tra SET trang_thai='REJECTED' WHERE id=? AND"
                        + " trang_thai='PENDING'",
                cacThamSo);
    }

    /**
     * Cập nhật từ chối 4 trong JdbcPheDuyetDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật từ chối 4
     */
    public static int capNhatTuChoi4(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                    + " trang_thai='REJECTED',nguoi_xu_ly_id=?,phan_hoi=?,xu_ly_luc=SYSDATETIME()"
                    + " WHERE ma_yeu_cau=?",
                cacThamSo);
    }
}
