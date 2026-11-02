package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho LichSuKhachHang; service gọi qua hợp đồng này. */
public interface LichSuKhachHangDAO {
    List<Object[]> layThuCung(Object... cacThamSo);

    List<Object[]> layCacHoaDon(Object... cacThamSo);

    List<Object[]> layLichSuSpa(Object... cacThamSo);

    List<Object[]> layLichSuLuuTru(Object... cacThamSo);

    List<Object[]> layLichSuDiem(Object... cacThamSo);

    List<Object[]> layVoucher(Object... cacThamSo);
}
