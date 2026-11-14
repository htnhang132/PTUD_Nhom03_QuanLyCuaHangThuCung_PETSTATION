package com.petstation.dao.hethong;

import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.TaiKhoan;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaiKhoanDAO {
    Optional<TaiKhoan> timTheoTenDangNhap(Connection ketNoi, String tenDangNhap) throws Exception;

    Set<Quyen> timCacQuyen(Connection ketNoi, String tenDangNhap) throws Exception;

    List<String> timCua(String maVaiTro);

    void ghiNhanDangNhapThatBai(
            Connection ketNoi, String tenDangNhap, int soLuongThatBai, LocalDateTime khoaDenLuc)
            throws Exception;

    void ghiNhanDangNhapThanhCong(Connection ketNoi, String tenDangNhap) throws Exception;

    String layMaBamMatKhau(String tenDangNhap);

    int capNhatMatKhau(String matKhauMoi, String tenDangNhap);
}
