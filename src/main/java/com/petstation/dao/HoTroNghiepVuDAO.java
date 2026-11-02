package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho HoTroNghiepVu; service gọi qua hợp đồng này. */
public interface HoTroNghiepVuDAO {
    List<Object[]> timThuCung(Object... cacThamSo);
}
