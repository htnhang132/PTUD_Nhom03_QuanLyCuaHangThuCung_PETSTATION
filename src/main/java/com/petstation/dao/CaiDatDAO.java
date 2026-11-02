package com.petstation.dao;

/** Thao tác dữ liệu cho CaiDat; service gọi qua hợp đồng này. */
public interface CaiDatDAO {
    Object timGiaTri(Object... cacThamSo);

    int themGiaTri(Object... cacThamSo);

    int capNhatGiaTri(Object... cacThamSo);
}
