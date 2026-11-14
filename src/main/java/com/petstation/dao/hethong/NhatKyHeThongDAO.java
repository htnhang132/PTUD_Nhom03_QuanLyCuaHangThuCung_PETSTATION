package com.petstation.dao.hethong;

import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.model.hethong.NhatKyHeThong;

import java.sql.Connection;
import java.util.List;

public interface NhatKyHeThongDAO {
    void them(Connection ketNoi, NhatKyHeThong muc) throws Exception;

    int capNhatNhatKy(
            String maNhanVien, String hanhDong, String ghiChu, String loaiSuKien, String ketQua);

    List<DongNhatKyHeThong> timCacMuc();
}
