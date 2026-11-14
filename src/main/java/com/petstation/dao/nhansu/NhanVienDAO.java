package com.petstation.dao.nhansu;

import com.petstation.model.nhansu.NhanVien;

import java.sql.Connection;
import java.util.Optional;

public interface NhanVienDAO {
    Optional<NhanVien> timTheoId(Connection ketNoi, String maNhanVien) throws Exception;

    Optional<NhanVien> timTheoMa(Connection ketNoi, String maNhanVien) throws Exception;
}
