package com.petstation.dao;

import java.util.List;

public interface LichSuKhachHangDAO {
    List<Object[]> layThuCung(Object... cacThamSo);

    List<Object[]> layCacHoaDon(Object... cacThamSo);

    List<Object[]> layLichSuSpa(Object... cacThamSo);

    List<Object[]> layLichSuLuuTru(Object... cacThamSo);

    List<Object[]> layLichSuDiem(Object... cacThamSo);

    List<Object[]> layVoucher(Object... cacThamSo);
}
