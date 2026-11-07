package com.petstation.dao;

import com.petstation.model.NhanVien;

import java.sql.Connection;
import java.util.Optional;

public interface NhanVienDAO {
    Optional<NhanVien> timTheoId(Connection ketNoi, String maNhanVien) throws Exception;

    Optional<NhanVien> timTheoMa(Connection ketNoi, String maNhanVien) throws Exception;
}
