package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho Kho; service gọi qua hợp đồng này. */
public interface KhoDAO {
    int capNhatNhapKho(Object... cacThamSo);

    int capNhatGiaNhapGanNhat(Object... cacThamSo);

    List<Object[]> timApDungXuatHuy(Object... cacThamSo);

    int capNhatApDungXuatHuy(Object... cacThamSo);

    int capNhatApDungKiemKe(Object... cacThamSo);

    int danhDauKiemKeDaDuyet(Object... cacThamSo);

    long demNhapKho(Object... cacThamSo);

    long demNhaCungCapDangHopTac(Object... cacThamSo);

    long themNhapKho(Object... cacThamSo);

    int themChiTietNhapKho(Object... cacThamSo);

    List<Object[]> timNhapKho(Object... cacThamSo);

    int themLoHang(Object... cacThamSo);

    List<Object[]> timXuatHuy(Object... cacThamSo);

    long themXuatHuy(Object... cacThamSo);

    int capNhatXuatHuy(Object... cacThamSo);

    long demApDungXuatHuy(Object... cacThamSo);

    int truTonXuatHuy(Object... cacThamSo);

    List<Object[]> timKiemKe(Object... cacThamSo);

    long themKiemKe(Object... cacThamSo);

    int capNhatKiemKe(Object... cacThamSo);

    long demApDungKiemKe(Object... cacThamSo);

    List<Object[]> timApDungKiemKe(Object... cacThamSo);

    int themThanhToanNhaCungCap(Object... cacThamSo);

    List<Object[]> layNhapKhoChiTiet(Object... cacThamSo);
}
