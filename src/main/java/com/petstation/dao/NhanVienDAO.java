package com.petstation.dao;

import com.petstation.model.NhanVien;

import java.sql.Connection;
import java.util.Optional;

/** Thao tác dữ liệu cho NhanVien; service gọi qua hợp đồng này. */
public interface NhanVienDAO {
    Optional<NhanVien> timTheoId(Connection ketNoi, long idNhanVien) throws Exception;
}
