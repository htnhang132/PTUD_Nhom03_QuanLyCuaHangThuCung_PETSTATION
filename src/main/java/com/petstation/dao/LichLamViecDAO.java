package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho LichLamViec; service gọi qua hợp đồng này. */
public interface LichLamViecDAO {
    List<Object[]> timKhoang(Object... cacThamSo);

    long demPhanCong(Object... cacThamSo);

    long demNhanVienDangLam(Object... cacThamSo);

    long demLichTrungNgayCa(Object... cacThamSo);

    int capNhatPhanCong(Object... cacThamSo);

    int themLichLamViec(Object... cacThamSo);

    int capNhatXoa(Object... cacThamSo);

    int capNhatDanhDauNghiPhepDaDuyet(Object... cacThamSo);

    int themLichNghiPhep(Object... cacThamSo);

    long demThangSoGio(Object... cacThamSo);
}
