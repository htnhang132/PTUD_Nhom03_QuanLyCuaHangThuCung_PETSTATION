package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho QuyenVaiTro; service gọi qua hợp đồng này. */
public interface QuyenVaiTroDAO {
    List<Object[]> timCua(Object... cacThamSo);
}
