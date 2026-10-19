package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC Hotel DAO. */
public final class JdbcLuuTruDAO {
    /** Khởi tạo JdbcLuuTruDAO với các giá trị được cung cấp. */
    private JdbcLuuTruDAO() {}

    /**
     * Cập nhật nhận phòng trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhận phòng
     */
    public static int capNhatNhanPhong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE hop_dong_luu_tru SET trang_thai='CHECKED_IN' WHERE id=?", cacThamSo);
    }

    /**
     * Tìm đặt phòng trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm đặt phòng
     */
    public static List<Object[]> timDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,gia_dem,trang_thai,loai_phong FROM phong_hotel"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_phong=?",
                cacThamSo);
    }

    /**
     * Đếm đặt phòng trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm đặt phòng
     */
    public static long demDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (phong_id=? OR thu_cung_id=?) AND trang_thai IN"
                        + " ('BOOKED','CHECKED_IN') AND ngay_vao<? AND"
                        + " ngay_ra_du_kien>?",
                cacThamSo);
    }

    /**
     * Thêm đặt phòng trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm đặt phòng
     */
    public static long themDatPhong(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu)"
                    + " VALUES(?,?,?,?,?,?,?,'BOOKED',?,?)",
                cacThamSo);
    }

    /**
     * Lấy nhận phòng trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy nhận phòng
     */
    public static Object layNhanPhong(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT phong_id FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND trang_thai='BOOKED'",
                cacThamSo);
    }

    /**
     * Cập nhật nhận phòng 2 trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhận phòng 2
     */
    public static int capNhatNhanPhong2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel WITH(UPDLOCK,HOLDLOCK) SET"
                        + " trang_thai='OCCUPIED' WHERE id=? AND"
                        + " trang_thai='AVAILABLE'",
                cacThamSo);
    }

    /**
     * Đếm nhật ký trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm nhật ký
     */
    public static long demNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hop_dong_luu_tru WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND trang_thai='CHECKED_IN'",
                cacThamSo);
    }

    /**
     * Cập nhật nhật ký trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhật ký
     */
    public static int capNhatNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,an_uong,ve_sinh,tinh_trang,thoi_gian)"
                    + " VALUES(?,?,?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    /**
     * Tìm Hotel còn phải thu trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm Hotel còn phải thu
     */
    public static List<Object[]> timHotelConPhaiThu(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                        + " h.ngay_vao,h.ngay_ra_du_kien,COALESCE(h.gia_dem,p.gia_dem),h.tien_coc"
                        + " FROM hop_dong_luu_tru h WITH(UPDLOCK,HOLDLOCK) JOIN phong_hotel p"
                        + " ON p.id=h.phong_id WHERE h.id=? AND h.trang_thai='CHECKED_IN' AND"
                        + " h.hoa_don_id IS NULL",
                cacThamSo);
    }

    /**
     * Ghi sự kiện vào nhật ký hệ thống chi tiết trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy nhật ký chi tiết
     */
    public static List<Object[]> layNhatKyChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT j.thoi_gian,n.ho_ten,j.an_uong,j.ve_sinh,j.tinh_trang FROM"
                        + " nhat_ky_cham_soc j JOIN nhan_vien n ON n.id=j.nhan_vien_id WHERE"
                        + " j.hop_dong_id=? ORDER BY j.thoi_gian DESC",
                cacThamSo);
    }

    /**
     * Đánh dấu phòng khả dụng trong JdbcLuuTruDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đánh dấu phòng khả dụng
     */
    public static int danhDauPhongKhaDung(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phong_hotel SET trang_thai='AVAILABLE'"
                        + " WHERE id=? AND trang_thai='CLEANING'",
                cacThamSo);
    }
}
