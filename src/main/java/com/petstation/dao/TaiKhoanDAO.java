package com.petstation.dao;

import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/** Thao tác dữ liệu cho TaiKhoan; service gọi qua hợp đồng này. */
public interface TaiKhoanDAO {
    Optional<TaiKhoan> timTheoTenDangNhap(Connection ketNoi, String tenDangNhap) throws Exception;

    Set<Quyen> timCacQuyen(Connection ketNoi, long idTaiKhoan) throws Exception;

    void ghiNhanDangNhapThatBai(
            Connection ketNoi, long idTaiKhoan, int soLuongThatBai, LocalDateTime khoaDenLuc)
            throws Exception;

    void ghiNhanDangNhapThanhCong(Connection ketNoi, long idTaiKhoan) throws Exception;

    Object layMaBamMatKhau(Object... cacThamSo);

    int capNhatMatKhau(Object... cacThamSo);
}
