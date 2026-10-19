package com.petstation.dao;

import com.petstation.model.NhanVien;

import java.sql.Connection;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của nhân viên DAO. */
public interface NhanVienDAO {
    /**
     * Tìm theo id trong NhanVienDAO.
     *
     * @param ketNoi kết nối
     * @param idNhanVien id nhân viên
     * @return kết quả tìm theo id
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    Optional<NhanVien> timTheoId(Connection ketNoi, long idNhanVien) throws Exception;
}
