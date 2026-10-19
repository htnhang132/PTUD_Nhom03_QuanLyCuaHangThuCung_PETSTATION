package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

/** Thực hiện truy vấn JDBC cho JDBC tài khoản DAO. */
public final class JdbcQuanLyTaiKhoanDAO {
    /** Khởi tạo JdbcQuanLyTaiKhoanDAO với các giá trị được cung cấp. */
    private JdbcQuanLyTaiKhoanDAO() {}

    /**
     * Lấy mã băm mật khẩu trong JdbcQuanLyTaiKhoanDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả lấy mã băm mật khẩu
     */
    public static Object layMaBamMatKhau(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT mat_khau_hash FROM tai_khoan WITH(UPDLOCK,HOLDLOCK) WHERE id=?", cacThamSo);
    }

    /**
     * Cập nhật mật khẩu trong JdbcQuanLyTaiKhoanDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật mật khẩu
     */
    public static int capNhatMatKhau(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE tai_khoan SET mat_khau_hash=?,bat_buoc_doi_mat_khau=0 WHERE id=?",
                cacThamSo);
    }
}
