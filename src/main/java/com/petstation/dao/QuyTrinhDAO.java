package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho QuyTrinh; service gọi qua hợp đồng này. */
public interface QuyTrinhDAO {
    List<Object[]> laySpa(long id);

    String layTrangThaiHotel(long id);

    List<Object[]> layLichHen(long id);

    List<Object[]> layPhong();

    java.util.List<Object[]> layPhongChiTiet();

    java.util.List<Object[]> layLichPhong();
}
