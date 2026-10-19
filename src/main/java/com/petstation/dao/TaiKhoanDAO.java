package com.petstation.dao;

import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/** Khai báo thao tác truy cập dữ liệu của người dùng tài khoản DAO. */
public interface TaiKhoanDAO {
    /**
     * Tìm theo tên đăng nhập trong TaiKhoanDAO.
     *
     * @param ketNoi kết nối
     * @param tenDangNhap tên đăng nhập
     * @return kết quả tìm theo tên đăng nhập
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    Optional<TaiKhoan> timTheoTenDangNhap(Connection ketNoi, String tenDangNhap) throws Exception;

    /**
     * Tìm các quyền trong TaiKhoanDAO.
     *
     * @param ketNoi kết nối
     * @param idTaiKhoan id tài khoản
     * @return kết quả tìm các quyền
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    Set<Quyen> timCacQuyen(Connection ketNoi, long idTaiKhoan) throws Exception;

    /**
     * Ghi nhận lần đăng nhập thất bại trong TaiKhoanDAO.
     *
     * @param ketNoi kết nối
     * @param idTaiKhoan id tài khoản
     * @param soLuongThatBai số lượng thất bại
     * @param khoaDenLuc khóa đến lúc
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    void ghiNhanDangNhapThatBai(
            Connection ketNoi, long idTaiKhoan, int soLuongThatBai, LocalDateTime khoaDenLuc)
            throws Exception;

    /**
     * Ghi nhận lần đăng nhập thành công trong TaiKhoanDAO.
     *
     * @param ketNoi kết nối
     * @param idTaiKhoan id tài khoản
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    void ghiNhanDangNhapThanhCong(Connection ketNoi, long idTaiKhoan) throws Exception;
}
