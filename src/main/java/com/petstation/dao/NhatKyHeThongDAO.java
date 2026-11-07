package com.petstation.dao;

import com.petstation.model.NhatKyHeThong;

import java.sql.Connection;
import java.util.List;

public interface NhatKyHeThongDAO {
    void them(Connection ketNoi, NhatKyHeThong muc) throws Exception;

    int capNhatNhatKy(Object... cacThamSo);

    List<Object[]> timCacMuc(Object... cacThamSo);
}
