package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC kho DAO. */
public final class JdbcKhoDAO {
    /** Khởi tạo JdbcKhoDAO với các giá trị được cung cấp. */
    private JdbcKhoDAO() {}

    /**
     * Cập nhật nhập kho trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhập kho
     */
    public static int capNhatNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật nhập kho 2 trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhập kho 2
     */
    public static int capNhatNhapKho2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE san_pham SET gia_nhap_gan_nhat=? WHERE id=?", cacThamSo);
    }

    /**
     * Tìm áp dụng xuất hủy trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm áp dụng xuất hủy
     */
    public static List<Object[]> timApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT lo_hang_id,so_luong FROM chi_tiet_phieu_xuat WHERE phieu_xuat_id=?",
                cacThamSo);
    }

    /**
     * Cập nhật áp dụng xuất hủy trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng xuất hủy
     */
    public static int capNhatApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='POSTED' WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật áp dụng kiểm kê trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng kiểm kê
     */
    public static int capNhatApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=? WHERE id=? AND so_luong_ton=?", cacThamSo);
    }

    /**
     * Cập nhật áp dụng kiểm kê 2 trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng kiểm kê 2
     */
    public static int capNhatApDungKiemKe2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='APPROVED' WHERE id=?", cacThamSo);
    }

    /**
     * Đếm nhập kho trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm nhập kho
     */
    public static long demNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND theo_doi_hsd=1",
                cacThamSo);
    }

    /**
     * Đếm nhập kho 2 trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm nhập kho 2
     */
    public static long demNhapKho2(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nha_cung_cap WHERE id=? AND" + " trang_thai='COOPERATING'",
                cacThamSo);
    }

    /**
     * Thêm nhập kho trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm nhập kho
     */
    public static long themNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_nhap_kho(so_phieu,nha_cung_cap_id,nguoi_lap_id,ngay_nhap,tong_tien,trang_thai,da_tra)"
                    + " VALUES(?,?,?,CAST(GETDATE() AS DATE),?,'POSTED',?)",
                cacThamSo);
    }

    /**
     * Cập nhật nhập kho 3 trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhập kho 3
     */
    public static int capNhatNhapKho3(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_phieu_nhap(phieu_nhap_id,san_pham_id,so_lo,han_su_dung,so_luong,gia_nhap)"
                    + " VALUES(?,?,?,?,?,?)",
                cacThamSo);
    }

    /**
     * Tìm nhập kho trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm nhập kho
     */
    public static List<Object[]> timNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,han_su_dung FROM lo_hang WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE san_pham_id=? AND so_lo=?",
                cacThamSo);
    }

    /**
     * Cập nhật nhập kho 4 trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhập kho 4
     */
    public static int capNhatNhapKho4(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                        + " VALUES(?,?,?,?)",
                cacThamSo);
    }

    /**
     * Tìm xuất hủy trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm xuất hủy
     */
    public static List<Object[]> timXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.so_luong_ton,COALESCE(p.gia_nhap_gan_nhat,p.gia_ban)"
                        + " FROM lo_hang l WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p"
                        + " ON p.id=l.san_pham_id WHERE l.id=?",
                cacThamSo);
    }

    /**
     * Thêm xuất hủy trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm xuất hủy
     */
    public static long themXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_xuat_kho(so_phieu,nguoi_lap_id,loai_xuat,ly_do,tong_gia_tri,trang_thai)"
                    + " VALUES(?,?,'WRITE_OFF',?,?,'DRAFT')",
                cacThamSo);
    }

    /**
     * Cập nhật xuất hủy trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật xuất hủy
     */
    public static int capNhatXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                        + " chi_tiet_phieu_xuat(phieu_xuat_id,lo_hang_id,so_luong,gia_tri)"
                        + " VALUES(?,?,?,?)",
                cacThamSo);
    }

    /**
     * Đếm áp dụng xuất hủy trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm áp dụng xuất hủy
     */
    public static long demApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_xuat_kho WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='DRAFT'",
                cacThamSo);
    }

    /**
     * Cập nhật áp dụng xuất hủy 2 trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật áp dụng xuất hủy 2
     */
    public static int capNhatApDungXuatHuy2(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-? WHERE id=? AND"
                        + " so_luong_ton>=?",
                cacThamSo);
    }

    /**
     * Tìm kiểm kê trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm kiểm kê
     */
    public static List<Object[]> timKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE" + " id=?",
                cacThamSo);
    }

    /**
     * Thêm kiểm kê trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm kiểm kê
     */
    public static long themKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                        + " bien_ban_kiem_ke(so_bien_ban,nguoi_lap_id,ky_kiem_ke,trang_thai)"
                        + " VALUES(?,?,?,?)",
                cacThamSo);
    }

    /**
     * Cập nhật kiểm kê trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật kiểm kê
     */
    public static int capNhatKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " chi_tiet_kiem_ke(bien_ban_id,lo_hang_id,ton_he_thong,ton_thuc_te,nguyen_nhan)"
                    + " VALUES(?,?,?,?,?)",
                cacThamSo);
    }

    /**
     * Đếm áp dụng kiểm kê trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm áp dụng kiểm kê
     */
    public static long demApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM bien_ban_kiem_ke WITH(UPDLOCK,HOLDLOCK) WHERE id=?"
                        + " AND trang_thai='PENDING'",
                cacThamSo);
    }

    /**
     * Tìm áp dụng kiểm kê trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm áp dụng kiểm kê
     */
    public static List<Object[]> timApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT lo_hang_id,ton_he_thong,ton_thuc_te FROM chi_tiet_kiem_ke WHERE"
                        + " bien_ban_id=?",
                cacThamSo);
    }

    /**
     * Thêm thanh toán nhà cung cấp trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm thanh toán nhà cung cấp
     */
    public static int themThanhToanNhaCungCap(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_nhap_kho SET da_tra=da_tra+? WHERE id=? AND"
                        + " da_tra+?<=tong_tien AND trang_thai='POSTED'",
                cacThamSo);
    }

    /**
     * Lấy nhập kho chi tiết trong JdbcKhoDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy nhập kho chi tiết
     */
    public static List<Object[]> layNhapKhoChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ten,c.so_lo,c.han_su_dung,c.so_luong,c.gia_nhap FROM"
                        + " chi_tiet_phieu_nhap c JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                        + " c.phieu_nhap_id=? ORDER BY c.id",
                cacThamSo);
    }
}
