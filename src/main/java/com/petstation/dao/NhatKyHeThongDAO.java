package com.petstation.dao;

import com.petstation.model.NhatKyHeThong;

import java.sql.Connection;

/** Khai báo thao tác truy cập dữ liệu của nhật ký hệ thống nhật ký DAO. */
public interface NhatKyHeThongDAO {
    /**
     * Thêm trong NhatKyHeThongDAO.
     *
     * @param ketNoi kết nối
     * @param muc mục
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    void them(Connection ketNoi, NhatKyHeThong muc) throws Exception;
}
