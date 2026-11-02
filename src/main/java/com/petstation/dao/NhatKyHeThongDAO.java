package com.petstation.dao;

import com.petstation.model.NhatKyHeThong;

import java.sql.Connection;
import java.util.List;

/** Thao tác dữ liệu cho NhatKyHeThong; service gọi qua hợp đồng này. */
public interface NhatKyHeThongDAO {
    void them(Connection ketNoi, NhatKyHeThong muc) throws Exception;

    int capNhatNhatKy(Object... cacThamSo);

    List<Object[]> timCacMuc(Object... cacThamSo);
}
