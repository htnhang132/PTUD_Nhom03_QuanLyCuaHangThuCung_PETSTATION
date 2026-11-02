package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho DoiTra; service gọi qua hợp đồng này. */
public interface PhieuDoiTraDAO {
    int capNhatApDungDoiTra(Object... cacThamSo);

    long demDoiTraMuc(Object... cacThamSo);

    List<Object[]> laySoLuongLoConDuocDoi(Object... cacThamSo);

    int capNhatDoiTraMuc(Object... cacThamSo);
}
